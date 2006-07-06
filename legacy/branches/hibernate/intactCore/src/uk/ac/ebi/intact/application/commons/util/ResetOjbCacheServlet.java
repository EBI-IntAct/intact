/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.util;

import org.apache.ojb.broker.PersistenceBrokerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When called that servlet is reseting the OJB cache of the calling application.
 *
 *
 * To be added in the web.xml of you application
 * <pre>
 *  &lt;!--  When called that servlet is creating an IntAct user and reset
 *        the content of the OJB cache.
 *    --&gt;
 *  &lt;servlet&gt;
 *      &lt;servlet-name&gt;resetOjbCache&lt;/servlet-name&gt;
 *      &lt;servlet-class&gt;uk.ac.ebi.intact.application.commons.util.ResetOjbCacheServlet&lt;/servlet-class&gt;
 *  &lt;/servlet&gt;
 *
 *  &lt;servlet-mapping&gt;
 *      &lt;servlet-name&gt;resetOjbCache&lt;/servlet-name&gt;
 *      &lt;url-pattern&gt;resetOjbCache&lt;/url-pattern&gt;
 *  &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ResetOjbCacheServlet extends HttpServlet {

    /**
     * Clear the OJB cache of the calling application.
     * This is executed whether the user is calling a GET or POST HTTP request.
     *
     * @param aRequest the user HTTP request
     * @param aResponse the HTTP response
     */
    public void service( HttpServletRequest aRequest, HttpServletResponse aResponse ) {

        PersistenceBrokerFactory.defaultPersistenceBroker().clearCache();
        System.out.println ( "ResetOjbCacheServlet - Cache is now reset via defaultBroker" );
    }
}
