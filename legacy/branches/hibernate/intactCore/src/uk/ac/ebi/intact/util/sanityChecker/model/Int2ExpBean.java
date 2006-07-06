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
public class Int2ExpBean extends IntactBean{

    private String interaction_ac;

    private String experiment_ac;

    public Int2ExpBean() {
    }

    public String getInteraction_ac() {
        return interaction_ac;
    }

    public void setInteraction_ac(String interaction_ac) {
        this.interaction_ac = interaction_ac;
    }

    public String getExperiment_ac() {
        return experiment_ac;
    }

    public void setExperiment_ac(String experiment_ac) {
        this.experiment_ac = experiment_ac;
    }
}
