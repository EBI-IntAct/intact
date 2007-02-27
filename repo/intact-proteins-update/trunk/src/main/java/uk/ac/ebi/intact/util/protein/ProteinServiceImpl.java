/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotProteinType;
import uk.ac.ebi.intact.uniprot.model.UniprotSpliceVariant;
import uk.ac.ebi.intact.uniprot.service.UniprotService;
import uk.ac.ebi.intact.util.biosource.BioSourceService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Feb-2007</pre>
 */
public class ProteinServiceImpl implements ProteinService {

    public static final Log log = LogFactory.getLog( ProteinServiceImpl.class );

    // TODO not public Constructor but a Factory that decides whether to use Remote API or Yasp

    // TODO Factory could coordinate a shared cache between multiple instances of the service (eg. multiple services running in threads)

    // TODO when running tests using this service, the implementation of UniprotBridgeAdapter could be DummyUniprotBridgeAdapter
    // TODO that creates proteins without relying on the network. Spring configuration might come in handy to configure the tests.

    /**
     * UniProt Data Source.
     */
    private UniprotService uniprotService;

    /**
     * BioSource service allowing to create new BioSource in the database.
     */
    private BioSourceService bioSourceService;

    /**
     * If set to true, all query goes to UniProt and existing IntAct proteins are updated.
     */
    private boolean forceUpdate = false;

    //////////////////////////
    // Constructor

    ProteinServiceImpl( UniprotService uniprotService ) {
        if ( uniprotService == null ) {
            throw new IllegalArgumentException( "You must give a non null implementation of a UniProt Bridge." );
        }
        this.uniprotService = uniprotService;
    }

    /////////////////////////
    // Getters and Setters

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate( boolean forceUpdate ) {
        this.forceUpdate = forceUpdate;
    }

    public BioSourceService getBioSourceService() {
        return bioSourceService;
    }

    public void setBioSourceService( BioSourceService bioSourceService ) {
        if ( bioSourceService == null ) {
            throw new NullPointerException( "bioSourceService must not be null." );
        }
        this.bioSourceService = bioSourceService;
    }

    public UniprotService getUniprotService() {
        return uniprotService;
    }

    public void setUniprotService( UniprotService uniprotService ) {
        if ( uniprotService == null ) {
            throw new NullPointerException( "uniprotService must not be null." );
        }
        this.uniprotService = uniprotService;
    }

    //////////////////////////
    // ProteinLoaderService

    public Collection<Protein> retrieve( String uniprotId ) {
        if ( uniprotId == null ) {
            throw new IllegalArgumentException();
        }

        uniprotId = uniprotId.trim();

        if ( uniprotId.length() == 0 ) {
            throw new IllegalArgumentException();
        }

        // TODO add update flag that forces retreival from UniProt and update existing proteins.

        // Check if we have it in IntAct
        Collection<Protein> intactProteins = searchIntact( uniprotId );

        if ( intactProteins.isEmpty() ) {
            // Not in IntAct, then query UniProt...
            Collection<UniprotProtein> proteins = retreiveFromUniprot( uniprotId );
            intactProteins = saveOrUpdate( proteins );
        }

        return intactProteins;
    }

    public Collection<Protein> retrieve( String uniprotId, int taxidFilter ) {
        throw new UnsupportedOperationException();
    }

    public Collection<Protein> retrieve( String uniprotId, Collection<Integer> taxidFilters ) {
        throw new UnsupportedOperationException();
    }

    public Collection<Protein> retrieve( Collection<String> uniprotIds ) {
        throw new UnsupportedOperationException();
    }

    public Collection<Protein> retrieve( Collection<String> uniprotIds, int taxidFilter ) {
        throw new UnsupportedOperationException();
    }

    public Collection<Protein> retrieve( Collection<String> uniprotIds, Collection<Integer> taxidFilters ) {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////
    // Private methods

    private Protein saveOrUpdate( UniprotProtein uniprotProtein ) throws ProteinServiceException {

        // . Open a transaction

        // . search IntAct for an instance of that protein
        //   search is made by primary AC. then secondary (do it anyway to check if any exist)
        //   then filter on taxid
        //   then filter on no-uniprot-update annotation
        //   if any left, then found = true.

        Collection<Protein> proteins = null;

        proteins = searchIntactByPrimaryAc( uniprotProtein );
        if ( proteins.isEmpty() ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Could not find any protein by primary ID, trying by Secondary AC..." );
            }
            proteins = searchIntactBySecondaryAc( uniprotProtein );
        }

        String taxid = String.valueOf( uniprotProtein.getOrganism().getTaxid() );
        proteins = filterByTaxid( proteins, taxid );

        boolean forceUpdate = false;

        if ( proteins.isEmpty() ) {
            // IDEA - if collection is empty, create a minimalistic IntAct protein and send it for update.
            //        minimalistic = ID, AC, shortlabel and Uniprot Xref should be enough ...
            proteins.add( createMinimalisticProtein( uniprotProtein ) );
            assert ( !proteins.isEmpty() );
            forceUpdate = true;
        }

        // reason for having more than one proteins here: the uniprot protein was created in inteact before it was demerged 

        // . protein were found, then update them
        for ( Protein protein : proteins ) {

            if ( log.isDebugEnabled() ) {
                log.debug( "Processing: " + protein.getShortLabel() + " (" + protein.getAc() + ")" );
            }

            // processing protein
            if ( hasNoUniprotUpdateAnnotation( protein ) ) {
                // should not be updated, but returned to the user.
                if ( log.isDebugEnabled() ) {
                    log.debug( "The protein is not from UniProt, skip update." );
                }

            } else {
                updateProtein( protein, uniprotProtein );
            }
        }

        // . Close transaction

        throw new UnsupportedOperationException( "Implementation not finished yet." );
    }

    /**
     * Update an existing intact protein's annotations.
     * <p/>
     * That includes, all Xrefs, Aliases, splice variants.
     *
     * @param protein        the intact protein to update.
     * @param uniprotProtein the uniprot protein used for data input.
     */
    private void updateProtein( Protein protein, UniprotProtein uniprotProtein ) {
       throw new UnsupportedOperationException( );
    }

    /**
     * Update an existing splice variant.
     *
     * @param spliceVariant
     * @param uniprotSpliceVariant
     */
    private void updateSpliceVariant( Protein spliceVariant, UniprotSpliceVariant uniprotSpliceVariant ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create a simple protein in view of updating it.
     * <p/>
     * It should contain the following elements: Shorltabel, Biosource and UniProt Xrefs.
     *
     * @param uniprotSpliceVariant the Uniprot splice variant we are going to build the intact on from.
     *
     * @return a non null, persisted intact protein.
     */
    private Protein createMinimalisticSpliceVariant( UniprotSpliceVariant uniprotSpliceVariant ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create a simple protein in view of updating it.
     * <p/>
     * It should contain the following elements: Shorltabel, Biosource and UniProt Xrefs.
     *
     * @param uniprotProtein the Uniprot protein we are going to build the intact on from.
     *
     * @return a non null, persisted intact protein.
     */
    private Protein createMinimalisticProtein( UniprotProtein uniprotProtein ) {
        throw new UnsupportedOperationException();
    }

    private Collection<Protein> saveOrUpdate( Collection<UniprotProtein> uniprotProtein ) {
        Collection<Protein> proteins = new ArrayList<Protein>( uniprotProtein.size() );

        for ( UniprotProtein protein : uniprotProtein ) {
            proteins.add( saveOrUpdate( protein ) );
        }

        return proteins;
    }

    private Collection<Protein> searchIntact( String uniprotAc ) {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        ProteinDao pdao = daoFactory.getProteinDao();
        List<ProteinImpl> proteins = pdao.getByUniprotId( uniprotAc );

        Collection<Protein> p = new ArrayList<Protein>( proteins.size() );
        for ( ProteinImpl protein : proteins ) {
            p.add( protein );
        }

        return p;
    }

    private Collection<Protein> searchIntactByPrimaryAc( UniprotProtein uniprotProtein ) {
        Collection<Protein> proteins = searchIntact( uniprotProtein.getPrimaryAc() );

        if ( log.isDebugEnabled() ) {
            log.debug( "Searching by Primary Ac yielded " + proteins.size() + " proteins." );
        }

        return proteins;
    }

    private Collection<Protein> searchIntactBySecondaryAc( UniprotProtein uniprotProtein ) {
        Collection<Protein> proteins = new ArrayList<Protein>( 2 );

        for ( String ac : uniprotProtein.getSecondaryAcs() ) {

            Collection<Protein> ps = searchIntact( ac );
            if ( log.isDebugEnabled() ) {
                log.debug( "Searching by secondary AC[ " + ac + " ] yielded " + ps.size() + " proteins." );
            }
            proteins.addAll( ps );
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Search by secondary AC yielded overall " + proteins.size() + " proteins." );
        }

        return proteins;
    }

    private Collection<UniprotProtein> retreiveFromUniprot( String uniprotId ) {
        throw new UnsupportedOperationException();
    }

    private Map<String, Collection<UniprotProtein>> retreiveFromUniprot( Collection<String> uniprotIds ) {
        throw new UnsupportedOperationException();
    }

    private boolean hasNoUniprotUpdateAnnotation( Protein protein ) {
        return ProteinUtils.isFromUniprot( protein );
    }

    private Collection<Protein> filterNonUniprotProteins( Collection<Protein> proteins ) {
        Collection<Protein> filtered = new ArrayList<Protein>( proteins.size() );

        for ( Protein protein : proteins ) {
            if ( !hasNoUniprotUpdateAnnotation( protein ) ) {
                filtered.add( protein );
            }
        }

        return filtered;
    }

    private boolean updateXrefs( Protein intactProtein, UniprotProtein uniprotProtein ) {
        throw new UnsupportedOperationException();
    }

    private boolean updateAliases( Protein intactProtein, UniprotProtein uniprotProtein ) {
        throw new UnsupportedOperationException();
    }

    private String generateProteinShortlabel( UniprotProtein uniprotProtein ) {

        String name = null;

        // if this is a TrEMBL protein, we need to add _SPECIES to it !!
        if ( uniprotProtein.getSource().equals( UniprotProteinType.TREMBL ) ) {
            name = uniprotProtein.getId() + "_" + uniprotProtein.getOrganism().getName().toUpperCase();
        } else {
            name = uniprotProtein.getId();
        }

        return name;
    }

    private boolean isSpliceVariant( Protein protein ) {
        if ( protein == null ) {
            throw new IllegalArgumentException( "You must give a non null protein." );
        }

        Collection<InteractorXref> xrefs = protein.getXrefs();
        if ( xrefs == null ) {
            return false;
        }

        CvXrefQualifier isoformParent = CvHelper.getQualifierByMi( CvXrefQualifier.ISOFORM_PARENT_MI_REF );

        for ( InteractorXref xref : xrefs ) {
            CvXrefQualifier qualifier = xref.getCvXrefQualifier();
            if ( qualifier != null && qualifier.equals( isoformParent ) ) {
                return true;
            }
        }

        return false;
    }

    private Collection<Protein> getIntactProteinsByPrimaryAC( UniprotProtein uniprotProtein ) {
        Collection<Protein> proteins = searchIntact( uniprotProtein.getPrimaryAc() );
        if ( log.isDebugEnabled() ) {
            log.debug( "Retreived " + proteins.size() + " protein(s) by primary AC[ " + uniprotProtein.getPrimaryAc() + "]" );
        }
        return searchIntact( uniprotProtein.getPrimaryAc() );
    }

    private Collection<Protein> getIntactProteinsBySecondaryAC( UniprotProtein uniprotProtein ) {
        Collection<Protein> proteins = new ArrayList<Protein>( uniprotProtein.getSecondaryAcs().size() );

        for ( String ac : uniprotProtein.getSecondaryAcs() ) {
            Collection<Protein> p = searchIntact( uniprotProtein.getPrimaryAc() );
            proteins.addAll( p );
        }

        if ( log.isDebugEnabled() ) {
            StringBuffer sb = new StringBuffer( 7 * uniprotProtein.getSecondaryAcs().size() ); // 7 because id + 1 space
            for ( String ac : uniprotProtein.getSecondaryAcs() ) {
                sb.append( ac ).append( ' ' );
            }
            log.debug( "Retreived " + proteins.size() + " protein(s) by secondary AC[ " + sb.toString() + "]" );
            sb = null;
        }

        return proteins;
    }

    private Collection<Protein> filterByTaxid( Collection<Protein> proteins, String taxid ) {

        if ( log.isDebugEnabled() ) {
            log.debug( "Filtering protein collection (" + proteins.size() + ") by taxid: " + taxid );
        }
        Collection<Protein> filtered = new ArrayList<Protein>( proteins.size() );

        for ( Protein protein : proteins ) {
            if ( protein.getBioSource().getTaxId().equals( taxid ) ) {
                filtered.add( protein );
            }
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "After filtering, " + filtered.size() + " protein(s) remain." );
        }

        return filtered;
    }

    /**
     * Get existing splice variant from the master protein given. <br>
     *
     * @param master The master protein of the splice variant
     *
     * @return the created splice variants
     */
    private Collection<ProteinImpl> getSpliceVariants( Protein master ) {

        if ( master == null ) {
            throw new IllegalArgumentException( "You must give a non null protein." );
        }

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        return daoFactory.getProteinDao().getSpliceVariants( master );
    }

    /**
     * Check if the given protein has been demerged. <br>
     * <p/>
     * Algorithm sketch:
     * <pre>
     * (1) gets its uniprot id as Xref( uniprot, identity )
     * (2) query the Uniprot Service and retreive a set of UniProt proteins
     * (3) count the occurence of that ID being a secondary AC
     *     if( > 1 ) : return true
     *          else : return false.
     * </pre>
     *
     * @param protein the protein for which we want to know if it has been demerged
     *
     * @return true if the protein has been demerged, otherwise false.
     *
     * @throws ProteinServiceException If the protein given doesn't have exactly one UniProt identity.
     */
    private boolean isDemerged( Protein protein ) throws ProteinServiceException {

        // get the Xref( uniprot, identity ) of that protein
        CvDatabase uniprot = CvHelper.getDatabaseByMi( CvDatabase.UNIPROT_MI_REF );
        CvXrefQualifier identity = CvHelper.getQualifierByMi( CvXrefQualifier.IDENTITY_MI_REF );

        Collection<Xref> xrefs = AnnotatedObjectUtils.searchXrefs( protein, uniprot, identity );

        String primaryAC = null;
        if ( xrefs.size() == 1 ) {
            // in most case, we have 1, we may also have 2 in case of chimeric proteins, or 0 in case of say, GI proteins.
            primaryAC = xrefs.iterator().next().getPrimaryId();
            log.debug( "Found UniProt identity: " + primaryAC );
        }

        if ( null == primaryAC ) {
            // no Xref( uniprot, identity ) found
            throw new ProteinServiceException( "No Xref( uniprot, identity ) found for the protein( " + protein.getAc() + " )" );

        }


        String sourceUrl = null;

        // count of reference to that ac in the secondary ACs of the retreived Entry from SRS.
        int countSecondaryAC = 0;

        if ( log.isDebugEnabled() ) {
            log.debug( "Retrieving protein by AC: " + primaryAC );
        }
        Collection<UniprotProtein> proteins = uniprotService.retreive( primaryAC );

        for ( UniprotProtein p : proteins ) {
            if ( p.getPrimaryAc().equals( primaryAC ) ) {

            } else {
                if ( p.getSecondaryAcs().contains( primaryAC ) ) {
                    // found one instance
                    countSecondaryAC++;
                }
            }
        }

        boolean isDemerged = false;

        log.debug( primaryAC + " was found " + countSecondaryAC + " time(s) as secondary AC in " + sourceUrl );

        if ( countSecondaryAC > 1 ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Protein " + protein.getAc() + " is eligible for demerge." );
            }
            isDemerged = true;
        }

        return isDemerged;
    }
}