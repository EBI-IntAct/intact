package uk.ac.ebi.intact.application.hierarchView.business.tulip.client;

import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.*;

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
   * @return the conputed tlp content
   */
  public String getComputedTlpContent (String tlpContent) {
    String result = null;
    if (null != tulip) {
      try {
	result = tulip.getComputedTlpContent (tlpContent);
      } catch (java.rmi.RemoteException se) {}
    }
    return result;
  } // getComputedTlpContent


  /**
   * get the line separator string
   *
   */
  public String getLineSeparator () {
    String result = null;
    if (null != tulip) {
      try {
	result = tulip.getLineSeparator();
      } catch (java.rmi.RemoteException se) {}
    }
    return result;
  } // getLineSeparator

} // TulipClient

