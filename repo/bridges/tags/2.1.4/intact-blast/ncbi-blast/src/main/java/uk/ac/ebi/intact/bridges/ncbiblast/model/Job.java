/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.ncbiblast.model;

/**
 * Class representing a submitted NCBI blast job.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 7 Sep 2007
 * </pre>
 */
public class Job {

	private String			id;
	private String		blastInput;
	private BlastJobStatus	status;

	public Job(String id, String blastInput) {
		this.id = id;
		this.blastInput = blastInput;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the blastInput
	 */
	public String getSequence() {
		return blastInput;
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
		if (status == null){
			throw new IllegalArgumentException("Status must not be null!");
		}
		this.status = status;
	}
	
	@Override
	public String toString() {
		return id + ": " + blastInput + ": " + status.toString();
	}
}
