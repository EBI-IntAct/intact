/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.view.utils;

/**
 * Simple JavaBean to represent a GO source data.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class SourceBean {

    // ----------------------------------------------------------- Instance variables

    private String id;

    private String description;

    private String SourceBrowserUrl;

    private String directHighlightUrl;

    private boolean selected;

    private String applicationPath;

    // ----------------------------------------------------------- Constructors

    public SourceBean (String id,
                       String description,
                       String sourceBrowserUrl,
                       String directHighlightUrl,
                       boolean clickable,
                       String applicationPath) {
        this.id = id;
        this.description = description;
        this.SourceBrowserUrl = sourceBrowserUrl;
        this.directHighlightUrl = directHighlightUrl;
        this.selected = clickable;
        this.applicationPath = applicationPath;
    }

    // ------------------------------------------------------------- Properties

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceBrowserUrl() {
        return SourceBrowserUrl;
    }

    public void setSourceBrowserUrl(String sourceBrowserUrl) {
        this.SourceBrowserUrl = sourceBrowserUrl;
    }

    public String getDirectHighlightUrl() {
        return directHighlightUrl;
    }

    public void setDirectHighlightUrl(String directHighlightUrl) {
        this.directHighlightUrl = directHighlightUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Return a string representation of this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("SourceBean[Id=");
        sb.append(id);
        sb.append(", description=");
        sb.append(description);
        sb.append(", selected=");
        sb.append(selected);
        sb.append("]");
        return (sb.toString());
    }
}


