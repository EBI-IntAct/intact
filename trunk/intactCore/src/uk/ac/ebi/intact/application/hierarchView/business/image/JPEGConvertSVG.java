package uk.ac.ebi.intact.application.hierarchView.business.image;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Interface allowing convert a SVG document to an other format (JPEG, TIFF, PNG)
 *
 * @author Emilie FROT
 */

public class JPEGConvertSVG extends ConvertSVG {

/**
 * Convert an object Document to a byte []
 *
 */
  public byte[] convert(Document doc) throws Exception {
   
    if (null == doc) System.out.println("document is null");

    JPEGTranscoder t = new JPEGTranscoder();
    t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
			 new Float(.8));

    TranscoderInput input   = new TranscoderInput(doc);
    OutputStream ostream    = new ByteArrayOutputStream();
    TranscoderOutput output = new TranscoderOutput(ostream);

    t.transcode(input, output);
    
    ostream = output.getOutputStream();
    ByteArrayOutputStream baostream = (ByteArrayOutputStream) ostream;
    ostream.flush();
    byte[] imageData = baostream.toByteArray();

    ostream.close();
    return imageData;
  }
  
  /**
   * Update the Mime type
   *
   */
  public String getMimeType() {
    return "image/jpeg";
  }
  
} // JPEGConvertSVG

