/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.model.*;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Bean to store data for an Interactor (Protein).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinBean extends AbstractEditBean implements Serializable {

    // Class Data

    /**
     * The saving new state; this is different to saving state as this state
     * indicates saving a new item. This state is only used by proteins.jsp
     * to save new componets as as result of a search.
     */
    private static final String SAVE_NEW = "saveNew";

    /**
     * Identifier for an error bean.
     */
    public static final String ERROR = "error";

    // Instance Data

    /**
     * The object this instance is created with.
     */
    private Component myComponent;

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
    private float myStoichiometry = 1.0f;

    /**
     * The organism.
     */
    private String myOrganism;

    /**
     * A flag to indicate that this instance is marked for deletion.
     */
    private boolean myDeleteFlag;

    /**
     * Instantiate an object of this class from a Protein instance.
     * @param protein the <code>Protein</code> object.
     */
    public ProteinBean(Protein protein) {
        myComponent = new Component();
        myComponent.setOwner(protein.getOwner());
        myComponent.setInteractor(protein);
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
        myComponent = component;
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

    public Component getComponent() {
        return myComponent;
    }

    public Component getComponent(EditUserI user) throws SearchException {
        CvComponentRole newrole = getRole(user);
        if (newrole != null) {
            myComponent.setCvComponentRole(newrole);
        }
        myComponent.setStoichiometry(getStoichiometry());
        BioSource neworg = getOrganism(user);
        if (neworg != null) {
            myComponent.getInteractor().setBioSource(neworg);
        }
        return myComponent;
    }

    public String getAc() {
        return myAc;
    }

    public String getShortLabel() {
        return myShortLabel;
    }

    public String getShortLabelLink() {
        return getLink("Protein", myShortLabel);
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

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the short labels
     * for both objects match.
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Can safely cast it.
            ProteinBean other = (ProteinBean) obj;
            return this.myShortLabel.equals(other.myShortLabel)
                    && this.myRole.equals(other.myRole);
        }
        return false;
    }

    /**
     * Marks this bean as it is ready for deletion from the view.
     */
    public void markForDelete() {
        myDeleteFlag = true;
    }

    /**
     * True if this bean is marked for delete.
     * @return true if this bean is marked for delete.
     */
    public boolean isMarkedForDelete() {
        return myDeleteFlag;
    }

    public void update(EditUserI user) throws SearchException {
        CvComponentRole role = (CvComponentRole) user.getObjectByLabel(
                    CvComponentRole.class, myRole);
        myComponent.setCvComponentRole(role);
        myComponent.setStoichiometry(myStoichiometry);
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
            if (xref.getCvDatabase().getShortLabel().equals("sptr")) {
                return xref.getPrimaryId();
            }
        }
        return "";
    }

    private CvComponentRole getRole(EditUserI user) throws SearchException  {
        if (myRole != null) {
            return (CvComponentRole) user.getObjectByLabel(
                    CvComponentRole.class, myRole);
        }
        return null;
    }

    private BioSource getOrganism(EditUserI user) throws SearchException  {
        if (myOrganism != null) {
            return (BioSource) user.getObjectByLabel(BioSource.class, myOrganism);
        }
        return null;
    }
}
