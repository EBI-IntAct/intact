package uk.ac.ebi.intact.application.hierarchView.business.tulip.webService;

import org.apache.axis.*;
import org.apache.axis.session.*;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;

import java.lang.Runtime;
import java.lang.Process;
import java.lang.InterruptedException;

// import HttpServletRequest

/**
  * Purpose : <br>
  * Allows the user to send a TLP file to Tulip and get back 
  * the computed file.
  *
  * @author Samuel KERRIEN (skerrien@ebi.ac.uk)
  */

public class TulipAccessImpl 
  implements TulipAccess {


  /**************/
  /** Constants */
  /**************/


  /**
   * Had to be moved in a property file
   */
  private static String TULIP_BINARY_FILE = "/scratch/tulipRemote/cli";  
  private static String TULIP_IP          = "arafel.ebi.ac.uk";
  private static String TULIP_PORT        = "2000";


  /**
   * Definition of the Tulip supported mode (based on the following list)
   *
   *  #define Circular 1
   *  #define GEM 2
   *  #define GeneralGraph 4
   *  #define GeneralGraph3D 8
   *  #define GeneralGraphBox 16
   *  #define Random 32
   *  #define SpringElectrical 64
   *  #define Tute 128
   *
   *  for the section Sizes : viewSize
   *	
   *  #define Auto_sizing 256 
   *  #define FitToLabel 512
   *  
   *  for the section metric : viewMetric
   *  Be careful -> this action action create a color proxy (necessary) with a linear default color proxy. 
   *                If you want to change te color proxy then call for the modes colors : see below
   *
   *  #define ConnecandTree 1024
   *  #define Barycenter 2048
   *  #define Cluster 4096
   *  #define DagLevel 8192
   *
   *  for the section color : view color
   *  #define Linear 16384
   *  #define Distribution 32768
   *
   */
  private static String DEFAULT_MASK = "0";

  private static String CIRCULAR_MASK          = "1";
  private static String GEM_MASK               = "2";
  private static String GENERAL_GRAPH_MASK     = "4";
  private static String GENERAL_GRAPH_3D_MASK  = "8";
  private static String GENERAL_GRAPH_BOX_MASK = "16";
  private static String RANDOM_MASK            = "32";
  private static String SPRING_ELECTRICAL_MASK = "64";
  private static String TUTE_MASK              = "128";

  private static String AUTO_SIZING_MASK  = "256";
  private static String FIT_TO_LABEL_MASK = "512";

  private static String CONNECAND_TREE_MASK = "1024";
  private static String BARYCENTER_MASK     = "2048";
  private static String CLUSTER_MASK        = "4096";
  private static String DAG_LEVEL_MASK      = "8192";

  private static String LINEAR_MASK       = "16384";
  private static String DISTRIBUTION_MASK = "32768";



  /****************************************/
  /** Public methods over the web service */
  /****************************************/

  /**
    * get the computed TLP content from tulip
    * @param tlpContent tlp content to compute
    * @return the computed tlp file content or <b>null</b> if an error occurs.
    */
  public String getComputedTlpContent ( String tlpContent ) {
    // get a unique key
    String sessionKey = getUniqueIdentifier ();

    String inputFile  = sessionKey + ".in";
    String outputFile = sessionKey + ".out";

    // Store the content in a file on hard disk
    if (!storeInputFile (inputFile, tlpContent)) return null;

    // call the tulip client in a new process
    if (!computeTlpFile (inputFile, outputFile, "0")) return null;

    // Read the content of the generated file
    String computedContent = readOutputFile (outputFile);

    // delete temporary files
    deleteFile (inputFile);
    deleteFile (outputFile);

    // Send back the computed content
    return computedContent;
  } // computeTlpContent


  /**
   * get the line separator string.
   * It allows to use the same separator int the service and int the client
   * to keep the multiplateform aspect.
   *
   * @return the line separator
   */
  public String getLineSeparator () {
    return System.getProperty ("line.separator");
  } // getLineSeparator




  /*********************/
  /** Internal methods */
  /*********************/

  /** Purpose : <br>
    * Creates a pure identifier that is unique with respect to the host on which it is generated
    *
    * @return a unique key
    */
  private String getUniqueIdentifier () {
    return new java.rmi.server.UID().toString();
  } // getSessionKey



  /** Purpose : <br>
    * Store the content int a file called <i>anInputFile</i>.
    *
    * @param anInputFile the path of the file to write in.
    * @param aTlpContent the string to write in the file.
    * @return true if the file is right written, false else.
    */
  private boolean storeInputFile (String anInputFile, String aTlpContent) {
    try {
      FileWriter fileWriter = new FileWriter (anInputFile);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      bufferedWriter.write(aTlpContent);      
      bufferedWriter.close();
      fileWriter.close();
    } catch (IOException e) {
      return false;
    }
    return true;
  } // storeInputFile



  /** Purpose : <br>
    * Call the tulip client by creating a new process.
    * client syntax is the following :
    *            cli <IP tulip> <PORT tulip> <input file> <output file>
    *
    * @param anInputFile the path of the input TLP file
    * @param anOutputFile the path of the output TLP file
    * @param aMask the mask allows to choose the tulip mode used to compute the tlp file
    * @return true if the computed file is well generated, false else.
    */  
  private boolean computeTlpFile (String anInputFile, String anOutputFile, String aMask) {
    try {
      Runtime runtime = Runtime.getRuntime();
      
      // set the mask to the default value if it's wrong
      if ((null == aMask) || (aMask.length() < 1)) {
	aMask = DEFAULT_MASK;
      } else {
	try {
	  int i = Integer.parseInt (aMask);
	} catch (NumberFormatException e) {
	  aMask = DEFAULT_MASK;
	}
      }

      Process process = runtime.exec (TULIP_BINARY_FILE + " " +
				      TULIP_IP + " " + 
				      TULIP_PORT + " " + 
				      anInputFile + " " + 
				      anOutputFile + " " +
				      aMask);
      process.waitFor();
    } catch ( Exception e ) {
      // an error occurs during execution of the process
      return false;
    }
    
    // test if the output file had been created
    File outputFile = new File(anOutputFile);
    if (!outputFile.exists()) {
      // an error occur on Tulip, no generated file
      return false;
    }    
    return true;
  } // computeTlpFile
  


  /** Purpose : <br>
    * Read a file that the path is given in parameter and store the content in a string.
    * 
    * @param anOutputFile the path of the file to read
    * @return the content of the file or null if a problems occur.
    */
  private String readOutputFile (String anOutputFile) {
    StringBuffer stringBuffer = new StringBuffer ();
    try {
      FileReader fileReader = new FileReader(anOutputFile);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String currentLine;
      String newLine = getLineSeparator();

      while( (currentLine = bufferedReader.readLine()) != null ){
	stringBuffer.append (currentLine + newLine);
      }

      bufferedReader.close();
      fileReader.close ();
    } catch (IOException e) {
      return null;
    }
    return stringBuffer.toString();
  } // readOutputFile


  /** Purpose : <br>
    * Delete a file from its pathname.
    * 
    * @param aFilename the path of the file 
    */
  private void deleteFile (String aFilename) {
    try {
      File file = new File (aFilename);
      file.delete ();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


} // TulipAccessImpl











