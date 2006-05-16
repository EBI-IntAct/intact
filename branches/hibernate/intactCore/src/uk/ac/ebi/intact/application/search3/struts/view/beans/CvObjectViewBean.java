/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.application.commons.util.AnnotationFilter;
import uk.ac.ebi.intact.application.commons.search.SearchClass;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Xref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Michael Kleen
 * @version SingleViewBean.java Date: Nov 14, 2004 Time: 9:09:19 PM
 */


/**
 * This view bean is used to provide the information for JSP display relating to a particular CvObject. Its main purpose
 * is to provide very simple beans for display in an initial search result page.
 */
public class CvObjectViewBean extends AbstractViewBean {

    /**
     * The CvObject (currently BioSource, CvObjects)
     */
    private final CvObject obj;

    /**
     * Holds the URL to perform subsequent searches from JSPs - used to build 'complete' URLs for use by JSPs
     */
    private final String searchURL;

    /**
     * Cached search URL, set up on first request for it.
     */
    private String objSearchURL;

    /**
     * The intact type of the wrapped CvObject. Note that only the interface types are relevant for display purposes -
     * thus any concrete 'Impl' types will be considered to be their interface types in this case (eg a wrapped
     * ProteinImpl will have the intact type of 'Protein'). Would be nice to get rid of the proxies one day ...:-)
     */
    private String intactType;

    /**
     * The bean constructor requires an CvObject to wrap, plus beans on the context path to the search application and
     * the help link. The object itself can be any one of Experiment, Protein, Interaction or CvObject type.
     *
     * @param obj         The CvObject whose beans are to be displayed
     * @param link        The link to the help pages
     * @param searchURL   The general URL to be used for searching (can be filled in later).
     * @param contextPath The path to the search application.
     */
    public CvObjectViewBean( final CvObject obj, final String link, final String searchURL,
                             final String contextPath ) {
        super( link, contextPath );
        this.searchURL = searchURL;
        this.obj = obj;

    }


    /**
     * not used ! just here to satified the AbstractViewBean.
     */
    @Override
    public void initHighlightMap() {

    }


    /**
     * Returns the help section. Needs to be reviewed.
     *
     * @return a string representation of the help section
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
     * The CvObject's AC. Required in all view types.
     *
     * @return String the AC of the wrapped object.
     */
    public String getObjAc() {
        return this.obj.getAc();
    }

    /**
     * This is currently assumed to be the CvObject's full name. Required by all view types.
     *
     * @return String a description of the CvObject, or a "-" if there is none.
     */
    public String getObjDescription() {
        if ( this.obj.getFullName() != null ) {
            return this.obj.getFullName();
        }
        return "-";
    }


    /**
     * Provides a String representation of a URL to perform a search on this CvObject's beans (curently via AC).
     *
     * @return String a String representation of a search URL link for the wrapped CvObject
     */
    public String getObjSearchURL() {

        if ( objSearchURL == null ) {
            //set it on the first call
            //NB need to get the correct intact type of the wrapped object
            objSearchURL = searchURL + this.obj.getAc() + "&amp;searchClass=" + getIntactType() +
                           "&filter=ac";
        }
        return objSearchURL;
    }


    /**
     * Provides direct access to the wrapped CvObject itself.
     *
     * @return CvObject The reference to the wrapped object.
     */
    public CvObject getObject() {
        return this.obj;
    }

    /**
     * Provides access to Annotations of the CVTopics of the  wrraped AnnotadObject stored in SingleViewBeans for the
     * prasentation in the jsp.
     *
     * @return Collection of all Anotations wrapped in a SingleViewBean
     */
    public Collection<AnnotationViewBean> getAnnotations() {

        // wennn anatations etwas mit if ((label.equals("remark")) || (label.equals("uniprot-dr-export"))) {
        //   return;
        final ArrayList<AnnotationViewBean> result = new ArrayList<AnnotationViewBean>();
        Collection<Annotation> someAnnotations = this.obj.getAnnotations();

        for ( Iterator<Annotation> iterator = someAnnotations.iterator(); iterator.hasNext(); ) {
            Annotation anAnnotation = ( iterator.next() );
            AnnotationViewBean anAnnotationViewBean = new AnnotationViewBean( anAnnotation, "" );
            result.add( anAnnotationViewBean );
        }
        return result;
    }

    /**
     * Convenience method to provide a filtered list of Annotations for a given BioSource Object. Useful in JSP display
     * to apply the same filters of the wrapped BioSource Object.
     *
     * @return Collection the filtered List of Annotations (empty if there are none)
     */
    public Collection<AnnotationViewBean> getFilteredAnnotations() {
        final ArrayList<AnnotationViewBean> result = new ArrayList<AnnotationViewBean>();
        Collection<Annotation> someAnnotations = this.obj.getAnnotations();

        for (Annotation annotation : someAnnotations)
        {
            //run through the filter
            if (false == AnnotationFilter.getInstance().isFilteredOut(annotation))
            {
                // if it's not in the filter get them
                AnnotationViewBean anAnnotationViewBean = new AnnotationViewBean(annotation, this.searchURL);
                result.add(anAnnotationViewBean);
            }
        }

        return result;
    }


    /**
     * Provides access to Annotations of the CVTopics of the wrraped AnnotadObject stored in SingleViewBeans for the
     * prasentation in the jsp.
     *
     * @return Collection with all Xrefs wrapped in a SingleViewBean
     */

    public Collection<XrefViewBean> getXrefs() {
        final ArrayList<XrefViewBean> result = new ArrayList<XrefViewBean>();
        final Collection<Xref> someXrefs = this.obj.getXrefs();

        for (Xref aXref : someXrefs)
        {
            result.add(new XrefViewBean(aXref, this.getHelpLink(), this.searchURL));

        }

        return result;
    }

    /**
     * Provides the basic Intact type of the wrapped CvObject (ie no java package beans). NOTE: only the INTERFACE types
     * are provided as these are the only ones of interest in the model - display pages are not interested in objects of
     * type XXXImpl. For subclasses of CvObject we only need 'CvObject' for display purposes.
     *
     * @return String The intact type of the wrapped object (eg 'Experiment')
     */
    public String getIntactType() {

        if ( intactType == null ) {
            intactType = getIntactType(obj);
        }
        return intactType;

    }

    /**
     * Get the search URL.
     *
     * @param anAnnotatedObject an annotated object.
     *
     * @return the SearchUrl to the given AnnotatadObject
     */
    public String getSearchUrl( final AnnotatedObject anAnnotatedObject ) {

        final String aSearchURL = this.searchURL + anAnnotatedObject.getAc() + "&amp;searchClass=" + getIntactType( anAnnotatedObject ) + "&filter=ac";
        return aSearchURL;

    }

    /**
     * Get the search URL.
     *
     * @return the SearchUrl to the given AnnotatadObject
     */
    public String getSearchUrl() {

        final String aSearchURL = this.searchURL + this.obj.getAc() + "&amp;searchClass=" + getIntactType( this.obj ) + "&filter=ac";
        return aSearchURL;

    }

    /**
     * Get the fullname.
     *
     * @return the FullName to the given CvObject
     */
    public String getFullname() {
        return this.obj.getFullName();
    }

}