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
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
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

    // * Public servlet methods

    public void init(ServletConfig config)
            throws ServletException {

        // MANDATORY!
        super.init(config);
    }


    /**
     *
     *
     *
     */
    public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse)
            throws ServletException{

        try
        {
            // binary output
            ServletOutputStream out = aResponse.getOutputStream();

            // take the parameter in the request
            String format = aRequest.getParameter("format");

            String className = null;
            Properties propertiesBusiness = PropertyLoader.load (Constants.PROPERTY_FILE);

            if (null != propertiesBusiness) {
                className = propertiesBusiness.getProperty ("hierarchView.image.format." + format + ".class" );
            }

            ConvertSVG convert = ConvertSVG.getConvertSVG(className);
            logger.info (className + " created");

            // Encode the off-screen image into a JPEG and send it to the client
            String typeMime = convert.getMimeType();
            logger.info ("set MIME Type to " + typeMime);
            aResponse.setContentType(typeMime);

            // get the current user session
            HttpSession session = aRequest.getSession();
            ImageBean ib = (ImageBean) session.getAttribute (StrutsConstants.ATTRIBUTE_IMAGE_BEAN);

            if (null == ib) {
                logger.error ("ImageBean in the session is null");
                return;
            }

            Document document = ib.getDocument();
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
                    logger.error ("Couldn't convert the DOM document");
                }
            }
            else logger.error ("SVG DOM document is null");

            out.flush ();
            out.close ();
        }
        catch (IOException e) {return;}

    } // doGet

} // GenerateImage


