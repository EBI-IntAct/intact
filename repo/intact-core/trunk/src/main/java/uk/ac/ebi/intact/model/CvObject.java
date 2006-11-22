/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Represents a controlled vocabulary object. CvObject is derived from AnnotatedObject to allow to store annotation of
 * the term within the object itself, thus allowing to build an integrated dictionary.
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
@Entity
@Table(name = "ia_controlledvocab")
@DiscriminatorColumn(name="objclass", discriminatorType = DiscriminatorType.STRING, length = 255)
public abstract class CvObject extends AnnotatedObjectImpl<CvObjectXref,CvObjectAlias> implements Searchable
{
    private String objClass;

    public CvObject() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that CvObjects cannot be created without at least a shortLabel and an
     * owner specified.
     *
     * @param shortLabel The memorable label to identify this CvObject
     * @param owner      The Institution which owns this CvObject
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    protected CvObject( Institution owner, String shortLabel ) {
        //super call sets up a valid AnnotatedObject (and also CvObject, as there is
        //nothing more to add)
        super( shortLabel, owner );
    }

    @Column(name = "objclass", insertable = false, updatable = false)
    public String getObjClass()
    {
        return objClass;
    }

    public void setObjClass(String objClass)
    {
        this.objClass = objClass;
    }

    @ManyToMany (cascade = {CascadeType.PERSIST})    
    @JoinTable(
        name="ia_cvobject2annot",
        joinColumns={@JoinColumn(name="cvobject_ac")},
        inverseJoinColumns={@JoinColumn(name="annotation_ac")}
    )
    @Override
    public Collection<Annotation> getAnnotations()
    {
        return super.getAnnotations();
    }

    @OneToMany (mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Override
    public Collection<CvObjectXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany (mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Override
    public Collection<CvObjectAlias> getAliases() {
        return super.getAliases();
    }

    /**
     * Equality for CvObject is currently based on equality for primary id of Xref having the qualifier of identity and
     * short label if there is xref of identity. We need to equals method to avoid circular references when invoking
     * equals methods
     *
     * @param obj The object to check
     *
     * @return true if given object has an identity xref and its primary id matches to this' object's primary id or
     *         short label if there is no identity xref.
     *
     * @see Xref
     */
    @Override
    public boolean equals( Object obj ) {
        if (!super.equals(obj))
        {
            return false;
        }

        if (!(obj instanceof CvObject))
        {
            return false;
        }

        final CvObject other = (CvObject) obj;

        if (ac != null && other.getAc() != null)
        {
            if (ac.equals(other.getAc()))
            {
                return true;
            }
        }

        // Check this object has an identity xref first.
        Xref idXref = getIdentityXref();
        Xref idOther = other.getIdentityXref();

        if ( ( idXref != null ) && ( idOther != null ) ) {
            // Both objects have the identity xrefs
            return idXref.getPrimaryId().equals( idOther.getPrimaryId() );
        }
        if ( ( idXref == null ) && ( idOther == null ) ) {
            // Revert to short labels.
            return getShortLabel().equals( other.getShortLabel() );
        }
        return false;
    }


    /**
     * This class overwrites equals. To ensure proper functioning of HashTable, hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();

        Xref idXref = getIdentityXref();

        //need check as we still have no-arg constructor...
        if ( idXref != null ) {
            result = 29 * result + idXref.getPrimaryId().hashCode();
        } else {
            result = 29 * result + ((getShortLabel() == null)? 31 :getShortLabel().hashCode());
        }

        return result;
    }

    /**
     * Returns the Identity xref
     *
     * @return the Identity xref or null if there is no Identity xref found.
     */
    @Transient
    public Xref getIdentityXref() {
        for (Xref xref : getXrefs())
        {
            CvXrefQualifier xq = xref.getCvXrefQualifier();
            if ((xq != null) && CvXrefQualifier.IDENTITY.equals(xq.getShortLabel()))
            {
                return xref;
            }
        }
        return null;
    }
} // end CvObject




