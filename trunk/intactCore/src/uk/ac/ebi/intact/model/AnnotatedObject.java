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

    public String getShortLabel();

    public void setShortLabel(String shortLabel);

    public String getFullName();

    public void setFullName(String fullName);

    ///////////////////////////////////////
    // access methods for associations
    public void setAnnotation(Collection someAnnotation);

    public Collection getAnnotations();

    public void addAnnotation(Annotation annotation);

    public void removeAnnotation(Annotation annotation);

    public Person getCurator();

    public void setCurator(Person person);

    ///////////////////
    // Xref related
    ///////////////////
    public void setXrefs(Collection someXrefs);

    public Collection getXrefs();

    public void addXref(Xref aXref);

    public void removeXref(Xref xref);

    ///////////////////
    // Alias related
    ///////////////////
    public void setAliases(Collection someAliases);

    public Collection getAliases();

    public void addAlias( Alias alias );

    public void removeAlias( Alias alias );

    public void setReferences(Collection someReferences);

    public Collection getReferences();

    public void addReference(Reference reference);

    public void removeReference(Reference reference);

    //attributes used for mapping BasicObjects - project synchron
    public String getCuratorAc();

    public void setCuratorAc(String ac);

    public AnnotatedObject update(IntactHelper helper) throws IntactException;

    public Annotation updateUniqueAnnotation(CvTopic topic, String description, Institution owner);

    public boolean equals (Object o);

    public int hashCode();

    public String toString();

}
