package uk.ac.ebi.intact.application.hierarchView.business;

import uk.ac.ebi.intact.application.hierarchView.struts.LabelValueBean;
import uk.ac.ebi.intact.application.hierarchView.struts.Constants;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * Allows to create some collection to populate option list in HTML form.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class OptionGenerator {

  /**
   * create a collection of LabelValueBean object from a properties file
   *
   * @return a collection of LabelValueBean object
   */
  public static ArrayList getHighlightmentSources () {

    ArrayList sources = new ArrayList ();
    
    // read the ApplicationResource.proterties file 
    Properties properties = PropertyLoader.load (Constants.PROPERTY_FILE);
        
    if (null != properties) {

      String sourceList = properties.getProperty ("highlightment.source.allowed");

      if ((null == sourceList) || (sourceList.length() < 1)) {
	return null;
      } 

      // parse source list
      String token = properties.getProperty ("highlightment.source.token");
      
      if ((null == token) || (token.length() < 1)) {	
	return null;	
      } 
      
      StringTokenizer st = new StringTokenizer (sourceList, token);
      
      while (st.hasMoreTokens()) {
	String sourceKey = st.nextToken();
	String label = properties.getProperty ("highlightment.source." + sourceKey + ".label");
    
	if ((null == label) || (label.length() < 1))
	  continue;
    
	sources.add (new LabelValueBean(label, sourceKey));
      } // while
    }
 
    return sources;

  } // getHighlightmentMethods
  

  /**
   * Create a collection of LabelValueBean object specific of an highlightment method
   * from a properties file
   *
   * @param anHighlightmentMethod
   * @return a collection of LabelValueBean object specific of an highlightment method
   */  
  public static ArrayList getAuthorizedBehaviour (String anHighlightmentMethod) {

    ArrayList behaviours = new ArrayList ();

    // read the ApplicationResource.proterties file 
    Properties properties = PropertyLoader.load (Constants.PROPERTY_FILE);
        
    if (null != properties) {

      String behaviourList = properties.getProperty ("highlightment.behaviour." + anHighlightmentMethod + ".allowed");

      if ((null == behaviourList) || (behaviourList.length() < 1)) {

	// As there are no specified allowed list of behaviour for this method,
	// we try to load the global definition of defined behaviour.
	behaviourList = properties.getProperty ("highlightment.behaviour.existing");

	if ((null == behaviourList) || (behaviourList.length() < 1)) {
	  return null;
	}
      }	

      // parse behaviour list
      String token = properties.getProperty ("highlightment.behaviour.token");
      
      if ((null == token) || (token.length() < 1)) {	
	return null;	  	
      } 
	
      StringTokenizer st = new StringTokenizer (behaviourList, token);
      
      while (st.hasMoreTokens()) {
	String sourceKey = st.nextToken();
	String label = properties.getProperty ("highlightment.behaviour." + sourceKey + ".label");
	String value = properties.getProperty ("highlightment.behaviour." + sourceKey + ".class");

    	if ((null == label) || (label.length() < 1) || (null == value) || (value.length() < 1))
	  continue; // don't add this element
    
	behaviours.add (new LabelValueBean(label, value));
      } // while
	      
    } // if

    return behaviours;

  } // getHighlightmentMethods
  
} // OptionGenerator
