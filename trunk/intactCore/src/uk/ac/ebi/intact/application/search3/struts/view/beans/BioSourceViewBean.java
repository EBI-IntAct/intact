/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.BioSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author Michael Kleen
 * @version BioSourceViewBean.java Date: Nov 14, 2004 Time: 9:09:19 PM
 */

/**
 * This class provides JSP view information for a particular BioSource Object. It is used in the BioSource View
 */
public class BioSourceViewBean extends AbstractViewBean {

    /**
     * The BioSource Object
     */
    private final BioSource obj;

    /**
     * Holds the URL to perform subsequent searches from JSPs - used to build 'complete' URLs for
     * use by JSPs
     */
    private final String searchURL;

    /**
     * Cached search URL, set up on first request for it.
     */
    private String objSearchURL;

    /**
     * The intact type of the wrapped AnnotatedObject. Note that only the interface types are
     * relevant for display purposes - thus any concrete 'Impl' types will be considered to be their
     * interface types in this case (eg a wrapped ProteinImpl will have the intact type of
     * 'Protein'). Would be nice to get rid of the proxies one day ...:-)
     */
    private String intactType;

    /**
     * List of Annotation topics which should be filtered on. The values are set in the bean's
     * constructor.
     */
    private List annotationFilters;


    /**
     * The bean constructor requires an AnnotatedObject to wrap, plus beans on the context path to
     * the search application and the help link. The object itself can be any one of Experiment,
     * Protein, Interaction or CvObject type.
     *
     * @param obj         The AnnotatedObject whose beans are to be displayed
     * @param link        The link to the help pages
     * @param searchURL   The general URL to be used for searching (can be filled in later).
     * @param contextPath The path to the search application.
     */
    public BioSourceViewBean(final BioSource obj, final String link, final String searchURL,
                             final String contextPath) {
        super(link, contextPath);
        this.searchURL = searchURL;
        this.obj = obj;
        this.annotationFilters = new ArrayList();

        //now set up the Annotation filter list
        annotationFilters = new ArrayList();
        annotationFilters.add("remark");
        annotationFilters.add("uniprot-dr-export");
        annotationFilters.add("uniprot-cc-export");
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
        if (this.obj.getFullName() != null) return this.obj.getFullName();
        return "-";
    }


    /**
     * Provides a String representation of a URL to perform a search on this AnnotatedObject's beans
     * (curently via AC)
     *
     * @return String a String representation of a search URL link for the wrapped AnnotatedObject
     */
    public String getObjSearchURL() {

        if (objSearchURL == null) {
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
     * Convenience method to provide a filtered list of Annotations for a given BioSource Object. Useful
     * in JSP display to apply the same filters of the wrapped BioSource Object.
     *
     * @return Collection the filtered List of Annotations (empty if there are none)
     */
    public Collection getFilteredAnnotations() {
         final ArrayList result = new ArrayList();
         // get all Annotations
         Collection someAnnotations = this.obj.getAnnotations();
         // looks what is in it
         for (Iterator it = someAnnotations.iterator(); it.hasNext();) {
             Annotation annotation = (Annotation) it.next();
             //run through the filter
             if ((!annotationFilters.contains(annotation.getCvTopic().getShortLabel()))) {
             // if it's not in the filter get them
             AnnotationViewBean anAnnotationViewBean = new AnnotationViewBean(annotation,
                     this.searchURL);
             result.add(anAnnotationViewBean);
             }
         }

         return result;
     }


    /**
     * Provides access to Annotations of the CVTopics of the  wrraped AnnotadObject stored in
     * SingleViewBeans for the prasentation in the jsp
     *
     * @return Collection with all XrefsViewBeans which  wrapped all Xrefs from the given Object
     */
    public Collection getXrefs() {
        final ArrayList result = new ArrayList();
        // first get all Xrefs
        final Collection someXrefs = this.obj.getXrefs();
        // then create a collection of XrefViewBean
        for (Iterator iterator = someXrefs.iterator(); iterator.hasNext();) {
            final Xref aXref = ((Xref) iterator.next());
            result.add(new XrefViewBean(aXref, this.getHelpLink(), this.searchURL));

        }
        return result;
    }

    /**
     * Provides the basic Intact type of the wrapped AnnotatedObject (ie no java package beans).
     * NOTE: only the INTERFACE types are provided as these are the only ones of interest in the
     * model - display pages are not interested in objects of type XXXImpl. For subclasses of
     * CvObject we only need 'CvObject' for display purposes.
     *
     * @return String The intact type of the wrapped object (eg 'Experiment')
     */
    public String getIntactType() {

        if (intactType == null) {

            final String className = obj.getClass().getName();
            final String basicType = className.substring(className.lastIndexOf(".") + 1);

            intactType = ((basicType.indexOf("Impl") == -1) ?
                    basicType : basicType.substring(0, basicType.indexOf("Impl")));

        }
        return intactType;

    }

    /**
     * @param anAnnotatedObject
     * @return the SearchUrl to the given AnnotatadObject
     */
    public String getSearchUrl(final AnnotatedObject anAnnotatedObject) {

        final String aSearchURL = this.searchURL + anAnnotatedObject.getAc() + "&amp;searchClass=" + getIntactType(
                anAnnotatedObject);
        return aSearchURL;

    }

    /**
     * @return the SearchUrl to the given AnnotatadObject
     */
    public String getSearchUrl() {

        final String aSearchURL = this.searchURL + this.obj.getAc() + "&amp;searchClass=" + getIntactType(
                this.obj);
        return aSearchURL;

    }

    /**
     * @return the FullName to the given AnnotatedObject
     */
    public String getFullname() {
        return this.obj.getFullName();
    }

    /**
     * @param anAnnotatedObject
     * @return String  the intact type of  the annotedObject
     */
    private String getIntactType(final AnnotatedObject anAnnotatedObject) {

        final String objectIntactType;
        final String className = anAnnotatedObject.getClass().getName();
        final String basicType = className.substring(className.lastIndexOf(".") + 1);

        objectIntactType = ((basicType.indexOf("Impl") == -1) ?
                basicType : basicType.substring(0, basicType.indexOf("Impl")));


        return objectIntactType;

    }
}

