package uk.ac.ebi.intact.application.hierarchView.business.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.*;
import java.text.*;

import java.io.IOException;

import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.struts.Constants;
//import uk.ac.ebi.intact.application.hierarchView.business.Constants;

/**
   Purpose: <br>
   For a bean given in the session, forward an image (png) to be displayed.
*/
public class GenerateImage extends HttpServlet {

  // * Public servlet methods

  public void init(ServletConfig config)
    throws ServletException {
    
    // MANDATORY!
    super.init(config);
    
    // get init parameters
    // todo ...
  }
  
  
  /**
   *
   *
   *
   */
  public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse)
    throws ServletException{
        
    try
      {
    // binary output
    ServletOutputStream out = aResponse.getOutputStream();
    
    // Encode the off-screen image into a JPEG and send it to the client
    aResponse.setContentType("image/png");

    // get the current user session
    HttpSession session = aRequest.getSession();
    ImageBean ib = (ImageBean) session.getAttribute (Constants.ATTRIBUTE_IMAGE_BEAN);

    if (null == ib) return;

    out.write (ib.getImageData());

    out.flush ();
    out.close ();
      }
    catch (IOException e) {return;}

  } // doGet
  

  /**
   *
   *
   *
   */
  public void
    destroy(){}

  

} // GenerateImage


