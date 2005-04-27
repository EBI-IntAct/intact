package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw;

import uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw.graph.*;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDagObject;
import uk.ac.ebi.intact.model.CvInteraction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Draw an example graph.
 * <p/>
 * After running see: <a href="../../../../../../InterGraph.html">results</a>.
 */
public class SampleGraphDraw {


    public static void main(String[] args) {
        CvGraph test = new CvGraph();
        BufferedImage image = null;
        String imageMap = null;

        PrintWriter pw = null;
        try {
            image = (BufferedImage) test.createImage("CvInteraction");
            imageMap = test.getImageMap();
            pw = new PrintWriter(new FileWriter("InterGraph.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ImageIO.write(image, "png", new FileOutputStream("interGraph.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pw.println("<html><body><img src='interGraph.png' usemap='#bob' /><map name='bob'>" + imageMap + "</map></body></html>");
        pw.close();
    }

}
