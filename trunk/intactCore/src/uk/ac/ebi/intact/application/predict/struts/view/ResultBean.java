/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.predict.struts.view;

import uk.ac.ebi.intact.application.commons.util.XrefHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Xref;

import java.util.Iterator;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ResultBean {

    /**
     * The rank of the protein.
     */
    private int myRank;

    /**
     * The short label of the protein as a link.
     */
    private String myShortLabelLink;

    /**
     * The full name of the protein.
     */
    private String myFullName;

    public ResultBean(Protein protein, int rank) throws IntactException {
        myRank = rank;
        myFullName = protein.getFullName();

        // The xref helper to get the search link.
        XrefHelper xrefHelper = new XrefHelper();

        // The primary id link.
        String link = xrefHelper.getEmptyLink();

        // Get all the xrefs and iterate through them.
        for (Iterator iter = protein.getXrefs().iterator(); iter.hasNext();) {
            link = xrefHelper.getPrimaryIdLink((Xref) iter.next());
            if (!xrefHelper.isLinkEmpty(link)) {
                // Found a non empty link.
                break;
            }
        }
        // Add the java script stuff only if the link is not empty.
        myShortLabelLink = xrefHelper.isLinkEmpty(link) ? link :
                "<a href=\"" + "javascript:showProtein('" + link + "')\">"
                + protein.getShortLabel() + "</a>";

//        for (Iterator iter0 = protein.getXrefs().iterator(); iter0.hasNext();) {
//            Xref xref = (Xref) iter0.next();
//
//            // Null to indicate that there is no search-url for this protein.
//            String searchUrl = null;
//
//            // Loop through xref's db annotations looking for search-url.
//            Collection dbannots = xref.getCvDatabase().getAnnotations();
//            for (Iterator iter1 = dbannots.iterator(); iter1.hasNext();) {
//                Annotation annot = (Annotation) iter1.next();
//                if (annot.getCvTopic().getShortLabel().equals("search-url")) {
//                    // save searchUrl for future use
//                    searchUrl = annot.getAnnotationText();
//                    break;
//                }
//            }
//            if (searchUrl != null) {
//                Matcher matcher = ourSearchUrlPat.matcher(searchUrl);
//                // After replacing the ac with primary id.
//                searchUrl = matcher.replaceAll(xref.getPrimaryId());
//                myShortLabelLink = "<a href=\"" + "javascript:showProtein('" + searchUrl + "')\">"
//                        + protein.getShortLabel() + "</a>";
//                break;
//            }
//        }
    }

    // Get methods to access info from JSP pages.

    public int getRank() {
        return myRank;
    }

    public String getShortLabelLink() {
        return myShortLabelLink;
    }

    public String getFullName() {
        return myFullName;
    }
}
