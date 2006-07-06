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
import uk.ac.ebi.intact.application.goDensity.business.binaryInteractions.GoGoDensity;
import uk.ac.ebi.intact.application.goDensity.business.dag.FastGoDag;
import uk.ac.ebi.intact.application.goDensity.exception.GoIdNotInDagException;
import uk.ac.ebi.intact.application.goDensity.exception.SameGraphPathException;
import uk.ac.ebi.intact.application.goDensity.setupGoDensity.Config;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.text.DecimalFormat;

/**
 * Image rendering class for go-go-densities
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class DensityImage {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    static Logger logger = Logger.getLogger("goDensity");

    /**
     * Buffered Image for output
     */
    private BufferedImage _image;

    /**
     * Image map which is used on the html site where the figure is represented
     */
    private String _javaScriptCode;

    /**
     * goIds along the axis
     */
    private Vector _axes;

    /**
     * Fullname of the GO:Ids along the axes (used for tooltip)
     */
    private String[] _goTermsFull;

    /**
     * - true: image will be rendered in color (heat gradient)<br>
     * - false: image will be rendered in grayscale
     */
    private boolean _isColor;

    /**
     * max value of interaction density
     */
    private double _max;

    /**
     * min value of interaction density
     */
    private double _min;

    /**
     * the interaction densities for all go-go-pairs
     */
    private double[][] _densities; // 0 to 1 normalized

    /**
     * the possible interactions between goIds
     */
    private int[][] _possibleIA;

    /**
     * the existing interactions between goIds
     */
    private int[][] _isIA;

    /**
     * size x and y for one colored cluster
     */
    private double clusterSize;

    /**
     * image size for x and y
     */
    private final int _imageSizeX = Config.IMAGeSIZE;
    private final int _imageSizeY = _imageSizeX;

    /**
     * offset on the left and on the bottom of the image
     */
    private final int _bigOffset = Config.BIgOFFSET;

    /**
     * offset on the right and on the top of the image
     */
    private final int _smallOffset = Config.SMALlOFFSET;

    // =======================================================================
    // Constructor
    // =======================================================================

    private DensityImage() {
    };

    /**
     * constructor for a density image
     * @param interactions GoGoDensity interaction data (FastGoDag.getBinaryInteractions())
     * @param color switch for grayscale and heatgradient image  <br>
     * - true -> color<br>
     * - false -> black&white
     */
    public DensityImage(GoGoDensity[][] interactions, boolean color) {

        _image = new BufferedImage(_imageSizeX, _imageSizeY, BufferedImage.TYPE_INT_RGB);
        _densities = new double[interactions.length][interactions.length];
        _isIA = new int[interactions.length][interactions.length];
        _possibleIA = new int[interactions.length][interactions.length];
        _axes = new Vector();
        _isColor = color;
        _max = 0;
        _min = 1;


        // fill the local _densities array with the equivalent values and
        // determine the max and min value of interaction density and store
        // it in _min and _max
        for (int i = 0; i < interactions.length; i++) {

            GoGoDensity interaction = interactions[i][interactions.length - 1];
            _axes.add(interaction.getGoId1());

            for (int j = i; j < interactions.length; j++) {

                _isIA[i][j] = interactions[i][j].getCoutInteractions();
                _possibleIA[i][j] = interactions[i][j].getCountPossibleIAs();

                double value = interactions[i][j].getDensity();
                _densities[i][j] = value;

                if (_max < value) {
                    _max = value;
                }
                if (_min > value && value != -1) { // -1 error code for no density data!
                    _min = value;
                }
                if (_min == _max) { // happens if there is only one singel goId
                    _min = 0;
                }

                logger.debug(interactions[i][j].getGoId1() + " " + interactions[i][j].getGoId2());
                logger.debug("value = " + value);
            }
        }

        // if no density data is available
        if (_min == 1 && _max == 0) {
            _min = 0;
            _max = 0;
        }

        // normalize: _min to _max to 0 ... 1
        logger.debug("_max = " + _max);
        logger.debug("_min = " + _min);

        double newmax = _max - _min;
        logger.debug("newmax = " + newmax);

        double factor;
        if (newmax > 0)
            factor = 1 / newmax;
        else
            factor = 1;
        logger.debug("factor = " + factor);

        // calculate normalized densities (min will be 0 and max will be 1)
        for (int i = 0; i < _densities.length; i++) {
            for (int j = i; j < _densities[i].length; j++) {
                if (_densities[i][j] != -1) {
                    _densities[i][j] = (_densities[i][j] - _min) * factor;
                    logger.debug("_densities[" + i + "][" + j + "] = " + _densities[i][j]);
                }
            }
        }
        _goTermsFull = new String[_axes.size()];
        this.generate();
    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * renders the whole content of the picture
     */
    private void generate() {

        Graphics2D g2d = _image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ------------------------------------------------------------------
        // set Background of _image white

        g2d.setBackground(Color.white);
        g2d.setColor(Color.white);
        g2d.fill(new Rectangle2D.Float(0, 0, _imageSizeX, _imageSizeY));


        // clusterSize is the size of on go-go-colored-fields
        clusterSize = (_imageSizeX - _bigOffset - _smallOffset) / (double) _axes.size();

        // -------------------------------------------------------------------
        // draw colored fields (densities) of the picture

        g2d.setColor(Color.black);

        if (_axes.size() > 1) { // rectangles
            for (int i = 0; i < _densities.length; i++) { // x-direction
                for (int j = i; j < _densities[i].length; j++) { // y-direction
                    double value = _densities[i][j];
                    if (value != -1) { // -1 error code for no data
                        setClusterColor(value, g2d);
                        g2d.fill(
                                new RoundRectangle2D.Double(
                                        _bigOffset + 3 + (i * clusterSize),
                                        _smallOffset + 3 + (j * clusterSize),
                                        clusterSize - 5.5,
                                        clusterSize - 5.5,
                                        6,
                                        6));
                    } else {
                        g2d.setColor(Color.DARK_GRAY);

                        g2d.drawLine((int) (_bigOffset + 3 + i * clusterSize),
                                     (int) (_smallOffset + 3 + j * clusterSize),
                                     (int) (_bigOffset - 3 + i * clusterSize + clusterSize),
                                     (int) (_smallOffset - 3 + j * clusterSize + clusterSize));

                        g2d.drawLine((int) (_bigOffset - 3 + i * clusterSize + clusterSize),
                                     (int) (_smallOffset + 3 + j * clusterSize),
                                     (int) (_bigOffset + 3 + i * clusterSize),
                                     (int) (_smallOffset - 3 + j * clusterSize + clusterSize));
                    }
                }
            }
        } else { // one single triangle
            double value = _densities[0][0];
            if (value != -1) {
                setClusterColor(value, g2d);
                int[] xpoints = {(_bigOffset + 4),
                                 (_bigOffset + 4),
                                 (_bigOffset + 4 + (int) clusterSize - 12)};
                int[] ypoints = {(_smallOffset - 3 + (int) clusterSize),
                                 (_smallOffset - 3 + 12),
                                 (_smallOffset - 3 + (int) clusterSize)};
                g2d.fill(new Polygon(xpoints, ypoints, 3));
            } else {
                g2d.setColor(Color.RED);
                g2d.drawString("NO data",
                               (_bigOffset + 5),
                               (_smallOffset + 13));
            }
        }


        // -------------------------------------------------------------------
        // draw color legend on the right

        g2d.setColor(Color.black);

        int rectWidth = 30;
        int rectHeight = 100;
        int x = _imageSizeX - _smallOffset - rectWidth;
        int y = (int) (_smallOffset * 3.3);

        for (int i = 0; i < rectHeight; ++i) {
            float value = (float) i / rectHeight;
            if (_isColor) {
                float f = value / 1.47f;
                g2d.setColor(Color.getHSBColor(f, 1f, 0.9f));
            } else {
                int rgb = Math.abs((int) (value * 255));
                g2d.setColor(new Color(rgb, rgb, rgb));
            }
            g2d.drawLine(x, y + i, x + rectWidth, y + i);
        }

        // max text legend
        g2d.setPaint(Color.black);
        g2d.drawString("density in %", _imageSizeX - _smallOffset - 2 * rectWidth, _smallOffset * 1.5f);

        DecimalFormat df = new DecimalFormat("0.#E0");

        String max = df.format((new Double(_max * 100)).doubleValue());
        g2d.drawString(max, _imageSizeX - _smallOffset - 2.7f * rectWidth, _smallOffset * 3);

        String mid = df.format((new Double(_max * 50)).doubleValue());
        g2d.drawString(mid, _imageSizeX - _smallOffset - 2.7f * rectWidth, _smallOffset * 3.5f + rectHeight / 2);

        String min = df.format((new Double(_min * 100)).doubleValue());
        g2d.drawString(min, _imageSizeX - _smallOffset - 2.7f * rectWidth, _smallOffset * 4 + rectHeight);

        // -------------------------------------------------------------------
        // draw axes and lines

        String[] goTermsShort = new String[_axes.size()];

        // fill goTermsShort and goTermsFull for tooltips
        for (int i = 0; i < _axes.size(); i++) {
            String goId = (String) _axes.get(i);
            FastGoDag dag = FastGoDag.getInstance();
            goTermsShort[i] = dag.getGoTermShort(goId);
            _goTermsFull[i] = dag.getGoTermFullname(goId);
        }

        for (int i = 0; i < _axes.size(); i++) {

            if (_axes.size() > 1) {
                // vertical line right side
                g2d.draw(
                        new Line2D.Double(
                                _bigOffset + clusterSize + (i * clusterSize),
                                _smallOffset + (i * clusterSize),
                                _bigOffset + clusterSize + (i * clusterSize),
                                _imageSizeY - _bigOffset));
            } else {
                // diagonal line for triangle at _axes.size() = 1
                g2d.draw(
                        new Line2D.Double(_bigOffset, _smallOffset, _bigOffset + clusterSize, _imageSizeY - _bigOffset));
            }

            // vertical line left side
            g2d.draw(
                    new Line2D.Double(_bigOffset, _smallOffset, _bigOffset, _imageSizeY - _bigOffset));

            if (_axes.size() > 1) {
                // horizontal line top
                g2d.draw(
                        new Line2D.Double(
                                _bigOffset,
                                _smallOffset + (i * clusterSize),
                                _bigOffset + clusterSize + (i * clusterSize),
                                _smallOffset + (i * clusterSize)));
            }

            // horizontal line bottom
            g2d.draw(
                    new Line2D.Double(
                            _bigOffset,
                            _imageSizeY - _bigOffset,
                            _imageSizeX - _smallOffset,
                            _imageSizeY - _bigOffset));

            // y-axis text
            g2d.drawString(
                    goTermsShort[i],
                    _smallOffset,
                    (int) (_smallOffset + (i * clusterSize) + (clusterSize / 2.0)));
        }

        // x-axis text
        for (int i = 0; i < _axes.size(); i++) {
            g2d.setTransform(
                    AffineTransform.getRotateInstance(
                            -Math.PI / 2,
                            _bigOffset + (i * clusterSize) + (clusterSize / 2.0),
                            _imageSizeY - 10));
            g2d.drawString(
                    goTermsShort[i],
                    (int) (_bigOffset + (i * clusterSize) + (clusterSize / 2.0)),
                    _imageSizeY - 10);
        }

    }

    private void setClusterColor(double value, Graphics2D g2d) {
        // HSB color model is used. But only from 0 to 0.68 instead of 0 to 1
        // 0 equals red, 0.16 yellow, 0.33 green, 0.50 turquoise, 0.68 blue
        // (0.68 - value) because 0 should equal blue and 0.68 red
        if (_isColor) {
            float hsb = 0.68f - (float) value / 1.47f;
            logger.debug("value (density) = " + value);
            logger.debug("hsb for value = " + hsb);
            Color hsbColor = Color.getHSBColor(hsb, 1.0f, 0.9f);
            g2d.setColor(hsbColor);
        } else {
            int rgb = Math.abs((int) (245 - value * 255));
            g2d.setColor(new Color(rgb, rgb, rgb));
        }
    }

    /**
     * save the jpg image to a specified path
     * @param path path where the image should be stored
     */
    public void saveImage(String path) {
        try {
            FileOutputStream outFile = new FileOutputStream("/home/markus/density.jpg");
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outFile);

            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(_image);
            param.setQuality(0.9f, false);
            encoder.setJPEGEncodeParam(param);

            encoder.encode(_image);

            outFile.close();
        } catch (FileNotFoundException e) {
            logger.error("error: " + e);
        } catch (IOException e) {
            logger.error("error: " + e);
        }
    }

    /**
     * @return an ImageMap for the image, which make a link for each cluster.
     * Each link should forward to a site, where the go-children of the actual
     * go cluster are displayed
     */
    public String getImageMap(String applicationPathURL) {
        if (_javaScriptCode != null)
            return _javaScriptCode.toString(); //cached
        else {
            StringBuffer map = new StringBuffer("\n\n<map NAME=\"go\">");
            StringBuffer tooltips = new StringBuffer("\n<script LANGUAGE=\"JavaScript\" TYPE=\"text/javascript\">\n<!--");

            // imagemap & tooltip for the clusters itself to get the children
            for (int i = 0; i < _axes.size(); i++) {     // x-direction
                int left = (int) (_bigOffset + (i * clusterSize));
                int right = (int) (left + clusterSize);
                String goId1 = (String) _axes.get(i);
                for (int j = i; j < _axes.size(); j++) { // y-direction
                    if (_densities[i][j] != -1) {  // error code -1

                        int top = (int) (_smallOffset + (j * clusterSize));
                        int bottom = top + (int) clusterSize;
                        String goId2 = (String) _axes.get(j);

                        Double realDensity;
                        if (_isIA[i][j] == 0)
                            realDensity = new Double(0);
                        else
                            realDensity = new Double((double) _isIA[i][j] / (double) _possibleIA[i][j] * 100);

                        DecimalFormat df = new DecimalFormat("0.#E0");
                        String dens = df.format(realDensity.doubleValue());

                        tooltips.append("\nmaketip('" + goId1 + goId2 + "','" + goId2 + " & " + goId1 + "'," +
                                        "\'<div align=\"center\">click box to explore child terms" +
                                        " <hr noshade=\"noshade\" width=\"97%\" size=\"1\"></div>" +
                                        " Interaction density of <b>" + dens + " %</b> between <br>" +
                                        goId2 + " and " + goId1 + " and all <br>" +
                                        " their children (exclusive overlapping).<br> " +
                                        " <b>" + _isIA[i][j] + "</b> observed interactions within<br>" +
                                        " <b>" + _possibleIA[i][j] + "</b> possible interactions\');");

                        String link = applicationPathURL +
                                "/children.do?goId1=" + goId1 +
                                "&goId2=" + goId2;

                        map.append("\n<area SHAPE=RECT COORDS=\"" + (left + 3) + "," +
                                   (top + 3) + "," + (right - 3) + "," + (bottom - 3) +
                                   "\" HREF=\"" + link + "\"" +
                                   "  onMouseOver=\"tip('" + goId1 + goId2 + "')\" onMouseOut=\"untip()\"\">");
                    }
                }
            }

            // imagemap & tooltip for the y-acis (goIds)
            // y-axis image map
            for (int i = 0; i < _axes.size(); i++) {
                String goId = (String) _axes.get(i);
                String link = "http://www.ebi.ac.uk/ego/gohierarchy?code=" + goId + "," + goId;
                String ahref = "javascript:PopupPic(\'" + link + "\')";

                tooltips.append("\nmaketip('" + goId + "','Term ID " + goId + "','<div align=\"center\">click GO term to see hierarchy" +
                                " <hr noshade=\"noshade\" width=\"97%\" size=\"1\">" +
                                " Name:<br>" + _goTermsFull[i] + "</div>');");

                map.append("\n<area SHAPE=RECT COORDS=\"" + _smallOffset + "," +
                           (int) (_smallOffset - 10 + (i * clusterSize) + (clusterSize / 2.0)) + "," +
                           _bigOffset + "," +
                           (int) (_smallOffset + (i * clusterSize) + (clusterSize / 2.0)) +
                           "\" HREF=\"" + ahref + "\"" +
                           " onMouseover=\"tip('" + goId + "')\" onMouseOut=\"untip()\"\">");
            }

            // imagemap & tooltip for the x-acis (goIds)
            // x-axis image map
            for (int i = 0; i < _axes.size(); i++) {
                String goId = (String) _axes.get(i);
                String link = "http://www.ebi.ac.uk/ego/gohierarchy?code=" + goId + "," + goId;
                String ahref = "javascript:PopupPic(\'" + link + "\')";

                map.append("\n<area SHAPE=RECT COORDS=\"" + (_bigOffset - 10 + i * clusterSize + clusterSize / 2) + "," +
                           (int) (_smallOffset + (_axes.size() * clusterSize)) + "," +
                           (_bigOffset + i * clusterSize + clusterSize / 2) + "," +
                           (int) (_smallOffset + _bigOffset + (_axes.size() * clusterSize)) + "," +
                           "\" HREF=\"" + ahref + "\"" +
                           " onMouseover=\"tip('" + goId + "')\" onMouseOut=\"untip()\"\">");
            }


            tooltips.append("\n//-->\n</script>");
            map.append("\n</map>");
            tooltips.append(map.toString());
            _javaScriptCode = tooltips.toString();
            return _javaScriptCode;
        }
    }

    /**
     * @return the BufferedImage
     */
    public BufferedImage getBufferedImage() {
        return _image;
    }

    // =======================================================================
    // Test methods
    // =======================================================================

    public static void main(String[] s) {

        List x = new ArrayList();
        //x.add("GO:0005829");
        //x.add("GO:0005783");

        x.add("GO:0005929");
        //x.add("GO:0005634");
        //x.add("GO:0005737");
        //x.add("GO:0005737");
        //x.add("GO:0005686");

        FastGoDag dag = FastGoDag.getInstance();
        GoGoDensity[][] binaryInteractions = new GoGoDensity[0][];


        try {
            binaryInteractions = dag.getBinaryInteractions(x, true);
        } catch (GoIdNotInDagException e) {
            e.printStackTrace();
        } catch (SameGraphPathException e) {
            e.printStackTrace();
        }

        //binaryInteractions[0][0].getCountPossibleIAs();


        DensityImage image = new DensityImage(binaryInteractions, true);
        image.saveImage("/home/markus/density.jpg");
        System.out.println("Picture was rendered - see file output path ...");
    }
}
