/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util ;

import gnu.regexp.RE;
import gnu.regexp.REMatch;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DAOFactory;

import uk.ac.ebi.interfaces.Factory;
import uk.ac.ebi.interfaces.sptr.SPTRCrossReference;
import uk.ac.ebi.interfaces.sptr.SPTREntry;
import uk.ac.ebi.interfaces.sptr.SPTRException;

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
 * <p>
 * Here is the detail implemented algorithm <br>
 * <pre>
 *
 * (1) From the URL given by the user, get an <i>EntryIterator</i> to process them one by one.
 *
 * (2) for each <i>SPTREntry</i>
 *
 *    (2.1) From the Accession number, retreive from IntAct all <i>Protein</i> with that AC as
 *          a SPTR <i>Xref</i>. We can find several instance of Protein in case they are link
 *          to different <i>BioSource</i>. Lets call that set of Protein: PROTEINS.
 *          Note: an SPTREntry can contains several AC so we check in IntAct for all of them.
 *
 *    (2.2) The user can give a taxid 'filter' (lets call it t) in order to update/create only
 *          protein related to that taxid. In an SPTREntry, there is 1..n specified organism
 *          (i.e. taxid). So if the taxid parameter t is null, we keep all taxid found in the
 *          SPTREntry, else we will work only with t if it is present in the SPTREntry.
 *          If it is not found, the procedure fails.
 *
 *    (2.3) For each taxid (lets call it TAXID)
 *
 *       (2.3.1) Get up-to-date informations about the organism from Newt.
 *               If that organism is already existing inIntAct as a BioSource, we check if an
 *               update is needed.
 *               We take also into account that a taxid can be obsolete and in such a case we
 *               update IntAct data.
 *
 *       (2.3.2)
 *                 a) If a Protein from PROTEINS (cf. 2.1) has TAXID as BioSource,
 *                    we update its data from the SPTREntry.
 *
 *                 b) If no Protein from PROTEINS has TAXID as BioSource, we create a new Protein.
 *
 *                 c) If a Protein from PROTEINS has a taxid not found in the SPTREntry, we display
 *                    a warning message.
 *
 *   The update and creation process (2.3.2 a and b) includes a check of the following Xref :
 *   SPTR, GO, SGD.
 *
 *   CAUTION: Be aware that no checks have been done about the ownership of updated objects.
 *
 * </pre>
 * </p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class UpdateProteins extends UpdateProteinsI {

    private static final String ENTRY_OUTPUT_FILE = "/tmp/Entries.error";



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
    private int proteinCreated;
    private int proteinUpdated;
    private int proteinUpToDate;
    private int entryCount;
    private int entrySkipped;

    /**
     * Set of updated/created proteins during the process.
     */
    private Collection proteins;


    public UpdateProteins (IntactHelper helper) throws UpdateException{
         super (helper);
    }



    public int getCreatedCount () {
        return proteinCreated;
    }

    public int getUpdatedCount () {
        return proteinUpdated;
    }

    public int getUpToDateCount () {
        return proteinUpToDate;
    }

    public int getProteinCount () {
        return proteinTotal;
    }

    public int getProteinSkippedCount () {
        return (proteinTotal - (proteinCreated + proteinUpdated + proteinUpToDate));
    }

    public int getEntryCount () {
        return entryCount;
    }

    public int getEntryProcessededCount () {
        return entryCount - entrySkipped;
    }

    public int getEntrySkippedCount () {
        return entrySkipped;
    }

    public void setDebugOnScreen (boolean debug) {
        this.debugOnScreen = debug;
    }

    public void init () {

        proteins = new ArrayList();

        proteinTotal    = 0;
        proteinCreated  = 0;
        proteinUpdated  = 0;
        proteinUpToDate = 0;
        entryCount      = 0;
        entrySkipped    = 0;
    }

    public final String getUrl(String sptrAC) {
        String url = "http://www3.ebi.ac.uk/srs7bin/cgi-bin/wgetz?-e+[SWALL-acc:"
                + sptrAC + "]+-vn+2+-ascii" ;

        return url ;
    }

    public String getAnEntry (String anUrl) {
        BufferedReader br = null ;
        StringBuffer sb = null;
        URL u = null;

        try {
            u = new URL (anUrl) ;
        } catch( Exception e ) {
            logger.error ("Please supply URL to getAnEntry() method ...") ;
            logger.error ("If the URL returns html file, this program won't parse it.") ;
        }

        try {
            InputStream in = u.openStream() ;
            InputStreamReader isr = new InputStreamReader(in) ;
            br = new BufferedReader(isr) ;
            String line ;

            sb = new StringBuffer (4096);
            String lineSeparator = System.getProperty ("line.separator");

            while ((line = br.readLine()) != null ) {
                sb.append (line).append (lineSeparator) ;
            }

            return sb.toString();
        }
        catch (MalformedURLException e) {
            logger.error (e);
        }
        catch (IOException e) {
            logger.error (e);
        }

        return null;
    }

    /**
     * from a given string and a given pattern(string), to find all matches. The matched are
     * retured as a list. This method uses gnu.regexp.* package, not the org.apache.regexp.*
     *@param textin A string from which some pattern will be matched.
     *@param pattern A string as a pattern.
     *@return A list of matched pattern.
     */
    public REMatch[] match (String textin, String pattern ) {
        REMatch[] allMatches = null ;
        try {
            RE magic = new RE ( pattern );
            allMatches = magic.getAllMatches ( textin );
        } catch (Exception e_RE) {
            e_RE.printStackTrace() ;
        }

        return allMatches ;
    }

    /**
     * From a SPTR entry we try to get a set of IntAct protein.<br>
     * As a SPTR entry can contains several ACs, there is a probability that it gives us
     * several IntAct protein.
     *
     * @param sptrEntry a SPTR entry
     * @param helper the intact data source
     * @param taxid the taxid filter (can be null)
     * @return An collection of Intact protein or null if an error occur.
     */
    private Collection getProteinsFromSPTrAC (SPTREntry sptrEntry, String taxid, IntactHelper helper)
            throws SPTRException {

        String spAC[] = sptrEntry.getAccessionNumbers();
        Collection proteins = new ArrayList();
        boolean errorOccured = false;
        int i = 0;
        try {
            Collection tmp = null;
            for (i = 0; i < spAC.length; i++) {
                String ac = spAC[i];
                tmp = helper.getObjectsByXref (Protein.class, ac);

                if (proteins.isEmpty()) {
                    for (Iterator iterator = tmp.iterator(); iterator.hasNext();) {
                        Protein p = (Protein) iterator.next();
                        // keep the protein only if the taxid is the same OR if no taxid is specified
                        if (taxid == null || p.getBioSource().getTaxId().equals(taxid)) {
                            proteins.add (p);
                        }
                    }
                } else {
                    if (tmp != null) {
                        for (Iterator iterator = tmp.iterator(); iterator.hasNext();) {
                            Protein p = (Protein) iterator.next();
                            // keep the protein only if the taxid is the same OR if no taxid is specified
                            if (taxid == null || p.getBioSource().getTaxId().equals(taxid)) {
                                if (false == proteins.contains(p)) {
                                    proteins.add(p);
                                }
                            }
                        }
                    }
                }
            }

        } catch (IntactException e) {
            // multiple object found for that criteria
            logger.error ("error when retreiving Proteins from AC: " + spAC[i], e);
            errorOccured = true;
        }

        if (errorOccured == true) return null;

        return proteins;
    }


    /**
     * Update the given BioSource with data taken from Newt.<br>
     * it assumes that the taxid is existing in the given BioSource.
     *
     * @param taxid the taxid from which we want to get a Biosource
     * @return an updated BioSource or null
     */
    private BioSource getNewtBiosource (String taxid) {

        if (taxid == null) return null;

        logger.info ("Try to get BioSource data from Newt");
        NewtServerProxy.NewtResponse response = null;

        try {
            response = newtProxy.query ( Integer.parseInt(taxid) );
        } catch (IOException e) {
            logger.error (e);
            return null;
        } catch (NumberFormatException e) {
            logger.error ("invalid taxid: " + taxid, e);
            return null;
        } catch (NewtServerProxy.TaxIdNotFoundException e) {
            logger.error ("taxId not found from Newt: " + taxid, e);
            return null;
        }

        BioSource bioSource = new BioSource();
        // the taxId can be different in obsoleteness case.
        bioSource.setTaxId ( "" + response.getTaxId() );
        bioSource.setFullName ( response.getFullName() );
        bioSource.setShortLabel ( response.getShortLabel() );
        bioSource.setOwner ( myInstitution );

        return bioSource;
    }

    /**
     * Try to update an existing IntAct BioSource from an other.
     *
     * @param bioSource the IntAct BioSource
     * @param newtBioSource the one from which we get the up-to-date data
     *
     * @return an up-to-date IntAct BioSource
     * @throws IntactException
     */
    private BioSource updateBioSource (BioSource bioSource,
                                       BioSource newtBioSource) throws IntactException {

        boolean needUpdate = false;

        // compare these two BioSource and update in case of differences
        String newtTaxid = newtBioSource.getTaxId();
        if (false == bioSource.getTaxId().equals (newtTaxid)) {
            bioSource.setTaxId(newtTaxid);
            logger.debug ("Obsolete taxid: taxid " + bioSource.getTaxId() + " becomes " + newtTaxid);
            needUpdate = true;
        }

        String fullName = newtBioSource.getFullName();
        if (false == fullName.equals(bioSource.getFullName())) {
            bioSource.setFullName (fullName);
            needUpdate = true;
        }

        String shortLabel = newtBioSource.getShortLabel();
        if (false == shortLabel.equals(bioSource.getShortLabel())) {
            bioSource.setShortLabel (shortLabel);
            needUpdate = true;
        }

        if (needUpdate) {
            logger.info ("update biosource (taxid="+ bioSource.getTaxId() +")");

            try {
                helper.update (bioSource);
            } catch (IntactException ie) {
                throw ie;
            }
        }

        return bioSource;
    } // updateBioSource


    private String getUpToDateTaxid (final String taxid) throws IntactException {

        String uptodateTaxid = null;

        if (taxid != null) {
            // there is a filter, check if the given taxid is valid AND not obsolete
            BioSource newtBioSource = getNewtBiosource (taxid);
            if (newtBioSource != null) {
                if (newtBioSource.getTaxId().equals(taxid)) {
                    // the current taxid is up-to-date
                    uptodateTaxid = taxid;
                } else {
                    // obsolete taxid ... update IntAct and the taxid filter
                    Collection c = helper.search(BioSource.class.getName(),
                            "taxId",
                            taxid);
                    if (c.size() == 0) {
                        // doesn't exist in IntAct

                        // update the taxid filter
                        uptodateTaxid = newtBioSource.getTaxId();

                    } else if (c.size() == 1) {
                        // it Exists so update it
                        BioSource bs = (BioSource) c.iterator().next();
                        bs = updateBioSource(bs, newtBioSource);

                        // update the taxid filter
                        uptodateTaxid = bs.getTaxId();
                    } else {
                        // Inconsistancy: we should have 0 or 1 record
                        logger.error("The taxid " + newtBioSource.getTaxId() +
                                " gives us several BioSource in IntAct.");
                    }
                }
            } else {
                // no Newt Biosource
                logger.error  ("The filter taxid " + taxid + " gives no results in Newt.");
            }

            if (uptodateTaxid == null) {
                logger.error ("The taxid parameter given by the user is wrong");
            }
        }

        return uptodateTaxid;
    }

    /**
     * From a SPTREntry, that method will look for the correxponding proteins
     * in IntAct in order to update its data or create brand new if it doesn't exists.<br>
     *
     * @param sptrEntry the SPTR entry
     * @param taxid species filter (null means no filter). That taxid must not be obsolete.
     * @param update If true, update existing Protein objects according to the retrieved data.
     *               else, skip existing Protein objects.
     */
    private void createProteinFromSPTrEntry (final SPTREntry sptrEntry,
                                             String taxid,
                                             final boolean update) throws SPTRException {

        Protein protein = null;
        int i;

        try {
            // according to the SPTR entry, get the corresponding proteins in IntAct
            Collection proteins = getProteinsFromSPTrAC (sptrEntry, taxid, helper);
            if (proteins == null) {
                logger.error  ("An error occured when trying to get IntAct protein, exit update");
                writeEntry2file ();
                return ;
            }

            int organismCount = sptrEntry.getOrganismNames().length;
            ArrayList taxids = new ArrayList (organismCount);
            if (taxid == null) {
                // keep all taxids from the SPTR Entry
                for (i = 0; i < organismCount; i++) {
                    String organism   = sptrEntry.getOrganismNames()[i];
                    String entryTaxid = sptrEntry.getNCBITaxonomyID (organism);
                    taxids.add (entryTaxid);
                }
            } else {
                // apply the filter and keep only the one specified if existing in the Entry
                for (i = 0; i < organismCount; i++) {
                    String organism   = sptrEntry.getOrganismNames()[i];
                    String entryTaxid = sptrEntry.getNCBITaxonomyID (organism);
                    if (taxid.equals(entryTaxid))
                        taxids.add (entryTaxid);
                    else logger.info ("SKIP: sptrTaxid=" + entryTaxid);
                }
            }


            /**
             * Process all colelcted BioSource
             */
            int taxidCount = taxids.size();
            for (i = 0; i < taxidCount; i++) {

                proteinTotal++;

                // for each taxid the user want to process
                String sptrTaxid = (String) taxids.get(i);
                logger.info ("\tPROCESS: sptrTaxid=" + sptrTaxid);
                boolean error = false;


                /*
                    From the taxId found in the SPTREntry, we are going now to get (maybe update)
                    or create the corresponding BioSource in IntAct.
                    We rely on Newt (http://www.ebi.ac.uk/newt) to get up to date data related
                    to that taxid
                    We take into account that the taxid we got from SPTR can be obsolete and so
                    the one we get from Newt can be different. In such a case we update IntAct
                    with the up to date data.
                */

                // look for that BioSource in Intact
                Collection c = helper.search (BioSource.class.getName(), "taxId", sptrTaxid);
                BioSource bioSource = null;

                if (c.size() <= 1) {

                    // 0 or 1 BioSource found in IntAct
                    BioSource newtBioSource = getNewtBiosource (sptrTaxid);

                    if (newtBioSource == null) {
                        logger.error("The taxid " + sptrTaxid + " gives no results in Newt.");
                        error = true;
                    } else {

                        if (c.size() == 0) {
                            // that taxid doesn't exists yet in IntAct

                            if ( false == newtBioSource.getTaxId().equals (sptrTaxid) ) {
                                /*
                                    The taxid we got from newt is different from the one we
                                    was looking for originaly. It was probably obsolete.
                                    So we have to update IntAct.
                                 */

                                // look for that BioSource (newt taxid) in Intact
                                Collection c2 = helper.search (BioSource.class.getName(),
                                        "taxId",
                                        newtBioSource.getTaxId());

                                if (c2.size() == 0) {
                                    // Doesn't exists so create a new Biosource in IntAct
                                    logger.info ("Create new BioSource in IntAct, taxid: " + newtBioSource.getTaxId());
                                    helper.create (newtBioSource);
                                    bioSource = newtBioSource;

                                    // Keep the up to date taxId
                                    sptrTaxid = bioSource.getTaxId();

                                } else if (c2.size() == 1) {
                                    // already exists so update it (only if needed)
                                    bioSource = (BioSource) c2.iterator().next();
                                    bioSource = updateBioSource (bioSource, newtBioSource);

                                    // Keep the up to date taxId
                                    sptrTaxid = bioSource.getTaxId();
                                } else {
                                    // Inconsistancy: we should have 0 or 1 record
                                    logger.error ("The taxid " + newtBioSource.getTaxId() +
                                                  " gives us several BioSource in IntAct.");
                                    error = true;
                                }

                            } else {
                                // TaxIds doesn't change so create a new Biosource in IntAct
                                logger.info ("Create new BioSource in IntAct, taxid: " + newtBioSource.getTaxId());
                                helper.create (newtBioSource);
                                bioSource = newtBioSource;
                            }

                        } else {
                            // exactly 1 BioSource found in IntAct with the original taxid, so, check for update.
                            bioSource = (BioSource) c.iterator().next();

                            // already exists so update it (only if needed)
                            bioSource = updateBioSource (bioSource, newtBioSource);
                        } // else
                    } // else
                } else {
                    // Inconsistancy: we should have one or no record
                    logger.error ("The taxid " + sptrTaxid + " gives us several BioSource in IntAct.");
                    error = true;
                }


                if (error == false) {
                    protein = null;
                    // look for a protein in the set which has that taxid
                    for (Iterator iterator = proteins.iterator(); iterator.hasNext() && protein == null;) {
                        Protein tmp = (Protein) iterator.next();
                        BioSource bs = tmp.getBioSource();

                        /*
                            Problem here if the taxid in the entry is obsolete and
                            in intact we have stored the right one ... we don't get in
                            the loop and so that protein is not removed from the collection.
                         */

                        if (bs != null && bs.getTaxId().equals(sptrTaxid)) {
                            // found ... remove it from the collection
                            protein = tmp;
                            proteins.remove (tmp);
                        }
                    }

                    if (protein == null) {
                        // doesn't found so create a new one
                        logger.info ("No existing protein for that taxid, create a new one");

                        helper.startTransaction();
                        if (createNewProtein (sptrEntry, bioSource)) {
                            logger.info ("creation sucessfully done");
                        }
                        helper.finishTransaction();
                        logger.info ("Transaction conplete");
                    } else {
                        if (update) {
                            /*
                               We are doing the update of the existing protein only if the
                               user request it we only update its content if needed
                             */
                            logger.info ("A protein exists for that taxid, try to update");

                            helper.startTransaction();
                            if (updateExistingProtein (protein, sptrEntry, bioSource)) {
                                logger.info ("update sucessfully done");
                            }
                            helper.finishTransaction();
                            logger.info ("Transaction conplete");
                        }
                    }

                } else {
                    logger.info ("Stop updating from taxid: " + sptrTaxid);
                }
            } // for each taxid

            /*
               Check if the protein list is empty, if not, that means we have in IntAct some
               proteins linked with a BioSource which is not recorded in the current SPTR Entry
             */
            if (false == proteins.isEmpty()) {

                logger.error ("The following association's <protein,taxid> list has been found in IntAct but not in SPTR:");
                for (Iterator iterator = proteins.iterator(); iterator.hasNext();) {
                    Protein p = (Protein) iterator.next();
                    BioSource bs = p.getBioSource();

                    logger.error ("\t intactAC=" + p.getAc() + " taxid=" + (bs == null ? "none" : bs.getTaxId()) );
                }
            }
        } catch (IntactException ie) {
            logger.error (ie.getRootCause(), ie);

            writeEntry2file();

            // Try to rollback
            if (helper.isInTransaction()) {
                logger.error ("Try to undo transaction.");
                try {
                    // try to undo the transaction
                    helper.undoTransaction();
                } catch (IntactException ie2) {
                    logger.error ("Could not undo the current transaction");
                }
            }
        }

    } // createProteinFromSPTrEntry


    public void addNewXref (AnnotatedObject current, Xref xref)  {
        // Make sure the xref does not yet exist in the object
        Collection xrefs = current.getXref();
        for (Iterator iterator = xrefs.iterator(); iterator.hasNext();) {
            Xref anXref = (Xref) iterator.next();
            if (anXref.equals(xref)) {
                return;
            }
        }

        // add the xref to the AnnotatedObject
        current.addXref (xref);

        // That test is done to avoid to record in the database an Xref
        // which is already linked to that AnnotatedObject.
        if (xref.getParentAc() == current.getAc()) {
            try {
                helper.create(xref);
            } catch (Exception e_xref) {
                logger.error ("Error when creating an Xref for protein " + current, e_xref);
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
    private boolean updateXref (final SPTREntry sptrEntry,
                                Protein protein,
                                final String database,
                                final CvDatabase cvDatabase) throws SPTRException{

        boolean needUpdate = false;

        // create existing GO Xrefs
        SPTRCrossReference cr[] = sptrEntry.getCrossReferences (database);

        for (int i = 0; i < cr.length; i++) {
            SPTRCrossReference sptrXref = cr[i];
            String ac = sptrXref.getAccessionNumber();
            String id = sptrXref.getPropertyValue(SPTRCrossReference.PROPERTY_DESCRIPTION);

            Xref xref = new Xref ( myInstitution,
                                   cvDatabase,
                                   ac,
                                   id, null, null) ;

            Collection xrefs = protein.getXref();
            if (! xrefs.contains(xref)) {
                // link the Xref to the protein and record it in the database
                addNewXref (protein, xref);
                logger.info ("CREATE "+ database +" Xref[AC: " + ac + ", Id: " + id + "]");
                needUpdate = needUpdate || true;
            } else {
                logger.info ("SKIP: "+ database +" Xref[AC: " + ac + ", Id: " + id + "] already exists");
            }
        }

        return needUpdate;
    }

    /**
     * Update an existing protein with data from a SPTR Entry.
     *
     * @param protein the protein to update
     * @param sptrEntry the source entry
     * @param bioSource the BioSource to link to the Protein
     * @return true is the protein is created

     * @throws SPTRException
     * @throws IntactException
     */
    private boolean updateExistingProtein (Protein protein,
                                           SPTREntry sptrEntry,
                                           BioSource bioSource)
            throws SPTRException,
            IntactException {

        boolean needUpdate = false;

        // get the protein info we need
        String fullName    = sptrEntry.getProteinName();
        String shortLabel  = sptrEntry.getID();
        String proteinAC[] = sptrEntry.getAccessionNumbers();
        String sequence    = sptrEntry.getSequence();
        String crc64       = sptrEntry.getCRC64();

        if (!protein.getFullName().equals(fullName)) {
            protein.setFullName (fullName);
            needUpdate = true;
        }

        if (!protein.getShortLabel().equals(shortLabel)) {
            protein.setShortLabel (shortLabel);
            needUpdate = true;
        }

        if (!protein.getSequence().equals(sequence)) {
            protein.setSequence (helper, sequence);
            needUpdate = true;
        }

        if (!protein.getCrc64().equals(crc64)) {
            protein.setCrc64 (crc64);
            needUpdate = true;
        }

        // check bioSource
        boolean needBiosourceUpdate = false;
        BioSource _biosource = protein.getBioSource();
        if (! _biosource.getTaxId().equals (bioSource.getTaxId())) {
            needBiosourceUpdate = true;
        }

        if (! _biosource.getFullName().equals (bioSource.getFullName())) {
            needBiosourceUpdate = true;
        }

        if (needBiosourceUpdate) {
            protein.setBioSource (bioSource);
            needUpdate = true;
        }

        /*
         * false || false -> false
         * false || true -> true
         * true || false -> true
         * true || true -> true
         */
        needUpdate = needUpdate || updateXref (sptrEntry, protein, Factory.XREF_SGD, sgdDatabase);
        needUpdate = needUpdate || updateXref (sptrEntry, protein, Factory.XREF_GO, goDatabase);

        // update SPTR Xref
        Xref sptrXref = new Xref ( myInstitution,
                                   sptrDatabase,
                                   proteinAC[0],
                                   shortLabel, null, null) ;

        // check Xrefs
        Collection xrefs = protein.getXref();
        if (! xrefs.contains(sptrXref)) {
            // link the Xref to the protein and record it in the database
            addNewXref (protein, sptrXref);
            needUpdate = true;

            logger.info ("CREATE SPTR Xref[spAC: " + proteinAC[0] + ", spId: " + shortLabel + "]");
        } else {
            logger.info ("SKIP: SPTR Xref[spAC: " + proteinAC[0] + ", spId: " + shortLabel + "] already exists");
        }

        if (needUpdate == true) {
            // update databse
            try {
                helper.update (protein);
                // keep that protein
                proteins.add(protein);

                if (debugOnScreen) System.out.print (" U");
                proteinUpdated++;
                return true;
            } catch (IntactException ie) {
                logger.error (protein, ie);
                throw ie;
            }
        } else {
            logger.info ("That protein was up-to-date");
            if (debugOnScreen) System.out.print (" -");
            proteinUpToDate++;
        }

        return false;
    } // updateExistingProtein


    /**
     * From a SPTR Entry, create in IntAct a new protein.
     *
     * @param sptrEntry the source entry
     * @param bioSource the BioSource to link to the Protein
     * @return true is the protein is created
     * @throws SPTRException
     * @throws IntactException
     */
    private boolean createNewProtein (SPTREntry sptrEntry,
                                      BioSource bioSource)
            throws SPTRException,
                   IntactException {

        Protein protein = new Protein ();

        protein.setOwner (myInstitution);

        // get the protein info we need
        String shortLabel  = sptrEntry.getID();

        helper.create(protein);

        String fullName    = sptrEntry.getProteinName();
        String proteinAC[] = sptrEntry.getAccessionNumbers();
        String sequence    = sptrEntry.getSequence();
        String crc64       = sptrEntry.getCRC64();

        protein.setFullName (fullName);
        protein.setShortLabel (shortLabel);
        protein.setSequence (helper, sequence);
        protein.setCrc64 (crc64);
        protein.setBioSource (bioSource);

        updateXref (sptrEntry, protein, Factory.XREF_SGD, sgdDatabase);
        updateXref (sptrEntry, protein, Factory.XREF_GO, goDatabase);

        // create a SPTR Xref
        Xref sptrXref = new Xref ( myInstitution,
                                   sptrDatabase,
                                   proteinAC[0],
                                   shortLabel, null, null) ;

        addNewXref (protein, sptrXref);
        logger.info ("created SPTR Xref[spAC: " + proteinAC[0] + ", spId: " + shortLabel + "]");

        // update database
        try {
            helper.update (protein);
            // keep that protein
            proteins.add(protein);

            logger.info ("protein updated: " + protein);
            if (debugOnScreen) System.out.print(" C");

            proteinCreated++;
            return true;
        } catch (IntactException e) {
            logger.error (protein, e);
            throw e;
        }

    } // createNewProtein


    public Collection insertSPTrProteins (String proteinAc) {

        String url = getUrl(proteinAc);
        int i = insertSPTrProteins(url, null, true);
        if (debugOnScreen) System.out.println(i + " proteins created/updated.");

        return proteins;
    }

    /**
     * Creates a simple Protein object for entries which are not in SPTR.
     * The Protein will more or less only contain the crossreference to the source database.
     * @param anAc The primary identifier of the protein in the external database.
     * @param aDatabase The database in which the protein is listed.
     * @param aTaxId The tax id the protein should have
     * @return the protein created or retrieved from the IntAct database
     */
    public Protein insertSimpleProtein(String anAc, CvDatabase aDatabase, String aTaxId)
            throws IntactException{

        // Search for the protein or create it
        Collection newProteins = helper.getObjectsByXref(Protein.class, anAc);

        // Get or create valid biosource from taxid
        BioSource validBioSource = getValidBioSource(aTaxId);

        /* If there were obsolete taxids in the db, they should now be updated.
           So we will only compare valid biosources.
        */

        // Filter for exactly one entry with appropriate taxId
        Protein targetProtein = null;
        for (Iterator i = newProteins.iterator(); i.hasNext();){
            Protein tmpProtein = (Protein) i.next();
            if (tmpProtein.getBioSource().getTaxId().equals(validBioSource.getTaxId())){
                if (null == targetProtein){
                    targetProtein = tmpProtein;
                } else {
                    throw new IntactException("More than one Protein with AC "
                                              + anAc
                                              + " and taxid "
                                              + aTaxId
                                              + " found.");
                }
            }
        }

        if (null == targetProtein) {
            // No appropriate protein found, create it.

            // Create new Protein
            targetProtein = new Protein((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                             validBioSource, anAc);
            helper.create(targetProtein);

            // Create new Xref if a DB has been given
            if (null != aDatabase) {
                Xref newXref = new Xref();
                newXref.setOwner(myInstitution);
                newXref.setCvDatabase(aDatabase);
                newXref.setPrimaryId(anAc);
                targetProtein.addXref(newXref);
                helper.create(newXref);
            }
        }

        return targetProtein;
    }


    public int insertSPTrProteins (String sourceUrl,
                                   String taxid,
                                   boolean update) {

        logger.info ("update from URL: " + sourceUrl);
        if (debugOnScreen) System.out.println ("update from URL: " + sourceUrl);

        if (sourceUrl == null) return 0;

        /**
         * Init() has to be called in order to have the statistics properly initialized
         * as well as to keep track of all updated/created proteins.
         */
        init ();

        // check the taxid parameter validity
        try {
            String newTaxid = getUpToDateTaxid (taxid);
            if (taxid != null && newTaxid == null) {
                logger.error ("Could not find an up-to-date taxid for " + taxid + " abort update procedure.");
                return getCreatedCount() + getUpdatedCount();
            }
        } catch (IntactException ie) {
            String msg = "Could not find an up-to-date taxid for " + taxid + " abort update procedure.";
            logger.error (msg, ie);
            return getCreatedCount() + getUpdatedCount();
        }


        try {
            URL url = new URL (sourceUrl);

            // parse it with YASP
            if (debugOnScreen) {
                System.out.print ("Parsing...");
                System.out.flush();
            }

            entryIterator = YASP.parseAll (url);

            if (debugOnScreen) System.out.println ("done");

            /**
             * C A U T I O N
             * -------------
             *  The YASP Iterator has to taken with carefulness.
             * .next() method gives you the current element
             * .hasNext() loads the next elements and says you if there was one.
             * So, calling twice .hasNext() without processing in between would
             * make you miss an element.
             */
            while (entryIterator.hasNext()) {

                entryCount++;

                /**
                 *  E X I T   H E R E
                 *  after n iteration
                 */
                // if (entryCount == 200) return proteinCreated + proteinUpdated;


                // Check if there is any exception remaining in the Entry before to use it
                if (entryIterator.hadException()) {
                    Exception originalException = entryIterator.getException().getOriginalException();

                    if (originalException != null) {
                        originalException.printStackTrace();
                    } else {
                        logger.error (entryIterator.getException());
                        entryIterator.getException().printStackTrace();
                    }

                    // wrong entries are NOT processed any further
                    writeEntry2file();
                    entrySkipped++;
                    continue;
                }

                // get the SPTREntry
                SPTREntry sptrEntry = (SPTREntry) entryIterator.next();

                if (sptrEntry == null) {
                    logger.error("\n\nSPTR entry is NULL ... skip it");

                    entrySkipped++;
                    continue;
                }

                if (debugOnScreen) System.out.print("(" + sptrEntry.getID() + ":");

                createProteinFromSPTrEntry (sptrEntry, taxid, update);

                if (debugOnScreen) System.out.println(")");


                // Display some statistics every 500 entries processed.
                if (entryCount % 500 == 0) {
                    printStats();
                }
            }

        } catch (YASPException e) {
            logger.error (e.getOriginalException());

        } catch (MalformedURLException e) {
            logger.error ("URL error: " + sourceUrl, e);
            logger.error ("Please provide a valid URL");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error (e);
        }

        closeFile(); // try to close the bad entries repository if it exists

        printStats();

        return getCreatedCount() + getUpdatedCount() ;
    }

    private void printStats() {
        // in log file
        logger.info ("Protein created:    " + getCreatedCount());
        logger.info ("Protein updated:    " + getUpdatedCount());
        logger.info ("Protein up-to-date: " + getUpToDateCount());
        logger.info ("Protein skipped:    " + getProteinSkippedCount());

        logger.info ("Entry processed:    " + getEntryProcessededCount());
        logger.info ("Entry skipped:      " + getEntrySkippedCount());

        // on STDOUT
        if (debugOnScreen) System.out.println ("Protein created:    " + getCreatedCount());
        if (debugOnScreen) System.out.println ("Protein updated:    " + getUpdatedCount());
        if (debugOnScreen) System.out.println ("Protein up-to-date: " + getUpToDateCount());
        if (debugOnScreen) System.out.println ("Protein skipped:    " + getProteinSkippedCount());

        if (debugOnScreen) System.out.println ("Entry processed:    " + getEntryProcessededCount());
        if (debugOnScreen) System.out.println ("Entry skipped:      " + getEntrySkippedCount());

    }


    public BioSource addBioSource (Institution institution,
                                   String orgName,
                                   String taxId) {
        BioSource bs = new BioSource() ;
        try {
            bs.setOwner(institution) ;
            bs.setFullName(orgName) ;
            bs.setTaxId(taxId) ;
            helper.create(bs) ;
        } catch (Exception e_bioSrc) {
            e_bioSrc.printStackTrace() ;
        }
        return bs;
    }


    /**
     * Write the content of an Entry in a file for later on processing
     */
    private void writeEntry2file () {

        if (file == null) {
            // make a generic output byte stream
            try {
                // Get today's date and current time
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd@HH.mm");
                String time = formatter.format(date);

                filename = ENTRY_OUTPUT_FILE + "-" + time;
                file = new FileOutputStream(filename);
                // attach BufferedOutputStream to buffer it
                buffer = new BufferedOutputStream(file, 4096);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }

        // write the entry in the file
        try {
            String entry = entryIterator.getOriginal();
            if (entry != null) {
                buffer.write(entry.getBytes());
                logger.error ("\nEntry written in the file");
            } else {
                logger.error ("Couldn't write the entry in the file");
            }
        } catch (IOException e) {
            logger.error ("An error occur when trying to save an entry which cause a processing problem", e);
        }
    }


    private void closeFile () {

        if (buffer != null) {
            try {
                buffer.close();
            } catch (IOException e) {
                logger.error ("Error when trying to close faulty entry file", e);
            }
        }

        if (file != null ) {
            try {
                file.close();
            } catch (IOException e) {
                logger.error ("Error when trying to close faulty entry file", e);
            }
        }

    }


    public String getErrorFileName () {
        return filename;
    }


    /**
     * Create or update a BioSource object from a taxid.
     * @param aTaxId The tax id to create/update a biosource for
     * @return a valid, persistent BioSource
     */
    public BioSource getValidBioSource (String aTaxId) throws IntactException {

        // If a valid BioSource object already exists, return it.
        if (bioSourceCache.containsKey(aTaxId)) {
            return (BioSource) bioSourceCache.get(aTaxId);
        }

        // Get all existing BioSources with aTaxId
        // Exception if there are more than one.
        Collection currentBioSources = helper.search (BioSource.class.getName(),
                                                      "taxId", aTaxId);
        if (currentBioSources.size() > 1) {
            throw new IntactException("More than one BioSource with this taxId found: " + aTaxId);
        }

        // Get a correct BioSource from Newt
        BioSource validBioSource = getNewtBiosource(aTaxId);
        if (null == validBioSource) {
            throw new IntactException("The taxId is invalid: " + aTaxId);
        }

        // The verified BioSource
        BioSource newBioSource = null;

        // If there is no current BioSource, create it
        if (0 == currentBioSources.size()) {
            if (validBioSource.equals(aTaxId)) {
                // look for that new taxid in Intact
                helper.create(validBioSource);
                newBioSource = validBioSource;
            } else {
                // it was obsolete
                Collection bioSources = helper.search (BioSource.class.getName(),
                                                              "taxId", validBioSource.getTaxId());
                switch (bioSources.size()) {
                    case 0:
                        // doesn't exists, so create it.
                        helper.create(validBioSource);
                        newBioSource = validBioSource;
                        break;

                    case 1:
                        // it exists, try to update it.
                        BioSource intactBs = (BioSource) bioSources.iterator().next();
                        newBioSource = updateBioSource(intactBs, validBioSource);

                    default:
                        throw new IntactException("More than one BioSource with this taxId found: " + aTaxId);
                }
            }
        } else {
            // only one BioSource found with the original taxid
            // If it is obsolete, update current BioSource
            BioSource currentBioSource = (BioSource) currentBioSources.iterator().next();
            if (! currentBioSource.equals(validBioSource)){
                newBioSource = updateBioSource(currentBioSource, validBioSource);
            }  else {
                newBioSource = currentBioSource;
            }
        }

        // Return valid BioSource and update cache
        /* The bioSourceCache will also contain associations from obsolete taxIds
           to valid BioSource objects to avoid looking up the same obsolete Id
           over and over again.
        */
        bioSourceCache.put(aTaxId, newBioSource);

        return newBioSource;
    }


    /**
     *  D E M O
     *
     * Could be use for loading from a .txl file
     * ./scripts/javaRun.sh UpdateProteins file:///homes/user/mySPTRfile.txl
     *
     */
    public static void main(String[] args) throws Exception {

        IntactHelper helper = null;

        try {
            String url = null;

            if (args.length >= 1) {
                url = args[0];
                System.out.println("URL: " + url);
            } else {
                System.out.println("Usage: javaRun.sh UpdateProteins <URL>");
                System.exit(1);
            }

            try {
                DAOSource dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");
                String repositoryFile = "config/repository.xml";
                Map fileMap = new HashMap();
                fileMap.put (Constants.MAPPING_FILE_KEY, repositoryFile);
                dataSource.setConfig(fileMap);
                helper = new IntactHelper(dataSource);
                System.out.println("Helper created");
            } catch (IntactException e) {
                System.out.println("Root cause: " + e.getRootCause());
                e.printStackTrace();
                System.exit (1);
            }

            UpdateProteinsI update = new UpdateProteins (helper);
            Chrono chrono = new Chrono();
            chrono.start();

            update.setDebugOnScreen (true);
            int nb = update.insertSPTrProteins (url, null, true);

            chrono.stop();
            System.out.println("Time elapsed: " + chrono);
            System.out.println("Entries error in : " + update.getErrorFileName());

            System.out.println (nb + " protein updated/created");

        } catch (Exception e ) {
            e.printStackTrace();
        } finally {
            if (helper != null) helper.closeStore();
        }
    }
}
