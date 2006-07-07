/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.correctionAssigner;


import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.util.sanityChecker.MessageSender;
import uk.ac.ebi.intact.util.sanityChecker.ReportTopic;
import uk.ac.ebi.intact.util.sanityChecker.model.ExperimentBean;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.util.*;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class Assigner {

    /**
     * MessageSender, to build the message for each superCurator and administrator and send them.
     */
    MessageSender messageSender = new MessageSender();

    /**
     * Lister, holding the collection of experiment to assign, the collection of already assigned experiment.
     */
    ExperimentLister lister;

    SuperCuratorsGetter superCuratorsGetter;

    //HashMap pubmedNewlyAssigned = new HashMap();

    //Pubmed assigned to the super-curator going to correct it.
    HashMap pubmedPreviouslyAssigned = new HashMap();

    public Assigner( boolean debug ) throws Exception, IntactException {
        lister = new ExperimentLister( debug );
        superCuratorsGetter = new SuperCuratorsGetter();
    }

    /**
     * We go through the list of assignedExperiments (experiments having an annotation with cvTopic = reviewer and
     * description = nameOfTheSuperCurator) and add each experiments to the Collection of experiments to correct of each
     * corresponding SuperCurator.
     *
     * @throws Exception if the name of the reviewer contained in the annotation description does not correspond to any
     *                   superCurator contained in the correctionAssigner.properties file.
     */
    public void treatAssignedExperiments() throws Exception {
        Collection assignedExperiments = lister.getAssignedExperiments();

        for ( Iterator iterator = assignedExperiments.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = (ComparableExperimentBean) iterator.next();
            // We get the super curator having the name contained in the reviewer property.
            SuperCurator superCurator = superCuratorsGetter.getSuperCurator( exp.getReviewer().toLowerCase() );
            if ( superCurator != null ) {
                superCurator.addExperiment( exp );
            } else {
                // If we couldn't get the SuperCurator corresponding to the reviewer, there's no point in running the
                // programm, therefore, we throw an Exception, this can be due to the fact that the reviewer is not
                // defined in the correctionAssigner.properties file, or because it was a problem initializing the
                // SuperCuratorGetter object.
                throw new Exception( "The experiment[" + exp.getAc() + "," + exp.getShortlabel() + "] was assigned to " +
                                     exp.getReviewer() + "but we couldn't get the superCurator corresponding to this reviewer, we " +
                                     " he was on holyday we assign to somebody else." );

            }
            pubmedPreviouslyAssigned.put( exp.getPubmedId(), exp.getReviewer().toLowerCase() );
        }
    }

    /**
     * Given a percentage and a the total number of experiments to correct, it return the number of pubmed corresponding
     * to the given ratio. As it would make no sence that a SuperCurator would have 1.5 pubmedId to correct, we
     * approximate the result to the inferior integer and return the int (ex : 1.6 will become 1).
     *
     * @param percentage (normally, the percentage of pubmed a SuperCurator is expected to correct.
     * @param total      (the total number of pubmed to be corrected).
     *
     * @return the number of pubmedIds corresponding to the given ratio.
     */
    public int getNumberOfPubmed( int percentage, int total ) {
        int i = 0;
        float npmid = total;
        float n = ( npmid / 100 ) * percentage;
        Float f = new Float( n );
        i = f.intValue();
        return i;
    }


    /**
     * What can happen is that : on day 1  CuratorA starts entering the data corresponding to the pubmedId 1 and
     * therefore create exp1 and exp2. Therefore on day 1, SuperCuratorB get assigned this pubmedId1 to correct, so an
     * annotation reviewer(SuperCuratorB) is added to exp1 and exp2.
     * <p/>
     * on day 2, CuratorA finishes entering the data corresponding to the pubmedId 1 in creating exp3. As pubmed1 is
     * already assigned to SuperCuratorB, superCuratorB should automatically be assigned exp3 and exp3 should be
     * remooved from the list of experiments to be assigned, and exp3 should be added a reviewer annotation. As this
     * pubmedId was affected the day before it does not count in the number of pubmed the curator should be affected on
     * that day.
     * <p/>
     * This is what this method does.
     *
     * @param experiments (the filtered experiments collection).
     */
    public void filterOutAlreadyAssignedPubmed( Collection experiments ) throws Exception {
        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = (ComparableExperimentBean) iterator.next();
            //If the experiment pubmed Id is in the map of allready assigned pubmed then :
            if ( pubmedPreviouslyAssigned.containsKey( exp.getPubmedId() ) ) {
                //Get the name of the SuperCurator from the know-reviewer associated to this pubmed.
                String knownReviewer = (String) pubmedPreviouslyAssigned.get( exp.getPubmedId() );
                //Get the superCurator object corresponding.
                SuperCurator superCurator = superCuratorsGetter.getSuperCurator( knownReviewer.toLowerCase() );
                //Add the experiment to its list of experiments to correct.
                superCurator.addExperiment( exp );
                //Add a reviewer annotation to the newly assigned experiment.
                addReviewerAnnotation( exp.getAc(), superCurator.getName() );
                //Remove the experiment from the Collection of experiments to correct.
                iterator.remove();
            }
        }
    }

    /**
     * According to the percentage associated to each curator and the total number of pubmed to be assigned it assigns
     * the pubmedId to the superCurators.
     *
     * @param notAssignedPmid2creator, map associating the not assigned pubmedIds to their creator.
     * @param pubmedToExp,             map associating the pubmedId to the Collection of corresponding experiments
     *                                 existing in the database and needing to be corrected.
     * @param notAssignedExperiments   Collection of not assigned experiments.
     *
     * @throws IntactException
     */
    public void assignExperiments( HashMap notAssignedPmid2creator, HashMap pubmedToExp, Collection notAssignedExperiments ) throws Exception {
        Collection superCurators = superCuratorsGetter.getSuperCurators();

        // For each superCurator :
        for ( Iterator iterator = superCurators.iterator(); iterator.hasNext(); ) {
            SuperCurator superCurator = (SuperCurator) iterator.next();
            if ( superCurator.getPercentage() != 0 ) {

                // Get the number of pubmedIds this superCurator should be affected.
                int numberOfPubmeds = getNumberOfPubmed( superCurator.getPercentage(), lister.getNotAssignedPmid2creator().size() );

                int i = 0;
                // For each not assigned pubmedId (pmid) and while the number of affected pubmedId is smaller then the number
                // pubmedId this superCurator should be assigned :
                Iterator it = notAssignedPmid2creator.entrySet().iterator();
                while ( it.hasNext() && i < numberOfPubmeds ) {

                    Map.Entry pairs = (Map.Entry) it.next();
                    // Check that the superCurator name is not the creator of the pubmedId. As a curator can not correct
                    // its own data.
                    if ( !superCurator.getName().toLowerCase().equals( ( (String) pairs.getValue() ).toLowerCase() ) ) {
                        Collection expToAdd = (Collection) pubmedToExp.get( pairs.getKey() );
                        //For each experiment corresponding to this pubmed we just assigned.
                        for ( Iterator iterator1 = expToAdd.iterator(); iterator1.hasNext(); ) {
                            ComparableExperimentBean exp = (ComparableExperimentBean) iterator1.next();
                            //Add the experiment the Collection of experiments to correct of the superCurator.
                            superCurator.addExperiment( exp );
                            //remove the experiment from the Collection of not assigned experiments.
                            notAssignedExperiments.remove( exp );
                            //Add the annotation reviewer to the experiment.
                            addReviewerAnnotation( exp.getAc(), superCurator.getName() );
                        }
                        //remove the key/value pubmedId/creator from the map notAssignedPmid2creator.
                        it.remove();
                        //increment i to show that one more pubmedId has been assigned to this superCreator.
                        i++;
                    }

                }
            }
        }
    }

    /**
     * ex : 2 SuperCurators John and Jane. John and Jane must correct 50% of the total number of pubmed to correct. 3
     * pubmedw to correct getNumberOfPubmed(50, 3) will return 1. As it would make no sense to affect a pubmed and a
     * half. As a consequence, 1 pubmed would be not affected to anybody.
     * <p/>
     * So for each pubmedId remaining we go through the Collection of SuperCurators and if the superCurator to come is
     * not the one who created the experiment we affect it to him.
     *
     * @param notAssignedPmid2creator, map associating the not assigned pubmedIds to their creator.
     * @param pmid2ExpColl,            map associating the pubmedId to the Collection of corresponding experiments
     *                                 existing in the database and needing to be corrected.
     * @param notAssignedExperiments,  Collection of not assigned experiments.
     *
     * @throws IntactException
     */
    public void assignRemainingExperiments( HashMap notAssignedPmid2creator, HashMap pmid2ExpColl, Collection notAssignedExperiments ) throws Exception {
        Collection superCurators = superCuratorsGetter.getSuperCurators();
        //If there are still some pubmed not assigned...
        if ( notAssignedPmid2creator.size() != 0 ) {
            //then iterate on those pubmed
            Iterator iter = notAssignedPmid2creator.entrySet().iterator();
            while ( iter.hasNext() ) {
                Map.Entry pairs = (Map.Entry) iter.next();
                //Iterate on the collection of SuperCurators
                for ( Iterator iterator = superCurators.iterator(); iterator.hasNext(); ) {
                    SuperCurator superCurator = (SuperCurator) iterator.next();
                    if ( superCurator.getPercentage() != 0 ) {
                        //If the superCurator is not the curator who entered this pubmed into the database we affect it to him.
                        if ( !superCurator.getName().toLowerCase().equals( pairs.getValue() ) ) {
                            //Affect all the experiment corresponding to this pubmed Id to the superCurator.
                            Collection expToAdd = (Collection) pmid2ExpColl.get( pairs.getKey() );
                            for ( Iterator iterator1 = expToAdd.iterator(); iterator1.hasNext(); ) {
                                ComparableExperimentBean exp = (ComparableExperimentBean) iterator1.next();
                                //Add the experiment the Collection of experiments to correct of the superCurator.
                                superCurator.addExperiment( exp );
                                //remove the experiment from the Collection of not assigned experiments.
                                notAssignedExperiments.remove( exp );
                                //Add the annotation reviewer to the experiment.
                                addReviewerAnnotation( exp.getAc(), superCurator.getName() );
                            }
                            //remove the key/value pubmedId/creator from the map notAssignedPmid2creator.
                            iter.remove();
                            // As the SuperCurator was not the creator, the pubmed has been assigned so we don't need to
                            //try with an other SuperCurator. So break the iteration on the SuperCurators collection.
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     * Once an experiment has been assigned to a SuperCurator we must add a reviewer annotation (with cvTopic = reviewer
     * and description = name of the curator) so that from one day to an another we keep trace in the database of the
     * SuperCurator who has to do the correction. Like that we do not re-affect the same experiment to a different
     * personne everyday.
     *
     * @param expAc
     * @param reviewerName, value to set the annotation description value.
     *
     * @throws IntactException
     */
    public void addReviewerAnnotation( String expAc, String reviewerName ) throws Exception {
        //Get the util.model.Experiment object corresponding to this experiment ac.
        Experiment experiment = DaoFactory.getExperimentDao().getByAc(expAc);
        //Create the annotation reviewer using as description the reviewerName.
        Annotation reviewerAnnotation = createAnnotation( reviewerName );
        //It the annotation is not persistant, create it.
        DaoFactory.getAnnotationDao().update(reviewerAnnotation);

        //Add the annotation to the experiment.
        experiment.addAnnotation( reviewerAnnotation );
        // If experiment is persistent update it, if not thow an Exception.
        DaoFactory.getExperimentDao().update(experiment);
    }

    /**
     * Create an annotation object with cvTopic = reviewer and description = reviewerName.
     *
     * @param reviewerName string to set the description.
     *
     * @return
     *
     * @throws IntactException
     */
    public Annotation createAnnotation( String reviewerName ) throws IntactException {
        Institution owner = DaoFactory.getInstitutionDao().getInstitution();
        CvTopic reviewer = DaoFactory.getCvObjectDao(CvTopic.class).getByShortLabel(CvTopic.REVIEWER);
        Annotation annotation = new Annotation( owner, reviewer, reviewerName );
        return annotation;
    }

    /**
     * This method should be call after the method treatAssignedExperiments(). It take care of affecting all the not
     * assigned experiments to the superCurators.
     *
     * @throws Exception
     */
    public void treatNotAssignedExperiments() throws Exception {

        Collection notAssignedExperiments = lister.getNotAssignedExperiments();

        //remove the experiment not affected yet but corresponding to to pubmed allready assigned, see the javadoc
        // corresponding to the method for explanation.
        filterOutAlreadyAssignedPubmed( notAssignedExperiments );


        Collection superCurators = superCuratorsGetter.getSuperCurators();
        HashMap notAssignedPmid2creator = new HashMap( lister.getNotAssignedPmid2creator() );
        HashMap pmid2ExpColl = lister.getPmid2expColl();

        //Assign the experiment. See javadoc for the corresponding method.
        assignExperiments( notAssignedPmid2creator, pmid2ExpColl, notAssignedExperiments );

        //Assign remaining experiments. See javadoc for the corresponding method.
        assignRemainingExperiments( notAssignedPmid2creator, pmid2ExpColl, notAssignedExperiments );

    }

    /**
     * Go though the collection of superCurators and for each experiments to be corrected, add a message to the global
     * email which is going to be sent to the concerned super-curator.
     */
    public void addMessage() throws SQLException, IntactException {
        Collection superCurators = superCuratorsGetter.getSuperCurators();
        for ( Iterator iterator = superCurators.iterator(); iterator.hasNext(); ) {
            SuperCurator sc = (SuperCurator) iterator.next();
            Collection exps = sc.getExperiments();
            Collections.sort( (List) exps );
            // exps being the experiment to be corrected and sc.getName the name of the superCurator to whom the email
            // is going to be sent.
            messageSender.addMessage( exps, sc.getName() );
        }

        Collection experiments = lister.getOnHoldExperiments();
        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
            ExperimentBean experimentBean = (ExperimentBean) iterator.next();
            messageSender.addMessage( ReportTopic.EXPERIMENT_ON_HOLD, experimentBean );
        }

        experiments = lister.getToBeReviewedExperiments();
        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
            ExperimentBean experimentBean = (ExperimentBean) iterator.next();
            messageSender.addMessage( ReportTopic.EXPERIMENT_TO_BE_REVIEWED, experimentBean );
        }

        experiments = lister.getNotAcceptedNotToBeReviewed();
        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
            ExperimentBean experimentBean = (ExperimentBean) iterator.next();
            messageSender.addMessage( ReportTopic.EXPERIMENT_NOT_ACCEPTED_NOT_TO_BE_REVIEWED, experimentBean );
        }

    }

    /**
     * Method which assign the experiments.
     *
     * @throws Exception
     */
    public void assign() throws Exception {
        treatAssignedExperiments();
        treatNotAssignedExperiments();
    }

    public static void main( String[] args ) throws Exception {

            System.out.println( "Database: " + DaoFactory.getBaseDao().getDbName() );

            Assigner assigner = new Assigner(  true );
            assigner.assign();
            assigner.addMessage( );
            try {
                assigner.messageSender.postEmails( MessageSender.CORRECTION_ASSIGNMENT );

            } catch ( MessagingException e ) {
                // scould not send emails, then how error ...
                //e.printStackTrace();
            }
    }
}
