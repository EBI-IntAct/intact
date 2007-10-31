package uk.ac.ebi.intact.sanity.check.range;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.standard.InteractionPersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.check.AbstractSanityLegacyTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Range Checker Tester
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RangeCheckerTest extends AbstractSanityLegacyTest {

    @Test
    public void correct_range() throws Exception {

        String seq = "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" + // 60
                     "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" + // 120
                     "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" + // 180
                     "ETQPKRKCCIF";                                                   // 191

        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
        Experiment exp = mockBuilder.createExperimentEmpty( "kerrien-2007-1" );
        Protein bait = mockBuilder.createProtein( "P12345", "foo" );
        bait.setSequence( seq );
        Protein prey = mockBuilder.createProtein( "Q98765", "bar" );
        Interaction interaction = mockBuilder.createInteraction( "foo-bar", bait, prey, exp );
        Assert.assertEquals( 2, interaction.getComponents().size() );
        for ( Component component : interaction.getComponents() ) {
            if ( component.getInteractor() == bait ) {
                // add a feature
                Feature feature = mockBuilder.createFeatureRandom();
                feature.setCvFeatureType( mockBuilder.createCvObject( CvFeatureType.class, "MI:0117", "binding site" ) );
                Range range = mockBuilder.createRange( 4, 4, 20, 20 ); // KCVVVGDGAVGKTCLL
                range.setFromCvFuzzyType( null );
                range.setToCvFuzzyType( null );
                feature.addRange( range );
                range.prepareSequence( bait.getSequence() );
                Assert.assertEquals( 100, range.getSequence().length() );
                Assert.assertEquals( seq.substring( 4, 104 ), range.getSequence() );
                component.addBindingDomain( feature );
            }
        }

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate( interaction );
        InteractionPersister.getInstance().commit();

        // the persister might have synchronized the bait with an already existing protein in the database, do reload it
        final List<ProteinImpl> list = getDaoFactory().getInteractorDao( ProteinImpl.class ).getByXrefLike( "P12345" );
        Assert.assertNotNull( list );
        Assert.assertEquals( 1, list.size() );
        bait = list.iterator().next();
        String ac = bait.getAc();
        Assert.assertNotNull( ac );

        // check on that protein
        beginTransaction();
        RangeChecker checker = new RangeChecker( getSanityCheckConfig() );
        final Collection<RangeReport> reports = checker.check( Arrays.asList( ac ) );
        Assert.assertNotNull( reports );
        Assert.assertTrue( reports.isEmpty() );
        commitTransaction();

        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void leading_methionine_removed() throws Exception {
        String seq = "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" + // 60
                     "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" + // 120
                     "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" + // 180
                     "ETQPKRKCCIF";                                                   // 191

        String seq2 = "QTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" + // 59
                      "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" + // 119
                      "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" + // 179
                      "ETQPKRKCCIF";                                                   // 19

        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
        Experiment exp = mockBuilder.createExperimentEmpty( "kerrien-2007-1" );
        Protein bait = mockBuilder.createProtein( "P12345", "foo" );
        bait.setSequence( seq );
        Protein prey = mockBuilder.createProtein( "Q98765", "bar" );
        Interaction interaction = mockBuilder.createInteraction( "foo-bar", bait, prey, exp );
        Assert.assertEquals( 2, interaction.getComponents().size() );
        for ( Component component : interaction.getComponents() ) {
            if ( component.getInteractor() == bait ) {
                // add a feature
                Feature feature = mockBuilder.createFeatureRandom();
                feature.setCvFeatureType( mockBuilder.createCvObject( CvFeatureType.class, "MI:0117", "binding site" ) );
                Range range = mockBuilder.createRange( 4, 4, 20, 20 ); // KCVVVGDGAVGKTCLL
                range.setFromCvFuzzyType( null );
                range.setToCvFuzzyType( null );
                feature.addRange( range );
                range.prepareSequence( bait.getSequence() );
                Assert.assertEquals( 100, range.getSequence().length() );
                Assert.assertEquals( seq.substring( 4, 104 ), range.getSequence() );
                component.addBindingDomain( feature );
            }
        }

        bait.setSequence( seq2 );

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate( interaction );
        InteractionPersister.getInstance().commit();
        Assert.assertEquals( 1, bait.getActiveInstances().size() );


        beginTransaction();
        // the persister might have synchronized the bait with an already existing protein in the database, do reload it
        final List<ProteinImpl> list = getDaoFactory().getInteractorDao( ProteinImpl.class ).getByXrefLike( "P12345" );
        Assert.assertNotNull( list );
        Assert.assertEquals( 1, list.size() );
        bait = list.iterator().next();
        String ac = bait.getAc();
        Assert.assertNotNull( ac );
        Assert.assertEquals( 1, bait.getActiveInstances().size() );

        // check on that protein
        beginTransaction();
        RangeChecker checker = new RangeChecker( getSanityCheckConfig() );
        final Collection<RangeReport> reports = checker.check( Arrays.asList( ac ) );
        Assert.assertNotNull( reports );
        Assert.assertEquals( 1, reports.size() );

        final RangeReport report = reports.iterator().next();
        Assert.assertEquals( RangeReportType.METHIONINE_REMOVED, report.getReportType() );
        Assert.assertTrue( report.hasRemappingSucceeded() );

        commitTransaction();
        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void leading_methionine_added() throws Exception {
        String seq = "QTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" + // 59
                     "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" + // 119
                     "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" + // 179
                     "ETQPKRKCCIF";                                                   // 19

        String seq2 = "M" + seq;

        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
        Experiment exp = mockBuilder.createExperimentEmpty( "kerrien-2007-1" );
        Protein bait = mockBuilder.createProtein( "P12345", "foo" );
        bait.setSequence( seq );
        Protein prey = mockBuilder.createProtein( "Q98765", "bar" );
        Interaction interaction = mockBuilder.createInteraction( "foo-bar", bait, prey, exp );
        Assert.assertEquals( 2, interaction.getComponents().size() );
        for ( Component component : interaction.getComponents() ) {
            if ( component.getInteractor() == bait ) {
                // add a feature
                Feature feature = mockBuilder.createFeatureRandom();
                feature.setCvFeatureType( mockBuilder.createCvObject( CvFeatureType.class, "MI:0117", "binding site" ) );
                Range range = mockBuilder.createRange( 4, 4, 20, 20 ); // KCVVVGDGAVGKTCLL
                range.setFromCvFuzzyType( null );
                range.setToCvFuzzyType( null );
                feature.addRange( range );
                range.prepareSequence( bait.getSequence() );
                Assert.assertEquals( 100, range.getSequence().length() );
                Assert.assertEquals( seq.substring( 4, 104 ), range.getSequence() );
                component.addBindingDomain( feature );
            }
        }

        bait.setSequence( seq2 );

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate( interaction );
        InteractionPersister.getInstance().commit();

        // the persister might have synchronized the bait with an already existing protein in the database, do reload it
        final List<ProteinImpl> list = getDaoFactory().getInteractorDao( ProteinImpl.class ).getByXrefLike( "P12345" );
        Assert.assertNotNull( list );
        Assert.assertEquals( 1, list.size() );
        bait = list.iterator().next();
        String ac = bait.getAc();
        Assert.assertNotNull( ac );

        // check on that protein
        beginTransaction();
        RangeChecker checker = new RangeChecker( getSanityCheckConfig() );
        final Collection<RangeReport> reports = checker.check( Arrays.asList( ac ) );
        Assert.assertNotNull( reports );
        Assert.assertEquals( 1, reports.size() );

        final RangeReport report = reports.iterator().next();
        System.out.println( "report = " + report );
        Assert.assertEquals( RangeReportType.METHIONINE_ADDED, report.getReportType() );
        Assert.assertTrue( report.hasRemappingSucceeded() );

        commitTransaction();

        IntactContext.getCurrentInstance().close();
    }

    @Test
    public void range_without_sequence() throws Exception {
        String seq = "QTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" + // 59
                     "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" + // 119
                     "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" + // 179
                     "ETQPKRKCCIF";                                                   // 190

        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
        Experiment exp = mockBuilder.createExperimentEmpty( "kerrien-2007-1" );
        Protein bait = mockBuilder.createProtein( "P12345", "foo" );
        bait.setSequence( seq );
        Protein prey = mockBuilder.createProtein( "Q98765", "bar" );
        Interaction interaction = mockBuilder.createInteraction( "foo-bar", bait, prey, exp );
        for ( Component component : interaction.getComponents() ) {
            if ( component.getInteractor() == bait ) {
                // add a feature
                Feature feature = mockBuilder.createFeatureRandom();
                feature.setCvFeatureType( mockBuilder.createCvObject( CvFeatureType.class, "MI:0117", "binding site" ) );
                Range range = mockBuilder.createRange( 4, 4, 20, 20 ); // KCVVVGDGAVGKTCLL
                range.setFromCvFuzzyType( null );
                range.setToCvFuzzyType( null );
                feature.addRange( range );

                // the range doesn't have an internal sequence
                range.setSequence( null );

                Assert.assertEquals( 100, range.getSequence().length() );
                Assert.assertEquals( seq.substring( 4, 104 ), range.getSequence() );
                component.addBindingDomain( feature );
            }
        }

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate( interaction );
        InteractionPersister.getInstance().commit();

        String ac = bait.getAc();
        Assert.assertNotNull( ac );

        // check on that protein
        beginTransaction();
        RangeChecker checker = new RangeChecker( getSanityCheckConfig() );
        final Collection<RangeReport> reports = checker.check( Arrays.asList( ac ) );
        Assert.assertNotNull( reports );
        Assert.assertEquals( 1, reports.size() );

        final RangeReport report = reports.iterator().next();
        Assert.assertEquals( RangeReportType.NO_RANGE_SEQUENCE, report.getReportType() );
        Assert.assertTrue( report.hasRemappingSucceeded() );

        commitTransaction();
    }

    @Test
    public void auto_realignement() throws Exception {
        String seq = "QTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" + // 59
                     "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" + // 119
                     "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" + // 179
                     "ETQPKRKCCIF";                                                   // 190

        String seq2 = seq.substring( 3 ); // remove the 3 first AA

        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
        Experiment exp = mockBuilder.createExperimentEmpty( "kerrien-2007-1" );
        Protein bait = mockBuilder.createProtein( "P12345", "foo" );
        bait.setSequence( seq );
        Protein prey = mockBuilder.createProtein( "Q98765", "bar" );
        Interaction interaction = mockBuilder.createInteraction( "foo-bar", bait, prey, exp );
        CvFuzzyType rangeFuzztType = getMockBuilder().createCvObject( CvFuzzyType.class, CvFuzzyType.RANGE_MI_REF, CvFuzzyType.RANGE );
        for ( Component component : interaction.getComponents() ) {
            if ( component.getInteractor() == bait ) {
                // add a feature
                Feature feature = mockBuilder.createFeatureRandom();
                feature.setCvFeatureType( mockBuilder.createCvObject( CvFeatureType.class, "MI:0117", "binding site" ) );
                Range range = mockBuilder.createRange( 4, 4, 20, 20 ); // KCVVVGDGAVGKTCLLI
                range.setFromCvFuzzyType( rangeFuzztType );
                range.setToCvFuzzyType( rangeFuzztType );
                feature.addRange( range );
                final String s = bait.getSequence();
                range.prepareSequence( s );
                Assert.assertTrue( range.getSequence(), range.getSequence().startsWith( "KCVVVGDGAVGKTCLLI" ) );
                Assert.assertEquals( 100, range.getSequence().length() );
                Assert.assertEquals( seq.substring( 4, 104 ), range.getSequence() );
                component.addBindingDomain( feature );
            }
        }

        bait.setSequence( seq2 );

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate( interaction );
        InteractionPersister.getInstance().commit();

        String ac = bait.getAc();
        Assert.assertNotNull( ac );

        // check on that protein
        beginTransaction();
        RangeChecker checker = new RangeChecker( getSanityCheckConfig() );
        final Collection<RangeReport> reports = checker.check( Arrays.asList( ac ) );
        Assert.assertNotNull( reports );
        Assert.assertEquals( 1, reports.size() );

        final RangeReport report = reports.iterator().next();
        Assert.assertEquals( RangeReportType.RANGE_REMAPPING, report.getReportType() );
        Assert.assertTrue( report.hasRemappingSucceeded() );

        commitTransaction();
    }

    public void polymer_without_sequence() {
        // TODO
    }

//    @Test
//    public void check_default() throws Exception {
//        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
//        Protein mockProt = mockBuilder.createProteinRandom();
//
//        beginTransaction();
//        InteractorPersister.getInstance().saveOrUpdate( mockProt );
//        InteractorPersister.getInstance().commit();
//        begin();
//
//        beginTransaction();
//
//        List<String> acs = new ArrayList<String>();
//        for ( Protein prot : getDaoFactory().getProteinDao().getAll() ) {
//            acs.add( prot.getAc() );
//        }
//
//        RangeChecker checker = new RangeChecker( getSanityCheckConfig() );
//        checker.check( acs );
//
//        commitTransaction();
//    }
//
//    @Test
//    public void check_checkRangeEntireDatabase() throws Exception {
//        RangeChecker rangeChecker = new RangeChecker();
//        rangeChecker.checkRangeEntireDatabase();
//    }
}
