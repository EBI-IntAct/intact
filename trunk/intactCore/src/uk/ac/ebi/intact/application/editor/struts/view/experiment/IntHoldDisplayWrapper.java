/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import uk.ac.ebi.intact.application.editor.struts.framework.util.PageValueBean;

/**
 * This class is a decorator for the Interaction objects in the hold section.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntHoldDisplayWrapper extends AbstractIntDisplayWrapper {

    public String getAction() {
        PageValueBean pvbEdit = new PageValueBean("hold", "add", getAc());
        PageValueBean pvbDel = new PageValueBean("hold", "hide", getAc());
        return "<button type=\"submit\" name=\"intCmd\" "
                + pvbEdit + "\">Add Interaction</button>"
                + "<button type=\"submit\" name=\"intCmd\" "
                + pvbDel + "\">Hide Interaction</button>";
    }
}
