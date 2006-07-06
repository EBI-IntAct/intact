/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Product;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorProxy  extends AnnotatedObjectProxy implements Interactor {

    public InteractorProxy()
    {
    }

    /**
     * @param uniqueId org.apache.ojb.broker.Identity
     */
    public InteractorProxy(PBKey key, Identity uniqueId)
    {
        super(key, uniqueId);
    }

    public InteractorProxy(InvocationHandler handler)
    {
        super(handler);
    }

    private Interactor realSubject()
    {
        try
        {
            return (Interactor) getRealSubject();
        }
        catch (Exception e)
        {
            return null;
        }
    }


    /**
     * Implements AnnotatedObject methods
     */

    public BioSource getBioSource () {
        return realSubject().getBioSource();
    }

    public void setBioSource ( BioSource bioSource ) {
        realSubject().setBioSource( bioSource );
    }

    public void setActiveInstances ( Collection someActiveInstance ) {
        realSubject().setActiveInstances( someActiveInstance );
    }

    public Collection getActiveInstances () {
        return realSubject().getActiveInstances();
    }

    public void addActiveInstance ( Component component ) {
        realSubject().addActiveInstance( component );
    }

    public void removeActiveInstance ( Component component ) {
        realSubject().removeActiveInstance( component );
    }

    public void setProducts ( Collection someProduct ) {
        realSubject().setProducts( someProduct );
    }

    public Collection getProducts () {
        return realSubject().getProducts();
    }

    public void addProduct ( Product product ) {
        realSubject().addProduct( product );
    }

    public void removeProduct ( Product product ) {
        realSubject().removeProduct( product );
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getBioSourceAc () {
        return realSubject().getBioSourceAc();
    }

    public void setBioSourceAc ( String bioSourceAc ) {
        realSubject().setBioSourceAc( bioSourceAc );
    }

    public boolean equals ( Object o ) {
        return realSubject().equals( o );
    }

    public int hashCode () {
        return realSubject().hashCode();
    }
}