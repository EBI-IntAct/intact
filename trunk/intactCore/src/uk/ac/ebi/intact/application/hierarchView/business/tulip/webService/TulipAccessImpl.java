package uk.ac.ebi.intact.application.hierarchView.business.tulip.webService;

// Web Service
import org.apache.axis.*;
import org.apache.axis.session.*;

// File & Input/Output managment
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;

// Process 
import java.lang.Runtime;
import java.lang.Process;
import java.lang.InterruptedException;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
  * Purpose : <br>
  * Allows the user to send a TLP file to Tulip and get back the coordinate of the nodes.
  *
  * @author Samuel KERRIEN (skerrien@ebi.ac.uk)
  */

public class TulipAccessImpl implements TulipAccess {


  /**************/
  /** Constants */
  /**************/

  /**
   * Had to be moved in a property file
   */
  private static String TULIP_BINARY_CLIENT_FILE  = "/scratch/tulipRemote/cli";
  private static String TULIP_IP                  = "arafel.ebi.ac.uk";
  private static String TULIP_PORT                = "2000";

  private static String TULIP_BINARY_CONSOLE_FILE = "/scratch/tulipRemote/console";



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
   * @param optionMask the option of the Tulip process
   * @return the computed tlp file content or <b>null</b> if an error occurs.
   */
  public ProteinCoordinate[] getComputedTlpContent ( String tlpContent, String optionMask ) {
    // get a unique key
    String sessionKey = getUniqueIdentifier ();

    String inputFile  = sessionKey + ".in";
    String outputFile = sessionKey + ".out";

    // Store the content in a file on hard disk
    if (!storeInputFile (inputFile, tlpContent)) return null;

    // call the tulip client in a new process
    if (!computeTlpFile (inputFile, outputFile, optionMask)) return null;

    // Read the content of the generated file and create a collection of proteinCoordinate
    Collection coordinateSet = parseOutputFile (outputFile);

    // delete temporary files
    deleteFile (inputFile);
    deleteFile (outputFile);

    // create the array
    ProteinCoordinate[] pc = new ProteinCoordinate[coordinateSet.size()];
    Iterator iterator = coordinateSet.iterator ();
    int i = 0;
    while (iterator.hasNext()) {
      pc[i++] = (ProteinCoordinate) iterator.next();;
    }

    // Send back the computed content
    return pc;

  } // computeTlpContent


  /**
   * get the line separator string.
   * It allows to use the same separator int the service and int the client
   * to keep the multiplateform aspect.
   *
   * @return the line separator
   */
  private String getLineSeparator () {
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

    // test the validity of the mask, set to a default value if it's wrong.
    if ((null == aMask) || (0 == aMask.length())) {
      // warning log : mask undefined
      aMask = DEFAULT_MASK;
    } else {
      int i = 0;
      try {
	i = Integer.parseInt (aMask);
      } catch (NumberFormatException nfe) {
	// warning log : invalid mask format
	aMask = DEFAULT_MASK;
      }
      if ((i < 0) || (i > 65535)) {
	// warning log : mask out of range
	aMask = DEFAULT_MASK;
      }      
    }

    try {
      Runtime runtime = Runtime.getRuntime();
    
      /**
       * Use the remote Tulip access via the cli <--> Serveur binary files.
       */

//       Process process = runtime.exec (TULIP_BINARY_CLIENT_FILE + " " +
// 				      TULIP_IP + " " + 
// 				      TULIP_PORT + " " + 
// 				      anInputFile + " " + 
// 				      anOutputFile + " " +
// 				      aMask);

      /**
       * Use the console binary file to access Tulip library, indeed, 
       * Tulip have to be installed on the same conputer
       */

      Process process = runtime.exec (TULIP_BINARY_CONSOLE_FILE + " " +
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



  /**
   * Parse the Tulip conputed TLP file to grab coordinates of each protein
   *
   * Here is the format of the section to parse :
   *
   * (property  0 layout "viewLayout"
   * (default "(105.000000,966.000000,359.000000)" "()" )
   * (node 1 "(215.500000,7.000000,0.000000)")
   * (node 2 "(57.500000,-288.000000,0.000000)")
   * (node 3 "(40.500000,191.000000,0.000000)")
   * (...)
   * )
   *
   * @param anOutputFile the path of the file to read
   * @return a collection of ProteinCoordinate
   */
  public static Collection parseOutputFile (String anOutputFile) {

    ArrayList collection = new ArrayList ();

    try {
      FileReader fileReader = new FileReader(anOutputFile);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String currentLine;

      while (( currentLine = bufferedReader.readLine() ) != null ){	
       if ( currentLine.equals ("") ) continue;
       
       // We look, now, word by word
       StringTokenizer st = new StringTokenizer (currentLine);

      if (st.nextToken().equals ("(property")) {
	 st.nextToken();
	 st.nextToken();
	 currentLine = st.nextToken();
	 // test null
	 
	 
	 // viewLayout treatment : parse and store computed coordinates
	 if (currentLine.equals ("\"viewLayout\"")) {
  
	   currentLine = bufferedReader.readLine(); // get pass the default line
	   currentLine = bufferedReader.readLine();
	   // test null
	   
	   while (! currentLine.equals (")") ) {
	     int index;
	     float x,y;
	     StringBuffer buf;
	     
	     st = new StringTokenizer (currentLine);
	     
	     if (!st.nextToken().equals ("(node"))
	       throw new IOException ("Imported data don't contain the good number of nodes"); 

	     index   = (new Integer(st.nextToken())).intValue();
	     buf     = new StringBuffer (st.nextToken()); /* = "(15.0000, 45.2540, 78.454)" */
	     buf.delete (0,2);
	     buf.delete (buf.length() - 3, buf.length());

	     st = new StringTokenizer (buf.toString(), ",");
	     x  = Float.parseFloat (st.nextToken());
	     y  = Float.parseFloat (st.nextToken());
	     
	     // Store coordinates
	     ProteinCoordinate pc = new ProteinCoordinate (index, x, y);
	     collection.add (pc);

	     currentLine = bufferedReader.readLine();
	   } // while

	   // stop reading file
	   


	 } // if "viewLayout"
       } // if "property"
      } // while

      bufferedReader.close();
      fileReader.close ();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return (Collection) collection;

  } // parseOutputFile



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











