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
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;
import psidev.psi.mi.tab.model.builder.Row;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;
import uk.ac.ebi.intact.psimitab.PsimitabTools;
import uk.ac.ebi.intact.psimitab.model.ExtendedInteractor;
import uk.ac.ebi.intact.psimitab.converters.Intact2BinaryInteractionConverter;
import uk.ac.ebi.intact.psimitab.converters.expansion.SpokeWithoutBaitExpansion;
import uk.ac.ebi.intact.psimitab.converters.expansion.ExpansionStrategy;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.commons.util.ETACalculator;
import uk.ac.ebi.intact.irefindex.seguid.RigDataModel;
import uk.ac.ebi.intact.irefindex.seguid.RogidGenerator;
import uk.ac.ebi.intact.irefindex.seguid.SeguidException;
import uk.ac.ebi.intact.irefindex.seguid.RigidGenerator;

import javax.persistence.Query;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Creates a MITAB file where each line represents a single interaction. Data is retrieved from the database and
 * the resulting data file may feature multiple lines involving the same interactors (ie. non clustered interactions).
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DatabaseSimpleMitabExporter {

    private static final Log log = LogFactory.getLog( DatabaseSimpleMitabExporter.class );
    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String SMALLMOLECULE_MI_REF = "MI:0328";
    

    public DatabaseSimpleMitabExporter() {
    }

    public void exportAllInteractors(Writer mitabWriter) throws IOException, IntactTransactionException {
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final int interactionCount = daoFactory.getInteractorDao( InteractionImpl.class ).countAll();
        final int allinteractorCount = daoFactory.getInteractorDao().countAll();
        final int interactingMoleculeTotalCount = allinteractorCount - interactionCount;
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        final String allInteractorsHql = "from InteractorImpl where objclass <> '" + InteractionImpl.class.getName()+"'";
        exportInteractors(allInteractorsHql, interactingMoleculeTotalCount, mitabWriter);
    }

    public void exportInteractors(String interactorQueryHql,
                                  int interactorTotalCount,
                                  Writer mitabWriter) throws IOException, IntactTransactionException {

        if (interactorQueryHql == null) {
            throw new NullPointerException("Query for interactors is null: interactorQuery");
        }
        if (mitabWriter == null) {
            throw new NullPointerException("mitabWriter is null");
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Preparing to index " + interactorTotalCount + " interactor(s)." );
        }

        ETACalculator eta = null;
        if( interactorTotalCount > 0 ) {
            eta = new ETACalculator( interactorTotalCount );
        }

        // build the interaction clusters

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        final Set<String> interactionAcProcessed = new HashSet<String>();

        List<? extends Interactor> interactors = null;

        Intact2BinaryInteractionConverter converter =
                new Intact2BinaryInteractionConverter(new SpokeWithoutBaitExpansion(),
                                                      null); // no clustering at this stage.

        int firstResult = 0;
        int maxResults = 1;
        int interactorCount = 0;

        final IntactDocumentDefinition docDef = new IntactDocumentDefinition();

        do {
            dataContext.beginTransaction();
            interactors = findInteractors(interactorQueryHql, firstResult, maxResults);

            firstResult = firstResult + maxResults;

            for (Interactor interactor : interactors) {

                final Collection<Component> components = interactor.getActiveInstances();
                if( components.isEmpty() ) {
                    // not need to index
                    log.info( "Interactor " + interactor.getShortLabel() + " isn't involved in any interactions, skipping." );
                    continue;
                }

                if (log.isTraceEnabled()) log.trace("Processing interactor: "+interactor.getShortLabel());

                List<Interaction> interactions = new ArrayList<Interaction>();
                for (Component comp : components) {
                    final Interaction interaction = comp.getInteraction();

                    if (!interactionAcProcessed.contains(interaction.getAc())) {
                        interactions.add(interaction);
                        interactionAcProcessed.add(interaction.getAc());
                    }
                }

                if (log.isTraceEnabled()) log.trace("Starting conversion and property enrichment: "+interactor.getShortLabel());

                Collection<IntactBinaryInteraction> binaryInteractions = new ArrayList<IntactBinaryInteraction>( );

                for ( Interaction interaction : interactions ) {

                    final Collection<IntactBinaryInteraction> myInteractions = converter.convert( interaction );

                    for ( IntactBinaryInteraction binaryInteraction : myInteractions ) {

                        // ISSUE: we are handling n-ary interactions here, not the expanded binary !!!
                        //        Need to expand priot to converting to MITAB model !
                        Interactor[] pair = findInteractors( interaction, binaryInteraction );

                        // Update Interactors' ROGID - first, identify in which order they are stored in MITAB
                        RogidGenerator rogidGenerator = new RogidGenerator();
                        RigDataModel rigA = buildRigDataModel( pair[0] );
                        RigDataModel rigB = buildRigDataModel( pair[1] );
                        try {
                            final String rogA = rogidGenerator.calculateRogid( rigA.getSequence(), rigA.getTaxid() );
                            binaryInteraction.getInteractorA().getAlternativeIdentifiers().add(
                                    new CrossReferenceImpl( "irefindex", rogA, "rogid" ) );

                            final String rogB = rogidGenerator.calculateRogid( rigB.getSequence(), rigB.getTaxid() );
                            binaryInteraction.getInteractorB().getAlternativeIdentifiers().add(
                                    new CrossReferenceImpl( "irefindex", rogB, "rogid" ) );

                            // Update Interaction RIGID
                            RigidGenerator rigidGenerator = new RigidGenerator();
                            rigidGenerator.addSequence( rigA.getSequence(), rigA.getTaxid() );
                            rigidGenerator.addSequence( rigB.getSequence(), rigB.getTaxid() );
                            String rig = rigidGenerator.calculateRigid();
                            binaryInteraction.getInteractionAcs().add( new CrossReferenceImpl( "irefindex", rig, "rigid" ) );

                        } catch ( SeguidException e ) {
                            throw new RuntimeException( "An error occured while generating RIG/ROG identifier", e );
                        }

                        // Update Interaction RIGID
                        List<RigDataModel> sequences = new ArrayList<RigDataModel>();
                    }

                    binaryInteractions.addAll( myInteractions );
                }

                if ( log.isTraceEnabled() ) log.trace( "Storing " + binaryInteractions.size() + " interactions..." );

                int count = 0;
                for (IntactBinaryInteraction bi : binaryInteractions) {
                    count++;
                    if ( log.isTraceEnabled() ) {
                        log.trace( "Processing interaction #" + count );
                    }

                    // TODO is that still necessary ???
                    flipInteractorsIfNecessary(bi);

                    // write MITAB line
                    final Row row = docDef.createInteractionRowConverter().createRow( bi );

                    // here we could save some processing by converting first the line to a Row,
                    // and then converting tow to string and giving this row to the indexers

                    final String line = row.toString();
                    mitabWriter.write(line+ NEW_LINE);
                }

                interactorCount++;

                if( (count % 100) == 0 ) {
                    if ( log.isDebugEnabled() ) {
                        log.debug( "Processed " + interactorCount + " of " + interactorTotalCount + " interactor(s)" );
                    }
                    mitabWriter.flush();
                }
            }

            dataContext.commitTransaction();

        } while (!interactors.isEmpty());

        if ( log.isInfoEnabled() ) {
            log.info( "Completed export of Interactions as MITAB." );
        }
    }

    public void exportAllInteractions(Writer mitabWriter) throws IOException, IntactTransactionException {
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final int interactionCount = daoFactory.getInteractorDao( InteractionImpl.class ).countAll();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        final String allInteractorsHql = "from InteractorImpl where objclass = '" + InteractionImpl.class.getName()+"'";
        exportInteractions(allInteractorsHql, interactionCount, mitabWriter);
    }

    public void exportInteractions(String interactionHqlQuery,
                                   int interactionTotalCount,
                                   Writer mitabWriter) throws IOException, IntactTransactionException {

        if (interactionHqlQuery == null) {
            throw new NullPointerException("Query for interactions is null: interactionQuery");
        }
        if (mitabWriter == null) {
            throw new NullPointerException("mitabWriter is null");
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Preparing to index " + interactionTotalCount + " interaction(s)." );
        }

        ETACalculator eta = null;
        if( interactionTotalCount > 0 ) {
            eta = new ETACalculator( interactionTotalCount );
        }

        // build the interaction clusters

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        List<? extends Interaction> interactions = null;

        Intact2BinaryInteractionConverter converter =
                new Intact2BinaryInteractionConverter(null,  // no expansion here
                                                      null); // no clustering at this stage.

        int firstResult = 0;
        int maxResults = 1;
        int interactionCount = 0;

        final IntactDocumentDefinition docDef = new IntactDocumentDefinition();
        final ExpansionStrategy expansion = new SpokeWithoutBaitExpansion();

        do {
            dataContext.beginTransaction();
            interactions = findInteractions(interactionHqlQuery, firstResult, maxResults);

            firstResult = firstResult + maxResults;

            for (Interaction interaction : interactions) {

                if (log.isTraceEnabled()) log.trace("Processing interaction: "+interaction.getShortLabel());

                // expand our interaction into binary
                final Collection<Interaction> expandedInteractions = expansion.expand( interaction );
                if (log.isTraceEnabled()) log.trace( expansion.getName() + " generated "+ expandedInteractions.size() + " binary interactions");

                for ( Interaction expandedInteraction : expandedInteractions ) {

                    final Collection<IntactBinaryInteraction> mitabInteractions = converter.convert( expandedInteraction );
                    final IntactBinaryInteraction mitabInteraction = mitabInteractions.iterator().next();

                    flipInteractorsIfNecessary(mitabInteraction);

                    Interactor[] pair = findInteractors( expandedInteraction, mitabInteraction );

                    // Update Interactors' ROGID - first, identify in which order they are stored in MITAB
                    RogidGenerator rogidGenerator = new RogidGenerator();
                    RigDataModel rigA = buildRigDataModel( pair[0] );
                    RigDataModel rigB = buildRigDataModel( pair[1] );
                    try {
                        final String rogA = rogidGenerator.calculateRogid( rigA.getSequence(), rigA.getTaxid() );
                        mitabInteraction.getInteractorA().getAlternativeIdentifiers().add(
                                new CrossReferenceImpl( "irefindex", rogA, "rogid" ) );

                        final String rogB = rogidGenerator.calculateRogid( rigB.getSequence(), rigB.getTaxid() );
                        mitabInteraction.getInteractorB().getAlternativeIdentifiers().add(
                                new CrossReferenceImpl( "irefindex", rogB, "rogid" ) );

                        // Update Interaction RIGID
                        RigidGenerator rigidGenerator = new RigidGenerator();
                        rigidGenerator.addSequence( rigA.getSequence(), rigA.getTaxid() );
                        rigidGenerator.addSequence( rigB.getSequence(), rigB.getTaxid() );
                        String rig = rigidGenerator.calculateRigid();
                        mitabInteraction.getInteractionAcs().add( new CrossReferenceImpl( "irefindex", rig, "rigid" ) );

                    } catch ( SeguidException e ) {
                        throw new RuntimeException( "An error occured while generating RIG/ROG identifier", e );
                    }


                    // write MITAB line
                    final Row row = docDef.createInteractionRowConverter().createRow( mitabInteraction );

                    // here we could save some processing by converting first the line to a Row,
                    // and then converting tow to string and giving this row to the indexers

                    final String line = row.toString();
                    mitabWriter.write(line+ NEW_LINE);
                }

                interactionCount++;

                if( (interactionCount % 100) == 0 ) {
                    if ( log.isDebugEnabled() ) {
                        log.debug( "Processed " + interactionCount + " of " + interactionTotalCount + " interactor(s)" );
                    }
                    mitabWriter.flush();
                }
            }

            dataContext.commitTransaction();

        } while (!interactions.isEmpty());

        if ( log.isInfoEnabled() ) {
            log.info( "Completed export of Interactions as MITAB." );
        }
    }

    private RigDataModel buildRigDataModel( Interactor interactor ) {

        String taxid = null;
        if( interactor.getBioSource() != null ) {
            taxid =interactor.getBioSource().getTaxId();
        }

        String seq = null;
        if( interactor.getClass().isAssignableFrom( Polymer.class ) ) {
            Polymer polymer = (Polymer) interactor;
            seq = polymer.getSequence();
//        } else if( interactor instanceof SmallMolecule ) {
            // find INCHI key
            // AnnotatedObjectUtils.searchXrefs( interactor, CvDatabase.INCHI );
        } else {
            // use IntAct AC
            seq = interactor.getAc();
        }

        return new RigDataModel( seq, taxid );
    }

    private Interactor[] findInteractors( Interaction interaction, IntactBinaryInteraction binaryInteraction ) {

        Interactor[] pair = new Interactor[2];

        String interactorA = getIntactAc( binaryInteraction.getInteractorA() );
        String interactorB = getIntactAc( binaryInteraction.getInteractorB() );

        for ( Component component : interaction.getComponents() ) {
            if( component.getInteractor().getAc().equals( interactorA ) ) {
                pair[0] = component.getInteractor();
            } else if( component.getInteractor().getAc().equals( interactorB ) ){
                pair[1] = component.getInteractor();
            } else {
                throw new IllegalStateException( "Found Ac: '"+ component.getInteractor().getAc() +
                                                 "' when expeting '"+interactorA+"' or '"+interactorB+"'" );
            }
        }

        return pair;
    }

    private String getIntactAc( ExtendedInteractor interactor ) {
        for ( CrossReference reference : interactor.getIdentifiers() ) {
            if( reference.getDatabase().equalsIgnoreCase("intact" ) ) {
                return reference.getIdentifier();
            }
        }
        return null;
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

    private static List<? extends Interaction> findInteractions( String interactionHqlQuery, int firstResult, int maxResults ) {
        Query q = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getEntityManager().createQuery(interactionHqlQuery);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        return q.getResultList();
    }

    private static List<? extends Interactor> findInteractors(String hql, int firstResult, int maxResults) {
        Query q = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getEntityManager().createQuery(hql);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        return q.getResultList();
    }
}