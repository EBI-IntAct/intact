/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridge.UniprotBridgeException;
import uk.ac.ebi.intact.bridge.adapters.crossRefAdapter.ReflectionCrossReferenceBuilder;
import uk.ac.ebi.intact.bridge.adapters.crossRefAdapter.UniprotCrossReference;
import uk.ac.ebi.intact.bridge.model.Organism;
import uk.ac.ebi.intact.bridge.model.*;
import uk.ac.ebi.kraken.interfaces.uniprot.*;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.*;
import uk.ac.ebi.kraken.interfaces.uniprot.features.ChainFeature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.GeneNameSynonym;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.ORFName;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.OrderedLocusName;
import uk.ac.ebi.kraken.model.uniprot.util.SplicedSequenceCalculator;
import uk.ac.ebi.kraken.uuw.services.remoting.*;

import java.util.*;

/**
 * Adapter to read UniProt entries using the remote services.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Oct-2006</pre>
 */
public class UniprotRemoteServiceAdapter extends AbstractUniprotBridgeAdapter {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotRemoteServiceAdapter.class );

    public Collection<UniprotProtein> retreive( String ac ) throws UniprotBridgeException {

        Collection<UniprotProtein> results = new ArrayList<UniprotProtein>();

        Iterator<UniProtEntry> it = getUniProtEntry( ac );

        if ( !it.hasNext() ) {
            // we didn't find anything
            throw new UniprotBridgeException( "Could not find protein: " + ac, new ProteinNotFoundException( ac ) );
        }

        while ( it.hasNext() ) {
            UniProtEntry uniProtEntry = it.next();
            results.add( buildUniprotProtein( uniProtEntry ) );
        }
        return results;
    }

    public Map<String, Collection<UniprotProtein>> retreive( Collection<String> acs ) throws UniprotBridgeException {
        Map<String, Collection<UniprotProtein>> results = new HashMap<String, Collection<UniprotProtein>>();

        // TODO use bulk loading and post sort the Map
        for ( String ac : acs ) {
            results.put( ac, retreive( ac ) );
        }

        return results;
    }

    //////////////////////////
    // private methods

    private EntryRetrievalService getEntryRetrievalService() {
        UniProtRemoteServiceFactory factory = new UniProtRemoteServiceFactory();
        return factory.getEntryRetrievalService();
    }

    private Iterator<UniProtEntry> getUniProtEntry( String ac ) {
        Iterator<UniProtEntry> iterator = null;

        if ( IdentifierChecker.isSpliceVariantId( ac ) || IdentifierChecker.isFeatureChainId( ac ) ) {

            // we only use this search for splice variants and feature chains
            UniProtRemoteServiceFactory factory = new UniProtRemoteServiceFactory();
            Query query = UniProtQueryBuilder.buildFullTextSearch( ac );
            UniProtQueryService uniProtQueryService = factory.getUniProtQueryService();
            iterator = uniProtQueryService.getEntries( query );

        } else {

            iterator = getUniProtEntryForProteinEntry( ac );
        }

        return iterator;
    }

    private Iterator<UniProtEntry> getUniProtEntryForProteinEntry( String ac ) {
        UniProtRemoteServiceFactory factory = new UniProtRemoteServiceFactory();
        UniProtQueryService uniProtQueryService = factory.getUniProtQueryService();
        return uniProtQueryService.getEntries( UniProtQueryBuilder.buildQuery( ac ) );
    }

    private UniprotProtein buildUniprotProtein( UniProtEntry uniProtEntry ) {

        int organismCount = uniProtEntry.getOrganisms().size();

        if ( organismCount > 1 ) {
            throw new IllegalStateException( "Entry: " + uniProtEntry.getUniProtId() +
                                             ": expected to find a single organism. Instead found " + organismCount );
        }

        // Process OS, OC, OX
        List<uk.ac.ebi.kraken.interfaces.uniprot.Organism> organisms = uniProtEntry.getOrganisms();
        List<NcbiTaxonomyId> taxids = uniProtEntry.getNcbiTaxonomyIds();

        uk.ac.ebi.kraken.interfaces.uniprot.Organism organism = organisms.get( 0 );
        String organismName = organism.getScientificName().getValue();
        String entryTaxid = taxids.get( 0 ).getValue();
        Organism o = new Organism( Integer.parseInt( entryTaxid ), organismName );

        // extract parent's names
        if ( organism.hasCommonName() ) {
            o.getParents().add( organism.getCommonName().getValue() );
        }
        if ( organism.hasSynonym() ) {
            o.getParents().add( organism.getSynonym().getValue() );
        }

        // TODO check on protein description
        UniprotProtein uniprotProtein = new UniprotProtein( uniProtEntry.getUniProtId().getValue(),
                                                            uniProtEntry.getPrimaryUniProtAccession().getValue(),
                                                            o,
                                                            uniProtEntry.getDescription().getProteinNames().get( 0 ).getValue() );

        List<SecondaryUniProtAccession> secondaryAcs = uniProtEntry.getSecondaryUniProtAccessions();
        for ( SecondaryUniProtAccession secondaryAc : secondaryAcs ) {
            uniprotProtein.getSecondaryAcs().add( secondaryAc.getValue() );
        }

        // version of the entry
        uniprotProtein.setReleaseVersion( getSPTREntryReleaseVersion( uniProtEntry ) );
        uniprotProtein.setLastAnnotationUpdate( uniProtEntry.getEntryAudit().getLastAnnotationUpdateDate() );
        uniprotProtein.setLastSequenceUpdate( uniProtEntry.getEntryAudit().getLastSequenceUpdateDate() );

        // Process gene names, orfs, synonyms, locus...
        processGeneNames( uniProtEntry, uniprotProtein );

        // comments: function
        List<FunctionComment> functions = uniProtEntry.getComments( CommentType.FUNCTION );
        for ( FunctionComment function : functions ) {

            uniprotProtein.getFunctions().add( function.getValue() );
        }

        // Comments: disease
        List<DiseaseComment> diseases = uniProtEntry.getComments( CommentType.DISEASE );
        for ( DiseaseComment disease : diseases ) {
            uniprotProtein.getFunctions().add( disease.getValue() );
        }

        // keywords
        List<Keyword> keywords = uniProtEntry.getKeywords();
        for ( Keyword keyword : keywords ) {
            uniprotProtein.getKeywords().add( keyword.getValue() );
        }

        // Cross references
        processCrossReference( uniProtEntry, uniprotProtein );

        // sequence
        uniprotProtein.setSequence( uniProtEntry.getSequence().getValue() );
        uniprotProtein.setSequenceLength( uniProtEntry.getSequence().getLength() );
        uniprotProtein.setCrc64( uniProtEntry.getSequence().getCRC64() );
        // TODO molecular weight ?!

        // splice variants
        processSpliceVariants( uniProtEntry, uniprotProtein );

        // chains
        processFeatureChain( uniProtEntry, uniprotProtein );

        return uniprotProtein;
    }


    /**
     * Extract from the SPTREntry the annotation release and the entry type, then combine them to get a version we will
     * use in the Xref. uniprot, identity )
     *
     * @param sptrEntry the entry from which we extract the information.
     *
     * @return a version as a String.
     *
     * @throws uk.ac.ebi.interfaces.sptr.SPTRException
     *
     */
    private String getSPTREntryReleaseVersion( UniProtEntry sptrEntry ) {
        String version = null;
        String uniprotRelease = String.valueOf( sptrEntry.getEntryAudit().getEntryVersion() );

        if ( sptrEntry.getType().equals( UniProtEntryType.SWISSPROT ) ) {
            version = SWISS_PROT_PREFIX + uniprotRelease;
        } else if ( sptrEntry.getType().equals( UniProtEntryType.TREMBL ) ) {
            // will allow Version up to 999 ... then it will be truncated as Xref.dbRelease is VARCHAR2(10)
            version = TREMBL_PREFIX + uniprotRelease;
        } else {
            // though should not happen.
            log.warn( "Unexpected SPTREntry type: " + sptrEntry.getType().getValue() );
            version = uniprotRelease;
        }

        return version;
    }

    private void processFeatureChain( UniProtEntry uniProtEntry, UniprotProtein protein ) {
        Collection<ChainFeature> features = uniProtEntry.getFeatures( FeatureType.CHAIN );

        log.debug( "Processing " + features.size() + " feature chain..." );

        for ( ChainFeature featureChain : features ) {

            String id = featureChain.getFeatureId().getValue();

            FeatureLocation location = featureChain.getFeatureLocation();
            int begin = location.getStart();
            int end = location.getEnd();

            // TODO find more about startFuzzyness and endFuzzyness
            String chainSequence = protein.getSequence().substring( begin - 1, end );

            UniprotFeatureChain chain = new UniprotFeatureChain( id,
                                                                 protein.getOrganism(),
                                                                 chainSequence );
            chain.setStart( begin );
            chain.setEnd( end );

            // add the chain to the protein
            protein.getFeatureChains().add( chain );
        }
    }

    private void processGeneNames( UniProtEntry uniProtEntry, UniprotProtein protein ) {

        List<Gene> genes = uniProtEntry.getGenes();
        log.debug( "Processing " + genes.size() + " gene names..." );

        for ( Gene gene : genes ) {

            protein.getGenes().add( gene.getGeneName().getValue() );

            for ( GeneNameSynonym synonym : gene.getGeneNameSynonyms() ) {
                protein.getSynomyms().add( synonym.getValue() );
            }

            for ( ORFName orf : gene.getORFNames() ) {
                protein.getOrfs().add( orf.getValue() );
            }

            for ( OrderedLocusName locus : gene.getOrderedLocusNames() ) {
                protein.getLocuses().add( locus.getValue() );
            }
        }
    }

    private Collection<UniprotCrossReference> convert( Collection<DatabaseCrossReference> refs ) {
        Collection<UniprotCrossReference> convertedRefs = new ArrayList<UniprotCrossReference>();
        ReflectionCrossReferenceBuilder builder = new ReflectionCrossReferenceBuilder();
        for ( DatabaseCrossReference ref : refs ) {
            convertedRefs.add( builder.build( ref ) );
        }
        return convertedRefs;
    }

    private void processCrossReference( UniProtEntry uniProtEntry, UniprotProtein protein ) {
        log.debug( "Processing cross references..." );

        Collection<DatabaseCrossReference> databaseCrossReferences = uniProtEntry.getDatabaseCrossReferences();
        Collection<UniprotCrossReference> xrefs = convert( databaseCrossReferences );

        for ( UniprotCrossReference xref : xrefs ) {

            String ac = xref.getAccessionNumber();
            String db = xref.getDatabase();

            if ( getCrossReferenceSelector() != null && !getCrossReferenceSelector().isSelected( db ) ) {
                log.trace( getCrossReferenceSelector().getClass().getSimpleName() + " filtered out database: '" + db + "'." );
                continue;
            }

            String desc = xref.getDescription(); // TODO There is so far no straight forward way to process all cross refrence and extract descriptions. We could at least provide specific handlers in case we know we need to process specific databases.

            protein.getCrossReferences().add( new UniprotXref( ac, db, desc ) );

            // handles HUGE cross references (stored as gene name or synonym and start with KIAA)
            if ( getCrossReferenceSelector() != null &&
                 ( getCrossReferenceSelector().isSelected( "HUGE" ) || getCrossReferenceSelector().isSelected( "KIAA" ) ) ) {

                // we only do this if the filter requires it explicitely.
                List<Gene> genes = uniProtEntry.getGenes();
                for ( Gene gene : genes ) {

                    String geneName = gene.getGeneName().getValue();
                    if ( geneName.startsWith( "KIAA" ) ) {
                        protein.getCrossReferences().add( new UniprotXref( geneName, "HUGE" ) );
                    }

                    List<GeneNameSynonym> synonyms = gene.getGeneNameSynonyms();
                    for ( GeneNameSynonym synonym : synonyms ) {
                        String syn = synonym.getValue();
                        if ( syn.startsWith( "KIAA" ) ) {
                            protein.getCrossReferences().add( new UniprotXref( syn, "HUGE" ) );
                        }
                    }
                }
            }
        } // for Cross Ref
    }

    private void processSpliceVariants( UniProtEntry uniProtEntry, UniprotProtein protein ) {

        List<AlternativeProductsComment> comments = uniProtEntry.getComments( CommentType.ALTERNATIVE_PRODUCTS );
        for ( AlternativeProductsComment comment : comments ) {
            List<AlternativeProductsIsoform> isoforms = comment.getIsoforms();
            for ( AlternativeProductsIsoform isoform : isoforms ) {

                List<String> ids = new ArrayList<String>();

                List<IsoformId> isoIDs = isoform.getIds();
                for ( IsoformId isoID : isoIDs ) {
                    log.debug( "isoID.getValue() = " + isoID.getValue() );

                    // TODO remove this once the API is fixed, currently when multiple ids are present they are returned as a comma separated value :(
                    for ( int i = 0; i < isoID.getValue().split( "," ).length; i++ ) {
                        String id = isoID.getValue().split( "," )[ i ].trim();
                        log.debug( "Found ID " + i + ":" + id );
                        ids.add( id );
                    }
                }

                // process alternative sequence
                // check that the sequence is in the current entry
                String sequence = null;
                String status = isoform.getIsoformSequenceStatus().getValue();
                log.debug( "Sequence status: " + status );
                String parentProtein = getUniProtAccFromSpliceVariantId( ids.get( 0 ) );
                if ( getUniProtAccFromSpliceVariantId( ids.get( 0 ) ).equals( protein.getPrimaryAc() ) ) {

                    log.debug( "Calculating alternative sequence using current entry." );
                    sequence = SplicedSequenceCalculator.getSplicedSequenceForIsoId( uniProtEntry, isoIDs.get( 0 ).getValue() );
                    log.debug( "sequence = " + sequence );

                } else {

                    // then we need to load an external protein entry
                    log.warn( "The alternative sequence has to be calculated on the basis of an external entry: " + parentProtein );
                    log.debug( "Loading external parent protein: " + parentProtein );
                    Iterator<UniProtEntry> it = getUniProtEntry( parentProtein );

                    boolean multipleProteinFound = false;
                    UniProtEntry protEntry = null;
                    while ( it.hasNext() ) {
                        if ( protEntry == null ) {

                            protEntry = it.next();
                            log.debug( "Loaded " + protEntry.getUniProtId() );

                        } else {

                            // we were expecting to find only one protein - hopefully that should not happen !
                            log.error( "We were expecting to find only one protein while loading external sequence from: " + parentProtein );
                            log.error( "Found " + protEntry.getUniProtId() );
                            while ( it.hasNext() ) {
                                UniProtEntry p = it.next();
                                log.error( "Found " + p.getUniProtId() );
                            }

                            multipleProteinFound = true;
                        }
                    }

                    if ( !multipleProteinFound ) {
                        sequence = SplicedSequenceCalculator.getSplicedSequenceForIsoId( protEntry, isoIDs.get( 0 ).getValue() );
                        log.debug( "sequence = " + sequence );
                    }
                }

                // build splice variant
                UniprotSpliceVariant sv = new UniprotSpliceVariant( ids.get( 0 ),
                                                                    protein.getOrganism(),
                                                                    sequence );
                // add secondary ids (if any)
                for ( int i = 1; i < ids.size(); i++ ) {
                    String id = ids.get( i );
                    sv.getSecondaryAcs().add( id );
                }

                // process synonyms
                List<IsoformSynonym> syns = isoform.getSynonyms();
                for ( IsoformSynonym syn : syns ) {
                    sv.getSynomyms().add( syn.getValue() );
                }

                // process note
                sv.setNote( isoform.getNote().getValue() );

                // add the splice variant to the original protein
                protein.getSpliceVariants().add( sv );

            } // for isoform
        } // for comments
    }

    private String getUniProtAccFromSpliceVariantId( String svId ) {
        int index = svId.indexOf( "-" );
        if ( index == -1 ) {
            throw new IllegalArgumentException( "The given accession number if not of a splice variant: " + svId );
        }
        return svId.substring( 0, index );
    }
}