/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.viewx.interaction;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.application.editor.struts.view.EditBean;

import java.io.Serializable;

/**
 * Bean to store data for an Interactor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinBean extends EditBean implements Serializable {

    // Instance Data

    /**
     * The AC.
     */
    private String myAc;

    /**
     * The short label.
     */
    private String myShortLabel;

    /**
     * Swiss-Prot AC.
     */
    private String mySPAc;

    /**
     * The full name of the Protein.
     */
    private String myFullName;

    /**
     * The role of this Protein.
     */
    private String myRole;

    /**
     * The stoichiometry.
     */
    private float myStoichiometry;

    /**
     * The organism.
     */
    private String myOrganism;

    /**
     * Instantiate an object of this class from a Component instance.
     * @param component the <code>Component</code> object.
     */
    public ProteinBean(Component component) {
        Interactor interact = component.getInteractor();
        myAc = interact.getAc();
        myShortLabel = interact.getShortLabel();
        mySPAc = ""; // need to clarify this.
        myFullName = interact.getFullName();
        myRole = component.getCvComponentRole().getShortLabel();
        myStoichiometry = component.getStoichiometry();
        BioSource biosource = component.getExpressedIn();
        if (biosource != null) {
            myOrganism = biosource.getShortLabel();
        }
    }

    public String getAc() {
        return myAc;
    }

    public String getShortLabel() {
        return myShortLabel;
    }

    public String getSpAc() {
        return mySPAc;
    }

    public String getFullName() {
        return myFullName;
    }

    public String getRole() {
        return myRole;
    }

    public void setRole(String role) {
       myRole = role;
    }

    public float getStoichiometry() {
        return myStoichiometry;
    }

    public void setStoichiometry(float stoichiometry) {
        myStoichiometry = stoichiometry;
    }

    public String getOrganism() {
        return myOrganism;
    }

    public void setOrganism(String organism) {
        myOrganism = organism;
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the internal
     * keys for both objects match.
     *
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
//        if ((obj != null) && (getClass() == obj.getClass())) {
//            // Can safely cast it.
//            return myKey == ((InteractorBean) obj).getKey();
//        }
        return false;
    }
}
