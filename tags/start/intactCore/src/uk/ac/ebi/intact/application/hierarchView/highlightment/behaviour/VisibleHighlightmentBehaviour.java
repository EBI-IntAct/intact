
package uk.ac.ebi.intact.application.hierarchView.highlightment.behaviour;

import uk.ac.ebi.intact.application.hierarchView.business.graph.*;
//import uk.ac.ebi.intact.application.hierarchView.struts.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.image.Utilities;

import java.util.Collection;
import java.lang.UnsupportedOperationException;
import java.util.ArrayList;

  /**
   * Abstract class allowing to deals with the Highlightment behaviour,
   * the implementation of that class would just specify the behaviour 
   * of one node of the graph.
   *
   * @author Emilie FROT
   */

public class VisibleHighlightmentBehaviour 
       extends HighlightmentBehaviour {
  /**
   * Select all the graph protein which are not in the given collection
   * When we call apply, the proteins visibility of the new collection will put to false.
   *
   * @param proteins
   * @param aGraph 
   * 
   * @return the new collection to highlight
   */
  public Collection modifyCollection (Collection proteins, InteractionNetwork aGraph) throws UnsupportedOperationException {
     Protein protein;
     boolean b;
     
     /* Get the list of proteins in the current InteractionNetwork */
     ArrayList listAllProteins  = new ArrayList (aGraph.getNodes().values()); 

     /* Make a clone of the list */
     Collection newList         = (Collection) listAllProteins.clone();

     /* Remove all proteins of the collection "proteins" */
     b = newList.removeAll(proteins);
     if (!b) throw new UnsupportedOperationException("Unable to remove a protein");
   
    return newList;
  } // modifyCollection


  /**
   * Apply the implemented behaviour to the specific Node of the graph.
   * Here, we change the visibility to false for the given node.
   *
   * @param aProtein the node on which we want to apply the behaviour
   */
  public void applyBehaviour (Protein aProtein) {
    aProtein.put(Constants.ATTRIBUTE_VISIBLE, new Boolean (false));

  } // applyBehaviour

} // VisibleHighlightmentBehaviour









