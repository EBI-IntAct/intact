/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.view.utils;

import org.apache.taglibs.display.Decorator;


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
    public String getId() {
        SourceBean bean = (SourceBean) this.getObject();
        if (bean == null) return "";

        final String id = bean.getId();
        final String sourceBrowserUrl = bean.getSourceBrowserUrl();

        return "<a href=\"" + sourceBrowserUrl + "\" target=\"selectedSourcetFrame\">" + id + "</a>";
    }


    /**
     * Transform the label and value data contained by the LabelValueBean beiing
     * displayed by the Display:* tag into a link.
     *
     * e.g. label = XY:0000123 and value = http://www.mysourceXY.com/displayIt?id=XY:0000123
     *      it format it as : <a href="http://www.mysourceXY.com/displayIt?id=XY:0000123"> XY:0000123 </a>
     *
     * @return data properly formated in order to be displayed by the display taglib
     */
    public String getDirectHighlightUrl() {
        SourceBean bean = (SourceBean) this.getObject();
        if (bean == null) return "";

        String applicationPath = bean.getApplicationPath();

        if (bean.isSelected() == false) {
            final String url = bean.getDirectHighlightUrl();
            return "<center><a href=\"" + url + "\" target=\"_top\"><img src=\""+ applicationPath +
                   "/images/ok-grey.png\" border=\"0\" alt=\"(*)\"></a></center>";
        } else {
            return "<center><img src=\""+ applicationPath +
                   "/images/ok-red.png\" border=\"0\" alt=\"(-)\"></center>";
        }
    }


    /**
     * Send back the desciption of the source and in case it doesn't exists,
     * give a meaningfull message.
     * @return the soruce description or an explanation messages.
     */
    public String getDescription () {
        SourceBean bean = (SourceBean) this.getObject();
        if (bean == null) return "";

        String description = bean.getDescription();
        if (description == null || description.trim().length() == 0) {
            description = "<font color=\"#898989\"> No description available </font>";
        }

        return description;
    }
}
