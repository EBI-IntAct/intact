/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Iterator;

/**
 * Represents a controlled vocabulary object. CvObject is derived from AnnotatedObject to allow to store annotation of
 * the term within the object itself, thus allowing to build an integrated dictionary.
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
public abstract class CvObject extends AnnotatedObjectImpl {

    /**
     * no-arg constructor provided for compatibility with subclasses that have no-arg constructors.
     */
    protected CvObject() {
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
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( obj == null ) {
            return false;
        }

        if ( ! getClass().equals( obj.getClass() ) ) {
            return false;
        }

        if ( !( obj instanceof CvObject ) ) {
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
    public int hashCode() {
        Xref idXref = getIdentityXref();
        return idXref != null ? 29 * idXref.getPrimaryId().hashCode()
               : 29 * getShortLabel().hashCode();
    }

    /**
     * Returns the Identity xref
     *
     * @return the Identity xref or null if there is no Identity xref found.
     */
    public Xref getIdentityXref() {
        for ( Iterator iter = getXrefs().iterator(); iter.hasNext(); ) {
            Xref xref = (Xref) iter.next();
            CvXrefQualifier xq = (CvXrefQualifier) xref.getCvXrefQualifier();
            if ( ( xq != null ) && CvXrefQualifier.IDENTITY.equals( xq.getShortLabel() ) ) {
                return xref;
            }
        }
        return null;
    }
} // end CvObject




