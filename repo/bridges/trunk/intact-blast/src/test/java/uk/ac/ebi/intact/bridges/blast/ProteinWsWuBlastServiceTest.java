package uk.ac.ebi.intact.bridges.blast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * ProteinWsWuBlastService Tester
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Mar-2010</pre>
 */

public class ProteinWsWuBlastServiceTest {

    ProteinWsWuBlastService blastService;

    @Before
	public void setUp() throws BlastServiceException {
        blastService = new ProteinWsWuBlastService("marine@ebi.ac.uk");
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
