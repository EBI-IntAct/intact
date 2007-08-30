/**
 * 
 */
package uk.ac.ebi.intact.bridges.blast;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * @author iarmean
 * 
 */
public class BlastClientTest {

	/**
	 * Sets up a logger for that class.
	 */
	public static final Log				log	= LogFactory.getLog(BlastClientTest.class);
	private String tmpDir = "E:\\tmp\\";
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
		HashSet<String> proteinAc = new HashSet<String>(Arrays.asList("Q9W486", "P43609", "Q03124"));
		long start = System.currentTimeMillis();
		List<String> results = bc.blast(proteinAc, proteinAc);
		long end = System.currentTimeMillis();
		assertEquals(proteinAc.size(), results.size());
		long time = end -start;
		log.info("one uniprotAc (min): "+ (time/60000));
	}

	@Test
	public void testBlast4UniprotAcs() {
		BlastClient bc2 = new BlastClient(0.001);
		HashSet<String> uniprotAc1 = new HashSet<String>(Arrays.asList("P03973", "Q9W486", "P43609", "Q03124"));
		HashSet<String> uniprotAc2 = new HashSet<String> (Arrays.asList("A4K2P4", "A4K2V7"));
		long start = System.currentTimeMillis();
		List <String> results = bc2.blast(uniprotAc1, uniprotAc2);
		long end = System.currentTimeMillis();
		assertEquals(uniprotAc1.size(), results.size());
		long time = end -start;
		log.info("4 uniprotAc (min): "+ (time/60000));
		try {
			FileWriter fw = new FileWriter(new File(tmpDir+"aligns4.txt"));
			bc2.printResults(results, fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBlast5UniprotAcs() {
		BlastClient bc2 = new BlastClient(0.001);
		HashSet<String> uniprotAc1 = new HashSet<String> (Arrays.asList("P03973", "Q9W486", "P43609", "Q03124", "A4K2P4"));
		HashSet<String> uniprotAc2 = new HashSet<String> (Arrays.asList("A4K2P4", "A4K2V7"));
		long start = System.currentTimeMillis();
		List<String> results = bc2.blast(uniprotAc1, uniprotAc2);
		long end = System.currentTimeMillis();
		assertEquals(uniprotAc1.size(), results.size());
		long time = end -start;
		log.info("5 uniprotAc (min): "+ (time/60000));
		try {
			FileWriter fw = new FileWriter(new File(tmpDir+"aligns5.txt"));
			bc2.printResults(results, fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
