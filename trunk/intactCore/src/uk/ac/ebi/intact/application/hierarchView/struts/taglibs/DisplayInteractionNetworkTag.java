/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Properties;



/**
 * That class allow to initialize properly the HTTPSession object
 * with what will be neededlater by the user of the web application.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class DisplayInteractionNetworkTag extends TagSupport {

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return EVAL_BODY_INCLUDE;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * option list.
     */
    public int doEndTag() throws JspException {
        HttpSession session = pageContext.getSession();

        try {
            ImageBean imageBean = (ImageBean) session.getAttribute (StrutsConstants.ATTRIBUTE_IMAGE_BEAN);
            String AC           = (String)  session.getAttribute (StrutsConstants.ATTRIBUTE_AC);

            /**
             *  Display only the picture if an AC is in the session
             */
            if ((null != AC) && (null != imageBean)) {

                // Display the HTML code map
                pageContext.getOut().write (imageBean.getMapCode());

                // read the Graph.proterties file
                String mapName = null;
                String format = null;

                Properties propertiesBusiness = PropertyLoader.load (uk.ac.ebi.intact.application.hierarchView.business.Constants.PROPERTY_FILE);

                if (null != propertiesBusiness) {
                    mapName = propertiesBusiness.getProperty ("hierarchView.image.map.name");
                    format = propertiesBusiness.getProperty ("hierarchView.image.format.name");
                }

                String msg = "<p align=\"left\">\n"
                        + "  <center>"
                        + "     <img src=\"/hierarchView/GenerateImage?format=" + format + "\" "
                        + "      USEMAP=\"#" + mapName +"\" border =\"0\">"
                        + "     <br>"
                        + "  </center>"
                        + "</p>";

                pageContext.getOut().write (msg);
            }

        } catch (Exception ioe) {
            throw new JspException ("Fatal error: init tag could not initialize user's HTTPSession.");
        }
        return EVAL_PAGE;
    } // doEndTag

} // DisplayInteractionNetworkTag