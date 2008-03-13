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
import java.net.URL;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.EbiWsWUBlast;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastResult;
import uk.ac.ebi.intact.bridges.blast.model.Hit;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/**
 * Test class for  EbiWsWUBlast.
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

	private AbstractBlastService	wsBlast;
	private File					testDir;

    /**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testDir = getTargetDirectory();
		testDir = new File(testDir, "BlastEbiWsTest");
		testDir.mkdir();

		String email = "iarmean@ebi.ac.uk";
		String tableName = "jobTest";
		int nr = 20;
        File dbFolder = new File( getTargetDirectory(), "BlastDbTest" );
		dbFolder.mkdir();
		File blastWorkDir = testDir;
		blastWorkDir = blastWorkDir.getParentFile();
		wsBlast = new EbiWsWUBlast( dbFolder, tableName, blastWorkDir, email, nr);
        wsBlast.deleteJobsAll();
        wsBlast.importCsv(new File(BlastEbiWsTest.class.getResource("initDb.csv").getPath()));
    }

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
        if (wsBlast != null) {
            wsBlast.close();
        }
    }

	/**
	 * Test method for {@link AbstractBlastService#submitJob(uk.ac.ebi.intact.bridges.blast.model.UniprotAc)}.
	 * 
	 * @throws BlastServiceException
	 */
	@Test
	public final void testSubmitJob() throws BlastServiceException {
		BlastJobEntity jobEntity = wsBlast.submitJob(new UniprotAc("P40348"));

		assertNotNull(jobEntity);
		BlastResult result = wsBlast.fetchResult(jobEntity);
        int i =0;
        int nrTries = 2;
        while (result == null && i< nrTries
				&& !(jobEntity.getStatus().equals(BlastJobStatus.NOT_FOUND) || jobEntity.getStatus().equals(
						BlastJobStatus.FAILED))) {
			result = wsBlast.fetchResult(jobEntity);
            i++;
        }
        if (i == nrTries ){
            assertNull( result );
        } else {
            assertNotNull(result);
        }
    }

	/**
	 * Test method for {@link AbstractBlastService#submitJobs(java.util.Set)}.
	 * 
	 * @throws BlastServiceException
	 */
	@Test
	public final void testSubmitJobsBadUniprotAc() throws BlastServiceException {
		Set<UniprotAc> uniprotAcs = new HashSet<UniprotAc>(2);
		uniprotAcs.add(new UniprotAc("Q9V586"));
		uniprotAcs.add(new UniprotAc("Q9V902"));
		List<BlastJobEntity> jobs = wsBlast.submitJobs(uniprotAcs);

		assertNotNull(jobs);
		for (BlastJobEntity jobEntity : jobs) {
			assertNotNull(jobEntity);
			assertEquals(BlastJobStatus.FAILED, jobEntity.getStatus());
		}
	}

	/**
	 * Test method for {@link AbstractBlastService#submitJobs(java.util.Set)}.
	 * 
	 * @throws BlastServiceException
	 */
	@Test
	public final void testSubmitJobs() throws BlastServiceException {
		Set<UniprotAc> uniprotAcs = new HashSet<UniprotAc>(2);
		uniprotAcs.add(new UniprotAc("Q12345"));
		uniprotAcs.add(new UniprotAc("P12345"));
		uniprotAcs.add(new UniprotAc("R12345"));
		List<BlastJobEntity> jobs = wsBlast.submitJobs(uniprotAcs);
		assertNotNull(jobs);
		List<BlastResult> results = wsBlast.fetchAvailableBlasts(jobs);
        int i = 0;
        int nrTries =2;
        while (i < nrTries && results.size() != jobs.size()) {
			results = wsBlast.fetchAvailableBlasts(jobs);
            i++;
        }
        if (i == nrTries){
            assertEquals( 1, results.size() );
        } else {
            assertEquals(uniprotAcs.size(), jobs.size());
        }
    }

    @Test
    @Ignore
    public void testDelete() throws BlastServiceException {
        BlastJobEntity jobEntity1 = wsBlast.submitJob(new UniprotAc("Q9D1K4"));
        assertNotNull(jobEntity1);
        wsBlast.deleteJob(jobEntity1);
        BlastJobEntity jobEntity2 = wsBlast.submitJob( new UniprotAc("Q9D1K4"));
        assertNotNull(jobEntity2);
        assertNotSame(jobEntity1,  jobEntity2);
    }

    @Test
    public void testRefreshDb() throws Exception {
        wsBlast.refreshDb();
        File csvFile = new File(testDir, "exportAfterRefresh.csv");
        if (csvFile.exists()){
            csvFile.delete();
        }
        wsBlast.exportCsv( csvFile);
        assertTrue(csvFile.exists());
    }

    @Test
    public void testFetchAvailableBlast() throws BlastServiceException {
        BlastResult result  =  wsBlast.fetchAvailableBlast(new BlastJobEntity("blast-20071102-10414431"));
    }

    /**
	 * Test method for
	 * {@link AbstractBlastService#fetchAvailableBlasts(java.util.Set)}.
	 * 
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
				throw new BlastServiceException(e);
			}
		}
	}

	/**
	 * Test method for
	 * {@link AbstractBlastService#fetchAvailableBlasts(java.util.List)}.
	 * 
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
				throw new BlastServiceException(e);
			}
		}
	}


    @Test
    public void processResult() throws Exception {
        
    }


    // TODO: test resubmission of failed examples

	private void printResult(BlastResult result, Writer writer) throws BlastServiceException {
		try {
			writer.append(result.getUniprotAc() + " - alignmenthits \n");
			for (Hit hit : result.getHits()) {
				String align = hit.getUniprotAc() + ":" + hit.getEValue() + "\n";
				writer.append(align);
			}
			writer.close();
		} catch (IOException e) {
			throw new BlastServiceException(e);
		}
	}

	private File getTargetDirectory() {
		String outputDirPath = BlastEbiWsTest.class.getResource("/").getFile();
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
