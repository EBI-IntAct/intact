package uk.ac.ebi.intact.application.hierarchView.business.image;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.util.*;



/**
 * This class give some usefull methods
 *
 */


public class Utilities
{


  /**
   * Parse a string (number separated by comma) and create a Color Object
   * The string has to be initialized
   * 
   * @param the string to parse
   * @param default RED composite
   * @param default GREEN composite
   * @param default BLUE composite
   * @return the parsed object color
   */
  public static Color parseColor (String stringColor, 
				  int defaultRed, int defaultGreen, int defaultBlue) {

    StringTokenizer tokens = null;
    
    int red               = defaultRed;
    int green             = defaultGreen;
    int blue              = defaultBlue;
    
    tokens = new StringTokenizer(stringColor,",");
    
    try {
      if (tokens.hasMoreTokens()) red   = new Integer(tokens.nextToken()).intValue();
      if (tokens.hasMoreTokens()) green = new Integer(tokens.nextToken()).intValue();
      if (tokens.hasMoreTokens()) blue  = new Integer(tokens.nextToken()).intValue();
    } catch (NumberFormatException e) {
      // let the default value
    }

    return new Color(red, green, blue);

  } // parseColor
 

 
} // Utilities





