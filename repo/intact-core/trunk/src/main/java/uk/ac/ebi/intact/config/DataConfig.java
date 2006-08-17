/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public abstract class DataConfig<T,C>
{

    public abstract String getName();

    public abstract void initialize();

    public abstract T getSessionFactory();

    public abstract C getConfiguration();

    private boolean initialized;

    protected void checkInitialization()
    {
        if (!isInitialized())
        {
            initialize();
            this.initialized = true;
        }
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
