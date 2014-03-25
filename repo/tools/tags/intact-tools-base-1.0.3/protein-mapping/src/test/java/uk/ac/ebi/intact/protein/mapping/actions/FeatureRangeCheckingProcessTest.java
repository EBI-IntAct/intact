package uk.ac.ebi.intact.protein.mapping.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.commons.util.Crc64;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.factories.impl.DefaultReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.FeatureRangeCheckingContext;
import uk.ac.ebi.intact.protein.mapping.model.contexts.UpdateContext;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultBlastResults;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-May-2010</pre>
 */
@ContextConfiguration(locations = {"classpath*:/META-INF/jpa.test.spring.xml"})
public class FeatureRangeCheckingProcessTest extends IntactBasicTestCase{

    private FeatureRangeCheckingProcess process;
    private UpdateContext context;
    private IntactContext intactContext;
    private String acToFind;

    private BioSource createBiosource(String shortLabel, String fullName, String taxId){
        BioSource bioSource = new BioSource();
        bioSource.setFullName(fullName);
        bioSource.setShortLabel(shortLabel);
        bioSource.setTaxId(taxId);

        return bioSource;
    }

    @Before
    public void createProcess(){
        this.process = new FeatureRangeCheckingProcess(new DefaultReportsFactory());
        this.context = new UpdateContext();

        this.intactContext = IntactContext.getCurrentInstance();

        String sequence = "MEWPCILLFLLSVTEGVHSQVQLLQSGPELVKPGASVKISCRASGYAFSKSWMNWVKRRP\n" +
                "GKGLEWIGRIFPGDGDTHYSGKFQGKAKLTADKSSVTAFLQLTSLTSEDSAVYFCARDSD\n" +
                "YGDYFDDWGQGATVTVSSAKTTPPSVYPLAPGCGDTTGSSVTLGCLVKGYFPESVTVTWN\n" +
                "SGSLSSSVHTFPALLQSGLYTMSSSVTVPSSTWPSQTVTCSVAHPASSTTVDKKLEPSGP\n" +
                "ISTINPCPPCKECHKCPAPNLEGGPSVFIFPPNIKDVLMISLTPKVTCVVVDVSEDDPDV\n" +
                "QISWFVNNVEVHTAQTQTHREDYNSTIRVVSALPIQHQDWMSGKEFKCKVNNKDLPSPIE\n" +
                "RTISKIKGLVRAPQVYILPPPAEQLSRKDVSLTCLVVGFNPGDISVEWTSNGHTEENYKD\n" +
                "TAPVLDSDGSYFIYSKLDIKTSKWEKTDSFSCNVRHEGLKNYYLKKTISRSPGK";

        Protein prot = getMockBuilder().createProtein("P12345", "test-protein");
        prot.getXrefs().clear();
        prot.setBioSource( createBiosource("human", "Homo Sapiens", "9606") );
        prot.setSequence(sequence);
        prot.setCrc64(Crc64.getCrc64(sequence));

        this.intactContext.getCorePersister().saveOrUpdate(prot);
        acToFind = prot.getAc();
        
        Interaction interaction = getMockBuilder().createInteraction(prot, getMockBuilder().createProtein("P11111", "test2-protein"));
        Component component = interaction.getComponents().iterator().next();
        component.setInteractor(prot);

        Feature feature = getMockBuilder().createFeatureRandom();
        feature.getRanges().clear();
        Range range = getMockBuilder().createRange(236, 236, 474, 474);
        range.prepareSequence(sequence);

        feature.getRanges().add(range);
        component.getBindingDomains().clear();
        component.getBindingDomains().add(feature);

        this.intactContext.getCorePersister().saveOrUpdate(interaction);
        this.intactContext.getCorePersister().saveOrUpdate(component);
        this.intactContext.getCorePersister().saveOrUpdate(feature);

        Assert.assertEquals(getDaoFactory().getComponentDao().getByInteractorAc(acToFind).size(), 1);
    }

    @Test
    public void test_Feature_Range_Conflict(){
        final DataContext dataContext = this.intactContext.getDataContext();
        TransactionStatus transactionStatus = dataContext.beginTransaction();

        String sequence = "MEWPCILLFLLSVTEGVHSQVQLLQSGPELVKPGASVKISCRASGYAFSKSWMNWVKRRP\n" +
                "GKGLEWIGRIFPGDGDTHYSGKFQGKAKLTADKSSVTAFLQLTSLTSEDSAVYFCARDSD\n" +
                "YGDYFDDWGQGATVTVSSAKTTPPSVYPLAPGCGDTTGSSVTLGCLVKGYFPESVTVTWN\n" +
                "SGSLSSSVHTFPALLQSGLYTMSSSVTVPSSTWPSQTVTCSVAHPASSTTVDKKLEPSGP\n" +
                "ISTINPCPPCKECHKCPAPNLEGGPSVFIFPPNIKDVLMISLTPKVTCVVVDVSEDDPDV\n" +
                "QISWFVNNVEVHTAQTQTHREDYNSTIRVVSALPIQHQDWMSGKEFKCKVNNKDLPSPIE\n" +
                "RTISKIKGLVRAPQVYILPPPAEQLSRKDVSLTCLVVGFNPGDISVEWTSNGHTEENYKD\n" +
                "TAPVLDSDGSYFIYSKLDIKTSKWEKTDSFSCNVRHEGLKNYYLKKTISRSPGK";
        this.context.setIntactAccession(acToFind);
        this.context.setOrganism(null);
        this.context.setSequence(sequence);
        String tremblAccession = "P01867";
        BlastProtein swissprot = new BlastProtein();
        swissprot.setAccession("P01867");
        swissprot.setDatabase("SP");
        swissprot.setIdentity(99);
        swissprot.setStartMatch(1);
        swissprot.setStartQuery(140);
        swissprot.setEndMatch(335);
        swissprot.setEndQuery(474);
        swissprot.setSequence("KTTPPSVYPLAPGCGDTTGSSVTLGCLVKGYFPESVTVTWNSGSLSSSVHTFPALLQSGL" +
                "YTMSSSVTVPSSTWPSQTVTCSVAHPASSTTVDKKLEPSGPISTINPCPPCKECHKCPAP" +
                "NLEGGPSVFIFPPNIKDVLMISLTPKVTCVVVDVSEDDPDVQISWFVNNVEVHTAQTQTH" +
                "REDYNSTIRVVSTLPIQHQDWMSGKEFKCKVNNKDLPSPIERTISKIKGLVRAPQVYILP" +
                "PPAEQLSRKDVSLTCLVVGFNPGDISVEWTSNGHTEENYKDTAPVLDSDGSYFIYSKLNM" +
                "KTSKWEKTDSFSCNVRHEGLKNYYLKKTISRSPGLDLDDICAEAKDGELDGLWTTITIFI" +
                "SLFLLSVCYSASVTLFKVKWIFSSVVELKQKISPDYRNMIGQGA");

        FeatureRangeCheckingContext featureContext = new FeatureRangeCheckingContext(this.context);
        featureContext.setTremblAccession(tremblAccession);
        featureContext.getResultsOfSwissprotRemapping().add(new DefaultBlastResults(swissprot));

        try {
            String id = this.process.runAction(featureContext);
            List<MappingReport> reports = this.process.getListOfActionReports();
            dataContext.commitTransaction(transactionStatus);

            for (MappingReport report : reports){
                System.out.println(report.getStatus().getLabel() + " " + report.getStatus().getDescription());
                for (String warn : report.getWarnings()){
                    System.out.println(warn);
                }
            }

            Assert.assertNotNull(id);
            Assert.assertEquals(tremblAccession, id);
        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}