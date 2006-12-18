/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import java.io.Serializable;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public abstract class IntactSession
{
    public abstract Object getApplicationAttribute(String name);

    public abstract void setApplicationAttribute(String name, Object attribute);

    public abstract Serializable getAttribute(String name);

    public abstract void setAttribute(String name, Serializable attribute);

    public abstract Object getRequestAttribute(String name);

    public abstract void setRequestAttribute(String name, Object value);

    public abstract boolean containsInitParam(String name);

    public abstract String getInitParam(String name);

    public abstract void setInitParam(String name, String value);

    public abstract boolean isWebapp();
}
