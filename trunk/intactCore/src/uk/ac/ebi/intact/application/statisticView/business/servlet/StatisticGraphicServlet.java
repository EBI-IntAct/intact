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




/**
 * 
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class StatisticGraphicServlet extends HttpServlet {

    //static Logger logger = Logger.getLogger ("statisticView");
     //  private final static String ERROR_MESSAGE = "Unable to produce the interaction network, please warn your administrator";

    /**
     * This method create an instance of the right class!
     */
      public static GraphSkeleton getGraphInstance (String aClassName) {

        Object object = null;

        try {

            // create a class by its name
            Class cls = Class.forName(aClassName);

            // Create an instance of the class invoked
            object = cls.newInstance();

            if (false == (object instanceof GraphSkeleton)) {
                // my object is not from the proper type
                return null;
            }
        } catch (Exception e) {
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

           // get the current user session
                HttpSession session = aRequest.getSession ();
            /*    IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
                if (user == null) {
                    aResponse.getOutputStream().print(ERROR_MESSAGE);

                    logger.error ("No user in the session, don't displays interaction network");
                    return;
                }

                ImageBean imageBean = user.getImageBean();

                if (null == imageBean) {
                    logger.error ("ImageBean in the session is null");
                    return;
                }
                  */

           String type = aRequest.getParameter("TYPE");
           GraphSkeleton graph = getGraphInstance("uk.ac.ebi.intact.application.statisticView.business.graphic.Graph"+type);
           if (graph == null) {
                System.err.println("none instance created");
           }

           //GraphSkeleton graph = new GraphProtein()
          graph.setSizeImage(StatGraphConstants.IMAGE_PROTEIN_WIDTH,
                                                    StatGraphConstants.IMAGE_PROTEIN_HEIGHT);


        // create the graph and initialize the graph size
        //graph = new GraphProtein();
        graph.drawSkeleton();

        //graph.drawCurve();

        BufferedImage image = graph.getImageData();

        outputStream = new BufferedOutputStream(aResponse.getOutputStream(), 1024);

        // Send image to browser
        aResponse.setContentType("image/jpg");

           // JPEG encoding
           // JPEGEncodeParam  jpegEncodeParam  = null;
           // jpegEncodeParam  = JPEGCodec.getDefaultJPEGEncodeParam (image);
       /*
        *  Encode the produced image in JPEG.
        *   Quality range : 0.0 .. 1.0
        *   0.7  : high quality   (good compromise between file size and quality)
        *   0.5  : medium quality
        *   0.25 : low quality
        */
        //jpegEncodeParam.setQuality (0.7F, false);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
        encoder.encode(image);

        outputStream.close();

        }
        catch (IOException e) {
          try {
                outputStream.close();
            }
            catch (IOException ioe) {}
            //logger.error ("Error during the image producing process", e);
            return;
        }

    }
}
