/**
 * Created by IntelliJ IDEA.
 * User: hhe
 * Date: Apr 11, 2003
 * Time: 1:31:41 PM
 * To change this template use Options | File Templates.
 */
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactHelper;

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

}
