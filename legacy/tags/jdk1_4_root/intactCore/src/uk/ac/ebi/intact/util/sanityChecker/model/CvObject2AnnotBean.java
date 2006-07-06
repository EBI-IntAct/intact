/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class CvObject2AnnotBean extends IntactBean {

    private String cvobject_ac;

    private String annotation_ac;

    public CvObject2AnnotBean() {
    }

    public String getCvobject_ac() {
        return cvobject_ac;
    }

    public void setCvobject_ac(String cvobject_ac) {
        this.cvobject_ac = cvobject_ac;
    }

    public String getAnnotation_ac() {
        return annotation_ac;
    }

    public void setAnnotation_ac(String annotation_ac) {
        this.annotation_ac = annotation_ac;
    }
}
