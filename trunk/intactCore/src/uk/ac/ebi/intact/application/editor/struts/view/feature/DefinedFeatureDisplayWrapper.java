/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;
import org.apache.taglibs.display.TableDecorator;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.AbstractIntDisplayWrapper;

/**
 * This class is a decorator for the defined Feature. It is responsible for
 * formatting the Auto-Complete button.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class DefinedFeatureDisplayWrapper extends TableDecorator {

    public String getAction() {
        // Resource to get labels.
        MessageResources msgres = (MessageResources)
                getPageContext().getServletContext().getAttribute(Globals.MESSAGES_KEY);

        String label = ((DefinedFeatureBean) getObject()).getShortLabel();

        return "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getMessage("button.auto.complete") + "\""
                + " onclick=\"setDefinedFeature('" + label + "');\">";
    }
}
