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
    * @param helper     IntactHelper object to access the database.
    * @param update     If true, update existing Protein objects according to the retrieved data.
    *                   else,    skip existing Protein objects.
    * @return           The number of protein objects created.
    */

    public int insertSPTrProteins(String sourceUrl,
                                  String taxid,
                                  IntactHelper helper,
                                  boolean update);

    /**
     * From a given sptr AC, returns a full URL from where a flatfile format SPTR entry
     * will be fetched. Note, the SRS has several format of data output, the URLs which
     * outputs html format SPTR entry CANNOT be used, since YASP does't have html parsing 
     * function.
     *
     *@param sptrAC, a SPTR AC 
     *@return a full URL. 
     */
    public String getUrl(String sptrAC) ;


    /**
     * From a given URL, returns a string of a SPTR entry. 
     * 
     *@param url, a URL which outputs flatfile of   
     *@return a full URL. 
     */
    public String getAnEntry(String url) ;


    /**
     * from a given string and a given pattern(string), to find all matches. The matched are  
     * retured as a list. This method uses gnu.regexp.* package, not the org.apache.regexp.*
     *@param textin A string from which some pattern will be matched. 
     *@param pattern A string as a pattern.
     *@return A list of matched pattern.
     */
    public REMatch[] match(String textin, String pattern ) ;


    public void addNewXref(AnnotatedObject current, 
                           Xref xref) ;


    /**
     * add (not update) a new BioSource to the db
     *@param orgName Organism name 
     *@param taxId Taxonomy ID 
     */
    public void addBioSource(String orgName, String taxId) ;


}
