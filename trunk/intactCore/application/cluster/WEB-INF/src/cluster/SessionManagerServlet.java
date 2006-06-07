/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package cluster;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * Searches the IntAct database and display the number of hits as well as all matching identifiers.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Feb-2006</pre>
 */
public class SessionManagerServlet extends HttpServlet implements Externalizable {

    private transient static Logger logger = Logger.getLogger( SessionManagerServlet.class );

    ////////////////////////////
    // Constants

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    public static final String VERSION = "0.1";

    public static final String TEXT_MIME_TYPE = "text/plain";

    public static final String PARAM_NEW_SESSION = "new";
    public static final String PARAM_SESSION_PARAM = "attr";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_CLEAR = "clear";

    public static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    ///////////////////////////
    // Externalizable

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        // the logger is transient as it is not serializable, so reset it upon deserialization.
        SessionManagerServlet.logger = Logger.getLogger( SessionManagerServlet.class );
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
    }

    /////////////////////////////
    // HttpServlet

    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response )
            throws ServletException, IOException {

        // handle parameters
        String newSession = request.getParameter( SessionManagerServlet.PARAM_NEW_SESSION );
        String param = request.getParameter( SessionManagerServlet.PARAM_SESSION_PARAM );
        String size = request.getParameter( SessionManagerServlet.PARAM_SIZE );
        String clear = request.getParameter( SessionManagerServlet.PARAM_CLEAR );

        HttpSession session = null;

        StringBuffer sb = new StringBuffer( 256 );

        // recap all params
        sb.append( "Usage: [new] [param] [size|clear]" ).append( NEW_LINE );
        sb.append( "-----------------------------------------------------------" ).append( NEW_LINE );
        sb.append( PARAM_NEW_SESSION ).append( "=" ).append( newSession ).append( NEW_LINE );
        sb.append( PARAM_SESSION_PARAM ).append( "=" ).append( param ).append( NEW_LINE );
        sb.append( PARAM_SIZE ).append( "=" ).append( size ).append( NEW_LINE );
        sb.append( PARAM_CLEAR ).append( "=" ).append( clear ).append( NEW_LINE );
        sb.append( "-----------------------------------------------------------" ).append( NEW_LINE );


        if ( "TRUE".equalsIgnoreCase( newSession ) ) {

            // invalidating session
            session = request.getSession( false );

            if (session != null)
                session.invalidate();

            session = request.getSession( true );
            long created = session.getCreationTime();
            String time = sdf.format( new Date( created ) );
            String sessionId = session.getId();
            sb.append( "Created new session (" ).append( sessionId ).append( ") creation time: " + time ).append( NEW_LINE );

        } else {

            session = request.getSession( false );

            if (session == null)
                session = request.getSession(true);

            long created = session.getCreationTime();
            String time = sdf.format( new Date( created ) );
            String sessionId = session.getId();
            sb.append( "Reuse existing session (" ).append( sessionId ).append( " / created: " ).append( time ).append( ")" ).append( NEW_LINE );
        }

        if ( param != null && size != null ) {
            try {
                int paramSize = Integer.parseInt( size );
                Byte[] array = new Byte[ paramSize ];
                session.setAttribute( param, array );

                sb.append( "Stored " + paramSize + " bytes in the session (attribute: " + param + ")." ).append( NEW_LINE );

            } catch ( NumberFormatException e ) {
                sb.append( "Size given was not numeric. abort." ).append( NEW_LINE );
            }
        } else if ( param != null && clear != null ) {

            if ( "TRUE".equalsIgnoreCase( clear ) ) {

                Byte[] array = (Byte[]) session.getAttribute( param );
                session.removeAttribute( param );

                if ( array != null ) {
                    sb.append( "Removed from the session " + array.length + " bytes (attribute: " + param + ")." ).append( NEW_LINE );
                } else {
                    sb.append( "Nothing was stored in the session under attribute: " + param + ". abort." ).append( NEW_LINE );
                }
            } else {
                sb.append( "Clear was not set to TRUE. abort." ).append( NEW_LINE );
            }
        } else {
            sb.append( "No parameter with size or clear specified. abort." ).append( NEW_LINE );
        }

        if (session != null)
            sb.append(generateSessionDebugInfo(session));

        sb.append(generateSingletonInfo(request));

        response.setContentType( SessionManagerServlet.TEXT_MIME_TYPE );

        // write servlet output
        ServletOutputStream out = response.getOutputStream();

        out.write( sb.toString().getBytes() );
        out.flush();
        out.close();
    }

    public String getServletInfo() {
        return "Session Manager " + SessionManagerServlet.VERSION + " by Samuel Kerrien (skerrien@ebi.ac.uk)";
    }

    private StringBuffer generateSessionDebugInfo(HttpSession session) {
        StringBuffer sb = new StringBuffer();

        sb.append(NEW_LINE);
        sb.append("Session contents: ").append(NEW_LINE);
        sb.append( "-----------------------------------------------------------" ).append( NEW_LINE );

        Enumeration<String> e = session.getAttributeNames();

        while (e.hasMoreElements())
        {
            String attName = e.nextElement();

            if (session.getAttribute(attName) instanceof Byte[])
            {
                Byte[] value = (Byte[]) session.getAttribute(attName);
                sb.append(attName+": "+value.length+" bytes.").append(NEW_LINE);
            }
            else
            {
                sb.append(attName+": "+session.getAttribute(attName)).append(NEW_LINE);
            }
        }

        return sb;
    }

    private StringBuffer generateSingletonInfo(HttpServletRequest request)
    {
         StringBuffer sb = new StringBuffer();

        sb.append(NEW_LINE);
        sb.append("Singleton info: ").append(NEW_LINE);
        sb.append( "-----------------------------------------------------------" ).append( NEW_LINE );

        MrSingleton singleton = MrSingleton.getInstance(request);

        sb.append("Creation Date: "+singleton.getCreationDate()).append(NEW_LINE);
        sb.append("Hashcode: "+singleton.hashCode()).append(NEW_LINE);

        return sb;
    }
}