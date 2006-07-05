/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanityChecker.model;

/**
 * Holds the parent of a splice variant.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2006</pre>
 */
public class SpliceVariantParentBean {
    private String ac;
    private String parentName;
    private String variantName;

    public SpliceVariantParentBean() {
    }

    public String getAc() {
        return ac;
    }

    public void setAC( String ac ) {
        this.ac = ac;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName( String parentName ) {
        this.parentName = parentName;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName( String variantName ) {
        this.variantName = variantName;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final SpliceVariantParentBean that = (SpliceVariantParentBean) o;

        if ( !ac.equals( that.ac ) ) {
            return false;
        }
        if ( !parentName.equals( that.parentName ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ac.hashCode();
        result = 29 * result + parentName.hashCode();
        return result;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append( "SpliceVariantBean" );
        sb.append( "{AC='" ).append( ac ).append( '\'' );
        sb.append( ", parentName='" ).append( parentName ).append( '\'' );
        sb.append( ", variantName='" ).append( variantName ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}