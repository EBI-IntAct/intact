/*
 Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import java.util.ResourceBundle;

import uk.ac.ebi.intact.model.Interaction;

/**
 * This class contains data for an Interaction row in the experiment editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionRowData {

    private String myAc;

    private String myShortLabel;

    private String myFullName;

    private Interaction myInteraction;
    
    /**
     * This contains HTML script for action button. Allows access from
     * subclasses.
     */
    protected String myAction;

    /**
     * This constructor is mainly used for creating an instance to find it in a
     * collection.
     * @param ac the ac is required as it is used for equals method.
     */
    public InteractionRowData(String ac) {
        myAc = ac;
    }

    /**
     * Creates an instance of this class using given Interaction.
     * @param inter the interaction to wrap this instance around.
     */
    public InteractionRowData(Interaction inter) {
        this(inter.getAc(), inter.getShortLabel(), inter.getFullName());
        myInteraction = inter;
    }
    
    private InteractionRowData(String ac, String shortlabel, String fullname) {
        myAc = ac;
        myShortLabel = shortlabel;
        myFullName = fullname;
        setAction();
    }

    // Setter method for suclasses to override.
    protected void setAction() {
        // Resource bundle to access the message resources to set keys.
        ResourceBundle msgres = ResourceBundle
                .getBundle("uk.ac.ebi.intact.application.editor.MessageResources");
        myAction = "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getString("exp.int.button.edit") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">"
                + "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getString("exp.int.button.del") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">";
    }
    
    // Getter methods
    public String getAc() {
        return myAc;
    }

    public String getShortLabel() {
        return myShortLabel;
    }

    public String getFullName() {
        return myFullName;
    }

    public Interaction getInteraction() {
        return myInteraction;
    }
    
    public String getAction() {
        return myAction;
    }

    // Override equals method.

    /**
     * True if ACs match
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Same class; can cast safely.
            InteractionRowData other = (InteractionRowData) obj;
            return myAc.equals(other.myAc);
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return myAc.hashCode();
    }
}