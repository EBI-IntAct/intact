/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.struts.taglibs;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;


/**
 * Allows to display a javascript link pointing on the intact documentation
 * directly on the right section.
 * This tag is callin a JSP page which is supposed to display the right section
 * thanks to the <b>section</b> parameter.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DocumentationTag extends TagSupport {

//    public static final String
    public static final int INFORMATIONAL = 1;
    public static final int SUCCESSFUL    = 2;
    public static final int REDIRECTION   = 3;
    public static final int ERROR         = 4;
    public static final int SERVER_ERROR  = 5;

    private static final String PAGE      = "/intact/displayDoc.jsp";
    private static final String URL_BEGIN = "<b><a name=\"#\" onClick=\"w=window.open('";
    private static final String URL_MID   = PAGE + "?section=";
    private static final String URL_END   = "', 'helpwindow', " +
                                            "'width=800,height=500,toolbar=no,directories=no,menu bar=no,scrollbars=yes,resizable=yes');"+
                                            "w.focus();\">"+
                                            "<font color=\"red\">[?]</font></a></b>";
    private static final String PROTOCOL  = "http://";
    private static final String PORT_SEPARATOR = ":";

    /* Tag attribute*/
    private String section;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    /**
     * Skip the body content.
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }


    /**
     * Display the link to the documentation page with eventually the section.
     *
     * @throws javax.servlet.jsp.JspException
     */
    public int doEndTag() throws JspException {

        final HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
        final String servername = request.getServerName();
        final int serverport    = request.getServerPort();

        // display the documentation link only if the page is available
        if (documentationIsAvailable (servername, serverport)) {

            StringBuffer sb = new StringBuffer ();

            sb.append (URL_BEGIN);

            // Assumes that the intact application is on the same server
            // add current server path
            sb.append (PROTOCOL).append (servername).append (PORT_SEPARATOR).append (serverport);

            sb.append (URL_MID);
            if (section != null) sb.append (section);
            sb.append (URL_END);

            try {
                pageContext.getOut().write (sb.toString());
            } catch (IOException ioe) {}

        }

        return EVAL_PAGE; // the rest of the calling JSP is evaluated
    }

    public void release () {}


    private boolean documentationIsAvailable (String servername, int serverport) {
        /**
         * Going back to RFC 2616, you'll notice the following categories of response codes:
         *
         * 1xx - informational
         * 2xx - successful
         * 3xx - redirection
         * 4xx - error
         * 5xx - server error
         */

        try {
            URL url = new URL ("http", servername, serverport, PAGE);
            URLConnection connection = url.openConnection();

            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.connect();
                int response = httpConnection.getResponseCode();

                int code = response / 100;
                if (code == SUCCESSFUL) {
                    return true;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
