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

    private static class AmbiguousBioSourceException extends Exception {

        public AmbiguousBioSourceException( String message ) {
            super( message );
        }
    }

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

    /**
     * Build an identifier for the cache
     *
     * @param id uniprot id
     * @param taxid taxid of the biosource (can be null)
     * @return a unique identifier for the given protein and taxid.
     */
    private static String buildID( final String id,
                                   final String taxid ) {
        String cacheId = null;

        if(null == taxid){
            cacheId = id;
        } else {
            cacheId = id + '#' + taxid;
        }

        return cacheId;
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


    /**
     * Remove from a collection of Protein all those that are not related to the given taxid.
     *
     * @param proteins the collection of protein to filter out.
     * @param taxid the taxid that the returned protein must have (can be null - in which case there is no filtering).
     * @return a new collection of proteins.
     */
    private static Collection filterByTaxid( final Collection proteins,
                                             final String taxid ) {

        if( taxid == null ) {
            if(DEBUG) {
                System.out.println( "No taxid specified, returns identical collection" );
            }

            return proteins;
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
     * @param taxid  the taxid filter (can be null)
     * @param helper the access to the Intact database.
     *
     * @return the objects that holds either [protein, -] or [protein, spliceVariant] or null if not found.
     */
    private static ProteinHolder getIntactProtein( final String id,
                                                   final String taxid,
                                                   final IntactHelper helper )
            throws AmbiguousBioSourceException {

        if( id == null ) {
            throw new IllegalArgumentException( "the protein ID must not be null" );
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

                // search all protein having the uniprot Xref for that ID (it doesn't retreive the splice variant).
                Collection proteins = helper.getObjectsByXref( Protein.class, proteinId );
                if( proteins != null ) {

                    if( null == taxid ) {
                        // no filtering will be possible, so we have to check if there is ambiguity on
                        // which protien to pick up.
                        if( hasMultipleBioSource( proteins ) ) {
                            StringBuffer sb = new StringBuffer( 64 );
                            sb.append( "The uniprot id: " ).append( id );
                            sb.append( " describes proteins related to multiple Biosources: " );
                            for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
                                Protein protein1 = (Protein) iterator.next();
                                BioSource biosource = protein1.getBioSource();
                                sb.append( biosource.getShortLabel() ).append( '(' );
                                sb.append( biosource.getTaxId() ).append( ')' ).append( ' ' );
                            }
                            sb.append( '.' ).append( "You need to specify a specific taxid." );
                            throw new AmbiguousBioSourceException( sb.toString() );
                        }
                    }

                    Collection filteredProteins = filterByTaxid( proteins, taxid );

                    if( filteredProteins.size() == 1 ) {
                        protein = (Protein) filteredProteins.iterator().next();
                    } else {
                        StringBuffer sb = new StringBuffer( 64 );
                        sb.append( "Search By Xref(" + proteinId + ") returned " + proteins.size() + " elements" );
                        sb.append( "After filtering on taxid(" + taxid + "): " + filteredProteins.size() +
                                   " proteins remaining" );
                        throw new AmbiguousBioSourceException( sb.toString() );
                    }
                }
            } catch ( IntactException e ) {
                e.printStackTrace();
                return null;
            }

            if( protein == null ) {
                if( DEBUG ) {
                    System.out.println( "Could not found the master protein ("+ proteinId +")" );
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

                Collection filtered = filterByTaxid( spliceVariants, taxid );
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

            // This is not a splice variant but a protein ID.

            if( DEBUG ) {
                System.out.println( "This is a Protein ID" );
            }

            Protein protein = null;
            try {
                Collection proteins = helper.getObjectsByXref( Protein.class, id );

                if( null == taxid ) {

                    // no filtering will be possible, so we have to check if there is ambiguity on
                    // which protien to pick up.
                    if( hasMultipleBioSource( proteins ) ) {
                        StringBuffer sb = new StringBuffer( 64 );
                        sb.append( "The uniprot id: " ).append( id );
                        sb.append( " describes proteins related to multiple Biosources: " );
                        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
                            Protein protein1 = (Protein) iterator.next();
                            BioSource biosource = protein1.getBioSource();
                            sb.append( biosource.getShortLabel() ).append( '(' );
                            sb.append( biosource.getTaxId() ).append( ')' ).append( ' ' );
                        }
                        sb.append( '.' ).append( "You need to specify a specific taxid." );
                        throw new AmbiguousBioSourceException( sb.toString() );
                    }
                }

                Collection filteredProteins = filterByTaxid( proteins, taxid );

                if( filteredProteins.size() == 1 ) {
                    protein = (Protein) filteredProteins.iterator().next();
                    if( DEBUG ) {
                        System.out.println( "Found: " + protein );
                    }
                } else {
                    StringBuffer sb = new StringBuffer( 64 );
                    sb.append( "Search By Xref(" + id + ") returned " + proteins.size() + " elements" );
                    sb.append( "After filtering on taxid(" + taxid + "): " + filteredProteins.size() +
                               " proteins remaining" );
                    throw new AmbiguousBioSourceException( sb.toString() );
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
     * Check if the set of proteins is related to more than one biosource.
     *
     * @param proteins
     * @return true if more than one distinct biosource found, else false.
     */
    private static boolean hasMultipleBioSource( final Collection proteins ) {
         Set biosources = new HashSet();

        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();
            BioSource bioSource = protein.getBioSource();
            if( null != bioSource ){
                biosources.add( bioSource );
            }
        }

        boolean answer;

        if( biosources.size() > 1 ) {
            answer = true;
        } else {
            answer = false;
        }

        return answer;
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
        String taxId = null;
        if( organism != null ) {
            taxId = organism.getTaxId();
            OrganismChecker.check( organism, helper, bioSourceFactory );
        }

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
         * 1. retreive either protein or splice variant from IntAct
         *    getIntactObject( Object[2] prot_and_sv,  )
         *    1a. if something as been found, cache it and finish
         *    1b. if not, use UpdateProteins to get the data and search again.
         *
         * 2. do as in 1. 
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

            // cache:  [uniprotID, taxid] -> [protein, spliceVariant] or [protein, null]

            String source = null;
            ProteinHolder result = null;

            // WARNING:
            // if we ask for reuse of protein, and let's say a Protein A is already in IntAct but the entry
            // in SRS has now 2 proteins (A and B). If no taxid is specified and reuseProtein requested, we might
            // happily take A instead of throwing an error because of the existence of B that make the case ambiguous.
            // Hence, reuse protein must be used only if taxid is not null !
            if( CommandLineOptions.getInstance().reuseProtein() && taxId != null ) {
                // check if the proteins are in IntAct
                source = "IntAct";

                if( DEBUG ) {
                    System.out.println( "Searching in Intact..." );
                }

                // taxid is not null here so no exception can be thrown.
                try {
                    result = getIntactProtein( uniprotId, taxId, helper );
                } catch ( AmbiguousBioSourceException e ) {
                    // we should never get here ! but just in case ...
                    MessageHolder.getInstance().addCheckerMessage( new Message( e.getMessage() ) );
                    System.out.println( e.getMessage() );
                }
            }

            // retreived by ID having different taxid

            if( result == null ) { // always null if taxId == null or no reuseProtein requested.
                // Update database
                if( DEBUG ) {
                    System.out.println( "Protein not found in intact, updating..." );
                }
                source = "UpdateProteins";
                Collection tmp = proteinFactory.insertSPTrProteins( uniprotId, taxId, true ); // taxId can be null !

                if( DEBUG ) {
                    System.out.println( tmp.size() + " Protein created/updated." );
                }

                // search against updated database
                try {
                    result = getIntactProtein( uniprotId, taxId, helper );
                } catch ( AmbiguousBioSourceException e ) {
                    MessageHolder.getInstance().addCheckerMessage( new Message( e.getMessage() ) );
                    System.out.println( e.getMessage() );
                }
            }


            if( result == null ) {
                // error
                final String msg = "Could not find Protein for uniprot ID: " + uniprotId +
                                   " and BioSource " + taxId;
                MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
                System.out.println( msg );

            } else {

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