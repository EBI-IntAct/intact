package uk.ac.ebi.intact.application.hierarchView.business.tulip.webService;

/**
  * Purpose : <br>
  * Allows the user to send a TLP file to Tulip and get back 
  * the computed file.
  *
  * @author Samuel KERRIEN (skerrien@ebi.ac.uk)
  */

public interface TulipAccess {

  /**
   * get the computed TLP content from tulip
   *
   */
  public String getComputedTlpContent ( String tlpContent );

  /**
   * get the line separator string
   *
   */
  public String getLineSeparator ();
  
} // TulipAccess
