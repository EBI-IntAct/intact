/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.application.search3.struts.view.beans.AbstractViewBean;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.util.SearchReplace;

import java.util.*;

/**
 * <p>
 * A bean used to support display of a single Protein. This view is different to
 * other AnnotatedObjects and so must be handled seperately. The bean is used by
 * JSPs to provide easy access to the Protein's data in a form suitable for
 * display in a web page. Xrefs of the Protein are an exception, because they each
 * contain detailed String data and therefore the JSP itself should extract what
 * it requires from each Xref for display.
 * </p>
 * <p>
 * The methods available from this bean are based on the data  required by new search
 * interface mock pages, created June 2004. According to that simple web page, the
 * information to be supplied from this bean is as follows:
 * <ul>
 * <li>Intact name</li>
 * <li>Source (ie BioSource beans)</li>
 * <li>Description (ie the full name)</li>
 * <li>gene names (found via the Aliases)</li>
 * <li>Xrefs</li>
 * <li>sequence length</li>
 * <li>CRC64 checksum</li>
 * <li>the Protein sequence itself</li>
 * </ul>
 * </p>
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class ProteinViewBean extends AbstractViewBean {

    /**
     * The Protein for which the view is required.
     */
    private Protein protein;

    /**
     * The gene names related to the Protein. Buffer so we can return a String
     */
    private StringBuffer geneNames;

    /**
     * Holds the URL to perform subsequent searches from JSPs - used
     * to build 'complete' URLs for use by JSPs
     */
    private String searchURL;

    /**
     * Map of retrieved DB URLs already retrieved from the DB. This
     * is basically a cache to avoid recomputation every time a CvDatabase URL
     * is requested.
     */
    private Map dbUrls;

    //----------------- a selection of particular URLs of use -----------------

    /**
     * String URL for searching on the Protein itself
     */
    private String protSearchURL;

    /**
     * String URL for searching on the Protein's BioSource (uses the source AC)
     */
    private String bioSearchURL;



    /**
     * The bean constructor requires a Protein to wrap, plus beans on
     * the context path to the search application and the help link.
     * @param prot The Protein whose beans are to be displayed
     * @param link The link to the help pages
     * @param searchURL The general URL to be used for searching (can be filled in later).
     * @param contextPath The path to the search application.
     */
    public ProteinViewBean(Protein prot, String link, String searchURL, String contextPath) {
        super(link, contextPath);
        dbUrls = new HashMap();
        this.searchURL = searchURL;
        protein = prot;
    }


    //---------------- basic abstract methods that need implementing --------------
    /**
     * Adds the shortLabel of the Protein to an internal list used
     * later for highlighting in a display.
     * NOT SURE IF WE STILL NEED THIS!!
     */
    public void initHighlightMap() {
            Set set  = new HashSet( 1 );
            set.add( protein.getShortLabel() );
            setHighlightMap(set);
        }


    /**
     * Returns the help section.
     */
    public String getHelpSection() {
        return "protein.single.view";
    }

    /**
     * This is left over from the earlier version - will be removed.
     * It does nothing here.
     */
    public void getHTML( java.io.Writer writer ){};

    //----------------- the useful stuff for the JSPs -----------------------------

    /**
     * Basic accessor, provided in case anything ever needs access to the wrapped object.
     * @return Protein the Protein instance wrapped by this view bean.
     */
    public Protein getProtein() {
        return protein;
    }

    public String getProteinIntactName() {
        return protein.getShortLabel();
    }

    public String  getProteinAc() {
        return protein.getAc();
    }

    /**
     * NB In the webpage mockup, this is a hyperlink to the BioSource
     * help page...
     * @return
     */
    public String getProteinDescription() {
        return protein.getFullName();
    }


    //--------------- Useful BioSource data --------------------

    /**
     * Provides the AC for this Protein's BioSource.
     * @return String the BioSource AC
     */
    public String getBioAc() {
        return protein.getBioSource().getAc();
    }

    /**
     * Provides the Intact Name used to identify this Protein's BioSource.
     * @return String the BioSource's intact name (ie shortLabel)
     */
    public String getBioIntactName() {
        return protein.getBioSource().getShortLabel();
    }

    /**
     * Provides the full name of this Protein's BioSource.
     * @return String the BioSource's 'common' name.
     */
    public String getBioFullName() {
        return protein.getBioSource().getFullName();
    }

    //------------------- useful URL data --------------------------------
    /**
     * Provides a String representation of a URL to perform a search on
     * this Protein's BioSource beans (curently via AC)
     * @return String a String representation of a search URL link for BioSource
     */
    public String getBioSearchURL() {

        if(bioSearchURL == null) {
            //set it on the first call
            bioSearchURL = searchURL + getBioAc() + "&amp;searchClass=BioSource";
        }

        return bioSearchURL;
    }

    /**
     * Provides a String representation of a URL to perform a search on
     * this Protein's beans (curently via AC)
     * @return String a String representation of a search URL link for Protein
     */
    public String getProteinSearchURL() {

        if(protSearchURL == null) {
            //set it on the first call
            protSearchURL = searchURL + protein.getAc() + "&amp;searchClass=Protein";
        }
        return protSearchURL;
    }

    /**
     * Provides a String representation of a URL to access the CV related to the Xref
     * (ie the Cv beans describing the Xref's database).
     * @param xref The Xref for which the URL is required
     * @return String a String representation of a URL link for the Xref beans (CvDatabase)
     */
    public String getCvDbURL(Xref xref) {

        return (searchURL + xref.getCvDatabase().getAc() + "&amp;searchClass=CvDatabase");
    }

    /**
     * Provides a String representation of a URL to access the CV qualifier
     * info related to the Xref
     * (ie the Cv beans describing the Xref's qualifier info).
     * @param xref The Xref for which the URL is required
     * @return String a String representation of a URL link for the Xref beans (CvXrefQualifier)
     */
    public String getCvQualifierURL(Xref xref) {

        return (searchURL + xref.getCvXrefQualifier().getAc() + "&amp;searchClass=CvXrefQualifier");
    }


    /**
     * Provides a String representation of a URL to provide acces to an Xrefs'
     * database (curently via AC). The URL is at present stored via an
     * Annotation for the Xref in the Intact DB itself.
     * @param xref The Xref for which the DB URL is required
     * @return String a String representation of a DB URL link for the Xref, or a '-'
     * if there is no stored URL link for this Xref
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

    //------------------- other Protein display data ----------------------------

    /**
     * Provides a comma-separated list of gene names for this Protein.
     * @return String al list of gene names as a String.
     */
    public String getGeneNames() {

        //populate on first request
        if(geneNames == null) {
            Set nameSet = new HashSet();    //useful because sometimes they are repeated!! (eg GIOT)
            geneNames = new StringBuffer();
            //the gene names are obtained from the Aliases for the Protein
            //which are of type 'gene name'...
            Collection aliases = protein.getAliases();
            for(Iterator it = aliases.iterator(); it.hasNext();) {
                Alias alias = (Alias)it.next();

                //NB check the type String in this!!
                if(alias.getCvAliasType().getShortLabel().equals("gene-name")) {
                    //don't know how many gene names there are - also
                    //there may be more aliases than gene names, so we can't
                    //tell here if we are done or not
                    nameSet.add(alias.getName());
                }
            }
            //now create a String - if there are any names....
            if(nameSet.size() > 0) {
                for(Iterator it = nameSet.iterator(); it.hasNext();) {
                    geneNames.append(it.next());
                    if(it.hasNext()) geneNames.append(",");
                }
            }
            else geneNames.append("-");
        }

        return geneNames.toString();
    }

    /**
     * @return String The length of the Protein sequence, as a String
     */
    public String getSeqLength() {
        return Integer.toString(protein.getSequence().length());
    }

    /**
     *
     * @return String the Protein's sequence.
     */
    public String getSequence() {
        return protein.getSequence();
    }

    /**
     *
     * @return String the Protein's sequence checksum
     */
    public String getCheckSum() {
        return protein.getCrc64();

    }

    /**
     * Provides access to the Xrefs of the Protein. Note that because these are
     * complex objects containing their own display data, the calling JSP must
     * access the beans that it requires from each Xref.
     * @return The Protein's Xrefs.
     */
    public Collection getXrefs() {
        return protein.getXrefs();
    }
}
