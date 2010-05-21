package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;
import uk.ac.ebi.intact.curationTools.model.unit.UpdateBasicTestCase;
import uk.ac.ebi.intact.curationTools.persistence.dao.PICRCrossReferencesDAO;
import uk.ac.ebi.intact.curationTools.persistence.dao.PICRReportDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */

public class PICRCrossReferenceDaoImplTest extends UpdateBasicTestCase{

    @Test
    public void search_all() throws Exception {
        final PICRCrossReferencesDAO picrCrossReferenceDoa = getDaoFactory().getPicrCrossReferencesDao();
        Assert.assertEquals( 0, picrCrossReferenceDoa.countAll() );

        PICRCrossReferences picrRefs = getMockBuilder().createAutomaticPICRCrossReferences();

        picrCrossReferenceDoa.persist( picrRefs );
        picrCrossReferenceDoa.flush();
        Assert.assertEquals( 1, picrCrossReferenceDoa.countAll() );
    }

    @Test
    public void search_PICRCrossReferences_ByDatabaseName_successful() throws Exception {
        final PICRCrossReferencesDAO picrCrossReferenceDoa = getDaoFactory().getPicrCrossReferencesDao();
        Assert.assertEquals( 0, picrCrossReferenceDoa.countAll() );

        PICRCrossReferences picrRefs = getMockBuilder().createAutomaticPICRCrossReferences();

        picrCrossReferenceDoa.persist( picrRefs );
        picrCrossReferenceDoa.flush();


        List<PICRCrossReferences> picrResults = picrCrossReferenceDoa.getCrossReferencesByDatabaseName("Ensembl");

        Assert.assertTrue(!picrResults.isEmpty());
        Assert.assertEquals("Ensembl", picrResults.iterator().next().getDatabase());
    }

    @Test
    public void search_PICRCrossReferences_ByDatabaseName_Unsuccessful() throws Exception {
        final PICRCrossReferencesDAO picrCrossReferenceDoa = getDaoFactory().getPicrCrossReferencesDao();
        Assert.assertEquals( 0, picrCrossReferenceDoa.countAll() );

        PICRCrossReferences picrRefs = getMockBuilder().createAutomaticPICRCrossReferences();

        picrCrossReferenceDoa.persist( picrRefs );
        picrCrossReferenceDoa.flush();


        List<PICRCrossReferences> picrResults = picrCrossReferenceDoa.getCrossReferencesByDatabaseName("Uniprot");

        Assert.assertTrue(picrResults.isEmpty());
    }

    @Test
    public void search_PICRCrossReferences_ByDatabaseName_and_ActionId_successful() throws Exception {
        final PICRCrossReferencesDAO picrCrossReferenceDoa = getDaoFactory().getPicrCrossReferencesDao();
        final PICRReportDao picrReportDao = getDaoFactory().getPICRReportDao();
        Assert.assertEquals( 0, picrCrossReferenceDoa.countAll() );
        Assert.assertEquals( 0, picrReportDao.countAll() );

        PICRReport report = getMockBuilder().createAutomaticPICRReport();
        PICRCrossReferences picrRefs = getMockBuilder().createAutomaticPICRCrossReferences();
        report.addPICRCrossReference(picrRefs);

        picrReportDao.persist( report );
        picrReportDao.flush();

        long id = report.getId();

        List<PICRCrossReferences> picrResults = picrCrossReferenceDoa.getCrossReferencesByDatabaseNameAndActionId("Ensembl", id);

        Assert.assertTrue(!picrResults.isEmpty());
        Assert.assertNotNull(picrResults.get(0).getPicrReport());
        Assert.assertTrue(picrResults.get(0).getPicrReport().getId() == id);
    }

    @Test
    public void search_PICRCrossReferences_ByDatabaseName_and_ActionId_Unsuccessful() throws Exception {
        final PICRCrossReferencesDAO picrCrossReferenceDoa = getDaoFactory().getPicrCrossReferencesDao();
        final PICRReportDao picrReportDao = getDaoFactory().getPICRReportDao();
        Assert.assertEquals( 0, picrCrossReferenceDoa.countAll() );
        Assert.assertEquals( 0, picrReportDao.countAll() );

        PICRReport report = getMockBuilder().createAutomaticPICRReport();
        PICRCrossReferences picrRefs = getMockBuilder().createAutomaticPICRCrossReferences();
        report.addPICRCrossReference(picrRefs);

        picrReportDao.persist( report );
        picrReportDao.flush();

        long id = report.getId();

        List<PICRCrossReferences> picrResults = picrCrossReferenceDoa.getCrossReferencesByDatabaseNameAndActionId("Ensembl", 1);

        Assert.assertTrue(picrResults.isEmpty());
    }

    @Test
    public void search_PICRCrossReferences_ById_successful() throws Exception {
        final PICRCrossReferencesDAO picrCrossReferenceDoa = getDaoFactory().getPicrCrossReferencesDao();
        Assert.assertEquals( 0, picrCrossReferenceDoa.countAll() );

        PICRCrossReferences picrRefs = getMockBuilder().createAutomaticPICRCrossReferences();

        picrCrossReferenceDoa.persist( picrRefs );
        picrCrossReferenceDoa.flush();

        long id = picrRefs.getId();

        PICRCrossReferences picrResults = picrCrossReferenceDoa.getCrossReferenceWithId(id);

        Assert.assertNotNull(picrResults);
        Assert.assertTrue(picrResults.getId() == id);
    }

    @Test
    public void search_PICRCrossReferences_ById_Unsuccessful() throws Exception {
        final PICRCrossReferencesDAO picrCrossReferenceDoa = getDaoFactory().getPicrCrossReferencesDao();
        Assert.assertEquals( 0, picrCrossReferenceDoa.countAll() );

        PICRCrossReferences picrRefs = getMockBuilder().createAutomaticPICRCrossReferences();

        picrCrossReferenceDoa.persist( picrRefs );
        picrCrossReferenceDoa.flush();

        long id = picrRefs.getId();

        PICRCrossReferences picrResults = picrCrossReferenceDoa.getCrossReferenceWithId(1);

        Assert.assertNull(picrResults);
    }

}
