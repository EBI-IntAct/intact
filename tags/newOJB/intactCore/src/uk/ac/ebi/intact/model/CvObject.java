/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * Represents a controlled vocabulary object. CvObject is derived from
 * AnnotatedObject to allow to store annotation of the term within the
 * object itself, thus allowing to build an integrated dictionary.
 *
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
public abstract class CvObject extends AnnotatedObjectImpl {

    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    protected CvObject() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that CvObjects cannot be
     * created without at least a shortLabel and an owner specified.
     * @param shortLabel The memorable label to identify this CvObject
     * @param owner The Institution which owns this CvObject
     * @exception NullPointerException thrown if either parameters are not specified
     */
    protected CvObject(Institution owner, String shortLabel) {

        //super call sets up a valid AnnotatedObject (and also CvObject, as there is
        //nothing more to add)
        super(shortLabel, owner);
    }

} // end CvObject




