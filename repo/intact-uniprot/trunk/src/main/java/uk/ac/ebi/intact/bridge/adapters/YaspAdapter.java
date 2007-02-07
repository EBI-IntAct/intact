/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.aristotle.model.sptr.AristotleSPTRException;
import uk.ac.ebi.aristotle.model.sptr.comment.Function;
import uk.ac.ebi.aristotle.model.sptr.feature.PolypeptideChainFeature;
import uk.ac.ebi.aristotle.util.interfaces.AlternativeSplicingAdapter;
import uk.ac.ebi.intact.bridge.UniprotBridgeException;
import uk.ac.ebi.intact.bridge.model.*;
import uk.ac.ebi.interfaces.Factory;
import uk.ac.ebi.interfaces.feature.Feature;
import uk.ac.ebi.interfaces.feature.FeatureException;
import uk.ac.ebi.interfaces.feature.FeatureLocation;
import uk.ac.ebi.interfaces.sptr.*;
import uk.ac.ebi.sptr.flatfile.yasp.EntryIterator;
import uk.ac.ebi.sptr.flatfile.yasp.YASP;
import uk.ac.ebi.sptr.flatfile.yasp.YASPException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.util.*;

/**
 * Adapter to read UniProt entries using YASP/Aristotle.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public class YaspAdapter extends AbstractUniprotBridgeAdapter {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( YaspAdapter.class );

    /**
     * Expected content of the beginning of the first line of a UniProt flat file.
     */
    public static final String FIRST_LINE_FIRST_FIVE_CHARS = "ID   ";

    public static final int CHAR_TO_READ = FIRST_LINE_FIRST_FIVE_CHARS.length();

    ///////////////////////////////////
    // AbstractUniprotBridgeAdapter

    public Collection<UniprotProtein> retreive( String ac ) {

        if ( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null protein AC." );
        }

        String entryUrl = buildUniprotSearchUrl( ac );

        InputStream is = null;
        Collection<UniprotProtein> proteins = new ArrayList<UniprotProtein>();

        // 1. Check on format of the entry
        try {
            is = checkUrlDataFormat( new URL( entryUrl ) );
        } catch ( IOException e ) {
            addError( ac, new UniprotBridgeReport( "Error upon reading URL: " + entryUrl, e ) );
            return proteins;
        } catch ( UniprotBridgeException e ) {
            addError( ac, new UniprotBridgeReport( "UniProt entry has invalid format: " + entryUrl, e ) );
            return proteins;
        }

        // 2. Process entry
        try {
            Collection<UniprotProtein> p = retreive( is, ac );
            if ( p != null ) {
                proteins.addAll( p );
                log.debug( "[AC: " + ac + "] Retreived " + p.size() + " protein(s)." );
            } else {
                log.error( "" );
            }
        } catch ( UniprotBridgeException e ) {
            addError( ac, new UniprotBridgeReport( "Error while processing UniProt entry: " + ac, e ) );
            return proteins;
        }

        // 3. Cleanup I/O
        try {
            is.close();
        } catch ( IOException e ) {
            addError( ac, new UniprotBridgeReport( "Error while closing URL: " + entryUrl, e ) );
        }

        return proteins;
    }

    public Map<String, Collection<UniprotProtein>> retreive( Collection<String> acs ) {

        if ( acs == null ) {
            throw new IllegalArgumentException( "You must give a non null List of UniProt ACs." );
        }

        if ( acs.isEmpty() ) {
            throw new IllegalArgumentException( "You must give a non empty List of UniProt ACs." );
        }

        Map<String, Collection<UniprotProtein>> results = new HashMap<String, Collection<UniprotProtein>>( acs.size() );
        String ac = null;

        for ( Iterator<String> iterator = acs.iterator(); iterator.hasNext(); ) {
            ac = iterator.next();
            Collection<UniprotProtein> proteins = retreive( ac );
            if ( proteins != null ) {
                results.put( ac, proteins );
            } else {
                addError( ac, new UniprotBridgeReport( "Could not retreive any proteins for UniProt AC: " + ac ) );
            }
        }

        return results;
    }

    ////////////////////////////////
    // Private methods

    /**
     * Open the given URL and check that the data pointed to start with the expected prefix.
     * <p/>
     * The check is on the 5 first char contained in the InputStream, they have to match 'ID   '.
     *
     * @param url the URL pointing to the UniProt protein entry (can be one or more).
     *
     * @return the input stream one can read the entry (1..n) from.
     *
     * @throws UniprotBridgeException if the format of the entry is not as expected.
     * @throws IOException
     */
    private InputStream checkUrlDataFormat( URL url ) throws UniprotBridgeException, IOException {

        if ( url == null ) {
            throw new IllegalArgumentException( "URL must not be null." );
        }

        PushbackInputStream pis = new PushbackInputStream( url.openStream(), 5 );
        byte[] b = new byte[CHAR_TO_READ];

        pis.read( b, 0, CHAR_TO_READ );
        if ( b.length < CHAR_TO_READ ) {
            throw new RuntimeException( "Could not read the whole 5 bytes" );
        }

        String fiveFirstChars = new String( b );
        if ( !FIRST_LINE_FIRST_FIVE_CHARS.equals( fiveFirstChars ) ) {
            throw new UniprotBridgeException( "Invalid UniProt entry format. An entry is expected to start with :'" +
                                              FIRST_LINE_FIRST_FIVE_CHARS + "' and not '" + fiveFirstChars + "'." );
        }

        // check is fine, rewind the stream
        pis.unread( b );

        return pis;
    }

    /**
     * Build a URL allowing to access the UniProt flat file given a specific UniProt AC.
     *
     * @param ac (non null) UniProt AC.
     *
     * @return a URL as a String.
     */
    private String buildUniprotSearchUrl( String ac ) {

        if ( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null AC." );

        }

        //        CvObjectDao cvDao = IntactContext.getCurrentInstance();
//        IntactContext.getCurrentInstance();
//        .getDataContext().getDaoFactory().getCvObjectDao( CvDatabase.class );
//        List<CvDatabase> dbs = cvDao.getByXrefLike( CvDatabase.UNIPROT_MI_REF );
//        if ( dbs.size() != 1 ) {
//            throw new IllegalStateException();
//        }

//        CvDatabase db = new CvDatabase( new Institution( "foo" ), "bla" );
//        cvDao.persist( db );
//        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

//        CvDatabase uniprot = dbs.iterator().next();

//        // fetch the URL
//        String url = null;
//        for ( Annotation annotation : uniprot.getAnnotations() ) {
//            if ( "search-url-ascii".equals( annotation.getCvTopic().getShortLabel() ) ) {
//                url = annotation.getAnnotationText();
//            }
//        }
//
//        if( url == null ) {
//            throw new IllegalStateException( );
//        }
//
//        // replace the given AC in the URL
//        // http://www.ebi.uniprot.org/entry/${ac}?format=text&ascii
//        String entryUrl = url.replaceAll( "${ac}", ac );
//
//        System.out.println( "entryUrl = " + entryUrl );

        // http://www.ebi.uniprot.org/entry/P21181?format=text&ascii
        String url = "http://www.ebi.uniprot.org/entry/" + ac + "?format=text&ascii";
//        String url = "http://www.expasy.org/uniprot/" + ac + ".txt";

        log.debug( "Built URL: " + url );

        return url;
    }

    /**
     * Build UniprotProteins based on a given InputStream.
     *
     * @param is InputStream on the data.
     * @param ac protein ac
     *
     * @return a non null collection of UniprotProteins.
     *
     * @throws UniprotBridgeException
     */
    private Collection<UniprotProtein> retreive( InputStream is, String ac ) throws UniprotBridgeException {

        if ( is == null ) {
            throw new IllegalArgumentException( "You must give a non null InputStream." );
        }

        if ( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null AC." );
        }

        Collection<UniprotProtein> uniprotProteins = new ArrayList<UniprotProtein>( 4 );
        UniprotProtein uniprotProtein = null;

        try {
            // parse it with YASP

            EntryIterator entryIterator = YASP.parseAll( is );

            /**
             * C A U T I O N
             * -------------
             *  The YASP Iterator has to be used carrefully. It doesn't behave like an java.util.Iterator.
             * .next() method gives you the current element
             * .hasNext() loads the next elements and says you if there was one.
             * So, calling twice .hasNext() without processing in between would
             * make you miss an element.
             */
            int entryCount = 0;
            while ( entryIterator.hasNext() ) {

                entryCount++;

                // Check if there is any exception remaining in the Entry before to use it
                if ( entryIterator.hadException() ) {

                    YASPException originalException = entryIterator.getException();
                    throw originalException;

//                    addError( ac, new UniprotBridgeReport( "Error occured while parsing entry (AC: " + ac + ")", originalException ) );
//
//                    if ( originalException != null ) {
//
//                        if ( log != null ) {
//                            log.error( "Parsing error while processing the entry " + entryCount, entryIterator.getException() );
//                            log.error( "Original exception was:", entryIterator.getException().getOriginalException() );
//                        }
//                    }
//
//                    continue;
                }

                // get the SPTREntry
                SPTREntry sptrEntry = ( SPTREntry ) entryIterator.next();

                if ( sptrEntry == null ) {
                    if ( log != null ) {
                        log.error( "\n\nSPTR entry is NULL ... skip it" );
                    }

                    continue;
                }

                log.info( "Processing " + sptrEntry.getID() + " ..." );

                uniprotProtein = buildUniprotProtein( sptrEntry );
                uniprotProteins.add( uniprotProtein );
            }

        } catch ( YASPException e ) {
            throw new UniprotBridgeException( "A YASP error occured while processing", e );
        } catch ( SPTRException e ) {
            throw new UniprotBridgeException( "An SPTR error occured while processing", e );
        } catch ( IOException e ) {
            throw new UniprotBridgeException( "An IO error occured while processing", e );
        } catch ( FeatureException e ) {
            throw new UniprotBridgeException( "An error occured while processing the features of an entry", e );
        }

        return uniprotProteins;
    }

    /**
     * Builds a UniprotProtein from a SPTREntry.
     *
     * @param sptrEntry
     *
     * @return
     *
     * @throws SPTRException
     * @throws FeatureException
     */
    private UniprotProtein buildUniprotProtein( SPTREntry sptrEntry ) throws SPTRException, FeatureException {

        int organismCount = sptrEntry.getOrganismNames().length;

        if ( organismCount > 1 ) {
            throw new IllegalStateException( "Entry: " + sptrEntry.getID() +
                                             ": expected to find a single organism. Instead found " + organismCount );
        }

        // Process OS, OC, OX
        String organismName = sptrEntry.getOrganismNames()[0];
        String entryTaxid = sptrEntry.getNCBITaxonomyID( organismName );
        Organism o = new Organism( Integer.parseInt( entryTaxid ), organismName );

        // extract parent's names
        String[] taxons = sptrEntry.getTaxonomy( organismName );
        for ( int i = 0; i < taxons.length; i++ ) {
            String taxon = taxons[i];
            o.getParents().add( taxon );
        }

        UniprotProtein uniprotProtein = new UniprotProtein( sptrEntry.getID(),
                                                            sptrEntry.getAccessionNumbers()[0],
                                                            o,
                                                            sptrEntry.getProteinName() );

        String proteinAC[] = sptrEntry.getAccessionNumbers();
        if ( proteinAC.length > 1 ) {
            for ( int i = 1; i < proteinAC.length; i++ ) {
                String ac = proteinAC[i];
                uniprotProtein.getSecondaryAcs().add( ac );
            }
        }

        // version of the entry
        uniprotProtein.setReleaseVersion( getSPTREntryReleaseVersion( sptrEntry ) );
        uniprotProtein.setLastAnnotationUpdate( new Date( sptrEntry.getLastAnnotationUpdateDate().getTime() ) );
        uniprotProtein.setLastSequenceUpdate( new Date( sptrEntry.getLastSequenceUpdateDate().getTime() ) );

        // Process gene names, orfs, synonyms, locus...
        processGeneNames( sptrEntry, uniprotProtein );

        // comments: function
        SPTRComment[] comments = sptrEntry.getAllComments();
        for ( int i = 0; i < comments.length; i++ ) {
            SPTRComment comment = comments[i];
            log.debug( "comment.getClass().getSimpleName() = " + comment.getClass().getSimpleName() );
            if ( comment instanceof Function ) {
                log.debug( "Found a Comment( Function )." );
                Function function = ( Function ) comment;
                uniprotProtein.getFunctions().add( function.getDescription() );
            }
        }

        // Comments: disease
        SPTRComment[] diseases = sptrEntry.getComments( "DISEASE" );
        for ( int i = 0; i < diseases.length; i++ ) {
            String disease = comments[i].getPropertyValue( 0 );
            uniprotProtein.getDiseases().add( disease );
        }

        // keywords
        String[] keywords = sptrEntry.getKeywords();
        for ( int i = 0; i < keywords.length; i++ ) {
            String keyword = keywords[i];
            uniprotProtein.getKeywords().add( keyword );
        }

        // Cross references
        processCrossReference( sptrEntry, uniprotProtein );

        // sequence
        uniprotProtein.setSequence( sptrEntry.getSequence() );
        uniprotProtein.setSequenceLength( sptrEntry.getSQSequenceLength() );
        uniprotProtein.setCrc64( sptrEntry.getCRC64() );

        // splice variants
        processSpliceVariants( sptrEntry, uniprotProtein );

        // chains
        processFeatureChain( sptrEntry, uniprotProtein );

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
     * @throws SPTRException
     */
    private String getSPTREntryReleaseVersion( SPTREntry sptrEntry ) throws SPTRException {
        String version = null;
        String uniprotRelease = sptrEntry.getLastAnnotationUpdateRelease();

        if ( sptrEntry.getEntryType() == SPTREntry.SWISSPROT ) {
            version = SWISS_PROT_PREFIX + uniprotRelease;
        } else if ( sptrEntry.getEntryType() == SPTREntry.TREMBL ) {
            // will allow Version up to 999 ... then it will be truncated as Xref.dbRelease is VARCHAR2(10)
            version = TREMBL_PREFIX + uniprotRelease;
        } else {
            // though should not happen.
            log.warn( "Unexpected SPTREntry type: " + sptrEntry.getEntryType() );
            version = uniprotRelease;
        }

        return version;
    }

    private void processFeatureChain( SPTREntry sptrEntry, UniprotProtein protein ) throws SPTRException {
        Feature[] features = sptrEntry.getFeatures();
        for ( Feature feature : features ) {

            if ( feature instanceof PolypeptideChainFeature ) {
                log.debug( "Found a PolypeptideChainFeature" );
                PolypeptideChainFeature pcf = ( PolypeptideChainFeature ) feature;

                String id = pcf.getID();

                String chainSequence = null;
                String[] sequences = pcf.getSequenceVariations();

                FeatureLocation location = pcf.getLocation();
                Integer begin = null;
                Integer end = null;
                try {
                    begin = location.getLocationBegin();
                    end = location.getLocationEnd();
                } catch ( FeatureException e ) {
                    // ignored
                }

                if ( sequences.length > 0 ) {
                    for ( int i = 0; i < sequences.length; i++ ) {
                        log.debug( "sequence[i] = " + sequences[i] );
                    }

                    if ( sequences.length > 1 ) {
                        log.debug( "Pick the first sequence out of " + sequences.length + ";" );
                        chainSequence = sequences[0];
                    }
                } else {
                    // TODO find more about startFuzzyness and endFuzzyness 
                    log.warn( "Yasp doesn't seem to provide alternative sequence for that feature. We go DIY-style..." );
                    if ( begin != null && end != null ) {
                        chainSequence = protein.getSequence().substring( begin - 1, end );
                    } else {
                        if ( log.isWarnEnabled() ) {
                            if ( begin == null ) {
                                log.warn( "Begin of feature range was missing." );
                            }
                            if ( end == null ) {
                                log.warn( "End of feature range was missing." );
                            }
                            log.warn( "Could not build the chain sequence => Skipping feature chain " + id );
                        }
                        continue;
                    }
                }

                UniprotFeatureChain chain = new UniprotFeatureChain( id,
                                                                     protein.getOrganism(),
                                                                     chainSequence );
                chain.setStart( begin );
                chain.setEnd( end );

                // add the chain to the protein
                protein.getFeatureChains().add( chain );
            }
        }
    }

    private void processGeneNames( SPTREntry sptrEntry, UniprotProtein protein ) throws SPTRException {

        log.debug( "Processing gene names..." );
        Gene[] genes = sptrEntry.getGenes();
        for ( Gene gene : genes ) {

            String geneName = gene.getName();
            if ( geneName != null && ( false == "".equals( geneName.trim() ) ) ) {
                protein.getGenes().add( geneName );
            }

            // create synonyms
            String[] synonyms = gene.getSynonyms();
            if ( synonyms.length > 0 ) {
                for ( String syn : synonyms ) {
                    protein.getSynomyms().add( syn );
                }
            }

            // create locus
            String[] locus = gene.getLocusNames();
            if ( locus.length > 0 ) {
                for ( String l : locus ) {
                    protein.getLocuses().add( l );
                }
            }

            // create orfs
            String[] orfs = gene.getORFNames();
            if ( orfs.length > 0 ) {
                for ( String orf : orfs ) {
                    protein.getOrfs().add( orf );
                }
            }
        }
    }

    private void processCrossReference( SPTREntry sptrEntry, UniprotProtein protein ) throws SPTRException {
        log.debug( "Processing cross references..." );
        SPTRCrossReference cr[] = sptrEntry.getCrossReferences();

        for ( SPTRCrossReference sptrXref : cr ) {
            String ac = sptrXref.getAccessionNumber();
            String db = sptrXref.getDatabaseName();

            if ( getCrossReferenceSelector() != null && !getCrossReferenceSelector().isSelected( db ) ) {
                log.debug( getCrossReferenceSelector().getClass().getSimpleName() + " filtered out database: '" + db + "'." );
                continue;
            }

            String desc = null;

            try {
                desc = sptrXref.getPropertyValue( SPTRCrossReference.SECONDARY_PROPERTY );
            }
            catch ( AristotleSPTRException e ) {
                // there was no description, we don't fail for that.
            }

            protein.getCrossReferences().add( new UniprotXref( ac, db, desc ) );
        }

        // handles HUGE cross references (stored as gene name or synonym and start with KIAA)
        if ( getCrossReferenceSelector() != null &&
             ( getCrossReferenceSelector().isSelected( "HUGE" ) || getCrossReferenceSelector().isSelected( "KIAA" ) ) ) {

            // we only do this if the filter requires it explicitely.
            Gene[] genes = sptrEntry.getGenes();
            for ( Gene gene : genes ) {

                String geneName = gene.getName();
                if ( geneName.startsWith( "KIAA" ) ) {
                    protein.getCrossReferences().add( new UniprotXref( geneName, "HUGE" ) );
                }

                String[] synonyms = gene.getSynonyms();
                for ( String syn : synonyms ) {
                    if ( syn.startsWith( "KIAA" ) ) {
                        protein.getCrossReferences().add( new UniprotXref( syn, "HUGE" ) );
                    }
                }
            }
        }
    }

    private void processSpliceVariants( SPTREntry sptrEntry, UniprotProtein protein ) throws SPTRException,
                                                                                             FeatureException {
        log.debug( "Processing splice variants..." );
        // retrieve the comments of that entry
        SPTRComment[] comments = sptrEntry.getComments( Factory.COMMENT_ALTERNATIVE_SPLICING );

        for ( int j = 0; j < comments.length; j++ ) {
            SPTRComment comment = comments[j];

            if ( !( comment instanceof AlternativeSplicingAdapter ) ) {
                log.error( "Looking for Comment type: " + AlternativeSplicingAdapter.class.getName() );
                log.error( "Could not handle comment type: " + comment.getClass().getName() );
                log.error( "SKIP IT." );
                continue; // skip it, go to next iteration.
            }

            AlternativeSplicingAdapter asa = ( AlternativeSplicingAdapter ) comment;
            Isoform[] isoForms = asa.getIsoforms();

            // for each comment, browse its isoforms ...
            for ( int ii = 0; ii < isoForms.length; ii++ ) {
                Isoform isoForm = isoForms[ii];

                /**
                 * browse isoform's IDs which, in case they have been store in the database,
                 * are used as shortlabel of the related Protein.
                 */
                String[] ids = isoForm.getIDs();

                if ( ids.length > 0 ) {

                    // only the first ID should be taken into account, the following ones are secondary IDs.
                    String spliceVariantID = ids[0];
                    log.debug( "Splice variant ID: " + spliceVariantID );
                    String sequence = sptrEntry.getAlternativeSequence( isoForm );
                    UniprotSpliceVariant sv = new UniprotSpliceVariant( ids[0],
                                                                        protein.getOrganism(),
                                                                        sequence );
                    protein.getSpliceVariants().add( sv );

                    if ( ids.length > 1 ) {
                        for ( int i = 1; i < ids.length; i++ ) {
                            String id = ids[i];
                            sv.getSecondaryAcs().add( id );
                        }
                    }

                    // set the note if any
                    if ( isoForm.getNote() != null ) {
                        sv.setNote( isoForm.getNote() );
                    }

                    // synonyms
                    for ( int i = 0; i < isoForm.getSynonyms().length; i++ ) {
                        sv.getSynomyms().add( isoForm.getSynonyms()[i] );
                    }
                } // for ids
            } // for isoforms
        } // for comments
    }
}