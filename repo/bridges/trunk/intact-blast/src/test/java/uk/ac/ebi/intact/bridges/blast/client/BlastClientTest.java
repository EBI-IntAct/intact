/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.client;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.model.BlastInput;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.blast.model.Job;
import uk.ac.ebi.intact.bridges.blast.model.Sequence;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 7 Sep 2007
 * </pre>
 */
public class BlastClientTest {

	File	testDir;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testDir = getTargetDirectory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testBlastClient() throws BlastClientException {
		assertTrue(true);
		// BlastClient bc = new BlastClient("iarmean@ebi.ac.uk");
		// Job job = new Job("blast-20071009-14520134", new BlastInput(new
		// UniprotAc("P07149")));
		// bc.checkStatus(job);
		// while (!BlastJobStatus.DONE.equals(job.getStatus())) {
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// bc.checkStatus(job);
		// }
		// bc.getResult(job);
		// BlastOutput result = job.getBlastResult();
		// FileWriter fw;
		// try {
		// fw = new FileWriter(new File("E:/P07149.xml"));
		// fw.append(result.getResult());
		// fw.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	@Test
	@Ignore
	public final void testBlastSeq() throws BlastClientException {
		BlastClient bc = new BlastClient("iarmean@ebi.ac.uk");
		String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
		UniprotAc ac = new UniprotAc("Q9VQS6-1");
		Sequence seqObj = new Sequence(seq);
		BlastInput bi = new BlastInput(ac, seqObj);
		Job job = bc.blastSeq(bi);
		while (!BlastJobStatus.DONE.equals(job.getStatus())) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			bc.checkStatus(job);
		}
		bc.getResult(job);
		BlastOutput result = job.getBlastResult();
		FileWriter fw;
		try {
			fw = new FileWriter(new File(testDir, ac.getAcNr() + ".xml"));
			fw.append(result.getResult());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public final void testSubmitSplicevariant() throws BlastClientException {
		BlastClient bc = new BlastClient("iarmean@ebi.ac.uk");
		String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
		UniprotAc ac = new UniprotAc("Q9VQS6-1");
		BlastInput bI = new BlastInput(ac, new Sequence(seq));
		Job job = bc.blast(bI);
		while (!BlastJobStatus.DONE.equals(job.getStatus())) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			bc.checkStatus(job);
		}
		bc.getResult(job);
		BlastOutput result = job.getBlastResult();
		FileWriter fw;
		try {
			fw = new FileWriter(new File(testDir, ac.getAcNr() + ".xml"));
			fw.append(result.getResult());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public final void testSpecificContent() throws BlastClientException {
		String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
		UniprotAc ac = new UniprotAc("Q9VQS6");
		BlastInput bI = new BlastInput(ac, new Sequence(seq));
		BlastClient bc = new BlastClient("iarmean@ebi.ac.uk");
		String content = bc.getSpecificContent(bI);
		assertTrue(content.equals("uniprot:Q9VQS6"));
	}

	private File getTargetDirectory() {
		String outputDirPath = BlastClientTest.class.getResource("/").getFile();
		Assert.assertNotNull(outputDirPath);
		File outputDir = new File(outputDirPath);
		// we are in intact-blast/target/test-classes , move 1 up
		outputDir = outputDir.getParentFile();
		Assert.assertNotNull(outputDir);
		Assert.assertTrue(outputDir.getAbsolutePath(), outputDir.isDirectory());
		Assert.assertEquals("target", outputDir.getName());
		return outputDir;
	}
}
