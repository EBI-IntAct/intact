package uk.ac.ebi.intact.application.hierarchView.business.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.*;
import java.text.*;

import java.io.IOException;

import org.w3c.dom.Document;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.business.image.ConvertSVG;

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
    
    // take the parameter in the request
    String format = (String) aRequest.getParameter("format");

    String className = null;
    Properties propertiesBusiness = PropertyLoader.load (uk.ac.ebi.intact.application.hierarchView.business.Constants.PROPERTY_FILE);

    if (null != propertiesBusiness) {
      className = propertiesBusiness.getProperty ("hierarchView.image.format." + format + ".class" );
    }

    ConvertSVG convert = ConvertSVG.getConvertSVG(className);

    // Encode the off-screen image into a JPEG and send it to the client

    String typeMime = convert.getMimeType();
    aResponse.setContentType(typeMime);
   

    // get the current user session
    HttpSession session = aRequest.getSession();
    ImageBean ib = (ImageBean) session.getAttribute (uk.ac.ebi.intact.application.hierarchView.struts.Constants.ATTRIBUTE_IMAGE_BEAN);

    if (null == ib) {
      System.out.println("ib == null");
      return;
    }

    Document document = ib.getDocument();
    if (null != document) {
      try {
	byte[] imageData = convert.convert(document);
	if (null != imageData) {
	  System.out.println("lenght = " + imageData.length);
	  out.write (imageData);
	}
	else System.out.println("imageData is null");
      }
      catch (Exception e) {
	e.printStackTrace(); 
	System.out.println("Impossible to convert the document");
      }
    }
    else System.out.println("document is null");
    

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


