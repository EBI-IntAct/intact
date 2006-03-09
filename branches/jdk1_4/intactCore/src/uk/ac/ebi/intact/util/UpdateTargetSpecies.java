/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Update all experiment's target-species Xrefs. <br> Given the fact that an Experiment relates to a set of interaction,
 * and each interaction having a set of Protein, we build a distinct list of interacting Protein's BioSource and for
 * each BioSource, we create at the Experiment level an Xref( primaryId=biosource.taxid,
 * secondaryId=biosource.shortlabel, qualifier=target-species ). <br> That set of Xref is kept up-to-date, by adding
 * missing Xref, and removing those that should no longer exists.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Oct-2005</pre>
 */
public class UpdateTargetSpecies {

    public static CvTopic noUniprotUpdate = null;
    public static CvDatabase newt = null;
    public static CvXrefQualifier targetSpeciesQualifier = null;


    /**
     * Collect required CVs. Throws a RuntimeException if one of the object is not found.
     *
     * @param helper dataSource
     *
     * @throws IntactException
     */
    public static void init( IntactHelper helper ) throws IntactException {

        // loading required CVs
        noUniprotUpdate = (CvTopic) helper.getObjectByLabel( CvTopic.class, CvTopic.NON_UNIPROT );
        if ( noUniprotUpdate == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvTopic( " +
                                             CvTopic.NON_UNIPROT + " ). abort." );
        } else {
            System.out.println( "CvTopic( " + CvTopic.NON_UNIPROT + " )." );
        }


        newt = (CvDatabase) helper.getObjectByPrimaryId( CvDatabase.class, CvDatabase.NEWT_MI_REF );
        if ( newt == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvDatabase( " + CvDatabase.NEWT +
                                             " ) having an Xref( " + CvDatabase.NEWT_MI_REF + " ). abort." );
        } else {
            System.out.println( "CvDatabase( " + CvDatabase.NEWT + " )." );
        }

        targetSpeciesQualifier = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES );
        if ( targetSpeciesQualifier == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvXrefQualifier( " +
                                             CvXrefQualifier.TARGET_SPECIES + " ). abort." );
        } else {
            System.out.println( "CvXrefQualifier( " + CvXrefQualifier.TARGET_SPECIES + " ) found" );
        }
    }

    /**
     * Checks if the protein has been annotated with the no-uniprot-update CvTopic, if so, return false, otherwise true.
     * That flag is added to a protein when created via the editor. As some protein may have a UniProt ID as identity we
     * don't want those to be overwitten.
     *
     * @param protein the protein to check
     *
     * @return false if no Annotation having CvTopic( no-uniprot-update ), otherwise true.
     */
    public static boolean isFromUniprot( final Protein protein ) {

        // TODO Move this to the IntAct model

        boolean isFromUniprot = true;

        if ( null == noUniprotUpdate ) {
            // in case the term hasn't been created, assume there are no proteins created via editor.
            return true;
        }

        for ( Iterator iterator = protein.getAnnotations().iterator(); iterator.hasNext() && true == isFromUniprot; ) {
            Annotation annotation = (Annotation) iterator.next();

            if ( noUniprotUpdate.equals( annotation.getCvTopic() ) ) {
                isFromUniprot = false;
            }
        }

        return isFromUniprot;
    }

    /**
     * Collects all Xref having a CvXrefQualifier( target-species ) linked to the given experiement.
     *
     * @param experiment
     *
     * @return a Collection of Xref. never null.
     */
    public static Collection getTargetSpeciesXrefs( Experiment experiment ) {

        Collection targets = new ArrayList();
        for ( Iterator iterator = experiment.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();
            if ( targetSpeciesQualifier.equals( xref.getCvXrefQualifier() ) ) {
                targets.add( xref );
            }
        }
        return targets;
    }


    /**
     * D E M O
     */
    public static void main( String[] args ) throws IntactException, SQLException, LookupException {

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            System.out.println( "Database: " + helper.getDbName() );
            System.out.println( "User: " + helper.getDbUserName() );

            init( helper );

            Collection experiments = helper.search( Experiment.class, "shortlabel", "*" );
            Set biosources = new HashSet( 4 );
            Set biosourcesTaxid = new HashSet( 4 );
            for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
                Experiment experiment = (Experiment) iterator.next();

                System.out.println( "Updating " + experiment.getShortLabel() + " (" + experiment.getAc() + ")" );
                // empty collections
                biosources.clear();
                biosourcesTaxid.clear();

                // 1. look for distinct list of Protein's biosource
                for ( Iterator iterator1 = experiment.getInteractions().iterator(); iterator1.hasNext(); ) {
                    Interaction interaction = (Interaction) iterator1.next();

                    for ( Iterator iterator2 = interaction.getComponents().iterator(); iterator2.hasNext(); ) {
                        Component component = (Component) iterator2.next();

                        Interactor i = component.getInteractor();
                        if ( i instanceof Protein ) {
                            Protein protein = (Protein) i;

                            // we only take into account UniProt Proteins
                            if ( isFromUniprot( protein ) ) {
                                biosources.add( protein.getBioSource() );
                                biosourcesTaxid.add( protein.getBioSource().getTaxId() );
                            }
                        }
                    } // components
                } // interactions


                // 2. process the list of BioSource.
                Collection existingTargetXrefs = getTargetSpeciesXrefs( experiment );
                for ( Iterator iterator1 = biosources.iterator(); iterator1.hasNext(); ) {
                    BioSource bioSource = (BioSource) iterator1.next();

                    // create the Xref
                    Xref xref = new Xref( helper.getInstitution(), newt,
                                          bioSource.getTaxId(), bioSource.getShortLabel(),
                                          null,
                                          targetSpeciesQualifier );

                    // add it only if not already there
                    if ( false == experiment.getXrefs().contains( xref ) ) {
                        System.out.println( "\tAdding Xref(" + xref.getPrimaryId() + ", " + xref.getSecondaryId() + ")" );
                        experiment.addXref( xref );
                        helper.create( xref );
                    } else {
                        // only keep in that collection the Xref that do not match the set of BioSource.
                        existingTargetXrefs.remove( xref );
                    }

                } // biosources


                // 3. remove Xref( target-species ) that should not be there
                for ( Iterator iterator1 = existingTargetXrefs.iterator(); iterator1.hasNext(); ) {
                    Xref xref = (Xref) iterator1.next();

                    System.out.println( "\tRemove Xref(" + xref.getPrimaryId() + ", " + xref.getSecondaryId() + ")" );
                    experiment.removeXref( xref );
                    helper.delete( xref );
                }


                // try to free up some resource.
                iterator.remove();

            } // experiments

        } finally {
            if ( helper != null ) {
                helper.closeStore();
                System.out.println( "Database access closed." );
            }
        }
    }
}