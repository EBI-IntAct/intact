/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * The type of the alias.
 * example "common name"
 * example "misspelling"
 *
 * @author hhe
 * @version $Id$
 */
public class CvAliasType extends CvObject implements Editable {

    ////////////////////////////
    // Constants
    
    public static final String ISOFORM_SYNONYM = "isoform synonym";
    public static final String ISOFORM_SYNONYM_MI_REF = "MI:0304";

    public static final String GENE_NAME = "gene name";
    public static final String GENE_NAME_MI_REF = "MI:0301";

    public static final String GENE_NAME_SYNONYM = "gene name-synonym";
    public static final String GENE_NAME_SYNONYM_MI_REF = "MI:0302";

    public static final String ORF_NAME = "orf name";
    public static final String ORF_NAME_MI_REF = "MI:0306";

    public static final String LOCUS_NAME = "locus name";
    public static final String LOCUS_NAME_MI_REF = "MI:0305";

    public static final String GO_SYNONYM = "go synonym";
    public static final String GO_SYNONYM_MI_REF = "MI:0303";


    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private CvAliasType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvAliasType instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvAliasType
     * @param owner The Institution which owns this CvAliasType
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvAliasType(Institution owner, String shortLabel) {

        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }

} // end CvAliasType