package uk.ac.ebi.intact.bridges.blast.jdbc;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobDao;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

@org.junit.Ignore
public class BlastJobDaoTest {

	BlastJobDao	blastJobDao;

	@Before
	public void setUp() throws Exception {
		String tableName = "BlastJobDaoTest";
		File dbFolder = new File("testDbFolder");
		blastJobDao = new BlastJobDao(dbFolder, tableName);
		blastJobDao.deleteJobs();
	}

	@After
	public void tearDown() throws Exception {
		blastJobDao.close();
	}

	@Test
	public final void testDeleteJobs() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("testDeleteJobs", "P89012", BlastJobStatus.DONE,
				new File("test1"), Timestamp.valueOf("2007-09-13 10:30:25"));
		BlastJobEntity blastJob1 = new BlastJobEntity("testDeleteJobs1", "P78901", BlastJobStatus.PENDING, new File(
				"test1"), Timestamp.valueOf("2007-09-13 12:30:25"));
		BlastJobEntity blastJob2 = new BlastJobEntity("testDeleteJobs2", "P67890", BlastJobStatus.FAILED, new File(
				"test1"), Timestamp.valueOf("2007-09-13 11:30:25"));
		BlastJobEntity blastJob3 = new BlastJobEntity("testDeleteJobs3", "P56789", BlastJobStatus.NOT_FOUND, new File(
				"test1"), Timestamp.valueOf("2007-09-13 10:40:25"));
		blastJobDao.saveJobs(Arrays.asList(blastJob, blastJob1, blastJob2, blastJob3));
		List<BlastJobEntity> jobs = blastJobDao.selectAllJobs();
		assertNotNull(jobs); // should never be null
		assertEquals(4, jobs.size());
		blastJobDao.deleteJobs(Arrays.asList(blastJob, blastJob1, blastJob2, blastJob3));
		List<BlastJobEntity> empty = blastJobDao.selectAllJobs();
		assertNotNull(empty); // should never be null
		assertEquals(0, empty.size());
	}

	@Test
	public final void testDeleteJobByIdAndAc() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("testDeleteJobById", "P45678", BlastJobStatus.RUNNING, new File(
				"test1"), Timestamp.valueOf("2007-09-13 10:40:25"));
		blastJobDao.saveJob(blastJob);
		BlastJobEntity savedJob = blastJobDao.getJobById(blastJob.getJobid());
		assertNotNull(savedJob); // this can be null
		assertEquals(blastJob.getJobid(), savedJob.getJobid());
		blastJobDao.deleteJobById(blastJob.getJobid());
		BlastJobEntity job = blastJobDao.getJobById(blastJob.getJobid());
		assertNull(job); // this can be null
	}

	@Test
	public final void testSaveJob() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest1", "P34567", BlastJobStatus.DONE, new File(
				"test1"), Timestamp.valueOf("2007-09-13 10:40:25"));
		blastJobDao.saveJob(blastJob);
		BlastJobEntity job = blastJobDao.getJobById(blastJob.getJobid());
		assertNotNull(job); // this can be null
		assertionEquals(blastJob, job);
	}

	@Test
	public final void testSaveJobs() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest2", "P23456", BlastJobStatus.DONE, new File(
				"test2"), Timestamp.valueOf("2007-09-13 10:40:25"));
		BlastJobEntity blastJob2 = new BlastJobEntity("blastJobDaoTest2b", "P12345", BlastJobStatus.DONE, new File(
				"test2b"), Timestamp.valueOf("2007-10-21 10:40:25"));
		blastJobDao.saveJobs(Arrays.asList(blastJob, blastJob2));
		BlastJobEntity job1 = blastJobDao.getJobById(blastJob.getJobid());
		BlastJobEntity job2 = blastJobDao.getJobById(blastJob2.getJobid());
		assertNotNull(job1); // this can be null
		assertNotNull(job2); // this can be null

		assertionEquals(blastJob, job1);
		assertionEquals(blastJob2, job2);
	}

	@Test
	public final void testSelectAllJobs() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest3", "P67890", BlastJobStatus.DONE, new File(
				"test3"), Timestamp.valueOf("2007-09-13 10:40:25"));
		blastJobDao.saveJob(blastJob);
		List<BlastJobEntity> jobs = blastJobDao.selectAllJobs();
		assertNotNull(jobs);
		assertionEquals(blastJob, jobs.get(0));
	}

	@Test
	public final void testGetJobById() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest4", "P56789", BlastJobStatus.DONE, new File(
				"test4"), Timestamp.valueOf("2007-09-13 10:40:25"));
		blastJobDao.saveJob(blastJob);
		BlastJobEntity job = blastJobDao.getJobById(blastJob.getJobid());

		assertionEquals(blastJob, job);
	}

	@Test
	public final void testGetJobsByDate() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest2", "P23456", BlastJobStatus.DONE, new File(
				"test2"), Timestamp.valueOf("2007-09-13 10:40:25"));
		BlastJobEntity blastJob1 = new BlastJobEntity("blastJobDaoTest2a", "P34567", BlastJobStatus.DONE, new File(
				"test2a"), Timestamp.valueOf("2007-09-13 10:40:25"));
		BlastJobEntity blastJob2 = new BlastJobEntity("blastJobDaoTest2b", "P45678", BlastJobStatus.DONE, new File(
				"test2b"), Timestamp.valueOf("2007-10-21 10:40:25"));
		blastJobDao.saveJobs(Arrays.asList(blastJob, blastJob1, blastJob2));
		List<BlastJobEntity> jobs = blastJobDao.getJobsByTimestamp(Timestamp.valueOf("2007-9-13 10:40:25"));

		assertNotNull(jobs);
		assertEquals(2, jobs.size());

		assertionEquals(blastJob, jobs.get(0));
		assertionEquals(blastJob1, jobs.get(1));
	}

	@Test
	public final void testGetJobByAc() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest2", "P12345", BlastJobStatus.DONE, new File(
				"test2"), Timestamp.valueOf("2007-09-13 10:20:25"));
		BlastJobEntity blastJob1 = new BlastJobEntity("blastJobDaoTest2a", "P23456", BlastJobStatus.DONE, new File(
				"test2a"), Timestamp.valueOf("2007-09-20 10:20:25"));
		BlastJobEntity blastJob2 = new BlastJobEntity("blastJobDaoTest2b", "P34567", BlastJobStatus.DONE, new File(
				"test2b"), Timestamp.valueOf("2007-10-21 10:20:25"));
		blastJobDao.saveJobs(Arrays.asList(blastJob, blastJob1, blastJob2));
		UniprotAc ac = new UniprotAc(blastJob.getUniprotAc());
		BlastJobEntity job = blastJobDao.getJobByAc(ac);
		assertNotNull(job); // should never be null
		assertionEquals(blastJob, job);
	}

	@Test
	public final void testGetJobByAcAndLatestDate() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest2", "P45678", BlastJobStatus.DONE, new File(
				"test2"), Timestamp.valueOf("2007-09-13 10:20:25"));
		BlastJobEntity blastJob1 = new BlastJobEntity("blastJobDaoTest2a", "P56789", BlastJobStatus.DONE, new File(
				"test2a"), Timestamp.valueOf("2007-09-20 10:20:25"));
		BlastJobEntity blastJob2 = new BlastJobEntity("blastJobDaoTest2b", "P67890", BlastJobStatus.DONE, new File(
				"test2b"), Timestamp.valueOf("2007-10-21 10:20:25"));
		blastJobDao.saveJobs(Arrays.asList(blastJob, blastJob1, blastJob2));
		BlastJobEntity job = blastJobDao.getJobByAcAndLatestDate(blastJob.getUniprotAc());
		assertNotNull(job);

		assertionEquals(blastJob, job);
	}

	@Test
	public final void testUpdateJob() throws BlastJdbcException {
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTest2", "P12345", BlastJobStatus.RUNNING, new File(
				"test2"), Timestamp.valueOf("2007-09-13 10:20:25"));
		blastJobDao.saveJob(blastJob);
		blastJob.setStatus(BlastJobStatus.DONE);
		blastJob.setResult(new File("newFile"));
		blastJob.setTimestamp(Timestamp.valueOf("2007-12-31 10:20:25"));
		blastJobDao.updateJob(blastJob);
		BlastJobEntity jobEntity = blastJobDao.getJobById(blastJob.getJobid());
		assertNotNull(jobEntity);
		assertionEquals(blastJob, jobEntity);
	}

	@Test
	public final void testExportJob() throws BlastJdbcException {
		File csvFile = new File("testExportJob.csv");
		BlastJobEntity blastJob = new BlastJobEntity("blastJobDaoTestExport1", "P23456", BlastJobStatus.DONE, new File(
				"test2"), Timestamp.valueOf("2007-09-13 10:40:25"));
		BlastJobEntity blastJob2 = new BlastJobEntity("blastJobDaoExport2", "P12345", BlastJobStatus.DONE, new File(
				"test2b"), Timestamp.valueOf("2007-10-21 10:40:25"));
		blastJobDao.saveJobs(Arrays.asList(blastJob, blastJob2));
		blastJobDao.exportCSV(csvFile);
		System.out.println(csvFile.getAbsolutePath());

	}
	
	@Test
	public final void testImportJob() throws BlastJdbcException {
		File csvFile = new File("testExportJob.csv");
		blastJobDao.importCSV(csvFile);
		blastJobDao.exportCSV(new File("testExportInportJob.csv"));
	}
	
	private void assertionEquals(BlastJobEntity expected, BlastJobEntity observed) {
		assertEquals(expected.getJobid(), observed.getJobid());
		assertEquals(expected.getUniprotAc(), observed.getUniprotAc());
		assertEquals(expected.getStatus(), observed.getStatus());
		assertEquals(expected.getResultPath(), observed.getResultPath());
		assertEquals(expected.getTimestamp(), observed.getTimestamp());
	}
}
