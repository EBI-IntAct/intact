/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.util.SearchReplace;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Michael Kleen
 * @version XrefViewBean.java Date: Nov 24, 2004 Time: 12:50:50 PM
 */

/**
 * This class provides JSP view information for a particular AnnotatedObject. Its main purpose is to
 * provide very simple beans for display in an initial search result page. Currenty the types that
 * may be displayed with this bean are
 */
public class XrefViewBean {

    private Xref obj;
    private String helpLink;
    private String searchURL;

    /**
     * @param obj
     * @param link
     * @param searchURL
     */
    public XrefViewBean(final Xref obj, final String link, final String searchURL) {


        this.obj = obj;
        this.helpLink = link;
        this.searchURL = searchURL;
    }


    /**
     * not used ! just here to satified the AbstractViewBean
     */
    public void initHighlightMap() {

    }


    /**
     * Returns the help section. Needs to be reviewed.
     */
    public String getHelpSection() {
        return "protein.single.view";
    }

    /**
     * This is left over from the earlier version - will be removed. It does nothing here.
     */
    public void getHTML(java.io.Writer writer) {
    };




    /**
     * Provides direct access to the wrapped AnnotatedObject itself.
     *
     * @return AnnotatedObject The reference to the wrapped object.
     */
    public Xref getObject() {
        return this.obj;
    }


    /**
     * @return the SearchUrl to the given AnnotatadObject
     */
    public String getSearchUrl() {
        String searchUrl = null;
        String id = this.obj.getPrimaryId();

        if (id != null) {

            Collection dbAnnotation = obj.getCvDatabase().getAnnotations();
            if (null != dbAnnotation) {
                Iterator i = dbAnnotation.iterator();
                while (i.hasNext()) {
                    Annotation annot = (Annotation) i.next();
                    if (annot.getCvTopic().getShortLabel().equals("search-url")) {
                        searchUrl = annot.getAnnotationText();
                        // replace it, then its linkable 
                        searchUrl = SearchReplace.replace(searchUrl, "${ac}", id);
                        return searchUrl;
                    }
                }
            }
        }
        return "-";
    }

    /**
     * @return
     */
    public String getName() {
        return this.obj.getCvDatabase().getShortLabel();
    }

    /**
     * @return
     */
    public String getXrefQualifierName() {
        if (null != this.obj.getCvXrefQualifier()) {
            return this.obj.getCvXrefQualifier().getShortLabel();
        } else
            return "-";
    }

    /**
     * @return
     */
    public String getXrefQualifierURL() {

        return null;

    }

    /**
     * @return
     */
    public XrefViewBean getcvrefType() {
        return null;

    }

    /**
     * @return
     */
    public String getPrimaryId() {

        if (null != this.obj.getPrimaryId()) {
            return this.obj.getPrimaryId();

        } else {
            return "-";
        }

    }

    /**
     * @return
     */
    public String getPrimaryIdSearchUrl() {

        if (null != this.obj.getPrimaryId()) {

            //TODO
            return null;
        } else {
            return "-";
        }

    }

    /**
     * @return
     */
    public String getSecondaryId() {

        if (null != this.obj.getSecondaryId()) {
            return this.obj.getSecondaryId();

        } else {
            return "-";
        }

    }

    /**
     * @return
     */
    public String getSecondaryIdSearchUrl() {

        if (null != this.obj.getSecondaryId()) {

            //TODO
            return null;

        } else {
            return "-";
        }

    }

    /**
     * @return
     */
    public String getType() {

        return "Type:";

    }

    /**
     * @return
     */
    public String getTypeUrl() {

        return this.helpLink + "Xref.cvrefType";

    }


}

