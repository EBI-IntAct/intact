package uk.ac.ebi.intact.application.hierarchView.business.graph;
 
import uk.ac.ebi.intact.simpleGraph.*;
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
//import uk.ac.ebi.intact.application.hierarchView.struts.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.image.Utilities;

import java.util.Properties;
import java.util.StringTokenizer;
import java.awt.Color;

public class Protein extends Node {
  
  /********** CONSTANTS ************/
  private static int DEFAULT_COLOR_NODE_RED = 0;
  private static int DEFAULT_COLOR_NODE_GREEN = 0;
  private static int DEFAULT_COLOR_NODE_BLUE = 255;
  private static String DEFAULT_COLOR_NODE = DEFAULT_COLOR_NODE_RED + "," +
                                             DEFAULT_COLOR_NODE_GREEN + "," + 
                                             DEFAULT_COLOR_NODE_BLUE;

  private static int DEFAULT_COLOR_LABEL_RED = 255;
  private static int DEFAULT_COLOR_LABEL_GREEN = 255;
  private static int DEFAULT_COLOR_LABEL_BLUE = 255;
  private static String DEFAULT_COLOR_LABEL = DEFAULT_COLOR_LABEL_RED + "," +
                                              DEFAULT_COLOR_LABEL_GREEN + "," + 
                                              DEFAULT_COLOR_LABEL_BLUE;

  private static String DEFAULT_SIZE_LENGTH = "40";
  private static String DEFAULT_SIZE_HEIGHT = "40";
  

/********** INSTANCE VARIABLES ************/
  private boolean neighbour;


  /** Constructors */
  public Protein (){}
  
  
  public Protein (String label)
  {
    this.initialization();
    
    // read the ApplicationResource.proterties file 
    Properties properties = PropertyLoader.load (Constants.PROPERTY_FILE);
    
    String stringLength   = null;
    String stringHeight   = null;
    
    if (null != properties) {
      stringLength = properties.getProperty ("hierarchView.image.size.default.node.length");
      stringHeight = properties.getProperty ("hierarchView.image.size.default.node.height");
    }

    if (null == stringLength)  stringLength = DEFAULT_SIZE_LENGTH;
    if (null == stringHeight) stringHeight = DEFAULT_SIZE_HEIGHT;

    Float length = new Float(stringLength);
    Float height = new Float (stringHeight);
    
    this.put(Constants.ATTRIBUTE_LENGTH,length);
    this.put(Constants.ATTRIBUTE_HEIGHT,height);
    
    
    this.neighbour = true;
    this.put(Constants.ATTRIBUTE_LABEL,label);
  }
  

  /**
   * Allow to put the default color and default visibility for each protein of the interaction network 
   */
  public void initialization () {
    // read the ApplicationResource.proterties file 
    Properties properties = PropertyLoader.load (Constants.PROPERTY_FILE);
    
    String stringColorNode  = null;
    String stringColorLabel = null; 
    
    if (null != properties) {
      stringColorNode  = properties.getProperty ("hierarchView.image.color.default.node");
      stringColorLabel = properties.getProperty ("hierarchView.image.color.default.label");
      
    }
    
    if (null == stringColorNode)  stringColorNode  = DEFAULT_COLOR_NODE;
    if (null == stringColorLabel) stringColorLabel = DEFAULT_COLOR_LABEL;
    
    Color colorNode = Utilities.parseColor(stringColorNode, 
					   DEFAULT_COLOR_NODE_RED, 
					   DEFAULT_COLOR_NODE_GREEN,
					   DEFAULT_COLOR_NODE_BLUE);
    
    
    this.put(Constants.ATTRIBUTE_COLOR_NODE, colorNode);
    Color colorLabel = Utilities.parseColor(stringColorLabel, 
					    DEFAULT_COLOR_LABEL_RED, 
					    DEFAULT_COLOR_LABEL_GREEN,
					    DEFAULT_COLOR_LABEL_BLUE);
    
    this.put(Constants.ATTRIBUTE_COLOR_LABEL, colorLabel);
    
    this.put(Constants.ATTRIBUTE_VISIBLE,new Boolean(true));
  } // initialization



  public String getId()
  {return null;}

  public void setId(String anId){}

  public String getLabel()
  {return (String) this.get(Constants.ATTRIBUTE_LABEL);}

  public void setLabel(String aLabel)
  {this.put(Constants.ATTRIBUTE_LABEL,aLabel);}

  public String getAc()
  {return null;}

  public void setAc(String anAc){}

  public boolean getNeighbour()
  {return this.neighbour;}
  
  public void setNeighbour(boolean value)
  {this.neighbour = value;}
 
} // Protein
