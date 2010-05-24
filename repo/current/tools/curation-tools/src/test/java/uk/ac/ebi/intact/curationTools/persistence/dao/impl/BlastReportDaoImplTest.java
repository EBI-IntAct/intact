package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.model.unit.UpdateBasicTestCase;
import uk.ac.ebi.intact.curationTools.persistence.dao.BlastReportDao;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateResultsDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */

public class BlastReportDaoImplTest extends UpdateBasicTestCase{

    @Test
    public void search_all() throws Exception {
        final BlastReportDao blastReportDao = getDaoFactory().getBlastReportDao();
        Assert.assertEquals( 0, blastReportDao.countAll() );

        BlastReport report = getMockBuilder().createAutomaticBlastReport();

        blastReportDao.persist( report );
        blastReportDao.flush();
        Assert.assertEquals( 1, blastReportDao.countAll() );
    }

    @Test
    public void search_BlastReport_ByResultId_successful() throws Exception {
        final BlastReportDao blastReportDao = getDaoFactory().getBlastReportDao();
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, blastReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        BlastReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<BlastReport> r = blastReportDao.getBlastReportsByResultsId(id);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getUpdateResult() != null);
        Assert.assertTrue(r.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void search_BlastReport_ByResultId_Unsuccessful() throws Exception {
        final BlastReportDao blastReportDao = getDaoFactory().getBlastReportDao();
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, blastReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticBlastReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<BlastReport> r = blastReportDao.getBlastReportsByResultsId(1);

        Assert.assertTrue(r.isEmpty());
    }
}
