
// IntAct - hierarchView
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.TulipClient;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;

// Collection ...
import java.util.*;

// File & Input/Output managment
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;

 
/**
 * Purpose :
 *
 * Test the Tulip web service
 *
 * Usage : ./start [OPTIONAL TLP FILE]
 */

public class Tulip {


  /** Purpose : <br>
    * Read a file that the path is given in parameter and store the content in a string.
    * 
    * @param anOutputFile the path of the file to read
    * @return the content of the file or null if a problems occur.
    */
  public static String file2String (String filename) {
    StringBuffer stringBuffer = new StringBuffer ();
    try {
      FileReader fileReader = new FileReader(anOutputFile);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String currentLine;
      String newLine = System.getProperty ("line.separator");

      while( (currentLine = bufferedReader.readLine()) != null ){
	stringBuffer.append (currentLine + newLine);
      }

      bufferedReader.close();
      fileReader.close ();
    } catch (IOException e) {
      return null;
    }
    return stringBuffer.toString();
  } // file2String



  /**
   *
   *   D E M O
   *
   */

  public static void main (String args[]) {

    ProteinCoordinate[] result = null;

    // Create a Tulip access
    TulipClient client = new TulipClient();

    // the tlp content we want to compute 
    String content = null;

    if (args.length > 1) {
      content = file2String (args[1]);
    }

    if (null == content) {
      content = "(nodes 1 2 3 4 5)\n" +
	        "(edge 1 1 5)\n"+
	        "(edge 2 2 1)\n"+
	        "(edge 3 3 1)\n"+
	        "(edge 4 4 2)\n"+
	        "(edge 5 5 3)\n";
    }

    result = client.getComputedTlpContent (content);

    if (null != result) {

      for (int i = 0; i < result.length; i++) {
	ProteinCoordinate c = (ProteinCoordinate) result[i];
	System.out.println ( c.getId() + "  X=" + c.getX() + "  Y=" + c.getY() );
      }

      String[] errorMessages = client.getErrorMessages (true);
      if (null != errorMessages) {
	String msg = null;
	for (int i = 0; i < errorMessages.length; i++) {
	  msg += errorMessages[i] + "\n";
	}
	System.out.println (msg);
      } else {
	System.out.println ("No message retreived.");
      }

    } else {

      System.out.println ("result is null");

      String[] errorMessages = client.getErrorMessages (true);
      if (null != errorMessages) {
	String msg = null;
	for (int i = 0; i < errorMessages.length; i++) {
	  msg += errorMessages[i] + "\n";
	}
	System.out.println (msg);
      } else {
	System.out.println ("No message retreived.");
      }
    }

  } // main

} // Tulip

