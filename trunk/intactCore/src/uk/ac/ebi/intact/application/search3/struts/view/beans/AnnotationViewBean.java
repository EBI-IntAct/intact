package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;

/**
 * @author Michael Kleen
 * @version AnnotationViewBean.java Date: Nov 23, 2004 Time: 4:24:13 PM
 */
public class AnnotationViewBean {
    private final Annotation anAnnotation;
    private final String searchURL;


    public AnnotationViewBean(final Annotation anAnnotation, final String searchURL) {
        this.anAnnotation = anAnnotation;
        this.searchURL = searchURL;
    }


    public String getText() {
        return this.anAnnotation.getAnnotationText();
    }


    public String getName() {
        return this.anAnnotation.getCvTopic().getShortLabel();

    }


    public String getSearchUrl() {
        return this.searchURL;
    }


    public Annotation getObject() {
        return this.anAnnotation;
    }


    private String getIntactType(final AnnotatedObject anAnnotatedObject) {

        final String objectIntactType;
        final String className = anAnnotatedObject.getClass().getName();
        final String basicType = className.substring(className.lastIndexOf(".") + 1);

        objectIntactType = ((basicType.indexOf("Impl") == -1) ?
                basicType : basicType.substring(0, basicType.indexOf("Impl")));


        return objectIntactType;

    }
}