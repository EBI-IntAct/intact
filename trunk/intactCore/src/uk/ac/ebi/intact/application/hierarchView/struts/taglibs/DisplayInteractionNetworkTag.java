/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Properties;

import org.apache.log4j.Logger;



/**
 * That class allow to initialize properly the HTTPSession object
 * with what will be neededlater by the user of the web application.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class DisplayInteractionNetworkTag extends TagSupport {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

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
            IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
            ImageBean imageBean = user.getImageBean();
            String AC = user.getAC();

            /**
             *  Display only the picture if an AC is in the session
             */
            if ((null != AC) && (null != imageBean)) {

                // Display the HTML code map
                pageContext.getOut().write (imageBean.getMapCode());

                // read the Graph.proterties file
                String mapName = null;
                String format = null;

                Properties properties = PropertyLoader.load (uk.ac.ebi.intact.application.hierarchView.business.Constants.PROPERTY_FILE);

                if (null != properties) {
                    mapName = properties.getProperty ("hierarchView.image.map.name");
                    format = properties.getProperty ("hierarchView.image.format.name");
                } else {
                    logger.error("Unable to load properties from " +
                                 uk.ac.ebi.intact.application.hierarchView.business.Constants.PROPERTY_FILE);
                }

                // the random parameter in the URL is given to prevent some browser
                // (eg. Netscape 4.7) to cache image.
                String msg = "<p align=\"left\">\n"
                        + "  <center>"
                        + "     <img src=\"/hierarchView/GenerateImage?format=" + format
                        +        "&random="+ System.currentTimeMillis() +"\" "
                        + "      USEMAP=\"#" + mapName +"\" border =\"0\">"
                        + "     <br>"
                        + "  </center>"
                        + "</p>";

                pageContext.getOut().write (msg);
            } else {
                if (null == AC) logger.info ("Don't enter the servlet calling: AC == null");
                if (null == imageBean) logger.info ("Don't enter the servlet calling: imageBean == null");
            }

        } catch (Exception ioe) {
            throw new JspException ("Fatal error: init tag could not initialize user's HTTPSession.");
        }
        return EVAL_PAGE;
    } // doEndTag

} // DisplayInteractionNetworkTag