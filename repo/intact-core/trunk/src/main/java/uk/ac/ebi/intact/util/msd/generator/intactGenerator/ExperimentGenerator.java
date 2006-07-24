package uk.ac.ebi.intact.util.msd.generator.intactGenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.util.cdb.ExperimentAutoFill;
import uk.ac.ebi.intact.util.cdb.PublicationNotFoundException;
import uk.ac.ebi.intact.util.cdb.UnexpectedException;
import uk.ac.ebi.intact.util.msd.generator.msdGenerator.MsdExperiment;
import uk.ac.ebi.intact.util.msd.generator.msdGenerator.MsdInteraction;

import java.util.Collection;

/**
 * The aim of this class is to create an Experiment from an MsdExperiment object.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentGenerator {

    private static final Log log = LogFactory.getLog(ExperimentGenerator.class);

    /**
     * Given an msdExperiment it creates the corresponding IntAct Experiment with it's associated Interactions.
     * Pre-requisite :
     *      The experiment bioSource will always be set to in vitro biosource (taxid = -1)
     *      The participant detection method to predetermined ( MI:0396 )
     *      The interaction detection method will be function of the msdExperiment cvIntDetect.
     *      The xref to pubmed will be equal to the msdExperiment pmid and allow us to automatically get from cytexplore
     *      webservice : the author list for the author annotation
     *                   the contact email for the contact email annotation
     *                   the journal for the journal annotation
     *                   the year for the publication year annotation
     *                   the shortlabel for the experiment shortlabel
     *                   the fullname for the experiment fullname
     * Once this is done, we iterate on the msdInteractions collection of the MsdExperiment object and each of the
     * msdInteraction is given to the method createInteraction of the MsdInteraction class. The created interactions are
     * then added to the experiment.
     *
     * @param msdExperiment
     * @return an Experiment if the experiment was successfully created, null otherwise.
     * @throws IntactException
     */
    public Experiment createExperiment(MsdExperiment msdExperiment) throws IntactException {
        Experiment experiment;
        ExperimentAutoFill expAutoFiller;
        try {
            String pubmedId =  msdExperiment.getPmid();
            if(pubmedId == null){
                //TODO log if no pubmed
                return null;
            }
            expAutoFiller = new ExperimentAutoFill(pubmedId);

            // INSTANTIATE THE EXPERIMENT (WITH IN VITRO BIOSOURCE)
            experiment = new Experiment(GeneratorHelper.getOwner(), expAutoFiller.getShortlabel(), GeneratorHelper.getInVitro());
            // SET FULLNAME
            experiment.setFullName(expAutoFiller.getFullname());
            // ADD THE DIFFERENT ANNOTATIONS
            experiment = addAuthorListAnnotation(expAutoFiller, experiment);
            experiment = addContactEmailAnnotation(expAutoFiller, experiment);
            experiment = addJournalAnnotation(expAutoFiller, experiment);
            experiment = addYearAnnotation(expAutoFiller, experiment);
            //
            //SET THE PARTICIPANT DETECTION TO PREDETERMINE
            experiment.setCvIdentification(GeneratorHelper.getPredetermined());
            //SET THE CVINTERACTION METHOD
            experiment.setCvInteraction((CvInteraction) msdExperiment.getCvIntDetect());
            //ADD XREF TO PUBMED
            experiment = addXrefToPubmed(pubmedId, experiment);

            Collection<MsdInteraction> msdInteractions = msdExperiment.getMsdInteractions();




        } catch (PublicationNotFoundException e) {
           return null;
        } catch (UnexpectedException e) {
           return null;
        }
        return experiment;
    }


    /**
     * Create and add the author-list annotation to the experiment.
     * @param expAutoFiller ExperimentAutoFill object using the cytexplore webservice to auto-complete part of the
     * experiment information.
     * @param experiment The experiment we want to add the annotaiton on.
     * @return the modified experiment if the annotation creation was successfull the unmodified experiment otherwise.
     */
    private Experiment addAuthorListAnnotation(ExperimentAutoFill expAutoFiller, Experiment experiment){
        Annotation annotation;
        try {
            annotation = new Annotation(GeneratorHelper.getOwner(),GeneratorHelper.getAuthorList(),expAutoFiller.getAuthorList());
        } catch (IntactException e) {
            //TODO add in log "could not add author list."
            return experiment;
        }
        experiment.addAnnotation(annotation);
        return experiment;
    }

    /**
     * Create and add the contact-email annotation to the experiment.
     * @param expAutoFiller ExperimentAutoFill object using the cytexplore webservice to auto-complete part of the
     * experiment information.
     * @param experiment The experiment we want to add the annotaiton on.
     * @return the modified experiment if the annotation creation was successfull the unmodified experiment otherwise.
     */
    private Experiment addContactEmailAnnotation(ExperimentAutoFill expAutoFiller, Experiment experiment){
        Annotation annotation;
        try {
            annotation = new Annotation(GeneratorHelper.getOwner(),GeneratorHelper.getContactEmail(),expAutoFiller.getAuthorEmail());
        } catch (IntactException e) {
            //TODO add in log "could not add"
            return experiment;
        }
        experiment.addAnnotation(annotation);
        return experiment;
    }

    /**
     * Create and add the journal annotation to the experiment.
     * @param expAutoFiller ExperimentAutoFill object using the cytexplore webservice to auto-complete part of the
     * experiment information.
     * @param experiment The experiment we want to add the annotaiton on.
     * @return the modified experiment if the annotation creation was successfull the unmodified experiment otherwise.
     */
    private Experiment addJournalAnnotation(ExperimentAutoFill expAutoFiller, Experiment experiment){
        Annotation annotation;
        try {
            annotation = new Annotation(GeneratorHelper.getOwner(), GeneratorHelper.getJournal(), expAutoFiller.getJournal());
        } catch (IntactException e) {
            //TODO add in log "could not add"
            return experiment;
        }
        experiment.addAnnotation(annotation);
        return experiment;
    }

    /**
     * Create and add the publication-year annotation to the experiment.
     * @param expAutoFiller ExperimentAutoFill object using the cytexplore webservice to auto-complete part of the
     * experiment information.
     * @param experiment The experiment we want to add the annotaiton on.
     * @return the modified experiment if the annotation creation was successfull the unmodified experiment otherwise.
     */
    private Experiment addYearAnnotation(ExperimentAutoFill expAutoFiller, Experiment experiment){
        Annotation annotation;
        try{
           annotation = new Annotation(GeneratorHelper.getOwner(), GeneratorHelper.getPublicationYear(),Integer.toString(expAutoFiller.getYear()));
        }catch (IntactException e) {
            //TODO add in log "could not add"
            return experiment;
        }
        experiment.addAnnotation(annotation);
        return experiment;
    }

    /**
     * Create and add the xref (pubmed, primary-ref) to the experiment.
     * @param pubmedId String containing the pubmed id corresponding to this experiment.
     * @param experiment The experiment we want to add the xref to.
     * @return the modified experiment if the xref was succesfully created, null otherwise (we don't want to create an
     * experiment without a xref to pubmed.
     */
    private Experiment addXrefToPubmed(String pubmedId, Experiment experiment){
        try {
            ExperimentXref xref = new ExperimentXref(GeneratorHelper.getOwner(),GeneratorHelper.getPubmed(),pubmedId,null,null,GeneratorHelper.getPrimaryRef());
            experiment.addXref(xref);
            return experiment;
        } catch (IntactException e) {
            //TODO log if no pubmed
            return null;
        }
    }




}
