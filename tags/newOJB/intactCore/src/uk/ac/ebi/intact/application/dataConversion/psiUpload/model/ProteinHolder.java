/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.model;

import uk.ac.ebi.intact.model.Protein;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class ProteinHolder {

    private final Protein protein;
    private Protein spliceVariant;


    ////////////////////////
    // Constructors

    public ProteinHolder( Protein protein ) {

        if( protein == null ) {
            throw new IllegalArgumentException( "You must give a non null protein" );
        }

        this.protein = protein;
    }

    public ProteinHolder( Protein protein, Protein spliceVariant ) {
        this( protein );
        this.spliceVariant = spliceVariant;
    }


    /////////////////////
    // Getters

    public Protein getProtein() {
        return protein;
    }

    public boolean isSpliceVariantExisting() {
        return spliceVariant != null;
    }

    public Protein getSpliceVariant() {
        return spliceVariant;
    }


    //////////////////////////
    // Equality

    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( !( o instanceof ProteinHolder ) ) {
            return false;
        }

        final ProteinHolder proteinHolder = (ProteinHolder) o;

        if( !protein.equals( proteinHolder.protein ) ) {
            return false;
        }
        if( spliceVariant != null ? !spliceVariant.equals( proteinHolder.spliceVariant ) : proteinHolder.spliceVariant != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = protein.hashCode();
        result = 29 * result + ( spliceVariant != null ? spliceVariant.hashCode() : 0 );
        return result;
    }


    /////////////////////
    // toString

    public String toString() {
        return "ProteinHolder{" +
               "protein=" + protein +
               ", spliceVariant=" + spliceVariant +
               "}";
    }

}
