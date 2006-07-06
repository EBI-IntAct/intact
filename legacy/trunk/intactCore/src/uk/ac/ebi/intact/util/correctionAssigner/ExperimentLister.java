/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.correctionAssigner;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.sanityChecker.SanityCheckerHelper;
import uk.ac.ebi.intact.util.sanityChecker.model.AnnotationBean;
import uk.ac.ebi.intact.util.sanityChecker.model.ControlledvocabBean;
import uk.ac.ebi.intact.util.sanityChecker.model.ExperimentBean;
import uk.ac.ebi.intact.util.sanityChecker.model.Int2ExpBean;

import java.sql.SQLException;
import java.util.*;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentLister {

    CvHolder cvHolder;

    /**
     * Collection of experimentBeans : being created after september 2005 having no annotation to-be-reviewed or
     * accepted having no annotation reviewer In other words, experiment not yet corrected and not yet assigned to any
     * super-curator for correction
     */
    private Collection notAssignedExperiments = new ArrayList();


    /**
     * Collection of experimentBeans : being created after september 2005 having no annotation to-be-reviewed or
     * accepted having an annotation reviewer In other words, experiment not yet corrected but already assigned to a
     * super-curator for correction.
     */
    private Collection assignedExperiments = new ArrayList();

    private Collection onHoldExperiments = new ArrayList();

    private Collection toBeReviewedExperiments = new ArrayList();

    private Collection notAcceptedNotToBeReviewed = new ArrayList();

    /**
     * HashMap permiting to map pubmed String to the corresponding Collection of ExperimentBean
     */
    private HashMap pmid2expColl = new HashMap();

    /**
     * HashMap permiting to map pubmed id of experiments(not assigned to any reviewer) to their creator.
     */
    private HashMap notAssignedPmid2creator = new HashMap();

    /**
     * HashMap permiting to map pubmed id of experiments (assigned to a reviewer) to their creator.
     */
    private HashMap assignedPmid2creator = new HashMap();

//    public void removeElementFromAssignedPmid2creator(String pmid){
//        assignedPmid2creator.remove(pmid);
//    }
    private static boolean DEBUG = false;

    /**
     * Constructor of ExperimentLister.
     *
     * @throws SQLException
     * @throws IntactException
     */
    public ExperimentLister( IntactHelper helper, boolean debug ) throws Exception, IntactException {
        if ( helper == null ) {
            throw new IllegalArgumentException( "IntactHelper must not be null." );
        }
        this.DEBUG = debug;
        initialize( helper );
    }

    private CvHolder getCvHolder( IntactHelper helper ) throws Exception {
        if ( cvHolder == null ) {
            cvHolder = new CvHolder( helper );
        }
        return cvHolder;
    }

    private void initialize( IntactHelper helper ) throws Exception, SQLException {

        cvHolder = getCvHolder( helper );

        removeCorrectionForSuperCuratorAway( helper );

        fillNotAssignedExpCollection( helper );
        removeExpOnHoldAndWithNoInteraction( helper, notAssignedExperiments );

        fillAssignedExpCollection( helper );
        removeExpOnHoldAndWithNoInteraction( helper, assignedExperiments );

        fillPmid2CreatorMaps();
        fillPmid2expColl();
        fillNotAcceptedNotToBeReviewedExperiments( helper );
        fillOnHoldAndToBeReviewedExperiments( helper );
    }

    /**
     * Getter for the HashMap pmid2expColl.
     *
     * @return the HashMap pmid2expColl.
     */
    public HashMap getPmid2expColl() {
        return pmid2expColl;
    }

    /**
     * Getter for Collection notAssignedExperiments.
     *
     * @return the collection notAssignedExperiments.
     */
    public Collection getNotAssignedExperiments() {
        return notAssignedExperiments;
    }

    /**
     * Getter for the Collection assignedExperiments.
     *
     * @return the Collection assignedExperiments
     */
    public Collection getAssignedExperiments() {
        return assignedExperiments;
    }

    /**
     * Getter for the HashMap notAssignedPmid2creator.
     *
     * @return the HashMap notAssignedPmid2creator
     */
    public HashMap getNotAssignedPmid2creator() {
        return notAssignedPmid2creator;
    }

//    public void addElementInNotAssignedPmid2creator(String pmid, String creator){
//        notAssignedPmid2creator.put(pmid, creator.toLowerCase());
//    }

    public Collection getOnHoldExperiments() {
        return onHoldExperiments;
    }

    public Collection getToBeReviewedExperiments() {
        return toBeReviewedExperiments;
    }

    public Collection getNotAcceptedNotToBeReviewed() {
        return notAcceptedNotToBeReviewed;
    }

//    public void addExp2NotAcceptedNotToBeReviewed(ComparableExperimentBean exp){
//        notAcceptedNotToBeReviewed.add(exp);
//    }

    /**
     * Via the sanityCheckerHelper this method is using the dbUtils library to get the Collection notAssignedExperiments
     * of experimentBeans being to assigned to a reviewer for correction.
     *
     * @throws IntactException
     * @throws SQLException
     */
    private void fillNotAssignedExpCollection( IntactHelper helper ) throws Exception, SQLException {

        if(DEBUG){
            System.out.println("Searching for experiment not accepted, to-be-reviewed and not assigned : ");
        }

        CvHolder holder = getCvHolder( helper );

        SanityCheckerHelper sch = new SanityCheckerHelper();

        sch.addMapping(  ComparableExperimentBean.class, "select e.ac, e.created_user, e.created, e.shortlabel, x.primaryId as pubmedId " +
                                                                "from ia_experiment e, ia_xref x " +
                                                                "where x.parent_ac = e.ac and " +
                                                                "x.database_ac = '" + holder.pubmed.getAc() + "' and " +
                                                                "x.qualifier_ac = '" + holder.primaryRef.getAc() + "' and " +
                                                                "e.ac not in ( " +
                                                                "select e.ac " +
                                                                "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
                                                                "where e.ac=e2a.experiment_ac and " +
                                                                "e2a.annotation_ac=a.ac and " +
                                                                "a.topic_ac in  ('" + holder.accepted.getAc() + "','" + holder.toBeReviewed.getAc() + "') " +
                                                                "union " +
                                                                "select e.ac " +
                                                                "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
                                                                "where e.ac=e2a.experiment_ac and " +
                                                                "e2a.annotation_ac=a.ac and " +
                                                                "a.topic_ac in  ('" + holder.reviewer.getAc() + "') " +
                                                                ") " +
                                                                "and e.created >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS') and e.ac like ? " +
                                                                "order by created_user" );
        if (DEBUG){
            System.out.println("... Here is the request done\n\n");
            System.out.println("select e.ac, e.created_user, e.created, e.shortlabel, x.primaryId as pubmedId \n" +
                    "from ia_experiment e, ia_xref x \n" +
                    "where x.parent_ac = e.ac and \n" +
                    "x.database_ac = '" + holder.pubmed.getAc() + "' and \n" +
                    "x.qualifier_ac = '" + holder.primaryRef.getAc() + "' and \n" +
                    "e.ac not in ( \n" +
                    "select e.ac \n" +
                    "from ia_experiment e, ia_exp2annot e2a, ia_annotation a \n" +
                    "where e.ac=e2a.experiment_ac and \n" +
                    "e2a.annotation_ac=a.ac and \n" +
                    "a.topic_ac in  ('" + holder.accepted.getAc() + "','" + holder.toBeReviewed.getAc() + "') \n" +
                    "union \n" +
                    "select e.ac \n" +
                    "from ia_experiment e, ia_exp2annot e2a, ia_annotation a \n" +
                    "where e.ac=e2a.experiment_ac and \n" +
                    "e2a.annotation_ac=a.ac and \n" +
                    "a.topic_ac in  ('" + holder.reviewer.getAc() + "') \n" +
                    ") \n" +
                    "and e.created >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS') and e.ac like ? \n" +
                    "order by created_user\n\n");
    }
        notAssignedExperiments = sch.getBeans( ComparableExperimentBean.class, "%" );
        if(DEBUG){
            System.out.println("..." + notAssignedExperiments.size() + " experiments found.");
        }
    }

    /**
     * * Via the sanityCheckerHelper this method is using the dbUtils library to get the Collection assignedExperiments
     * of experimentBeans being already assigned in the past to a reviewer for correction.
     *
     * @throws IntactException
     * @throws SQLException
     */

    private void fillAssignedExpCollection( IntactHelper helper ) throws Exception, SQLException {
        if(DEBUG){
            System.out.println("Searching for experiments assigned and not accepted or to-be-reviewed : ");
        }
        CvHolder holder = getCvHolder( helper );

        SanityCheckerHelper sch = new SanityCheckerHelper();
        sch.addMapping( ComparableExperimentBean.class, "select e.ac, e.created_user, e.created, e.shortlabel, x.primaryId as pubmedId, a.description as reviewer " +
                                                                "from ia_experiment e, ia_exp2annot e2a, ia_annotation a, ia_xref x " +
                                                                "where e.ac=e2a.experiment_ac and " +
                                                                "x.parent_ac = e.ac and " +
                                                                "x.parent_ac = e.ac and " +
                                                                "x.database_ac = '" + holder.pubmed.getAc() + "' and " +
                                                                "x.qualifier_ac = '" + holder.primaryRef.getAc() + "' and " +
                                                                "e2a.annotation_ac=a.ac and " +
                                                                "e.ac not in ("+
                                                                    "select e.ac " +
                                                                    "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
                                                                    "where e.ac=e2a.experiment_ac and " +
                                                                    "e2a.annotation_ac=a.ac and " +
                                                                    "a.topic_ac in  ('" + holder.accepted.getAc() + "','" + holder.toBeReviewed.getAc() + "') " +
                                                                    ") and " +
                                                                "a.topic_ac in  ('" + holder.reviewer.getAc() + "') " +
                                                                "and e.created >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS') and e.ac like ? " +
                                                                "order by created_user" );

        assignedExperiments = sch.getBeans( ComparableExperimentBean.class, "%" );
        if(DEBUG){
            System.out.println("..." + assignedExperiments.size() + " experiments found.");
            System.out.println("... Here was the request done : \n\n");
            System.out.println("select e.ac, e.created_user, e.created, e.shortlabel, x.primaryId as pubmedId, a.description as reviewer \n" +
                    "from ia_experiment e, ia_exp2annot e2a, ia_annotation a, ia_xref x \n" +
                    "where e.ac=e2a.experiment_ac and \n" +
                    "x.parent_ac = e.ac and \n" +
                    "x.parent_ac = e.ac and \n" +
                    "x.database_ac = '" + holder.pubmed.getAc() + "' and \n" +
                    "x.qualifier_ac = '" + holder.primaryRef.getAc() + "' and \n" +
                    "e2a.annotation_ac=a.ac and \n" +
                    "e.ac not in (\n"+
                    "select e.ac \n" +
                    "from ia_experiment e, ia_exp2annot e2a, ia_annotation a \n" +
                    "where e.ac=e2a.experiment_ac and \n" +
                    "e2a.annotation_ac=a.ac and \n" +
                    "a.topic_ac in  ('" + holder.accepted.getAc() + "','" + holder.toBeReviewed.getAc() + "')\n " +
                    ") and " +
                    "a.topic_ac in  ('" + holder.reviewer.getAc() + "') \n" +
                    "and e.created >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS') and e.ac like ? \n" +
                    "order by created_user\n\n");
    }
    }


    /**
     * If a superCurator is away we should re-assigne its corrections to somebody else and stoppe assigning him new
     * corrections. The fact that a superCurator is away can be seen in the fact that it's method getPercentage return 0
     * . To re-assign it's correction to somebody else we just remove all the annotation on experiment having as
     * topic_ac the ac of the reviewer controlled vocabulary and as description the name of the superCurator being away.
     * Then the assigner will automatically detect its former-assigned experiments as not assigned experiments and will
     * automatically re-assign them.
     *
     * @throws Exception
     */
    public void removeCorrectionForSuperCuratorAway( IntactHelper helper ) throws Exception {
        if(DEBUG){
            System.out.println("Removing assignment to curators being away");
        }
        SuperCuratorsGetter superCurotorsGetter = new SuperCuratorsGetter();
        Collection superCurators = superCurotorsGetter.getSuperCurators();

        for ( Iterator iterator = superCurators.iterator(); iterator.hasNext(); ) {
            SuperCurator sc = (SuperCurator) iterator.next();
            if ( sc.getPercentage() == 0 ) {
                SanityCheckerHelper sch = new SanityCheckerHelper();
                sch.addMapping( ComparableExperimentBean.class, "select e.ac, e.shortlabel, e.created, e.created_user " +
                                                                        "from ia_experiment e, ia_exp2annot e2a , ia_annotation a " +
                                                                        "where e.ac = e2a.experiment_ac " +
                                                                        "and a.ac = e2a.annotation_ac " +
                                                                        "and a.topic_ac = '" + cvHolder.reviewer.getAc() + "' " +
                                                                        "and a.description = ? " );
                Collection experiments = sch.getBeans( ComparableExperimentBean.class, sc.getName().toLowerCase() );
                if( DEBUG ) {
                    System.out.println("... curator " + sc.getName() + "is away and have " + experiments.size() +
                    " experiments to review : ");
                }
                for ( Iterator iterator1 = experiments.iterator(); iterator1.hasNext(); ) {
                    ComparableExperimentBean comparableExperimentBean = (ComparableExperimentBean) iterator1.next();

                    if(DEBUG){
                        System.out.println("...... " + comparableExperimentBean.getAc() + "," + comparableExperimentBean.getShortlabel() );
                    }
                    removeReviewerAnnotation( comparableExperimentBean.getAc() );
                }
                if(DEBUG){
                    System.out.println("They will be re-assigned to the others super-curators.");
            }
        }
    }
    }

    /**
     * Remove the reviewer annotation linked to this experiment having the ac given in paremeter.
     *
     * @param expAc ac of the experiment from which we need to remove the reviewer annotation(s).
     *
     * @throws IntactException
     */
    public void removeReviewerAnnotation( String expAc ) throws IntactException {

        IntactHelper helper = new IntactHelper();
        //Get the util.model.Experiment object corresponding to this experiment ac.
        Experiment experiment = (Experiment) helper.getObjectByAc( Experiment.class, expAc );

        Collection annotations = experiment.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
            Annotation annotation = (Annotation) iterator.next();
            if ( annotation.getCvTopic().getShortLabel().equals( CvTopic.REVIEWER ) ) {
                iterator.remove();
                helper.delete( annotation );
                experiment.removeAnnotation( annotation );

            }
        }

        helper.update( experiment );

        helper.closeStore();
    }

    /**
     * From the Collection of not yet asseigned ExperimentBean, build a the hashMap pmid2expColl.
     */
    private void fillPmid2expColl() {
        for ( Iterator iterator = notAssignedExperiments.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = (ComparableExperimentBean) iterator.next();
            String pubmed = exp.getPubmedId();
            if ( pmid2expColl.containsKey( pubmed ) ) {
                Collection experiments = (Collection) pmid2expColl.get( pubmed );
                experiments.add( exp );
                pmid2expColl.put( pubmed, experiments );
            } else {
                Collection experiments = new ArrayList();
                experiments.add( exp );
                pmid2expColl.put( pubmed, experiments );
            }
        }
    }

    /**
     * From the 2 collections of ExperimentBean (assignedExperiments and notAssignedExperiment) build a map the maps
     * assignedPmid2creator notAssignedPmid2creator
     */
    private void fillPmid2CreatorMaps() {
        for ( Iterator iterator = assignedExperiments.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = (ComparableExperimentBean) iterator.next();
            assignedPmid2creator.put( exp.getPubmedId(), exp.getCreated_user().toLowerCase() );

        }

        for ( Iterator iterator = notAssignedExperiments.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = (ComparableExperimentBean) iterator.next();
            if ( !assignedPmid2creator.containsKey( exp.getPubmedId() ) ) {
                notAssignedPmid2creator.put( exp.getPubmedId(), exp.getCreated_user().toLowerCase() );
            }
        }


    }

    private void fillOnHoldAndToBeReviewedExperiments( IntactHelper helper ) throws IntactException, SQLException {
        SanityCheckerHelper sch = new SanityCheckerHelper();

        sch.addMapping( ExperimentBean.class, "select e.ac, e.created_user, e.created, e.shortlabel " +
                                                      "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
                                                      "where e2a.annotation_ac = a.ac " +
                                                      "and e2a.experiment_ac = e.ac " +
                                                      "and a.topic_ac = ? " +
                                                      "order by e.shortlabel" );
        onHoldExperiments = sch.getBeans( ExperimentBean.class, cvHolder.onHold.getAc() );
        toBeReviewedExperiments = sch.getBeans( ExperimentBean.class, cvHolder.toBeReviewed.getAc() );

    }

    private void fillNotAcceptedNotToBeReviewedExperiments( IntactHelper helper ) throws IntactException, SQLException {
        SanityCheckerHelper sch = new SanityCheckerHelper();

        sch.addMapping( ExperimentBean.class, "select ac, created_user, created, shortlabel from ia_experiment where ac not in " +
                                                      "(select e.ac " +
                                                      "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
                                                      "where e.ac=e2a.experiment_ac and " +
                                                      "e2a.annotation_ac=a.ac and " +
                                                      "a.topic_ac in ('" + cvHolder.accepted.getAc() + "','" + cvHolder.toBeReviewed.getAc() + "')) " +
                                                      "and created >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS') and ac like ? " );
        notAcceptedNotToBeReviewed = sch.getBeans( ExperimentBean.class, "%" );

    }

    /**
     * @param experiment
     *
     * @return
     *
     * @throws IntactException
     * @throws SQLException
     */
    private boolean isOnHold( IntactHelper helper, ComparableExperimentBean experiment ) throws Exception, SQLException {
        boolean onHold = true;

        CvHolder holder = getCvHolder( helper );

        SanityCheckerHelper sch = new SanityCheckerHelper();
        sch.addMapping( AnnotationBean.class, "select a.ac " +
                                                      "from ia_annotation a, ia_exp2annot e2a " +
                                                      "where e2a.annotation_ac = a.ac and " +
                                                      "a.topic_ac = '" + holder.onHold.getAc() + "' " +
                                                      "and e2a.experiment_ac = ? " );
        Collection annotations = sch.getBeans( AnnotationBean.class, experiment.getAc() );
        if ( annotations.isEmpty() ) {
            onHold = false;
        }
        return onHold;
    }

    private boolean hasNoInteractions( IntactHelper helper, ComparableExperimentBean experiment ) throws IntactException, SQLException {
        boolean hasNoInteractions = false;


        SanityCheckerHelper sch = new SanityCheckerHelper();
        sch.addMapping( Int2ExpBean.class, "select interaction_ac " +
                                                   "from ia_int2exp " +
                                                   "where experiment_ac = ? " );
        Collection int2exps = sch.getBeans( Int2ExpBean.class, experiment.getAc() );
        if ( int2exps.isEmpty() ) {
            hasNoInteractions = true;
        }
        return hasNoInteractions;
    }

    private void removeExpOnHoldAndWithNoInteraction( IntactHelper helper, Collection expBeans ) throws Exception, IntactException {
        if(DEBUG){
            System.out.println("Filtering out experiments being on-hold or without interactions : ");
        }
        for ( Iterator iterator = expBeans.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = (ComparableExperimentBean) iterator.next();
            boolean removed = false;
            if ( isOnHold( helper, exp ) ) {
                if(DEBUG){
                    System.out.println("..." + exp.getAc() + ", " + exp.getShortlabel() + " is on hold.");
                }
                iterator.remove();
                removed = true;
            }
            if ( hasNoInteractions( helper, exp ) && false == removed ) {
                if(DEBUG){
                    System.out.println("..." + exp.getAc() + ", " + exp.getShortlabel() + " has no interactions.");
                }
                iterator.remove();
            }
        }
    }

    /**
     * Class holding the needed ControlledVocabBean to build the needed experiments Collection for the correction
     * assigments (pubmed, primaryRef, reviewer, toBeReviewed, accepted and onHold).
     */
    private class CvHolder {

        /**
         * A controlledvocabBean corresponding to the pubmed cv.
         */
        final ControlledvocabBean pubmed;
        /**
         * A controlledvocabBean corresponding to the primaryRef cv.
         */
        final ControlledvocabBean primaryRef;
        /**
         * A controlledvocabBean corresponding to the reviewer cv.
         */
        final ControlledvocabBean reviewer;
        /**
         * A controlledvocabBean corresponding to the toBeReviewed cv.
         */
        final ControlledvocabBean toBeReviewed;
        /**
         * A controlledvocabBean corresponding to the accepted cv.
         */
        final ControlledvocabBean accepted;
        /**
         * A controlledvocabBean corresponding to the onHold cv.
         */
        final ControlledvocabBean onHold;


        public CvHolder( IntactHelper helper ) throws Exception, SQLException {

            SanityCheckerHelper sch = new SanityCheckerHelper();
            sch.addMapping( ControlledvocabBean.class, "SELECT ac, objclass FROM ia_controlledvocab WHERE shortlabel = ?" );

            pubmed = getCvBean( helper, CvDatabase.PUBMED, sch );


            primaryRef = getCvBean( helper, CvXrefQualifier.PRIMARY_REFERENCE, sch );
            reviewer = getCvBean( helper, CvTopic.REVIEWER, sch );
            accepted = getCvBean( helper, CvTopic.ACCEPTED, sch );
            toBeReviewed = getCvBean( helper, CvTopic.TO_BE_REVIEWED, sch );
            onHold = getCvBean( helper, CvTopic.ON_HOLD, sch );
        }

        /**
         * @param shortlabel the shortlabel of the controlled vocabulary you want to load
         * @param sch        A sanityCheckerHelper. The mapping for ControlledvocabBean.class should already be done as
         *                   it's not done inside the method.
         *
         * @return ControlledvocabBean having as shortlabel the given shortlabel in argument.
         *
         * @throws Exception if the controlledvocabBean corresponding to this shortlabel was not found.
         */
        private ControlledvocabBean getCvBean( IntactHelper helper, String shortlabel, SanityCheckerHelper sch ) throws Exception {
            ControlledvocabBean cvBean;

            List cvBeans = sch.getBeans( ControlledvocabBean.class, shortlabel );
            if ( !cvBeans.isEmpty() ) {
                cvBean = (ControlledvocabBean) cvBeans.get( 0 );
            } else {
                throw new Exception( "Couldn't create ControlledvocabBean for " + shortlabel );
            }

            return cvBean;
        }

    }
}

