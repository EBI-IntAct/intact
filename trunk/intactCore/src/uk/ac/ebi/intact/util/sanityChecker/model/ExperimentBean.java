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
public class ExperimentBean extends AnnotatedBean{

    private String relatedexperiment_ac;

    private String biosource_ac;

    private String detectmethod_ac;

    private String identmethod_ac;

    private String pmid;

    public String getRelatedexperiment_ac() {
        return relatedexperiment_ac;
    }

    public void setRelatedexperiment_ac(String relatedexperiment_ac) {
        this.relatedexperiment_ac = relatedexperiment_ac;
    }

    public String getBiosource_ac() {
        return biosource_ac;
    }

    public void setBiosource_ac(String biosource_ac) {
        this.biosource_ac = biosource_ac;
    }

    public String getDetectmethod_ac() {
        return detectmethod_ac;
    }

    public void setDetectmethod_ac(String detectmethod_ac) {
        this.detectmethod_ac = detectmethod_ac;
    }

    public String getIdentmethod_ac() {
        return identmethod_ac;
    }

    public void setIdentmethod_ac(String identmethod_ac) {
        this.identmethod_ac = identmethod_ac;
    }

}
