/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectProxy  extends BasicObjectProxy implements AnnotatedObject {

    public AnnotatedObjectProxy()
    {
    }

    /**
     * @param uniqueId org.apache.ojb.broker.Identity
     */
    public AnnotatedObjectProxy(PBKey key, Identity uniqueId)
    {
        super(key, uniqueId);
    }

    public AnnotatedObjectProxy(InvocationHandler handler)
    {
        super(handler);
    }

    private AnnotatedObject realSubject()
    {
        try
        {
            return (AnnotatedObject) getRealSubject();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Implements AnnotatedObject's methods
     */

    public String getShortLabel () {
        return realSubject().getShortLabel();
    }

    public void setShortLabel ( String shortLabel ) {
        realSubject().setShortLabel( shortLabel );
    }

    public String getFullName () {
        return realSubject().getFullName();
    }

    public void setFullName ( String fullName ) {
        realSubject().setFullName( fullName );
    }

    ///////////////////////////////////////
    // access methods for associations
    public void setAnnotation ( Collection<Annotation> someAnnotation ) {
        realSubject().setAnnotation( someAnnotation );
    }

    public Collection<Annotation> getAnnotations () {
        return realSubject().getAnnotations();
    }

    public void addAnnotation ( Annotation annotation ) {
        realSubject().addAnnotation( annotation );
    }

    public void removeAnnotation ( Annotation annotation ) {
        realSubject().removeAnnotation( annotation );
    }

    public String getCreator () {
        return realSubject().getCreator();
    }

//    public void setCreator ( String person ) {
//        realSubject().setCreator( person );
//    }

    public String getUpdator () {
        return realSubject().getUpdator();
    }

//    public void setUpdator ( String person ) {
//        realSubject().setUpdator( person );
//    }

    ///////////////////
    // Xref related
    ///////////////////
    public void setXrefs ( Collection<Xref> someXrefs ) {
        realSubject().setXrefs( someXrefs );
    }

    public Collection<Xref> getXrefs () {
        return realSubject().getXrefs();
    }

    public void addXref ( Xref aXref ) {
        realSubject().addXref( aXref );
    }

    public void removeXref ( Xref xref ) {
        realSubject().removeXref( xref );
    }

    ///////////////////
    // Alias related
    ///////////////////
    public void setAliases ( Collection<Alias> someAliases ) {
        realSubject().setAliases( someAliases );
    }

    public Collection<Alias> getAliases () {
        return realSubject().getAliases();
    }

    public void addAlias ( Alias alias ) {
        realSubject().addAlias( alias );
    }

    public void removeAlias ( Alias alias ) {
        realSubject().removeAlias( alias );
    }

    public void setReferences ( Collection<Reference> someReferences ) {
        realSubject().setReferences( someReferences );
    }

    public Collection<Reference> getReferences () {
        return realSubject().getReferences();
    }

    public void addReference ( Reference reference ) {
        realSubject().addReference( reference );
    }

    public void removeReference ( Reference reference ) {
        realSubject().removeReference( reference );
    }

    //attributes used for mapping BasicObjects - project synchron
    public String getCuratorAc () {
        return realSubject().getCuratorAc();
    }

    public void setCuratorAc ( String ac ) {
        realSubject().setCuratorAc( ac );
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