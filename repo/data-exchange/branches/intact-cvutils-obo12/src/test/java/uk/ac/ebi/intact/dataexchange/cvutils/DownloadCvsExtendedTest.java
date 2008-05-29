package uk.ac.ebi.intact.dataexchange.cvutils;

import org.apache.log4j.Logger;
import org.bbop.dataadapter.DataAdapterException;
import org.obo.dataadapter.OBOFileAdapter;
import org.obo.dataadapter.OBOAdapter;
import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.*;
import static org.obo.datamodel.OBOProperty.BUILTIN_TYPES;
import org.obo.datamodel.impl.*;
import org.obo.history.HistoryList;
import org.obo.history.HistoryGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.ebi.intact.dataexchange.cvutils.model.DownloadCvsExtended;
import uk.ac.ebi.intact.dataexchange.cvutils.model.CvObjectOntologyBuilder;
import uk.ac.ebi.intact.dataexchange.cvutils.model.AnnotationInfoDataset;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvDagObject;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * Created by IntelliJ IDEA.
 * User: prem
 * Date: 23-May-2008
 * Time: 16:03:14
 * To change this template use File | Settings | File Templates.
 */
public class DownloadCvsExtendedTest extends IntactBasicTestCase {

    //initialize logger
    protected final static Logger log = Logger.getLogger(DownloadCvsExtendedTest.class);

    public final static String testFile = "/Development/intact/intact-cvutils-obo12/src/test/resources/psi-mi25.obo";

    private  List<CvDagObject> allCvs;
    @Before
    public void persistCvs() throws OBOParseException, IOException, PsiLoaderException {
        URL url = CvUpdaterTest.class.getResource("/psi-mi25.obo");
        log.info("url " + url);

        OBOSession oboSession = OboUtils.createOBOSession(url);
        CvObjectOntologyBuilder ontologyBuilder = new CvObjectOntologyBuilder(oboSession);
        AnnotationInfoDataset annotationDataset = OboUtils.createAnnotationInfoDatasetFromDefault(10841);

        CvUpdater updater = new CvUpdater();
        List<CvDagObject> allCvs_ = updater.getAllCvsAsList(ontologyBuilder);
        //CvUpdaterStatistics stats = updater.createOrUpdateCVs(allCvs, annotationDataset);

        //log.info("CreatedCvs->" + stats.getCreatedCvs().size());
        //log.info("UpdatedCvs->" + stats.getUpdatedCvs().size());
       // log.info("ObsoleteCvs->" + stats.getObsoleteCvs().size());
       // log.info("InvalidTerms->" + stats.getInvalidTerms().size());

        //int total = getDaoFactory().getCvObjectDao().countAll();
       // log.info("Total->" + total);
        this.allCvs=allCvs_;
       // allCvs = updater.getAllCvs();
        log.info("allCvs size "+allCvs.size());



    }//end method


    public void testRoundtrip() throws DataAdapterException, IOException {

        OBOFileAdapter adapter = new OBOFileAdapter();
        OBOFileAdapter.OBOAdapterConfiguration config = new OBOFileAdapter.OBOAdapterConfiguration();
        config.getReadPaths().add(testFile);
        OBOSession session = (OBOSession) adapter.doOperation(
                OBOAdapter.READ_ONTOLOGY, config, null);
        config = new OBOFileAdapter.OBOAdapterConfiguration();
        File outFile = File.createTempFile("test", ".obo",new File("C:/Development/intact/intact-cvutils-obo12/src/test/resources/temp"));
        config.setSerializer("OBO_1_2");
        config.setWritePath(outFile.getAbsolutePath());
        adapter.doOperation(OBOAdapter.WRITE_ONTOLOGY, config, session);

        // Read in the round-tripped file
        config = new OBOFileAdapter.OBOAdapterConfiguration();
        config.getReadPaths().add(outFile.getAbsolutePath());
        OBOSession session2 = (OBOSession) adapter.doOperation(
                OBOAdapter.READ_ONTOLOGY, config, null);

        // get the history generator version of the changes
        HistoryList allChanges = HistoryGenerator.getHistory(session, session2);
        log.info("allChanges size: "+allChanges.size());

        Assert.assertTrue("The file should be exactly the same going in as going out; in = " +
                testFile + ", out = " + outFile + ", allChanges = " + allChanges,
                allChanges.size() == 12);

    }//end method



    @Test
    public void testAllCvs() throws DataAdapterException, IOException{
        DownloadCvsExtended downloadCv=new DownloadCvsExtended();
        
        log.info("From Test all : "+allCvs.size());
        OBOSession oboSession = downloadCv.convertCvList2OBOSession(allCvs);
        File outFile = File.createTempFile("test", ".obo",new File("C:/Development/intact/intact-cvutils-obo12/src/test/resources/temp"));
        downloadCv.writeOBOFile(downloadCv.getOboSession(),outFile);

    }//end method




  // @Test
    public void testSimpleCv() throws DataAdapterException, IOException{

        DownloadCvsExtended downloadCv=new DownloadCvsExtended();
        OBOClass obj1 = new OBOClassImpl("molecular interaction", "MI:000");
        obj1.setDefinition("Controlled vocabularies originally created for protein protein interactions, extended to other molecules interactions [PMID:14755292]");

        Synonym syn = new SynonymImpl();
        syn.setText("mi");
        SynonymCategory synCat = new  SynonymCategoryImpl();
        synCat.setID("PSI-MI-short");

        syn.setSynonymCategory(synCat);
        syn.setScope(1);
        obj1.addSynonym(syn);

        CvObject cvObject=getMockBuilder().createCvObject(CvInteraction.class, "MI:0001", "interaction detect");
        cvObject.setFullName("interaction detection method");

        OBOClass obj2 =  downloadCv.convertCv2OBO(cvObject);

        Link linkToObj2 = new OBORestrictionImpl(obj2);
        //linkToObj2.setType(OBOProperty.IS_A);

       OBOProperty oboProp = new OBOPropertyImpl("part_of");
        linkToObj2.setType(oboProp);

        obj1.addChild(linkToObj2);

        downloadCv.getOboSession().addObject(obj1);
        downloadCv.getOboSession().addObject(obj2);

        File outFile = File.createTempFile("test", ".obo",new File("C:/Development/intact/intact-cvutils-obo12/src/test/resources/temp"));
        downloadCv.writeOBOFile(downloadCv.getOboSession(),outFile);



    }//end method



}//end class