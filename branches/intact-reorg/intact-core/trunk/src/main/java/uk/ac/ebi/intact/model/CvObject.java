/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.DiscriminatorValue;
import java.util.Iterator;
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
@DiscriminatorColumn(name="objclass")
@DiscriminatorValue("uk.ac.ebi.intact.model.CvObject")
public abstract class CvObject extends AnnotatedObjectImpl {

    /**
     * no-arg constructor provided for compatibility with subclasses that have no-arg constructors.
     */
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

    @ManyToMany
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
        if ( this == obj ) {
            return true;
        }

        if ( obj == null ) {
            return false;
        }

        final CvObject other = (CvObject) obj;

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
        int result = getClass().hashCode();

        Xref idXref = getIdentityXref();

        //need check as we still have no-arg constructor...
        if ( idXref != null ) {
            result = 29 * result + idXref.getPrimaryId().hashCode();
        } else {
            result = 29 * result + getShortLabel().hashCode();
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




