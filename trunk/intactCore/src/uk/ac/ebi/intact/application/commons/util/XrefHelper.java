/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.commons.util;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains various useful utilities. All methods are static.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefHelper {

    // Class Data

    /**
     * Empty primary id link.
     */
    private static final String ourEmptyPidLink = "---";

    /**
     * The pattern to replace the ac.
     */
    private static Pattern ourSearchUrlPat = Pattern.compile("\\$\\{ac\\}");

    // Instance Data

    /**
     * Maps: Short label of CV database -> url.
     */
    private Map myCvDbToUrl = new HashMap();

    /**
     * Return the primary id as a link. Only used when viewing a xref.
     * @param xref Xrefenece object to access primary id and other information.
     * @return the primary id as a link only if the primary id is not null and
     * 'search-url' is found among the annotations. {@link #ourEmptyPidLink} is
     *  returned for a null primary id. The primary id is returned as the link
     * if there is 'search-url' is found among the annotations for given
     * xreference.
     */
    public String getPrimaryIdLink(Xref xref) {
        // Return the empty link if there is no primar yd.
        String pid = xref.getPrimaryId();
        if (pid == null) {
            return ourEmptyPidLink;
        }
        // The short label of the database of the xref.
        String dbname = xref.getCvDatabase().getShortLabel();

        // Null to indicate that there is no search-url in the annotations.
        String searchUrl = (String) myCvDbToUrl.get(dbname);

        // Is it in the cache?
        if (searchUrl == null) {
            // Not in the cache; create it and store in the cache.

            // Loop through annotations looking for search-url.
            Collection annots = xref.getCvDatabase().getAnnotations();
            for (Iterator iter = annots.iterator(); iter.hasNext();) {
                Annotation annot = (Annotation) iter.next();
                if (annot.getCvTopic().getShortLabel().equals("search-url")) {
                    // save searchUrl for future use
                    searchUrl = annot.getAnnotationText();
                    break;
                }
            }
            if (searchUrl == null) {
                // The db has no annotation "search-url". Don't search again in
                // the future.
                searchUrl = ourEmptyPidLink;
            }
            // Cache the url.
            myCvDbToUrl.put(dbname, searchUrl);
        }
        // return pid if there is no search url for the database.
        if (searchUrl.equals(ourEmptyPidLink)) {
            return pid;
        }
        Matcher matcher = ourSearchUrlPat.matcher(searchUrl);
        // After replacing the ac with primary id.
        return matcher.replaceAll(pid);
    }

    /**
     * True if given link is an empty link.
     * @param link the link to check for.
     * @return true if <code>link</code> equals to {@link #ourEmptyPidLink}.
     */
    public boolean isLinkEmpty(String link) {
        return link.equals(ourEmptyPidLink);
    }

    /**
     * Returns the identifier for the empty primary id link.
     * @return the identifier for the empty primary id link.
     */
    public String getEmptyLink() {
        return ourEmptyPidLink;
    }
}
