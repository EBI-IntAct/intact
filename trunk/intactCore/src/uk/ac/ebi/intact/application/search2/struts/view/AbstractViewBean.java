/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.view;

import java.io.Serializable;
import java.util.Set;

/**
 * Abstract class containing some basic operations useful to display beans for Intact.
 * Subclasses might for example be based around requirements for particular
 * Intact types (eg BasicObjects) or perhaps concrete type requiring specific functionality
 * (eg Proteins).
 *
 * @author Chris Lewington
 * @version $Id$
 */
public abstract class AbstractViewBean implements Serializable {

    //---- Attributes needed by all subclasses - protected to allow easy access -----

    /**
     * The default link to help pages (to localhost). Typically used in stylesheets.
     */
    private String helpLink;

    /**
     *  A collection of short labels to highlight.
     */
    private Set highlightMap;


    //------------ methhods useful to all subclasses --------------------------

    /**
     * Construst an instance of this class with help link.
     * @param link the link to help page.
     */
    public AbstractViewBean(String link) {
        helpLink = link;
    }

    /**
     * Returns the higlight map.
     * @return map consists of short labels for the current bean.
     */
    public Set getHighlightMap() {
        if (highlightMap == null) {
            initHighlightMap();
        }
        return highlightMap;
    }

    public void setHighlightMap(Set highlightMap) {
        this.highlightMap = highlightMap;
    }

    public String getHelpLink() {
        return helpLink;
    }

    /**
     * The graph buttons are not displayed by default.
     * Subclasses needs to overwrite it to change that behaviour.
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return false;
    }

    /**
     * Instructs the bean to create an HTML content for the object that it wraps.
     */
    public abstract String getHTML();

    public abstract void initHighlightMap();
}
