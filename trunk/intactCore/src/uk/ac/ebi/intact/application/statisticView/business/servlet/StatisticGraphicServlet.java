/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.servlet;

import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import uk.ac.ebi.intact.application.statisticView.business.graphic.GraphSkeleton;
import uk.ac.ebi.intact.application.statisticView.business.StatGraphConstants;
import uk.ac.ebi.intact.application.statisticView.business.Constants;
import org.apache.log4j.Logger;




/**
 * 
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class StatisticGraphicServlet extends HttpServlet {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /**
     * This method create an instance of the right class!
     */
    public static GraphSkeleton getGraphInstance (String aClassName) {

        Object object = null;

        try {
            logger.info("Try to instanciate Graph: " + aClassName);

            // create a class by its name
            Class cls = Class.forName(aClassName);

            // Create an instance of the class invoked
            object = cls.newInstance();

            if (false == (object instanceof GraphSkeleton)) {
                // my object is not from the proper type
                logger.error ("The Graph object generated ("+ aClassName + ")is not a GraphSkeleton");
                return null;
            }

            logger.info("instanciation done");
        } catch (Exception e) {
            logger.error ("Could not The Graph object generated ("+ aClassName + ")is not a GraphSkeleton");
            // nothing to do, object is already setted to null
        }

        return (GraphSkeleton) object;
    } // getGraphInstance


    /**
     * Servlet allowing to get image data, rasterize into JPEG and send
     * to the browser the image by taking care of the MIME type.
     */
    public void doGet (HttpServletRequest aRequest, HttpServletResponse aResponse)
            throws ServletException{

        OutputStream outputStream = null;

        try {
            String type = aRequest.getParameter("TYPE");
            GraphSkeleton graph = getGraphInstance("uk.ac.ebi.intact.application.statisticView.business.graphic.Graph"+type);

            if (graph == null) {
                logger.info("Graph is null");
                aResponse.getOutputStream().println("Could not create graphics.");
                aResponse.getOutputStream().flush();
                return;
            }

            graph.setSizeImage (StatGraphConstants.IMAGE_PROTEIN_WIDTH,
                                StatGraphConstants.IMAGE_PROTEIN_HEIGHT);

            graph.drawSkeleton();
            logger.info("Drawing done");

            BufferedImage image = graph.getImageData();
            outputStream = new BufferedOutputStream(aResponse.getOutputStream(), 1024);

            // Send image to browser, set MIME type
            aResponse.setContentType("image/jpg");

            // JPEG encoding
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
            encoder.encode(image);
            logger.info("Image encoded");

            outputStream.flush();
            outputStream.close();
        }
        catch (IOException ioe) {
            logger.error ("Error during the image producing process, cause: " + ioe.getCause(), ioe);
            /*
            * Could be possible to send back an image saying that we can't show a graph.
            */

        } catch (NoClassDefFoundError se) {
            logger.error ("Could not create graphic probably due to a connection problem with the X server", se);
            /*
             * Could be possible to send back an image saying that we can't show a graph.
             */

        } finally {
            try {
                if (outputStream != null) outputStream.close();
            }
            catch (IOException ioe) {}
        }
        return;
    }
}
