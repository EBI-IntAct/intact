/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.util.Utilities;

import java.util.*;

/**
 * Funtional description of an object.
 *
 * @author hhe
 */
public class Annotation extends BasicObject {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    protected String cvTopicAc;

    // replaced by cvTopicAc conform to synchron specifications
    // private String topicAc;

    /**
     * Text describing one aspect of the annotation of
     * an object.
     */
    protected String annotationText;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public CvTopic cvTopic;

    ///////////////////////////////////////
    //access methods for attributes

    public String getAnnotationText() {
        return annotationText;
    }
    public void setAnnotationText(String annotationText) {
        this.annotationText = annotationText;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvTopic getCvTopic() {
        return cvTopic;
    }
    public void setCvTopic(CvTopic cvTopic) {
        this.cvTopic = cvTopic;
    }

    //attributes used for mapping BasicObjects - project synchron
    public String getCvTopicAc() {
        return this.cvTopicAc;
    }
    public void setCvTopicAc(String ac){
        this.cvTopicAc = ac;
    }

    /** Returns true if the "important" attributes are equal.
     */
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        if (!super.equals(o)) return false;

        final Annotation annotation = (Annotation) o;

        return (Utilities.equals(this.cvTopic, annotation.getCvTopic()) &&
                Utilities.equals(this.annotationText, annotation.getAnnotationText()));
    }

    /** This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){

        int code = super.hashCode();

        if (null != cvTopic)        code = 29 * code + cvTopic.hashCode();
        if (null != annotationText) code = 29 * code + annotationText.hashCode();

        return code;
    }

} // end Annotation




