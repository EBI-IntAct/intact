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
public class Int2AnnotBean extends IntactBean{

    private String interactor_ac;

    private String annotation_ac;

    public Int2AnnotBean() {
    }

    public String getInteractor_ac() {
        return interactor_ac;
    }

    public void setInteractor_ac(String interactor_ac) {
        this.interactor_ac = interactor_ac;
    }

    public String getAnnotation_ac() {
        return annotation_ac;
    }

    public void setAnnotation_ac(String annotation_ac) {
        this.annotation_ac = annotation_ac;
    }
}
