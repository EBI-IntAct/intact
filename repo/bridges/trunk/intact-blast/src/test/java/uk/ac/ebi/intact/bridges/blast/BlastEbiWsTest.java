/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.BlastResult;
import uk.ac.ebi.intact.bridges.blast.EbiWsWUBlast;
import uk.ac.ebi.intact.bridges.blast.Hit;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 17 Sep 2007
 * </pre>
 */
public class BlastEbiWsTest {

	private AbstractBlastService			wsBlast;
	private File			testDir;

	// for test
	private BlastJobEntity	jobEntity;
	private String			jobId;
	private String			uniAc;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testDir = getTargetDirectory();
		testDir = new File(testDir, "BlastStrategyTest");
		testDir.mkdir();

		// String dirPath = "/ebi/sp/pro5/";
		// dirPath = "/net/nfs7/vol22/sp-pro5";
		// File testFile = new File(dirPath, "20071016_iarmean");
		// testFile.mkdir();

//		String dirPath = "E:/";
//		File workDir = new File(dirPath, "20071016_iarmean");
//		workDir.mkdir();
		String email = "iarmean@ebi.ac.uk";
		wsBlast = new EbiWsWUBlast(testDir, email);

		jobId = "blast-20070917-12102329";
		uniAc = "P17795";
		jobEntity = new BlastJobEntity(jobId);
		jobEntity.setUniprotAc(uniAc);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link AbstractBlastService#submitJob(java.lang.String)}.
	 * @throws BlastServiceException 
	 */
	@Test
	public final void testSubmitJob() throws BlastServiceException {
		BlastJobEntity jobEntity = wsBlast.submitJob(new UniprotAc("Q23451"));
		assertNotNull(jobEntity);
	}

	/**
	 * Test method for
	 * {@link AbstractBlastService#submitJobs(java.util.Set)}.
	 * @throws BlastServiceException 
	 */
	@Test
	public final void testSubmitJobs() throws BlastServiceException {
		Set<UniprotAc> uniprotAcs = new HashSet<UniprotAc>(2);
		uniprotAcs.add(new UniprotAc("Q12345"));
		uniprotAcs.add(new UniprotAc("P12345"));
		List<BlastJobEntity> jobs = wsBlast.submitJobs(uniprotAcs);
		assertNotNull(jobs);
		assertEquals(uniprotAcs.size(), jobs.size());
	}

	/**
	 * Test method for
	 * {@link AbstractBlastService#fetchAvailableBlasts(java.util.Set)}.
	 * @throws BlastServiceException 
	 */
	@Test
	public final void testFetchAvailableBlastsSetOfString() throws BlastServiceException {
		Set<UniprotAc> uniprotAcs = new HashSet<UniprotAc>(2);
		uniprotAcs.add(new UniprotAc("Q12345"));
		uniprotAcs.add(new UniprotAc("P12345"));
		List<BlastResult> results = wsBlast.fetchAvailableBlasts(uniprotAcs);
		for (BlastResult blastResult : results) {
			File f = new File(testDir, blastResult.getUniprotAc() + "_Str.txt");
			try {
				printResult(blastResult, new FileWriter(f));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test method for
	 * {@link AbstractBlastService#fetchAvailableBlasts(java.util.List)}.
	 * @throws BlastServiceException 
	 */
	@Test
	public final void testFetchAvailableBlastsListOfBlastJobEntity() throws BlastServiceException {
		Set<UniprotAc> uniprotAcs = new HashSet<UniprotAc>(2);
		uniprotAcs.add(new UniprotAc("Q12345"));
		uniprotAcs.add(new UniprotAc("P12345"));
		List<BlastJobEntity> jobs = wsBlast.fetchJobEntities(uniprotAcs);
		List<BlastResult> results = wsBlast.fetchAvailableBlasts(jobs);
		for (BlastResult blastResult : results) {
			File f = new File(testDir, blastResult.getUniprotAc() + "_Job.txt");
			try {
				printResult(blastResult, new FileWriter(f));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void printResult(BlastResult result, Writer writer) {
		try {
			writer.append(result.getUniprotAc() + " - alignmenthits \n");
			for (Hit hit : result.getHits()) {
				String align = hit.getUniprotAc() + ":" + hit.getEValue() + "\n";
				writer.append(align);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private File getTargetDirectory() {
		String outputDirPath = BlastEbiWsTest.class.getResource("/").getFile();
		Assert.assertNotNull(outputDirPath);
		File outputDir = new File(outputDirPath);
		// we are in src/main/resources , move 3 up

		// TODO: for eclipse use : 
		outputDir = outputDir.getParentFile().getParentFile().getParentFile();
		// TODO: for unix, cygwin use: outputDir = outputDir.getParentFile();

		// we are in confidence-score folder, move 1 down, in target folder
		String outputPath;
		// TODO: for eclipse use: 
		outputPath = outputDir.getPath() + "/target/";
		// TODO: for unix, cygwin use: outputPath = outputDir.getPath();

		outputDir = new File(outputPath);
		outputDir.mkdir();

		Assert.assertNotNull(outputDir);
		Assert.assertTrue(outputDir.getAbsolutePath(), outputDir.isDirectory());
		Assert.assertEquals("target", outputDir.getName());
		return outputDir;
	}
}
