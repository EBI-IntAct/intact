/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.OrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinHolder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinInteractorTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.CommandLineOptions;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.util.BioSourceFactory;
import uk.ac.ebi.intact.util.UpdateProteinsI;

import java.util.*;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class ProteinInteractorChecker {

    private final static boolean DEBUG = CommandLineOptions.getInstance().isDebugEnabled();


    /**
     * will avoid to have to search again later !
     * <p/>
     * uniprotId#taxid --> Collection(Protein)
     */
    private static final Map cache = new HashMap();

    /**
     * Search a protein in the cache
     *
     * @param id
     * @param bioSource
     * @return
     */
    public static ProteinHolder getProtein( String id, BioSource bioSource ) {

        return (ProteinHolder) cache.get( buildID( id, bioSource.getTaxId() ) );
    }

    private static String buildID( final String id,
                                   final String taxid ) {
        return id + '#' + taxid;
    }

    /**
     * Answer the question: is that protein a Splice Variant ?
     * One (quick) way is to check if the shortlabel match the pattern: XXXXXX-#
     *
     * @param label the protein label to check
     * @return true is this is a splice variant, otherwise false.
     */
    private static boolean isSpliceVariant( final String label ) {
        return ( label.indexOf( '-' ) != -1 );
    }

    private static Collection filterByTaxid( final Collection proteins,
                                             final String taxid ) {

        if( proteins == null ) {
            throw new IllegalArgumentException( "The Collection to filter has to be not null." );
        }

        Collection filteredProteins = new ArrayList( proteins.size() );

        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            final Protein protein = (Protein) iterator.next();
            if( taxid.equals( protein.getBioSource().getTaxId() ) ) {
                filteredProteins.add( protein );
            }
        }

        return filteredProteins;
    }


    /**
     * Get Protein from IntAct from its ID.
     * If this is ID refers to
     * (1) a protein, we send back a Protein only
     * (2) a splice varaint, we send back the splice variant and its master protein.
     *
     * @param id     the id of the object we are looking for (must not be null)
     * @param taxid  the taxid filter (must not be null)
     * @param helper the access to the Intact database.
     * @return the objects that holds either [protein, -] or [protein, spliceVariant] or null if not found.
     */
    private static ProteinHolder getIntactProtein( final String id,
                                                   final String taxid,
                                                   final IntactHelper helper ) {

        if( id == null ) {
            throw new IllegalArgumentException( "the protein ID must not be null" );
        }

        if( taxid == null ) {
            throw new IllegalArgumentException( "the taxid of the protein must not be null" );
        }

        ProteinHolder result = null;

        if( DEBUG ) {
            System.out.println( "\ngetIntactObject(" + id + ", " + taxid + ")" );
        }

        if( isSpliceVariant( id ) ) {
            if( DEBUG ) {
                System.out.println( "is splice variant ID" );
            }
            // the ID is a splice variant's one.
            String proteinId = id.substring( 0, id.indexOf( '-' ) );

            if( DEBUG ) {
                System.out.println( "Protein ID: " + proteinId );
            }

            Protein protein = null;
            try {
                // TODO find why this could returns several proteins
                Collection proteins = helper.getObjectsByXref( Protein.class, proteinId );
                if( proteins != null ) {
                    Collection filteredProteins = filterByTaxid( proteins, taxid );
                    if( filteredProteins.size() == 1 ) {
                        protein = (Protein) filteredProteins.iterator().next();
                    } else {
                        if( DEBUG ) {
                            System.out.println( "Search By Xref(" + proteinId + ") returned " + proteins.size() + " elements" );
                            System.out.println( "After filtering on taxid(" + taxid + "): " + filteredProteins.size() +
                                                " proteins remaining" );
                        }
                    }
                }
            } catch ( IntactException e ) {
                e.printStackTrace();
                return null;
            }

            if( protein == null ) {
                if( DEBUG ) {
                    System.out.println( "Could not found the master protein" );
                }
                return null;
            }

            if( DEBUG ) {
                System.out.println( "found master protein: " + protein );
            }

            // search for splice variant
            Protein spliceVariant = null;
            try {
                if( DEBUG ) {
                    System.out.println( "search splice variant of master AC: " + protein.getAc() );
                }

                Collection spliceVariants = helper.getObjectsByXref( Protein.class, protein.getAc() );
                if( spliceVariants == null || spliceVariants.isEmpty() ) {
                    if( DEBUG ) {
                        System.out.println( "No splice variant found, abort" );
                    }
                    return null;
                }

                if( DEBUG ) {
                    System.out.println( spliceVariants.size() + " splice variants found" );
                }

                if( DEBUG ) {
                    System.out.println( "Before filering:" );
                    for ( Iterator iterator = spliceVariants.iterator(); iterator.hasNext(); ) {
                        Protein p = (Protein) iterator.next();
                        System.out.println( p.getShortLabel() + "  -->  " + p.getBioSource().getTaxId() );
                    }
                }

                Collection filtered = filterByTaxid( spliceVariants, taxid );

                if( DEBUG ) {
                    System.out.println( "after filering, before shortlabel selection (" + id.toLowerCase() + "):" );
                    for ( Iterator iterator = spliceVariants.iterator(); iterator.hasNext(); ) {
                        Protein p = (Protein) iterator.next();
                        System.out.println( p.getShortLabel() + "  -->  " + p.getBioSource().getTaxId() );
                    }
                }

                for ( Iterator iterator = filtered.iterator(); iterator.hasNext(); ) {
                    final Protein sv = (Protein) iterator.next();

                    // The splice variant shortlabel can be either the id (lowercase) of the id to which we
                    // have concatenated the biosource shortlabel (occurs when we have multiple species for 
                    // a splice variant).
                    if( sv.getShortLabel().startsWith( id.toLowerCase() ) ) {
                        spliceVariant = sv;
                        break; // exit the loop.
                    }
                }

                if( spliceVariant == null ) {
                    if( DEBUG ) {
                        System.out.println( "Didn't find it !" );
                    }
                    return null;
                }

                if( DEBUG ) {
                    System.out.println( "Selected: " + spliceVariant );
                }

            } catch ( IntactException e ) {
                e.printStackTrace();
                return null;
            }

            if( DEBUG ) {
                System.out.println( "Create protein Holder with protein and splice variant" );
            }
            result = new ProteinHolder( protein, spliceVariant );

        } else {

            if( DEBUG ) {
                System.out.println( "This is a Protein ID" );
            }

            // the ID is a protein's one.
            Protein protein = null;
            try {
                Collection proteins = helper.getObjectsByXref( Protein.class, id );

                if( DEBUG ) {
                    System.out.println( "Before filering:" );
                    for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
                        Protein p = (Protein) iterator.next();
                        System.out.println( p.getShortLabel() + "  -->  " + p.getBioSource().getTaxId() );
                    }
                }

                Collection tmp = filterByTaxid( proteins, taxid );

                if( DEBUG ) {
                    System.out.println( "after filering" );
                    for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
                        Protein p = (Protein) iterator.next();
                        if( DEBUG ) {
                            System.out.println( p.getShortLabel() + "  -->  " + p.getBioSource().getTaxId() );
                        }
                    }
                }

                if( tmp != null && !tmp.isEmpty() ) {
                    protein = (Protein) tmp.iterator().next();
                    if( DEBUG ) {
                        System.out.println( "Found: " + protein );
                    }
                } else {
                    if( DEBUG ) {
                        System.out.println( "Cound not find protein for id: " + id + " taxid:" + taxid );
                    }
                }

            } catch ( IntactException e ) {
                e.printStackTrace();
                return null;
            }

            if( protein == null ) {
                if( DEBUG ) {
                    System.out.println( "Could not find it in intact" );
                }
                return null;
            }

            if( DEBUG ) {
                System.out.println( "Create protein Holder with only a protein" );
            }
            result = new ProteinHolder( protein );
        }

        return result;
    }


    /**
     * @param proteinInteractor
     * @param helper
     * @param proteinFactory
     * @param bioSourceFactory
     */
    public static void check( final ProteinInteractorTag proteinInteractor,
                              final IntactHelper helper,
                              final UpdateProteinsI proteinFactory,
                              final BioSourceFactory bioSourceFactory ) {

        final OrganismTag organism = proteinInteractor.getOrganism();
        final String taxId = organism.getTaxId();
        OrganismChecker.check( organism, bioSourceFactory );

        final XrefTag uniprotDef = proteinInteractor.getUniprotXref();
        XrefChecker.check( uniprotDef, helper );

        final String uniprotId = uniprotDef.getId();
        final String cacheId = buildID( uniprotId, taxId );

        /**
         * -STATEGY-
         *
         * 2 cases: the user can have requested
         *              (1) to reuse existing protein in which case the UpdateProtein
         *                  is only called if the protein is not found in the IntAct node.
         *              (2) to force update, in whilch case the UpdateProteins is called
         *                  for every single ID in order to have up-to-date data.
         *
         * BEWARE: the uniprot ID given in the XML file can refer to either a protein or a splice variant
         *         and we link that ID to the relevant objects.
         */

        /**
         * 1. retreive either protein or splice variant from IntAct            (COULD BE A FUNCTION)
         *    getIntactObject( Object[2] prot_and_sv,  )
         *    1a. if something as been found, cache it and finish
         *    1b. if not, use UpdateProteins to get the data and search again.
         *
         * 2. do as in 1.          (CALL THE FUNCTIOn)
         *    2a. if found ok
         *    2b. if not, error.
         *
         *
         *
         * 1. details
         * ----------
         *
         * ID could be P12345 (Protein) or Q87264-2 (Splice variant)
         *
         * if (ID is splice variant) {
         *     search by shortlabel (lowercase(ID))
         *     get also the master protein using the xref to its AC.
         * } else {
         *    search by xref
         * }
         *
         */


        if( DEBUG ) {
            System.out.println( "\nChecking on " + cacheId );
        }

        if( !cache.keySet().contains( cacheId ) ) {

            // TODO cache:  key -> [protein, spliceVariant] or [protein, null]

            String source = null;
            ProteinHolder result = null;


            if( CommandLineOptions.getInstance().reuseProtein() ) {
                // check if the proteins are in IntAct
                source = "IntAct";

                if( DEBUG ) {
                    System.out.println( "Searching in Intact..." );
                }
                result = getIntactProtein( uniprotId, taxId, helper );
            }


            if( result == null ) { // always null if user requested force update.
                // Update database
                if( DEBUG ) {
                    System.out.println( "Protein not found in intact, updating..." );
                }
                source = "UpdateProteins";
                Collection tmp = null;
                tmp = proteinFactory.insertSPTrProteins( uniprotId, organism.getTaxId(), true );

                if( DEBUG ) {
                    System.out.println( tmp.size() + " Protein created/updated." );
                }

                // search against updated database
                result = getIntactProtein( uniprotId, taxId, helper );
            }


            if( result == null ) {
                // error
                final String msg = "Could not find Protein for uniprot ID: " + uniprotId +
                                   " and BioSource " + taxId;
                MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );

            } else {

                // display what has been found
                if( result == null ) {
                    throw new IllegalArgumentException( "Result should not be null here !!!!" );
                }

                if( result.isSpliceVariantExisting() ) {

                    String svLabel = result.getSpliceVariant().getShortLabel();
                    String pLabel = result.getProtein().getShortLabel();
                    System.out.println( "Found 1 splice variant (" + svLabel + ") and its master protein (" + pLabel +
                                        ") for uniprot ID: " + uniprotId + " (" + source + ")" );
                } else {

                    String pLabel = result.getProtein().getShortLabel();
                    System.out.println( "Found 1 protein (" + pLabel + ") for uniprot ID: " +
                                        uniprotId + " (" + source + ")" );
                }
            }

            cache.put( cacheId, result );
        } else {
            if( DEBUG ) {
                System.out.println( "Found from cache ... " );
            }
        }
    } // check
}