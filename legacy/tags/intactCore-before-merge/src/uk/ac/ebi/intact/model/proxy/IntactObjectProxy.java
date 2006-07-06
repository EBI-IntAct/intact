/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.VirtualProxy;
import uk.ac.ebi.intact.model.IntactObject;

import java.lang.reflect.InvocationHandler;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class IntactObjectProxy  extends VirtualProxy implements IntactObject {

    public IntactObjectProxy()
    {
    }

    /**
     * @param uniqueId org.apache.ojb.broker.Identity
     */
    public IntactObjectProxy(PBKey key, Identity uniqueId)
    {
        super(key, uniqueId);
    }

    public IntactObjectProxy(InvocationHandler handler)
    {
        super(handler);
    }

    private IntactObject realSubject()
    {
        try
        {
            return (IntactObject) getRealSubject();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Return the class of the real subject.
     *
     * @return the class of the real subject
     */
    public Class getRealClassName() {
        return realSubject().getClass();
    }

    /**
     * Implements IntactObject's methods
     */

    public String getAc () {
        return realSubject().getAc();
    }

    public void setAc ( String ac ) {
        realSubject().setAc( ac );
    }

    public Timestamp getCreated () {
        return realSubject().getCreated();
    }

    public void setCreated ( Date created ) {
        realSubject().setCreated( created );
    }

    public Timestamp getUpdated () {
        return realSubject().getUpdated();
    }

    public void setUpdated ( Date updated ) {
        realSubject().setUpdated( updated );
    }

    @Override
    public boolean equals ( Object o ) {
        return realSubject().equals( o );
    }

    @Override
    public int hashCode () {
        return realSubject().hashCode();
    }

    /**
     * A call to this method should materialize the real object.
     * @return String representation of the proxied object.
     */
    @Override
    public String toString() {
        return realSubject().toString();

    }
}