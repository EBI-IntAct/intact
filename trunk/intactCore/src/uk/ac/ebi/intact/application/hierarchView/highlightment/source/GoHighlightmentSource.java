package uk.ac.ebi.intact.application.hierarchView.highlightment.source;

// JDK
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.sql.*;
import java.util.zip.*;
import java.net.MalformedURLException;
import java.lang.String;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

// Intact
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.Utilities;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.simpleGraph.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.application.hierarchView.struts.Constants;

import uk.ac.ebi.intact.application.hierarchView.business.graph.*;
import uk.ac.ebi.intact.application.hierarchView.struts.LabelValueBean;

//OJB
import org.apache.ojb.broker.*;
import org.apache.ojb.broker.util.logging.*;
import org.apache.ojb.broker.accesslayer.*;
import org.apache.ojb.broker.query.*;
import org.apache.ojb.broker.util.*;



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
  private static String KEY_SEPARATOR = ",";
  private String ATTRIBUTE_OPTION_CHILDREN = "CHILDREN";
  private String PROMPT_OPTION_CHILDREN = "With children of the selected GO term";
  
/**
   * Return the html code for specific options of the source to integrate int the highlighting form.
   * if the method return null, the source hasn't options.
   *
   * @return the html code for specific options of the source.
   */
  public String getHtmlCodeOption(HttpSession aSession) {

    String htmlCode;
    String check = (String)  aSession.getAttribute (ATTRIBUTE_OPTION_CHILDREN);

    if (check == null) {
      check = "";  
    }

    htmlCode = "<INPUT TYPE=\"checkbox\" NAME=\"" + 
                ATTRIBUTE_OPTION_CHILDREN +"\" " + 
                check + " VALUE=\"checked\">" +
                PROMPT_OPTION_CHILDREN;

    return htmlCode;
  }


/**
   * Return a set of keys corresponding to the source and finding in the IntAct database.
   * if the method send back no keys, the given parameter have not keys for the source.
   *
   * @param aProteinAC : a protein identifier (AC)
   * @return a set of keys (this keys are a String) (this Keys are a String[] which contains the GOterm and a description)
   */
  public Collection getKeysFromIntAct (String aProteinAC) {

    Collection result;
    Iterator iterator;
    DAOSource dataSource;
    Collection listGOTerm = new ArrayList();
    //String goTerm;
    
    try {
      dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");
      
      // set the config details, ie repository file for OJB in this case
      Map config = new HashMap();
      /*
       *  TO BE STORED LATER IN A PROPERTY FILE !!!!!
       */
      config.put("mappingfile", "config/repository.xml");
      dataSource.setConfig(config);
      
      IntactHelper ih = new IntactHelper(dataSource);
      result = ih.search ("uk.ac.ebi.intact.model.Protein","ac", aProteinAC);
      
      // recup object
      if (result.isEmpty()) return null;
      
      iterator = result.iterator();
      uk.ac.ebi.intact.model.Interactor interactor = (uk.ac.ebi.intact.model.Interactor) iterator.next();
    
      // get Xref collection
      Collection xRef = interactor.getXref();
      Iterator xRefIterator = xRef.iterator() ;
      int cptXRef = 0;
      
      while (xRefIterator.hasNext() ) {
	String[] goterm = new String[2];
	Xref xref = (Xref) xRefIterator.next();
	
	if (((String) xref.getCvDatabase().getShortLabel()).equals("GO")) {
	  goterm[0] = (String) xref.getPrimaryId();
	  goterm[1] = (String) xref.getSecondaryId();
	  // listGOTerm.add((String) xref.getPrimaryId());  
          listGOTerm.add(goterm);
	}
      }
    }
    catch (Exception e) {
    return null;}
     
    
    
    return listGOTerm;	
    
  } // getKeysFromIntAct


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
    Collection nodeList = new Vector ();
    // String keys         = (String)  aSession.getAttribute (Constants.ATTRIBUTE_KEYS);

    Collection keys     = (Collection)  aSession.getAttribute (Constants.ATTRIBUTE_KEYS);

    // Read source option in the session
    String  check = (String)  aSession.getAttribute (ATTRIBUTE_OPTION_CHILDREN); 

    ArrayList listOfNode = (ArrayList) aGraph.getOrderedNodes();
    int size                = listOfNode.size();
    for (int i=0 ; i<size ; i++)
      {
	Node node = (Node) listOfNode.get(i);
	
	String ac = (String) node.getAc();
	
	// Search all GoTerm for this ac number
	Collection listGOTerm = this.getKeysFromIntAct(ac);
	
	if (listGOTerm!= null && !listGOTerm.isEmpty())
	  {
	    String[] goTermInfo;
	    String goTerm;
	    Iterator list = listGOTerm.iterator();
	    
	    while (list.hasNext())
	      {
		Iterator it = keys.iterator();
		String selectedGOTerm = null;
		
		if (it.hasNext()){
		  selectedGOTerm = (String) it.next();
		}
		
		goTermInfo = new String[2];
		goTerm = new String();
		// goTerm = (String) list.next();
		goTermInfo = (String[]) list.next();
		goTerm = (String) goTermInfo[0];
		
 		if (selectedGOTerm.equals(goTerm)) 
 		  {
		    nodeList.add(node);
		    break;
		  }
		if ((check != null) && (check.equals("checked"))) {
		  while (it.hasNext()) {
		    String newGOTerm = (String) it.next();
		    
		    if (newGOTerm.equals(goTerm)) {
		      nodeList.add(node);
		      break;
		    }
		  }
		  // goterm.isChildrenOf(keys)?? -> if it'OK nodeList.add(node) et break
		}
	      }
	  }	
      }
    
    return nodeList;
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
    
    String[] result = (String[]) aRequest.getParameterValues(ATTRIBUTE_OPTION_CHILDREN);

    if (result != null) {
    aSession.setAttribute(ATTRIBUTE_OPTION_CHILDREN,result[0]);
    }
    
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
    
    String[] goTermInfo;
    String goTerm, goTermDescription; 
    
    Collection listGOTerm = this.getKeysFromIntAct(aProteinAC);
    
    if (listGOTerm != null && !listGOTerm.isEmpty())
      {
	Iterator list = listGOTerm.iterator();
	while (list.hasNext())
	  {
	    goTermInfo        = new String[2];
	    goTerm            = new String();
	    goTermDescription = new String();
	    //goTerm = (String) list.next();
	    goTermInfo        = (String[]) list.next();
	    goTerm            = goTermInfo[0];
	    goTermDescription = goTermInfo[1];
	    
	    
        // TODO : put that server name as a parameter, eventually in a properties file !
	    urls.add (new LabelValueBean(goTerm, "http://holbein:8080/interpro/DisplayGoTerm?id=" + goTerm + "&format=simple", goTermDescription));
	  }
      }
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

    if ((null == someKeys) || (someKeys.length() < 1)) {
	return null;
      } 

    StringTokenizer st = new StringTokenizer (someKeys, KEY_SEPARATOR);
      
      while (st.hasMoreTokens()) {
	String key = st.nextToken();

	keys.add(key);
      }

    return  keys;
  } // parseKeys

} // GoHighlightmentSource

  























































































