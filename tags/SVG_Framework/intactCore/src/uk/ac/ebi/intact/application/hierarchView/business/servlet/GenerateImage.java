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
//import uk.ac.ebi.intact.application.hierarchView.business.Chrono;
import uk.ac.ebi.intact.application.hierarchView.business.image.ConvertSVG;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

/**
 * Purpose: <br>
 * For a bean given in the session, forward an image to be displayed.
 * The image format is parameterized in the Graph.properties file.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class GenerateImage extends HttpServlet {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);
    private final static String ERROR_MESSAGE = "Unable to produce the interaction network, please warn your administrator";

    /**
     * Servlet allowing to get SVG data, convert them into the user wanted format
     * and send to the browser the image by taking care of the MIME type.
     */
    public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse)
            throws ServletException{
        ServletOutputStream out = null;

        try {
            // get the current user session
            HttpSession session = aRequest.getSession ();
            IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
            if (user == null) {
               logger.error ("No user in the session, don't displays interaction network");
                return;
            }

            ImageBean imageBean = user.getImageBean();

            if (null == imageBean) {
                logger.error ("ImageBean in the session is null");
                return;
            }

            // binary output
            out = aResponse.getOutputStream();

            // take the parameter in the request
            String format = aRequest.getParameter("format");

            String className = null;
            Properties propertiesBusiness = PropertyLoader.load (Constants.PROPERTY_FILE);

            if (null != propertiesBusiness) {
                className = propertiesBusiness.getProperty ("hierarchView.image.format." + format + ".class" );
            }

            /*
            * Create a SVG Rasterizer to convert the SVG DOM to an image
            * The ConvertSVG abstract instanciate one of its implementation
            * according to the class name.
            */
            ConvertSVG convert = ConvertSVG.getConvertSVG (className);

            if (null == convert) {
                logger.error ("Unable to create the rasterizer " + className);
                out.println (ERROR_MESSAGE);
            } else {
                logger.info (className + " created");

                // set MIME type according to user format choice
                String typeMime = convert.getMimeType();
                logger.info ("set MIME Type to " + typeMime);
                aResponse.setContentType(typeMime);

                Document document = imageBean.getDocument();
                if (null != document) {
                    try {
//                        Chrono chrono = new Chrono ();
//                        chrono.start();
                        byte[] imageData = convert.convert(document);
//                        chrono.stop();
//                        String msg = "Time for rasterizing the SVG DOM " + chrono;
//                        logger.info(msg);

                        logger.info ("SVG transcoding done");

                        if (null != imageData) {
                            logger.info ("Image file size: " + imageData.length + " bytes");
                            out.write (imageData);
                        }
                        else {
                            logger.error ("No data produced by " + className);
                            out.println (ERROR_MESSAGE);
                        }
                    }
                    catch (Exception e) {
                        logger.error ("Couldn't convert the DOM document", e);
                        out.println (ERROR_MESSAGE);
                    }
                }
                else {
                    logger.error ("SVG DOM document is null");
                    out.println (ERROR_MESSAGE);
                }
            }
            out.flush ();
            out.close ();
        }
        catch (IOException e) {
            logger.error ("Error during the image producing process", e);
            return;
        }
        finally {
            try {
                out.close();
            } catch (IOException ioe) {}
        }
    } // doGet
}


