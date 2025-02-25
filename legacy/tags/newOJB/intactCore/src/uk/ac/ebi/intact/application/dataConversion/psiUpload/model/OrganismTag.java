/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.model;

/**
 * That class reflects what is needed to create an IntAct <code>BioSource</code>.
 * <p/>
 * <pre>
 *      &lt;organism ncbiTaxId="4932"&gt;
 *          &lt;names&gt;
 *              &lt;shortLabel&gt;s cerevisiae&lt;/shortLabel&gt;
 *              &lt;fullName&gt;Saccharomyces cerevisiae&lt;/fullName&gt;
 *          &lt;/names&gt;
 *          &lt;tissue&gt;
 *              &lt;xref&gt;
 *                  &lt;primaryRef db="psi-mi" id="MI:xxx" secondary="" version=""/&gt;
 *              &lt;/xref&gt;
 *          &lt;/tissue&gt;
 *          &lt;cellType&gt;
 *              &lt;xref&gt;
 *                  &lt;primaryRef db="psi-mi" id="MI:xxx" secondary="" version=""/&gt;
 *              &lt;/xref&gt;
 *          &lt;/cellType&gt;
 *      &lt;/hostOrganism&gt;
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.BioSource
 */
public final class OrganismTag {

    private final String taxId;
    private CellTypeTag cellType;
    private TissueTag tissue;


    ///////////////////////////
    // Constructors

    public OrganismTag( final String taxId ) {
        if( taxId == null || taxId.trim().equals( "" ) ) {
            throw new IllegalArgumentException( "You must give a non null/empty taxId for a hostOrganism" );
        }

        try {
            Integer.parseInt( taxId );
        } catch ( NumberFormatException e ) {
            throw new IllegalArgumentException( "You must give a hostOrganism's taxId as an integer value." );
        }

        this.taxId = taxId;
    }

    public OrganismTag( final String taxId, final CellTypeTag cellType, final TissueTag tissue ) {

        this( taxId );
        this.cellType = cellType;
        this.tissue = tissue;
    }


    ////////////////////////
    // Getters

    public String getTaxId() {
        return taxId;
    }

    public CellTypeTag getCellType() {
        return cellType;
    }

    public TissueTag getTissue() {
        return tissue;
    }


    ////////////////////////
    // Equality

    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( !( o instanceof OrganismTag ) ) {
            return false;
        }

        final OrganismTag organismTag = (OrganismTag) o;

        if( cellType != null ? !cellType.equals( organismTag.cellType ) : organismTag.cellType != null ) {
            return false;
        }
        if( !taxId.equals( organismTag.taxId ) ) {
            return false;
        }
        if( tissue != null ? !tissue.equals( organismTag.tissue ) : organismTag.tissue != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = taxId.hashCode();
        result = 29 * result + ( cellType != null ? cellType.hashCode() : 0 );
        result = 29 * result + ( tissue != null ? tissue.hashCode() : 0 );
        return result;
    }


    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append( "HostOrganismTag" );
        buf.append( "{taxId=" ).append( taxId );
        buf.append( ", tissue=" );
        if( null == tissue ) {
            buf.append( '-' );
        } else {
            buf.append( tissue.getPsiDefinition().getId() );
        }
        buf.append( ", cellType=" );
        if( null == cellType ) {
            buf.append( '-' );
        } else {
            buf.append( cellType.getPsiDefinition().getId() );
        }
        buf.append( '}' );
        return buf.toString();
    }
}
