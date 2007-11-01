package uk.ac.ebi.intact.sanity.check.correctionassigner;

import org.junit.*;
import uk.ac.ebi.intact.config.impl.SmallCvPrimer;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.standard.InteractionPersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.sanity.check.AbstractSanityLegacyTest;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.config.SanityConfigurationException;
import uk.ac.ebi.intact.sanity.check.config.SuperCurator;
import uk.ac.ebi.intact.sanity.check.model.ComparableExperimentBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Assigner Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AssignerTest extends AbstractSanityLegacyTest {

    /**
     * Creates required CVs for this test.
     */
    private class CorrectionAssignerCvPrimer extends SmallCvPrimer {

        public CorrectionAssignerCvPrimer( DaoFactory daoFactory ) {
            super( daoFactory );
        }

        public void createCVs() {
            super.createCVs();
            getCvObject( CvTopic.class, CvTopic.REVIEWER );
            getCvObject( CvTopic.class, CvTopic.TO_BE_REVIEWED );
            getCvObject( CvTopic.class, CvTopic.ACCEPTED );
            getCvObject( CvTopic.class, CvTopic.ON_HOLD );
        }
    }

    @Before
    public void initializeCvs() throws Exception {
        new IntactUnit().createSchema( true );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final CorrectionAssignerCvPrimer correctionAssignerCvPrimer = new CorrectionAssignerCvPrimer(daoFactory);

        beginTransaction();
        correctionAssignerCvPrimer.createCVs();
        commitTransaction();
    }

    @After
    public void finishTransactionIfNecessary() throws Exception {
        commitTransaction();
    }

    ///////////////////////
    // Local asserts

    private void assertHasToBeReviewed( Experiment exp ) {
        for ( Annotation a : exp.getAnnotations() ) {
            if ( a.getCvTopic().getShortLabel().equals( CvTopic.TO_BE_REVIEWED ) ) {
                return;
            }
        }
        Assert.fail( "Experiment " + exp.getAc() + " should have a to-be-reviewed annotation." );
    }

    private void assertHasReviewer( Experiment exp, String reviewerName ) {
        Assert.assertNotNull( reviewerName );
        System.out.println( exp );
        for ( Annotation a : exp.getAnnotations() ) {
            System.out.println( a );
            if ( a.getCvTopic().getShortLabel().equals( CvTopic.REVIEWER ) && a.getAnnotationText().equals( reviewerName ) ) {
                return;
            }
        }
        Assert.fail( "Experiment " + exp.getAc() + " should have a reviewer("+ reviewerName +") annotation." );
    }

    private void assertHasNoReviewer( Experiment exp ) {
        for ( Annotation a : exp.getAnnotations() ) {
            if ( a.getCvTopic().getShortLabel().equals( CvTopic.REVIEWER ) ) {
                Assert.fail( "Experiment " + exp.getAc() + " should not have a reviewer annotation." );
            }
        }
        Assert.fail( "Experiment " + exp.getAc() + " should have a to-be-reviewed annotation." );
    }

    //////////////////////
    // Tests

    // TODO check that if John has created all of these experiments, then he should not be assigned to check them

    @Test
    public void assign_default() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        for ( int i = 0; i < 5; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 5 );

            beginTransaction();
            persistExperiment( experiment );
            commitTransaction();
        }

        beginTransaction();

        SanityCheckConfig config = super.getSanityCheckConfig();

        Assigner assigner = new Assigner( config, false );
        assigner.assign();

        Assert.assertEquals( 5, config.getSuperCurator( "John" ).getExperiments().size() );

        commitTransaction();
    }

    @Test
    public void assign_multiple_super_curators() throws Exception {

        SanityCheckConfig config = super.getSanityCheckConfig();
        final List<SuperCurator> superCurators = config.getSuperCurators();
        Assert.assertNotNull( superCurators );
        Assert.assertEquals( 1, superCurators.size() );

        SuperCurator john = config.getSuperCurator( "John" );
        Assert.assertNotNull( john );
        Assert.assertEquals( 100, john.getPercentage() );

        // add 2 more super curators so we have 3 thirds for the assignment
        SuperCurator ellen = new SuperCurator( 33, "Ellen" );
        SuperCurator donald = new SuperCurator( 33, "Donald" );
        john.setPercentage( 34 );

        config.getSuperCurators().add( ellen );
        config.getSuperCurators().add( donald );

        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        for ( int i = 0; i < 9; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 2 );

            beginTransaction();
            persistExperiment( experiment );
            commitTransaction();
        }

        beginTransaction();

        Assigner assigner = new Assigner( config, false );
        assigner.assign();

        Assert.assertEquals( 3, config.getSuperCurator( "John" ).getExperiments().size() );
        Assert.assertEquals( 3, config.getSuperCurator( "Ellen" ).getExperiments().size() );
        Assert.assertEquals( 3, config.getSuperCurator( "Donald" ).getExperiments().size() );

        commitTransaction();
    }

    @Test
    public void assign_multiple_super_curators_2() throws Exception {

        SanityCheckConfig config = super.getSanityCheckConfig();
        final List<SuperCurator> superCurators = config.getSuperCurators();
        Assert.assertNotNull( superCurators );
        Assert.assertEquals( 1, superCurators.size() );

        SuperCurator john = config.getSuperCurator( "John" );
        Assert.assertNotNull( john );
        Assert.assertEquals( 100, john.getPercentage() );

        // add 2 more super curators so we have 3 thirds for the assignment
        SuperCurator ellen = new SuperCurator( 33, "Ellen" );
        SuperCurator donald = new SuperCurator( 33, "Donald" );
        john.setPercentage( 34 );

        config.getSuperCurators().add( ellen );
        config.getSuperCurators().add( donald );

        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        for ( int i = 0; i < 10; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 2 );

            beginTransaction();
            persistExperiment( experiment );
            commitTransaction();
        }

        beginTransaction();

        Assigner assigner = new Assigner( config, false );
        assigner.assign();

        // john being the super curator that has the highest percentage and is not the original curator, he gets 1 more
        Assert.assertEquals( 4, config.getSuperCurator( "John" ).getExperiments().size() );
        Assert.assertEquals( 3, config.getSuperCurator( "Ellen" ).getExperiments().size() );
        Assert.assertEquals( 3, config.getSuperCurator( "Donald" ).getExperiments().size() );

        commitTransaction();
    }

    @Test
    public void assign_experiment_accepted() throws Exception {

        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        for ( int i = 0; i < 5; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 5 );

            beginTransaction();

            if ( i == 0 || i == 1 || i == 2 ) {
                experiment.addAnnotation( mockBuilder.createAnnotation( "", "IA:9999", CvTopic.ACCEPTED ) );
                experiment.addAnnotation( mockBuilder.createAnnotation( "Bruno", "IA:9999", CvTopic.REVIEWER ) );
            }
            persistExperiment( experiment );
            commitTransaction();
        }

        beginTransaction();

        SanityCheckConfig config = super.getSanityCheckConfig();

        Assigner assigner = new Assigner( config, true );
        assigner.assign();

        Collection<ComparableExperimentBean> experiments = config.getSuperCurator( "John" ).getExperiments();
        Assert.assertEquals( 2, experiments.size() );

        commitTransaction();
        beginTransaction();

        for ( ComparableExperimentBean experiment : experiments ) {
            Experiment exp = getDaoFactory().getExperimentDao().getByAc( experiment.getAc() );
            Assert.assertNotNull( exp );
            assertHasReviewer( exp, "John" );
        }

        commitTransaction();
    }

    @Test
    public void assign_experiment_onhold() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        for ( int i = 0; i < 5; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 5 );

            beginTransaction();

            if ( i == 0 || i == 1 || i == 2 ) {
                experiment.addAnnotation( mockBuilder.createAnnotation( "", "IA:9999", CvTopic.ON_HOLD ) );
            }
            persistExperiment( experiment );
            commitTransaction();
        }

        beginTransaction();

        SanityCheckConfig config = super.getSanityCheckConfig();

        Assigner assigner = new Assigner( config, false );
        assigner.assign();

        Collection<ComparableExperimentBean> experiments = config.getSuperCurator( "John" ).getExperiments();
        Assert.assertEquals( 2, experiments.size() );

        commitTransaction();
        beginTransaction();

        for ( ComparableExperimentBean experiment : experiments ) {
            Experiment exp = getDaoFactory().getExperimentDao().getByAc( experiment.getAc() );
            Assert.assertNotNull( exp );
            assertHasReviewer( exp, "John" );
        }

        commitTransaction();
    }

    @Test
    public void assign_experiment_with_reviewer() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        Experiment experiment = mockBuilder.createExperimentRandom( 5 );

        beginTransaction();
        experiment.addAnnotation( mockBuilder.createAnnotation( "John", "IA:9999", CvTopic.REVIEWER ) );
        persistExperiment( experiment );
        commitTransaction();

        beginTransaction();

        SanityCheckConfig config = super.getSanityCheckConfig();

        Assigner assigner = new Assigner( config, false );
        assigner.assign();

        Collection<ComparableExperimentBean> experiments = config.getSuperCurator( "John" ).getExperiments();
        Assert.assertEquals( 1, experiments.size() );

        commitTransaction();
        beginTransaction();

        Experiment exp = getDaoFactory().getExperimentDao().getByAc( experiment.getAc() );
        Assert.assertNotNull( exp );
        assertHasReviewer( exp, "John" );

        commitTransaction();
    }

    @Test( expected = SanityConfigurationException.class )
    public void assign_wrongPercentages() throws Exception {
        List<SuperCurator> curators = new ArrayList<SuperCurator>( 2 );
        curators.add( new SuperCurator( 60, "Peter" ) );
        curators.add( new SuperCurator( 50, "Anne" ) );

        beginTransaction();

        SanityCheckConfig config = new SanityCheckConfig( curators );

        Assigner assigner = new Assigner( config, false );
        assigner.assign();

        commitTransaction();
    }

    @Test
    public void assign_twoCurators_1away() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        beginTransaction();

        for ( int i = 0; i < 5; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 5 );
            if ( i < 3 ) {
                experiment.addAnnotation( mockBuilder.createAnnotation( "Anne", "IA:9999", CvTopic.REVIEWER ) );
            }
            persistExperiment( experiment );
        }
        commitTransaction();

        List<SuperCurator> curators = new ArrayList<SuperCurator>( 2 );
        curators.add( new SuperCurator( 100, "Peter" ) );
        curators.add( new SuperCurator( 0, "Anne" ) );


        SanityCheckConfig config = new SanityCheckConfig( curators );

        Assigner assigner = new Assigner( config, true );
        assigner.assign();

        final Collection<ComparableExperimentBean> anneExps = config.getSuperCurator("Anne").getExperiments();
        final Collection<ComparableExperimentBean> peterExps = config.getSuperCurator("Peter").getExperiments();
        Assert.assertEquals( 3, anneExps.size() );
        Assert.assertEquals( 2, peterExps.size() );

        for ( ComparableExperimentBean ceb : anneExps ) {
            Experiment exp = getDaoFactory().getExperimentDao().getByAc(ceb.getAc());
            assertHasReviewer( exp, "Anne" );
        }
        
        for ( ComparableExperimentBean ceb : peterExps ) {
            Experiment exp = getDaoFactory().getExperimentDao().getByAc(ceb.getAc());
            assertHasReviewer( exp, "Peter" );
        }
    }

    @Test
    public void assign_twoCurators() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );

        beginTransaction();

        for ( int i = 0; i < 5; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 5 );


            persistExperiment( experiment );

        }
        commitTransaction();

        List<SuperCurator> curators = new ArrayList<SuperCurator>( 2 );
        curators.add( new SuperCurator( 50, "Peter" ) );
        curators.add( new SuperCurator( 50, "Anne" ) );

        beginTransaction();

        SanityCheckConfig config = new SanityCheckConfig( curators );

        Assigner assigner = new Assigner( config, false );
        assigner.assign();

        Assert.assertEquals( 3, config.getSuperCurator( "Peter" ).getExperiments().size() );
        Assert.assertEquals( 2, config.getSuperCurator( "Anne" ).getExperiments().size() );

        commitTransaction();
    }

    @Test
    @Ignore // TODO implement this feature !!!
    public void assign_publication_partially_on_hold() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
        final String pubmedId = "123456789";
        Publication publication = mockBuilder.createPublication( pubmedId );

        CvXrefQualifier primaryRef = mockBuilder.createCvObject( CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF, CvXrefQualifier.PRIMARY_REFERENCE );
        CvDatabase pubmed = mockBuilder.createCvObject( CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED );

        beginTransaction();

        // 3 experiments attached to a single publication, one experiment on-hold so no experiment should get assigned.
        for ( int i = 0; i < 3; i++ ) {
            Experiment experiment = mockBuilder.createExperimentRandom( 3 );
            experiment.setPublication( publication );

            // set the primary-reference of the experiment
            Collection<Xref> xrefs = AnnotatedObjectUtils.searchXrefs( experiment, pubmed, primaryRef );
            Assert.assertNotNull( xrefs );
            Assert.assertEquals( 1, xrefs.size() );
            xrefs.iterator().next().setPrimaryId( pubmedId );

            if ( i == 0 ) {
                experiment.addAnnotation( mockBuilder.createAnnotation( "Almost finished ;)", "IA:1234", CvTopic.ON_HOLD ) );
            }

            persistExperiment( experiment );
        }
        commitTransaction();

        List<SuperCurator> curators = new ArrayList<SuperCurator>( 2 );
        curators.add( new SuperCurator( 50, "Peter" ) );
        curators.add( new SuperCurator( 50, "Anne" ) );

        beginTransaction();

        SanityCheckConfig config = new SanityCheckConfig( curators );

        Assigner assigner = new Assigner( config, true );
        assigner.assign();

        Assert.assertEquals( 0, config.getSuperCurator( "Anne" ).getExperiments().size() );
        Assert.assertEquals( 0, config.getSuperCurator( "Peter" ).getExperiments().size() );

        commitTransaction();
        beginTransaction();

        for ( Experiment exp : getDaoFactory().getExperimentDao().getAll() ) {
            assertHasNoReviewer( exp );
        }

        commitTransaction();
    }

    ////////////////////
    // Utilities

    private void persistExperiment( Experiment experiment ) throws PersisterException {
        for ( Interaction interaction : experiment.getInteractions() ) {
            InteractionPersister.getInstance().saveOrUpdate( interaction );
        }
        InteractionPersister.getInstance().commit();
    }
}