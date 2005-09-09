package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvFeatureIdentification;
import uk.ac.ebi.intact.model.CvFeatureType;
import uk.ac.ebi.intact.model.CvInteraction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Draw an example graphs.
 */
public class SampleGraphDraw {

    public static void produceImage( Class cvClass, File file ) throws IntactException, IOException {

        String cvName = cvClass.getName().substring( cvClass.getName().lastIndexOf( "." ) + 1 );

        CvGraph graphGenerator = new CvGraph();

        System.out.print( "Creating "+ cvName +"'s DAG representation..." );

        BufferedImage image = (BufferedImage) graphGenerator.createImage( cvClass );
        String map = graphGenerator.getImageMap();

        System.out.println( "done." );

        // write to disk
        ImageIO.write( image, "png", new FileOutputStream( file ) );

        PrintWriter pw = new PrintWriter( new FileWriter( cvName + ".html" ) );
        pw.println( "<html><body>" +
                    "<img border=\"0\" src=\""+ cvName + ".png\" usemap=\"#"+ cvName +"\" />" +
                    "<map name=\""+ cvName +"\">" + map + "</map>" +
                    "</body></html>" );

        pw.close();
    }

    /**
     * D E M O
     */
    public static void main( String[] args ) throws IntactException, IOException {

        produceImage( CvInteraction.class, new File( "CvInteraction.png" ) );
        produceImage( CvFeatureType.class, new File( "CvFeatureType.png" ) );
        produceImage( CvFeatureIdentification.class, new File( "CvFeatureIdentification.png" ) );
    }
}