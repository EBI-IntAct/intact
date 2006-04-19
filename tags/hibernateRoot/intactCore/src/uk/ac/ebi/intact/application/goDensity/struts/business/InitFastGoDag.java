/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.struts.business;

/**
 * When Tomcat is started, the singleton-pattern class FastGoDag will be
 * instanciated to be available once a request is send.
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

import uk.ac.ebi.intact.application.goDensity.business.dag.FastGoDag;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public class InitFastGoDag extends HttpServlet {

   public void init(ServletConfig servletConfig) throws ServletException {
       super.init(servletConfig);
       System.out.println("\nSetting the cache for tomcat: FastGoDag instance will be created ...\n");
       ServletContext oContext = servletConfig.getServletContext();
       oContext.setAttribute("dataCache", FastGoDag.getInstance());
   }
}