package uk.ac.ebi.intact.application.hierarchView.business.tulip.client;

import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessService;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessServiceLocator;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipSoapBindingStub;

import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;


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
      // Make a service
      TulipAccessServiceLocator serviceLocator = new TulipAccessServiceLocator();
      
      // get a service object
      TulipAccessService service = (TulipAccessService) serviceLocator;
      
      // Now use the service to get a stub
      tulip = service.getTulip();
    } catch (javax.xml.rpc.ServiceException se) {
      tulip = null;
    }
  } // constructor


  /**
   * allows to know if the service is ready or not.
   *
   * @return 
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

