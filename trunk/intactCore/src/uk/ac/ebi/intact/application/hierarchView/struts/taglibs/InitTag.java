/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUser;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.WebServiceManager;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.DataSourceException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import java.util.logging.Logger;


/**
 * That class allow to initialize properly the HTTPSession object
 * with what will be neededlater by the user of the web application.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class InitTag extends TagSupport {

    private static Logger logger = Logger.getLogger("InitTag");

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return SKIP_BODY;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * option list.
     */
    public int doEndTag() throws JspException {

        initDataSource ();
        initWebService ();

        // TODO : forward to an error page in case of exception

        return EVAL_PAGE;
    } // doEndTag



    /**
     * Create a datasource for the user and store it into the user's session.
     * @throws JspException
     */
    private void initDataSource () throws JspException {

        HttpSession session = pageContext.getSession();

        IntactUser user = (IntactUser) session.getAttribute (Constants.USER_KEY);
        if (null != user) {
            // user already exists
            logger.info ("User already exists ... don't create a new one !");
            return ;
        }

        // Store an IntactUser Object in the Session
        try {
            // Save the context to avoid repeat calls.
            ServletContext servletContext = pageContext.getServletContext();

            // Name of the mapping file and data source.
            String repfile = servletContext.getInitParameter (Constants.MAPPING_FILE);
            String ds      = servletContext.getInitParameter (Constants.DATA_SOURCE);

            // Create an instance of IntactUser which we'll store in the Session
            user = null;
            try {
                user = new IntactUser (repfile, ds);
                session.setAttribute (Constants.USER_KEY, user);
            }
            catch (DataSourceException de) {
                // Unable to get a data source...can't proceed
                 de.printStackTrace ();
                logger.severe(de.toString());
                throw new JspException ("Unable to get a data source.");
            }
            catch (IntactException se) {
                // Unable to construct lists such as topics, db names etc.
                logger.severe(se.toString());
                se.printStackTrace ();
            }

        } catch (Exception ioe) {
            throw new JspException ("Fatal error: init tag could not initialize user's HTTPSession.");
        }
    } // initDataSource



    /**
     * Deploy the Tulip web service in the running time.
     * @throws JspException
     */
    private void initWebService () throws JspException {

        // ServletContext allows to share data for every user.
        ServletContext servletContext = pageContext.getServletContext();
        WebServiceManager webServiceManager =
                (WebServiceManager) servletContext.getAttribute (Constants.WEB_SERVICE_MANAGER);

        if (null == webServiceManager) {
                try {
                    WebServiceManager WSmanager = new WebServiceManager ();
                    WSmanager.deploy();
                    logger.info ("Tulip web service deployed successfully");
                    servletContext.setAttribute (Constants.WEB_SERVICE_MANAGER, WSmanager);
                } catch (Exception e) {
                    logger.info (e.getMessage() + "\n" + e.toString());
                    throw new JspException ("Fatal error: init tag could not deploy the Tulip web service.");
                }
        } else {
            logger.info ("Web service already deployed ...");
        }

    } // initWebService

} // InitTag