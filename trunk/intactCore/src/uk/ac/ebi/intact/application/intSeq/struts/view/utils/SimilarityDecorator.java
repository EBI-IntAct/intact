/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.view.utils;

import org.apache.taglibs.display.Decorator;
import uk.ac.ebi.intact.application.intSeq.struts.view.utils.SimilarityResultBean;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * Decorator which should be used by the display:table tag in order to display properly
 * accession numbers, with their percentage identity with our query and the coordinates with
 * the matched fragment. This display table is in the similaritySearchResults.jsp page
 *
 * <p> Example : <br>
 *      <b>similarityList</b> is the name under which you have stored a List object to display in the session.
 *   <p>
 *      <display:table width="100%" name="similarityList"
 *       <p>
 *          decorator="newIntactCore.intactCore.src.uk.ac.ebi.intact.application.intSeq.struts.view.utils.ProteinDecorator">
 *            <p>
 *              <display:column property="intactacc" />
 *              <display:column property="percentageidentity"  />
 *              <display:column property="fragmentstart"  />
 *              <display:column property="fragmentend"  />
 *            </p>
 *       </p>
 *      </display:table>
 *   </p>
 * </p>
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class SimilarityDecorator extends Decorator {

    /**
     * Transform the intact accession number contained by the SimilarityResultBean beiing
     * displayed by the Display:* tag into a link, which will refer to the Search Application.
     *
     * @return data properly formated in order to be displayed by the display taglib
     */
    public String getIntactacc() {

        SimilarityResultBean bda = (SimilarityResultBean) this.getObject();
        if (bda == null) return "";

        String acc = bda.getAc();
        /*if (acc == null || acc.trim().length() == 0) {
            acc = "<font color=\"#898989\"> No IntAct Entry available </font>";
        } */

        return "<a href=http://web7-node1.ebi.ac.uk:8160/intact/search/do/hvWelcome?searchString="  + acc + "> "+ acc +" </a>";
        //return acc;
        //return "<a href=\"/do/search/?searchString="  + acc + "\"> "+ acc +" </a>";
    }

    /**
     * Send back the percentage identity retrieved in the fasta output file
     * and in case it doesn't exists, give a meaningfull message.
     * @return the percentage identity or an explanation messages.
     */
    public String getPercentageidentity () {
        SimilarityResultBean bda = (SimilarityResultBean) this.getObject();
        if (bda == null) return "";

        String percentage = bda.getPercentage();
        if (percentage == null || percentage.trim().length() == 0) {
            percentage = "<font color=\"#898989\"> No percentage available </font>";
        }

        return percentage;
    }

    /**
     * Send back the both fragment starts, for the query and the subject
     * @return the fragment starts.
     */
    public String getFragmentstart () {
        SimilarityResultBean bda = (SimilarityResultBean) this.getObject();
        if (bda == null) return "";

        int queryStart = bda.getQueryFragmentBegin();
        int subjectStart = bda.getSubjectFragmentBegin();

        String start = queryStart + " / " + subjectStart;


        return start;
    }

    /**
     * Send back the both fragment ends, for the query and the subject
     * @return the fragment ends.
     */
    public String getFragmentend () {
        SimilarityResultBean bda = (SimilarityResultBean) this.getObject();
        if (bda == null) return "";

        int queryEnd = bda.getQueryFragmentEnd();
        int subjectEnd = bda.getSubjectFragmentEnd();

        String end = queryEnd + " / " + subjectEnd;

        return end;
    }

}
