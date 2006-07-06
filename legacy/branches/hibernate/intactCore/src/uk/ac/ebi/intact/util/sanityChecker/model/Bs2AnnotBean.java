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
public class Bs2AnnotBean extends IntactBean{

    private String annotation_ac;

    private String biosource_ac;

    public Bs2AnnotBean() {
    }

    public String getAnnotation_ac() {
        return annotation_ac;
    }

    public void setAnnotation_ac(String annotation_ac) {
        this.annotation_ac = annotation_ac;
    }

    public String getBiosource_ac() {
        return biosource_ac;
    }

    public void setBiosource_ac(String biosource_ac) {
        this.biosource_ac = biosource_ac;
    }
}
