package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.model.unit.UpdateBasicTestCase;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateResultsDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */

public class UpdateResultsDaoImplTest extends UpdateBasicTestCase{

    @Test
    public void search_all() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();
        Assert.assertEquals( 1, updateResultDao.countAll() );
    }

    @Test
    public void test_GetUpdateResultsWithId_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();

        long id = results.getId();

        UpdateResults r = updateResultDao.getUpdateResultsWithId(id);

        Assert.assertNotNull(r);
        Assert.assertTrue(r.getId() == id);
    }

    @Test
    public void test_GetUpdateResultsWithId_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();

        UpdateResults r = updateResultDao.getUpdateResultsWithId(1);

        Assert.assertNull(r);
    }

    @Test
    public void test_GetUpdateResultsForProteinAc_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();

        long id = results.getId();

        UpdateResults r = updateResultDao.getUpdateResultsForProteinAc("EBI-0001001");

        Assert.assertNotNull(r);
        Assert.assertTrue(r.getId() == id);
    }

    @Test
    public void test_GetUpdateResultsForProteinAc_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();

        UpdateResults r = updateResultDao.getUpdateResultsForProteinAc("ebi_268");

        Assert.assertNull(r);
    }

    @Test
    public void test_GetActionReportByNameAndProteinAc_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsByNameAndProteinAc(ActionName.PICR_accession, "EBI-0001001");

        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals(ActionName.PICR_accession, list.get(0).getName());
    }

    @Test
    public void test_GetUActionReportsByNameAndProteinAc_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsByNameAndProteinAc(ActionName.PICR_sequence_Swissprot, "EBI-0001001");

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetActionReportByNameAndResultId_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        long id = results.getId();

        List<ActionReport> list = updateResultDao.getActionReportsByNameAndResultId(ActionName.PICR_accession, id);

        Assert.assertTrue(!list.isEmpty());
        Assert.assertNotNull(list.get(0).getUpdateResult());
        Assert.assertTrue(list.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void test_GetUActionReportsByNameAndResultId_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();


        List<ActionReport> list = updateResultDao.getActionReportsByNameAndResultId(ActionName.PICR_accession, 1);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetActionReportByStatusAndProteinAc_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsByStatusAndProteinAc(StatusLabel.COMPLETED, "EBI-0001001");

        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals(StatusLabel.COMPLETED.toString(), list.get(0).getStatusLabel());
    }

    @Test
    public void test_GetUActionReportsByStatusAndProteinAc_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsByStatusAndProteinAc(StatusLabel.TO_BE_REVIEWED, "EBI-0001001");

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetActionReportByStatusAndResultId_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        long id = results.getId();

        List<ActionReport> list = updateResultDao.getActionReportsByStatusAndResultId(StatusLabel.COMPLETED, id);

        Assert.assertTrue(!list.isEmpty());
        Assert.assertNotNull(list.get(0).getUpdateResult());
        Assert.assertTrue(list.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void test_GetUActionReportsByStatusAndResultId_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();

        updateResultDao.persist( results );
        updateResultDao.flush();


        List<ActionReport> list = updateResultDao.getActionReportsByStatusAndResultId(StatusLabel.COMPLETED, 1);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetBlastReportByProteinAc_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithBlastResultsByProteinAc("EBI-0001001");

        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals("EBI-0001001", list.get(0).getUpdateResult().getIntactAccession());
    }

    @Test
    public void test_GetBlastReportByProteinAc_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithBlastResultsByProteinAc("EBI-11212");

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetSwissprotRemappingReportByProteinAc_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticSwissprotRemappingReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithSwissprotRemappingResultsByProteinAc("EBI-0001001");

        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals("EBI-0001001", list.get(0).getUpdateResult().getIntactAccession());
    }

    @Test
    public void test_GetSwissprotRemappingReportByProteinAc_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticSwissprotRemappingReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithSwissprotRemappingResultsByProteinAc("EBI-01234");

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetPICRReportByProteinAc_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithPICRCrossReferencesByProteinAc("EBI-0001001");

        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals("EBI-0001001", list.get(0).getUpdateResult().getIntactAccession());
    }

    @Test
    public void test_GetPICRReportByProteinAc_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);
        
        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithPICRCrossReferencesByProteinAc("EBI-01234");

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetBlastReportByResultId_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        long id = results.getId();

        List<ActionReport> list = updateResultDao.getActionReportsWithBlastResultsByResultsId(id);

        Assert.assertTrue(!list.isEmpty());
        Assert.assertTrue(list.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void test_GetBlastReportByByResultId_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithBlastResultsByResultsId(1);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetSwissprotRemappingReportByResultId_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticSwissprotRemappingReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        long id = results.getId();

        List<ActionReport> list = updateResultDao.getActionReportsWithSwissprotRemappingResultsByResultsId(id);

        Assert.assertTrue(!list.isEmpty());
        Assert.assertTrue(list.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void test_GetSwissprotRemappingReportByResultId_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticSwissprotRemappingReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithSwissprotRemappingResultsByResultsId(1);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetPICRReportByResultId_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        long id = results.getId();

        List<ActionReport> list = updateResultDao.getActionReportsWithPICRCrossReferencesByResultsId(id);

        Assert.assertTrue(!list.isEmpty());
        Assert.assertTrue(list.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void test_GetPICRReportByResultId_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithPICRCrossReferencesByResultsId(1);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetReportsWithWarningsByProteinAc_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithWarningsByProteinAc("EBI-0001001");

        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals(list.get(0).getUpdateResult().getIntactAccession(), "EBI-0001001");
    }

    @Test
    public void test_GetReportsWithWarningsByProteinAc_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithoutWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<ActionReport> list = updateResultDao.getActionReportsWithWarningsByProteinAc("EBI-0001001");

        Assert.assertTrue(list.isEmpty());
    }
}
