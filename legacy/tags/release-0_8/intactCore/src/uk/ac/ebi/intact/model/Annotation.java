/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;


/**
 * Funtional description of an object.
 *
 * @author hhe
 * @version $Id$
 */
public class Annotation extends BasicObjectImpl {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String cvTopicAc;

    // replaced by cvTopicAc conform to synchron specifications
    private String topicAc;

    /**
     * Text describing one aspect of the annotation of
     * an object.
     */
    private String annotationText;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private CvTopic cvTopic;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private Annotation() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid Annotation instance. A valid instance must have at least
     * a non-null Institution specified. A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     * @param owner The <code>Institution</code> which 'owns' this BioSource
     * @param topic Refers to the controlled vocabulary topic this Annotation relates
     * to. This should be non-null.
     * @exception NullPointerException thrown if no Institution specified.
     */
    public Annotation(Institution owner, CvTopic topic) {

        //super call sets creation time data
        super(owner);
        if(topic == null) throw new NullPointerException("valid Annotation must have an associated topic!");
        this.cvTopic = topic;
    }

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

    /**
     * Equality for Annotations is currently based on equality for
     * <code>CvTopics</code> and annotationText (a String).
     * @see uk.ac.ebi.intact.model.CvTopic
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     */
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;

        final Annotation annotation = (Annotation) o;

        if(cvTopic != null) {
        if (!cvTopic.equals(annotation.cvTopic)) return false;
        }
        else {
           if (annotation.cvTopic != null) return false;
        }

        //get to here and cvTopics are equal (null or non-null)
        if(annotationText != null) {
            return annotationText.equals(annotation.annotationText);
        }
        return annotation.annotationText == null;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){

        int code = 29;
        if(cvTopic != null) code = 29*code + cvTopic.hashCode();
        if (null != annotationText) code = 29 * code + annotationText.hashCode();

        return code;
    }

    public String toString() {
        return "Annotation[type: " + (cvTopic != null ? cvTopic.getShortLabel() : "" ) +
               ", text: " + annotationText + "]";
    }

} // end Annotation




