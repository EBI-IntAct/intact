/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/**
 * DAO for the job entries in the blast DB.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 12 Sep 2007
 * </pre>
 */
public class BlastJobDao {
	/**
	 * Sets up a logger for that class.
	 */
	public static final Log				log	= LogFactory.getLog(BlastJobDao.class);
	private static Connection			conn;
	private static PreparedStatement	insertStat;
	private static PreparedStatement	selectWhereIdStat;
	private static PreparedStatement	selectWhereTimestampStat;
	private static PreparedStatement	selectWhereUniprotAcStat;
	private static PreparedStatement	selectWhereUniproAcLatestDateStat;

	private static PreparedStatement	updateJobStat;
	private static PreparedStatement	selectWhereStatusStat;

	private static PreparedStatement	deleteByIdStat;
	private static PreparedStatement	deleteByAcStat;

	private static Statement			stat;

	/**
	 * Constructor
	 * @throws BlastJdbcException 
	 */
	public BlastJobDao(String tableName) throws BlastJdbcException {
		BlastDb blastDb = new BlastDb();
		conn = blastDb.getConn();
		stat = blastDb.getStat();
		blastDb.createJobTable(tableName);
		initPreparedStat();
	}

	// /////////////////
	// public Methods
	/**
	 * Deletes all the entries in the JOB table.
	 * @throws BlastJdbcException 
	 */
	public void deleteJobs() throws BlastJdbcException {
		try {
			String delete = "DELETE FROM Job;";
			stat.execute(delete);
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	public void deleteJob(BlastJobEntity job) throws BlastJdbcException {
		if (job == null) {
			throw new NullPointerException("Job must not be null!");
		}
		deleteJobById(job.getJobid());
	}

	public void deleteJobs(List<BlastJobEntity> jobs) throws BlastJdbcException {
		if (jobs == null) {
			throw new NullPointerException("Jobs list must not be null!");
		}
		if (jobs.size() == 0) {
			throw new IllegalArgumentException("Jobs list must not be empty!");
		}
		for (BlastJobEntity jobEntity : jobs) {
			deleteJob(jobEntity);
		}
	}

	/**
	 * Deletes the JOB entry with the specified job id.
	 * 
	 * @param jobid
	 * @throws BlastJdbcException 
	 */
	public void deleteJobById(String jobid) throws BlastJdbcException {
		if (jobid == null) {
			throw new NullPointerException("Jobid must not be null!");
		}
		try {
			deleteByIdStat.setString(1, jobid);

			int result = deleteByIdStat.executeUpdate();
			if (result != 1) {
				log.debug("Delete statement failed! " + deleteByIdStat);
			}
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	/**
	 * Deletes the JOB entry with the specified uniprotAc.
	 * 
	 * @param set
	 *            of jobids
	 * @throws BlastJdbcException 
	 */
	public void deleteJobsByIds(Set<String> jobids) throws BlastJdbcException {
		if (jobids == null) {
			throw new NullPointerException("Jobid list must not be null!");
		}
		if (jobids.size() == 0) {
			throw new IllegalArgumentException("Jobid list must not be empty!");
		}
		for (String jobid : jobids) {
			deleteJobById(jobid);
		}
	}

	/**
	 * Deletes the JOB entry with the specified uniprotAc.
	 * 
	 * @param uniprotAc
	 * @throws BlastJdbcException 
	 */
	public void deleteJobByAc(UniprotAc uniprotAc) throws BlastJdbcException {
		if (uniprotAc == null) {
			throw new NullPointerException("UniprotAc must not be null!");
		}
		if (uniprotAc.getAcNr() == null) {
			throw new NullPointerException("UniprotAc.acNr must not be null!");
		}
		try {
			deleteByAcStat.setString(1, uniprotAc.getAcNr());

			int result = deleteByAcStat.executeUpdate();
			if (result != 1) {
				log.debug("Delete statement failed! " + deleteByAcStat);
			}
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	/**
	 * Deletes the JOB entries with the specified uniprotAcs.
	 * 
	 * @param set
	 *            of uniprotAcs
	 * @throws BlastJdbcException 
	 */
	public void deleteJobsByAcs(Set<UniprotAc> uniprotAcs) throws BlastJdbcException {
		if (uniprotAcs == null) {
			throw new NullPointerException("UniprotAcs list must not be null!");
		}
		if (uniprotAcs.size() == 0) {
			throw new IllegalArgumentException("UniprotAcs list must not be empty!");
		}
		for (UniprotAc uniprotAc : uniprotAcs) {
			deleteJobByAc(uniprotAc);
		}
	}

	/**
	 * Saves the specified jobEntity in the DB.
	 * 
	 * @param job
	 * @throws BlastJdbcException 
	 */
	public void saveJob(BlastJobEntity job) throws BlastJdbcException {
		if (job == null) {
			throw new NullPointerException("Job must not be null!");
		}
		if (job.getJobid() == null || job.getUniprotAc() == null || job.getTimestamp() == null) {
			throw new IllegalArgumentException("Job mut contain not null data, please check the data!");
		}
		if (existsInDb(new UniprotAc(job.getUniprotAc()))) {
			throw new IllegalArgumentException("UniprotAc already in the Db!");
		}
		try {
			insertStat.setString(1, job.getJobid());
			insertStat.setString(2, job.getUniprotAc());
			insertStat.setString(3, job.getStatus().toString());
			insertStat.setString(4, job.getResultPath());
			insertStat.setTimestamp(5, job.getTimestamp());
			insertStat.execute();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	/**
	 * Saves the list of jobEntities to the DB.
	 * 
	 * @param jobs
	 * @throws BlastJdbcException 
	 */
	public void saveJobs(List<BlastJobEntity> jobs) throws BlastJdbcException {
		if (jobs == null) {
			throw new NullPointerException("Jobs-list must not be null!");
		}
		if (jobs.size() == 0) {
			throw new IllegalArgumentException("Jobs list must not be empty!");
		}
		for (BlastJobEntity job : jobs) {
			saveJob(job);
		}
	}

	public void updateJob(BlastJobEntity job) throws BlastJdbcException {
		if (job == null) {
			throw new NullPointerException("Job must not be null!");
		}
		if (job.getJobid() == null || job.getUniprotAc() == null || job.getResultPath() == null
				|| job.getTimestamp() == null) {
			throw new IllegalArgumentException("Job mut contain not null data, please check the data!");
		}
		if (!existsInDb(job.getJobid(), job.getUniprotAc())) {
			throw new IllegalArgumentException("Job not in the Db!");
		}
		try {
			updateJobStat.setString(1, job.getStatus().toString());
			updateJobStat.setTimestamp(2, job.getTimestamp());
			updateJobStat.setString(3, job.getResultPath());
			updateJobStat.setString(4, job.getJobid());
			updateJobStat.setString(5, job.getUniprotAc());
			updateJobStat.execute();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	/**
	 * Retrieves the list of jobs in the DB
	 * 
	 * @return list of jobs (an empty list if there are no jobs in the DB)
	 * @throws BlastJdbcException 
	 */
	public List<BlastJobEntity> selectAllJobs() throws BlastJdbcException {
		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		try {
			ResultSet rs = stat.executeQuery("SELECT * FROM Job");

			while (rs.next()) {
				String path = rs.getString("resultPath");
				if (path == null) {
					path = "tmpAux";
				}
				BlastJobStatus status = getStatus(rs.getString("status"));

				BlastJobEntity newJob = new BlastJobEntity(rs.getString("jobid"), rs.getString("uniprotAc"), status,
						new File(path), rs.getTimestamp("timestamp"));
				jobs.add(newJob);
			}

			rs.close();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
		return jobs;
	}

	/**
	 * Retrieves the job with the specified jobid
	 * 
	 * @param jobid
	 * @return BlastJobEntity, or null
	 * @throws BlastJdbcException 
	 */
	public BlastJobEntity getJobById(String jobid) throws BlastJdbcException {
		if (jobid == null) {
			throw new NullPointerException("Jobid must not be null!");
		}
		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		try {
			selectWhereIdStat.setString(1, jobid);

			ResultSet rs = selectWhereIdStat.executeQuery();
			while (rs.next()) {
				String jobId = rs.getString("jobid");
				String uniAc = rs.getString("uniprotAc");
				String path = rs.getString("resultPath");
				if (path == null) {
					path = "tmpAux";
				}

				BlastJobStatus status = getStatus(rs.getString("status"));
				BlastJobEntity newJob = new BlastJobEntity(jobId, uniAc, status, new File(path), rs.getTimestamp("timestamp"));
				jobs.add(newJob);
			}

			rs.close();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}

		if (jobs.size() == 0) {
			return null;
		}
		if (jobs.size() > 1) {
			log.debug("Make sure the jobId is unique key for the db !!!");
		}
		return jobs.get(0);
	}

	public List<BlastJobEntity> getJobsByAc(Set<UniprotAc> uniprotAcs) throws BlastJdbcException {
		if (uniprotAcs == null) {
			throw new NullPointerException("UniprotAcs list must not be null!");
		}
		if (uniprotAcs.size() == 0) {
			throw new IllegalArgumentException("UniprotAcs list must not be empty!");
		}

		List<BlastJobEntity> results = new ArrayList<BlastJobEntity>();

		for (UniprotAc ac : uniprotAcs) {
			BlastJobEntity job = getJobByAc(ac);
			if (job != null) {
				results.add(job);
			}
		}
		return results;
	}

	public List<BlastJobEntity> getJobsById(Set<String> jobids) throws BlastJdbcException {
		if (jobids == null) {
			throw new NullPointerException("Jobid list must not be null!");
		}
		if (jobids.size() == 0) {
			throw new IllegalArgumentException("Jobid list must not be empty!");
		}
		List<BlastJobEntity> results = new ArrayList<BlastJobEntity>();

		for (String id : jobids) {
			BlastJobEntity job = getJobById(id);
			if (job != null) {
				results.add(job);
			}
		}
		return results;
	}

	/**
	 * Retrieves the job with the specified uniprotAc
	 * 
	 * @param jobid
	 * @return BlastJobEntity, can be null
	 * @throws BlastJdbcException 
	 */
	public BlastJobEntity getJobByAc(UniprotAc uniprotAc) throws BlastJdbcException {
		if (uniprotAc == null) {
			throw new NullPointerException("UniprotAc must not be null!");
		}
		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		try {
			selectWhereUniprotAcStat.setString(1, uniprotAc.getAcNr());

			ResultSet rs = selectWhereUniprotAcStat.executeQuery();
			while (rs.next()) {
				String jobId = rs.getString("jobid");
				String uniAc = rs.getString("uniprotAc");
				String path = rs.getString("resultPath");
				if (path == null) {
					path = "tmpAux";
				}
				
				BlastJobStatus status = getStatus(rs.getString("status"));
				BlastJobEntity newJob = new BlastJobEntity(jobId, uniAc, status, new File(path), rs.getTimestamp("timestamp"));
				jobs.add(newJob);
			}

			rs.close();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}

		if (jobs.size() == 0) {
			return null;
		}
		if (jobs.size() > 1) {
			log.debug("Make sure the uniprotAc is unique for the DB !!!");
		}

		return jobs.get(0);
	}

	/**
	 * to test
	 * 
	 * @return list of blast job entities
	 * @throws BlastJdbcException 
	 */
	public List<BlastJobEntity> getJobsByStatus(BlastJobStatus status) throws BlastJdbcException {
		if (status == null){
			throw new IllegalArgumentException("Status must not be null!");
		}
		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		try {
			selectWhereStatusStat.setString(1, status.toString());
			ResultSet rs = selectWhereStatusStat.executeQuery();
			while (rs.next()) {
				String path = rs.getString("resultPath");
				if (path == null) {
					path = "tmpAux";
				}
				
				BlastJobStatus statusFromDb = getStatus(rs.getString("status"));
				BlastJobEntity newJob = new BlastJobEntity(rs.getString("jobid"), rs.getString("uniprotAc"), statusFromDb, new File(path), rs.getTimestamp("timestamp"));
				jobs.add(newJob);
			}

			rs.close();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}

		return jobs;
	}

	/**
	 * Retrieves the jobEntities with the specified Timestamp ('yyyy-mm-dd hh:mm:ss')
	 * 
	 * @param timestamp
	 * @return list of jobEntities, never null, but can be empty
	 * @throws BlastJdbcException 
	 */
	public List<BlastJobEntity> getJobsByTimestamp(Timestamp timestamp) throws BlastJdbcException {
		if (timestamp == null) {
			throw new NullPointerException("Timestamp must not be null!");
		}
		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		try {
			selectWhereTimestampStat.setTimestamp(1, timestamp);

			ResultSet rs = selectWhereTimestampStat.executeQuery();
			while (rs.next()) {
				String path = rs.getString("resultPath");
				if (path == null) {
					path = "tmpAux";
				}
				
				BlastJobStatus status = getStatus(rs.getString("status"));
				BlastJobEntity newJob = new BlastJobEntity(rs.getString("jobid"), rs.getString("uniprotAc"), status, new File(path), rs.getTimestamp("timestamp"));
				jobs.add(newJob);
			}

			rs.close();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}

		return jobs;
	}

	/**
	 * Retrieves the jobEntity with the specified uniprotAc and the latest
	 * submission date
	 * 
	 * @param uniprotAc
	 * @return a blast job entity, can be null
	 * @throws BlastJdbcException 
	 */
	public BlastJobEntity getJobByAcAndLatestDate(String uniprotAc) throws BlastJdbcException {
		if (uniprotAc == null) {
			throw new NullPointerException("UniprotAc must not be null!");
		}
		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		try {
			selectWhereUniproAcLatestDateStat.setString(1, uniprotAc);

			ResultSet rs = selectWhereUniproAcLatestDateStat.executeQuery();
			while (rs.next()) {
				String path = rs.getString("resultPath");
				if (path == null) {
					path = "tmpAux";
				}
				
				BlastJobStatus status = getStatus(rs.getString("status"));
				BlastJobEntity newJob = new BlastJobEntity(rs.getString("jobid"), rs.getString("uniprotAc"),status, new File(path), rs.getTimestamp("timestamp"));
				jobs.add(newJob);
			}

			rs.close();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}

		if (jobs.size() == 0) {
			return null;
		}
		if (jobs.size() > 1) {
			log.debug("There are more than one jobs submitted on the same day with the same uniprotAc!!!");
		}
		return jobs.get(0);
	}

	// /////////////////
	// private Methods
	private void initPreparedStat() throws BlastJdbcException {
		try {
			String insertSql = "INSERT INTO Job VALUES (?, ?, ?, ?, ?)";
			insertStat = conn.prepareStatement(insertSql);
			String selectSql = "SELECT * FROM Job WHERE jobid = ?";
			selectWhereIdStat = conn.prepareStatement(selectSql);
			String selectTimestampSql = "SELECT * FROM Job Where timestamp = ?";
			selectWhereTimestampStat = conn.prepareStatement(selectTimestampSql);
			String selectAcSql = "SELECT * FROM Job Where uniprotAc = ?";
			selectWhereUniprotAcStat = conn.prepareStatement(selectAcSql);
			String selectAcTimestampSql = "SELECT * FROM Job WHERE timestamp = (SELECT MAX(timestamp) FROM Job WHERE uniprotAc = ?)";
			selectWhereUniproAcLatestDateStat = conn.prepareStatement(selectAcTimestampSql);
			String selectPendingSql = "SELECt * FROM Job WHERE status =?;";
			selectWhereStatusStat = conn.prepareStatement(selectPendingSql);

			String updateJobSql = "UPDATE Job SET status= ?, timestamp =?, resultPath =? WHERE jobid= ? AND uniprotAc=?;";
			updateJobStat = conn.prepareStatement(updateJobSql);

			String deleteByIdSql = "DELETE FROM Job WHERE jobid = ?";
			deleteByIdStat = conn.prepareStatement(deleteByIdSql);

			String deleteByAcSql = "DELETE FROM Job WHERE uniprotAc = ?";
			deleteByAcStat = conn.prepareStatement(deleteByAcSql);

		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	private BlastJobStatus getStatus(String status) {
		if (BlastJobStatus.RUNNING.toString().equals(status)) {
			return BlastJobStatus.RUNNING;
		} else if (BlastJobStatus.PENDING.toString().equals(status)) {
			return BlastJobStatus.PENDING;
		} else if (BlastJobStatus.DONE.toString().equals(status)) {
			return BlastJobStatus.DONE;
		} else if (BlastJobStatus.FAILED.toString().equals(status)) {
			return BlastJobStatus.FAILED;
		} else if (BlastJobStatus.NOT_FOUND.toString().equals(status)) {
			return BlastJobStatus.NOT_FOUND;
		} 
		return null;
	}

	private boolean existsInDb(UniprotAc uniprotAc) throws BlastJdbcException {
		BlastJobEntity job = getJobByAc(uniprotAc);
		if (job == null) {
			return false;
		}
		return true;
	}

	private boolean existsInDb(String jobid, String uniprotAc) throws BlastJdbcException {
		BlastJobEntity job = getJobById(jobid);
		if (job == null) {
			return false;
		}
		if (job.getUniprotAc().equals(uniprotAc)) {
			return true;
		}
		return false;
	}
}
