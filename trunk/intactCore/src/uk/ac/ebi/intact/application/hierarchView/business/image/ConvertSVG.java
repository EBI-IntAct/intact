package uk.ac.ebi.intact.application.hierarchView.business.image;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.servlet.http.*;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;


/**
 * Abstract class allowing convert a SVG document to an other format (JPEG, TIFF, PNG)
 *
 * @author Emilie FROT
 */

public abstract class ConvertSVG {


/**
   * Provides a implementation of ConvertSVG by its name.
   * for example you have an implementation of this abstract class called : <b>JPEGConvertSVG</b>.
   *      so, you could call the following method to get an instance of this class :
   *      <br>
   *      <b>ConvertSVG.getConvertSVG ("mypackage.JPEGConvertSVG");</b>
   *      <br>
   *      then you're able to use methods provided by this abstract class without to know
   *      what implementation you are using.
   *
   * @param aClassName the name of the implementation class you want to get
   * @return an ConvertSVG object, or null if an error occurs.
   */
  public static ConvertSVG getConvertSVG (String aClassName) {

    Object object = null;

    try {

      // create a class by its name
      Class cls = Class.forName(aClassName);

      // Create an instance of the class invoked
      object = cls.newInstance();

    } catch (Exception e) {
      // nothing to do, object is already setted to null
    }

    return (ConvertSVG) object; 

  } // getConvertSVG

/**
 * Convert an object Document to a byte []
 *
 */
  abstract public byte[] convert(Document doc) throws Exception ;

/**
 * Give the MIME type
 *
 * @return String
 */
  abstract public String getMimeType();

} // ConvertSVG

