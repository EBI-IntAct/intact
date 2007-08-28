/**
 * 
 */
package uk.ac.ebi.intact.bridges.blast;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author iarmean
 * 
 */
public class BlastClientTest {

	private BlastClient	bc;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bc = new BlastClient(0.001);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link uk.ac.ebi.intact.bridges.blast.BlastClient#blast(java.util.List, java.util.List)}.
	 */
	@Test
	public void testBlastOneUniprotSc() {
		List<String> proteinAc = Arrays.asList("Q9W486", "P43609", "Q03124");
		List<String> results = bc.blast(proteinAc, proteinAc);
		assertEquals(proteinAc.size(), results.size());
	}

	@Test
	public void testBlastMoreUniprotAcs() {
		BlastClient bc2 = new BlastClient(0.001);
		List<String> uniprotAc1 = Arrays.asList("P03973", "Q9W486", "P43609", "Q03124");
		List<String> uniprotAc2 = Arrays.asList("A4K2P4", "A4K2V7");
		List<String> results = bc2.blast(uniprotAc1, uniprotAc2);
		assertEquals(uniprotAc1.size(), results.size());
		try {
			FileWriter fw = new FileWriter(new File("~/tmp/aligns.txt"));
			bc2.printResults(results, fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
