/**
 * Created by IntelliJ IDEA.
 * User: hhe
 * Date: Apr 11, 2003
 * Time: 1:31:41 PM
 * To change this template use Options | File Templates.
 */
package uk.ac.ebi.intact.util;

import gnu.regexp.* ;

//re: IntAct OJB
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;



/**
 * Defines the functionality of protein import utilities.
 */
public abstract class UpdateProteinsI {

    protected final static org.apache.log4j.Logger logger = Logger.getLogger("updateProtein");

    protected class UpdateException extends Exception {
        public UpdateException (String message) {
            super(message);
        }
    }


    // cache useful object to avoid redoing queries
    protected static CvDatabase sptrDatabase;
    protected static CvDatabase sgdDatabase;
    protected static CvDatabase goDatabase;
    protected static Institution myInstitution;

    /**
     * Cache valid BioSource objects for taxIds.
     */
    protected static HashMap bioSourceCache = null;

    protected NewtServerProxy newtProxy;

    protected IntactHelper helper = null;

    /**
     *
     * @param helper IntactHelper object to access (read/write) the database.
     * @throws UpdateException
     */
    public UpdateProteinsI (IntactHelper helper) throws UpdateException {
         this.helper = helper;

        try {
            myInstitution = (Institution) helper.getObjectByLabel(Institution.class, "EBI");
            if (myInstitution == null) {
                logger.error ("Unable to find the Institution");
                throw new UpdateException ("Unable to find the Institution");
            }

            sgdDatabase = (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "sgd");
            if (sgdDatabase == null) {
                logger.error ("Unable to find the SGD database in your IntAct node");
                throw new UpdateException ("Unable to find the SGD database in your IntAct node");
            }

            sptrDatabase = (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "sptr");
            if (sptrDatabase == null) {
                logger.error ("Unable to find the SPTR database in your IntAct node");
                throw new UpdateException ("Unable to find the SPTR database in your IntAct node");
            }

            goDatabase = (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "go");
            if (goDatabase == null) {
                logger.error ("Unable to find the GO database in your IntAct node");
                throw new UpdateException ("Unable to find the GO database in your IntAct node");
            }
        } catch (IntactException e) {
            logger.error (e);
            throw new UpdateException ("Couldn't find needed object in IntAct, cause: " + e.getMessage());
        }

        this.helper = helper;

        URL url = null;
        try {
            url = new URL("http://www.ebi.ac.uk/newt/display");
//            url = new URL("http://web7-node1.ebi.ac.uk:9120/newt/display");
        } catch (MalformedURLException e) {
            logger.error ("Newt URL is invalid", e);
            throw new UpdateException ("Unable to create Newt proxy, invalid URL: " + url);
        }

        newtProxy = new NewtServerProxy(url);
        newtProxy.enableCaching();

        bioSourceCache = new HashMap();

    }


    /**
    * Inserts zero or more proteins created from SPTR entries which are retrieved from a URL.
    * IntAct Protein objects represent a specific amino acid sequence in a specific organism.
    * If a SPTr entry contains more than one organism, one IntAct entry will be created for each organism,
    * unless the taxid parameter is not null.
    *
    *
    * @param sourceUrl  The URL which delivers zero or more SPTR flat file formatted entries.
    * @param taxid      Of all entries retrieved from sourceURL, insert only those which have this
    *                   taxid.
    *                   If taxid is empty, insert all protein objects.
    * @param update     If true, update existing Protein objects according to the retrieved data.
    *                   else, skip existing Protein objects.
    * @return           The number of protein objects created.
    */
    public abstract int insertSPTrProteins (String sourceUrl,
                                            String taxid,
                                            boolean update);

    /**
     * Inserts zero or more proteins created from SPTR entries which are retrieved from an SPTR Accession number.
     * IntAct Protein objects represent a specific amino acid sequence in a specific organism.
     * If a SPTr entry contains more than one organism, one IntAct entry will be created for each organism.
     *
     * @param proteinAc SPTR Accession number of the protein to insert/update
     * @return          a set of created/updated protein.
     */
    public abstract Collection insertSPTrProteins (String proteinAc);

    /**
     * Creates a simple Protein object for entries which are not in SPTR.
     * The Protein will more or less only contain the crossreference to the source database.
     * @param anAc The primary identifier of the protein in the external database.
     * @param aDatabase The database in which the protein is listed.
     * @param aTaxId The tax id the protein should have
     * @return the protein created or retrieved from the IntAct database
     */
    public abstract Protein insertSimpleProtein (String anAc, CvDatabase aDatabase,
                                                 String aTaxId)
            throws IntactException;

    /**
     * From a given sptr AC, returns a full URL from where a flatfile format SPTR entry
     * will be fetched. Note, the SRS has several format of data output, the URLs which
     * outputs html format SPTR entry CANNOT be used, since YASP does't have html parsing 
     * function.
     *
     * @param sptrAC a SPTR AC
     * @return a full URL.
     */
    public abstract String getUrl (String sptrAC) ;

    /**
     * From a given URL, returns a string of a SPTR entry. 
     * 
     * @param url a URL which outputs flatfile of
     * @return a full URL.
     */
    public abstract String getAnEntry (String url) ;

    /**
     * from a given string and a given pattern(string), to find all matches. The matched are  
     * retured as a list. This method uses gnu.regexp.* package, not the org.apache.regexp.*
     * @param textin A string from which some pattern will be matched.
     * @param pattern A string as a pattern.
     * @return A list of matched pattern.
     */
    public abstract REMatch[] match (String textin, String pattern) ;

    /**
     * add (not update) a new Xref to the given Annotated object and write it in the database.
     * @param current
     * @param xref
     */
    public abstract void addNewXref (AnnotatedObject current,
                                     Xref xref) ;

    /**
     * add (not update) a new BioSource to the db and send it back.
     * @param institution The owner of the BioSource to create
     * @param orgName Organism name
     * @param taxId Taxonomy ID
     * @return the newly created BioSource
     */
    public abstract BioSource addBioSource (Institution institution,
                                            String orgName,
                                            String taxId) ;

    /**
     * Gives the count of created protein
     * @return created protein count
     */
    public abstract int getCreatedCount () ;

    /**
     * Gives the count of updated protein
     * @return updated protein count
     */
    public abstract int getUpdatedCount () ;

    /**
     * Gives the count of up-to-date protein
     * (i.e. existing in IntAct but don't need to be updated)
     * @return up-to-date protein count
     */
    public abstract int getUpToDateCount () ;

    /**
     * Gives the count of all potential protein
     * (i.e. for an SPTREntry, we can create/update several IntAct protein. One by entry's taxid)
     * @return potential protein count
     */
    public abstract int getProteinCount () ;

    /**
     * Gives the count of protein which gaves us errors during the processing.
     * @return
     */
    public abstract int getProteinSkippedCount () ;

    /**
     * Gives the number of entry found in the given URL
     * @return entry count
     */
    public abstract int getEntryCount () ;

    /**
     * Gives the number of entry successfully processed.
     * @return entry successfully processed count.
     */
    public abstract int getEntryProcessededCount () ;

    /**
     * Gives the number of entry skipped during the process.
     * @return skipped entry count.
     */
    public abstract int getEntrySkippedCount () ;

    /**
     * Allows to displays on the screen what's going on during the update process.
     * @param debug <b>true</b> to enable, <b>false</b> to disable
     */
    public abstract void setDebugOnScreen (boolean debug);

    /**
     * return the filename in which have been saved all Entries which gaves us processing errors.
     * @return the filename or null if not existing
     */
    public abstract String getErrorFileName ();

    /**
     * Create or update a BioSource object from a taxid.
     * @param aTaxId The tax id to create/update a biosource for
     * @return a valid, persistent BioSource
     */
    public abstract BioSource getValidBioSource (String aTaxId) throws IntactException;

}
