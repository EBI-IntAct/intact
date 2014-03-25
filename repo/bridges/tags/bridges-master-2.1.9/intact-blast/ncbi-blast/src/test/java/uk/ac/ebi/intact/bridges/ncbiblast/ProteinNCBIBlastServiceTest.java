package uk.ac.ebi.intact.bridges.ncbiblast;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * ProteinNCBIBlastService Tester
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Mar-2010</pre>
 */

public class ProteinNCBIBlastServiceTest {

    ProteinNCBIBlastService blastService;

    public ProteinNCBIBlastServiceTest(){
        try {
            blastService = new ProteinNCBIBlastService("marine@ebi.ac.uk");
        } catch (BlastServiceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testResultsOfBlast_Uniprot(){
        ByteArrayInputStream results = blastService.getResultsOfBlastOnUniprot("MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR");

        Assert.assertNotNull(results);
    }

    @Test
    @Ignore
    public void testResultsOfBlast_Swissprot(){
        ByteArrayInputStream results = blastService.getResultsOfBlastOnSwissprot("MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR");

        Assert.assertNotNull(results);
    }

    @Test
    @Ignore
    public void testResultsOfBlast_Intact(){
        ByteArrayInputStream results = blastService.getResultsOfBlastOnIntact("MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR");

        Assert.assertNotNull(results);
    }

    @Test
    public void testResultsOfBlast_Uniprot_File(){
        File results = blastService.getResultsOfBlastOnUniprot("MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR", "test1.xml");

        Assert.assertNotNull(results);
    }

    @Test
    @Ignore
    public void testResultsOfBlast_Swissprot_File(){
        File results = blastService.getResultsOfBlastOnSwissprot("MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR", "test2.xml");

        Assert.assertNotNull(results);
    }

    @Test
    @Ignore
    public void testResultsOfBlast_Intact_File(){
        File results = blastService.getResultsOfBlastOnIntact("MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR", "test3.xml");

        Assert.assertNotNull(results);
    }
}
