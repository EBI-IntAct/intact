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
public class ComponentBean extends IntactBean {

    private String interactor_ac;
    private String interaction_ac;
    private String role;
    private float stoichiometry;

    public ComponentBean() {
    }

    public String getInteractor_ac() {
        return interactor_ac;
    }

    public void setInteractor_ac(String interactor_ac) {
        this.interactor_ac = interactor_ac;
    }

    public String getInteraction_ac() {
        return interaction_ac;
    }

    public void setInteraction_ac(String interaction_ac) {
        this.interaction_ac = interaction_ac;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public float getStoichiometry() {
        return stoichiometry;
    }

    public void setStoichiometry(float stoichiometry) {
        this.stoichiometry = stoichiometry;
    }
}
