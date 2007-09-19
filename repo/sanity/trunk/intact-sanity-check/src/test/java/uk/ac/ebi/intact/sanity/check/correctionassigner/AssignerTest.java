package uk.ac.ebi.intact.sanity.check.correctionassigner;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.standard.InteractionPersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.check.AbstractSanityLegacyTest;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.config.SanityConfigurationException;
import uk.ac.ebi.intact.sanity.check.config.SuperCurator;
import uk.ac.ebi.intact.sanity.check.model.ComparableExperimentBean;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IntactUnitDataset( dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
public class AssignerTest extends AbstractSanityLegacyTest {

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
        for ( Annotation a : exp.getAnnotations() ) {
            if ( a.getCvTopic().getShortLabel().equals( CvTopic.REVIEWER ) && a.getAnnotationText().equals( reviewerName ) ) {
                return;
            }
        }
        Assert.fail( "Experiment " + exp.getAc() + " should have a to-be-reviewed annotation." );
    }

    //////////////////////
    // Tests

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
            if( i < 3 ) {
                experiment.addAnnotation( mockBuilder.createAnnotation( "anne", "IA:9999", CvTopic.REVIEWER ) );
            }
            persistExperiment( experiment );
        }
        commitTransaction();

        List<SuperCurator> curators = new ArrayList<SuperCurator>( 2 );
        curators.add( new SuperCurator( 100, "Peter" ) );
        curators.add( new SuperCurator( 0, "Anne" ) );

        beginTransaction();

        SanityCheckConfig config = new SanityCheckConfig( curators );

        Assigner assigner = new Assigner( config, true );
        assigner.assign();

        Assert.assertEquals( 0, config.getSuperCurator( "Anne" ).getExperiments().size() );
        Assert.assertEquals( 5, config.getSuperCurator( "Peter" ).getExperiments().size() );

        commitTransaction();
        beginTransaction();

        for ( Experiment exp : getDaoFactory().getExperimentDao().getAll() ) {
            assertHasReviewer( exp, "Peter" );
        }

        commitTransaction();
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

    private void persistExperiment( Experiment experiment ) throws PersisterException {
        for ( Interaction interaction : experiment.getInteractions() ) {
            InteractionPersister.getInstance().saveOrUpdate( interaction );
        }
        InteractionPersister.getInstance().commit();
    }
}