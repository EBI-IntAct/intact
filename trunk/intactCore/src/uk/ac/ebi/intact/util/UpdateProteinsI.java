/**
 * Created by IntelliJ IDEA.
 * User: hhe
 * Date: Apr 11, 2003
 * Time: 1:31:41 PM
 * To change this template use Options | File Templates.
 */
package uk.ac.ebi.intact.util;

import java.io.*;
import java.util.*;
import gnu.regexp.* ;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

//re: YASP
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.sptr.flatfile.yasp.*;
import uk.ac.ebi.interfaces.sptr.*;

//re: IntAct OJB
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;



/**
 * Defines the functionality of protein import utilities.
 */
public interface UpdateProteinsI {

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
    * @param helper     IntactHelper object to access (read) the database.
    * @param update     If true, update existing Protein objects according to the retrieved data.
    *                   else, skip existing Protein objects.
    * @return           The number of protein objects created.
    */

    public int insertSPTrProteins (String sourceUrl,
                                   String taxid,
                                   IntactHelper helper,
                                   boolean update);

    /**
     * From a given sptr AC, returns a full URL from where a flatfile format SPTR entry
     * will be fetched. Note, the SRS has several format of data output, the URLs which
     * outputs html format SPTR entry CANNOT be used, since YASP does't have html parsing 
     * function.
     *
     * @param sptrAC a SPTR AC
     * @return a full URL.
     */
    public String getUrl (String sptrAC) ;

    /**
     * From a given URL, returns a string of a SPTR entry. 
     * 
     * @param url a URL which outputs flatfile of
     * @return a full URL.
     */
    public String getAnEntry (String url) ;

    /**
     * from a given string and a given pattern(string), to find all matches. The matched are  
     * retured as a list. This method uses gnu.regexp.* package, not the org.apache.regexp.*
     * @param textin A string from which some pattern will be matched.
     * @param pattern A string as a pattern.
     * @return A list of matched pattern.
     */
    public REMatch[] match (String textin, String pattern) ;

    /**
     * add (not update) a new Xref to the given Annotated object and write it in the database.
     * @param current
     * @param xref
     */
    public void addNewXref (AnnotatedObject current,
                            Xref xref) ;

    /**
     * add (not update) a new BioSource to the db and send it back.
     * @param institution The owner of the BioSource to create
     * @param orgName Organism name
     * @param taxId Taxonomy ID
     * @return the newly created BioSource
     */
    public BioSource addBioSource (Institution institution,
                                   String orgName,
                                   String taxId) ;


    /**
     * Gives the count of created protein
     * @return created protein count
     */
    public int getCreatedCount () ;

    /**
     * Gives the count of updated protein
     * @return updated protein count
     */
    public int getUpdatedCount () ;

    /**
     * Gives the count of up-to-date protein
     * (i.e. existing in IntAct but don't need to be updated)
     * @return up-to-date protein count
     */
    public int getUpToDateCount () ;

    /**
     * Gives the count of all potential protein
     * (i.e. for an SPTREntry, we can create/update several IntAct protein. One by entry's taxid)
     * @return potential protein count
     */
    public int getProteinCount () ;

    /**
     * Gives the count of protein which gaves us errors during the processing.
     * @return
     */
    public int getProteinSkippedCount () ;

    /**
     * Gives the number of entry found in the given URL
     * @return entry count
     */
    public int getEntryCount () ;

    /**
     * Gives the number of entry successfully processed.
     * @return entry successfully processed count.
     */
    public int getEntryProcessededCount () ;

    /**
     * Gives the number of entry skipped during the process.
     * @return skipped entry count.
     */
    public int getEntrySkippedCount () ;

    /**
     * Allows to displays on the screen what's going on during the update process.
     * @param debug <b>true</b> to enable, <b>false</b> to disable
     */
    public void setDebugOnScreen (boolean debug);

    /**
     * return the filename in which have been saved all Entries which gaves us processing errors.
     * @return the filename or null if not existing
     */
    public String getErrorFileName ();
}
