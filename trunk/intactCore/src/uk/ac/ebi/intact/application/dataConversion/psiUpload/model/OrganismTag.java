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
 *      &lt;/hostOrganism&gt;
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.BioSource
 */
public final class OrganismTag {

    private final String taxId;

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

    public String getTaxId() {
        return taxId;
    }


    ////////////////////////
    // Equality
    
    public boolean equals( final Object o ) {
        if( this == o ) {
            return true;
        }
        if( !( o instanceof OrganismTag ) ) {
            return false;
        }

        final OrganismTag hostOrganismTag = (OrganismTag) o;

        if( taxId != null ? !taxId.equals( hostOrganismTag.taxId ) : hostOrganismTag.taxId != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return ( taxId != null ? taxId.hashCode() : 0 );
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append( "OrganismTag" );
        buf.append( "{taxId=" ).append( taxId );
        buf.append( '}' );
        return buf.toString();
    }
}
