package uk.ac.ebi.interpro.taglibs.servlets;

import com.keypoint.PngEncoderB;
import org.apache.log4j.Category;
import uk.ac.ebi.ego.GoRelation;
import uk.ac.ebi.ego.GoTerm;
import uk.ac.ebi.ego.GoTermFactory;
import uk.ac.ebi.ego.GoTermUnavailableException;
import uk.ac.ebi.factory.FactoryBag;
import uk.ac.ebi.interpro.taglibs.configuration.*;
import uk.ac.ebi.interpro.taglibs.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedString;
import java.util.*;


/**
 * Implements the servlets which renders a Go hierarchy graph.
 * Uses the general GraphLayout class.
 */
public class GoHierarchy {

    /** Use the logging system */
    private static Category log = Category.getInstance(GoHierarchy.class);

    /** Take a BufferedImage and write a png file to output. */
    static void imageDump(OutputStream out, BufferedImage image) throws IOException {

        // Encode, no alpha, no filter, 9=maximum compression.
        PngEncoderB enc = new PngEncoderB(image, false, 0, 9);
        out.write(enc.pngEncode());
    }

    /**
     * A concrete implementation of a hierarchy node, to represent a GO term.
     */

    static class GoTermNodeInfo implements GraphLayout.NodeInfo {

        /**
         *  One node is the primary node, highlighted in green which is the one that the GO page displays
         * information about.
         */

        public final static int PRIMARY=0;

        /**
         * Some nodes may be selected, highlighted partly in blue. The parents of all selected nodes are shown.
         */

        public final static int SELECTED=1;

        /**
         * All other nodes are unselected (shown with a white background).
         */

        public final static int UNSELECTED=2;



        GoTerm term;

        String termText;

        String topUrl,bodyUrl;


        /** mode takes on one of the values PRIMARY, SELECTED, UNSELECTED. */
        int mode;


        // Drawing constants.

         int frameXMargin = 2,frameYMargin = 1,internalXMargin = 10,internalYMargin = 2;

        final Color highlightColor = new Color(192, 192, 255);
        final Color primaryColor = new Color(192, 255, 192);
        final Font accessionFont = new Font("Arial", 0, 8);
        final Font nameFont = new Font("Arial", 0, 10);
        final int topSize = 10;

        public int getWidth() {
            return 120;
        }

        public int getHeight() {
            return 50;
        }

        /**
         * Get the image map code for the go term node.
         * Includes two hotzones, at the top and bottom.
         */

        public String getImageMap(GraphLayout.RenderInfo r, GraphLayout.Node n) {
            int x1 = n.x - n.getWidth() / 2;
            int y1 = n.y + n.nodeTop;
            int y2 = n.y + n.nodeTop + topSize;
            int x2 = x1 + n.getWidth();
            int y3 = n.y + n.nodeBottom;
            String altText = term.getAccession() + " " + term.getName();
 
	    // Modifications for hierarchView
            int x4 = x1 + 30;
            int y4 = y1 + 10;
            String msg = "Highlight " + term.getAccession();
            String url = "http://localhost:8080/hierarchView/source.do?keys=" + term.getAccession();
            String target = "frameView";

            return
	      "<area alt=\"" + altText + "\" title=\"" + altText + "\" shape=\"Rect\" href=\"" + topUrl + "\" coords=\"" + x4 + "," + y4 + " " + x2 + "," + y2 + "\"/>" +
	      "<area alt=\"" + altText + "\" title=\"" + altText + "\" shape=\"Rect\" href=\"" + bodyUrl + "\" coords=\"" + x1 + "," + y2 + " " + x2 + "," + y3 + "\"/>"+
              "<area alt=\"" + msg + "\" title=\"" + msg + "\" shape=\"Rect\" target=\"" + target + "\" href=\"" + url + "\" coords=\"" + x1 + "," + y1 + " " + x4 + "," + y4 + "\"/>";
        }


        /**
         * Render the GO node onto a graphics context
         */

        public void render(Graphics2D g2, GraphLayout.RenderInfo r, GraphLayout.Node n) {
            int yp = n.y + n.nodeTop + frameYMargin;

            if (mode==PRIMARY) {
                g2.setColor(primaryColor);
                g2.fillRect(n.x - n.getWidth() / 2 + frameXMargin, yp, n.getWidth() - frameXMargin * 2, n.nodeBottom - n.nodeTop - frameYMargin * 2);
            }

            if (mode==SELECTED) {
                g2.setColor(highlightColor);
                g2.fillRect(n.x - n.getWidth() / 2 + frameXMargin, yp + topSize, n.getWidth() - frameXMargin * 2, n.nodeBottom - n.nodeTop - frameYMargin * 2 - topSize);
            }

            g2.setColor(Color.blue);
            g2.drawRect(n.x - n.getWidth() / 2 + frameXMargin, yp, n.getWidth() - frameXMargin * 2, n.nodeBottom - n.nodeTop - frameYMargin * 2);

            g2.setColor(Color.red);


            yp += internalYMargin;

            g2.setFont(accessionFont);
            FontMetrics fm;
            Rectangle2D r2;
            fm = g2.getFontMetrics();
            String text = term.getAccession().substring(3);
            r2 = fm.getStringBounds(text, g2);

            yp += fm.getAscent();

            g2.drawString(text, n.x + getWidth() / 2 - 10 - ((int) r2.getWidth()), yp);
            g2.setFont(nameFont);

            yp += fm.getDescent() + fm.getLeading();

	    // our add
            g2.setColor(Color.red);
            g2.fillRect((n.x - n.getWidth() / 2) + frameXMargin * 2, n.y + n.nodeTop + frameYMargin * 2, 30, 10);

            g2.setColor(Color.black);

            text = termText;

            Map attrs = new HashMap();
            attrs.put(TextAttribute.FONT, nameFont);

            LineBreakMeasurer measurer = new LineBreakMeasurer(new AttributedString(text, attrs).getIterator(), g2.getFontRenderContext());
            float wrappingWidth = getWidth() - internalXMargin * 2;

            int xp = n.x;
            int limit = 4,count = 0;

            while (measurer.getPosition() < text.length()) {

                count++;

                TextLayout layout = measurer.nextLayout(wrappingWidth);

                yp += (layout.getAscent());

                if (yp + layout.getDescent() > n.y + n.nodeBottom - internalYMargin) break;

                layout.draw(g2, xp - layout.getAdvance() / 2, yp);
                yp += layout.getDescent() + layout.getLeading();
            }

        }


        /**
         * Create a GO Term node in the graph.
         * @param term          The GO term
         * @param mode          PRIMARY, SELECTED or UNSELECTED
         * @param topUrl        The URL for the top line of the node
         * @param bodyUrl       The URL for the body of the node
         */

        GoTermNodeInfo(GoTerm term, int mode, String topUrl, String bodyUrl) {
            this.term = term;
            this.mode = mode;

            this.termText = term.getName();
            this.topUrl = topUrl;
            this.bodyUrl = bodyUrl;
        }
    }


    static Set getTermParents(Set terms, Set allTerms) throws GoTermUnavailableException {

        Set create = new HashSet();

        for (Iterator i = terms.iterator(); i.hasNext();) {
            GoTerm term = (GoTerm) i.next();
            GoTerm[] newTerms = term.getParents();

            for (int j = 0; j < newTerms.length; j++) {
                if (allTerms.contains(newTerms[j])) continue;
                if (create.contains(newTerms[j])) continue;
                create.add(newTerms[j]);
            }
        }

        return create;
    }


    static Set getTermChildren(Set terms, Set allTerms) throws GoTermUnavailableException {

        Set create = new HashSet();

        for (Iterator i = terms.iterator(); i.hasNext();) {
            GoTerm term = (GoTerm) i.next();
            GoTerm[] newTerms = term.getChildren();

            for (int j = 0; j < newTerms.length; j++) {
                if (allTerms.contains(newTerms[j])) continue;
                if (create.contains(newTerms[j])) continue;
                create.add(newTerms[j]);
            }
        }

        return create;
    }

    static void getTerms(Set append, GoTerm initial) throws GoTermUnavailableException {

        append.add(initial);

        /*

        Commented out section to support inclusion of children in Go term graph.

       Set parents = new HashSet();
       parents.add(initial);

       while (true) {
           Set newTerms = getTermChildren(parents, append);

           //if (newTerms.size()>8) break;
           if (newTerms.size() == 0) break;

           append.addAll(newTerms);
           parents = newTerms;

           break;
       }

       */



        Set children = new HashSet();
        children.add(initial);

        while (true) {
            Set newTerms = getTermParents(children, append);

            if (newTerms.size() == 0) break;

            append.addAll(newTerms);
            children = newTerms;
        }

    }

    static Set getTerms(Set initial) throws GoTermUnavailableException {
        Set terms = new HashSet();
        for (Iterator i = initial.iterator(); i.hasNext();) {
            GoTerm term = (GoTerm) i.next();
            getTerms(terms, term);
        }
        return terms;
    }

    static void findParentRelations(GoTerm term, Map termToNode, GraphLayout gl) throws GoTermUnavailableException {
        GraphLayout.Node child = (GraphLayout.Node) termToNode.get(term);
        log.debug("Node  " + term.getAccession() + " " + (child == null));
        if (child == null) return;
        GoRelation[] parentRelations = term.getParentRelations();
        for (int i = 0; i < parentRelations.length; i++) {
            GoTerm parentTerm = parentRelations[i].getParent();
            GraphLayout.Node parent = (GraphLayout.Node) termToNode.get(parentTerm);
            log.debug("Found edge " + term.getAccession() + "->" + parentTerm.getAccession() + " " + (parent == null));
            if (parent == null) continue;

            Color color;

            switch (parentRelations[i].getRelationType()) {
                case GoRelation.IS_A:
                    color = Color.black;
                    break;
                case GoRelation.PART_OF:
                    color = Color.red;
                    break;
                default:
                    color = Color.blue;
                    break;
            }


            gl.newEdge(parent, child, gl.new SplineEdgeInfo(color));
        }
    }

    static GraphLayout getTermGraph(GoTerm primary, Set initial, String url, String selectedParam, String extraParam) throws GoTermUnavailableException {


        GraphLayout gl = new GraphLayout();


        StringBuffer idList = new StringBuffer();


        Set terms = getTerms(initial);

        Map termToNodes = new HashMap();


        for (Iterator i = terms.iterator(); i.hasNext();) {

            GoTerm term = (GoTerm) i.next();


            int mode= GoTermNodeInfo.UNSELECTED;



            idList.setLength(0);


            for (Iterator j = initial.iterator(); j.hasNext();) {
                GoTerm term2 = (GoTerm) j.next();

                if (term2 != term)
                    idList.append(term2.getAccession()).append(",");
                else
                    mode= GoTermNodeInfo.SELECTED;
            }


            if (term==primary) mode=GoTermNodeInfo.PRIMARY;


            String selected = term.getAccession();

            String topUrl = url + "?" + selectedParam + "=" + selected + "&amp;" + extraParam + "=" + idList.toString();
            String bodyUrl;


            if (mode!=GoTermNodeInfo.SELECTED) {
                bodyUrl = topUrl;
            } else {
                bodyUrl = url + "?" + selectedParam + "=" + primary.getAccession() + "&amp;" + extraParam + "=" + idList.toString();
            }


            termToNodes.put(term,
                    gl.newNode(
                            new GoTermNodeInfo(term, mode, topUrl, bodyUrl)
                    ));

        }

        log.debug("Found nodes: " + termToNodes.size());

        for (Iterator i = terms.iterator(); i.hasNext();)
            findParentRelations((GoTerm) i.next(), termToNodes, gl);

        return gl;

    }






    public static String getHierarchyGraph(Configuration configuration, ServletContext servletContext, GoTerm primary, Set terms, String useURL, String selectedParam, String listParam) throws Exception {

        FactoryBag factoryBag = configuration.factoryBag;


        long time = System.currentTimeMillis();
        log.debug("Started");


        GraphLayout gl = getTermGraph(primary, terms, useURL, selectedParam, listParam);

        log.debug("Got term graph: " + (System.currentTimeMillis() - time));


        gl.layout();

        log.debug("Lay out term graph: " + (System.currentTimeMillis() - time));


        int width = gl.getWidth();
        int height = gl.getHeight();


        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        log.debug("Image for term graph: " + (System.currentTimeMillis() - time));


        Graphics2D g2 = image.createGraphics();


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);


        g2.setColor(Color.white);

        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.black);

        log.debug("Ready 2 Render: " + (System.currentTimeMillis() - time));
        gl.render(g2);
        log.debug("Rendered: " + (System.currentTimeMillis() - time));


        String uniqueName = "go-img-" + System.currentTimeMillis() + ".png";

        String fileName = servletContext.getRealPath("/image-temp") + "/" + uniqueName;
        String imgLocation = "image-temp/" + uniqueName;


        log.debug("Ready to dump: " + (System.currentTimeMillis() - time));
        OutputStream os = new BufferedOutputStream(new FileOutputStream(fileName));
        imageDump(os, image);
        log.debug("Image dumped: " + (System.currentTimeMillis() - time));

        return gl.getImageMap() + "<img usemap=\"#graphzones\" src=\"" + imgLocation + "\" border=\"0\"/>";

    }


    public static void main(String[] args) {


    }

}









