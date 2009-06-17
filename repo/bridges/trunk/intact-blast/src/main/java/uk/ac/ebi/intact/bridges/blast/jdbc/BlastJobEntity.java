/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.jdbc;

import java.io.File;
import java.sql.Timestamp;

import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;

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
public class BlastJobEntity {
	private String			jobid;
	private String			uniprotAc;
	private String			sequence;
	private BlastJobStatus	status;
	private File			resultFile;
	private Timestamp		timestamp;

	public BlastJobEntity(String jobid) {
		this.setJobid(jobid);
	}

	public BlastJobEntity(String jobid, String uniprotAc, BlastJobStatus status, File result, Timestamp timestamp) {
		this.jobid = jobid;
		this.uniprotAc = uniprotAc;
		this.status = status;
		this.resultFile = result;
		this.timestamp = timestamp;
	}

	public BlastJobEntity(String jobid, String uniprotAc, String sequence, BlastJobStatus status, File result,
			Timestamp timestamp) {
		this(jobid, uniprotAc, status, result, timestamp);
		this.sequence = sequence;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public String getUniprotAc() {
		return uniprotAc;
	}

	public void setUniprotAc(String uniprotAc) {
		this.uniprotAc = uniprotAc;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the status
	 */
	public BlastJobStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(BlastJobStatus status) {
		this.status = status;
	}

	public File getResult() {
		return resultFile;
	}

	public void setResult(File result) {
		this.resultFile = result;
	}

	/**
	 * 
	 * @return path
	 */
	public String getResultPath() {
		if (resultFile == null) {
			return null;
		}
		return resultFile.getPath();
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return jobid + ":" + uniprotAc + ":" + status + ":"
				+ (resultFile == null ? "null" : resultFile.getPath()) + ":" + timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO: test if it works
		if (obj instanceof BlastJobEntity) {
			BlastJobEntity job = (BlastJobEntity) obj;
			if (!jobid.equals(job.getJobid())) {
				return false;
			}
			if (!uniprotAc.equals(job.getUniprotAc())) {
				return false;
			}
			if (!sequence.equals(job.getSequence())) {
				return false;
			}
			if (!status.equals(job.getStatus())) {
				return false;
			}
			if (!resultFile.getPath().equals(job.getResultPath())) {
				return false;
			}
			if (timestamp.equals(job.getTimestamp())) {
				return false;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return uniprotAc.hashCode();
	}

}