/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 *
 * @see uk.ac.ebi.intact.model.AnnotatedObjectImpl
 */
public interface AnnotatedObject extends BasicObject {

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

    public String getCreator();

    public String getUpdator();

    ///////////////////
    // Xref related
    ///////////////////
    public void setXrefs(Collection<Xref> someXrefs);

    public Collection<Xref> getXrefs();

    public void addXref(Xref aXref);

    public void removeXref(Xref xref);

    ///////////////////
    // Alias related
    ///////////////////
    public void setAliases(Collection<Alias> someAliases);

    public Collection<Alias> getAliases();

    public void addAlias( Alias alias );

    public void removeAlias( Alias alias );

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
