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
public class InteractionsBean {
    private String protein1_ac;
    private String protein2_ac;
    private String interaction_ac;

    public String getProtein1_ac() {
        return protein1_ac;
    }

    public void setProtein1_ac(String protein1_ac) {
        this.protein1_ac = protein1_ac;
    }

    public String getProtein2_ac() {
        return protein2_ac;
    }

    public void setProtein2_ac(String protein2_ac) {
        this.protein2_ac = protein2_ac;
    }

    public String getInteraction_ac() {
        return interaction_ac;
    }

    public void setInteraction_ac(String interaction_ac) {
        this.interaction_ac = interaction_ac;
    }
}
