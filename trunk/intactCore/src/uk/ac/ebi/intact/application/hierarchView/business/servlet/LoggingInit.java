/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.logging.LogManager;

/**
 * Inititialize the Logger with the log4J properties file.
 *
 * Created by Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class LoggingInit extends HttpServlet {

    public void init() {

        String prefix =  getServletContext().getRealPath("/");
        String file = getInitParameter("logging-init-file");

        // if the logging-init-file is not set (cf. web.xml), then no point in trying

        if (file != null) {
            LogManager logManager = LogManager.getLogManager();

            // open config file as a stream
            InputStream is = null;
            try {
                is = new FileInputStream (new File (prefix+file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                logManager.readConfiguration (is) ;
                getServletContext().log ("File: " + prefix+file + " loaded");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    } // init

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
    }
} // LoggingInit

