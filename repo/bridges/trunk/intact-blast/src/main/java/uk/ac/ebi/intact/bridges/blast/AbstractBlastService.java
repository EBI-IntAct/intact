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
	// nr of proteins to be blasted at a time
	private int				nrSubmission;

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
	/**
	 * return BlastJobEntity, can be null
	 */
	public BlastJobEntity submitJob(UniprotAc uniprotAc) throws BlastServiceException {
		if (uniprotAc == null) {
			throw new IllegalArgumentException("UniprotAc must not be null!");
		}
		if (uniprotAc.getAcNr() == null) {
			throw new IllegalArgumentException("UniprotAc.getAcNr() must not be null!");
		}
		return submitJob(new BlastInput(uniprotAc));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#submitJob(uk.ac.ebi.intact.bridges.blast.model.BlastInput)
	 */
	public BlastJobEntity submitJob(BlastInput blastInput) throws BlastServiceException {
		if (blastInput == null || blastInput.getUniprotAc() == null) {
			throw new IllegalArgumentException("BlastInput(Uniprot, {Seq}) must not be null!");
		}
		if (blastInput.getUniprotAc().getAcNr() == null) {
			throw new IllegalArgumentException("UniprotAc.getAcNr() must not be null!");
		}
		if (blastInput.getSequence() == null) {
			if (log.isDebugEnabled()) {
				log.debug("BlastInput(" + blastInput.getUniprotAc() + ") has no Sequence!");
			}
		}
		try {
			BlastJobEntity jobEntity = blastJobDao.getJobByAc(blastInput.getUniprotAc());
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
					while (!okToSubmit()) {
						Thread.sleep(5000);
					}
					job = runBlast(blastInput);
					if (log.isInfoEnabled()){
						int runnningJobs = blastJobDao.countJobs(BlastJobStatus.RUNNING);
						log.info("after submit: #runningJobs: "+ runnningJobs);
					}
				} catch (BlastClientException e) {
					throw new BlastServiceException(e);
				} catch (InterruptedException e) {
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
	// TODO: return an iterator for larger sets of results
	public List<BlastJobEntity> submitJobs(Set<UniprotAc> uniprotAcs) throws BlastServiceException {
		if (uniprotAcs == null) {
			throw new IllegalArgumentException("UniprotAcs must not be null!");
		}
		if (uniprotAcs.size() == 0) {
			throw new IllegalArgumentException("UniprotAcs mut not be empty!");
		}
		try {
			List<BlastJobEntity> jobEntities = blastJobDao.getJobsByAc(uniprotAcs);

			if (jobEntities.size() == 0) {
				return runBlastOnlyNrATime(uniprotAcs, nrSubmission);
			} else {
				Set<UniprotAc> prots = notIncluded(jobEntities, uniprotAcs);
				jobEntities.addAll(runBlastOnlyNrATime(prots, nrSubmission));
				return jobEntities;
			}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#fetchAvailableBlast(uk.ac.ebi.intact.bridges.blast.model.UniprotAc)
	 */
	/**
	 * @return BlastResult, can be null, if the uniprotAc is not in the DB
	 */
	public BlastResult fetchAvailableBlast(UniprotAc uniprotAc) throws BlastServiceException {
		if (uniprotAc == null) {
			throw new IllegalArgumentException("UniprotAc must not be null!");
		}
		BlastJobEntity job;
		try {
			job = blastJobDao.getJobByAc(uniprotAc);
			if (job != null) {
				if (log.isInfoEnabled()) {
					log.info("found: " + job);
				}
				return fetchAvailableBlast(job);
			} else {
				return null;
			}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.bridges.blast.BlastService#fetchAvailableBlast(uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobEntity)
	 */
	public BlastResult fetchAvailableBlast(BlastJobEntity job) throws BlastServiceException {
		if (job == null) {
			throw new IllegalArgumentException("Job must not be null!");
		}
		refreshJob(job);
		try {
			BlastJobEntity jobEntity = blastJobDao.getJobById(job.getJobid());
			if (jobEntity != null) {
				if (BlastJobStatus.DONE.equals(job.getStatus())) {
					BlastResult res = fetchResult(job);
					return res;
				} else if (BlastJobStatus.FAILED.equals(jobEntity.getStatus())
						|| BlastJobStatus.NOT_FOUND.equals(jobEntity.getStatus())) {
					BlastResult res = new BlastResult(job.getUniprotAc(), new ArrayList<Hit>(0));
					return res;
				}
			} else {
				if (job == null) {
					if (log.isInfoEnabled()) {
						log.info("JobEntity not in DB: " + jobEntity);
					}
				}
			}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
		return null;
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
			try {
				BlastJobEntity job = blastJobDao.getJobByAc(ac);
				BlastResult result = resultFromDB(job);
				if (result != null) {
					results.add(result);
				}
			} catch (BlastJdbcException e) {
				throw new BlastServiceException(e);
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
					} else if (BlastJobStatus.FAILED.equals(job.getStatus())
							|| BlastJobStatus.NOT_FOUND.equals(job.getStatus())) {
						BlastResult res = new BlastResult(job.getUniprotAc(), new ArrayList<Hit>(0));
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

		// File resultDbFile = job.getResult();
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
		refreshFailedNotFound();
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

	public void deleteJobsAll() throws BlastServiceException {
		try {
			blastJobDao.deleteJobs();
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
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
	private boolean okToSubmit() throws BlastServiceException {
		try {
			refreshDb();
			int runningJobs = blastJobDao.countJobs(BlastJobStatus.RUNNING);
			if (log.isInfoEnabled()){
				log.info("#running jobs: "+ runningJobs);
			}
			return (runningJobs > (nrSubmission -1) ? false : true);
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	private List<BlastJobEntity> runBlastOnlyNrATime(Set<UniprotAc> uniprotAcs, int allowedNrRunningJobs)
			throws BlastServiceException {
		List<BlastJobEntity> jobEntities = new ArrayList<BlastJobEntity>(uniprotAcs.size());
		try {
			while (uniprotAcs.size() != 0) {
				// List<BlastJobEntity> runningJobs = fetchRunningJobs();

				int runningJobs = blastJobDao.countJobs(BlastJobStatus.RUNNING);

				while (runningJobs > allowedNrRunningJobs) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						throw new BlastServiceException(e);
					}
					refreshDb();
					runningJobs = blastJobDao.countJobs(BlastJobStatus.RUNNING);
				}
				int alreadyRunning = runningJobs;
				int nrToSubmit = allowedNrRunningJobs - alreadyRunning;
				if (nrToSubmit > 0) {
					Set<UniprotAc> toSubmit = onlyN(uniprotAcs, nrToSubmit);
					try {
						List<Job> jobs = runBlast(toSubmit);
						jobEntities.addAll(saveSubmittedJobs(jobs));
					} catch (BlastClientException e) {
						throw new BlastServiceException(e);
					}
				}
			}
		} catch (BlastJdbcException e1) {
			throw new BlastServiceException(e1);
		}
		return jobEntities;
	}

	private static Set<UniprotAc> onlyN(Set<UniprotAc> prots, int nr) {
		int size = (prots.size() < nr ? prots.size() : nr);
		Set<UniprotAc> protsSmall = new HashSet<UniprotAc>(size);
		for (int i = 0; i < size; i++) {
			protsSmall.add((UniprotAc) prots.toArray()[i]);
		}
		prots.removeAll(protsSmall);
		return protsSmall;
	}

	private void refreshJob(BlastJobEntity job) throws BlastServiceException {
		if (job == null) {
			throw new NullPointerException("JobEntity most be not null!");
		}
		if (log.isDebugEnabled()) {
			log.debug("refreshing job " + job);
		}
		if (BlastJobStatus.FAILED.equals(job.getStatus()) || BlastJobStatus.NOT_FOUND.equals(job.getStatus())) {
			if (log.isInfoEnabled()) {
				log.info("Job not found or failed! " + job.getJobid() + ":" + job.getUniprotAc());
			}
		} else {

			if (!BlastJobStatus.DONE.equals(job.getStatus())) {
				Job clientJob = getClientJob(job);
				try {
					BlastJobStatus status = checkStatus(clientJob);
					if (BlastJobStatus.DONE.equals(status)) {
						BlastOutput result = getResult(clientJob);
						saveResult(job, result);
					}
					if (BlastJobStatus.NOT_FOUND.equals(status) || BlastJobStatus.FAILED.equals(status)) {
						if (log.isInfoEnabled()) {
							log.info("JobWS not found or failed! " + job.getJobid() + ":" + job.getUniprotAc());
						}
					}
				} catch (BlastClientException e) {
					throw new BlastServiceException(e + job.toString());
				}
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
		refreshDb(BlastJobStatus.RUNNING);
		refreshDb(BlastJobStatus.PENDING);
	}

	//TODO: refresh only 10 at a time, but for now there should not be more than 20 running at a time
	private void refreshDb(BlastJobStatus status) throws BlastServiceException {
	//	int atATime = 15;
		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		try {
			jobs = blastJobDao.getJobsByStatus(status);//blastJobDao.getNrJobsByStatus(atATime, status);
			//while (jobs.size() != 0) {	
				for (BlastJobEntity jobEntity : jobs) {
					refreshJob(jobEntity);
				}
				if (log.isDebugEnabled()) {
					int jobsR = blastJobDao.countJobs(BlastJobStatus.RUNNING);
					log.debug("running jobs: " + jobsR);
					jobsR = blastJobDao.countJobs(BlastJobStatus.PENDING);
					log.debug("pending jobs: " + jobsR);
				}
			//	jobs = blastJobDao.getNrJobsByStatus(atATime, status);
			//}
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	private void refreshFailedNotFound() throws BlastServiceException {
		try {
			Set<BlastJobEntity> jobs = new HashSet<BlastJobEntity>();
			jobs.addAll(blastJobDao.getJobsByStatus(BlastJobStatus.FAILED));
			for (BlastJobEntity jobEntity : jobs) {
				blastJobDao.deleteJob(jobEntity);
				submitJob(new UniprotAc(jobEntity.getUniprotAc()));
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
	 *            a web service or blast job
	 */
	private BlastJobEntity saveSubmittedJob(Job job) throws BlastServiceException {
		if (job == null) {
			throw new IllegalArgumentException("Job must not be null!");
		}
		Timestamp timestamp = getCurrentDate();
		BlastJobEntity jobEntity = new BlastJobEntity(job.getId(), job.getBlastInput().getUniprotAc().getAcNr(), job
				.getStatus(), null, timestamp);
		try {
			blastJobDao.saveJob(jobEntity);
			if (log.isDebugEnabled()) {
				log.debug("job: " + job.toString() + "saved to DB");
			}

			return jobEntity;
		} catch (BlastJdbcException e) {
			throw new BlastServiceException(e);
		}
	}

	/**
	 * Saves the blast job with the current date in the DB
	 * 
	 * @param job
	 *            a web service or blast job
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
		if (resultFile == null) {
			throw new NullPointerException("ResultFile must not be null!");
		}
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
	private BlastResult resultFromDB(BlastJobEntity job) throws BlastServiceException {
		if (job != null) {
			if (BlastJobStatus.DONE.equals(job.getStatus()) && job.getResultPath() != null) {
				BlastResult result = fetchResult(job);
				if (log.isDebugEnabled()) {
					log.debug("fetched result for :" + job);
				}
				return result;
			} else if (BlastJobStatus.FAILED.equals(job.getStatus())
					|| BlastJobStatus.NOT_FOUND.equals(job.getStatus())) {
				BlastResult result = new BlastResult(job.getUniprotAc(), new ArrayList<Hit>(0));

				if (log.isDebugEnabled()) {
					log.debug("generated empty result for :" + job);
				}
				return result;
			}
		}
		return null;
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

	private void writeResults(byte[] result, Writer writer) throws BlastServiceException {
		try {
			String resultStr = new String(result);
			writer.append(resultStr);
			writer.close();
		} catch (IOException e) {
			throw new BlastServiceException(e);
		}
	}

	// ////////////////
	// abstract Methods
	protected abstract List<Job> runBlast(Set<UniprotAc> uniprotAcs) throws BlastClientException;

	protected abstract Job runBlast(UniprotAc uniprotAc) throws BlastClientException;

	protected abstract Job runBlast(BlastInput blastInput) throws BlastClientException;

	protected abstract BlastJobStatus checkStatus(Job job) throws BlastClientException;

	protected abstract BlastOutput getResult(Job job) throws BlastClientException;

	protected abstract BlastResult processOutput(File blastFile) throws BlastClientException;
}