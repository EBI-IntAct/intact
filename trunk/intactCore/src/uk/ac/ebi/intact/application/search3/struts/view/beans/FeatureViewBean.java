
/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.SearchReplace;

import java.util.*;

/**
 * This view bean is used to access the information relating to Features for display by JSPs. For
 * every Component of an Interaction that contains feature information, the will be a feature view
 * bean related to it. TODO: The ranges need handling - a Feature can have more than one...
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class FeatureViewBean extends AbstractViewBean {


    /**
     * The Feature we want the beans for.
     */
    private Feature feature;

    /**
     * Holds the URL to perform subsequent searches from JSPs - used to build 'complete' URLs for
     * use by JSPs
     */
    private String searchURL;

    /**
     * URL for searching for CvFeatureType.
     */
    private String cvFeatureTypeSearchURL = "";

    /**
     * URL for searching for CvFeatureIdentification.
     */
    private String cvFeatureIdentSearchURL = "";

    /**
     * Map of retrieved DB URLs already retrieved from the DB. This is basically a cache to avoid
     * recomputation every time a CvDatabase URL is requested.
     */
    private Map dbUrls;


    /**
     * Constructor. Takes a Feature that relates to an Interaction, and wraps the beans for it.
     *
     * @param feature     The Feature we are interested in
     * @param link        The link to help pages
     * @param searchURL   The standard search URL
     * @param contextPath The platform context path for the application
     */
    public FeatureViewBean(Feature feature, String link, String searchURL, String contextPath) {
        super(link, contextPath);
        this.searchURL = searchURL;
        this.feature = feature;
        dbUrls = new HashMap();
    }



//---------------- basic abstract methods that need implementing --------------
    /**
     * Adds the shortLabel of the Feature to an internal list used later for highlighting in a
     * display. NOT SURE IF WE STILL NEED THIS!!
     */
    public void initHighlightMap() {
        Set set = new HashSet(1);
        set.add(feature.getShortLabel());
        setHighlightMap(set);
    }


    /**
     * Returns the help section.
     */
    public String getHelpSection() {
        return "protein.single.view";
    }

    /**
     * This is left over from the earlier version - will be removed. It does nothing here.
     */
    public void getHTML(java.io.Writer writer) {
    };

    //-------------------------- the useful stuff ------------------------------------

    public String getFeatureName() {
        return feature.getShortLabel();

    }

    public Feature getFeature() {
        return feature;
    }

    /**
     * Provides a view bean for any bound Feature.
     *
     * @return featureViewBean a view bean for the Feature bound to this one, or null if there is no
     *         bound feature
     */
    public FeatureViewBean getBoundFeatureView() {
        if (feature.getBoundDomain() != null)
            return new FeatureViewBean(feature.getBoundDomain(),
                    getHelpLink(), searchURL, getContextPath());
        return null;
    }

    /**
     * Provides the feature type short label.
     *
     * @return String the CvFeatureType shortLabel, or '-' if the feature type itself is null.
     */
    public String getFeatureType() {

        if (feature.getCvFeatureType() != null) {
            // get the complete result
            String label = feature.getCvFeatureType().getShortLabel();
            // get the first char
            String begin = label.substring(0, 1);
            // put it to uppercase
            begin = begin.toUpperCase();
            // get the rest and add it to the beginning
            String rest = label.substring(1, label.length());
            return begin + rest;
            

        }
        return "-";

    }

    /**
     * Provides the short label of the feature identification.
     *
     * @return String the CvFeatureIdentification shortLabel, or '-' if the identification object
     *         itself is null
     */
    public String getFeatureIdentificationName() {

        if (feature.getCvFeatureIdentification() != null)
            return feature.getCvFeatureIdentification().getShortLabel();
        return "-";
    }

    public String getProteinName() {

        return feature.getComponent().getInteractor().getShortLabel();

    }

    /**
     * Provides the full name of the feature identification.
     *
     * @return String the CvFeatureIdentification full name, or '-' if the identification object
     *         itself or its full name are null
     */
    public String getFeatureIdentFullName() {

        if ((feature.getCvFeatureIdentification() != null) &&
                (feature.getCvFeatureIdentification().getFullName() != null))
            return feature.getCvFeatureIdentification().getFullName();
        return "-";
    }

    public Collection getFeatureXrefs() {
        return feature.getXrefs();

    }

    //------------------------------ useful URLs --------------------------------------

    /**
     * Provides a String representation of a URL to perform a search on CvFeatureType
     *
     * @return String a String representation of a search URL link for CvFeatureType.
     */
    public String getCvFeatureTypeSearchURL() {

        if ((cvFeatureTypeSearchURL == "") && (feature.getCvFeatureType() != null)) {
            //set it on the first call
            //get the CvInteraction object and pull out its AC
            cvFeatureTypeSearchURL = searchURL + feature.getCvFeatureType().getAc()
                    + "&amp;searchClass=CvFeatureType";
        }
        return cvFeatureTypeSearchURL;
    }

    /**
     * Provides a String representation of a URL to perform a search on CvFeatureIdentification
     *
     * @return String a String representation of a search URL link for CvFeatureIdentification.
     */
    public String getCvFeatureIdentSearchURL() {

        if ((cvFeatureIdentSearchURL == "") && (feature.getCvFeatureIdentification() != null)) {
            //set it on the first call
            //get the CvInteraction object and pull out its AC
            cvFeatureIdentSearchURL = searchURL + feature.getCvFeatureIdentification().getAc()
                    + "&amp;searchClass=CvFeatureIdentification";
        }
        return cvFeatureIdentSearchURL;
    }


    /**
     * Provides a String representation of a URL to provide acces to an Xrefs' database (curently
     * via AC). The URL is at present stored via an Annotation for the Xref in the Intact DB
     * itself.
     *
     * @param xref The Xref for which the DB URL is required
     * @return String a String representation of a DB URL link for the Xref, or a '-' if there is no
     *         stored URL link for this Xref
     */
    public String getPrimaryIdURL(Xref xref) {

        // Check if the id can be hyperlinked
        String searchUrl = (String) dbUrls.get(xref.getCvDatabase());
        if (searchUrl == null) {
            //not yet requested - do it now and cache it..
            Collection annotations = xref.getCvDatabase().getAnnotations();
            Annotation annot = null;
            for (Iterator it = annotations.iterator(); it.hasNext();) {
                annot = (Annotation) it.next();
                if (annot.getCvTopic().getShortLabel().equals("search-url")) {
                    //found one - we are done
                    searchUrl = annot.getAnnotationText();
                    break;
                }
            }

            //cache it - even if the URL is null, because it may be
            //requested again
            dbUrls.put(xref.getCvDatabase(), searchUrl);
        }

        //if it isn't null, fill it in properly and return
        if (searchUrl != null) {
            //An Xref's primary can't be null - the constructor doesn't allow it..
            searchUrl = SearchReplace.replace(searchUrl, "${ac}", xref.getPrimaryId());

        }
        return searchUrl;
    }


}

