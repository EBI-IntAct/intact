package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.model.unit.UpdateBasicTestCase;
import uk.ac.ebi.intact.curationTools.persistence.dao.ActionReportDao;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateResultsDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */

public class ActionReportDaoImplTest extends UpdateBasicTestCase{

    @Test
    public void search_all() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();
        Assert.assertEquals( 1, actionReportDao.countAll() );
    }

    @Test
    public void search_ActionReportWithId_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        long id = report.getId();

        ActionReport r = actionReportDao.getActionReportWithId(id);

        Assert.assertNotNull(r);
        Assert.assertTrue(r.getId() == id);
    }

    @Test
    public void search_ActionReportWithId_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        ActionReport r = actionReportDao.getActionReportWithId(1);

        Assert.assertNull(r);
    }

    @Test
    public void search_ActionReportByName_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getActionReportsByName(ActionName.BLAST_uniprot);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getName().equals(ActionName.BLAST_uniprot));
    }

    @Test
    public void search_ActionReportByName_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getActionReportsByName(ActionName.SEARCH_uniprot_name);

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_ActionReportByStatus_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getActionReportsByStatus(StatusLabel.TO_BE_REVIEWED);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getStatusLabel().equalsIgnoreCase(StatusLabel.TO_BE_REVIEWED.toString()));
    }

    @Test
    public void search_ActionReportByStatus_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getActionReportsByStatus(StatusLabel.COMPLETED);

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_ActionReportWithWarnings_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getAllActionReportsWithWarnings();

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(!r.get(0).getWarnings().isEmpty());
    }

    @Test
    public void search_ActionReportWithWartnings_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithoutWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getAllActionReportsWithWarnings();

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_ActionReportWithPossibleUniprot_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getAllActionReportsWithSeveralPossibleUniprot();

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(!r.get(0).getPossibleAccessions().isEmpty());
    }

    @Test
    public void search_ActionReportWithPossibleUniprot_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithoutPossibleUniprot();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<ActionReport> r = actionReportDao.getAllActionReportsWithSeveralPossibleUniprot();

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_PICRReport_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticPICRReport();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<PICRReport> r = actionReportDao.getAllPICRReports();

        Assert.assertTrue(!r.isEmpty());
    }

    @Test
    public void search_PICRReport_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<PICRReport> r = actionReportDao.getAllPICRReports();

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_BlastReport_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticSwissprotRemappingReport();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<BlastReport> r = actionReportDao.getAllBlastReports();

        Assert.assertTrue(!r.isEmpty());
    }

    @Test
    public void search_BlastReport_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<BlastReport> r = actionReportDao.getAllBlastReports();

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_SwissprotRemappingReport_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticSwissprotRemappingReport();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<BlastReport> r = actionReportDao.getAllSwissprotRemappingReports();

        Assert.assertTrue(!r.isEmpty());
    }

    @Test
    public void search_SwissprotRemappingReport_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        Assert.assertEquals( 0, actionReportDao.countAll() );

        ActionReport report = getMockBuilder().createAutomaticBlastReport();

        actionReportDao.persist( report );
        actionReportDao.flush();

        List<BlastReport> r = actionReportDao.getAllSwissprotRemappingReports();

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_BlastReport_ByResultId_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<BlastReport> r = actionReportDao.getBlastReportsByResultsId(id);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getUpdateResult() != null);
        Assert.assertTrue(r.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void search_BlastReport_ByResultId_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<BlastReport> r = actionReportDao.getBlastReportsByResultsId(1);

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_PICRReport_ByResultId_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<PICRReport> r = actionReportDao.getPICRReportsByResultsId(id);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getUpdateResult() != null);
        Assert.assertTrue(r.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void search_PICRReport_ByResultId_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<PICRReport> r = actionReportDao.getPICRReportsByResultsId(1);

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_ActionReport_WithWarnings_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<ActionReport> r = actionReportDao.getActionReportsWithWarningsByResultsId(id);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getUpdateResult() != null);
        Assert.assertTrue(r.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void search_ActionReport_WithWarnings_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<ActionReport> r = actionReportDao.getActionReportsWithWarningsByResultsId(1);

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_ActionReport_WithPossibleUniprot_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<ActionReport> r = actionReportDao.getActionReportsWithSeveralPossibleUniprotByResultId(id);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getUpdateResult() != null);
        Assert.assertTrue(r.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void search_ActionReport_WithPossibleUniprot_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<ActionReport> r = actionReportDao.getActionReportsWithSeveralPossibleUniprotByResultId(1);

        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void search_ActionReport_ByResultId_successful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<ActionReport> r = actionReportDao.getAllActionReportsByResultsId(id);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getUpdateResult() != null);
        Assert.assertTrue(r.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void search_ActionReport_ByResultId_Unsuccessful() throws Exception {
        final ActionReportDao<ActionReport> actionReportDao = getDaoFactory().getActionReportDao(ActionReport.class);
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, actionReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticActionReportWithWarning();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<ActionReport> r = actionReportDao.getAllActionReportsByResultsId(1);

        Assert.assertTrue(r.isEmpty());
    }
}
