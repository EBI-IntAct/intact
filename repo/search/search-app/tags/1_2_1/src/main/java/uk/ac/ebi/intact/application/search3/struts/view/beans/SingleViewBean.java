/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.searchengine.SearchClass;

/**
 * @author Michael Kleen
 * @version SingleViewBean.java Date: Nov 14, 2004 Time: 9:09:19 PM
 */

/**
 * This class provides JSP view information for a particular AnnotatedObject. Its main purpose is to provide very simple
 * beans for display in an initial search result page. Currenty the types that may be displayed with this bean are
 */
public class SingleViewBean extends AbstractViewBean {

    /**
     * The AnnotatedObject (currently BioSource, CvObjects)
     */
    private final AnnotatedObject obj;

    /**
     * Holds the URL to perform subsequent searches from JSPs - used to build 'complete' URLs for use by JSPs
     */
    private final String searchURL;

    /**
     * Cached search URL, set up on first request for it.
     */
    private String objSearchURL;

    /**
     * The intact type of the wrapped AnnotatedObject. Note that only the interface types are relevant for display
     * purposes - thus any concrete 'Impl' types will be considered to be their interface types in this case (eg a
     * wrapped ProteinImpl will have the intact type of 'Protein'). Would be nice to get rid of the proxies one day
     * ...:-)
     */
    private String intactType;


    /**
     * The bean constructor requires an AnnotatedObject to wrap, plus beans on the context path to the search
     * application and the help link. The object itself can be any one of Experiment, Protein, Interaction or CvObject
     * type.
     *
     * @param obj         The AnnotatedObject whose beans are to be displayed
     * @param link        The link to the help pages
     * @param searchURL   The general URL to be used for searching (can be filled in later).
     * @param contextPath The path to the search application.
     */
    public SingleViewBean( final AnnotatedObject obj, final String link, final String searchURL,
                           final String contextPath ) {
        super( link, contextPath );
        this.searchURL = searchURL;
        this.obj = obj;
    }


    /**
     * not used ! just here to satified the AbstractViewBean
     */
    @Override
    public void initHighlightMap() {

    }


    /**
     * Returns the help section. Needs to be reviewed.
     */
    @Override
    public String getHelpSection() {
        return "protein.single.view";
    }


    /**
     * The intact name for an object is its shortLabel. Required in all view types.
     *
     * @return String the object's Intact name.
     */
    public String getObjIntactName() {
        return this.obj.getShortLabel();
    }

    /**
     * The AnnotatedObject's AC. Required in all view types.
     *
     * @return String the AC of the wrapped object.
     */
    public String getObjAc() {
        return this.obj.getAc();
    }

    /**
     * This is currently assumed to be the AnnotatedObject's full name. Required by all view types.
     *
     * @return String a description of the AnnotatedObject, or a "-" if there is none.
     */
    public String getObjDescription() {
        if ( this.obj.getFullName() != null ) {
            return this.obj.getFullName();
        }
        return "-";
    }


    /**
     * Provides a String representation of a URL to perform a search on this AnnotatedObject's beans (curently via AC)
     *
     * @return String a String representation of a search URL link for the wrapped AnnotatedObject
     */
    public String getObjSearchURL() {

        if ( objSearchURL == null ) {
            //set it on the first call
            //NB need to get the correct intact type of the wrapped object
            objSearchURL = searchURL + this.obj.getAc() + "&amp;searchClass=" + getIntactType();
        }
        return objSearchURL;
    }


    /**
     * Provides direct access to the wrapped AnnotatedObject itself.
     *
     * @return AnnotatedObject The reference to the wrapped object.
     */
    public AnnotatedObject getObject() {
        return this.obj;
    }


    /**
     * Provides the basic Intact type of the wrapped AnnotatedObject (ie no java package beans). NOTE: only the
     * INTERFACE types are provided as these are the only ones of interest in the model - display pages are not
     * interested in objects of type XXXImpl. For subclasses of CvObject we only need 'CvObject' for display purposes.
     *
     * @return String The intact type of the wrapped object (eg 'Experiment')
     */
    public String getIntactType() {

        if ( intactType == null ) {
            intactType = SearchClass.valueOfMappedClass(obj.getClass()).getShortName();
        }
        return intactType;

    }

    /**
     * @param anAnnotatedObject
     *
     * @return the SearchUrl to the given AnnotatadObject
     */
    public String getSearchUrl( final AnnotatedObject anAnnotatedObject ) {

        final String aSearchURL = this.searchURL + anAnnotatedObject.getAc() + "&amp;searchClass=" + getIntactType(
                anAnnotatedObject );
        return aSearchURL;

    }

    /**
     * @return the SearchUrl to the given AnnotatadObject
     */
    public String getSearchUrl() {

        final String aSearchURL = this.searchURL + this.obj.getAc() + "&amp;searchClass=" + getIntactType(
                this.obj );
        return aSearchURL;

    }

    /**
     * @return the FullName to the given AnnotatedObject
     */
    public String getFullname() {
        return this.obj.getFullName();
    }

}

