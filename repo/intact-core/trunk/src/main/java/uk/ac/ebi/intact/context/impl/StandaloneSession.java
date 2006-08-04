/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context.impl;

import uk.ac.ebi.intact.context.IntactSession;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class StandaloneSession extends IntactSession
{

    private Map<String,Serializable> sessionMap;
    private Map<String,Object> applicationMap;

    public StandaloneSession()
    {
        this.sessionMap = new HashMap<String,Serializable>();
        this.applicationMap =  new HashMap<String,Object>();
    }

    public Object getApplicationAttribute(String name)
    {
        return applicationMap.get(name);
    }

    public void setApplicationAttribute(String name, Object attribute)
    {
       applicationMap.put(name, attribute);
    }

    public Serializable getAttribute(String name)
    {
        return sessionMap.get(name);
    }

    public void setAttribute(String name, Serializable attribute)
    {
        sessionMap.put(name,attribute);
    }

    public boolean containsInitParam(String name)
    {
        return false;
    }

    public String getInitParam(String name)
    {
        return null;
    }

    public boolean isWebapp()
    {
        return false;
    }
}
