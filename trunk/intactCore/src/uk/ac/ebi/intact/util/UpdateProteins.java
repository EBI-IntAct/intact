/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.aristotle.model.sptr.AristotleSPTRException;
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
 *     (2) Link to GO, SGD, INTERPRO, FLYBASE, REACTOME.
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

    // TODO if the SRS server doesn't reply, handle it nicely.

    private static final String TIME;

    static {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd@HH_mm" );
        TIME = formatter.format( new Date() );
        formatter = null;
    }

    // Store that error in the temp directory (OS independant)
    private static final String ENTRY_OUTPUT_FILE = System.getProperty( "java.io.tmpdir" ) + "Entries.error";

    // to record entry error
    private String entryErrorFilename = null;
    private FileOutputStream file = null;
    private BufferedOutputStream buffer = null;

    // to record local entry cache
    private String localEntryCacheFilename = System.getProperty( "java.io.tmpdir" ) + "localEntryCache." + TIME + ".txl";
    private boolean displayLocalEntryCacheMessage = true;

    private boolean localEntryCacheEnabled = false;
    private BufferedWriter localEntryCache = null;

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

    public static final int PRIMARY_AC = 0;
    public static final int SECONDARY_AC = 1;
    public static final int ALL_AC = 2;

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
    private Collection getProteinsFromSPTrAC( SPTREntry sptrEntry,
                                              CvXrefQualifier qualifier,
                                              String taxid,
                                              int acType,
                                              IntactHelper helper )
            throws SPTRException {

        if( PRIMARY_AC != acType &&
            SECONDARY_AC != acType &&
            ALL_AC != acType ) {
            throw new IllegalArgumentException( "the acType has to be either: PRIMARY_AC or SECONDARY_AC or ALL_AC" );
        }

        String spAC[] = sptrEntry.getAccessionNumbers();

        logger.info( spAC.length + " AC in the SPTR entry." );

        int start, stop = 0;
        if( PRIMARY_AC == acType ) {
            // only the first one
            start = 0;
            stop = 1;
        } else if( SECONDARY_AC == acType ) {
            // all but the first one
            start = 1;
            stop = spAC.length;
        } else {
            start = 0;
            stop = spAC.length;
        }

        logger.info( "We use: " + start + ".." + stop );

        Collection proteins = new HashSet();
        int i = 0;
        try {
            Collection tmp = null;
            for ( i = start; i < stop; i++ ) {
                String ac = spAC[ i ];
                tmp = helper.getObjectsByXref( Protein.class, uniprotDatabase, qualifier, ac );

                if( logger != null ) {
                    logger.info( "look for " + ac );
                    logger.info( tmp.size() + " proteins found" );
                }

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

        if( logger != null ) {
            logger.info( proteins.size() + " proteins selected" );
        }

        return proteins;
    }


    /**
     * Check if the given protein has been demerged.
     * <br>
     * <p/>
     * Algorithm sketch:
     * <pre>
     * (1) gets its uniprot id as Xref( uniprot, identity )
     * (2) query SRS and retreive a set of SPTR entries
     * (3) count the occurence of that ID being a secondary AC
     *     if( > 1 ) : return true
     *          else : return false.
     * </pre>
     *
     * @param protein the protein for which we want to know if it has been demerged
     * @return true if the protein has been demerged, otherwise false.
     * @throws IntactException if any exception is raised during the process.
     */
    private boolean isDemerged( Protein protein ) throws IntactException {

        // TODO should throw Exception in order to stop the update of that entry.

        Exception exceptionRaised = null;

        // get the Xref( uniprot, identity ) of that protein
        Collection xrefs = protein.getXrefs();
        String ac = null;
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext() && null == ac; ) {
            Xref xref = (Xref) iterator.next();

            if( identityXrefQualifier.equals( xref.getCvXrefQualifier() ) &&
                uniprotDatabase.equals( xref.getCvDatabase() ) ) {

                // found it
                ac = xref.getPrimaryId();
            }
        } // xrefs

        // count of reference to that ac in the secondary ACs of the retreived Entry from SRS.
        int countSecondaryAC = 0;

        if( null == ac ) {
            // no Xref( uniprot, identity ) found
            throw new IntactException( "No Xref( uniprot, identity ) found for the protein( " + protein.getAc() + " )" );

        } else {
            // query SRS
            String sourceUrl = getUrl( ac );

//            if( debugOnScreen ) {
//                System.out.println( "Parsing: " + sourceUrl );
//            }

            EntryIterator localEntryIterator = null;
            try {
                URL url = new URL( sourceUrl );
                InputStream is = url.openStream();
                localEntryIterator = YASP.parseAll( is );
            } catch ( IOException e ) {
                exceptionRaised = e;
            } catch ( YASPException e ) {
                exceptionRaised = e;
            }

            if( debugOnScreen ) {
                System.out.println( "done" );
            }

            while ( localEntryIterator.hasNext() ) {

                // Check if there is any exception remaining in the Entry before to use it
                if( localEntryIterator.hadException() ) {

                    Exception originalException = localEntryIterator.getException().getOriginalException();
                    parsingExceptions.put( new Integer( entryCount ), originalException );

                    if( originalException != null ) {
                        if( debugOnScreen ) {
                            originalException.printStackTrace();
                            localEntryIterator.getException().printStackTrace();
                        }
                    } else {
                        if( logger != null ) {
                            logger.error( "Parsing error while processing the entry " + entryCount,
                                          localEntryIterator.getException() );
                        }
                        if( debugOnScreen ) {
                            localEntryIterator.getException().printStackTrace();
                        }
                    }

                    // wrong entries are NOT processed any further
                    writeEntry2file();
                    continue;
                }

                // get the SPTREntry
                SPTREntry sptrEntry = (SPTREntry) localEntryIterator.next();
                String spAC[] = null;
                try {
                    spAC = sptrEntry.getAccessionNumbers();
                } catch ( SPTRException e ) {
                    exceptionRaised = e;
                }

                // Count the reference of 'ac' in the secondary ACs.
                for ( int i = 1; i < spAC.length; i++ ) {
                    String secondaryAC = spAC[ i ];

                    if( secondaryAC.equals( ac ) ) {
                        countSecondaryAC++;
                    }
                } // for
            }
        }

        boolean isDemerged = false;

        if( countSecondaryAC > 1 ) {
            isDemerged = true;
        } else if( exceptionRaised != null ) {
            // not sure that the protein was not demerged, then throw the exception
            throw new IntactException( "An Exception has been raise while checking if the protein( " + protein.getAc() +
                                       " ) was demerged", exceptionRaised );
        }

        return isDemerged;
    }


    /**
     * Get existing splice variant generated from that SPTREntry.
     * <br>
     * <pre>
     * Algorithm sketch:
     *          1 for each Protein p in PROTEINS
     *            1.1 get it's IntAct AC
     *            1.2 search for all Xrefs which have are qualified by isoform-parent with that AC as a primary key.
     *            1.3 Get the Proteins (should be one since Xref are not shared) who own that particular Xref.
     * </pre>
     *
     * @param masters The master protein of the splice variant
     * @param helper  The database access
     * @return the created splice variants
     */
    private Collection getSpliceVariantFromSPTrAC( Collection masters,
                                                   IntactHelper helper ) {

        if( masters == null ) {
            throw new IllegalArgumentException( "You must give a non null master Collection." );
        }

        Collection spliceVariants = new HashSet(); // we want a distinct set of Protein

        // need that reference to ac out of the loop in order to have it available if an exception is raised.
        String ac = null;
        try {
            for ( Iterator iterator = masters.iterator(); iterator.hasNext(); ) {
                Protein protein = (Protein) iterator.next();

                ac = protein.getAc();
                if( logger != null ) {
                    logger.info( "Look for splice variant for the master: " + protein.getShortLabel() + "(" + ac + ")" );
                }

                // All splice proteins have 'this' protein as the primary id.
                Collection proteins = helper.getObjectsByXref( Protein.class, intactDatabase, isoFormParentXrefQualifier, ac );
                if( proteins != null || !proteins.isEmpty() ) {
                    if( logger != null ) {
                        logger.info( proteins.size() + " splice variant(s) found." );
                    }
                    spliceVariants.addAll( proteins );
                } else {
                    if( logger != null ) {
                        logger.info( "no splice variant found." );
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

        logger.info( spliceVariants.size() + " splice variant(s) selected." );
        return spliceVariants;
    }


    private Collection getTaxids( final SPTREntry sptrEntry ) throws SPTRException {

        int organismCount = sptrEntry.getOrganismNames().length;
        ArrayList taxids = new ArrayList( organismCount );

        for ( int i = 0; i < organismCount; i++ ) {
            String organism = sptrEntry.getOrganismNames()[ i ];
            String entryTaxid = sptrEntry.getNCBITaxonomyID( organism );
            taxids.add( entryTaxid );
        }
        return taxids;
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

        Protein selectedProtein = null;

        try {
            // according to the SPTR entry, get the corresponding proteins in IntAct
            // TODO: we don't activate the taxid filter here
            Collection proteins = getProteinsFromSPTrAC( sptrEntry, identityXrefQualifier, null, PRIMARY_AC, helper );
            if( proteins == null ) {
                if( logger != null ) {
                    logger.error( "An error occured when trying to get IntAct protein, exit update" );
                }
                writeEntry2file();
                return;
            }

            // according to the SPTR entry, get the corresponding splice variant in IntAct
            // TODO: we don't activate the taxid filter here
            Collection spliceVariants = getSpliceVariantFromSPTrAC( proteins, helper );
            if( spliceVariants == null ) {
                if( logger != null ) {
                    logger.error( "An error occured when trying to get IntAct splice variants, exit update" );
                }
                writeEntry2file();
                return;
            }

            /**
             * Select which taxid to consider in the process.
             */
            Collection taxids = getTaxids( sptrEntry );
            Collection masters = new HashSet(); // to store the proteins which might be a master of a splice variant.

            /**
             * Process all collected BioSource
             */
            boolean generateProteinShortlabelUsingBiosource = false;
            if( taxids.size() > 1 ) {
                generateProteinShortlabelUsingBiosource = true;
            }

            for ( Iterator iteratorTaxid = taxids.iterator(); iteratorTaxid.hasNext(); ) {
                String sptrTaxid = (String) iteratorTaxid.next();

                proteinTotal++;

                if( logger != null ) {
                    logger.info( "Prossessing: sptrTaxid=" + sptrTaxid );
                }

                // get a valid Biosource from either Intact or Newt
                BioSource sptrBioSource = bioSourceFactory.getValidBioSource( sptrTaxid );

                if( logger != null ) {
                    logger.info( "selected biosource: " + sptrBioSource.getTaxId() );
                }

                selectedProtein = null;
                // look for a protein in the set (retreived from primary AC) which has that taxid
                for ( Iterator iterator = proteins.iterator(); iterator.hasNext() && selectedProtein == null; ) {
                    Protein p = (Protein) iterator.next();
                    BioSource bs = p.getBioSource();

                    if( bs.equals( sptrBioSource ) ) {
                        // found it.
                        selectedProtein = p;
                    }
                }

                if( logger != null ) {
                    if( selectedProtein != null ) {
                        logger.info( "selected protein: " + selectedProtein );
                    } else {
                        logger.info( "No protein selected." );
                    }
                }

                // remove it from the collection
                proteins.remove( selectedProtein );

                // allow to know while we are processing the splice variant if the master was demerged.
                boolean masterWasDemerged = false;

                if( selectedProtein == null ) {

                    /**
                     * We could NOT find an existing protein so now two cases have to be taken into account:
                     * if an old protein already exists (ie. created before demerge) it would not have been picked up
                     * searching by Xref( uniprot, identity ) for the primary AC so now we search by secondary AC of
                     * the SPTR Entry.
                     *     - If we find one
                     *         - Take that protein Xref( uniprot, identity ) and query SRS
                     *             - if MORE THAN ONE Entry have that AC as secondary, it means that this is a demerge.
                     *                 => we update that protein to be the demerged one
                     *             - if not, (ie. no demerge) create a new protein.
                     *
                     *       (!) Related splice variant (if any) have to be updated too.
                     *
                     *     - If we didn't find it, we create a new protein.
                     */


                    if( logger != null ) {
                        logger.info( "No existing protein for that taxid (" + sptrTaxid + "), create a new one" );
                    }

                    // (1) Search for IntAct proteins by Xref( uniprot, identity ) based on the secondary Ac of the entry
                    Collection secondaryProteins = getProteinsFromSPTrAC( sptrEntry, identityXrefQualifier, null,
                                                                          SECONDARY_AC, helper );

                    boolean doUpdate = false;
                    boolean doCreate = false;

                    if( secondaryProteins.isEmpty() ) {
                        // no protein found, then create it
                        doCreate = true;

                    } else {

                        // look for the splice variant related to those non demerged proteins
                        spliceVariants = getSpliceVariantFromSPTrAC( secondaryProteins, helper );

                        // found proteins
                        boolean isDemerged = false;
                        for ( Iterator iterator = secondaryProteins.iterator(); iterator.hasNext() && !isDemerged; ) {
                            Protein secondaryProtein = (Protein) iterator.next();

                            isDemerged = isDemerged( secondaryProtein ); // leave the procedure if an IntactException is thrown

                            // select the protein to be updated.
                            selectedProtein = secondaryProtein;
                        }

                        if( isDemerged ) {
                            // update the protein
                            doUpdate = true;
                            masterWasDemerged = true;
                        } else {
                            // create the protein
                            doCreate = true;
                        }
                    }

                    if( localTransactionControl ) {
                        helper.startTransaction( BusinessConstants.OBJECT_TX );
                    }

                    if( doCreate ) {
                        if( logger != null ) {
                            logger.info( "Call createProtein with parameter BioSource.taxId=" + sptrBioSource.getTaxId() );
                        }

                        if( ( selectedProtein = createNewProtein( sptrEntry, sptrBioSource, generateProteinShortlabelUsingBiosource ) ) != null ) {
                            if( logger != null ) {
                                logger.info( "creation sucessfully done: " + selectedProtein.getShortLabel() );
                            }

                            // Keep that reference as existing protein for that Entry and taxid.
                            masters.add( selectedProtein );
                        }
                    }

                    if( doUpdate ) {
                        if( updateExistingProtein( selectedProtein, sptrEntry, sptrBioSource, generateProteinShortlabelUsingBiosource ) ) {
                            if( logger != null ) {
                                logger.info( "update sucessfully done" );
                            }
                        }
                        masters.add( selectedProtein );
                    }

                    if( localTransactionControl ) {
                        helper.finishTransaction();
                    }
                    if( logger != null ) {
                        logger.info( "Transaction complete" );
                    }

                } else {

                    // Keep that reference as existing protein for that Entry and taxid.
                    masters.add( selectedProtein );

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
                        if( updateExistingProtein( selectedProtein, sptrEntry, sptrBioSource, generateProteinShortlabelUsingBiosource ) ) {
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
                        this.proteins.add( selectedProtein );
                    }
                }


                ///////////////////////////////////////
                // Management of the splice variant

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

                        /**
                         * browse isoform's IDs which, in case they have been store in the database,
                         * are used as shortlabel of the related Protein.
                         */
                        String[] ids = isoForm.getIDs();

                        if( ids.length > 0 ) {

                            // only the first ID should be taken into account, the following ones are secondary IDs.
                            String spliceVariantID = ids[ 0 ];

                            if( logger != null ) {
                                logger.info( "Splice variant ID: " + spliceVariantID );
                            }

                            // Search for an existing splice variant in the IntAct database
                            Protein spliceVariant = null;
                            boolean spliceVariantFound = false;
                            for ( Iterator iterator = spliceVariants.iterator(); iterator.hasNext() && !spliceVariantFound; ) {
                                Protein sv = (Protein) iterator.next();

                                /* How to spot and select the right splice variant ?
                                 * -----------------------------------------------
                                 * => Just filter using the bioSource + the Xref( uniprot, identity ).
                                 */

                                if( sv.getBioSource().equals( sptrBioSource ) ) {

                                    // check for Xref( uniprot, identity )
                                    for ( Iterator iterator1 = sv.getXrefs().iterator(); iterator1.hasNext() && !spliceVariantFound; ) {
                                        Xref xref = (Xref) iterator1.next();
                                        if( identityXrefQualifier.equals( xref.getCvXrefQualifier() )
                                            &&
                                            uniprotDatabase.equals( xref.getCvDatabase() ) ) {

                                            // check if the primary id of that Xref is one the the splice variant ID
                                            int idx = 0;
                                            for ( idx = 0; idx < ids.length && !spliceVariantFound; idx++ ) {
                                                if( xref.getPrimaryId().equals( ids[ idx ] ) ) {
                                                    // found it.
                                                    spliceVariant = sv;
                                                    spliceVariantFound = true;

                                                    if( logger != null ) {
                                                        logger.info( "Splice variant found using " +
                                                                     ( idx == 0 ? "primary" : "secondary" ) +
                                                                     " ID: " + ids[ idx ] );
                                                    }
                                                }
                                            }
                                        }
                                    } // for xrefs
                                }
                            } // for spliceVariants


                            // get the master protein from the collected data
                            Protein master = null;
                            for ( Iterator iterator = masters.iterator(); iterator.hasNext(); ) {
                                master = (Protein) iterator.next();
                                if( master.getBioSource().equals( sptrBioSource ) ) {
                                    if( logger != null ) {
                                        logger.info( "Found(" + master.getShortLabel() + ")" );
                                    }
                                    break; // found it
                                }
                            }
                            if( master == null ) {
                                // Shouldn't happen, we have found the splice variant from the masters.
                                if( debugOnScreen ) {
                                    System.out.println( "Didn't found a master protein for " + spliceVariantID );
                                }
                                if( logger != null ) {
                                    logger.error( "Didn't found a master protein for " + spliceVariantID );
                                }
                            }

                            if( spliceVariantFound ) {
                                // ... update it.

                                if( update || masterWasDemerged ) {

                                    // We only perform an update of the existing splice variant if the user request it
                                    // or if the master protein was a demerged protein.

                                    /**
                                     * We are doing the update of the existing protein only if the
                                     * user request it we only update its content if needed
                                     */
                                    if( logger != null ) {
                                        logger.info( "A splice variant exists for that taxid (" + sptrTaxid + "), try to update" );
                                    }

                                    if( localTransactionControl ) {
                                        /**
                                         * We want here to use a database transaction (NOT OBJECT) because
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
                                    if( updateExistingSpliceVariant( isoForm, spliceVariantID, spliceVariant, master, sptrEntry,
                                                                     sptrBioSource, generateProteinShortlabelUsingBiosource ) ) {
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

                                // could not find the splice variant

                                /**
                                 * We didn't find a Protein related to that isoform ... create it.
                                 * doesn't found so create a new one
                                 */
                                if( logger != null ) {
                                    logger.info( "No existing splice variant for that taxid (" + sptrTaxid + "), create a new one" );
                                }

                                if( localTransactionControl ) {
                                    /**
                                     * See remarks about database transaction above.
                                     */
                                    helper.startTransaction( BusinessConstants.JDBC_TX );
                                }
                                if( ( spliceVariant = createNewSpliceVariant( isoForm, spliceVariantID, master, sptrEntry, sptrBioSource,
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
                        } // if (ids.length > 0)
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
    }


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
        // YES ! we should search dor it first and reuse existing Annotation

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
            String id = null;
            try {
                id = sptrXref.getPropertyValue( SPTRCrossReference.PROPERTY_DESCRIPTION );
            } catch ( AristotleSPTRException e ) {
                // there was no description, we don't fail for that.
                if( logger != null ) {
                    logger.warn( "Entry(sptrId=" + sptrEntry.getAccessionNumbers()[ 0 ] + "), Xref(id=" + ac +
                                 ") has no description and a AristotleSPTRException was thrown", e );
                }
            }

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

    /**
     * Those Xref are not stored explicitly in the entry but are present as gene name.
     * Hence we are looking for geneNames starting with KIAA and create Xrefs out of them.
     *
     * @param sptrEntry the entry from which we'll read the gene name.
     * @param protein   the protein for which we want to update the xrefs
     * @return true if anything has been updated, otherwise false.
     */
    private boolean updateHugeXref( final SPTREntry sptrEntry,
                                    Protein protein ) throws SPTRException {

        boolean needUpdate = false;

        Gene[] genes = sptrEntry.getGenes();
        Collection kiaas = new ArrayList( 1 );
        for ( int i = 0; i < genes.length; i++ ) {

            String geneName = genes[ i ].getName();
            if( geneName.startsWith( "KIAA" ) ) {
                kiaas.add( geneName );
            }
        } // genes

        // convert kiaas to Xrefs
        for ( Iterator iterator = kiaas.iterator(); iterator.hasNext(); ) {
            String kiaa = (String) iterator.next();

            Xref xref = new Xref( myInstitution, hugeDatabase, kiaa, null, null, null );

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
        *       Currently, all name of alias are stored in lowercase in the db.
        */
        String lowerCaseName = name.toLowerCase();

        for ( Iterator iterator = aliases.iterator(); iterator.hasNext(); ) {
            Alias alias = (Alias) iterator.next();

            if( lowerCaseName.equals( alias.getName() ) && alias.getCvAliasType().equals( aliasType ) ) {
                return true;
            }
        }
        return false;
    }


    /**
     * create/update Aliases for a protein with gene-name, gene-name synonyms, ORF name.
     *
     * @param protein The protein to update
     * @return true if the protein has been updated, else false.
     * @throws SPTRException
     */
    private boolean updateAliases( final SPTREntry sptrEntry,
                                   Protein protein ) throws SPTRException {

        // TODO remove 'deprecated' aliases.

        boolean needUpdate = false;
        Collection aliases = protein.getAliases();
        Gene[] genes = sptrEntry.getGenes();
        Alias alias = null;

        for ( int i = 0; i < genes.length; i++ ) {

            String geneName = genes[ i ].getName();

            if( geneName != null && ( false == "".equals( geneName.trim() ) ) ) {

                if( !isAliasAlreadyExisting( aliases, geneName, geneNameAliasType ) ) {
                    alias = new Alias( myInstitution,
                                       protein,
                                       geneNameAliasType, // gene-name
                                       geneName );

                    // link the Alias to the protein and persist it in the database
                    addNewAlias( protein, alias );
                    if( logger != null ) {
                        logger.info( "ADD new Alias[name: " + alias.getName() +
                                     " type: " + geneNameAliasType.getShortLabel() + "]" +
                                     ", to: " + protein.getShortLabel() );
                    }
                    needUpdate = true;
                } else {
                    if( logger != null ) {
                        logger.info( "SKIP Alias[name: " + geneName +
                                     " type: " + geneNameAliasType.getShortLabel() + "]" +
                                     ", for: " + protein.getShortLabel() );
                    }
                } // Gene names
            } // if

            // create/update synonyms
            String[] synonyms = genes[ i ].getSynonyms();
            for ( int ii = 0; ii < synonyms.length; ii++ ) {

                String syn = synonyms[ ii ];

                if( syn != null && ( false == "".equals( syn.trim() ) ) ) {

                    if( !isAliasAlreadyExisting( aliases, syn, geneNameSynonymAliasType ) ) {
                        alias = new Alias( myInstitution,
                                           protein,
                                           geneNameSynonymAliasType, // gene-name-synonym
                                           syn );

                        // link the Alias to the protein and persist it in the database
                        addNewAlias( protein, alias );
                        if( logger != null ) {
                            logger.info( "ADD new Alias[name: " + alias.getName() +
                                         " type: " + geneNameSynonymAliasType.getShortLabel() + "]" +
                                         ", to: " + protein.getShortLabel() );
                        }
                        needUpdate = true;
                    } else {
                        if( logger != null ) {
                            logger.info( "SKIP Alias[name: " + syn +
                                         " type: " + geneNameSynonymAliasType.getShortLabel() + "]" +
                                         ", for: " + protein.getShortLabel() );
                        }
                    }
                }
            } // Gene name synonyms


            // create/update locus names
            String[] locus = genes[ i ].getLocusNames();
            for ( int ii = 0; ii < locus.length; ii++ ) {

                String locusName = locus[ ii ];

                if( locusName != null && ( false == "".equals( locusName.trim() ) ) ) {

                    if( !isAliasAlreadyExisting( aliases, locusName, locusNameAliasType ) ) {
                        alias = new Alias( myInstitution,
                                           protein,
                                           locusNameAliasType, // locus-name
                                           locusName );

                        // link the Alias to the protein and persist it in the database
                        addNewAlias( protein, alias );
                        if( logger != null ) {
                            logger.info( "ADD new Alias[name: " + alias.getName() +
                                         " type: " + locusNameAliasType.getShortLabel() + "]" +
                                         ", to: " + protein.getShortLabel() );
                        }
                        needUpdate = true;
                    } else {
                        if( logger != null ) {
                            logger.info( "SKIP Alias[name: " + locusName +
                                         " type: " + locusNameAliasType.getShortLabel() + "]" +
                                         ", for: " + protein.getShortLabel() );
                        }
                    }
                }
            } // Locus names


            // create/update ORF names
            String[] ORFs = genes[ i ].getORFNames();
            for ( int ii = 0; ii < ORFs.length; ii++ ) {

                String orfName = ORFs[ ii ];

                if( orfName != null && ( false == "".equals( orfName.trim() ) ) ) {

                    if( !isAliasAlreadyExisting( aliases, orfName, orfNameAliasType ) ) {
                        alias = new Alias( myInstitution,
                                           protein,
                                           orfNameAliasType, // orf-name
                                           orfName );

                        // link the Alias to the protein and persist it in the database
                        addNewAlias( protein, alias );
                        if( logger != null ) {
                            logger.info( "ADD new Alias[name: " + alias.getName() +
                                         " type: " + orfNameAliasType.getShortLabel() + "]" +
                                         ", to: " + protein.getShortLabel() );
                        }
                        needUpdate = true;
                    } else {
                        if( logger != null ) {
                            logger.info( "SKIP Alias[name: " + orfName +
                                         " type: " + orfNameAliasType.getShortLabel() + "]" +
                                         ", for: " + protein.getShortLabel() );
                        }
                    }
                }
            } // ORFs
        } // Genes

        return needUpdate;
    } // updateAliases


    private String generateProteinShortLabel( SPTREntry sptrEntry,
                                              BioSource bioSource,
                                              boolean generateProteinShortlabelUsingBiosource ) throws SPTRException {
        String shortlabel = null;

        if( generateProteinShortlabelUsingBiosource ) {

            Gene[] genes = sptrEntry.getGenes();

            if( genes.length > 0 && genes.length > 0 ) // if there is at least one gene
            {
                shortlabel = genes[ 0 ].getName();
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
        if( fullName.length() > 250 ) {
            fullName = fullName.substring( 0, 250 );
        }

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
        needUpdate = needUpdate | updateXref( sptrEntry, protein, Factory.XREF_REACTOME, reactomeDatabase );
        needUpdate = needUpdate | updateHugeXref( sptrEntry, protein );

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
                if( logger != null ) {
                    logger.error( protein, ie );
                }
                throw ie;
            }
        } else {
            if( logger != null ) {
                logger.info( "That protein was up-to-date" );
            }
            if( debugOnScreen ) {
                System.out.print( " p-" );
            }
            proteinUpToDate++;
        }

        return false;
    }

    private Collection getUniprotIdentityXrefs( Protein protein ) {

        Collection xrefs = new ArrayList( 1 ); // fits the size in most cases
        for ( Iterator iterator = protein.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();
            if( identityXrefQualifier.equals( xref.getCvXrefQualifier() )
                &&
                uniprotDatabase.equals( xref.getCvDatabase() ) ) {

                xrefs.add( xref );
            }
        }

        return xrefs;
    }

    /**
     * Update (create them if not exist) SPTR Cross references to the given protein
     * It also deletes all irrelevant Xref( uniprot, identity )
     *
     * @param sptrEntry the entry in which we'll find the primary ID of the Xrefs
     * @param protein   the protein to update
     * @return true if at least one Xref as been added, else false.
     * @throws SPTRException
     */
    private boolean updateUniprotXref4Protein( SPTREntry sptrEntry, Protein protein ) throws SPTRException, IntactException {

        boolean updated = false;
        String proteinAC[] = sptrEntry.getAccessionNumbers();
        String shortLabel = protein.getShortLabel();

        // Collect all existing Xref( uniprot, identity )
        Collection identities = getUniprotIdentityXrefs( protein );

        String ac = proteinAC[ 0 ];

        Xref sptrXref = new Xref( myInstitution,
                                  uniprotDatabase,
                                  ac,
                                  shortLabel,
                                  null,
                                  identityXrefQualifier );

        if( identities.contains( sptrXref ) ) {
            // keep only the identity that are not the new one
            identities.remove( sptrXref );
        } else {
            updated = updated | addNewXref( protein, sptrXref );
        }

        // remove all obsolete identity Xref.
        for ( Iterator iterator = identities.iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();
            if( logger != null ) {
                logger.info( "Delete Xref of protein( " + protein.getAc() + " ): " + xref );
            }

            helper.delete( xref );
            updated = true;
        }


        for ( int i = 1; i < proteinAC.length; i++ ) {
            ac = proteinAC[ i ];

            sptrXref = new Xref( myInstitution,
                                 uniprotDatabase,
                                 ac,
                                 shortLabel,
                                 null,
                                 secondaryXrefQualifier );

            updated = updated | addNewXref( protein, sptrXref );
        }

        return updated;
    }

    /**
     * Update (create them if not exist) UNIPROT Cross reference to the given splice variant.
     * It also deletes all irrelevant Xref( uniprot, identity )
     *
     * @param sptrEntry     the entry in which we'll find the primary ID of the Xrefs
     * @param spliceVariant the splice variant to update
     * @return true if at least one Xref as been added, else false.
     * @throws SPTRException
     */
    private boolean updateUniprotXref4SpliceVariant( SPTREntry sptrEntry,
                                                     Protein spliceVariant,
                                                     Isoform isoform ) throws SPTRException, IntactException {
        boolean updated = false;
        String masterAc = sptrEntry.getID();
        String[] ids = isoform.getIDs();

        // Collect all existing Xref( uniprot, identity )
        Collection identities = getUniprotIdentityXrefs( spliceVariant );

        for ( int i = 0; i < ids.length; i++ ) {
            String isoId = ids[ i ];

            // The first AC is primary, all others are secondary
            CvXrefQualifier xrefQualifier = null;
            if( 0 == i ) {
                xrefQualifier = identityXrefQualifier;
            } else {
                xrefQualifier = secondaryXrefQualifier;
            }

            Xref sptrXref = new Xref( myInstitution,
                                      uniprotDatabase,
                                      isoId,
                                      masterAc,
                                      null,
                                      xrefQualifier );

            // keep only the identity that are not the new one ()
            identities.remove( sptrXref );

            if( identities.size() > 0 ) {
                for ( Iterator iterator = identities.iterator(); iterator.hasNext(); ) {
                    Xref xref = (Xref) iterator.next();
                    if( logger != null ) {
                        logger.info( "Delete Xref of protein( " + spliceVariant.getAc() + " ): " + xref );
                    }

                    helper.delete( xref );
                }
            }

            updated = updated | addNewXref( spliceVariant, sptrXref );
        }

        return updated;
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
        if( fullName.length() > 250 ) {
            fullName = fullName.substring( 0, 250 );
        }

        String sequence = sptrEntry.getSequence();
        String crc64 = sptrEntry.getCRC64();

        protein.setFullName( fullName );
        protein.setSequence( helper, sequence );
        protein.setCrc64( crc64 );

        updateXref( sptrEntry, protein, Factory.XREF_SGD, sgdDatabase );
        updateXref( sptrEntry, protein, Factory.XREF_GO, goDatabase );
        updateXref( sptrEntry, protein, Factory.XREF_INTERPRO, interproDatabase );
        updateXref( sptrEntry, protein, Factory.XREF_FLYBASE, flybaseDatabase );
        updateXref( sptrEntry, protein, Factory.XREF_REACTOME, reactomeDatabase );
        updateHugeXref( sptrEntry, protein );

        updateUniprotXref4Protein( sptrEntry, protein );

        // create Aliases
        updateAliases( sptrEntry, protein );

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

    }


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
        boolean found = false;
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext() && !found; ) {
            isoformXref = (Xref) iterator.next();
            if( isoformXref.getCvXrefQualifier().equals( isoFormParentXrefQualifier ) ) {
                found = true; // exit, isoformXref is still refering to it.
            }
        }
        if( !found ) {
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
        needUpdate = needUpdate | updateUniprotXref4SpliceVariant( sptrEntry, spliceVariant, isoform );

        // check for aliases ... that we could update as Alias.
        Collection aliases = spliceVariant.getAliases();
        String[] isoSynonyms = isoform.getSynonyms();
        for ( int i = 0; i < isoSynonyms.length; i++ ) {
            String isoSynonym = isoSynonyms[ i ];

            found = false;
            for ( Iterator iterator = aliases.iterator(); iterator.hasNext() && !found; ) {
                Alias alias = (Alias) iterator.next();
                if( isoformSynonym.equals( alias.getCvAliasType() ) && alias.getName().equalsIgnoreCase( isoSynonym ) ) {
                    // stored lowercase in the database !
                    found = true; // exit the inner loop.
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
                if( isoformComment.equals( annotation.getCvTopic() ) ) {
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
    }


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
                                            String isoId, // TODO get rid of that !
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
        updateUniprotXref4SpliceVariant( sptrEntry, spliceVariant, isoform );
//        updateUniprotXref4SpliceVariant( sptrEntry, spliceVariant, isoId );

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
    }


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

            if( protein.getBioSource().getTaxId().equals( taxid ) ) { // NullPointerException
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
     * @param update      true to allow update, otherwise false.
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
         * Has to be called in order to have the statistics properly initialized
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

            if( localEntryCacheEnabled ) {
                try {
                    if( localEntryCache == null ) {
                        localEntryCache = new BufferedWriter( new FileWriter( localEntryCacheFilename, true ) );
                        if( displayLocalEntryCacheMessage ) {
                            System.out.println( "Local Entry cache created: " + localEntryCacheFilename );
                            displayLocalEntryCacheMessage = false; // display it only once
                        }
                    }
                } catch ( IOException e ) {
                    System.err.println( "Coud not create the local entry cache file (" + entryErrorFilename + ")." );
                    e.printStackTrace();
                }
            }

            /**
             * C A U T I O N
             * -------------
             *  The YASP Iterator has to be used carrefully. It doesn't behave like an java.util.Iterator.
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
                    parsingExceptions.put( new Integer( entryCount ), originalException );

                    if( originalException != null ) {
                        if( debugOnScreen ) {
                            originalException.printStackTrace();
                            entryIterator.getException().printStackTrace();
                        }
                    } else {
                        if( logger != null ) {
                            logger.error( "Parsing error while processing the entry " + entryCount,
                                          entryIterator.getException() );
                        }
                        if( debugOnScreen ) {
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

                // give the user the option to cache all entries in a text file.
                if( localEntryCacheEnabled ) {
                    try {
                        if( localEntryCache != null ) {
                            final String entry = entryIterator.getOriginal();
                            localEntryCache.write( entry );
                        }
                    } catch ( IOException e ) {
                        System.err.println( "Could not cache localy the current Entry." );
                        e.printStackTrace();
                    }
                }

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

        if( localEntryCacheEnabled ) {
            try {
                if( localEntryCache != null ) {
                    localEntryCache.close();
                    localEntryCache = null;
                }
            } catch ( IOException e ) {
                System.err.println( "Could not close the local cache Entry file." );
                e.printStackTrace();
            }
        }

        closeFile(); // try to close the bad entries repository if it exists

        printStats();

        return proteins;
    }


    /**
     * Create all the proteins related to that set of entry.
     * It apply the taxif filter and return the remaining proteins.
     *
     * @param inputStream the set of entries.
     * @param taxid       the taxid of the protein we will return
     * @param update      true if we update existing proteins
     * @return a set of proteins and splice variants (as Protein object)
     */
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
                entryErrorFilename = ENTRY_OUTPUT_FILE + "-" + TIME;
                file = new FileOutputStream( entryErrorFilename );
                // attach BufferedOutputStream to buffer it
                buffer = new BufferedOutputStream( file, 4096 );

            } catch ( FileNotFoundException e ) {
                if( logger != null ) {
                    logger.error( "Could not write the current entry to the temp file: " + entryErrorFilename, e );
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
        return entryErrorFilename;
    }

    public void enableLocalEntryCache() {
        localEntryCacheEnabled = true;
    }

    public void disableLocalEntryCache() {
        localEntryCacheEnabled = false;
    }

    public boolean isLocalEntryCacheEnabled() {
        return localEntryCacheEnabled;
    }

    public String getLocalEntryCacheFileName() {
        return localEntryCacheFilename;
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