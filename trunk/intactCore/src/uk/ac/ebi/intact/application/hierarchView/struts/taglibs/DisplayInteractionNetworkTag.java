/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.highlightment.HighlightProteins;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Properties;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * That class allows to display in the browser the current interaction network
 * and the associated HTML MAP.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class DisplayInteractionNetworkTag extends TagSupport {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /**
     * Skip the body content.
     */
    public int doStartTag() throws JspTagException {

        return SKIP_BODY;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag.
     */
    public int doEndTag() throws JspException {
        HttpSession session = pageContext.getSession();

        try {
            IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
            // Retrieve user's data
            if (user == null) {
                logger.error("User was null, exit the tag.");
                return EVAL_PAGE;
            }

            ImageBean imageBean   = user.getImageBean();
            String behaviour      = user.getBehaviour();
            InteractionNetwork in = user.getInteractionNetwork();

            /**
             * Apply an highlight if needed data are available
             */
            if (user.InteractionNetworkReadyToBeHighlighted()) {
                String methodClass = user.getMethodClass();
                HighlightProteins.perform (methodClass, behaviour, session, in) ;
            }

            /**
             *  Display only the picture if needed data are available
             */
            if (user.InteractionNetworkReadyToBeDisplayed()) {
                // Display the HTML code map
                pageContext.getOut().write (imageBean.getMapCode());

                // read the Graph.properties file
                String mapName = null;
                String format = null;
                int imageHeight = 0;
                int imageWidth = 0;
                Properties properties = PropertyLoader.load (uk.ac.ebi.intact.application.hierarchView.business.Constants.PROPERTY_FILE);

                if (null != properties) {
                    mapName = properties.getProperty ("hierarchView.image.map.name");
                    format = properties.getProperty ("hierarchView.image.format.name");
                    String heightStr = properties.getProperty ("hierarchView.image.size.default.image.height");
                    String widthStr  = properties.getProperty ("hierarchView.image.size.default.image.length");
                    imageHeight = Integer.parseInt(heightStr);
                    imageWidth  = Integer.parseInt(widthStr);
                } else {
                    logger.error("Unable to load properties from " +
                                 uk.ac.ebi.intact.application.hierarchView.business.Constants.PROPERTY_FILE);
                }

                /*
                 * Prepare an identifier unique for the generated image name, it will allows
                 * to take advantage of the client side caching.
                 */
                String AC = user.getAC();
                int depth = user.getCurrentDepth();
                String method = user.getMethodClass();
                Collection keys = user.getKeys();
                String highlightContext = "";
                if (keys != null) {
                    // a highlight has been requested
                    highlightContext = (String) keys.iterator().next();
                    // only relevant to add the behaviour if one is applied
                    highlightContext += behaviour;
                }

                String userContext = AC + depth + method + highlightContext;

                /* The context parameter in the URL is also given to prevent some browser
                 * (eg. Netscape 4.7) to cache image wrongly.
                 * If the image link were /hierarchView/GenerateImage, netscape don't even
                 * call the servlet and display cached image.
                 */
                String msg = "<p align=\"left\">\n"
                        + "  <center>"
                        + "     <img src=\"/hierarchView/GenerateImage?format=" + format
                        +        "&context="+ userContext +"\" "
                        + "      USEMAP=\"#" + mapName +"\" width=\""+ imageWidth +"\" "
                        +        "height=\""+ imageHeight +"\"  border =\"0\">"
                        + "     <br>"
                        + "  </center>"
                        + "</p>";

                pageContext.getOut().write (msg);
            }

        } catch (Exception ioe) {
            throw new JspException ("Error: could not display interaction network.");
        }

        return EVAL_PAGE; // the rest of the calling JSP is evaluated
    } // doEndTag
}