package uk.ac.ebi.intact.dataexchange.cvutils;

import org.junit.*;
import org.obo.datamodel.OBOSession;
import org.apache.commons.logging.Log;
import org.apache.axis.components.logger.LogFactory;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.dataexchange.cvutils.model.IntactOntology;
import uk.ac.ebi.intact.dataexchange.cvutils.model.AnnotationInfoDataset;
import uk.ac.ebi.intact.dataexchange.cvutils.model.CvObjectOntology;
import uk.ac.ebi.intact.dataexchange.cvutils.model.CvObjectOntologyBuilder;
import uk.ac.ebi.intact.model.CvInteractionType;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.context.IntactContext;

import java.net.URL;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */

public class CvUpdaterTest extends IntactBasicTestCase {

    public static final Log log= LogFactory.getLog("CvUpdaterTest.class");
    @Before
    public void clear() throws Exception {
        SchemaUtils.createSchema();
    }

    @Test
    public void createOrUpdateCVs() throws Exception {
        // IntactOntology ontology = OboUtils.createOntologyFromOboDefault(10841);
        URL url = CvUpdaterTest.class.getResource("/psi-mi25-next12.obo");
        //URL url = CvUpdaterTest.class.getResource("/psi-mi25-next12-alias.obo");

        //URL url = new URL(PSI_MI_OBO_LOCATION+"?revision="+"10841");
        log.info("url "+url);

        /*
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        int counter=0;
        while ((inputLine = in.readLine()) != null)
        {
          if(inputLine.startsWith("id: MI:"))    
          {   counter++;
              System.out.println(counter+ "  "+inputLine);
          }
        }
        System.out.println("Total MI "+counter);
        in.close();
        System.exit(0);
      */

        OBOSession oboSession = OboUtils.createOBOSession(url);
        CvObjectOntologyBuilder ontologyBuilder = new CvObjectOntologyBuilder(oboSession);

        AnnotationInfoDataset annotationDataset = OboUtils.createAnnotationInfoDatasetFromDefault(10841);

        CvUpdater updater = new CvUpdater();
        CvUpdaterStatistics stats = updater.createOrUpdateCVs(ontologyBuilder, annotationDataset);

        int total = getDaoFactory().getCvObjectDao().countAll();

        log.info("CreatedCvs->"+stats.getCreatedCvs().size());
        log.info("UpdatedCvs->"+stats.getUpdatedCvs().size());
        log.info("ObsoleteCvs->"+stats.getObsoleteCvs().size());
        log.info("InvalidTerms->"+stats.getInvalidTerms().size());


        log.info("Total->"+total);


        //  Assert.assertEquals(851, stats.getCreatedCvs().size());
        //  Assert.assertEquals(0, stats.getUpdatedCvs().size());
        //  Assert.assertEquals(50, stats.getObsoleteCvs().size());
        //  Assert.assertEquals(9, stats.getInvalidTerms().size());

        //   Assert.assertEquals(total, stats.getCreatedCvs().size());

        // update
        /*    CvUpdaterStatistics stats2 = updater.createOrUpdateCVs(ontologyBuilder,annotationDataset);

      Assert.assertEquals(total, getDaoFactory().getCvObjectDao().countAll());

      Assert.assertEquals(0, stats2.getCreatedCvs().size());
      Assert.assertEquals(0, stats2.getUpdatedCvs().size());
      Assert.assertEquals(50, stats2.getObsoleteCvs().size());
      Assert.assertEquals(9, stats2.getInvalidTerms().size()); */
    }

    /*
    @Test
    public void createOrUpdateCVs_existingTermToMarkAsObsolete() throws Exception {
        CvInteractionType aggregation = getMockBuilder().createCvObject(CvInteractionType.class, "MI:0191", "aggregation");
        CvTopic obsolete = getMockBuilder().createCvObject(CvTopic.class, CvTopic.OBSOLETE_MI_REF, CvTopic.OBSOLETE);
        PersisterHelper.saveOrUpdate(aggregation, obsolete);

        IntactOntology ontology = OboUtils.createOntologyFromOboDefault(10841);
        AnnotationInfoDataset annotationDataset = OboUtils.createAnnotationInfoDatasetFromDefault(10841);

        CvUpdater updater = new CvUpdater();
        CvUpdaterStatistics stats = updater.createOrUpdateCVs(ontology, annotationDataset);
        System.out.println(stats);

        int total = getDaoFactory().getCvObjectDao().countAll();

        Assert.assertEquals(851, total);

        Assert.assertEquals(847, stats.getCreatedCvs().size());
        Assert.assertEquals(1, stats.getUpdatedCvs().size());
        Assert.assertEquals(50, stats.getObsoleteCvs().size());
        Assert.assertEquals(9, stats.getInvalidTerms().size());

    }

    @Test
    @Ignore
    public void createOrUpdateCVs_includingNonMi() throws Exception {
        URL intactObo = CvUpdaterTest.class.getResource("/intact.20071203.obo");

        IntactOntology ontology = OboUtils.createOntologyFromObo(intactObo);

        CvUpdater updater = new CvUpdater();
        CvUpdaterStatistics stats = updater.createOrUpdateCVs(ontology);

        int total = getDaoFactory().getCvObjectDao().countAll();

        System.out.println(stats);

        // TODO adjust numbers when it works
        Assert.assertEquals(stats.getCreatedCvs().size(), 835);
        Assert.assertEquals(stats.getUpdatedCvs().size(), 0);
        Assert.assertEquals(stats.getObsoleteCvs().size(), 50);
        Assert.assertEquals(stats.getInvalidTerms().size(), 9);

        // update
        CvUpdaterStatistics stats2 = updater.createOrUpdateCVs(ontology);

        Assert.assertEquals(total, getDaoFactory().getCvObjectDao().countAll());

        Assert.assertEquals(stats2.getCreatedCvs().size(), 0);
        Assert.assertEquals(stats2.getUpdatedCvs().size(), 0);
        Assert.assertEquals(stats2.getObsoleteCvs().size(), 50);
        Assert.assertEquals(stats2.getInvalidTerms().size(), 9);
    }
*/

}
