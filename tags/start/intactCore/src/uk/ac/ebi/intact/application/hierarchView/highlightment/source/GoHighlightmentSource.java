package uk.ac.ebi.intact.application.hierarchView.highlightment.source;

import uk.ac.ebi.intact.application.hierarchView.business.graph.*;
import uk.ac.ebi.intact.application.hierarchView.struts.LabelValueBean;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Vector;
import java.util.ArrayList;

import java.lang.String;

  /**
   * Interface allowing to wrap an highlightment source.
   *
   * @author Samuel KERRIEN
   */

public class GoHighlightmentSource
       extends HighlightmentSource {

  /**
   * separator of keys, use to create and parse key string.
   */
  private static char KEY_SEPARATOR = ',';
  

  /**
   * Create a set of protein we must highlight in the graph given in parameter.
   * The keys are GO terms, used to know what protein to select.
   * If a protein of the graph has in attribute one of the GO term in keys,
   * it's added in the returned Vector.
   *
   * @param aSession 
   * @param aGraph the graph we want to highlight
   * @return a set of node to highlight
   */
  public Collection proteinToHightlight (HttpSession aSession, InteractionNetwork aGraph) {
    Collection proteins = new Vector ();
    
    // Read source option in the session


    /* Example */
    ArrayList listOfProtein = (ArrayList) aGraph.getOrderedNodes();
    int size                = listOfProtein.size();

    for (int i=0 ; i<size ; i++)
      {
	if (i%2 == 0)
	  {
	    Protein protein = (Protein) listOfProtein.get(i);
	    
	    proteins.add(protein);
	  }
      }

    return proteins;
  } // proteinToHightlight


  /**
   * Allows to update the session object with parameters' request.
   * These parameter are specific of the implementation.
   * The implementated method will have to use the updateSession method to do the work.
   *
   * @param aRequest request in which we have to get parameters to save in the session
   * @param aSession session in which we have to save the parameter
   */
  public void parseRequest (HttpServletRequest aRequest, HttpSession aSession) {


  }


  /**
   * Return a set of URL allowing to redirect to an end page of the highlightment source
   * if the method send back no URL, the given parameter is wrong.
   *
   * @param aProteinAC : a protein identifier (AC)
   * @return a set of URL pointing on the highlightment source
   */
  public Collection getUrl (String aProteinAC) {
    Collection urls = new Vector();

    // Search in Intact data Base all Go term for the AC accession number 
    // Enter in urls all adress int interpro for each Go term


    // populate the Collection
    urls.add (new LabelValueBean("GO:0030447", "http://holbein:8080/interpro/DisplayGoTerm?id=GO:0030447&format=simple"));
    urls.add (new LabelValueBean("GO:0008450", "http://holbein:8080/interpro/DisplayGoTerm?id=GO:0008450&format=simple"));
    urls.add (new LabelValueBean("GO:0007275", "http://holbein:8080/interpro/DisplayGoTerm?id=GO:0007275&format=simple"));

    return urls;
  } // getUrl


  /**
   * Generate a key string for a particular selectable item of the highlightment source.
   * That key string will be used to be sent from the highlightment source to
   * our the hierarchView module and used to select what protein we will have
   * to highlight in the interaction graph.
   * In the GO source, the item is a GO term Node in the GOTO hierarchy.
   * Each item in the generated key string is separate by a specific character.
   * 
   * @param selectedId the selected id, here a GO term accession id (GO:XXXXXXX)
   * @return a list of key separates by KEY_SEPARATOR.
   */
  public String generateKeys (String selectedId) {
    return "";
  } // generateKeys


  /**
   * Parse the set of key generate by the method above and given a set of keys.
   * 
   * @param someKeys a string which contains some key separates by KEY_SEPARATOR.
   * @return the splitted version of the key string
   */
  public Collection parseKeys (String someKeys) {
    Collection keys = new Vector ();

    return  keys;
  } // parseKeys

} // GoHighlightmentSource

  























































































