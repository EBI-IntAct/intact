/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.taglibs.display.TableDecorator;
import uk.ac.ebi.intact.model.Interaction;

/**
 * This class is the super class for all the decorator classes for Interactions.
 * List. This class provides common methods for formatting data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractIntDisplayWrapper extends TableDecorator {

    // Abstract Methods

    /**
     * Subclasses must provide the implementation for this method.
     * @return The action string for buttons.
     */
    public abstract String getAction();

    /**
     * Returns the topic with a link to show its contents in a window.
     * @return the topic as a browsable link.
     */
    public String getShortLabel() {
        String label = ((Interaction) getObject()).getShortLabel();
        return "<a href=\"" + "javascript:show('Interaction', '"
                + label + "')\"" + ">" + label + "</a>";
    }

    protected String getAc() {
        return ((Interaction) getObject()).getAc();
    }
}
