/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: Exp2AnnotBean.java,v 1.1 2005/07/28 16:13:29 catherineleroy Exp $
 */
public class Exp2AnnotBean extends IntactBean{

    private String experiment_ac;

    private String annotation_ac;

    public Exp2AnnotBean() {
    }

    public String getExperiment_ac() {
        return experiment_ac;
    }

    public void setExperiment_ac(String experiment_ac) {
        this.experiment_ac = experiment_ac;
    }

    public String getAnnotation_ac() {
        return annotation_ac;
    }

    public void setAnnotation_ac(String annotation_ac) {
        this.annotation_ac = annotation_ac;
    }
}
