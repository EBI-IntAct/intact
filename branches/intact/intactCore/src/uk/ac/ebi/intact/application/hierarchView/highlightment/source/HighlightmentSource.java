package uk.ac.ebi.intact.application.hierarchView.highlightment.source;


import uk.ac.ebi.intact.application.hierarchView.business.graph.*;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import java.util.Vector;
import java.util.Collection;
import java.lang.Class;
import java.lang.Object;


  /**
   * Abstract class allowing to wrap an highlightment source.
   *
   * @author Samuel KERRIEN
   */

public abstract class HighlightmentSource {

  /**
   * Provides a implementation of HighlightmentSource by its name.
   * e.g. you have an implementation of this abstract class called : <b>GoHighlightmentSource</b>.
   *      so, you could call the following method to get an instance of this class :
   *      <br>
   *      <b>HighlightmentSource.getHighlightmentSource ("mypackage.GoHighlightmentSource");</b>
   *      <br>
   *      then you're able to use methods provided by this abstract class without to know
   *      what implementation you are using.
   *
   * @param aClassName the name of the implementation class you want to get
   * @return an HighlightmentSource object, or null if an error occurs.
   */
  public static HighlightmentSource getHighlightmentSource (String aClassName) {

    Object object = null;

    try {

      // create a class by its name
      Class cls = Class.forName(aClassName);

      // Create an instance of the class invoked
      object = cls.newInstance();

    } catch (Exception e) {
      // nothing to do, object is already setted to null
    }

    return (HighlightmentSource) object; 

  } // HighlightmentSource


  /**
   * Create a set of protein we must highlight in the graph given in parameter.
   * The session is used to know what protein to select.
   *
   * @param aSession the session to update
   * @param aGraph the graph we want to highlight
   * @return a set of nodes to highlight
   */
  abstract public Collection proteinToHightlight (HttpSession aSession, InteractionNetwork aGraph);


  /**
   * Allows to update the session.<br>
   * This method must be called by <b>parseRequest</b> to do that work, so
   * the implemented class don't have to know how to update the session
   * but just what value to pick in the request and save in the session.
   *
   * @param aSession the session to update 
   * @param aRequest the request int which we get parameter to save in the session.
   * @param aKey the name of the parameter to save in the session
   */
  protected void updateSession (HttpSession aSession, HttpServletRequest aRequest, String aKey) {

    String value = null;

    if ( null == aSession.getAttribute (aKey) ) {
      
      // set the key to the default value.

    } else {
      if ( null == (value = aRequest.getParameter (aKey)) ) {

	aSession.setAttribute (aKey, value);

      } else {

	// nothing

      }
    }

  } // updateSession


  /**
   * Allows to update the session object with parameters' request.
   * These parameter are specific of the implementation.
   * The implementated method will have to use the updateSession method to do the work.
   *
   * @param aRequest request in which we have to get parameters to save in the session
   * @param aSession session in which we have to save the parameter
   */
  abstract public void parseRequest (HttpServletRequest aRequest, HttpSession aSession);


  /**
   * Return a set of URL allowing to redirect to an end page of the highlightment source
   * if the method send back no URL, the given parameter is wrong.
   *
   * @param aProteinAC : a protein identifier (AC)
   * @return a set of URL pointing on the highlightment source
   */
  abstract public Collection getUrl (String aProteinAC);

  /**
   * Generate a key string for a particular selectable item of the highlightment source.
   * That key string will be used to be sent from the highlightment source to
   * our the hierarchView module and used to select what protein we will have
   * to highlight in the interaction graph.
   * 
   * @param selectedId the selected id in the source item.
   * @return a list of key separated by a character.
   */
  abstract public String generateKeys (String selectedId);

  /**
   * Parse the set of key generate by the method above and given a set of keys.
   *
   * @param someKeys a string which contains some key separates by a character.
   * @return the splitted version of the key string  
   */
  abstract public Collection parseKeys (String someKeys);

} // HighlightmentSource
