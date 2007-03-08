/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotSpliceVariant;
import uk.ac.ebi.intact.uniprot.model.UniprotXref;
import uk.ac.ebi.intact.util.protein.CvHelper;

import java.util.*;

/**
 * Utilities for updating Xrefs.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.1.2
 */
public class XrefUpdaterUtils {

    private XrefUpdaterUtils() {
    }

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( XrefUpdaterUtils.class );

    public static Xref convert( UniprotXref uniprotXref, CvDatabase db ) {
        Institution owner = IntactContext.getCurrentInstance().getConfig().getInstitution();
        return new InteractorXref( owner, db, uniprotXref.getAccession(), null );
    }

    public static Collection<Xref> convert( Collection<UniprotXref> uniprotXrefs, CvDatabase db ) {
        Collection<Xref> xrefs = new ArrayList<Xref>( uniprotXrefs.size() );
        for ( UniprotXref uniprotXref : uniprotXrefs ) {
            xrefs.add( convert( uniprotXref, db ) );
        }
        return xrefs;
    }

    public static Map<String, Collection<UniprotXref>> clusterByDatabaseName( Collection<UniprotXref> xrefs ) {

        Map<String, Collection<UniprotXref>> xrefCluster = new HashMap<String, Collection<UniprotXref>>( xrefs.size() );

        for ( UniprotXref cr : xrefs ) {
            Collection<UniprotXref> c = xrefCluster.get( cr.getDatabase() );
            if ( c == null ) {
                // not found, add an entry
                c = new ArrayList<UniprotXref>();
                xrefCluster.put( cr.getDatabase(), c );
            }
            c.add( cr );
        }

        return xrefCluster;
    }

    public static void updateAllXrefs( Protein protein,
                                       UniprotProtein uniprotProtein,
                                       Map<String, String> databaseName2mi
    ) {

        Map<String, Collection<UniprotXref>> xrefCluster = XrefUpdaterUtils.clusterByDatabaseName( uniprotProtein.getCrossReferences() );

        for ( Map.Entry<String, Collection<UniprotXref>> entry : xrefCluster.entrySet() ) {

            String db = entry.getKey();
            Collection<UniprotXref> uniprotXrefs = entry.getValue();

            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            CvObjectDao<CvDatabase> dbDao = daoFactory.getCvObjectDao( CvDatabase.class );

            // search by shortlabel is dodgy ! Try mapping to MI:xxxx first.
            String mi = databaseName2mi.get( db );
            CvDatabase cvDatabase = null;
            if ( mi != null ) {
                cvDatabase = dbDao.getByPsiMiRef( mi );

                if( cvDatabase == null ) {
                    log.error( "Could not find CvDatabase by label: " + db );
                }

            } else {
                if ( log.isDebugEnabled() ) {
                    log.debug( "No mapping found for CvDatabase("+db+"), searching by database name instead of MI ref..." );
                }
                cvDatabase = dbDao.getByShortLabel( db, true );

                if( cvDatabase == null ) {
                    log.error( "Could not find CvDatabase by label: " + db );
                }
            }

            // Convert collection into Xref
            Collection<Xref> xrefs = XrefUpdaterUtils.convert( uniprotXrefs, cvDatabase );
            XrefUpdaterUtils.updateXrefCollection( protein, cvDatabase, xrefs );
        }
    }

    /**
     * Update of the Xref of a protein.
     * <p/>
     * <pre>
     * Algo sketch:
     * 1) select a subset of the xref of the given protein based on the given CvDatabase
     * 2) select the outdated Xref
     * 3) reused them to create new Xref and delete the remaining one. By doing so we don't waste ACs
     * </pre>
     *
     * @param protein  the protein what we want to update the Xrefs
     * @param database the target database
     * @param newXrefs the new set of xrefs
     *
     * @return true if the protein has been updated, otherwise false
     */
    public static boolean updateXrefCollection( Protein protein, CvDatabase database, Collection<Xref> newXrefs ) {

        if ( protein == null ) {
            throw new IllegalArgumentException( "You must give a non null protein." );
        }

        if ( database == null ) {
            throw new IllegalArgumentException( "You must give a non null database." );
        }

        if ( newXrefs == null ) {
            throw new IllegalArgumentException( "You must give a non null collection of xref." );
        }

        boolean updated = false;
        Collection<Xref> currentXrefs = null;

        // select only the xref of the given database
        for ( Xref xref : protein.getXrefs() ) {
            if ( database.equals( xref.getCvDatabase() ) ) {
                if ( currentXrefs == null ) {
                    currentXrefs = new ArrayList<Xref>();
                }
                currentXrefs.add( xref );
            }
        }

        if ( currentXrefs == null ) {
            currentXrefs = Collections.EMPTY_LIST;
        }

        Collection<Xref> toDelete = CollectionUtils.subtract( currentXrefs, newXrefs ); // current minus new
        Collection<Xref> toCreate = CollectionUtils.subtract( newXrefs, currentXrefs );

        Iterator toDeleteIterator = toDelete.iterator();
        for ( Xref xref : toCreate ) {
            if ( toDeleteIterator.hasNext() ) {
                // in order to avoid wasting ACs, we overwrite attributes of an outdated xref.
                Xref recycledXref = ( Xref ) toDeleteIterator.next();

                // note: parent_ac was already set before as the object was persistent
                recycledXref.setPrimaryId( xref.getPrimaryId() );
                recycledXref.setSecondaryId( xref.getSecondaryId() );
                recycledXref.setCvDatabase( xref.getCvDatabase() );
                recycledXref.setCvXrefQualifier( xref.getCvXrefQualifier() );
                recycledXref.setDbRelease( xref.getDbRelease() );

                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao().update( recycledXref );
                updated = true;

            } else {

                updated = updated | addNewXref( protein, xref );
            }
        }

        for ( ; toDeleteIterator.hasNext(); ) {
            // delete remaining outdated/unrecycled xrefs
            Xref xref = ( Xref ) toDeleteIterator.next();
            IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao().delete( xref );

            updated = true;
        }

        return updated;
    }

    public static boolean addNewXref( AnnotatedObject current, final Xref xref ) {
        // Make sure the xref does not yet exist in the object
        if ( current.getXrefs().contains( xref ) ) {
            log.debug( "SKIPPED: [" + xref + "] already exists" );
            return false; // quit
        }

        // add the xref to the AnnotatedObject
        current.addXref( xref );

        // That test is done to avoid to record in the database an Xref
        // which is already linked to that AnnotatedObject.
        if ( xref.getParentAc() == current.getAc() ) {
            try {
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao().persist( xref );
                log.debug( "CREATED: [" + xref + "]" );
            } catch ( Exception e_xref ) {
                log.error( "Error while creating an Xref for protein " + current, e_xref );
                return false;
            }
        }

        return true;
    }

    public static void updateUniprotXrefs( Protein protein, UniprotProtein uniprotProtein ) {

        CvDatabase uniprot = CvHelper.getDatabaseByMi( CvDatabase.UNIPROT_MI_REF );
        CvXrefQualifier identity = CvHelper.getQualifierByMi( CvXrefQualifier.IDENTITY_MI_REF );
        CvXrefQualifier secondaryAc = CvHelper.getQualifierByMi( CvXrefQualifier.SECONDARY_AC_MI_REF );
        Institution owner = CvHelper.getInstitution();

        String dbRelease = uniprotProtein.getReleaseVersion();

        if ( log.isDebugEnabled() ) {
            log.debug( "Building UniProt Xref collection prior to update of " + protein.getShortLabel() );
        }
        Collection<Xref> ux = new ArrayList<Xref>( uniprotProtein.getSecondaryAcs().size() + 1 );
        ux.add( new InteractorXref( owner, uniprot, uniprotProtein.getPrimaryAc(), null, dbRelease, identity ) );

        log.debug( "Found " + uniprotProtein.getSecondaryAcs().size() + " secondary ACs" );
        for ( String ac : uniprotProtein.getSecondaryAcs() ) {
            ux.add( new InteractorXref( owner, uniprot, ac, null, dbRelease, secondaryAc ) );
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Built " + ux.size() + " UniProt Xref(s)." );
        }

        XrefUpdaterUtils.updateXrefCollection( protein, uniprot, ux );
    }

    public static void updateSpliceVariantUniprotXrefs( Protein intactSpliceVariant,
                                                        UniprotSpliceVariant uniprotSpliceVariant,
                                                        UniprotProtein uniprotProtein ) {

        CvDatabase uniprot = CvHelper.getDatabaseByMi( CvDatabase.UNIPROT_MI_REF );
        CvXrefQualifier identity = CvHelper.getQualifierByMi( CvXrefQualifier.IDENTITY_MI_REF );
        CvXrefQualifier secondaryAc = CvHelper.getQualifierByMi( CvXrefQualifier.SECONDARY_AC_MI_REF );
        Institution owner = CvHelper.getInstitution();

        String dbRelease = uniprotProtein.getReleaseVersion();

        if ( log.isDebugEnabled() ) {
            log.debug( "Building UniProt Xref collection prior to update of " + intactSpliceVariant.getShortLabel() );
        }
        Collection<Xref> ux = new ArrayList<Xref>( uniprotSpliceVariant.getSecondaryAcs().size() + 1 );
        ux.add( new InteractorXref( owner, uniprot, uniprotSpliceVariant.getPrimaryAc(), null, dbRelease, identity ) );

        for ( String ac : uniprotSpliceVariant.getSecondaryAcs() ) {
            ux.add( new InteractorXref( owner, uniprot, ac, null, dbRelease, secondaryAc ) );
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "Built " + ux.size() + " Xref(s)." );
        }

        XrefUpdaterUtils.updateXrefCollection( intactSpliceVariant, uniprot, ux );
    }
}