/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.model.BasicObject;
import uk.ac.ebi.intact.model.Evidence;
import uk.ac.ebi.intact.model.Institution;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class BasicObjectProxy  extends IntactObjectProxy implements BasicObject {

    public BasicObjectProxy()
    {
    }

    /**
     * @param uniqueId org.apache.ojb.broker.Identity
     */
    public BasicObjectProxy(PBKey key, Identity uniqueId)
    {
        super(key, uniqueId);
    }

    public BasicObjectProxy(InvocationHandler handler)
    {
        super(handler);
    }

    private BasicObject realSubject()
    {
        try
        {
            return (BasicObject) getRealSubject();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Implements BasicObject's methods
     */

    public void setEvidences ( Collection<Evidence> someEvidence ) {
        realSubject().setEvidences( someEvidence );
    }

    public Collection<Evidence> getEvidences () {
        return realSubject().getEvidences();
    }

    public void addEvidence ( Evidence evidence ) {
        realSubject().addEvidence( evidence );
    }

    public void removeEvidence ( Evidence evidence ) {
        realSubject().removeEvidence( evidence );
    }

    public Institution getOwner () {
        return realSubject().getOwner() ;
    }

    public void setOwner ( Institution institution ) {
        realSubject().setOwner( institution );
    }

    public String getOwnerAc () {
        return realSubject().getOwnerAc();
    }

    public void setOwnerAc ( String ac ) {
        realSubject().setOwnerAc( ac );
    }

    @Override
    public boolean equals ( Object o ) {
        return realSubject().equals( o );
    }

    @Override
    public int hashCode () {
        return realSubject().hashCode();
    }
}