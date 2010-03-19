/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.model;

/**
 * Class representing a submitted blast job.
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
	private BlastInput		blastInput;
	private BlastJobStatus	status;
	private BlastOutput		blastResult;

	public Job(String id, BlastInput blastInput) {
		this.id = id;
		this.blastInput = blastInput;
	}

   public Job(String id, String sequence) {
		this.id = id;
       this.blastInput = new BlastInput(sequence);
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
	public BlastInput getBlastInput() {
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

	/**
	 * @return the blastResult
	 */
	public BlastOutput getBlastResult() {
		return blastResult;
	}

	/**
	 * @param blastResult
	 *            the blastResult to set
	 */
	public void setBlastResult(BlastOutput blastResult) {
		if (blastResult == null){
			throw new IllegalArgumentException("BlastResult must not be null!");
		}
		this.blastResult = blastResult;
	}
	
	@Override
	public String toString() {
		return id + ": " + blastInput + ": " + status.toString() +": " + blastResult;
	}
}
