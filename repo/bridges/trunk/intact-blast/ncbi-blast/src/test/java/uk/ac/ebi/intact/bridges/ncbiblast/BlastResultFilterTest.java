package uk.ac.ebi.intact.bridges.ncbiblast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;

import java.io.InputStream;
import java.util.List;

/**
 * BlastResultFilter tester
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Mar-2010</pre>
 */

public class BlastResultFilterTest {
    InputStream results;
    ProteinNCBIBlastService blastService;
    BlastResultFilter filter;
    private final String sequence = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";

    @Before
    public void setUp() throws BlastServiceException {
        blastService = new ProteinNCBIBlastService("marine@ebi.ac.uk");
        results = blastService.getResultsOfBlastOnUniprot(sequence);

        filter = new BlastResultFilter(results);
    }

    @Test
    public void getResultWithoutFiltering(){

        filter.readResultsWithoutFiltering();

        List<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());
        Assert.assertEquals(50, filteredResults.size());
    }

    @Test
    public void getResult_FilterIdentity(){

        filter.filterResultsWithIdentity((float) 100);

        List<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());

        for (BlastProtein prot : filteredResults){
            System.out.println(prot.getAccession());
            System.out.println(prot.getIdentity());
        }
        Assert.assertEquals(12, filteredResults.size());
    }

    @Test
    public void getResult_FilterOrganism(){

        filter.filterResultsWithOrganism("7227");

        List<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());

        for (BlastProtein prot : filteredResults){
            System.out.println(prot.getAccession());
            System.out.println(prot.getIdentity());
        }
        Assert.assertEquals(4, filteredResults.size());
    }

    @Test
    public void getResult_FilterOrganismAndIdentity(){

        filter.filterResultsWithIdentityAndOrganism((float)100, "7227");

        List<BlastProtein> filteredResults = filter.getMatchingEntries();

        Assert.assertEquals(false, filteredResults.isEmpty());

        for (BlastProtein prot : filteredResults){
            System.out.println(prot.getAccession());
            System.out.println(prot.getIdentity());
        }
        Assert.assertEquals(1, filteredResults.size());
    }

    @Test
    public void getResult_FilterOnMatchingEntries(){

        filter.readResultsWithoutFiltering();

        List<BlastProtein> filteredResultsOnIdentity = filter.filterMappingEntriesWithIdentity((float)100);

        Assert.assertEquals(false, filteredResultsOnIdentity.isEmpty());
        Assert.assertEquals(12, filteredResultsOnIdentity.size());

        List<BlastProtein> filteredResultsOnOrganism = filter.filterMappingEntriesWithOrganism("7227");

        Assert.assertEquals(false, filteredResultsOnOrganism.isEmpty());
        Assert.assertEquals(4, filteredResultsOnOrganism.size());

        List<BlastProtein> filteredResultsOnOrganismAndIdentity = filter.filterMappingEntriesWithIdentityAndOrganism((float)100, "7227");

        Assert.assertEquals(false, filteredResultsOnOrganismAndIdentity.isEmpty());
        Assert.assertEquals(1, filteredResultsOnOrganismAndIdentity.size()); 

        List<BlastProtein> filteredResultsWithTotalAlignment = BlastResultFilter.collectMappingEntriesWithTotalAlignment(filteredResultsOnIdentity, this.sequence.length());
        Assert.assertEquals(12, filteredResultsWithTotalAlignment.size());
    }
}
