/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.psimitab.converters.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.hibernate.annotations.CascadeType;
import org.springframework.transaction.TransactionStatus;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.builder.Row;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexSearcher;
import uk.ac.ebi.intact.commons.util.ETACalculator;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;
import uk.ac.ebi.intact.psimitab.OntologyNameFinder;
import uk.ac.ebi.intact.psimitab.PsimitabTools;
import uk.ac.ebi.intact.psimitab.converters.Intact2BinaryInteractionConverter;
import uk.ac.ebi.intact.psimitab.converters.expansion.NotExpandableInteractionException;
import uk.ac.ebi.intact.psimitab.converters.expansion.SpokeWithoutBaitExpansion;
import uk.ac.ebi.intact.psimitab.model.ExtendedInteractor;
import uk.ac.ebi.intact.psimitab.processor.IntactClusterInteractorPairProcessor;
import uk.ac.ebi.intact.psimitab.search.IntactInteractorIndexWriter;
import uk.ac.ebi.intact.psimitab.search.IntactPsimiTabIndexWriter;

import javax.persistence.Query;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Creates a MITAB file, and the interaction and interactors indexes directly using database data.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Deprecated
public class DatabaseMitabExporter {

    private static final Log log = LogFactory.getLog( DatabaseMitabExporter.class );
    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String SMALLMOLECULE_MI_REF = "MI:0328";

    private final OntologyIndexSearcher ontologiesIndexSearcher;
    private final OntologyNameFinder nameFinder;
    private final String[] ontologiesToExpand;

    private IntactPsimiTabIndexWriter interactionIndexer;
    private IntactInteractorIndexWriter interactorIndexer;

    public DatabaseMitabExporter(OntologyIndexSearcher ontologiesIndexSearcher, String ... supportedOntologies) {
        this.ontologiesIndexSearcher = ontologiesIndexSearcher;
        this.nameFinder = new OntologyNameFinder(ontologiesIndexSearcher);
        this.ontologiesToExpand = supportedOntologies;

        for (String supportedOntology : supportedOntologies) {
            nameFinder.addOntologyName(supportedOntology);
        }
    }

    public void exportAllInteractors(Writer mitabWriter, Directory interactionDirectory, Directory interactorDirectory) throws IOException {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final int interactionCount = daoFactory.getInteractorDao( InteractionImpl.class ).countAll();
        final int allinteractorCount = daoFactory.getInteractorDao().countAll();
        final int interactingMoleculeTotalCount = allinteractorCount - interactionCount;

        final String allInteractorsHql = "from InteractorImpl where objclass <> '" + InteractionImpl.class.getName()+"'";
        exportInteractors(allInteractorsHql, interactingMoleculeTotalCount, mitabWriter, interactionDirectory, interactorDirectory);
    }

    public void exportInteractors(String interactorQueryHql, int interactorTotalCount,
                                  Writer mitabWriter,
                                  Directory interactionDirectory,
                                  Directory interactorDirectory) throws IOException {
        if (interactorQueryHql == null) {
            throw new NullPointerException("Query for interactors is null: interactorQuery");
        }
        if (mitabWriter == null) {
            throw new NullPointerException("mitabWriter is null");
        }
        if (interactionDirectory == null && interactorDirectory == null) {
            throw new IllegalArgumentException("At least one of the directories has to be non-null in order to index something!");
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Preparing to index " + interactorTotalCount + " interactor(s)." );
        }

        // build the interaction clusters

        IndexWriter interactionIndexWriter = null;
        IndexWriter interactorIndexWriter = null;

        if (interactionDirectory != null) {
            interactionIndexWriter = new IndexWriter(interactionDirectory, new StandardAnalyzer(), true);
            interactionIndexer = new IntactPsimiTabIndexWriter(ontologiesIndexSearcher, ontologiesToExpand);
        }

        if (interactorDirectory != null) {
            interactorIndexWriter = new IndexWriter(interactorDirectory, new StandardAnalyzer(), true);
            interactorIndexer = new IntactInteractorIndexWriter(ontologiesIndexSearcher, ontologiesToExpand);
        }

        final Set<String> interactionAcProcessed = new HashSet<String>();

        List<? extends Interactor> interactors = null;

        Intact2BinaryInteractionConverter converter =
                new Intact2BinaryInteractionConverter(new SpokeWithoutBaitExpansion(),
                                                      null); // no clustering at this stage.

        final BinaryInteractionClusterBuilder clusterBuilder = new BinaryInteractionClusterBuilder();
        
        int firstResult = 0;
        int maxResults = 1; // only one interactor at a time
        int interactionCount = 0;
        int interactorCount = 0;

        // Convert database interactions into enriched MITAB interactions

        ETACalculator eta = null;
        if( interactorTotalCount > 0 ) {
            eta = new ETACalculator( interactorTotalCount );
        }

        if ( log.isInfoEnabled() ) {
            log.info( "Loading " + interactorTotalCount + " interactors from the database..." );
        }

        do {
            final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
            final TransactionStatus txStatus = dataContext.beginTransaction();

            interactors = findInteractors(interactorQueryHql, firstResult, maxResults);

            firstResult = firstResult + maxResults;

            for (Interactor interactor : interactors) {

                final Collection<Component> components = interactor.getActiveInstances();
                if( components.isEmpty() ) {
                    // not need to index
                    if (log.isDebugEnabled()) log.debug( "Interactor " + interactor.getShortLabel() + " isn't involved in any interactions, skipping." );
                    continue;
                }

                if (log.isDebugEnabled()) log.debug("Processing interactor: "+interactor.getShortLabel());

                List<Interaction> interactions = new ArrayList<Interaction>();
                for (Component comp : components) {
                    final Interaction interaction = comp.getInteraction();

                    // filter out negative interactions
                    if( ! InteractionUtils.isNegative( interaction ) ) {
                        if (interaction != null) {
                            if (!interactionAcProcessed.contains(interaction.getAc())) {
                                interactions.add(interaction);
                                interactionAcProcessed.add(interaction.getAc());
                            }
                        } else {
                            log.error("Component without interaction: "+comp.getAc());
                        }
                    } else {
                        log.warn( "Interaction " + interaction.getAc() + " was filtered out as it is negative." );
                    }
                }


                if (!interactions.isEmpty()) {
                    if (log.isTraceEnabled()) log.trace("Starting conversion and property enrichment: "+interactor.getShortLabel());

                    Collection<IntactBinaryInteraction> binaryInteractions = converter.convert(interactions);

                    if( binaryInteractions != null ) {

                        enrich(binaryInteractions);

                        if ( log.isTraceEnabled() ) log.trace( "Storing " + binaryInteractions.size() + " interactions..." );

                        int count = 0;
                        for (IntactBinaryInteraction bi : binaryInteractions) {
                            count++;
                            if ( log.isTraceEnabled() ) {
                                log.trace( "Processing interaction #" + count );
                            }

                            clusterBuilder.addBinaryInteraction( bi );
                        }
                    }

                    interactorCount++;
                } else {
                    log.debug("No interactions to convert for: "+interactor.getShortLabel());
                }
            }

            if( (firstResult%500) == 0 ) {
                if ( log.isInfoEnabled() ) {
                    log.info( "Progress - loaded " + firstResult + " of " + interactorTotalCount +
                              " interactors from database - ETA: " + eta.printETA( firstResult ) );
                }
            }

            dataContext.commitTransaction( txStatus );

        } while (!interactors.isEmpty());


        // TODO close database
        try {
            IntactContext.getCurrentInstance().destroy();
        } catch ( Exception e ) {
            log.warn( "Failed to close the database access", e );
        }


        // Now that we have the clusters built, let's browse then and generate the index

        final IntactDocumentDefinition docDef = new IntactDocumentDefinition();
        final Iterator<ProteinPair> proteinPairIterator = clusterBuilder.iterate();
        final IntactClusterInteractorPairProcessor interactorPairProcessor = new IntactClusterInteractorPairProcessor();


        // Process protein pairs

        final int totalPairProcessed = clusterBuilder.countProteinPairs();
        eta = new ETACalculator( totalPairProcessed );
        int pairProcessed = 0;

        if ( log.isInfoEnabled() ) {
            log.info( "Starting to process "+ totalPairProcessed + " protein pairs to build clustered MITAB and Lucene index..." );
        }

        while ( proteinPairIterator.hasNext() ) {
            ProteinPair proteinPair = proteinPairIterator.next();
            final Collection<IntactBinaryInteraction> interactions = interactorPairProcessor.process( proteinPair.getInteractions() );
            if ( log.isTraceEnabled() ) {
                log.trace( "Indexing "+interactions.size()+" interaction for interactors: " + proteinPair.getKey() );
            }
            int count = interactions.size();
            for ( IntactBinaryInteraction bi : interactions ) {

                count++;
                if ( log.isTraceEnabled() ) log.trace( "Processing interaction #" + count );

                flipInteractorsIfNecessary(bi);

                final Row row = docDef.createInteractionRowConverter().createRow( bi );

                // here we could save some processing by converting first the line to a Row,
                // and then converting tow to string and giving this row to the indexers

                final String line = row.toString();
                mitabWriter.write(line + NEW_LINE);

                if (interactionIndexer != null) {
                    interactionIndexer.addBinaryInteractionToIndex(interactionIndexWriter, row );
                }

                if (interactorIndexer != null) {
                    interactorIndexer.addBinaryInteractionToIndex(interactorIndexWriter, bi );
                }

                interactionCount++;

            } // interactions of the pair


            if (pairProcessed % 500 == 0) {
                if (log.isDebugEnabled()) log.debug("Processed "+interactionCount+" interactions ("+interactorCount+" interactors)");
                mitabWriter.flush();

                if (interactionIndexWriter != null) {
                    log.info( "Starting to optimize interaction index" );
                    interactionIndexWriter.optimize();
                    interactionIndexWriter.flush();
                    log.info( "Completed" );
                }

                if (interactorIndexWriter != null) {
                    log.info( "Starting to optimize interactor index" );
                    interactorIndexWriter.optimize();
                    interactorIndexWriter.flush();
                    log.info( "Completed" );
                }
            }

            if ( log.isInfoEnabled() ) {
                if (pairProcessed % 1000 == 0) {
                    log.info( "Progress - processed " + pairProcessed + " of " + totalPairProcessed +
                              " protein pairs - ETA: " + eta.printETA( pairProcessed ) );
                }
            }

            pairProcessed++;

        } // protein pairs

        if ( log.isInfoEnabled() ) {
            log.info( "Completed to process protein pairs." );
        }

        mitabWriter.flush();

        if (interactionIndexWriter != null) {
            if (log.isDebugEnabled()) log.debug("Optimizing interaction index");
            interactionIndexWriter.optimize();
            interactionIndexWriter.flush();
            interactionIndexWriter.close();
            if (log.isInfoEnabled()) log.info("Indexed "+interactionCount+" interactions");
        }

        if (interactorIndexWriter != null) {
            if (log.isDebugEnabled()) log.debug("Optimizing interactor index");
            interactorIndexWriter.optimize();
            interactorIndexWriter.flush();
            interactorIndexWriter.close();
            if (log.isInfoEnabled()) log.info("Indexed "+interactorCount+" interactors");
        }
    }

    /**
     * Flips the interactors if necessary, so the small molecule is always interactor A
     * @param bi
     */
    private void flipInteractorsIfNecessary(IntactBinaryInteraction bi) {
        PsimitabTools.reorderInteractors(bi, new Comparator<ExtendedInteractor>() {

            public int compare(ExtendedInteractor o1, ExtendedInteractor o2) {
                final CrossReference type1 = o1.getInteractorType();
                final CrossReference type2 = o2.getInteractorType();

                if (type1 != null && SMALLMOLECULE_MI_REF.equals(type1.getIdentifier())) {
                    return 1;
                } else if (type2 != null && SMALLMOLECULE_MI_REF.equals(type2.getIdentifier())) {
                    return -1;
                }
                return 0;
            }
        });
    }

    private void enrich(Collection<IntactBinaryInteraction> binaryInteractions) {
        for (IntactBinaryInteraction bi : binaryInteractions) {
            enrichCrossReferences(bi.getInteractorA().getProperties());
            enrichCrossReferences(bi.getInteractorB().getProperties());
        }
    }

    private void enrichCrossReferences(Collection<CrossReference> properties) {
        for (CrossReference xref : properties) {
            if (nameFinder.isOntologySupported(xref.getDatabase())) {
                try {
                    String name = nameFinder.getNameByIdentifier(xref.getIdentifier());
                    xref.setText(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static List<? extends Interactor> findInteractors(String hql, int firstResult, int maxResults) {
        Query q = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getEntityManager().createQuery(hql);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        return q.getResultList();
    }

    public IntactPsimiTabIndexWriter getIntactPsimiTabIndexWriter() {
        return this.interactionIndexer;
    }

    public IntactInteractorIndexWriter getIntactInteractorIndexWriter() {
        return this.interactorIndexer;
    }
}