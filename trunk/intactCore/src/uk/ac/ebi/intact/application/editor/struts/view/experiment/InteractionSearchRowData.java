/*
 Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import java.util.ResourceBundle;

import uk.ac.ebi.intact.model.Interaction;

/**
 * This class contains data for an Interaction search row in the experiment editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionSearchRowData extends InteractionRowData {

    public InteractionSearchRowData(Interaction interaction) {
        super(interaction);
    }

    // SOverride the super method to provide this class's own version of action
    protected void setAction() {
        // Resource bundle to access the message resources to set keys.
        ResourceBundle msgres = ResourceBundle
                .getBundle("uk.ac.ebi.intact.application.editor.MessageResources");

        myAction = "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getString("exp.int.button.add") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">"
                + "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getString("exp.int.button.hide") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">";
    }
}