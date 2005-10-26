/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.model;

import uk.ac.ebi.intact.model.CvDatabase;

import java.util.Collection;
import java.util.Collections;


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

    /////////////////////////////////////////////
    // Attribute related to a UniProt Protein

    /**
     * the UniProt Xref if available. if used, we don't need primaryXref and secondaryXrefs as they will be populated
     * from UniProt
     */
    private final XrefTag uniprotXref;

    ///////////////////////////////////////////////
    // Attribute related to a non UniProt Protein

    private String shortlabel = null;
    private String fullname = null;

    /**
     * Other primary Xref than UniProt.
     */
    private XrefTag primaryXref = null;

    /**
     * Other secondaryXref than UniProt.
     */
    private Collection secondaryXrefs = Collections.EMPTY_LIST;

    private Collection aliases = Collections.EMPTY_LIST;

    private String sequence = null;

    ////////////////////////////////////
    // Attribute shared in both cases

    private final OrganismTag organism;


    /////////////////////////
    // Constructor

    public ProteinInteractorTag( final XrefTag uniprotXref,
                                 final OrganismTag organism ) {

        if ( uniprotXref == null ) {
            throw new IllegalArgumentException( "You must give a non null uniprotXref for a proteinInteractor" );
        }

        //  as of june 2005 we have proteins without UniProt Xrefs.
        if ( !Constants.UNIPROT_DB_SHORTLABEL.equals( uniprotXref.getDb() ) ) {
            throw new IllegalArgumentException( "You must give a uniprot Xref, not " + uniprotXref.getDb() +
                                                " for a ProteinInteractor" );
        }

        this.organism = organism;
        this.uniprotXref = uniprotXref;
    }

    /**
     * Constructor for non UniProt proteins, both primary and secondary Xref are specified.
     *
     * @param primaryXref    the primary Xref of the protein
     * @param secondaryXrefs a collection of secondary Xrefs
     * @param organism       the organism of the protein
     */
    public ProteinInteractorTag( final String shortlabel,
                                 final String fullname,
                                 final XrefTag primaryXref,
                                 final Collection secondaryXrefs,
                                 final Collection aliases,
                                 final OrganismTag organism,
                                 final String sequence ) {

        // as of june 2005 we have proteins without UniProt Xrefs.
        // as of 2005-10-24 uniprot became uniprotkb, so for backward compatibility, we support both the
        //                  old and new UniProt shortlabel
        if ( Constants.UNIPROT_DB_SHORTLABEL.equals( primaryXref.getDb() )
             ||
             CvDatabase.UNIPROT.equals( primaryXref.getDb() ) ) {

            this.uniprotXref = primaryXref;

        } else {

            //  as of june 2005 we have proteins without UniProt Xrefs.
            if ( shortlabel == null || "".equals( shortlabel.trim() ) ) {
                throw new IllegalArgumentException( "You must give a non null/empty shortlabel for a ProteinInteractor " );
            }

            // this is not a UniProt Protein.
            this.uniprotXref = null;

            this.shortlabel = shortlabel;
            this.fullname = fullname;

            this.primaryXref = primaryXref;
            this.secondaryXrefs = Collections.unmodifiableCollection( secondaryXrefs );

            this.aliases = Collections.unmodifiableCollection( aliases );

            this.sequence = sequence;
        }

        this.organism = organism;
    }


    ////////////////////////
    // Getters

    public OrganismTag getOrganism() {
        return organism;
    }

    public XrefTag getUniprotXref() {
        return uniprotXref;
    }

    public boolean hasUniProtXref() {
        return uniprotXref != null;
    }

    public XrefTag getPrimaryXref() {
        return primaryXref;
    }

    public Collection getSecondaryXrefs() {
        return secondaryXrefs;
    }

    public String getShortlabel() {
        return shortlabel;
    }

    public String getFullname() {
        return fullname;
    }

    public Collection getAliases() {
        return aliases;
    }

    public String getSequence() {
        return sequence;
    }

    ////////////////////////
    // Equality

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ProteinInteractorTag ) ) {
            return false;
        }

        final ProteinInteractorTag proteinInteractorTag = (ProteinInteractorTag) o;

        if ( organism != null ? !organism.equals( proteinInteractorTag.organism ) : proteinInteractorTag.organism != null ) {
            return false;
        }

        if ( uniprotXref != null ? !uniprotXref.equals( proteinInteractorTag.uniprotXref ) : proteinInteractorTag.uniprotXref != null ) {
            return false;
        }

        if ( !shortlabel.equals( proteinInteractorTag.shortlabel ) ) {
            return false;
        }

        if ( fullname != null ? !fullname.equals( proteinInteractorTag.fullname ) : proteinInteractorTag.fullname != null ) {
            return false;
        }

        if ( primaryXref != null ? !primaryXref.equals( proteinInteractorTag.primaryXref ) : proteinInteractorTag.primaryXref != null ) {
            return false;
        }

        if ( sequence != null ? !sequence.equals( proteinInteractorTag.sequence ) : proteinInteractorTag.sequence != null ) {
            return false;
        }

        if ( !secondaryXrefs.equals( proteinInteractorTag.secondaryXrefs ) ) {
            return false;
        }

        if ( !aliases.equals( proteinInteractorTag.aliases ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = secondaryXrefs.hashCode();

        result = 29 * result + ( organism != null ? organism.hashCode() : 0 );

        result = 29 * result + ( uniprotXref != null ? uniprotXref.hashCode() : 0 );

        result = 29 * result + shortlabel.hashCode();
        result = 29 * result + ( fullname != null ? fullname.hashCode() : 0 );
        result = 29 * result + ( primaryXref != null ? primaryXref.hashCode() : 0 );
        result = 29 * result + ( sequence != null ? sequence.hashCode() : 0 );
        result = 29 * result + aliases.hashCode();
        return result;
    }


    public String toString() {

        String s = null;
        if ( hasUniProtXref() ) {
            s = "ProteinInteractorTag{" +
                "organism=" + organism +
                ", uniprotXref=" + uniprotXref +
                "}";

        } else {
            
            s = "ProteinInteractorTag{" +
                "shortlabel='" + shortlabel + "'" +
                ", fullname='" + fullname + "'" +
                ", organism=" + organism +
                ", primaryXref=" + primaryXref +
                ", secondaryXrefs=" + secondaryXrefs +
                ", aliases=" + aliases +
                ", sequence='" + sequence + "'" +
                "}";
        }

        return s;
    }
}
