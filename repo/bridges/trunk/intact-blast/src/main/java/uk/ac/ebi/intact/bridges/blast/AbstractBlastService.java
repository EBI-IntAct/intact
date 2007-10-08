/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ebi.intact.bridges.blast.client.BlastClientException;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJdbcException;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobDao;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.blast.model.BlastInput;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastResult;
import uk.ac.ebi.intact.bridges.blast.model.Hit;
import uk.ac.ebi.intact.bridges.blast.model.Job;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;
import uk.ac.ebi.intact.bridges.blast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.blast.BlastService;

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 12 Sep 2007
 * </pre>
 */
public abstract class AbstractBlastService implements BlastService {
	/**
	 * Sets up a logger for that class.
	 */
	public static final Log	log	= LogFactory.getLog(AbstractBlastService.class);

	private BlastJobDao		blastJobDao;
	private File			workDir;

	private int				nrSubmission;											// nr
																					// of
																					// proteins
																					// to
																					// be
																					// blasted
																					// at a
																					// time

	// protected boolean isXmlFormat;

	// ///////////////
	// Constructor
	AbstractBlastService(File dbFolder, String tableName, int nrPerSubmission) throws BlastServiceException {
		try {
			blastJobDao = new BlastJobDao(dbFolder, tableName);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
		nrSubmission = nrPerSubmission;
	}

	// ///////////////
	// Getters/setters
	public void setWorkDir(File workDir) {
		if (!workDir.exists()) {
			throw new IllegalArgumentException("WorkDir must exist! " + workDir.getPath());
		}
		if (!workDir.isDirectory()) {
			throw new IllegalArgumentException("WorkDir must be a directory! " + workDir.getPath());
		}
		if (!workDir.canWrite()) {
			throw new IllegalArgumentException("WorkDir must be writable! " + workDir.getPath());
		}
		this.workDir = workDir;
	}

	public File getWorkDir() {
		return workDir;
	}

	// ///////////////
	// inherited

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#exportCsv(java.io.File)
	 */
	public void exportCsv(File csvFile) throws BlastServiceException {
		try {
			blastJobDao.exportCSV(csvFile);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#importCsv(java.io.File)
	 */
	public void importCsv(File csvFile) throws BlastServiceException {
		try {
			blastJobDao.importCSV(csvFile);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.confidence.blast.BlastStrategy#submitJob(java.lang.String)
	 */
	public BlastJobEntity submitJob(UniprotAc uniprotAc) throws BlastServiceException {
		if (uniprotAc == null) {
			throw new IllegalArgumentException("UniprotAc must not be null!");
		}
		if (uniprotAc.getAcNr() == null) {
			throw new IllegalArgumentException("UniprotAc.getAcNr() must not be null!");
		}
		try {
			BlastJobEntity jobEntity = blastJobDao.getJobByAc(uniprotAc);
			if (jobEntity != null) {
				if (BlastJobStatus.RUNNING.equals(jobEntity.getStatus())
						|| BlastJobStatus.PENDING.equals(jobEntity.getStatus())) {
					refreshJob(jobEntity);
				}
				// FIXME: look into the uniprot, intact update policy and
				// caching of
				// the results, ask sam what this was about
				return jobEntity;
			} else {
				Job job = null;
				try {
					job = runBlast(uniprotAc);
				} catch (BlastClientException e) {
					throw new BlastServiceException(e);
				}
				return saveSubmittedJob(job);
			}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.confidence.blast.BlastStrategy#submitJobs(java.util.Set)
	 */
	/**
	 * 
	 * @param set
	 *            of uniprotAcs
	 * @throws BlastClientException
	 * @result list of BlastJobEntities , never null
	 */
	public List<BlastJobEntity> submitJobs(Set<UniprotAc> uniprotAcs) throws BlastServiceException {
		// TODO: 20.09.2007 test this method (only 20)
		if (uniprotAcs == null) {
			throw new IllegalArgumentException("UniprotAcs must not be null!");
		}
		if (uniprotAcs.size() == 0) {
			throw new IllegalArgumentException("UniprotAcs mut not be empty!");
		}
		try {
			List<BlastJobEntity> jobEntities = blastJobDao.getJobsByAc(uniprotAcs);

			if (jobEntities.size() == 0) {
				List<Job> jobs = runBlast(uniprotAcs);
				return saveSubmittedJobs(jobs);
			} else {
				Set<UniprotAc> prots = notIncluded(jobEntities, uniprotAcs);
				if (prots.size() != 0) {
					while (prots.size() > nrSubmission) {
						Set<UniprotAc> toGet = onlyN(prots, nrSubmission);
						// TODO: test it, although it should work
						List<Job> jobs = runBlast(toGet);
						jobEntities.addAll(saveSubmittedJobs(jobs));
					}
					if (prots.size() != 0 && prots.size() <= 20) {
						List<Job> jobs = runBlast(prots);
						jobEntities.addAll(saveSubmittedJobs(jobs));
					}
				}
				return jobEntities;
			}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		} catch (BlastClientException e) {
			throw new BlastServiceException(e);
		}
	}

	/**
	 * Retrieves the available results from the DB
	 * 
	 * @param set
	 *            of uniprotAcs(string)
	 * @return list of BlastResults
	 * @throws BlastServiceException
	 */
	public List<BlastResult> fetchAvailableBlasts(Set<UniprotAc> uniprotAcs) throws BlastServiceException {
		if (uniprotAcs == null) {
			throw new IllegalArgumentException("UniprotAcs must not be null!");
		}
		if (uniprotAcs.size() == 0) {
			throw new IllegalArgumentException("UniprotAcs mut not be empty!");
		}

		List<BlastResult> results = new ArrayList<BlastResult>(uniprotAcs.size());
		refreshDb();
		for (UniprotAc ac : uniprotAcs) {
			BlastResult result = resultAvailableInDB(ac);
			if (result != null) {
				results.add(result);
			}
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.confidence.blast.BlastStrategy#fetchAvailableBlasts(java.util.List)
	 */
	/**
	 * Retrieves the available results from the DB
	 * 
	 * @param list
	 *            of blastJobEntities
	 * @return list of BlastResults
	 * @throws BlastServiceException
	 */
	public List<BlastResult> fetchAvailableBlasts(List<BlastJobEntity> jobs) throws BlastServiceException {
		if (jobs == null) {
			throw new IllegalArgumentException("Jobs list must not be null!");
		}
		if (jobs.size() == 0) {
			throw new IllegalArgumentException("Jobs list mut not be empty!");
		}

		List<BlastResult> results = new ArrayList<BlastResult>(jobs.size());
		refreshJobs(jobs);
		try {
			for (BlastJobEntity jobEntity : jobs) {
				BlastJobEntity job = blastJobDao.getJobById(jobEntity.getJobid());
				if (job != null) {
					if (BlastJobStatus.DONE.equals(job.getStatus())) {
						BlastResult res = fetchResult(job);
						results.add(res);
					} else if (BlastJobStatus.FAILED.equals(jobEntity.getStatus())
							|| BlastJobStatus.NOT_FOUND.equals(job.getStatus())) {
						BlastResult res = new BlastResult(job.getUniprotAc(), new ArrayList<Hit>());
						results.add(res);
					}
				} else {
					if (job == null) {
						log.info("JobEntity not in DB: " + jobEntity);
					}
				}
			}
			return results;
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/**
	 * Retrieves the BlastResult from the jobEntity, without refreshing the job
	 * from DB / Blast.
	 * 
	 * @param BlastJobEntity
	 * @return the BlastResult
	 * @throws BlastServiceException
	 */
	public BlastResult fetchResult(BlastJobEntity job) throws BlastServiceException {
		if (job == null) {
			throw new NullPointerException("JobEntity most be not null!");
		}
		if (!BlastJobStatus.DONE.equals(job.getStatus()) || job.getResultPath() == null) {
			refreshJob(job);
			if (!BlastJobStatus.DONE.equals(job.getStatus())) {
				return null;
			}
		}

		File resultDbFile = job.getResult();

		// TODO: is this the final solution?
		File blastFile = new File(this.workDir, job.getUniprotAc() + ".xml");
		try {
			return processOutput(blastFile);
		} catch (BlastClientException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.confidence.blast.BlastStrategy#fetchPendingJobs()
	 */
	public List<BlastJobEntity> fetchPendingJobs() throws BlastServiceException {
		refreshDb();
		try {
			return blastJobDao.getJobsByStatus(BlastJobStatus.PENDING);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#fetchFailedJobs()
	 */
	public List<BlastJobEntity> fetchFailedJobs() throws BlastServiceException {
		refreshFailed();
		try {
			return blastJobDao.getJobsByStatus(BlastJobStatus.FAILED);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#fetchNotFoundJobs()
	 */
	public List<BlastJobEntity> fetchNotFoundJobs() throws BlastServiceException {
		refreshDb();
		try {
			return blastJobDao.getJobsByStatus(BlastJobStatus.NOT_FOUND);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#fetchRunningJobs()
	 */
	public List<BlastJobEntity> fetchRunningJobs() throws BlastServiceException {
		refreshDb();
		try {
			return blastJobDao.getJobsByStatus(BlastJobStatus.RUNNING);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/**
	 * For the specified list of proteins it retrieves the jobEntities.
	 * 
	 * @param a
	 *            set of uniprotAc
	 * @return a list of BlastJobEntities of not null members
	 */
	public List<BlastJobEntity> fetchJobEntities(Set<UniprotAc> uniprotAcs) throws BlastServiceException {
		if (uniprotAcs == null) {
			throw new IllegalArgumentException("UniprotAcs list must not be null!");
		}
		if (uniprotAcs.size() == 0) {
			throw new IllegalArgumentException("UniprotAcs list mut not be empty!");
		}
		try {
			return blastJobDao.getJobsByAc(uniprotAcs);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	public void deleteJob(BlastJobEntity job) throws BlastServiceException {
		try {
			blastJobDao.deleteJobById(job.getJobid());
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	public void deleteJobs(List<BlastJobEntity> jobs) throws BlastServiceException {
		for (BlastJobEntity job : jobs) {
			deleteJob(job);
		}
	}

	// TODO: remove after test
	public void close() throws BlastServiceException {
		try {
			blastJobDao.close();
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	// //////////////////////
	// private Methods
	private static Set<UniprotAc> onlyN(Set<UniprotAc> prots, int nr) {
		Set<UniprotAc> protsSmall = new HashSet<UniprotAc>(nr);
		for (int i = 0; i < nr; i++) {
			protsSmall.add((UniprotAc) prots.toArray()[i]);
		}
		prots.removeAll(protsSmall);
		return protsSmall;
	}

	private void refreshJob(BlastJobEntity job) throws BlastServiceException {
		if (job == null) {
			throw new NullPointerException("JobEntity most be not null!");
		}
		if (!BlastJobStatus.DONE.equals(job.getStatus())) {

			Job clientJob = getClientJob(job);
			BlastJobStatus status;
			try {
				status = checkStatus(clientJob);
				if (BlastJobStatus.DONE.equals(status)) {
					BlastOutput result = getResult(clientJob);
					saveResult(job, result);
				}
				if (BlastJobStatus.NOT_FOUND.equals(status) || BlastJobStatus.FAILED.equals(status)) {
					log.info("Job not found or failed! " + job.getJobid() + ":" + job.getUniprotAc());
				}
			} catch (BlastClientException e) {
				throw new BlastServiceException(e + job.toString());
			}
		}
	}

	private void refreshJobs(List<BlastJobEntity> jobs) throws BlastServiceException {
		for (BlastJobEntity jobEntity : jobs) {
			if (jobEntity.getStatus().equals(BlastJobStatus.RUNNING)
					|| (jobEntity.getStatus().equals(BlastJobStatus.PENDING))) {
				refreshJob(jobEntity);
			}
		}
	}

	private void refreshDb() throws BlastServiceException {
		try {
			Set<BlastJobEntity> jobs = new HashSet<BlastJobEntity>();
			jobs.addAll(blastJobDao.getJobsByStatus(BlastJobStatus.PENDING));
			jobs.addAll(blastJobDao.getJobsByStatus(BlastJobStatus.RUNNING));
			for (BlastJobEntity jobEntity : jobs) {
				// in case the set did not work, i prevent the refreshing of a
				// refreshed job
				// TODO: do i really need this if?\
				if (jobEntity.getStatus().equals(BlastJobStatus.RUNNING)
						|| (jobEntity.getStatus().equals(BlastJobStatus.PENDING))) {
					refreshJob(jobEntity);
				}
			}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	private void refreshFailed() throws BlastServiceException {
		try {
			Set<BlastJobEntity> jobs = new HashSet<BlastJobEntity>();
			jobs.addAll(blastJobDao.getJobsByStatus(BlastJobStatus.FAILED));
			for (BlastJobEntity jobEntity : jobs) {
				// in case the set did not work, i prevent the refreshing of a
				// refreshed job
				refreshJob(jobEntity);
			}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	private Job getClientJob(BlastJobEntity job) {
		if (job == null) {
			throw new NullPointerException("JobEntity most be not null!");
		}
		UniprotAc uniAc = new UniprotAc(job.getUniprotAc());
		BlastInput blastInput = new BlastInput(uniAc);
		return new Job(job.getJobid(), blastInput);

	}

	/**
	 * Saves the blast job with the current date in the DB
	 * 
	 * @param job
	 *            a webservice or blast job
	 */
	private BlastJobEntity saveSubmittedJob(Job job) throws BlastServiceException {
		Timestamp timestamp = getCurrentDate();
		BlastJobEntity jobEntity = new BlastJobEntity(job.getId(), job.getBlastInput().getUniprotAc().getAcNr(), job
				.getStatus(), null, timestamp);
		try {
			blastJobDao.saveJob(jobEntity);
			return jobEntity;
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/**
	 * Saves the blast job with the current date in the DB
	 * 
	 * @param job
	 *            a webservice or blast job
	 * @throws BlastServiceException
	 */
	private List<BlastJobEntity> saveSubmittedJobs(List<Job> jobs) throws BlastServiceException {
		List<BlastJobEntity> submitted = new ArrayList<BlastJobEntity>(jobs.size());
		for (Job job : jobs) {
			submitted.add(saveSubmittedJob(job));
		}
		return submitted;
	}

	private void saveResult(BlastJobEntity job, BlastOutput result) throws BlastServiceException {
		File resultFile = writeResultsToWorkDir(job, result);
		job.setResult(resultFile);
		job.setStatus(BlastJobStatus.DONE);
		try {
			blastJobDao.updateJob(job);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	// //////////////////////
	// private Methods
	private BlastResult resultAvailableInDB(UniprotAc uniprotAc) throws BlastServiceException {
		BlastJobEntity job;
		try {
			job = blastJobDao.getJobByAc(uniprotAc);
			if (job != null && BlastJobStatus.DONE.equals(job.getStatus()) && job.getResultPath() != null) {
				BlastResult result = fetchResult(job);
				return result;
			}
			return null;
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	private static Timestamp getCurrentDate() {
		Date today = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		sdf.format(today);
		Timestamp timestamp = new Timestamp(today.getTime());

		return timestamp;
	}

	private Set<UniprotAc> notIncluded(List<BlastJobEntity> results, Set<UniprotAc> proteins) {
		Set<UniprotAc> protNotIn = new HashSet<UniprotAc>(proteins.size());

		Set<UniprotAc> resultProt = new HashSet<UniprotAc>(results.size());
		for (BlastJobEntity jobEntity : results) {
			resultProt.add(new UniprotAc(jobEntity.getUniprotAc()));
		}

		for (UniprotAc ac : proteins) {
			if (!resultProt.contains(ac)) {
				protNotIn.add(ac);
			}
		}
		return protNotIn;
	}

	private File writeResultsToWorkDir(BlastJobEntity job, BlastOutput result) throws BlastServiceException {
		String fileName = job.getUniprotAc() + (result.isXmlFormat() ? ".xml" : ".txt");
		File tmpFile = new File(workDir.getPath(), fileName);
		try {
			writeResults(result.getResult(), new FileWriter(tmpFile));
		} catch (IOException e) {
			throw new BlastServiceException(e);
		}
		return tmpFile;
	}

	private void writeResults(String result, Writer writer) throws BlastServiceException {
		try {
			writer.append(result);
			writer.close();
		} catch (IOException e) {
			throw new BlastServiceException(e);
		}
	}

	// ////////////////
	// abstract Methods
	protected abstract List<Job> runBlast(Set<UniprotAc> uniprotAcs) throws BlastClientException;

	protected abstract Job runBlast(UniprotAc uniprotAc) throws BlastClientException;

	protected abstract BlastJobStatus checkStatus(Job job) throws BlastClientException;

	protected abstract BlastOutput getResult(Job job) throws BlastClientException;

	protected abstract BlastResult processOutput(File blastFile) throws BlastClientException;

	// /////////////
	// any use?
	// private BlastJobEntity getJobById(String jobid) {
	// return blastJobDao.getJobById(jobid);
	// }

	// private Set<String> checkArchiveForBlastOutputs(Set<String> uniprotAcs,
	// boolean isBlastOutXml) {
	// Set<String> notIn = new HashSet<String>();
	// for (String ac : uniprotAcs) {
	// if (!wasCached(ac, isBlastOutXml)) {
	// notIn.add(ac);
	// }
	// }
	// return notIn;
	// }

	// private boolean wasCached(String ac, boolean isBlastOutXml) {
	// String fileName = ac + (isBlastOutXml ? ".xml" : ".txt");
	// File blastFile = new File(getWorkDir().getPath(), fileName);
	// if (blastFile.exists()) {
	// return true;
	// }
	// return false;
	// }

	// /**
	// * Retrieves the latest job with the specified uniprotAc
	// *
	// * @param uniprotAc
	// * @return a blast job entity, can be null
	// */
	// private BlastJobEntity fetchLatest(String uniprotAc) {
	// return blastJobDao.getJobByAcAndLatestDate(uniprotAc);
	// }
}