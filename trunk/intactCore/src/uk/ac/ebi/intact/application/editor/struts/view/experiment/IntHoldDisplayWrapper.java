/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;

/**
 * This class is a decorator for the Interaction objects in the hold section.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntHoldDisplayWrapper extends AbstractIntDisplayWrapper {

    public String getAction() {
        // Resource to get labels.
        MessageResources msgres = (MessageResources)
                getPageContext().getServletContext().getAttribute(Globals.MESSAGES_KEY);

        return "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getMessage("exp.int.button.add") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">"
                + "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getMessage("exp.int.button.hide") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">";
    }
}
