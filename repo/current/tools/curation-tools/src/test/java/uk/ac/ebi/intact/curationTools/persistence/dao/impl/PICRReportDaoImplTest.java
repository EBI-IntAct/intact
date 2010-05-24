package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.model.unit.UpdateBasicTestCase;
import uk.ac.ebi.intact.curationTools.persistence.dao.PICRReportDao;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateResultsDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */

public class PICRReportDaoImplTest extends UpdateBasicTestCase{

    @Test
    public void search_all() throws Exception {
        final PICRReportDao picrReportDao = getDaoFactory().getPICRReportDao();
        Assert.assertEquals( 0, picrReportDao.countAll() );

        PICRReport report = getMockBuilder().createAutomaticPICRReport();

        picrReportDao.persist( report );
        picrReportDao.flush();
        Assert.assertEquals( 1, picrReportDao.countAll() );
    }

    @Test
    public void search_PICRReport_ByResultId_successful() throws Exception {
        final PICRReportDao picrReportDao = getDaoFactory().getPICRReportDao();
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, picrReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        PICRReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

        List<PICRReport> r = picrReportDao.getPICRReportsByResultsId(id);

        Assert.assertTrue(!r.isEmpty());
        Assert.assertTrue(r.get(0).getUpdateResult() != null);
        Assert.assertTrue(r.get(0).getUpdateResult().getId() == id);
    }

    @Test
    public void search_PICRReport_ByResultId_Unsuccessful() throws Exception {
         final PICRReportDao picrReportDao = getDaoFactory().getPICRReportDao();
        final UpdateResultsDao updateResultsDao = getDaoFactory().getUpdateResultsDao();
        Assert.assertEquals( 0, picrReportDao.countAll() );
        Assert.assertEquals( 0, updateResultsDao.countAll() );

        UpdateResults results = getMockBuilder().createAutomaticUpdateResult();
        ActionReport report = getMockBuilder().createAutomaticPICRReport();
        results.addActionReport(report);

        updateResultsDao.persist( results );
        updateResultsDao.flush();

        long id = results.getId();

       List<PICRReport> r = picrReportDao.getPICRReportsByResultsId(1);

       Assert.assertTrue(r.isEmpty());
    }
}
