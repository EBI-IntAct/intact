/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.editor.struts.view.EditBean;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Bean to store data for an Interactor (Protein).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinBean extends EditBean implements Serializable {

    // Class Data

    /**
     * The saving new state; this is different to saving state as this state
     * indicates saving a new item. This state is only used by proteins.jsp
     * to save new componets as as result of a search.
     */
    public static final String SAVE_NEW = "saveNew";

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
     * The organism. Empty if there is no biosource. This is necessary to
     * prevent Null pointer exception in validate method of ProteinEditForm.
     */
    private String myOrganism;

    /**
     * Instantiate an object of this class from a Protein instance.
     * @param protein the <code>Protein</code> object.
     */
    public ProteinBean(Protein protein) {
        myAc = protein.getAc();
        myShortLabel = protein.getShortLabel();
        mySPAc = getSPAc(protein);
        myFullName = protein.getFullName();
        setOrganism(protein);
        setEditState(SAVE_NEW);
    }

    /**
     * Instantiate an object of this class from a Component instance.
     * @param component the <code>Component</code> object.
     */
    public ProteinBean(Component component) {
        Interactor interact = component.getInteractor();
        myAc = interact.getAc();
        myShortLabel = interact.getShortLabel();
        mySPAc = getSPAc(interact);
        myFullName = interact.getFullName();
        myRole = component.getCvComponentRole().getShortLabel();
        myStoichiometry = component.getStoichiometry();
        setOrganism(interact);
    }

    // Read only properties.

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

    // Read/Write properties.

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

    // Helper methods

    private void setOrganism(Interactor interact) {
        BioSource biosource = interact.getBioSource();
        if (biosource != null) {
            myOrganism = biosource.getShortLabel();
        }
    }

    private String getSPAc(Interactor interact) {
        for (Iterator iter = interact.getXref().iterator(); iter.hasNext();) {
            Xref xref = (Xref) iter.next();
            // Only consider SwissProt database entries.
            if (xref.getCvDatabase().getShortLabel().equals("SPTR")) {
                return xref.getPrimaryId();
            }
        }
        return "";
    }
}
