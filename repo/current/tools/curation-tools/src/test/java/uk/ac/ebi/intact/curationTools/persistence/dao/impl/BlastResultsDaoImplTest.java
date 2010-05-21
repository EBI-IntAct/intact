package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.results.BlastResults;
import uk.ac.ebi.intact.curationTools.model.unit.UpdateBasicTestCase;
import uk.ac.ebi.intact.curationTools.persistence.dao.BlastReportDao;
import uk.ac.ebi.intact.curationTools.persistence.dao.BlastResultsDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */

public class BlastResultsDaoImplTest extends UpdateBasicTestCase{

    @Test
    public void search_all() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();
        Assert.assertEquals( 1, blastResultDao.countAll() );
    }

    @Test
    public void search_identity_superior_98_successful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        List<BlastResults> r = blastResultDao.getBlastResultsByIdentitySuperior((float)98);

        Assert.assertEquals(1, r.size());
        Assert.assertTrue(r.get(0).getIdentity() >= 98);
    }

    @Test
    public void search_identity_superior_98_Unsuccessful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createSwissprotRemappingResults("AXZ089", "P01234", 1, 198, 1, 198, (float) 96);

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        List<BlastResults> r = blastResultDao.getBlastResultsByIdentitySuperior((float)98);

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_identity_superior_98_and_actionId_successful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        final BlastReportDao blastReportDao = getDaoFactory().getBlastReportDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );
        Assert.assertEquals( 0, blastReportDao.countAll() );

        BlastReport r = getMockBuilder().createAutomaticSwissprotRemappingReport();
        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();
        r.addBlastMatchingProtein(blastResults);

        blastReportDao.persist( r );
        blastResultDao.flush();

        Long id = r.getId();

        List<BlastResults> results = blastResultDao.getBlastResultsByActionIdAndIdentitySuperior((float)98, id);

        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.get(0).getIdentity() >= 98);
        Assert.assertNotNull(results.get(0).getBlastReport());
        Assert.assertEquals(results.get(0).getBlastReport().getId(), id);
    }

    @Test
    public void search_identity_superior_98_and_actionId_Unsuccessful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        final BlastReportDao blastReportDao = getDaoFactory().getBlastReportDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );
        Assert.assertEquals( 0, blastReportDao.countAll() );

        BlastReport r = getMockBuilder().createAutomaticSwissprotRemappingReport();
        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();
        r.addBlastMatchingProtein(blastResults);

        blastReportDao.persist( r );
        blastResultDao.flush();

        Long id = r.getId();

        List<BlastResults> results = blastResultDao.getBlastResultsByActionIdAndIdentitySuperior((float)98, 1);

        Assert.assertEquals(0, results.size());
    }

    @Test
    public void search_all_swissprot_remapping_results() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        List<BlastResults> results = blastResultDao.getAllSwissprotRemappingResults();

        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.get(0).getTremblAccession()!= null);
    }

    @Test
    public void search_all_swissprot_remapping_results_Unsuccessful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticBlastResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        List<BlastResults> results = blastResultDao.getAllSwissprotRemappingResults();

        Assert.assertEquals(0, results.size());
    }

    @Test
    public void search_swissprotRemapping_resuts_and_actionId_successful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        final BlastReportDao blastReportDao = getDaoFactory().getBlastReportDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );
        Assert.assertEquals( 0, blastReportDao.countAll() );

        BlastReport r = getMockBuilder().createAutomaticSwissprotRemappingReport();
        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();
        r.addBlastMatchingProtein(blastResults);

        blastReportDao.persist( r );
        blastResultDao.flush();

        Long id = r.getId();

        List<BlastResults> results = blastResultDao.getAllSwissprotRemappingResultsFor(id);

        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.get(0).getTremblAccession() != null);
        Assert.assertNotNull(results.get(0).getBlastReport());
        Assert.assertEquals(results.get(0).getBlastReport().getId(), id);
    }

    @Test
    public void search_swissprotRemapping_resuts_and_actionId_Unsuccessful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        final BlastReportDao blastReportDao = getDaoFactory().getBlastReportDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );
        Assert.assertEquals( 0, blastReportDao.countAll() );

        BlastReport r = getMockBuilder().createAutomaticSwissprotRemappingReport();
        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();
        r.addBlastMatchingProtein(blastResults);

        blastReportDao.persist( r );
        blastResultDao.flush();

        Long id = r.getId();

        List<BlastResults> results = blastResultDao.getAllSwissprotRemappingResultsFor(1);

        Assert.assertEquals(0, results.size());
    }

    @Test
    public void search_swissprotRemapping_resuts_By_Trembl_ac_successful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        List<BlastResults> results = blastResultDao.getSwissprotRemappingResultsByTremblAc("Q8R3H6");

        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.get(0).getTremblAccession().equalsIgnoreCase("Q8R3H6"));
    }

    @Test
    public void search_swissprotRemapping_resuts_By_Trembl_ac_Unsuccessful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        List<BlastResults> results = blastResultDao.getSwissprotRemappingResultsByTremblAc("XXXXX");

        Assert.assertEquals(0, results.size());
    }

    @Test
    public void search_blast_resuts_By_Id_successful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        long id = blastResults.getId();

        BlastResults results = blastResultDao.getBlastResultById(id);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.getId() == id);
    }

    @Test
    public void search_blast_resuts_By_Id_Unsuccessful() throws Exception {
        final BlastResultsDao blastResultDao = getDaoFactory().getBlastResultsDao();
        Assert.assertEquals( 0, blastResultDao.countAll() );

        BlastResults blastResults = getMockBuilder().createAutomaticSwissprotRemappingResults();

        blastResultDao.persist( blastResults );
        blastResultDao.flush();

        BlastResults results = blastResultDao.getBlastResultById(1);

        Assert.assertNull(results);
    }
}
