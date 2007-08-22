/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins.targetspecies;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
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

    private static final Log log = LogFactory.getLog( UpdateTargetSpecies.class );

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

    public UpdateTargetSpecies() {
        if (getDataContext().isTransactionActive()) {
            throw new IntactException("Transaction must NOT be active when instantiating UpdateTargetSpecies");
        }

        beginTransaction();
        CvObjectDao cvObjectDao = getDataContext().getDaoFactory().getCvObjectDao();

        noUniprotUpdate = (CvTopic) cvObjectDao.getByShortLabel(CvTopic.class, CvTopic.NON_UNIPROT);
        newt = (CvDatabase) cvObjectDao.getByPsiMiRef( CvDatabase.NEWT_MI_REF );
        targetSpeciesQualifier = (CvXrefQualifier) cvObjectDao.getByShortLabel( CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES );

        commitTransaction();
    }

    public void updateExperiment(Experiment experiment) {
        if (getDataContext().isTransactionActive()) {
            throw new IntactException("Transaction must NOT be active");
        }

        Set<SimpleBioSource> existingBioSources = getExistingBioSources(experiment);
        Set<SimpleBioSource> allBioSources = getBioSourcesForExperimentInteractions(experiment);

        Collection<SimpleBioSource> bioSourcesToRemove = new ArrayList<SimpleBioSource>(existingBioSources);
        bioSourcesToRemove.removeAll(allBioSources);

        Collection<SimpleBioSource> bioSourcesToAdd = new ArrayList<SimpleBioSource>(allBioSources);
        bioSourcesToAdd.removeAll(existingBioSources);

        // refresh the experiment from the db
        beginTransaction();
        experiment = getDataContext().getDaoFactory().getExperimentDao().getByAc(experiment.getAc());

        // remove xrefs
        Collection<ExperimentXref> xrefs = experiment.getXrefs();
        for (Iterator<ExperimentXref> iterator = xrefs.iterator(); iterator.hasNext();) {
            ExperimentXref experimentXref = iterator.next();

            if (experimentXref.getCvXrefQualifier().equals(targetSpeciesQualifier)) {
                for (SimpleBioSource bioSourceToRemove : bioSourcesToRemove) {
                    if (experimentXref.getPrimaryId().equals(bioSourceToRemove.getTaxId())) {
                        iterator.remove();
                        getDataContext().getDaoFactory().getXrefDao(ExperimentXref.class).delete(experimentXref);
                        break;
                    }
                }
            }
        }

        // add new xrefs
        for (SimpleBioSource bioSourceToAdd : bioSourcesToAdd) {
            ExperimentXref xref = new ExperimentXref(experiment.getOwner(), newt,
                                                      bioSourceToAdd.getTaxId(), bioSourceToAdd.getLabel(), null, targetSpeciesQualifier);
            experiment.addXref(xref);
        }

        commitTransaction();

    }

    protected DataContext getDataContext() {
        return IntactContext.getCurrentInstance().getDataContext();
    }

    protected Set<SimpleBioSource> getExistingBioSources(Experiment experiment) {
        Set<SimpleBioSource> existingBioSources = new HashSet<SimpleBioSource>();

        for (ExperimentXref xref : getTargetSpeciesXrefs(experiment)) {
             existingBioSources.add(new SimpleBioSource(xref.getSecondaryId(), xref.getPrimaryId()));
        }

        return existingBioSources;
    }

    protected Set<SimpleBioSource> getBioSourcesForExperimentInteractions(Experiment experiment) {
        Set<SimpleBioSource> allBioSources = new HashSet<SimpleBioSource>();

        String experimentAc = experiment.getAc();

        int firstResult = 0;
        int maxResults = 50;
        Collection<Interaction> interactions;

        do {
            beginTransaction();
            interactions = getDataContext().getDaoFactory()
                    .getExperimentDao().getInteractionsForExperimentWithAc(experimentAc, firstResult, maxResults);

            for (Interaction interaction : interactions) {
                for (Component component : interaction.getComponents()) {
                    BioSource bioSource = component.getInteractor().getBioSource();
                    allBioSources.add(new SimpleBioSource(bioSource));
                }
            }
            commitTransaction();

            firstResult += maxResults;
        } while (!interactions.isEmpty());

        return allBioSources;
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

    protected void beginTransaction() {
        getDataContext().beginTransaction();
    }

    protected void commitTransaction() {
        try {
            getDataContext().commitTransaction();
        } catch (IntactTransactionException e) {
            throw new IntactException(e);
        }
    }

    protected class SimpleBioSource {

        private String taxId;
        private String label;

        private SimpleBioSource(String label, String taxId) {
            this.label = label;
            this.taxId = taxId;
        }

        private SimpleBioSource(BioSource bioSource) {
            this.label = bioSource.getShortLabel();
            this.taxId = bioSource.getTaxId();
        }

        public String getLabel() {
            return label;
        }

        public String getTaxId() {
            return taxId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleBioSource that = (SimpleBioSource) o;

            if (label != null ? !label.equals(that.label) : that.label != null) return false;
            if (taxId != null ? !taxId.equals(that.taxId) : that.taxId != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            result = (taxId != null ? taxId.hashCode() : 0);
            result = 31 * result + (label != null ? label.hashCode() : 0);
            return result;
        }
    }


}