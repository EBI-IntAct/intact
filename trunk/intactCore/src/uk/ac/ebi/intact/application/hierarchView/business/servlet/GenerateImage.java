/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.servlet;

import org.w3c.dom.Document;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.image.ConvertSVG;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

/**
 Purpose: <br>
 For a bean given in the session, forward an image (png) to be displayed.
 */
public class GenerateImage extends HttpServlet {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /**
     * Servlet allowing to get SVG data, convert them into the user wanted format
     * and send to the browser the image by taking care of the MIME type.
     */
    public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse)
            throws ServletException{

        try {
            // get the current user session
            HttpSession session = aRequest.getSession ();
            IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
            ImageBean imageBean = user.getImageBean();

            if (null == imageBean) {
                logger.error ("ImageBean in the session is null");
                return;
            }

            // binary output
            ServletOutputStream out = aResponse.getOutputStream();

            // take the parameter in the request
            String format = aRequest.getParameter("format");

            String className = null;
            Properties propertiesBusiness = PropertyLoader.load (Constants.PROPERTY_FILE);

            if (null != propertiesBusiness) {
                className = propertiesBusiness.getProperty ("hierarchView.image.format." + format + ".class" );
            }

            // Create a SVG Rasterizer to convert the SVG DOM to an image
            ConvertSVG convert = ConvertSVG.getConvertSVG (className);

            if (null == convert) {
                logger.error ("Unable to create the rasterizer " + className);
                String errorMsg = "Unable to produce the interaction network, please warn your administrator";
                out.println (errorMsg);
                out.flush ();
                out.close ();
            }

            logger.info (className + " created");

            // set MIME type according to user format choice
            String typeMime = convert.getMimeType();
            logger.info ("set MIME Type to " + typeMime);
            aResponse.setContentType(typeMime);

            Document document = imageBean.getDocument();
            if (null != document) {
                try {
                    byte[] imageData = convert.convert(document);
                    logger.info ("SVG transcoding done");

                    if (null != imageData) {
                        logger.info ("Image file size: " + imageData.length + " bytes");
                        out.write (imageData);
                    }
                    else logger.error ("No data produced by " + className);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    logger.error ("Couldn't convert the DOM document", e);
                }
            }
            else logger.error ("SVG DOM document is null");

            out.flush ();
            out.close ();
        }
        catch (IOException e) {
            logger.error ("Error during the image producing process", e);
            return;
        }

    } // doGet

} // GenerateImage


