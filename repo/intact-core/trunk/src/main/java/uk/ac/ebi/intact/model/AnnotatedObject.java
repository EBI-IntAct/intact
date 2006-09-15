/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 *
 * @see uk.ac.ebi.intact.model.AnnotatedObjectImpl
 */
public interface AnnotatedObject<T extends Xref, A extends Alias> extends BasicObject {

    /**
     * This matches with the column size for short label
     */
    public static final int MAX_SHORT_LABEL_LEN = 20;

    public String getShortLabel();

    public void setShortLabel(String shortLabel);

    public String getFullName();

    public void setFullName(String fullName);

    ///////////////////////////////////////
    // access methods for associations
    public void setAnnotations(Collection<Annotation> someAnnotation);

    public Collection<Annotation> getAnnotations();

    public void addAnnotation(Annotation annotation);

    public void removeAnnotation(Annotation annotation);

    ///////////////////
    // Xref related
    ///////////////////
    public void setXrefs(Collection<T> someXrefs);

    public Collection<T> getXrefs();

    public void addXref(T aXref);

    public void removeXref(T xref);

    ///////////////////
    // Alias related
    ///////////////////
    public void setAliases(Collection<A> someAliases);

    public Collection<A> getAliases();

    public void addAlias( A alias );

    public void removeAlias( A alias );

    public void setReferences(Collection<Reference> someReferences);

    public Collection<Reference> getReferences();

    public void addReference(Reference reference);

    public void removeReference(Reference reference);

//    public AnnotatedObject update(IntactHelper helper) throws IntactException;
//
//    public Annotation updateUniqueAnnotation(CvTopic topic, String description, Institution owner);

    public boolean equals (Object o);

    public int hashCode();

    public String toString();

}
