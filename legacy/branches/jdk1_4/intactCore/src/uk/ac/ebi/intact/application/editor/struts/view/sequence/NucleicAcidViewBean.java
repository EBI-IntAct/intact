/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.sequence;

import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.business.IntactException;

import java.util.HashMap;
import java.util.Map;

/**
 * NucleicAcid view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class NucleicAcidViewBean extends SequenceViewBean {

    /**
     * The map of menus for this view.
     */
    private transient Map myMenus = new HashMap();

    /**
     * Override to provide menus for this view.
     * @return a map of menus for this view. It consists of common menus for
     * annotation/xref, biosource and interactor type
     */
    public Map getMenus() throws IntactException {
        return myMenus;
    }

    public void loadMenus() throws IntactException {
        myMenus.clear();
        myMenus.putAll(super.getMenus());

        int mode = (getInteractorType() == null) ? 1 : 0;
        myMenus.put("Polymer", EditorMenuFactory.getInstance().getNucleicAcidMenu(mode));
    }
}
