/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUser;
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.WebServiceManager;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.DataSourceException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Properties;


/**
 * That class allow to initialize properly the HTTPSession object
 * with what will be neededlater by the user of the web application.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class InitTag extends TagSupport {

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return EVAL_BODY_INCLUDE;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * option list.
     */
    public int doEndTag() throws JspException {

        // TODO : create a logger here and store it in the session
        initDataSource ();
        initWebService ();

        return EVAL_PAGE;
    } // doEndTag



    /**
     *
     * @throws JspException
     */
    private void initDataSource () throws JspException {

        HttpSession session = pageContext.getSession();

        IntactUser user = (IntactUser) session.getAttribute (Constants.USER_KEY);
        if (null != user) {
            // user already exists
            System.out.println("User already exists ... don't create a new one !");
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
                // TODO : log it
                de.printStackTrace ();
                throw new JspException ("Unable to get a data source.");
            }
            catch (IntactException se) {
                // Unable to construct lists such as topics, db names etc.
                // TODO : log it
                se.printStackTrace ();
            }

        } catch (Exception ioe) {
            throw new JspException ("Fatal error: init tag could not initialize user's HTTPSession.");
        }
    } // initDataSource



    /**
     * Deploy the Tulip web service in the running time.
     */
    private void initWebService () throws JspException {

        // check if the web service has already been deployed
        HttpSession session = pageContext.getSession();

        Boolean isDeployed = (Boolean) session.getAttribute (Constants.IS_WEB_SERVICE_DEPLOYED);

        if ((null == isDeployed) || (false == isDeployed.booleanValue() )) {

            // The configuration file.
            String configFile = uk.ac.ebi.intact.application.hierarchView.struts.Constants.WEB_SERVICE_PROPERTY_FILE;

            System.out.println ("Loading web service's properties");
            Properties props = PropertyLoader.load (configFile);

            if (null != props) {
                String deploymentFile = props.getProperty ("webService.deployment");
                String undeploymentFile = props.getProperty ("webService.undeployment");

                System.out.println ("Properties Loaded :" +
                        "\nwebService.deployment = " + deploymentFile +
                        "\nwebService.undeployment = " + undeploymentFile);

                if ((null == deploymentFile) || (null == undeploymentFile)) {
                    String msg = "Fatal error: init tag could not deploy the Tulip web service.";
                    msg += "Main reason: unable to read properties file";

                    throw new JspException (msg);
                }

                WebServiceManager WSmanager = new WebServiceManager ();
                WSmanager.setDeploymentFile (deploymentFile);
                WSmanager.setUndeploymentFile (undeploymentFile);

                try {
                    WSmanager.deploy();
                    System.out.println ("Tulip web service deployed successfully");
                } catch (Exception e) {
                    System.out.println (e.getMessage() + "\n" + e.toString());
                    throw new JspException ("Fatal error: init tag could not deploy the Tulip web service.");
                }
            } // if

            // The Tulip web service has been deployed
            session.setAttribute (Constants.IS_WEB_SERVICE_DEPLOYED, new Boolean(true));
        } else {
            System.out.println ("Web service already deployed ...");
            // if is not deployed
        }

    } // initWebService

} // InitTag