/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.sequence;

import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.model.Protein;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Protein view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinViewBean extends SequenceViewBean {

    /**
     * The map of menus for this view.
     */
    private transient Map<String, List<String>> myMenus = new HashMap<String, List<String>>();

    /**
     * Override to provide menus for this view.
     * @return a map of menus for this view. It consists of common menus for
     * annotation/xref, biosource and interactor type
     */
    @Override
    public Map<String, List<String>> getMenus() throws IntactException {
        return myMenus;
    }

    @Override
    public void loadMenus() throws IntactException {
        myMenus.clear();

        myMenus.putAll(super.getMenus(Protein.class));

//        myMenus.putAll(super.getMenus());

        int mode = (getInteractorType() == null) ? 1 : 0;
        myMenus.put("Polymer", EditorMenuFactory.getInstance().getProteinMenu(mode));
    }
}
