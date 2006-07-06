/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

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
    public String cvTopicAc;


    private String topicAc;

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
} // end Annotation




