/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.application.commons.search.SearchClass;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.Serializable;
import java.util.Set;

/**
 * Abstract class containing some basic operations useful to display beans for Intact. Subclasses might for example be
 * based around requirements for particular Intact types (eg BasicObjects) or perhaps concrete type requiring specific
 * functionality (eg Proteins).
 *
 * @author Chris Lewington
 * @version $Id$
 */
public abstract class AbstractViewBean implements Serializable {

    /**
     * Logger for that class.
     */
    protected transient static final Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

    /**
     * The default link to help pages (to localhost). Typically used in stylesheets.
     */
    private String helpLink;

    /**
     * Context path of the current application.
     */
    private String contextPath;

    /**
     * A collection of short labels to highlight.
     */
    private Set highlightMap;

    /**
     * Construst an instance of this class with help link.
     *
     * @param link        the link to help page.
     * @param contextPath the path of the application.
     */
    public AbstractViewBean( String link, String contextPath ) {
        helpLink = link;
        this.contextPath = contextPath;
    }

    /**
     * Returns the higlight map.
     *
     * @return map consists of short labels for the current bean.
     *
     * @see #setHighlightMap
     */
    public Set getHighlightMap() {
        if ( highlightMap == null ) {
            initHighlightMap();
        }
        return highlightMap;
    }

    /**
     * Specifies the highlight map value.
     *
     * @param highlightMap set the value of the highlight map.
     *
     * @see #getHighlightMap
     */
    public void setHighlightMap( Set highlightMap ) {
        this.highlightMap = highlightMap;
    }

    /**
     * Returns the url based link to the help section based on the servlet context path.
     *
     * @return String which represents the url based link to the intact help section
     */
    public String getHelpLink() {
        return helpLink;
    }

    /**
     * Returns the context path as string based on the servlet context path.
     *
     * @return String which represents the context path
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * The graph buttons are not displayed by default. Subclasses needs to overwrite it to change that behaviour.
     *
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return false;
    }

    /**
     * Performs the initialisation of HighlightMap.
     */
    public abstract void initHighlightMap();

    /**
     * Returns the help section value.
     *
     * @return a String representing the help section value
     */
    public abstract String getHelpSection();

    /**
     * String representation of the type of an AnnotatedObject.
     *
     * @param anAnnotatedObject
     * @return String  the intact type of  the annotedObject
     */
    protected String getIntactType( final AnnotatedObject anAnnotatedObject ) {
         return SearchClass.valueOfMappedClass(anAnnotatedObject.getClass()).getShortName();
    }
}