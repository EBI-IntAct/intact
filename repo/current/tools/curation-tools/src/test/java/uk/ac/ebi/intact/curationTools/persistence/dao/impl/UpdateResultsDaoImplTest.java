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

    @Test
    public void test_GetResultsContainingActionWithSpecificName_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getResultsContainingAction(ActionName.BLAST_uniprot);

        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals(list.get(0).getActionsByName(ActionName.BLAST_uniprot).size(), 1);
    }

    @Test
    public void test_GetResultsContainingActionWithSpecificName_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getResultsContainingAction(ActionName.BLAST_Swissprot_Total_Identity);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetResultsContainingActionWithSpecificLabel_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getResultsContainingActionWithLabel(StatusLabel.TO_BE_REVIEWED);

        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void test_GetResultsContainingActionWithSpecificLabel_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getResultsContainingActionWithLabel(StatusLabel.COMPLETED);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetResultsContainingSwissprotRemapping_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        BlastReport remapping = getMockBuilder().createAutomaticSwissprotRemappingReport();

        results.addActionReport(report);
        results.addActionReport(remapping);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getUpdateResultsWithSwissprotRemapping();

        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void test_GetResultsContainingSwissprotRemapping_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getUpdateResultsWithSwissprotRemapping();

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetResults_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getSuccessfulUpdateResults();

        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void test_NoSuccessfulresults() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUnsuccessfulUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getSuccessfulUpdateResults();

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetResultsToBeReviewed_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport picr = getMockBuilder().createAutomaticPICRReport();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        BlastReport remapping = getMockBuilder().createAutomaticSwissprotRemappingReport();

        results.addActionReport(report);
        results.addActionReport(remapping);
        results.addActionReport(picr);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getUpdateResultsToBeReviewedByACurator();

        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void test_GetResultsToBeReviewed_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport picr = getMockBuilder().createAutomaticPICRReport();
        BlastReport remapping = getMockBuilder().createAutomaticSwissprotRemappingReport();

        results.addActionReport(remapping);
        results.addActionReport(picr);

        List<UpdateResults> list = updateResultDao.getUpdateResultsToBeReviewedByACurator();

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetResultsWithNoSequenceNoIdentityXRefs_successful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport picr = getMockBuilder().createAutomaticPICRReport();
        ActionReport report = getMockBuilder().createAutomaticUpdateReportWithNoSequenceNoIdentityXRef();
        BlastReport remapping = getMockBuilder().createAutomaticSwissprotRemappingReport();

        results.addActionReport(report);
        results.addActionReport(remapping);
        results.addActionReport(picr);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getProteinNotUpdatedBecauseNoSequenceAndNoIdentityXrefs();

        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void test_GetResultsWithNoSequenceNoIdentityXRefs_unsuccessful() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport picr = getMockBuilder().createAutomaticPICRReport();
        BlastReport remapping = getMockBuilder().createAutomaticSwissprotRemappingReport();

        results.addActionReport(remapping);
        results.addActionReport(picr);

        List<UpdateResults> list = updateResultDao.getProteinNotUpdatedBecauseNoSequenceAndNoIdentityXrefs();

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test_GetUnsuccessfulResults() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUnsuccessfulUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticReportWithStatusFailed();

        results.addActionReport(report);

        updateResultDao.persist( results );
        updateResultDao.flush();

        List<UpdateResults> list = updateResultDao.getUnsuccessfulUpdateResults();

        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void test_GetNoUnsuccessfulResults() throws Exception {
        final UpdateResultsDao updateResultDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, updateResultDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport picr = getMockBuilder().createAutomaticPICRReport();
        BlastReport remapping = getMockBuilder().createAutomaticSwissprotRemappingReport();

        results.addActionReport(remapping);
        results.addActionReport(picr);

        List<UpdateResults> list = updateResultDao.getUnsuccessfulUpdateResults();

        Assert.assertTrue(list.isEmpty());
    }
   
}
