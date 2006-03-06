/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.goDensity.business.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class DensityImageServlet extends HttpServlet {

    static Logger logger = Logger.getLogger("goDensity");

    /**
     * Servlet allowing to get image data, rasterize into JPEG and send
     * to the browser the image by taking care of the MIME type.
     */
    public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse)
            throws ServletException {

        OutputStream outputStream = null;
        BufferedImage buffer = null;
        try {
            HttpSession session = aRequest.getSession(false);

            if (session == null) {
                logger.error("session fails");
            } else {

                if (session.getAttribute("bufferedImage") == null) {
                    logger.error("bufferedImage fails");
                } else {
                    buffer = (BufferedImage) session.getAttribute("bufferedImage");
                }
            }

            //DensityImage image = new DensityImage(dens);
            //BufferedImage buffer = image.getBufferedImage();
            outputStream = new BufferedOutputStream(aResponse.getOutputStream(), 1024);
            aResponse.setContentType("image/jpg");

            // JPEG encoding
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(buffer);
            param.setQuality(0.95f, false);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(buffer);
            outputStream.flush();
            outputStream.close();
            logger.info("Image encoded");

        } catch (IOException ioe) {
            logger.error("Error during the image producing process, cause:");
        } catch (NoClassDefFoundError se) {
            logger.error("Could not create graphic probably due to a connection problem with the X server");
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
            }
        }

        return;
    }
}
