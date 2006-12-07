/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.dbutil.update;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;

import java.io.PrintStream;
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

    private static final Log log = LogFactory.getLog(UpdateTargetSpecies.class);

    //////////////////////////
    // Inner class

    //////////////////////////
    // Instance variable

    private CvTopic noUniprotUpdate = null;
    private CvDatabase newt = null;
    private CvXrefQualifier targetSpeciesQualifier = null;

    private Experiment currentExperiment;

    ///////////////////////////
    // Constructor

    private UpdateTargetSpecies() {
    }


    /**
     * Collect required CVs. Throws a RuntimeException if one of the object is not found.
     */
    public void init() throws IntactException {

        // loading required CVs
        noUniprotUpdate = IntactContext.getCurrentInstance().getCvContext().getByLabel(CvTopic.class, CvTopic.NON_UNIPROT);
        if ( noUniprotUpdate == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvTopic( " +
                                             CvTopic.NON_UNIPROT + " ). abort." );
        } else {
            log.warn( "CvTopic( " + CvTopic.NON_UNIPROT + " ) found." );
        }

        newt = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.NEWT_MI_REF );
        if ( newt == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvDatabase( " + CvDatabase.NEWT +
                                             " ) having an Xref( " + CvDatabase.NEWT_MI_REF + " ). abort." );
        } else {
            log.debug( "CvDatabase( " + CvDatabase.NEWT + " ) found." );
        }

        targetSpeciesQualifier = IntactContext.getCurrentInstance().getCvContext().getByLabel(CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES );
        if ( targetSpeciesQualifier == null ) {
            throw new IllegalStateException( "The IntAct database should contain a CvXrefQualifier( " +
                                             CvXrefQualifier.TARGET_SPECIES + " ). abort." );
        } else {
            log.debug( "CvXrefQualifier( " + CvXrefQualifier.TARGET_SPECIES + " ) found" );
        }
    }

    /**
     * Collects all Xref having a CvXrefQualifier( target-species ) linked to the given experiement.
     *
     * @param experiment
     *
     * @return a Collection of Xref. never null.
     */
    public Collection<ExperimentXref> getTargetSpeciesXrefs( Experiment experiment ) {

        Collection<ExperimentXref> targets = new ArrayList<ExperimentXref>();

        for ( ExperimentXref xref : experiment.getXrefs() ) {
            if ( targetSpeciesQualifier.equals( xref.getCvXrefQualifier() ) ) {
                targets.add( xref );
            }
        }
        return targets;
    }

    public static UpdateTargetSpeciesReport update(PrintStream ps, boolean dryRun)
    {
        return update(ps, dryRun, null);
    }

    public static UpdateTargetSpeciesReport update(PrintStream ps, boolean dryRun, String labelLike)
    {
        UpdateTargetSpecies updateTarget = new UpdateTargetSpecies();
        UpdateTargetSpeciesReport report = updateTarget.updateTargetSpecies(ps, dryRun, labelLike);

        return report;
    }

    public UpdateTargetSpeciesReport updateTargetSpecies(PrintStream ps, boolean dryRun)
    {
        return updateTargetSpecies(ps, dryRun, null);
    }

    public UpdateTargetSpeciesReport updateTargetSpecies(PrintStream ps, boolean dryRun, String labelLike)
    {

        UpdateTargetSpeciesReport report = new UpdateTargetSpeciesReport();

        init( );

        ExperimentDao experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();

        Iterator<Experiment> experimentIterator;

            // get all scrollableExperiments
        if (labelLike != null)
        {
            experimentIterator = experimentDao.getByShortLabelLikeIterator(labelLike, true);
        }
        else
        {
            experimentIterator = experimentDao.getAllIterator();
        }

            Map<BioSource,BioSourceStat> biosource2count = new HashMap<BioSource,BioSourceStat>( 4 );
            Set<BioSource> biosources = new HashSet<BioSource>( 4 );
            Set<String> biosourcesTaxid = new HashSet<String>( 4 );

            Map<String, BioSourceStat[]> stats = new HashMap<String,BioSourceStat[]>();

            while (experimentIterator.hasNext())
            {
                currentExperiment = experimentIterator.next();

                log.debug( "Updating " + currentExperiment.getShortLabel() + " (" + currentExperiment.getAc() + ")" );

                // clear collections
                biosources.clear();
                biosourcesTaxid.clear();
                biosource2count.clear();

                // 1. look for distinct list of Protein's biosource
                Iterator<Interaction> interactionIterator = experimentDao.getInteractionsForExperimentWithAcIterator(currentExperiment.getAc());

                while ( interactionIterator.hasNext() ) {
                    Interaction interaction = interactionIterator.next();

                    for (Component component : interaction.getComponents())
                    {
                        Interactor i = component.getInteractor();
                        if (i instanceof Protein)
                        {
                            Protein protein = (Protein) i;

                            // we only take into account UniProt Proteins
                            if (ProteinUtils.isFromUniprot(protein))
                            {
                                BioSource bioSource = protein.getBioSource();

                                biosources.add(bioSource);
                                biosourcesTaxid.add(bioSource.getTaxId());

                                // update stats for each new proteins
                                BioSourceStat count = biosource2count.get(bioSource);

                                if (count == null)
                                {
                                    count = new BioSourceStat(bioSource.getShortLabel(), bioSource.getTaxId());
                                    biosource2count.put(bioSource, count);
                                }

                                count.increment();
                            }
                        }
                    } // components
                } // interactions

                // reload the current experiment here, because to iterate through the interactions the
                // tansaction is automatically committed to avoid OutOfMemory errors.
                experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
                currentExperiment = experimentDao.getByAc(currentExperiment.getAc());

                // 2. process the list of BioSource.
                Collection<ExperimentXref> existingTargetXrefs = getTargetSpeciesXrefs( currentExperiment );
                for ( Iterator iterator1 = biosources.iterator(); iterator1.hasNext(); ) {
                    BioSource bioSource = (BioSource) iterator1.next();

                    // create the Xref
                    ExperimentXref xref = new ExperimentXref( IntactContext.getCurrentInstance().getInstitution(), newt,
                                          bioSource.getTaxId(), bioSource.getShortLabel(),
                                          null,
                                          targetSpeciesQualifier );

                    // add it only if not already there
                    if ( ! currentExperiment.getXrefs().contains( xref ) ) {
                        log.debug( "\tAdding Xref(" + xref.getPrimaryId() + ", " + xref.getSecondaryId() + ")" );

                        if (!dryRun)
                        {
                            currentExperiment.addXref( xref );
                            IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao().persist( xref );
                        }
                    } else {
                        // only keep in that collection the Xref that do not match the set of BioSource.

                        if (!dryRun)
                            existingTargetXrefs.remove( xref );
                    }

                } // biosources

                // 3. remove Xref( target-species ) that should not be there
                for (ExperimentXref xref : existingTargetXrefs)
                {
                    log.debug("\tRemove Xref(" + xref.getPrimaryId() + ", " + xref.getSecondaryId() + ")");

                    if (!dryRun)
                    {
                        currentExperiment.removeXref(xref);
                        IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao().delete(xref);
                    }
                }

                // show stats
                 List<BioSourceStat> bioStats = new ArrayList<BioSourceStat>(biosource2count.size());

                StringBuffer sb = new StringBuffer( 500 );
                sb.append( StringUtils.rightPad( currentExperiment.getShortLabel(), 25 ) );
                for ( Iterator<BioSource> iterator1 = biosource2count.keySet().iterator(); iterator1.hasNext(); ) {
                    BioSource bioSource = iterator1.next();

                    BioSourceStat stat = biosource2count.get( bioSource );
                    sb.append( stat.getName() ).append( "(" ).append( stat.getTaxid() ).append( "):" );
                    sb.append( stat.getCount() );

                    bioStats.add(stat);

                    if ( iterator1.hasNext() ) {
                        sb.append( "  " );
                    }
                }

                ps.println( sb.toString() );
 
                // fill the stats
                stats.put( currentExperiment.getAc(), bioStats.toArray(new BioSourceStat[bioStats.size()]) );

            } // scrollableExperiments

        return report;
    }


    /**
     * M A I N
     */
    public static void main( String[] args ) throws IntactException, SQLException {

            if (log.isInfoEnabled())
            {
                log.info( "Database: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbName() );
                log.info( "User: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbUserName() );
            }

            update(System.out, false).getStats();

    }
}