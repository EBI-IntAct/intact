package uk.ac.ebi.intact.application.hierarchView.business;

// hierarchView
import uk.ac.ebi.intact.application.hierarchView.business.graph.*;
import uk.ac.ebi.intact.util.IntactCoreApp;

// intact
import uk.ac.ebi.intact.simpleGraph.*;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.*;

// JDK
import java.util.*;



/**
 * GraphHelper.java
 *
 *
 * Created: Tue Aug 27 13:58:11 2002
 *
 * @author Samuel Kerrien
 * @version
 */

public class GraphHelper  {

  private IntactHelper helper;
  private DAOSource dataSource;

  /**
   * basic constructor - sets up (hard-coded) data source and an intact helper
   */
  public GraphHelper()  throws Exception {
    
    dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");
    
    //set the config details, ie repository file for OJB in this case
    Map config = new HashMap();
    config.put("mappingfile", "config/repository.xml");
    dataSource.setConfig(config);
    
    try {
      helper = new IntactHelper(dataSource);
    } catch (IntactException ie) {
      //something failed with type map or datasource...
      String msg = "unable to create intact helper class - no datasource";
      System.out.println(msg);
      ie.printStackTrace();
    }
  } // GraphHelper
  


  /**
   *
   *
   */
  public Collection doSearch(String className, String searchParam, String searchValue) throws Exception {
    Collection resultList = null;
    if (helper != null) {
      //NB assumes full java className supplied...
      resultList = helper.search(className, searchParam, searchValue);
      return resultList;
    } else {
      System.out.println("something failed - couldn't create a helper class to access the data!!");
      throw new Exception();
    }
  } // doSearch


  

  /**
   * Create a graph ...
   *
   *
   */
  public InteractionNetwork getInteractionNetwork (String anAC, int depth) throws Exception {

    InteractionNetwork in = new InteractionNetwork ();

    //get a complete Institution, then add it to an Experiment..
    System.out.println ("retrieving Interactor ...");
    Collection results = this.doSearch ("uk.ac.ebi.intact.model.Interactor", "ac", anAC);
    Iterator iter1     = results.iterator ();
    
    //there is at most one - ac is unique
    if (iter1.hasNext()) {
      System.out.println ("Starting graph generation ... ");
      Interactor interactor = (Interactor) iter1.next();
      Graph graph = (Graph) in;
      graph = this.helper.subGraph (interactor, 
				    depth, 
				    null, 
				    uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY,
				    graph);

      System.out.println (in);
    } else {
      in = null;
      System.out.println ("AC not found: " + anAC + "\n");
    }

    return in;

  } // getInteractionNetwork

 } // GraphHelper







