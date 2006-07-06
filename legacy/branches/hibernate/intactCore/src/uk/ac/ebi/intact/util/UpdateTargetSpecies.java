/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Update all experiment's target-species Xrefs and show stats.
 * <p/>
 * Given the fact that an Experiment relates to a set of interaction, and each interaction having a set of Protein, we
 * build a distinct list of interacting Protein's BioSource and for each BioSource, we create at the Experiment level an
 * Xref( primaryId=biosource.taxid, secondaryId=biosource.shortlabel, qualifier=target-species ).
 * <p/>
 * That set of Xref is kept up-to-date, by adding missing Xref, and removing those that should no longer exists.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Oct-2005</pre>
 */
public class UpdateTargetSpecies {

    //////////////////////////
    // Inner class

    /**
     * Contains stats about the count of protein's biosources per experiment.
     */
    private static class BioSourceStat {
        //////////////////////////
        // Instance variable

        private String name;
        private String taxid;
        private int count = 0;

        //////////////////////////
        // Constructor

        public BioSourceStat( String name, String taxid ) {
            this.name = name;
            this.taxid = taxid;
        }

        ////////////////////
        // Getters
        public String getName() {
            return name;
        }

        public String getTaxid() {
            return taxid;
        }

        public int getCount() {
            return count;
        }

        ///////////////////////
        // Update stats
        public void increment() {
            count++;
        }
    }

    //////////////////////////
    // Instance variable

    public static CvTopic noUniprotUpdate = null;
    public static CvDatabase newt = null;
    public static CvXrefQualifier targetSpeciesQualifier = null;
    private static final String NEW_LINE = System.getProperty( "line.separator" );

    ///////////////////////////
    // Constructor

    private UpdateTargetSpecies() {
        // no instantiable
    }


    /**
     * Collect required CVs. Throws a RuntimeException if one of the object is not found.
     *
     * @param helper dataSource
     *
     * @throws IntactException
     */
    public static void init( IntactHelper helper ) throws IntactException {

        // loading required CVs
        noUniprotUpdate = helper.getObjectByLabel( CvTopic.class, CvTopic.NON_UNIPROT );
        if ( noUniprotUpdate == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvTopic( " +
                                             CvTopic.NON_UNIPROT + " ). abort." );
        } else {
            System.out.println( "CvTopic( " + CvTopic.NON_UNIPROT + " ) found." );
        }

        newt = helper.getObjectByPrimaryId( CvDatabase.class, CvDatabase.NEWT_MI_REF );
        if ( newt == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvDatabase( " + CvDatabase.NEWT +
                                             " ) having an Xref( " + CvDatabase.NEWT_MI_REF + " ). abort." );
        } else {
            System.out.println( "CvDatabase( " + CvDatabase.NEWT + " ) found." );
        }

        targetSpeciesQualifier = helper.getObjectByLabel( CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES );
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

        Collection<Xref> targets = new ArrayList<Xref>();
        for ( Xref xref : experiment.getXrefs() ) {
            if ( targetSpeciesQualifier.equals( xref.getCvXrefQualifier() ) ) {
                targets.add( xref );
            }
        }
        return targets;
    }


    /**
     * M A I N
     */
    public static void main( String[] args ) throws IntactException, SQLException, LookupException {

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            System.out.println( "Database: " + helper.getDbName() );
            System.out.println( "User: " + helper.getDbUserName() );

            init( helper );

            // get all experiments
            Collection experiments = helper.search( Experiment.class, "shortlabel", "*" );

            Map biosource2count = new HashMap( 4 );
            Set biosources = new HashSet( 4 );
            Set biosourcesTaxid = new HashSet( 4 );

            ArrayList stats = new ArrayList( experiments.size() );

            for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
                Experiment experiment = (Experiment) iterator.next();

                System.out.println( "Updating " + experiment.getShortLabel() + " (" + experiment.getAc() + ")" );

                // empty collections
                biosources.clear();
                biosourcesTaxid.clear();
                biosource2count.clear();

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
                                BioSource bioSource = protein.getBioSource();

                                biosources.add( bioSource );
                                biosourcesTaxid.add( bioSource.getTaxId() );

                                // update stats for each new proteins
                                BioSourceStat count = (BioSourceStat) biosource2count.get( bioSource );

                                if ( count == null ) {
                                    count = new BioSourceStat( bioSource.getShortLabel(), bioSource.getTaxId() );
                                    biosource2count.put( bioSource, count );
                                }

                                count.increment();
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
                    if ( ! experiment.getXrefs().contains( xref ) ) {
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

                // show stats
                StringBuffer sb = new StringBuffer( 500 );
                sb.append( StringUtils.rightPad( experiment.getShortLabel(), 25 ) );
                for ( Iterator iterator1 = biosource2count.keySet().iterator(); iterator1.hasNext(); ) {
                    BioSource bioSource = (BioSource) iterator1.next();

                    BioSourceStat stat = (BioSourceStat) biosource2count.get( bioSource );
                    sb.append( stat.getName() ).append( "(" ).append( stat.getTaxid() ).append( "):" );
                    sb.append( stat.getCount() );

                    if ( iterator1.hasNext() ) {
                        sb.append( "  " );
                    }
                }

                stats.add( sb.toString() );
                System.out.println( sb.toString() );


            } // experiments

            // write stats in a file.
            try {
                BufferedWriter out = new BufferedWriter( new FileWriter( "target-species.stat" ) );
                for ( Iterator iterator = stats.iterator(); iterator.hasNext(); ) {
                    String line = (String) iterator.next();

                    out.write( line );
                    out.write( NEW_LINE );
                }

                out.close();
            } catch ( IOException e ) {
            }

        } finally {
            if ( helper != null ) {
                helper.closeStore();
                System.out.println( "Database access closed." );
            }
        }
    }
}