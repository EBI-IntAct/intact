package uk.ac.ebi.intact.application.hierarchView.highlightment;

import uk.ac.ebi.intact.application.hierarchView.highlightment.source.*;
import uk.ac.ebi.intact.application.hierarchView.highlightment.behaviour.*;
import uk.ac.ebi.intact.application.hierarchView.business.graph.*;
import uk.ac.ebi.intact.application.hierarchView.business.image.*;
import uk.ac.ebi.intact.application.hierarchView.business.servlet.*;
import uk.ac.ebi.intact.application.hierarchView.struts.Constants;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.util.Collection;


public class HighlightProteins{


/** Constructor
   * Allow to modify the current graph to highlight a part of this.
   *
   * @param source The highlighting source 
   * @param behaviour The highlighting behaviour
   * @param session The current session
   * @param in The interaction network
   */
  public HighlightProteins(String source, 
				String behaviour,  
				HttpSession session, 
				InteractionNetwork in)throws IOException{
    
    // Validate the request parameters specified by the user
    ActionErrors errors = new ActionErrors();

    // Put the default color and default visibility in the interaction network before to highlight this one 
    in.initNodes();


    // Search the list of protein to highlight
    HighlightmentSource highlightmentSource = HighlightmentSource.getHighlightmentSource(source);
   
    // Search the protein to highlight
    Collection proteinsToHighlight          = highlightmentSource.proteinToHightlight (session, in);

    
    // Interaction network 's modification
    HighlightmentBehaviour highlightmentBehaviour = HighlightmentBehaviour.getHighlightmentBehaviour(behaviour);
    highlightmentBehaviour.apply(proteinsToHighlight,in);
    


    GraphToImage te = new GraphToImage(in);

    te.draw();
    if (null == te) throw new IOException ("Unable to create the image data");
    ImageBean ib    = te.getImageBean();
    
    if (null == ib)
      errors .add("ImageBean", new ActionError("error.ImageBean.build"));
    
    // store the bean 
    session.setAttribute (Constants.ATTRIBUTE_IMAGE_BEAN, ib);
    
    // store the graph
    session.setAttribute (Constants.ATTRIBUTE_GRAPH, in);


  } // highlightProteins
  



} // HighlightProteins
