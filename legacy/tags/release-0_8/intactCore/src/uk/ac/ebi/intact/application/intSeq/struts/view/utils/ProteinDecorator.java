/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.view.utils;

import org.apache.taglibs.display.Decorator;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import uk.ac.ebi.intact.application.intSeq.struts.view.utils.SrsResultBean;


/**
 * Decorator which should be used by the display:table tag in order to display properly
 * accession numbers with their description. This display table is in the srsSearchResults.jsp page
 *
 * <p> Example : <br>
 *      <b>accList</b> is the name under which you have stored a List object to display in the session.
 *   <p>
 *      <display:table width="100%" name="accList"
 *       <p>
 *          decorator="newIntactCore.intactCore.src.uk.ac.ebi.intact.application.intSeq.struts.view.utils.ProteinDecorator">
 *            <p>
 *              <display:column property="acc" /><br>
 *              <display:column property="des"  />
 *            </p>
 *       </p>
 *      </display:table>
 *   </p>
 * </p>
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version $Id$
 */

public class ProteinDecorator extends Decorator {

    /**
     * Transform the accession number contained by the SrsResultBean bean beiing
     * displayed by the Display:* tag into a link.
     *
     * e.g. accession number = P22324
     * it formats it as:
     * <a href="http://www.site.com/intSeq/do/accession?acc=P22324"> P22324 </a>
     *
     * @return data properly formatted in order to be displayed by the display taglib
     */
    public String getAcc() {

        SrsResultBean sr = (SrsResultBean) this.getObject();
        if (sr == null) return "";

        String acc = sr.getAcc();

        return "<a href=\""+ sr.getUrl() +"/do/accession?acc="  + acc + "\"> "+ acc +" </a>";
    }

    /**
     * Send back the description corresponding to the accession number,
     *  and in case it doesn't exists, give a meaningfull message.
     * @return the description or an explanation messages.
     */
    public String getDescription () {
        SrsResultBean sr = (SrsResultBean) this.getObject();
        if (sr == null) return "";

        String description = sr.getDes();
        if (description == null || description.trim().length() == 0) {
            description = "<font color=\"#898989\"> No description available </font>";
        }

        return description;
    }
}
