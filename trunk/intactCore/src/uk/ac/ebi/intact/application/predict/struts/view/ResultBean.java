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

        // The primary id link.
        String link = XrefHelper.getEmptyLink();

        // Boolean flag set to true when a proper link is found.
        boolean linkFound = false;

        // Get all the xrefs and iterate through them.
        for (Iterator iter = protein.getXrefs().iterator(); iter.hasNext();) {
            link = XrefHelper.getPrimaryIdLink((Xref) iter.next());
            if (link.startsWith("http://")) {
                // Found a non empty link.
                linkFound = true;
                break;
            }
        }
        // javascipt to display the link is only for a valid link.
        myShortLabelLink = linkFound ?
                "<a href=\"" + "javascript:showProtein('" + link + "')\">"
                + protein.getShortLabel() + "</a>" : link;
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
