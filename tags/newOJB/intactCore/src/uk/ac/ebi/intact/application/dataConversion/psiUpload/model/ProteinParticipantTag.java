/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.model;

/**
 * That class reflects what is needed to create an IntAct <code>Component</code>.
 * <p/>
 * <pre>
 *       &lt;proteinParticipant&gt;
 *           &lt;proteinInteractorRef ref="EBI-333"/&gt;
 *           &lt;role&gt;prey&lt;/role&gt;
 *       &lt;/proteinParticipant&gt;
 * <p/>
 *          - OR -
 * <p/>
 *       &lt;proteinParticipant&gt;
 *            &lt;proteinInteractor id="EBI-333"&gt;
 *                    &lt;names&gt;
 *                        &lt;shortLabel&gt;yev6_yeast&lt;/shortLabel&gt;
 *                        &lt;fullName&gt;Hypothetical 29.7 kDa protein in RSP5-LCP5
 *                            intergenic region&lt;/fullName&gt;
 *                    &lt;/names&gt;
 *                    &lt;xref&gt;
 *                        &lt;primaryRef db="uniprot" id="P40078" secondary="yev6_yeast" version=""/&gt;
 *                        &lt;secondaryRef db="interpro" id="IPR001047" secondary="Ribosomal_S8E" version=""/&gt;
 *                    &lt;/xref&gt;
 *                    &lt;organism ncbiTaxId="4932"&gt;
 *                        &lt;names&gt;
 *                            &lt;shortLabel&gt;s cerevisiae&lt;/shortLabel&gt;
 *                            &lt;fullName&gt;Saccharomyces cerevisiae&lt;/fullName&gt;
 *                        &lt;/names&gt;
 *                    &lt;/organism&gt;
 *                    &lt;sequence&gt;MPQNDY (...) VNAVLLV&lt;/sequence&gt;
 *              &lt;/proteinInteractor&gt;
 *              &lt;role&gt;prey&lt;/role&gt;
 *      &lt;/proteinParticipant&gt;
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.Component
 */
public final class ProteinParticipantTag {

    private final ProteinInteractorTag proteinInteractor;
    private final String role;


    ///////////////////////////
    // Constructors

    public ProteinParticipantTag( final ProteinInteractorTag proteinInteractor,
                                  final String role ) {

        if( proteinInteractor == null ) {
            throw new IllegalArgumentException( "You must give a non null proteinInteractor for a proteinParticipant" );
        }

        if( role == null || "".equals( role.trim() ) ) {
            throw new IllegalArgumentException( "You must give a non null/empty role for a proteinParticipant" );
        }

        this.proteinInteractor = proteinInteractor;
        this.role = role;
    }


    //////////////////////////
    // Getters

    public ProteinInteractorTag getProteinInteractor() {
        return proteinInteractor;
    }

    public String getRole() {
        return role;
    }


    ////////////////////////
    // Equality

    public boolean equals( final Object o ) {
        if( this == o ) {
            return true;
        }
        if( !( o instanceof ProteinParticipantTag ) ) {
            return false;
        }

        final ProteinParticipantTag proteinParticipantTag = (ProteinParticipantTag) o;

        if( !proteinInteractor.equals( proteinParticipantTag.proteinInteractor ) ) {
            return false;
        }
        if( !role.equals( proteinParticipantTag.role ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = proteinInteractor.hashCode();
        result = 29 * result + role.hashCode();
        return result;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append( "ProteinParticipantTag" );
        buf.append( "{proteinInteractor=" ).append( proteinInteractor );
        buf.append( ",role=" ).append( role );
        buf.append( '}' );
        return buf.toString();
    }
}
