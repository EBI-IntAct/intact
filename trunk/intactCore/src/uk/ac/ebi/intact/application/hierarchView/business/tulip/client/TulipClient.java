package uk.ac.ebi.intact.application.hierarchView.business.tulip.client;

import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessService;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessServiceLocator;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;

import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.struts.Constants;
import java.util.Properties;

// JDK
import java.net.URL;
import java.net.MalformedURLException;


/**
 * 
 * Provide an access to the Tulip web service.
 * Wrap methods declared in the web service.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */


public class TulipClient {

  /* --------------------------------------------------- Instance variable
 
  /**
   * Stub to handle the tulip web service
   */
  private TulipAccess tulip;




  /* --------------------------------------------------- Methods
  /**
   * Prepare the web service.
   *
   * @return 
   */
   public TulipClient () {
    try {

      // Make a service locator (allow to find the service)
      TulipAccessServiceLocator serviceLocator = new TulipAccessServiceLocator();

      // Ask the service to maintain the session int order to keep values between two call
      serviceLocator.setMaintainSession (true);
      
      // get a service object
      TulipAccessService service = (TulipAccessService) serviceLocator;

      // Look in the property file where is the web service
      
      Properties properties = PropertyLoader.load (Constants.WEB_SERVICE_PROPERTY_FILE);
      String tulipAdress = null;
      if (null != properties) {
	tulipAdress = properties.getProperty ("webService.adress");
	System.out.println (" adress=" + tulipAdress);
      } else {	
	tulip = null;
	return;
      }

      URL tulipUrl = new URL (tulipAdress);

      // Now use the service to get a stub
      tulip = service.getTulip (tulipUrl);    

    } catch (MalformedURLException e) {
      tulip = null;
    }
    catch (javax.xml.rpc.ServiceException se) {
      tulip = null;
    }

  } // constructor


  /**
   * allows to know if the service is ready or not.
   * BUG : that method isn't reliable !
   *
   * @return is the webService is rubbubg 
   */
  public boolean isReady () {
    if (null == tulip) {
      return false;
    }
    return true;
  } // isReady


  /**
   * allows to compute a tlp content
   *
   * @param tlpContent the tlp content to compute
   * @return the collection of protein coordinates
   */
  public ProteinCoordinate[] getComputedTlpContent (String tlpContent) {

    ProteinCoordinate[] pc = null;
    String mask = "0";

    // - LOG -
    System.out.println (tlpContent);

    if (null != tulip) {
      try {
	pc = (ProteinCoordinate[]) tulip.getComputedTlpContent (tlpContent, mask);
      } catch (java.rmi.RemoteException se) {
	System.out.println ("Exception during retreiving proteins' coordinate");
	se.printStackTrace ();
      }
    }

    return pc;
  } // getComputedTlpContent


  /**
   * Get the list of messages produce in the web service
   *
   * @param hasToBeCleaned delete all messages after sended them back to the client
   */
  public String[] getErrorMessages (boolean hasToBeCleaned) {
    try {
      return tulip.getErrorMessages (hasToBeCleaned);
    } catch (java.rmi.RemoteException se) {
      // create an error message to display
      String[] errors = new String[1];
      errors[0] = "\n\nError while checking errors.";

      // log exception
      se.printStackTrace ();
      return errors;
    }
  } // getErrorMessages


  /**
   * get the line separator string
   *
   */
  public String getLineSeparator () {
    String result = null;

 //    if (null != tulip) {
//       try {
// 	result = tulip.getLineSeparator();
//       } catch (java.rmi.RemoteException se) {}
//     }

    return result;
  } // getLineSeparator

} // TulipClient

