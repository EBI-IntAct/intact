package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

import uk.ac.ebi.intact.application.hierarchView.business.IntactUser;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.WebServiceManager;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * That class allow to check if the initialisation has been properly done.
 * Prevents the user to use the application in that state.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class CheckInitTag  extends TagSupport {

    private String forwardOnError;

    public String getForwardOnError() {
        return forwardOnError;
    }

    public void setForwardOnError(String forwardOnError) {
        this.forwardOnError = forwardOnError;
    }


    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return SKIP_BODY;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag.
     * This will check if the datasource and the web service are OK.
     */
    public int doEndTag() throws JspException {

        // check the datasource
        HttpSession session = pageContext.getSession();
        IntactUser user = (IntactUser) session.getAttribute (Constants.USER_KEY);
        if (null == user) {
            System.out.println ("Data source unavailable, forward to home page");
            // user doesn't exists
            try {
                // FORWARD
                super.pageContext.forward (this.forwardOnError);
                return SKIP_PAGE;
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // check the web service edeployment
        ServletContext servletContext = pageContext.getServletContext();
        WebServiceManager webServiceManager =
                (WebServiceManager) servletContext.getAttribute (Constants.WEB_SERVICE_MANAGER);

        if ((null == webServiceManager) || (false == webServiceManager.isRunning() )) {
            System.out.println ("Web Service not properly deployed, forward to home page");
            try {
                // FORWARD
                super.pageContext.forward (this.forwardOnError);
                 return SKIP_PAGE;
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return EVAL_PAGE;

    } // doEndTag

} // CheckInitTag
