/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Dummy Servlet Context to be able to run the testfill.sh script without using a web app. Only setAttribute and
 * getAttribute are implemented. The other methods throw an <code>UnsupportedOperationException</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-Apr-2006</pre>
 */
@Deprecated
public class DummyServletContext implements javax.servlet.ServletContext {
    private Map<String, Object> attMap = new HashMap<String, Object>();

    public ServletContext getContext( String string ) {
        throw new UnsupportedOperationException();
    }

    public int getMajorVersion() {
        throw new UnsupportedOperationException();
    }

    public int getMinorVersion() {
        throw new UnsupportedOperationException();
    }

    public String getMimeType( String string ) {
        throw new UnsupportedOperationException();
    }

    public Set getResourcePaths( String string ) {
        throw new UnsupportedOperationException();
    }

    public URL getResource( String string ) throws MalformedURLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getResourceAsStream( String string ) {
        throw new UnsupportedOperationException();
    }

    public RequestDispatcher getRequestDispatcher( String string ) {
        throw new UnsupportedOperationException();
    }

    public RequestDispatcher getNamedDispatcher( String string ) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Servlet getServlet( String string ) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Enumeration getServlets() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Enumeration getServletNames() {
        throw new UnsupportedOperationException();
    }

    public void log( String string ) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void log( Exception exception, String string ) {
        throw new UnsupportedOperationException();
    }

    public void log( String string, Throwable throwable ) {
        throw new UnsupportedOperationException();
    }

    public String getRealPath( String string ) {
        throw new UnsupportedOperationException();
    }

    public String getServerInfo() {
        throw new UnsupportedOperationException();
    }

    public String getInitParameter( String string ) {
        throw new UnsupportedOperationException();
    }

    public Enumeration getInitParameterNames() {
        throw new UnsupportedOperationException();
    }

    public Object getAttribute( String string ) {
        return attMap.get( string );
    }

    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    public void setAttribute( String string, Object object ) {
        attMap.put( string, object );
    }

    public void removeAttribute( String string ) {
        attMap.remove( string );
    }

    public String getServletContextName() {
        throw new UnsupportedOperationException();
    }
}
