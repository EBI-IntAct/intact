/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;


/**
 * Controlled vocabulary for description topics.
 *
 * @author hhe
 * @version $Id$
 */
public class CvTopic extends CvObject implements Editable {

    //////////////////////////
    // Constants

    public static final String XREF_VALIDATION_REGEXP = "id-validation-regexp";
    public static final String INTERNAL_REMARK = "remark-internal";
    public static final String UNIPROT_DR_EXPORT = "uniprot-dr-export";
    public static final String UNIPROT_CC_EXPORT = "uniprot-cc-note";
    public static final String NEGATIVE = "negative";
    public static final String COPYRIGHT = "copyright";
    public static final String AUTHOR_CONFIDENCE = "author-confidence";
    public static final String CONFIDENCE_MAPPING = "confidence-mapping";
    public static final String SEARCH_URL_ASCII = "search-url-ascii";
    public static final String ISOFORM_COMMENT = "isoform-comment";
    public static final String NON_UNIPROT = "no-uniprot-update";
    public static final String CC_NOTE = "uniprot-cc-note";
    public static final String ON_HOLD = "on-hold";
    public static final String ACCEPTED = "accepted";
    public static final String REJECTED = "rejected";

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     * This should not be here as it has no model functionality but is
     * related to eg user interfaces.
     */
//    protected static Vector menuList = null;

    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    private CvTopic() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvTopic instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvTopic
     * @param owner      The Institution which owns this CvTopic
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvTopic( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvTopic




