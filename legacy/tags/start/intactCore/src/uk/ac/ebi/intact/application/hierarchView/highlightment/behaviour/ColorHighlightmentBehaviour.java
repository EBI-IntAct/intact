package uk.ac.ebi.intact.application.hierarchView.highlightment.behaviour;

import uk.ac.ebi.intact.application.hierarchView.business.graph.*;
//import uk.ac.ebi.intact.application.hierarchView.struts.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.image.Utilities;
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;

import java.awt.Color;
import java.util.Collection;
import java.util.Properties;

  /**
   * Abstract class allowing to deals with the Highlightment behaviour,
   * the implementation of that class would just specify the behaviour 
   * of one node of the graph.
   *
   * @author Samuel KERRIEN
   */

public class ColorHighlightmentBehaviour 
       extends HighlightmentBehaviour {


  /********** CONSTANTS ************/
  private final static int DEFAULT_COLOR_HIGHLIGHTING_RED   = 255;
  private final static int DEFAULT_COLOR_HIGHLIGHTING_GREEN = 0;
  private final static int DEFAULT_COLOR_HIGHLIGHTING_BLUE  = 0;
  private final static String DEFAULT_COLOR_HIGHLIGHTING    = DEFAULT_COLOR_HIGHLIGHTING_RED + "," +
                                                              DEFAULT_COLOR_HIGHLIGHTING_GREEN + "," + 
                                                              DEFAULT_COLOR_HIGHLIGHTING_BLUE;


  /**
   * Apply the implemented behaviour to the specific Node of the graph.
   * Here, we change the color of the highlighted node.
   *
   * @param aProtein the node on which we want to apply the behaviour
   */
  public void applyBehaviour (Protein aProtein) {
 
    // apply the behaviour
    // node.setColor (Color.red);
    
    // read the ApplicationResource.proterties file 
    Properties properties = PropertyLoader.load (Constants.PROPERTY_FILE);
    
    String colorString = null;
    
    if (null != properties) {
      colorString = properties.getProperty ("hierarchView.color.highlighting");
    }
    
    if (null == colorString) colorString   = DEFAULT_COLOR_HIGHLIGHTING;
    else {
      /*error log*/
    }
    
    Color color = Utilities.parseColor(colorString,
				       DEFAULT_COLOR_HIGHLIGHTING_RED,
				       DEFAULT_COLOR_HIGHLIGHTING_GREEN,
				       DEFAULT_COLOR_HIGHLIGHTING_BLUE);


    aProtein.put(Constants.ATTRIBUTE_COLOR_NODE, color);

  } // applyBehaviour

} // ColorHighlightmentBehaviour
