package uk.ac.ebi.intact.bridges.blast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.blast.model.BlastProtein;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * BlastResultFilter tester
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Mar-2010</pre>
 */

public class BlastResultFilterTest {
    InputStream results;
    ProteinWsWuBlastService blastService;
    BlastResultFilter filter;

    @Before
    public void setUp() throws BlastServiceException {
        blastService = new ProteinWsWuBlastService("marine@ebi.ac.uk");
        results = blastService.getResultsOfBlastOnUniprot("MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR");

        filter = new BlastResultFilter(results);
    }

    @Test
    public void getResultWithoutFiltering(){

        filter.readResultsWithoutFiltering();

        ArrayList<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());
        Assert.assertEquals(100, filteredResults.size());
    }

    @Test
    public void getResult_FilterIdentity(){

        filter.filterResultsWithIdentity((float) 100);

        ArrayList<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());
        System.out.println(filteredResults.size());
        for (BlastProtein prot : filteredResults){
            System.out.println(prot.getAccession());
            System.out.println(prot.getIdentity());
        }
        Assert.assertEquals(12, filteredResults.size());
    }

    @Test
    public void getResult_FilterOrganism(){

        filter.filterResultsWithOrganism("Drosophila melanogaster");

        ArrayList<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());
        System.out.println(filteredResults.size());
        for (BlastProtein prot : filteredResults){
            System.out.println(prot.getAccession());
            System.out.println(prot.getIdentity());
        }
        Assert.assertEquals(5, filteredResults.size());
    }

    @Test
    public void getResult_FilterOrganismAndIdentity(){

        filter.filterResultsWithIdentityAndOrganism((float)100, "Drosophila melanogaster");

        ArrayList<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());
        System.out.println(filteredResults.size());
        for (BlastProtein prot : filteredResults){
            System.out.println(prot.getAccession());
            System.out.println(prot.getIdentity());
        }
        Assert.assertEquals(1, filteredResults.size());
    }

    @Test
    public void getResult_FilterOnMatchingEntries(){

        filter.readResultsWithoutFiltering();

        ArrayList<BlastProtein> filteredResultsOnIdentity = filter.filterMappingEntriesWithIdentity((float)100);

        Assert.assertEquals(false, filteredResultsOnIdentity.isEmpty());
        Assert.assertEquals(12, filteredResultsOnIdentity.size());

        ArrayList<BlastProtein> filteredResultsOnOrganism = filter.filterMappingEntriesWithOrganism("Drosophila melanogaster");

        Assert.assertEquals(false, filteredResultsOnOrganism.isEmpty());
        Assert.assertEquals(5, filteredResultsOnOrganism.size());

        ArrayList<BlastProtein> filteredResultsOnOrganismAndIdentity = filter.filterMappingEntriesWithIdentityAndOrganism((float)100, "Drosophila melanogaster");

        Assert.assertEquals(false, filteredResultsOnOrganismAndIdentity.isEmpty());
        Assert.assertEquals(1, filteredResultsOnOrganismAndIdentity.size());
    }
}
