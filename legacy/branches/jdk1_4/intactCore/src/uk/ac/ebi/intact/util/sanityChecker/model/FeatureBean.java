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
public class FeatureBean  extends AnnotatedBean {
    private String linkedfeature_ac;

    private String identification_ac;

    private String featuretype_ac;

    private String component_ac;

    public String getLinkedfeature_ac() {
        return linkedfeature_ac;
    }

    public void setLinkedfeature_ac(String linkedfeature_ac) {
        this.linkedfeature_ac = linkedfeature_ac;
    }

    public String getIdentification_ac() {
        return identification_ac;
    }

    public void setIdentification_ac(String identification_ac) {
        this.identification_ac = identification_ac;
    }

    public String getFeaturetype_ac() {
        return featuretype_ac;
    }

    public void setFeaturetype_ac(String featuretype_ac) {
        this.featuretype_ac = featuretype_ac;
    }

    public String getComponent_ac() {
        return component_ac;
    }

    public void setComponent_ac(String component_ac) {
        this.component_ac = component_ac;
    }
}
