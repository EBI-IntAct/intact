/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.servlet;

import org.apache.log4j.PropertyConfigurator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Inititialize the Logger with the log4J properties file.
 *
 * Created by Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class LoggingInit extends HttpServlet {

    public void init() {

        String prefix =  getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");

        // if the logging-init-file is not set (cf. web.xml), then no point in trying

        if (file != null) {

            PropertyConfigurator.configure(prefix + file);

        }
    } // init

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
    }
} // LoggingInit

