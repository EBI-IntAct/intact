/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import gnu.regexp.RE;
import gnu.regexp.REMatch;
import uk.ac.ebi.aristotle.util.interfaces.AlternativeSplicingAdapter;
import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.interfaces.Factory;
import uk.ac.ebi.interfaces.feature.FeatureException;
import uk.ac.ebi.interfaces.sptr.*;
import uk.ac.ebi.sptr.flatfile.yasp.EntryIterator;
import uk.ac.ebi.sptr.flatfile.yasp.YASP;
import uk.ac.ebi.sptr.flatfile.yasp.YASPException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Parse an URL and update the IntAct database.
 * <p/>
 * Here is the detail implemented algorithm <br>
 * <pre>
 * <p/>
 * (1) From the URL given by the user, get an <i>EntryIterator</i> to process them one by one.
 * <p/>
 * (2) for each <i>SPTREntry</i>
 * <p/>
 *    (2.1)
 *          a)
 *          From the Accession number, retreive from IntAct all <i>Protein</i> with that AC as
 *          a SPTR <i>Xref</i>. We can find several instance of Protein in case they are link
 *          to different <i>BioSource</i>. Lets call that set of Protein: PROTEINS.
 *          Note: an SPTREntry can contains several AC so we check in IntAct for all of them.
 * <p/>
 *          b)
 *          From PROTEINS, we retreive from IntAct all <i>Splice Variant</i> (ie. <code>Protein</code>)
 *          with the AC of a retreived proteins (in PROTEINS) and having a CvXrefQualifier equals to
 *          <i>isoform-parent</i>. We can find several instance of Splice Variant per master Protein
 *          in case in case we have multiple <i>BioSource</i>. Lets call that set of Protein:
 *          SPLICE-VARIANTS.
 * <p/>
 *    (2.2) The user can give a taxid 'filter' (lets call it t) in order to retrieve only
 *          protein related to that taxid (beware that behind the scene, all protein are
 *          update/create). In an SPTREntry, there is 1..n specified organism (i.e. taxid).
 *          So if the taxid parameter t is null, we give back to the user all proteins created
 *          or updated, if a valid taxid is given by the user, we filter the set of proteins.
 *          If it is not found, the procedure fails.
 * <p/>
 *    (2.3) For each taxid of the SPTREntry (lets call it TAXID)
 * <p/>
 *       (2.3.1) Get up-to-date information about the organism from Newt.
 *               If that organism is already existing inIntAct as a BioSource, we check if an
 *               update is needed. We take also into account that a taxid can be obsolete and
 *               in such a case we update IntAct data accordingly.
 * <p/>
 *       (2.3.2)
 *                 a) If a Protein from PROTEINS (cf. 2.1) has TAXID as BioSource,
 *                    we update its data from the SPTREntry.
 * <p/>
 *                 b) If no Protein from PROTEINS has TAXID as BioSource, we create a new Protein.
 * <p/>
 *                 c) If a Protein from PROTEINS has a taxid not found in the SPTREntry, we display
 *                    a warning message.
 * <p/>
 *                 d) If a Protein from SPLICE-VARIANTS (cf. 2.1) has TAXID as BioSource,
 *                    we update its data from the SPTREntry.
 * <p/>
 *                 e) If no Protein from SPLICE-VARIANTS has TAXID as BioSource, we create a new Protein.
 * <p/>
 *                 f) If a Protein from SPLICE-VARIANTS has a taxid not found in the SPTREntry,
 *                    we display a warning message.
 * <p/>
 * <p/>
 * Cross references created on step 2.3.2:
 * <p/>
 * For Proteins :
 * <p/>
 *     (1) a link to uniprot
 * <p/>
 *          Xref( CvDatabase(uniprot)
 *                primaryId(uniprotAc-spliceVarNumber)
 *                secondaryId(uniprotId)
 *                CvXrefQualifier(identity)
 *              );
 * <p/>
 *     (2) Link to GO, SGD, INTERPRO, FLYBASE.
 *         Those Xrefs comply to the following schema:
 *         TODO: when updating Xrefs, remove those that no longer exists
 * <p/>
 *          Xref( CvDatabase(DB),
 *                primaryId(AC),
 *                secondaryId(ID),
 *                CvXrefQualifier(-)
 *              );
 * <p/>
 * <p/>
 * For Splice Variants:
 * <p/>
 *     (1) a link to the master protein
 * <p/>
 *           Xref( CvDatabase(intact)
 *                 primaryId(intactAc)
 *                 secondaryId(intactShortlabel)
 *                 CvXrefQualifier(isoform-parent)
 *               );
 * <p/>
 *     (2) a link to uniprot
 * <p/>
 *            Xref( CvDatabase(uniprot)
 *                  primaryId(uniprotAc-spliceVarNumber)
 *                  secondaryId(uniprotId)
 *                  CvXrefQualifier(identity)
 *                );
 * <p/>
 * BEWARE that no checks have been done about the ownership of updated objects.
 * <p/>
 * </pre>
 * </p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class UpdateProteins extends UpdateProteinsI {

    // Store that error in the temp directory (OS independant)
    private static final String ENTRY_OUTPUT_FILE = System.getProperty("java.io.tmpdir") + "Entries.error";

    // to record entry error
    private String filename = null;
    private FileOutputStream file = null;
    private BufferedOutputStream buffer = null;

    // flag for output on STDOUT
    private boolean debugOnScreen = false;

    // iterator on all parsed Entries.
    private EntryIterator entryIterator = null;

    // count of all potential protein
    // (i.e. for a SPTREntry, we can create/update several IntAct protein. One by BioSource)
    private int proteinTotal;

    // Successfully created/updated protein in IntAct
    private int entryCount;
    private int entrySkipped;

    private int proteinCreated;
    private int proteinUpdated;
    private int proteinUpToDate;

    private int spliceVariantTotal;
    private int spliceVariantCreated;
    private int spliceVariantUpdated;
    private int spliceVariantUpToDate;


    /**
     * Set of updated/created proteins during the process.
     */
    private Collection proteins;


    public UpdateProteins( IntactHelper helper ) throws UpdateException {
        super( helper );
    }

    public UpdateProteins( IntactHelper helper, boolean setOutput ) throws UpdateException {
        super( helper, setOutput );
    }

    public int getProteinCreatedCount() {
        return proteinCreated;
    }

    public int getProteinUpdatedCount() {
        return proteinUpdated;
    }

    public int getProteinUpToDateCount() {
        return proteinUpToDate;
    }

    public int getProteinCount() {
        return proteinTotal;
    }

    public int getProteinSkippedCount() {
        return ( proteinTotal - ( proteinCreated + proteinUpdated + proteinUpToDate ) );
    }

    public int getSpliceVariantCreatedCount() {
        return spliceVariantCreated;
    }

    public int getSpliceVariantUpdatedCount() {
        return spliceVariantUpdated;
    }

    public int getSpliceVariantUpToDateCount() {
        return spliceVariantUpToDate;
    }

    public int getSpliceVariantCount() {
        return spliceVariantTotal;
    }

    public int getSpliceVariantProteinCount() {
        return spliceVariantTotal;
    }

    public int getSpliceVariantSkippedCount() {
        return ( spliceVariantTotal - ( spliceVariantCreated + spliceVariantUpdated + spliceVariantUpToDate ) );
    }

    public int getEntryCount() {
        return entryCount;
    }

    public int getEntryProcessededCount() {
        return entryCount - entrySkipped;
    }

    public int getEntrySkippedCount() {
        return entrySkipped;
    }

    public void setDebugOnScreen( boolean debug ) {
        this.debugOnScreen = debug;
    }

    private void reset() {

        if( proteins == null ) {
            proteins = new ArrayList();
        } else {
            proteins.clear();
        }

        parsingExceptions.clear();

        proteinTotal = 0;
        proteinCreated = 0;
        proteinUpdated = 0;

        spliceVariantTotal = 0;
        spliceVariantCreated = 0;
        spliceVariantUpdated = 0;
        spliceVariantUpToDate = 0;

        proteinUpToDate = 0;
        entryCount = 0;
        entrySkipped = 0;
    }

    public final String getUrl( String uniprotAC ) {

        // the SRS url has been collected from the CvDatabase( uniprot ) during the initialisation process
        // and saved under srsUrl.
        return SearchReplace.replace( srsUrl, "${ac}", uniprotAC );
    }

    // TODO: the server might be busy, create a retry method ...

    public String getAnEntry( final String anUrl ) {
        BufferedReader br = null;
        StringBuffer sb = null;
        URL u = null;

        final String isProxySet = System.getProperty( "proxySet" );
        final String proxyHost = System.getProperty( "proxyHost" );
        final String proxyPort = System.getProperty( "proxyPort" );
        if( logger != null ) {
            logger.debug( "Uses: proxySet=" + isProxySet + ", proxyHost=" + proxyHost + ", proxyPort=" + proxyPort );
        }

        try {
            u = new URL( anUrl );
        } catch ( Exception e ) {
            if( logger != null ) {
                logger.error( "Please supply URL to getAnEntry() method ..." );
                logger.error( "If the URL returns html file, this program won't parse it." );
            }
        }

        InputStream in = null;
        try {
            in = u.openStream();
            InputStreamReader isr = new InputStreamReader( in );
            br = new BufferedReader( isr );
            String line;

            sb = new StringBuffer( 4096 );
            String lineSeparator = System.getProperty( "line.separator" );

            while ( ( line = br.readLine() ) != null ) {
                sb.append( line ).append( lineSeparator );
            }

            in.close();

            return sb.toString();
        } catch ( MalformedURLException e ) {
            if( logger != null ) {
                logger.error( anUrl, e );
            }
        } catch ( IOException e ) {
            if( logger != null ) {
                logger.error( anUrl, e );
            }
        } finally {
            if( in != null ) {
                try {
                    in.close();
                } catch ( Exception e ) {
                }
            }
        }

        return null;
    }

    /**
     * from a given string and a given pattern(string), to find all matches. The matched are
     * retured as a list. This method uses gnu.regexp.* package, not the org.apache.regexp.*
     *
     * @param textin  A string from which some pattern will be matched.
     * @param pattern A string as a pattern.
     * @return A list of matched pattern.
     */
    public REMatch[] match( String textin, String pattern ) {
        REMatch[] allMatches = null;
        try {
            RE magic = new RE( pattern );
            allMatches = magic.getAllMatches( textin );
        } catch ( Exception e_RE ) {
            if( logger!= null ) {
                logger.error( "Regular Expression error: text("+ textin +" pattern("+ pattern +")", e_RE );
            }
        }

        return allMatches;
    }

    private boolean isSpliceVariant( Protein protein ) {

        if( protein == null ) {
            return false;
        }

        Collection xrefs = protein.getXrefs();
        if( xrefs == null ) {
            return false;
        }

        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();
            CvXrefQualifier qualifier = xref.getCvXrefQualifier();
            if( qualifier == null ) {
                continue; // skip it
            }
            if( qualifier.equals( isoFormParentXrefQualifier ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * From a SPTR entry we try to get a set of IntAct protein.<br>
     * As a SPTR entry can contains several ACs, there is a probability that it gives us
     * several IntAct protein.<br>
     * We do not select splice variants.
     *
     * @param sptrEntry a SPTR entry
     * @param helper    the intact data source
     * @param taxid     the taxid filter (can be null)
     * @return An collection of Intact protein or null if an error occur.
     */
    private Collection getProteinsFromSPTrAC( SPTREntry sptrEntry, String taxid, IntactHelper helper )
            throws SPTRException {

        String spAC[] = sptrEntry.getAccessionNumbers();
        Collection proteins = new HashSet();
        int i = 0;
        try {
            Collection tmp = null;
            for ( i = 0; i < spAC.length; i++ ) {
                String ac = spAC[ i ];
                tmp = helper.getObjectsByXref( Protein.class, ac );

                if( tmp != null ) {
                    for ( Iterator iterator = tmp.iterator(); iterator.hasNext(); ) {
                        Protein p = (Protein) iterator.next();
                        // keep the protein only if the taxid is the same OR if no taxid is specified
                        if( taxid == null || p.getBioSource().getTaxId().equals( taxid ) ) {
                            if( !isSpliceVariant( p ) ) {
                                // insert only non splice variant
                                proteins.add( p );
                            }
                        }
                    }
                }
            }
        } catch ( IntactException e ) {
            // multiple object found for that criteria
            if( logger != null ) {
                logger.error( "error when retreiving Proteins from AC: " + spAC[ i ], e );
            }
            return null;
        }

        return proteins;
    }


    /**
     * Get existing splice variant generated from that SPTREntry.
     * <br>
     * <pre>
     * Algorithm sketch:
     *      0 if the collection of masters protein is not null, go to 2.1
     *      1 get AC's from SPTR Entry
     *      2 from those ACs, get master proteins (called PROTEINS).
     *          2.1 for each Protein p in PROTEINS
     *              2.1.1 get it's IntAct AC
     *              2.1.2 search for all Xrefs which have are qualified by isoform-parent with that AC as a primary key.
     *              2.1.3 Get the Proteins (should be one since Xref are not shared) who own that particular Xref.
     * </pre>
     *
     * @param sptrEntry The entry from which we will try to create the Proteins and splice variants
     * @param masters   The master protein of the splice variant
     * @param taxid     The organism we work on
     * @param helper    The database access
     * @return the created splice variants
     */
    private Collection getSpliceVariantFromSPTrAC( SPTREntry sptrEntry,
                                                   Collection masters,
                                                   String taxid,
                                                   IntactHelper helper )
            throws SPTRException {

        if( masters == null ) {
            masters = getProteinsFromSPTrAC( sptrEntry, taxid, helper );
            if( masters == null ) {
                return null;
            }
        }

        Collection spliceVariants = new HashSet(); // we want a distinct set of Protein

        // need that reference to ac out of the loop in order to have it available if an exception is raised.
        String ac = null;
        try {
            for ( Iterator iterator = masters.iterator(); iterator.hasNext(); ) {
                Protein protein = (Protein) iterator.next();
                ac = protein.getAc();

                // All splice proteins have 'this' protein as the primary id.
                Collection proteins = helper.search( Xref.class.getName(), "primaryId", ac );

                // Loop through proteins collection; only add the splice proteins.
                for ( Iterator iterator2 = proteins.iterator(); iterator2.hasNext(); ) {
                    Xref xref = (Xref) iterator2.next();
                    if( xref.getCvXrefQualifier().equals( isoFormParentXrefQualifier ) ) {
                        spliceVariants.addAll( helper.search( Protein.class.getName(), "ac", xref.getParentAc() ) );
                    }
                }
            }
        } catch ( IntactException e ) {
            // multiple object found for that criteria
            if( logger != null ) {
                logger.error( "error when retreiving splice variants (Protein) from AC: " + ac, e );
            }
            return null;
        }

        return spliceVariants;
    }


    /**
     * From a SPTREntry, that method will look for the correxponding proteins
     * in IntAct in order to update its data or create brand new if it doesn't exists.
     *
     * @param sptrEntry the SPTR entry
     * @param update    If true, update existing Protein objects according to the retrieved data.
     *                  else, skip existing Protein objects.
     */
    private void createProteinFromSPTrEntry( final SPTREntry sptrEntry,
                                             final boolean update ) throws SPTRException {

        Protein protein = null;
        int i;

        if( logger != null ) logger.info( "Update flag: " + update );

        try {
            // according to the SPTR entry, get the corresponding proteins in IntAct
            // TODO: we don't activate the taxid filter here
            Collection proteins = getProteinsFromSPTrAC( sptrEntry, null, helper );
            if( proteins == null ) {
                if( logger != null ) {
                    logger.error( "An error occured when trying to get IntAct protein, exit update" );
                }
                writeEntry2file();
                return;
            } else {
                if( logger != null && logger.isInfoEnabled() ) {
                    if( logger != null ) logger.info( "proteins found for that entry  (" + proteins.size() + "):" );
                    for ( Iterator iteratorX = proteins.iterator(); iteratorX.hasNext(); ) {
                        Protein p = (Protein) iteratorX.next();
                        logger.info( p.getAc() + " " + p.getShortLabel() + " " + p.getBioSource().getShortLabel() );
                    }
                }
            }

            // according to the SPTR entry, get the corresponding splice variant in IntAct
            // TODO: we don't activate the taxid filter here
            Collection spliceVariants = getSpliceVariantFromSPTrAC( sptrEntry, proteins, null, helper );
            if( spliceVariants == null ) {
                if( logger != null ) logger.error( "An error occured when trying to get IntAct splice variants, exit update" );
                writeEntry2file();
                return;
            } else {
                if( logger != null && logger.isInfoEnabled() ) {
                    logger.info( "Splice variant found for that entry  (" + spliceVariants.size() + "):" );
                    for ( Iterator iteratorX = spliceVariants.iterator(); iteratorX.hasNext(); ) {
                        Protein p = (Protein) iteratorX.next();
                        logger.info( p.getAc() + " " + p.getShortLabel() + " " + p.getBioSource().getShortLabel() );
                    }
                }
            }

            /**
             * Select which taxid to consider in the process.
             */
            int organismCount = sptrEntry.getOrganismNames().length;
            ArrayList taxids = new ArrayList( organismCount );
            for ( i = 0; i < organismCount; i++ ) {
                String organism = sptrEntry.getOrganismNames()[ i ];
                String entryTaxid = sptrEntry.getNCBITaxonomyID( organism );
                taxids.add( entryTaxid );
            }

            Collection masters = new HashSet(); // to store the proteins which might be a master of a splice variant.

            /**
             * Process all collected BioSource
             */
            int taxidCount = taxids.size();
            boolean generateProteinShortlabelUsingBiosource = false;
            if( taxidCount > 1 ) {
                generateProteinShortlabelUsingBiosource = true;
            }

            if( logger != null ) {
                logger.info( "generateProteinShortlabelUsingBiosource = " + generateProteinShortlabelUsingBiosource );
            }

            for ( i = 0; i < taxidCount; i++ ) {

                proteinTotal++;

                // for each taxid the user want to process
                String sptrTaxid = (String) taxids.get( i );
                if( logger != null ) {
                    logger.info( "Prossessing: sptrTaxid=" + sptrTaxid );
                }

                // get a valid Biosource from either Intact or Newt
                BioSource bioSource = bioSourceFactory.getValidBioSource( sptrTaxid );

                protein = null;
                // look for a protein in the set which has that taxid
                for ( Iterator iterator = proteins.iterator(); iterator.hasNext() && protein == null; ) {
                    Protein tmp = (Protein) iterator.next();
                    BioSource bs = tmp.getBioSource();

                    /*
                    * Problem here if the taxid in the entry is obsolete and
                    * in intact we have stored the up-to-date one ... we don't get in
                    * the loop and so that protein is not removed from the collection.
                    */
                    // TODO: to solve this, we could check all existing BioSource an update if needed.
                    if( bs != null && bs.getTaxId().equals( sptrTaxid ) ) {
                        // found ... remove it from the collection
                        protein = tmp;
                        // BEWARE: the fact to call .remove( o ) would raise an exception if an other object is read
                        // from the iterator.
                        proteins.remove( tmp );
                    }
                } // for, protein selection according to bioSource


                if( protein == null ) {
                    // didn't found an existing one so create a new one
                    if( logger != null ) {
                        logger.info( "No existing protein for that taxid (" + sptrTaxid + "), create a new one" );
                    }

                    if( localTransactionControl ) {
                        helper.startTransaction( BusinessConstants.OBJECT_TX );
                    }

                    if( logger != null ) {
                        logger.info( "Call createProtein with parameter BioSource.taxId=" + bioSource.getTaxId() );
                    }

                    if( ( protein = createNewProtein( sptrEntry, bioSource, generateProteinShortlabelUsingBiosource ) ) != null ) {
                        if( logger != null ) {
                            logger.info( "creation sucessfully done: " + protein.getShortLabel() );
                        }

                        // Keep that reference as existing protein for that Entry and taxid.
                        masters.add( protein );
                    }
                    if( localTransactionControl ) {
                        helper.finishTransaction();
                    }
                    if( logger != null ) {
                        logger.info( "Transaction complete" );
                    }

                } else {

                    // Keep that reference as existing protein for that Entry and taxid.
                    masters.add( protein );

                    if( update ) {
                        /*
                        * We are doing the update of the existing protein only if the
                        * user request it we only update its content if needed
                        */
                        if( logger != null ) {
                            logger.info( "A protein exists for that taxid (" + sptrTaxid + "), try to update" );
                        }

                        if( localTransactionControl ) {
                            helper.startTransaction( BusinessConstants.OBJECT_TX );
                        }
                        if( updateExistingProtein( protein, sptrEntry, bioSource, generateProteinShortlabelUsingBiosource ) ) {
                            if( logger != null ) {
                                logger.info( "update sucessfully done" );
                            }
                        }
                        if( localTransactionControl ) {
                            helper.finishTransaction();
                            if( logger != null ) {
                                logger.info( "Transaction complete" );
                            }
                        }
                    } else {
                        // Store the protein in the list we'll return
                        this.proteins.add( protein );
                    }
                }


                // retrieve the comments of that entry
                SPTRComment[] comments = sptrEntry.getComments( Factory.COMMENT_ALTERNATIVE_SPLICING );

                for ( int j = 0; j < comments.length; j++ ) {
                    SPTRComment comment = comments[ j ];
                    if( !( comment instanceof AlternativeSplicingAdapter ) ) {
                        if( logger != null ) {
                            logger.error( "Looking for Comment type: " + AlternativeSplicingAdapter.class.getName() );
                            logger.error( "Could not handle comment type: " + comment.getClass().getName() );
                            logger.error( "SKIP IT." );
                        }
                        continue; // skip it, go to next iteration.
                    }

                    AlternativeSplicingAdapter asa = (AlternativeSplicingAdapter) comment;
                    Isoform[] isoForms = asa.getIsoforms();

                    // for each comment, browse its isoforms ...
                    for ( int ii = 0; ii < isoForms.length; ii++ ) {
                        Isoform isoForm = isoForms[ ii ];

                        spliceVariantTotal++;

                        /*
                        * browse isoform's IDs which, in case they have been store in the database,
                        * are used as shortlabel of the related Protein.
                        */
                        String[] ids = isoForm.getIDs();
                        for ( int k = 0; k < ids.length; k++ ) {
                            String id = ids[ k ];

                            if( logger != null ) {
                                logger.info( "Splice variant ID: " + id );
                            }

                            // Search for an existing splice variant in the IntAct database
                            Protein spliceVariant = null;
                            for ( Iterator iterator = spliceVariants.iterator(); iterator.hasNext(); ) {
                                Protein sv = (Protein) iterator.next();

                                /* How to spot and select the right splice variant ?
                                 * -----------------------------------------------
                                 * Here we have to take into account that the label could be a lowercase
                                 * version of the isoForm id found in the Entry.
                                 * - OR -
                                 * that same id to which we have concatenated the biosource name.
                                 *
                                 * We do it by checking if the splice variant's shortlabel startwith
                                 * the isoform id (both lowercase) and if the biosource is the right one.
                                 * eg. shortlabel: p21181-1_human
                                 *     isoform id: P21181-1
                                 */

                                if( sv.getShortLabel().toLowerCase().startsWith( id.toLowerCase() )
                                    &&
                                    sv.getBioSource().equals( bioSource ) ) {
                                    // this isoform is already there for that bioSource... so update it.
                                    spliceVariant = sv;
                                    break; // exit the loop, we found it
                                }
                            }

                            if( spliceVariant != null ) {
                                // ... update it.

                                if( update ) {
                                    /*
                                    * We only perform an update of the existing splice variant if the user request it.
                                    */

                                    // get the original protein from the collected data
                                    Protein master = null;
                                    for ( Iterator iterator = masters.iterator(); iterator.hasNext(); ) {
                                        master = (Protein) iterator.next();
                                        if( master.getBioSource().equals( bioSource ) ) {
                                            break; // found it
                                        }
                                    }

                                    if( master == null ) {
                                        // error.
                                        if( logger != null ) {
                                            logger.debug( "No master protein exists for that splice variant: " + id );
                                        }
                                    }

                                    /**
                                     * We are doing the update of the existing protein only if the
                                     * user request it we only update its content if needed
                                     */
                                    if( logger != null ) {
                                        logger.info( "A splice variant exists for that taxid (" + sptrTaxid + "), try to update" );
                                    }

                                    if( localTransactionControl ) {
                                        /**
                                         * We want here to use a database transaction (NOT ONJECT) because
                                         * the creation of a splice variant can involves the creation of
                                         * Annotation. This is a problem with an Object transaction because
                                         * everything is written in the database when the transaction is
                                         * commited ... in the case of an Annotation, it needs to be written
                                         * in the DB before the Annotated object in order not to violate
                                         * any integrity rule.
                                         * By using a Database transaction (JDBC_TX) it is written when it's
                                         * asked for and everything is deleted if something goes wrong.
                                         */
                                        helper.startTransaction( BusinessConstants.JDBC_TX );
                                    }
                                    if( updateExistingSpliceVariant( isoForm, id, spliceVariant, master, sptrEntry,
                                                                     bioSource, generateProteinShortlabelUsingBiosource ) ) {
                                        if( logger != null ) {
                                            logger.info( "update sucessfully done" );
                                        }
                                    }
                                    if( localTransactionControl ) {
                                        helper.finishTransaction();
                                        if( logger != null ) {
                                            logger.info( "Transaction complete" );
                                        }
                                    }
                                } else {
                                    // Store the splice variant in the list we'll return
                                    this.proteins.add( spliceVariant );
                                }

                                // remove that splice variant from the collection.
                                spliceVariants.remove( spliceVariant );

                            } else {

                                // get the original protein from the collected data
                                Protein master = null;
                                if( logger != null ) {
                                    logger.info( "Look for master of: " + id );
                                }
                                for ( Iterator iterator = masters.iterator(); iterator.hasNext(); ) {
                                    master = (Protein) iterator.next();
                                    if( master.getBioSource().equals( bioSource ) ) {
                                        if( logger != null ) {
                                            logger.info( "Found(" + master.getShortLabel() + ")" );
                                        }
                                        break; // found it
                                    }
                                }
                                if( master == null ) {
                                    // Shouldn't happen, we have found the splice variant from the masters.
                                    if( debugOnScreen ) System.out.println( "Didn't found a master protein for " + id );
                                    if( logger != null ) {
                                        logger.error( "Didn't found a master protein for " + id );
                                    }
                                }

                                /**
                                 * We didn't find a Protein related to that isoform ... create it.
                                 * doesn't found so create a new one
                                 */
                                if( logger != null ) {
                                    logger.info( "No existing protein for that taxid (" + sptrTaxid + "), create a new one" );
                                }

                                if( localTransactionControl ) {
                                    /**
                                     * See remarks about database transaction above.
                                     */
                                    helper.startTransaction( BusinessConstants.JDBC_TX );
                                }
                                if( ( spliceVariant = createNewSpliceVariant( isoForm, id, master, sptrEntry, bioSource,
                                                                              generateProteinShortlabelUsingBiosource ) ) != null ) {
                                    if( logger != null ) {
                                        logger.info( "creation sucessfully done" );
                                    }
                                }
                                if( localTransactionControl ) {
                                    helper.finishTransaction();
                                }
                                if( logger != null ) {
                                    logger.info( "Transaction complete" );
                                }
                            }

                        } // for ids
                    } //for isoforms
                } //for comments
            } // for each taxid

            /*
            * Check if the protein list is empty, if not, that means we have in IntAct some
            * proteins linked with a BioSource which is not recorded in the current SPTR Entry
            */
            if( false == proteins.isEmpty() ) {

                if( logger != null ) {
                    logger.error( "The following association's <protein,taxid> list has been found in IntAct but not in SPTR:" );
                }
                for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
                    Protein p = (Protein) iterator.next();
                    BioSource bs = p.getBioSource();

                    if( logger != null ) {
                        logger.error( "\t intactAC=" + p.getAc() +
                                  " shortlabel:" + p.getShortLabel() +
                                  " taxid=" + ( bs == null ? "none" : bs.getTaxId() ) );
                    }
                }
            }
        } catch ( IntactException ie ) {
            if( logger != null ) {
                logger.error( ie.getRootCause(), ie );
            }

            writeEntry2file();

            // Try to rollback
            if( helper.isInTransaction() ) {
                if( logger != null ) {
                    logger.error( "Try to undo transaction." );
                }
                try {
                    // try to undo the transaction
                    helper.undoTransaction();
                } catch ( IntactException ie2 ) {
                    if( logger != null ) {
                        logger.error( "Could not undo the current transaction" );
                    }
                }
            }
        }

    } // createProteinFromSPTrEntry

    public boolean addNewXref( AnnotatedObject current, final Xref xref ) {
        // Make sure the xref does not yet exist in the object
        if( current.getXrefs().contains( xref ) ) {
            if( logger != null ) {
                logger.info( "SKIPPED: [" + xref + "] already exists" );
            }
            return false; // quit
        }

        // add the xref to the AnnotatedObject
        current.addXref( xref );

        // That test is done to avoid to record in the database an Xref
        // which is already linked to that AnnotatedObject.
        if( xref.getParentAc() == current.getAc() ) {
            try {
                helper.create( xref );
                if( logger != null ) {
                    logger.info( "CREATED: [" + xref + "]" );
                }
            } catch ( Exception e_xref ) {
                if( logger != null ) {
                    logger.error( "Error while creating an Xref for protein " + current, e_xref );
                }
                return false;
            }
        }

        return true;
    }

    public void addNewAlias( AnnotatedObject current, Alias alias ) {
        // Make sure the alias does not yet exist in the object
        Collection aliases = current.getAliases();
        for ( Iterator iterator = aliases.iterator(); iterator.hasNext(); ) {
            Alias anAlias = (Alias) iterator.next();
            if( anAlias.equals( alias ) ) {
                if( logger != null ) {
                    logger.info( "SKIPPED: [" + alias + "] already exists" );
                }
                return; // already in, exit
            }
        }

        // add the alias to the AnnotatedObject
        current.addAlias( alias );

        // That test is done to avoid to record in the database an Alias
        // which is already linked to that AnnotatedObject.
        if( alias.getParentAc() == current.getAc() ) {
            try {
                helper.create( alias );
                if( logger != null ) {
                    logger.info( "CREATED: [" + alias + "]" );
                }
            } catch ( Exception e_alias ) {
                if( logger != null ) {
                    logger.error( "Error when creating an Alias for protein " + current, e_alias );
                }
            }
        }
    }

    /**
     * Add an annotation to an annotated object.
     * <br>
     * We check if that annotation is not already existing, if so, we don't record it.
     *
     * @param current    the annotated object to which we want to add an Annotation.
     * @param annotation the annotation to add the Annotated object
     */
    public void addNewAnnotation( AnnotatedObject current, final Annotation annotation ) {

        // TODO: what if an annotation is already existing ... should we use a single one ?
        // TODO: YES ! we should search dor it first and reuse existing Annotation

        // Make sure the alias does not yet exist in the object
        Collection annotations = current.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
            Annotation anAnnotation = (Annotation) iterator.next();
            if( anAnnotation.equals( annotation ) ) {
                return; // already in, exit
            }
        }

        // add the alias to the AnnotatedObject
        current.addAnnotation( annotation );

        try {
            helper.create( annotation );
            if( logger != null ) {
                logger.info( "ADD " + annotation + " to: " + current.getShortLabel() );
            }
        } catch ( Exception e_alias ) {
            if( logger != null ) {
                logger.error( "Error when creating an Annotation for protein " + current, e_alias );
            }
        }
    }

    /**
     * update all Xref specific to a database.
     * That procedure is used when creating and updating a Protein Xref.
     *
     * @param sptrEntry  Entry from which we get the Xrefs
     * @param protein    The protein to update
     * @param database   The database filter
     * @param cvDatabase The CvDatabase to link in the Protein's Xref
     * @return true if the protein has been updated, else false.
     * @throws SPTRException
     */
    private boolean updateXref( final SPTREntry sptrEntry,
                                Protein protein,
                                final String database,
                                final CvDatabase cvDatabase ) throws SPTRException {

        // TODO: in order to remove the old Xrefs (ie. those who disapeared from the Entry)
        // TODO: we could flag those who have been created or are up-to-date ... the rest
        // TODO: is to be deleted

        boolean needUpdate = false;

        // create existing GO Xrefs
        SPTRCrossReference cr[] = sptrEntry.getCrossReferences( database );
        if( logger != null ) {
            logger.info( "Look in the entry for Xref of type: " + database + " (" + cr.length + " found)" );
        }


        for ( int i = 0; i < cr.length; i++ ) {
            SPTRCrossReference sptrXref = cr[ i ];
            String ac = sptrXref.getAccessionNumber();
            String id = sptrXref.getPropertyValue( SPTRCrossReference.PROPERTY_DESCRIPTION );

            if( logger != null ) {
                logger.info( "XREF[" + i + "] - id: " + ac + "  desc: " + id );
            }

            Xref xref = new Xref( myInstitution,
                                  cvDatabase,
                                  ac,
                                  id, null, null );

            /**
             * Watch out here - if we use ||, as soon as the function returns true once
             *                  it's never called again because || optimize the statement.
             *                  Using | is the solution
             */
            needUpdate = needUpdate | addNewXref( protein, xref );
        }

        return needUpdate;
    }

    private boolean isAliasAlreadyExisting( Collection aliases, String name, CvAliasType aliasType ) {

        /* TODO: the lowercase name is temporary, it should be removed later.
        *       Currently, all name of alias are stored in lozercase in the db.
        */
        String lowerCaseName = name.toLowerCase();

        for ( Iterator iterator = aliases.iterator(); iterator.hasNext(); ) {
            Alias alias = (Alias) iterator.next();

            if( alias.getName().equals( lowerCaseName ) && alias.getCvAliasType().equals( aliasType ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * update all Xref specific to a database.
     * That procedure is used when creating and updating a Protein Xref.
     *
     * @param protein The protein to update
     * @return true if the protein has been updated, else false.
     * @throws SPTRException
     */
    private boolean updateAliases( final SPTREntry sptrEntry,
                                   Protein protein ) throws SPTRException {

        boolean needUpdate = false;
        Collection aliases = protein.getAliases();
        // gene[i][0]: gene name
        // gene[i][1 ...]: synomyms of the gene's name
        String[][] genes = sptrEntry.getGenes();

        Alias alias = null;

        for ( int i = 0; i < genes.length; i++ ) {

            if( !isAliasAlreadyExisting( aliases, genes[ i ][ 0 ], geneNameAliasType ) ) {
                alias = new Alias( myInstitution,
                                   protein,
                                   geneNameAliasType, // gene-name
                                   genes[ i ][ 0 ] );

                // link the Xref to the protein and record it in the database
                addNewAlias( protein, alias );
                if( logger != null ) {
                    logger.info( "ADD new Alias[name: " + alias.getName() +
                             " type: " + geneNameAliasType.getShortLabel() + "]" +
                             ", to: " + protein.getShortLabel() );
                }
                needUpdate = true;
            } else {
                if( logger != null ) {
                    logger.info( "SKIP Alias[name: " + genes[ i ][ 0 ] +
                             " type: " + geneNameAliasType.getShortLabel() + "]" +
                             ", for: " + protein.getShortLabel() );
                }
            }

            for ( int ii = 1; ii < genes[ i ].length; ii++ ) {

                if( !isAliasAlreadyExisting( aliases, genes[ i ][ ii ], geneNameSynonymAliasType ) ) {
                    alias = new Alias( myInstitution,
                                       protein,
                                       geneNameSynonymAliasType, // gene-name-synonym
                                       genes[ i ][ ii ] );

                    // link the Xref to the protein and record it in the database
                    addNewAlias( protein, alias );
                    if( logger != null ) {
                        logger.info( "ADD new Alias[name: " + alias.getName() +
                                 " type: " + geneNameSynonymAliasType.getShortLabel() + "]" +
                                 ", to: " + protein.getShortLabel() );
                    }
                    needUpdate = true;
                } else {
                    if( logger != null ) {
                        logger.info( "SKIP Alias[name: " + genes[ i ][ ii ] +
                                 " type: " + geneNameSynonymAliasType.getShortLabel() + "]" +
                                 ", for: " + protein.getShortLabel() );
                    }
                }
            }
        }

        return needUpdate;
    } // updateAliases

    private String generateProteinShortLabel( SPTREntry sptrEntry,
                                              BioSource bioSource,
                                              boolean generateProteinShortlabelUsingBiosource ) throws SPTRException {
        String shortlabel = null;

        if( generateProteinShortlabelUsingBiosource ) {

            String[][] genes = sptrEntry.getGenes();

            if( genes.length > 0 && genes[ 0 ].length > 0 ) // if there is at least one gene
            {
                shortlabel = genes[ 0 ][ 0 ];
            }

            if( shortlabel == null ) {
                final String msg = "WARNING: could not generate the Shortlabel, no gene name available. Using the AC.";
                if( debugOnScreen ) {
                    System.err.println( msg );
                }
                if( logger != null ) {
                    logger.warn( msg );
                }
                shortlabel = sptrEntry.getID().toLowerCase();
            } else {
                // check if the ID contains already _specie (TREMBL)
                int index = shortlabel.indexOf( '_' );
                if( index != -1 ) {
                    if( logger != null ) {
                        logger.info( "Remove existing _${specie} from " + shortlabel );
                    }
                    shortlabel = shortlabel.substring( 0, index );
                    if( logger != null ) {
                        logger.info( "Result: " + shortlabel );
                    }
                }

                // Concatenate Biosource to the gene name !
                if( bioSource.getShortLabel() != null && !bioSource.getShortLabel().equals( "" ) ) {
                    shortlabel = shortlabel + "_" + bioSource.getShortLabel();
                } else {
                    final String msg = "WARNING: generate the shortlabel using taxid since the shortlabel doesn't exists.";
                    if( debugOnScreen ) {
                         System.err.println( msg );
                     }
                    if( logger != null ) {
                        logger.warn( msg );
                    }
                    shortlabel = shortlabel + "_" + bioSource.getTaxId();
                }
            }
        } else {
            shortlabel = sptrEntry.getID().toLowerCase();
        }

        return shortlabel.toLowerCase();
    }


    /**
     * Update an existing protein with data from a SPTR Entry.
     *
     * @param protein   the protein to update
     * @param sptrEntry the source entry
     * @param bioSource the BioSource to link to the Protein
     * @return true is the protein is created
     * @throws SPTRException
     * @throws IntactException
     */
    private boolean updateExistingProtein( Protein protein,
                                           SPTREntry sptrEntry,
                                           BioSource bioSource,
                                           boolean generateProteinShortlabelUsingBiosource )
            throws SPTRException,
                   IntactException {

        boolean needUpdate = false;

        // get the protein info we need
        String fullName = sptrEntry.getProteinName();
        String shortLabel = generateProteinShortLabel( sptrEntry, bioSource, generateProteinShortlabelUsingBiosource );
        String sequence = sptrEntry.getSequence();
        String crc64 = sptrEntry.getCRC64();

        if( !protein.getFullName().equals( fullName ) ) {
            protein.setFullName( fullName );
            needUpdate = true;
        }

        if( !protein.getShortLabel().equals( shortLabel ) ) {
            protein.setShortLabel( shortLabel );
            needUpdate = true;
        }

        if( !protein.getSequence().equals( sequence ) ) {
            protein.setSequence( helper, sequence );
            needUpdate = true;
        }

        if( !protein.getCrc64().equals( crc64 ) ) {
            protein.setCrc64( crc64 );
            needUpdate = true;
        }

        // check bioSource
        boolean needBiosourceUpdate = false;
        BioSource _biosource = protein.getBioSource();
        if( !_biosource.getTaxId().equals( bioSource.getTaxId() ) ) {
            needBiosourceUpdate = true;
        }

        if( !_biosource.getFullName().equals( bioSource.getFullName() ) ) {
            needBiosourceUpdate = true;
        }

        if( needBiosourceUpdate ) {
            protein.setBioSource( bioSource );
            needUpdate = true;
        }

        // TODO: the update of the cross reference and the alias should also:
        // TODO   o Delete Xref that are no longer in the Entry
        // TODO   o Delete Alias that are no longer in the Entry

        /**
         * false || false -> false
         * false || true -> true
         * true || false -> true
         * true || true -> true
         */
        needUpdate = needUpdate | updateXref( sptrEntry, protein, Factory.XREF_SGD, sgdDatabase );
        needUpdate = needUpdate | updateXref( sptrEntry, protein, Factory.XREF_GO, goDatabase );
        needUpdate = needUpdate | updateXref( sptrEntry, protein, Factory.XREF_INTERPRO, interproDatabase );
        needUpdate = needUpdate | updateXref( sptrEntry, protein, Factory.XREF_FLYBASE, flybaseDatabase );

        // update SPTR Xrefs
        needUpdate = needUpdate | updateUniprotXref4Protein( sptrEntry, protein );

        // check on aliases
        needUpdate = needUpdate | updateAliases( sptrEntry, protein );

        // keep that protein
        proteins.add( protein );

        if( needUpdate == true ) {
            // update databse
            try {
                /**
                 * If the object is proxied, the update will throw an Exception: ClassNotPersistenceCapableException
                 * So we use the IntactHelper to get the realObject.
                 */
                helper.update( IntactHelper.getRealIntactObject( protein ) );

                if( debugOnScreen ) {
                    System.out.print( " pU" );
                }
                proteinUpdated++;
                return true;
            } catch ( IntactException ie ) {
                if( logger != null ) logger.error( protein, ie );
                throw ie;
            }
        } else {
            if( logger != null ) logger.info( "That protein was up-to-date" );
            if( debugOnScreen ) {
                System.out.print( " p-" );
            }
            proteinUpToDate++;
        }

        return false;
    } // updateExistingProtein

    /**
     * Update (create them if not exist) SPTR Cross references to the given protein
     *
     * @param sptrEntry the entry in which we'll find the primary ID of the Xrefs
     * @param protein   the protein to update
     * @return true if at least one Xref as been added, else false.
     * @throws SPTRException
     */
    private boolean updateUniprotXref4Protein( SPTREntry sptrEntry, Protein protein ) throws SPTRException {

        boolean updated = false;
        String proteinAC[] = sptrEntry.getAccessionNumbers();
        String shortLabel = protein.getShortLabel();

        for ( int i = 0; i < proteinAC.length; i++ ) {
            String ac = proteinAC[ i ];

            // The first AC is primary, all others are secondary
            CvXrefQualifier xrefQualifier;
            if( i == 0 ) {
                xrefQualifier = identityXrefQualifier;
            } else {
                xrefQualifier = secondaryXrefQualifier;
            }

            Xref sptrXref = new Xref( myInstitution,
                                      uniprotDatabase,
                                      ac,
                                      shortLabel, null, null );

            sptrXref.setCvXrefQualifier( xrefQualifier );
            updated = updated | addNewXref( protein, sptrXref );
        }

        return updated;
    }

    /**
     * Update (create them if not exist) UNIPROT Cross reference to the given splice variant
     *
     * @param sptrEntry     the entry in which we'll find the primary ID of the Xrefs
     * @param spliceVariant the splice variant to update
     * @return true if at least one Xref as been added, else false.
     * @throws SPTRException
     */
    private boolean updateUniprotXref4SpliceVariant( SPTREntry sptrEntry,
                                                     Protein spliceVariant,
                                                     String isoId ) throws SPTRException {

        String id = sptrEntry.getID();

        Xref sptrXref = new Xref( myInstitution,
                                  uniprotDatabase,
                                  isoId,
                                  id, null, null );

        sptrXref.setCvXrefQualifier( identityXrefQualifier );

        return addNewXref( spliceVariant, sptrXref );
    }


    /**
     * From a SPTR Entry, create in IntAct a new protein.
     *
     * @param sptrEntry the source entry
     * @param bioSource the BioSource to link to the Protein
     * @return true is the protein is created
     * @throws SPTRException
     * @throws IntactException
     */
    private Protein createNewProtein( SPTREntry sptrEntry,
                                      BioSource bioSource,
                                      boolean generateProteinShortlabelUsingBiosource )
            throws SPTRException,
                   IntactException {

        /**
         * To avoid to have multiple species having the same short label
         * eg. cdc42 are known in human, mouse, bovine and dog
         * we shoold have the labels: cdc42_human, cdc42_mouse ...
         */
        String shortLabel = generateProteinShortLabel( sptrEntry, bioSource, generateProteinShortlabelUsingBiosource );

        Protein protein = new ProteinImpl( myInstitution, bioSource, shortLabel );

        // get the protein info we need
        helper.create( protein );

        String fullName = sptrEntry.getProteinName();
        String sequence = sptrEntry.getSequence();
        String crc64 = sptrEntry.getCRC64();

        protein.setFullName( fullName );
        protein.setSequence( helper, sequence );
        protein.setCrc64( crc64 );

        updateXref( sptrEntry, protein, Factory.XREF_SGD, sgdDatabase );
        updateXref( sptrEntry, protein, Factory.XREF_GO, goDatabase );
        updateXref( sptrEntry, protein, Factory.XREF_INTERPRO, interproDatabase );
        updateXref( sptrEntry, protein, Factory.XREF_FLYBASE, flybaseDatabase );

        updateUniprotXref4Protein( sptrEntry, protein );

        // create Aliases
        String[][] genes = sptrEntry.getGenes();
        Alias alias = null;
        for ( int i = 0; i < genes.length; i++ ) {

            alias = new Alias( myInstitution,
                               protein,
                               geneNameAliasType, // gene-name
                               genes[ i ][ 0 ] );

            addNewAlias( protein, alias );

            for ( int ii = 1; ii < genes[ i ].length; ii++ ) {

                alias = new Alias( myInstitution,
                                   protein,
                                   geneNameSynonymAliasType, // gene-name-synonym
                                   genes[ i ][ ii ] );

                addNewAlias( protein, alias );
            }
        }

        // update database
        try {
            // Only update if the protein exists in the DB.
            if( helper.isPersistent( protein ) ) {
                helper.update( protein );
            }
            // keep that protein
            proteins.add( protein );

            if( logger != null ) {
                logger.info( "protein updated: " + protein );
            }
            if( debugOnScreen ) {
                System.out.print( " pC" );
            }

            proteinCreated++;
            return protein;
        } catch ( IntactException e ) {
            if( logger != null ) {
                logger.error( protein, e );
            }
            throw e;
        }

    } // createNewProtein


    /**
     * Update an existing splice variant with data from a SPTR Entry.
     *
     * @param isoform       the isoform from which is originated that splice variant
     * @param spliceVariant the spliceVariant to update
     * @param master        the master protein to which link that splice variant
     * @param sptrEntry     the source entry
     * @param bioSource     the BioSource to link to the Protein
     * @return true is the spliceVariant is updated or up-to-date
     * @throws SPTRException
     * @throws IntactException
     */
    private boolean updateExistingSpliceVariant( Isoform isoform,
                                                 String isoId,
                                                 Protein spliceVariant,
                                                 Protein master,
                                                 SPTREntry sptrEntry,
                                                 BioSource bioSource,
                                                 boolean generateProteinShortlabelUsingBiosource )
            throws SPTRException,
                   IntactException {

        boolean needUpdate = false;

        if( master == null ) {
            if( logger != null ) {
                logger.error( "the master protein is null ... can't do that" );
            }
            return false;
        }

        // get the spliceVariant info we need
        String fullName = sptrEntry.getProteinName();
        String shortLabel = isoId;

        if( generateProteinShortlabelUsingBiosource ) {
            if( bioSource.getShortLabel() != null && !bioSource.getShortLabel().equals( "" ) ) {
                shortLabel = shortLabel + "_" + bioSource.getShortLabel();
            } else {
                final String msg = "WARNING: generate the shortlabel using taxid since the shortlabel doesn't exists.";
                if( debugOnScreen ) {
                    System.err.println( msg );
                }
                if( logger != null ) {
                    logger.warn( msg );
                }
                shortLabel = shortLabel + "_" + bioSource.getTaxId();
            }
        }
        shortLabel = shortLabel.toLowerCase();

        String sequence = null;
        String crc64 = null;
        try {
            sequence = sptrEntry.getAlternativeSequence( isoform );
            if( sequence != null ) {
                crc64 = Crc64.getCrc64( sequence );
            }
        } catch ( FeatureException e ) {
            if( logger != null ) {
                logger.error( "Could not get the Alternative splice variant sequence from SPTREntry.", e );
            }
            return false;
        }

        if( !spliceVariant.getFullName().equals( fullName ) ) {
            spliceVariant.setFullName( fullName );
            needUpdate = true;
        }

        if( !spliceVariant.getShortLabel().equals( shortLabel ) ) {
            spliceVariant.setShortLabel( shortLabel );
            needUpdate = true;
        }

        /**
         * The CRC64 is updated only if the sequence is so.
         */
        String _sequence = spliceVariant.getSequence();
        if( _sequence == null ) {
            if( sequence != null && ( false == "".equals( sequence ) ) ) {
                spliceVariant.setSequence( helper, sequence );
                spliceVariant.setCrc64( crc64 );
                needUpdate = true;
            }
        } else if( !spliceVariant.getSequence().equals( sequence ) ) {
            spliceVariant.setSequence( helper, sequence );
            spliceVariant.setCrc64( crc64 );
            needUpdate = true;
        }

        if( !spliceVariant.getBioSource().equals( bioSource ) ) {
            spliceVariant.setBioSource( bioSource );
            needUpdate = true;
        }

        // check the only single Xref (isoform-parent), which references the master spliceVariant ...
        // TODO count only the new reference of XREF (isoform-parent).
        Collection xrefs = spliceVariant.getXrefs();
        Xref isoformXref = null;
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            isoformXref = (Xref) iterator.next();
            if( isoformXref.getCvXrefQualifier().equals( isoFormParentXrefQualifier ) ) {
                break; // found it.
            }
        }
        if( isoformXref == null ) {
            // error ... but create it.
            isoformXref = new Xref( myInstitution,
                                    intactDatabase,
                                    master.getAc(),
                                    master.getShortLabel(),
                                    null, isoFormParentXrefQualifier );
            needUpdate = needUpdate | addNewXref( spliceVariant, isoformXref );
        } else {
            // check for update
            if( !isoformXref.getPrimaryId().equals( master.getAc() ) ) {
                // TODO odd, should not happen, so warn the user
                isoformXref.setPrimaryId( master.getAc() );
                needUpdate = true;
            }

            if( !isoformXref.getSecondaryId().equals( master.getShortLabel() ) ) {
                // TODO odd, should not happen, so warn the user
                isoformXref.setSecondaryId( master.getShortLabel() );
                needUpdate = true;
            }
        }

        // update SPTR Xrefs.
        needUpdate = needUpdate | updateUniprotXref4SpliceVariant( sptrEntry, spliceVariant, isoId );

        // check for aliases ... that we could update as Alias.
        Collection aliases = spliceVariant.getAliases();
        String[] isoSynonyms = isoform.getSynonyms();
        for ( int i = 0; i < isoSynonyms.length; i++ ) {
            String isoSynonym = isoSynonyms[ i ];

            boolean found = false;
            for ( Iterator iterator = aliases.iterator(); iterator.hasNext(); ) {
                Alias alias = (Alias) iterator.next();
                if( alias.getCvAliasType().equals( isoformSynonym )
                    &&
                    alias.getName().equalsIgnoreCase( isoSynonym ) ) { // stored lowercase in the database !
                    found = true;
                    break; // exit the inner loop.
                }
            }

            if( found == false ) {
                // create a new alias
                Alias alias = new Alias( myInstitution, spliceVariant, isoformSynonym, isoSynonym );
                addNewAlias( spliceVariant, alias );
                needUpdate = true;
            }
        }

        // check for the note ... that we could update as an Annotation.
        String note = isoform.getNote();
        if( ( note != null ) && ( !note.trim().equals( "" ) ) ) {
            Collection annotations = spliceVariant.getAnnotations();
            Annotation annotation = null;
            for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
                annotation = (Annotation) iterator.next();
                if( annotation.getCvTopic().equals( isoformComment ) ) {
                    break; // found it.
                }
            }

            if( annotation == null ) {
                // create it
                annotation = new Annotation( myInstitution, isoformComment );
                annotation.setAnnotationText( isoform.getNote() );
                helper.create( annotation );
                spliceVariant.addAnnotation( annotation );
                needUpdate = true;
            } else {
                // try to update it.
                if( !annotation.getAnnotationText().equals( isoform.getNote() ) ) {
                    annotation.setAnnotationText( isoform.getNote() );
                    needUpdate = true;
                }
            }
        }

        // keep that spliceVariant
        proteins.add( spliceVariant );

        if( needUpdate == true ) {
            // update databse
            try {
                /**
                 * If the object is proxied, the update will throw an Exception: ClassNotPersistenceCapableException
                 * So we use the IntactHelper to get the realObject.
                 */
                helper.update( IntactHelper.getRealIntactObject( spliceVariant ) );

                if( debugOnScreen ) {
                    System.out.print( " svU" );
                }
                spliceVariantUpdated++;
                return true;
            } catch ( IntactException ie ) {
                if( logger != null ) {
                    logger.error( spliceVariant, ie );
                }
                throw ie;
            }
        } else {
            if( logger != null ) {
                logger.info( "That splice variant was up-to-date" );
            }
            if( debugOnScreen ) {
                System.out.print( " sv-" );
            }
            spliceVariantUpToDate++;
        }

        return false;
    } // updateExistingSpliceVariant


    /**
     * From a SPTR Entry, create in IntAct a new splice variant.
     * <br>
     * Note that few information are store in that object, the main of it
     * is the sequence/crc64, eventual alias and node. The rest of the data is
     * store in the master protein that we can reach by following the Xref (isoform-parent).
     *
     * @param isoform   the isoform from which is originated that splice variant
     * @param sptrEntry the source entry
     * @param bioSource the BioSource to link to the Protein
     * @return true is the protein is created
     * @throws SPTRException
     * @throws IntactException
     */
    private Protein createNewSpliceVariant( Isoform isoform,
                                            String isoId,
                                            Protein master,
                                            SPTREntry sptrEntry,
                                            BioSource bioSource,
                                            boolean generateProteinShortlabelUsingBiosource )
            throws SPTRException, IntactException {

        String shortLabel = isoId;

        if( generateProteinShortlabelUsingBiosource ) {
            if( bioSource.getShortLabel() != null && !bioSource.getShortLabel().equals( "" ) ) {
                shortLabel = shortLabel + "_" + bioSource.getShortLabel();
            } else {
                System.err.println( "WARNING: generate the shortlabel using taxid since the shortlabel doesn't exists." );
                shortLabel = shortLabel + "_" + bioSource.getTaxId();
            }
        }

        Protein spliceVariant = new ProteinImpl( myInstitution, bioSource, shortLabel.toLowerCase() );

        // get the spliceVariant info we need
        helper.create( spliceVariant );

        String fullName = sptrEntry.getProteinName();

        if( master == null ) {
            if( logger != null ) {
                logger.error( " The given master is null, EXIT " );
            }
            return null;
        }

        String sequence = null;
        String crc64 = null;
        try {
            sequence = sptrEntry.getAlternativeSequence( isoform );
            if( sequence != null ) {
                crc64 = Crc64.getCrc64( sequence );
            }
        } catch ( FeatureException e ) {
            if( logger != null ) {
                logger.error( "Could not get the Alternative splice variant sequence from SPTREntry.", e );
            }
            return null;
        }

        spliceVariant.setFullName( fullName );
        spliceVariant.setSequence( helper, sequence );
        spliceVariant.setCrc64( crc64 );

        // add Xref (isoform-parent), which links to the splice variant's master protein ...
        Xref isoformXref = new Xref( myInstitution,
                                     intactDatabase,
                                     master.getAc(),
                                     master.getShortLabel(),
                                     null, isoFormParentXrefQualifier );
        addNewXref( spliceVariant, isoformXref );

        // Add UNIPROT xref (as it is done for a master protein.)
        updateUniprotXref4SpliceVariant( sptrEntry, spliceVariant, isoId );

        // Add existing synonyms ... as Alias.
        String[] isoSynonyms = isoform.getSynonyms();
        for ( int i = 0; i < isoSynonyms.length; i++ ) {
            String isoSynonym = isoSynonyms[ i ];
            Alias alias = new Alias( myInstitution, spliceVariant, isoformSynonym, isoSynonym );
            addNewAlias( spliceVariant, alias );
        }

        // Add existing note ... as an Annotation.
        String note = isoform.getNote();
        if( ( note != null ) && ( !note.trim().equals( "" ) ) ) {
            Annotation annotation = new Annotation( myInstitution, isoformComment );
            annotation.setAnnotationText( isoform.getNote() );
            addNewAnnotation( spliceVariant, annotation );
        }

        // update database
        try {
            helper.update( spliceVariant );

            // keep that spliceVariant
            proteins.add( spliceVariant );

            if( logger != null ) {
                logger.info( "spliceVariant updated: " + spliceVariant );
            }
            if( debugOnScreen ) {
                System.out.print( " svC" );
            }

            spliceVariantCreated++;
            return spliceVariant;
        } catch ( IntactException e ) {
            if( logger != null ) {
                logger.error( spliceVariant, e );
            }
            throw e;
        }
    } // createNewSpliceVariant


    public Collection insertSPTrProteins( String proteinAc ) {

        if( proteinAc == null || proteinAc.trim().equals( "" ) ) {
            throw new IllegalArgumentException( "The protein AC MUST not be null or empty." );
        }

        String url = getUrl( proteinAc );
        int i = insertSPTrProteinsFromURL( url, null, true );
        if( debugOnScreen ) {
            System.out.println( i + " proteins created/updated." );
        }

        return proteins;
    }

    /**
     * Filter a set of Proteins based on their biosource's taxid.
     *
     * @param c     the collection to filter out.
     * @param taxid the taxid on which we base the filter.
     *              ie. we'll keep all proteins having that taxid
     * @return a set of proteins where all have the given taxid
     */
    private Collection filterOnTaxid( Collection c, String taxid ) {

        Collection filteredProtein = new ArrayList();

        for ( Iterator iterator = c.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();
            if( protein.getBioSource().getTaxId().equals( taxid ) ) {
                filteredProtein.add( protein );
            }
        }

        return filteredProtein;
    }

    public Collection insertSPTrProteins( String proteinAc, String taxId, boolean update ) {

        if( proteinAc == null || proteinAc.trim().equals( "" ) ) {
            throw new IllegalArgumentException( "The protein AC MUST not be null or empty." );
        }

        String url = getUrl( proteinAc );
        int i = insertSPTrProteinsFromURL( url, taxId, update );
        if( debugOnScreen ) {
            System.out.println( i + " proteins created/updated." );
        }

        // TODO: could be nice to have a method like getProteins() and getProtein( String taxid )
        // to allow multiple post call to insert.
        if( taxId != null ) {
            // filter out using the given taxid before to return the collection.
            Collection c = filterOnTaxid( proteins, taxId );
            if( logger != null && logger.isInfoEnabled() ) {
                logger.info( "Protein selected after filtering (" + c.size() + "):" );
                for ( Iterator iterator = c.iterator(); iterator.hasNext(); ) {
                    Protein protein = (Protein) iterator.next();
                    logger.info( "\t" + protein.getShortLabel() );
                }
            }
            return c;
        }

        if( logger != null && logger.isInfoEnabled() ) {
            logger.info( "Protein created/updated (" + proteins.size() + "):" );
            for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
                Protein protein = (Protein) iterator.next();
                logger.info( "\t" + protein.getShortLabel() );
            }
        }

        return proteins;
    }

    /**
     * Creates a simple Protein object for entries which are not in SPTR.
     * The Protein will more or less only contain the crossreference to the source database.
     *
     * @param anAc      The primary identifier of the protein in the external database.
     * @param aDatabase The database in which the protein is listed.
     * @param aTaxId    The tax id the protein should have
     * @return the protein created or retrieved from the IntAct database
     */
    public Protein insertSimpleProtein( String anAc, CvDatabase aDatabase, String aTaxId )
            throws IntactException {

        // Search for the protein or create it
        Collection newProteins = helper.getObjectsByXref( Protein.class, anAc );

        if( localTransactionControl ) {
            helper.startTransaction( BusinessConstants.OBJECT_TX );
        }

        // Get or create valid biosource from taxid
        BioSource validBioSource = bioSourceFactory.getValidBioSource( aTaxId );

        /* If there were obsolete taxids in the db, they should now be updated.
         * So we will only compare valid biosources.
         */

        // Filter for exactly one entry with appropriate taxId
        Protein targetProtein = null;
        for ( Iterator i = newProteins.iterator(); i.hasNext(); ) {
            Protein tmpProtein = (Protein) i.next();
            if( tmpProtein.getBioSource().getTaxId().equals( validBioSource.getTaxId() ) ) {
                if( null == targetProtein ) {
                    targetProtein = tmpProtein;
                } else {
                    throw new IntactException( "More than one Protein with AC "
                                               + anAc
                                               + " and taxid "
                                               + aTaxId
                                               + " found." );
                }
            }
        }

        if( null == targetProtein ) {
            // No appropriate protein found, create it.

            // Create new Protein
            targetProtein = new ProteinImpl( myInstitution, validBioSource, anAc );
            helper.create( targetProtein );

            // Create new Xref if a DB has been given
            if( null != aDatabase ) {
                Xref newXref = new Xref( myInstitution, aDatabase, anAc, null, null, null );
                newXref.setOwner( myInstitution );
                newXref.setCvDatabase( aDatabase );
                newXref.setPrimaryId( anAc );
                targetProtein.addXref( newXref );
                helper.create( newXref );
            }
        }

        if( localTransactionControl ) {
            helper.finishTransaction();
        }

        return targetProtein;
    }


    public int insertSPTrProteinsFromURL( String sourceUrl, String taxid, boolean update ) {

        Collection result = null;

        try {
            if( logger != null ) {
                logger.info( "update from URL: " + sourceUrl );
            }
            if( debugOnScreen ) {
                System.out.println( "update from URL: " + sourceUrl );
            }

            if( sourceUrl == null ) {
                return 0;
            }

            URL url = new URL( sourceUrl );
            InputStream is = url.openStream();

            result = insertSPTrProteins( is, taxid, update );

            is.close();

        } catch ( MalformedURLException e ) {
            if( debugOnScreen ) {
                e.printStackTrace();
            }
            if( logger != null ) {
                logger.error( "URL error: " + sourceUrl, e );
                logger.error( "Please provide a valid URL" );
            }

        } catch ( IOException e ) {
            if( debugOnScreen ) {
                e.printStackTrace();
            }
            if( logger != null ) {
                logger.error( "URL error: " + sourceUrl, e );
                logger.error( "Please provide a valid URL" );
            }
        }

        return ( result == null ? 0 : result.size() );
    }


    /**
     * Process one to many entries and insert/update the database.
     *
     * @param inputStream where we read the entries from.
     * @param update true to allow update, otherwise false.
     * @return a collection of updated created proteins and Splice variant (Protein object).
     */
    private Collection insertSPTrProteins( InputStream inputStream, boolean update ) {

        if( inputStream == null ) {
            if( logger != null ) {
                logger.error( "You are trying to update using a null InputStream" );
            }
            return null;
        }

        /**
         * Init() has to be called in order to have the statistics properly initialized
         * as well as to keep track of all updated/created proteins.
         */
        reset();

        try {
            // parse it with YASP
            if( debugOnScreen ) {
                System.out.print( "Parsing..." );
                System.out.flush();
            }

            entryIterator = YASP.parseAll( inputStream );

            if( debugOnScreen ) {
                System.out.println( "done" );
            }

            /**
             * C A U T I O N
             * -------------
             *  The YASP Iterator has to taken with carefulness.
             * .next() method gives you the current element
             * .hasNext() loads the next elements and says you if there was one.
             * So, calling twice .hasNext() without processing in between would
             * make you miss an element.
             */
            while ( entryIterator.hasNext() ) {

                entryCount++;

                // Check if there is any exception remaining in the Entry before to use it
                if( entryIterator.hadException() ) {

                    Exception originalException = entryIterator.getException().getOriginalException();
                    parsingExceptions.put( new Integer(entryCount) , originalException );

                    if( originalException != null ) {
                        if( debugOnScreen ){
                            originalException.printStackTrace();
                            entryIterator.getException().printStackTrace();
                        }
                    } else {
                        if( logger != null ) {
                            logger.error( "Parsing error while processing the entry " + entryCount,
                                          entryIterator.getException() );
                        }
                        if( debugOnScreen ){
                            entryIterator.getException().printStackTrace();
                        }
                    }

                    // wrong entries are NOT processed any further
                    writeEntry2file();
                    entrySkipped++;
                    continue;
                }

                // get the SPTREntry
                SPTREntry sptrEntry = (SPTREntry) entryIterator.next();

                if( sptrEntry == null ) {
                    if( logger != null ) {
                        logger.error( "\n\nSPTR entry is NULL ... skip it" );
                    }

                    entrySkipped++;
                    continue;
                }

                if( debugOnScreen ) {
                    System.out.print( "(" + sptrEntry.getID() + ":" );
                }

                createProteinFromSPTrEntry( sptrEntry, update );

                if( debugOnScreen ) {
                    System.out.println( ")" );
                }

                // Display some statistics every 500 entries processed.
                if( entryCount % 500 == 0 ) {
                    printStats();
                }
            }

        } catch ( YASPException e ) {
            e.printStackTrace();
            if( logger != null ) {
                logger.error( e.getOriginalException() );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            if( logger != null ) {
                logger.error( "Error while processing an SPTREntry", e );
            }
        }

        closeFile(); // try to close the bad entries repository if it exists

        printStats();

        return proteins;
    }


    public Collection insertSPTrProteins( InputStream inputStream, String taxid, boolean update ) {

        // check the taxid parameter validity
        try {
            if( taxid != null ) {
                String newTaxid = bioSourceFactory.getUpToDateTaxid( taxid );
                if( newTaxid == null ) {
                    if( logger != null ) {
                        logger.error( "Could not find an up-to-date taxid for " + taxid + " abort update procedure." );
                    }
                    return null;
                }
            }
        } catch ( IntactException ie ) {
            String msg = "Could not find an up-to-date taxid for " + taxid + " abort update procedure.";
            if( logger != null ) {
                logger.error( msg, ie );
            }
            return null;
        }

        insertSPTrProteins( inputStream, update ); // updates the collection: proteins

        if( taxid != null ) {
            // filter out using the given taxid before to return the collection.
            Collection c = filterOnTaxid( proteins, taxid );
            if( logger != null && logger.isInfoEnabled() ) {
                logger.info( "Protein selected after filtering (" + c.size() + "):" );
                for ( Iterator iterator = c.iterator(); iterator.hasNext(); ) {
                    Protein protein = (Protein) iterator.next();
                    logger.info( "\t" + protein.getShortLabel() );
                }
            }
            return c;
        }

        return proteins;
    }

    private void printStats() {
        // in log file
        if( logger != null ) {
            logger.info( "Protein created:    " + getProteinCreatedCount() );
            logger.info( "Protein updated:    " + getProteinUpdatedCount() );
            logger.info( "Protein up-to-date: " + getProteinUpToDateCount() );
            logger.info( "Protein skipped:    " + getProteinSkippedCount() );
            logger.info( "Splice variant created:    " + getSpliceVariantCreatedCount() );
            logger.info( "Splice variant updated:    " + getSpliceVariantUpdatedCount() );
            logger.info( "Splice variant up-to-date: " + getSpliceVariantUpToDateCount() );
            logger.info( "Splice variant skipped:    " + getSpliceVariantSkippedCount() );
            logger.info( "Entry processed:    " + getEntryProcessededCount() );
            logger.info( "Entry skipped:      " + getEntrySkippedCount() );
        }

        // on STDOUT
        if( debugOnScreen ) {
            System.out.println( "Protein created:    " + getProteinCreatedCount() );
            System.out.println( "Protein updated:    " + getProteinUpdatedCount() );
            System.out.println( "Protein up-to-date: " + getProteinUpToDateCount() );
            System.out.println( "Protein skipped:    " + getProteinSkippedCount() );
            System.out.println( "Splice variant created:    " + getSpliceVariantCreatedCount() );
            System.out.println( "Splice variant updated:    " + getSpliceVariantUpdatedCount() );
            System.out.println( "Splice variant up-to-date: " + getSpliceVariantUpToDateCount() );
            System.out.println( "Splice variant skipped:    " + getSpliceVariantSkippedCount() );
            System.out.println( "Entry processed:    " + getEntryProcessededCount() );
            System.out.println( "Entry skipped:      " + getEntrySkippedCount() );
        }
    }


    /**
     * Write the content of an Entry in a file for later on processing
     */
    private void writeEntry2file() {

        if( file == null ) {
            // make a generic output byte stream
            try {
                // Get today's date and current time
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd@HH.mm" );
                String time = formatter.format( date );

                filename = ENTRY_OUTPUT_FILE + "-" + time;
                file = new FileOutputStream( filename );
                // attach BufferedOutputStream to buffer it
                buffer = new BufferedOutputStream( file, 4096 );

            } catch ( FileNotFoundException e ) {
                if( logger != null ) {
                    logger.error( "Could not write the current entry to the temp file: " + filename, e );
                }
                return;
            }
        }

        // write the entry in the file
        try {
            String entry = entryIterator.getOriginal();
            if( entry != null ) {
                buffer.write( entry.getBytes() );
                if( logger != null ) {
                    logger.error( "\nEntry written in the file" );
                }
            } else {
                if( logger != null ) {
                    logger.error( "Couldn't write the entry in the file" );
                }
            }
        } catch ( IOException e ) {
            if( logger != null ) {
                logger.error( "An error occur when trying to save an entry which cause a processing problem", e );
            }
        }
    }


    private void closeFile() {

        if( buffer != null ) {
            try {
                buffer.close();
            } catch ( IOException e ) {
                if( logger != null ) {
                    logger.error( "Error when trying to close faulty entry file", e );
                }
            }
        }

        if( file != null ) {
            try {
                file.close();
            } catch ( IOException e ) {
                if( logger != null ) {
                    logger.error( "Error when trying to close faulty entry file", e );
                }
            }
        }

    }


    public String getErrorFileName() {
        return filename;
    }


    /**
     * If true, each protein is updated in a distinct transaction.
     * If localTransactionControl is false, no local transactions are initiated,
     * control is left with the calling class.
     * This can be used e.g. to have transctions span the insertion of all
     * proteins of an entire complex.
     *
     * @return current value of localTransactionControl
     */
    public boolean isLocalTransactionControl() {
        return localTransactionControl;
    }

    /**
     * If true, each protein is updated in a distinct transaction.
     * If localTransactionControl is false, no local transactions are initiated,
     * control is left with the calling class.
     * This can be used e.g. to have transctions span the insertion of all
     * proteins of an entire complex.
     *
     * @param aLocalTransactionControl New value for localTransactionControl
     */
    public void setLocalTransactionControl( boolean aLocalTransactionControl ) {
        localTransactionControl = aLocalTransactionControl;
    }


    /**
     * D E M O
     * <p/>
     * Could be use for loading from a .txl file
     * ./scripts/javaRun.sh UpdateProteins file:///homes/user/mySPTRfile.txl
     */
    public static void main( String[] args ) throws Exception {

        IntactHelper helper = null;

        try {
            String url = null;

            if( args.length >= 1 ) {
                url = args[ 0 ];
            } else {
                System.out.println( "Usage: javaRun.sh UpdateProteins <URL>" );
                System.exit( 1 );
            }

            try {
                helper = new IntactHelper();
                System.out.println( "Helper created (User: " + helper.getDbUserName() + " " +
                                    "Database: " + helper.getDbName() + ")" );

            } catch ( IntactException e ) {
                System.out.println( "Root cause: " + e.getRootCause() );
                e.printStackTrace();
                System.exit( 1 );
            }


            UpdateProteinsI update = new UpdateProteins( helper );
            Chrono chrono = new Chrono();
            chrono.start();

            update.setDebugOnScreen( true );
            int nb = update.insertSPTrProteinsFromURL( url, null, true );

            chrono.stop();
            System.out.println( "Time elapsed: " + chrono );
            System.out.println( "Entries error in : " + update.getErrorFileName() );

            System.out.println( nb + " protein updated/created" );

        } catch ( OutOfMemoryError aome ) {

            aome.printStackTrace();

            System.err.println( "" );
            System.err.println( "UpdateProteins ran out of memory." );
            System.err.println( "Please run it again and change the JVM configuration." );
            System.err.println( "Here are some the options: http://java.sun.com/docs/hotspot/VMOptions.html" );
            System.err.println( "Hint: You can use -Xms -Xmx to specify respectively the minimum and maximum" );
            System.err.println( "      amount of memory that the JVM is allowed to allocate." );
            System.err.println( "      eg. java -Xms128m -Xmx512m <className>" );
            System.err.println( "      you can set it up in scripts/javaRun.sh" );

            System.exit( 1 );

        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        } finally {
            if( helper != null ) {
                helper.closeStore();
            }
        }

        System.exit( 0 );
    }
}