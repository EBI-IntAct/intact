/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.view.utils;

import org.apache.taglibs.display.Decorator;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import uk.ac.ebi.intact.application.hierarchView.struts.view.utils.LabelValueBean;


/**
 * Decorator which should be used by the display:table tag in order to display properly
 * highlight sources.
 *
 * <p> Example : <br>
 *      <b>sources</b> is the name under which you have stored a List object to display in the session.
 *   <p>
 *      <display:table width="100%" name="source"
 *       <p>
 *          decorator="uk.ac.ebi.intact.application.hierarchView.struts.view.SourceDecorator">
 *            <p>
 *              <display:column property="label" title="<%= user.getQueryString() %>" /><br>
 *              <display:column property="description"  />
 *            </p>
 *       </p>
 *      </display:table>
 *   </p>
 * </p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class SourceDecorator extends Decorator {

    /**
     * Transform the label and value data contained by the LabelValueBean beiing
     * displayed by the Display:* tag into a link.
     *
     * e.g. label = XY:0000123 and value = http://www.mysourceXY.com/displayIt?id=XY:0000123
     *      it format it as : <a href="http://www.mysourceXY.com/displayIt?id=XY:0000123"> XY:0000123 </a>
     *
     * @return data properly formated in order to be displayed by the display taglib
     */
    public String getLabel() {
        LabelValueBean lvb = (LabelValueBean) this.getObject();
        if (lvb == null) return "";

        String sourceName = lvb.getLabel();
        String sourceLink = lvb.getValue();

        // convert my URL string in UTF-8 format (= become %3d, ...)
//        String encodedUrl = null;
//        try {
//            encodedUrl = URLEncoder.encode (sourceLink, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
////             'UTF-8' is supported so, should not happen
//            e.printStackTrace();
//        }

        return "<a href=\"" + sourceLink + "\" target=\"selectedSourcetFrame\">" + sourceName + "</a>";
    }

    /**
     * Send back the desciption of the source and in case it doesn't exists,
     * give a meaningfull message.
     * @return the soruce description or an explanation messages.
     */
    public String getDescription () {
        LabelValueBean lvb = (LabelValueBean) this.getObject();
        if (lvb == null) return "";

        String description = lvb.getDescription();
        if (description == null || description.trim().length() == 0) {
            description = "<font color=\"#898989\"> No description available </font>";
        }

        return description;
    }
}
