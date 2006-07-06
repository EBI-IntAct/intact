/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.model;

/**
 * That class reflects what is needed to create an IntAct <code>Protein</code>.
 * <p/>
 * <pre>
 *      &lt;proteinInteractor id="EBI-35455"&gt;
 *          &lt;names&gt;
 *              &lt;shortLabel&gt;P------&lt;/shortLabel&gt;
 *              &lt;fullName&gt;blababla&lt;/fullName&gt;
 *          &lt;/names&gt;
 *          &lt;xref&gt;
 *              &lt;primaryRef db="uniprot" id="P------" secondary="" version=""/&gt;
 *              &lt;secondaryRef db="sgd" id="S0004064" secondary="BUD20" version=""/&gt;
 *              &lt;secondaryRef db="go" id="GO:0005634" secondary="C:nucleus" version=""/&gt;
 *          &lt;/xref&gt;
 *          &lt;organism ncbiTaxId="4932"&gt;
 *              &lt;names&gt;
 *                  &lt;shortLabel&gt;s cerevisiae&lt;/shortLabel&gt;
 *                  &lt;fullName&gt;Saccharomyces cerevisiae&lt;/fullName&gt;
 *              &lt;/names&gt;
 *          &lt;/organism&gt;
 *          &lt;sequence&gt;MGRYSVKRYKTKRRESVQKL (...) TKPGLGQHLKGKVHK&lt;/sequence&gt;
 *      &lt;/proteinInteractor&gt;
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.Protein
 */
public final class ProteinInteractorTag {

    private final XrefTag uniprotXref;
    private final OrganismTag organism;

    /////////////////////////
    // Constructor

    public ProteinInteractorTag( final XrefTag uniprotXref,
                                 final OrganismTag organism ) {

        if( uniprotXref == null ) {
            throw new IllegalArgumentException( "You must give a non null uniprotXref for a proteinInteractor" );
        }

        if( !Constants.UNIPROT_DB_SHORTLABEL.equals( uniprotXref.getDb() ) ) {
            throw new IllegalArgumentException( "You must give a uniprot Xref, not " + uniprotXref.getDb() +
                                                " for an ProteinInteractor" );
        }

        this.organism = organism;
        this.uniprotXref = uniprotXref;
    }

    
    ////////////////////////
    // Getters

    public OrganismTag getOrganism() {
        return organism;
    }

    public XrefTag getUniprotXref() {
        return uniprotXref;
    }


    ////////////////////////
    // Equality

    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( !( o instanceof ProteinInteractorTag ) ) {
            return false;
        }

        final ProteinInteractorTag proteinInteractorTag = (ProteinInteractorTag) o;

        if( organism != null ? !organism.equals( proteinInteractorTag.organism ) : proteinInteractorTag.organism != null ) {
            return false;
        }
        if( !uniprotXref.equals( proteinInteractorTag.uniprotXref ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = uniprotXref.hashCode();
        result = 29 * result + ( organism != null ? organism.hashCode() : 0 );
        return result;
    }


    public String toString() {
        return "ProteinInteractorTag{" +
               "organism=" + organism +
               ", uniprotXref=" + uniprotXref +
               "}";
    }
}
