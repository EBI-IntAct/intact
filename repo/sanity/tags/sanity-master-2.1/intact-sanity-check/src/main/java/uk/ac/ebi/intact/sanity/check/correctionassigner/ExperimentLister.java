/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.correctionassigner;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.OracleDialect;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.sanity.check.SanityCheckerHelper;
import uk.ac.ebi.intact.sanity.check.config.SuperCurator;
import uk.ac.ebi.intact.sanity.check.model.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Class that stores experiments and gives access to them according to criteria. Criteria can be of type: on-hold,
 * assigned, not-assigned or to-be-reviewed.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: ExperimentLister.java,v 1.4 2006/04/13 12:38:57 skerrien Exp $
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
    private Map pmid2expColl = new HashMap();

    /**
     * HashMap permiting to map pubmed id of experiments(not assigned to any reviewer) to their creator.
     */
    private Map<String, String> notAssignedPmid2creator = new HashMap<String, String>();

    /**
     * HashMap permiting to map pubmed id of experiments (assigned to a reviewer) to their creator.
     */
    private Map assignedPmid2creator = new HashMap();

    private static boolean DEBUG = false;

    private Collection<SuperCurator> superCurators;

    /**
     * Constructor of ExperimentLister.
     *
     * @throws SQLException
     * @throws IntactException
     */
    public ExperimentLister( Collection<SuperCurator> superCurators, boolean debug ) throws Exception, IntactException {
        this.DEBUG = debug;
        this.superCurators = superCurators;

        initialize();
    }

    private CvHolder getCvHolder() throws Exception {
        if ( cvHolder == null ) {
            cvHolder = new CvHolder();
        }
        return cvHolder;
    }

    private void initialize() throws Exception, SQLException {

        cvHolder = getCvHolder();

        removeCorrectionForSuperCuratorAway();

        fillNotAssignedExpCollection();

        // TODO remove also experiments that are part of publication from which an experiment is on-hold
        removeExpOnHoldAndWithNoInteraction( notAssignedExperiments );

        fillAssignedExpCollection();
        removeExpOnHoldAndWithNoInteraction( assignedExperiments );

        fillPmid2CreatorMaps();
        fillPmid2expColl();
        fillNotAcceptedNotToBeReviewedExperiments();
        fillOnHoldAndToBeReviewedExperiments();
    }

    /**
     * Getter for the HashMap pmid2expColl.
     *
     * @return the HashMap pmid2expColl.
     */
    public Map getPmid2expColl() {
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
    public Map getNotAssignedPmid2creator() {
        return notAssignedPmid2creator;
    }

    public Collection getOnHoldExperiments() {
        return onHoldExperiments;
    }

    public Collection getToBeReviewedExperiments() {
        return toBeReviewedExperiments;
    }

    public Collection getNotAcceptedNotToBeReviewed() {
        return notAcceptedNotToBeReviewed;
    }

    /**
     * Via the sanityCheckerHelper this method is using the dbUtils library to get the Collection notAssignedExperiments
     * of experimentBeans being to assigned to a reviewer for correction.
     *
     * @throws IntactException
     * @throws SQLException
     */
    private void fillNotAssignedExpCollection() throws Exception, SQLException {

        if ( DEBUG ) {
            System.out.println( "Searching for experiment not accepted, not to-be-reviewed and not assigned : " );
        }

        CvHolder holder = getCvHolder();

        SanityCheckerHelper sch = new SanityCheckerHelper();


        String sql = "select e.ac, e.created_user, e.created, e.shortlabel, x.primaryId as pubmedId \n" +
                     "from ia_experiment e, ia_experiment_xref x \n" +
                     "where x.parent_ac = e.ac and \n" +
                     "x.database_ac = '" + holder.pubmed.getAc() + "' and \n" +
                     "x.qualifier_ac = '" + holder.primaryRef.getAc() + "' and \n" +
                     "e.ac not in ( \n" +
                     "             select e.ac \n" +
                     "             from ia_experiment e, ia_exp2annot e2a, ia_annotation a \n" +
                     "             where e.ac=e2a.experiment_ac and \n" +
                     "                   e2a.annotation_ac=a.ac and \n" +
                     "                   a.topic_ac in  ('" + holder.accepted.getAc() + "','" + holder.toBeReviewed.getAc() + "') \n" +
                     "             union \n" +
                     "             select e.ac \n" +
                     "             from ia_experiment e, ia_exp2annot e2a, ia_annotation a \n" +
                     "             where e.ac=e2a.experiment_ac and \n" +
                     "                   e2a.annotation_ac=a.ac and \n" +
                     "                   a.topic_ac in  ('" + holder.reviewer.getAc() + "') \n" +
                     "            ) \n" +
                     "and e.created >  " + getToDateSqlFirstSep2005() + " and e.ac like ? \n" +
                     "order by created_user";

        sch.addMapping( ComparableExperimentBean.class, sql );
        if ( DEBUG ) {
            System.out.println( "... Here is the request done\n\n" );
            System.out.println( sql + "\n\n" );
        }

        notAssignedExperiments = sch.getBeans( ComparableExperimentBean.class, "%" );

        if ( DEBUG ) {
            System.out.println( "..." + notAssignedExperiments.size() + " not assigned experiments found." );
        }
    }

    /**
     * * Via the sanityCheckerHelper this method is using the dbUtils library to get the Collection assignedExperiments
     * of experimentBeans being already assigned in the past to a reviewer for correction.
     *
     * @throws IntactException
     * @throws SQLException
     */
    private void fillAssignedExpCollection() throws Exception, SQLException {
        if ( DEBUG ) {
            System.out.println( "Searching for experiments assigned and not accepted or to-be-reviewed : " );
        }
        CvHolder holder = getCvHolder();

        SanityCheckerHelper sch = new SanityCheckerHelper();
        sch.addMapping( ComparableExperimentBean.class, "select e.ac, e.created_user, e.created, e.shortlabel, x.primaryId as pubmedId, a.description as reviewer " +
                                                        "from ia_experiment e, ia_exp2annot e2a, ia_annotation a, ia_experiment_xref x " +
                                                        "where e.ac=e2a.experiment_ac and " +
                                                        "x.parent_ac = e.ac and " +
                                                        "x.parent_ac = e.ac and " +
                                                        "x.database_ac = '" + holder.pubmed.getAc() + "' and " +
                                                        "x.qualifier_ac = '" + holder.primaryRef.getAc() + "' and " +
                                                        "e2a.annotation_ac=a.ac and " +
                                                        "e.ac not in (" +
                                                        "select e.ac " +
                                                        "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
                                                        "where e.ac=e2a.experiment_ac and " +
                                                        "e2a.annotation_ac=a.ac and " +
                                                        "a.topic_ac in  ('" + holder.accepted.getAc() + "','" + holder.toBeReviewed.getAc() + "') " +
                                                        ") and " +
                                                        "a.topic_ac in  ('" + holder.reviewer.getAc() + "') " +
                                                        "and e.created >  " + getToDateSqlFirstSep2005() + " and e.ac like ? " +
                                                        "order by created_user" );

        assignedExperiments = sch.getBeans( ComparableExperimentBean.class, "%" );
        if ( DEBUG ) {
            System.out.println( "..." + assignedExperiments.size() + " experiments found." );
            System.out.println( "... Here was the request done : \n\n" );
            System.out.println( "select e.ac, e.created_user, e.created, e.shortlabel, x.primaryId as pubmedId, a.description as reviewer \n" +
                                "from ia_experiment e, ia_exp2annot e2a, ia_annotation a, ia_experiment_xref x \n" +
                                "where e.ac=e2a.experiment_ac and \n" +
                                "x.parent_ac = e.ac and \n" +
                                "x.parent_ac = e.ac and \n" +
                                "x.database_ac = '" + holder.pubmed.getAc() + "' and \n" +
                                "x.qualifier_ac = '" + holder.primaryRef.getAc() + "' and \n" +
                                "e2a.annotation_ac=a.ac and \n" +
                                "e.ac not in (\n" +
                                "select e.ac \n" +
                                "from ia_experiment e, ia_exp2annot e2a, ia_annotation a \n" +
                                "where e.ac=e2a.experiment_ac and \n" +
                                "e2a.annotation_ac=a.ac and \n" +
                                "a.topic_ac in  ('" + holder.accepted.getAc() + "','" + holder.toBeReviewed.getAc() + "')\n " +
                                ") and " +
                                "a.topic_ac in  ('" + holder.reviewer.getAc() + "') \n" +
                                "and e.created >  " + getToDateSqlFirstSep2005() + " and e.ac like ? \n" +
                                "order by created_user\n\n" );
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
    public void removeCorrectionForSuperCuratorAway() throws Exception {
        if ( DEBUG ) {
            System.out.println( "Removing assignment to curators being away" );
        }

        for ( Iterator iterator = superCurators.iterator(); iterator.hasNext(); ) {
            SuperCurator sc = ( SuperCurator ) iterator.next();
            if ( sc.getPercentage() == 0 ) {
                if ( DEBUG ) {
                    System.out.println( sc.getName() + " has a percentage of assignement of 0% ... remove her from all experiments" );
                    // TODO what about experiment that are already accepted.
                }
                SanityCheckerHelper sch = new SanityCheckerHelper();

                // TODO exclude from this one the experiment that also have accepted.
                sch.addMapping( ComparableExperimentBean.class, "select e.ac, e.shortlabel, e.created, e.created_user " +
                                                                "from ia_experiment e, ia_exp2annot e2a , ia_annotation a " +
                                                                "where e.ac = e2a.experiment_ac " +
                                                                "and a.ac = e2a.annotation_ac " +
                                                                "and a.topic_ac = '" + cvHolder.reviewer.getAc() + "' " +
                                                                "and a.description = ? " );
                Collection experiments = sch.getBeans( ComparableExperimentBean.class, sc.getName().toLowerCase() );

                // TODO what about experiment that are already accepted.

                if ( DEBUG ) {
                    System.out.println( "... curator " + sc.getName() + " is away (assignement: 0%) and has " +
                                        experiments.size() + " experiments to review : " );
                }
                for ( Iterator iterator1 = experiments.iterator(); iterator1.hasNext(); ) {
                    ComparableExperimentBean comparableExperimentBean = ( ComparableExperimentBean ) iterator1.next();

                    if ( DEBUG ) {
                        System.out.println( "...... " + comparableExperimentBean.getAc() + "," + comparableExperimentBean.getShortlabel() );
                    }
                    removeReviewerAnnotation( comparableExperimentBean.getAc() );
                }
                if ( DEBUG ) {
                    System.out.println( "They will be re-assigned to the other super-curators." );
                }
            }
        }
    }

    /**
     * Remove the reviewer annotation linked to this experiment having the ac given in paremeter.
     *
     * @param expAc ac of the experiment from which we need to remove the reviewer annotation(s).
     * @throws IntactException
     */
    public void removeReviewerAnnotation( String expAc ) throws IntactException {
        //Get the util.model.Experiment object corresponding to this experiment ac.
        Experiment experiment = getDaoFactory().getExperimentDao().getByAc( expAc );

        boolean updated = false;

        Collection annotations = experiment.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
            Annotation annotation = ( Annotation ) iterator.next();
            if ( annotation.getCvTopic().getShortLabel().equals( CvTopic.REVIEWER ) ) {
                if ( DEBUG )
                    System.out.println( "Found annotation REVIEWER on experiment: " + experiment.getShortLabel() + " ... deleting it" );
                iterator.remove();
                // TODO fails here to delete the annotation ... transaction is not commited ?!
                getDaoFactory().getAnnotationDao().delete( annotation );
                experiment.removeAnnotation( annotation );
                updated = true;
            }
        }

        if ( updated ) {
            getDaoFactory().getExperimentDao().update( experiment );
            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
                IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            } catch ( IntactTransactionException e ) {
                throw new RuntimeException( e );
            }
        } else {
            if ( DEBUG )
                System.out.println( "Could not find an annotation REVIEWER on experiment: " + experiment.getShortLabel() );
        }
    }

    /**
     * From the Collection of not yet asseigned ExperimentBean, build a the hashMap pmid2expColl.
     */
    private void fillPmid2expColl() {
        for ( Iterator iterator = notAssignedExperiments.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = ( ComparableExperimentBean ) iterator.next();
            String pubmed = exp.getPubmedId();
            if ( pmid2expColl.containsKey( pubmed ) ) {
                Collection experiments = ( Collection ) pmid2expColl.get( pubmed );
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
            ComparableExperimentBean exp = ( ComparableExperimentBean ) iterator.next();
            assignedPmid2creator.put( exp.getPubmedId(), exp.getCreated_user().toLowerCase() );
        }

        for ( Iterator iterator = notAssignedExperiments.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = ( ComparableExperimentBean ) iterator.next();
            if ( !assignedPmid2creator.containsKey( exp.getPubmedId() ) ) {
                notAssignedPmid2creator.put( exp.getPubmedId(), exp.getCreated_user().toLowerCase() );
            }
        }
    }

    private void fillOnHoldAndToBeReviewedExperiments() throws IntactException, SQLException {
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

    private void fillNotAcceptedNotToBeReviewedExperiments() throws IntactException, SQLException {
        SanityCheckerHelper sch = new SanityCheckerHelper();

        sch.addMapping( ExperimentBean.class, "select ac, created_user, created, shortlabel from ia_experiment where ac not in " +
                                              "(select e.ac " +
                                              "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
                                              "where e.ac=e2a.experiment_ac and " +
                                              "e2a.annotation_ac=a.ac and " +
                                              "a.topic_ac in ('" + cvHolder.accepted.getAc() + "','" + cvHolder.toBeReviewed.getAc() + "')) " +
                                              "and created >  " + getToDateSqlFirstSep2005() + " and ac like ? " );
        notAcceptedNotToBeReviewed = sch.getBeans( ExperimentBean.class, "%" );
    }

    /**
     * @param experiment
     * @return
     * @throws IntactException
     * @throws SQLException
     */
    private boolean isOnHold( ComparableExperimentBean experiment ) throws Exception, SQLException {
        boolean onHold = true;

        CvHolder holder = getCvHolder();

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

    private boolean hasNoInteractions( ComparableExperimentBean experiment ) throws IntactException, SQLException {
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

    private void removeExpOnHoldAndWithNoInteraction( Collection expBeans ) throws Exception, IntactException {
        if ( DEBUG ) {
            System.out.println( "Filtering out experiments being on-hold or without interactions : " );
        }
        for ( Iterator iterator = expBeans.iterator(); iterator.hasNext(); ) {
            ComparableExperimentBean exp = ( ComparableExperimentBean ) iterator.next();
            boolean removed = false;
            if ( isOnHold( exp ) ) {
                if ( DEBUG ) {
                    System.out.println( "..." + exp.getAc() + ", " + exp.getShortlabel() + " is on hold." );
                }
                iterator.remove();
                removed = true;
            }
            if ( hasNoInteractions( exp ) && false == removed ) {
                if ( DEBUG ) {
                    System.out.println( "..." + exp.getAc() + ", " + exp.getShortlabel() + " has no interactions." );
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


        public CvHolder() throws Exception, SQLException {

            SanityCheckerHelper sch = new SanityCheckerHelper();
            sch.addMapping( ControlledvocabBean.class, "SELECT ac, objclass FROM ia_controlledvocab WHERE shortlabel = ?" );

            pubmed = getCvBean( CvDatabase.PUBMED, sch );


            primaryRef = getCvBean( CvXrefQualifier.PRIMARY_REFERENCE, sch );
            reviewer = getCvBean( CvTopic.REVIEWER, sch );
            accepted = getCvBean( CvTopic.ACCEPTED, sch );
            toBeReviewed = getCvBean( CvTopic.TO_BE_REVIEWED, sch );
            onHold = getCvBean( CvTopic.ON_HOLD, sch );
        }

        /**
         * @param shortlabel the shortlabel of the controlled vocabulary you want to load
         * @param sch        A sanityCheckerHelper. The mapping for ControlledvocabBean.class should already be done as
         *                   it's not done inside the method.
         * @return ControlledvocabBean having as shortlabel the given shortlabel in argument.
         * @throws Exception if the controlledvocabBean corresponding to this shortlabel was not found.
         */
        private ControlledvocabBean getCvBean( String shortlabel, SanityCheckerHelper sch ) throws Exception {
            ControlledvocabBean cvBean;

            List cvBeans = sch.getBeans( ControlledvocabBean.class, shortlabel );
            if ( !cvBeans.isEmpty() ) {
                cvBean = ( ControlledvocabBean ) cvBeans.get( 0 );
            } else {
                throw new Exception( "Couldn't create ControlledvocabBean for shortlabel '" + shortlabel + "'" );
            }

            return cvBean;
        }

    }

    private static String getToDateSqlFirstSep2005() {
        DataConfig dataConfig = IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig();
        Dialect dialect = Dialect.getDialect( ( ( Configuration ) dataConfig.getConfiguration() ).getProperties() );

        String dateSql = "TIMESTAMP '2005-09-01 00:00:00'"; //H2

        if ( dialect instanceof OracleDialect ) {
            dateSql = "to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS')";
        }

        return dateSql;
    }

    private static DaoFactory getDaoFactory() {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }
}

