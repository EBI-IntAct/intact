/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast;

import java.util.List;
import java.util.Set;

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
 * 12 Sep 2007
 * </pre>
 */
public interface BlastService {
	public BlastJobEntity submitJob(UniprotAc uniprotAc) throws BlastServiceException;

	public List<BlastJobEntity> submitJobs(Set<UniprotAc> uniprotAcs) throws BlastServiceException;

	/**
	 * Retrieves from DB
	 * 
	 * @param uniprotAcs
	 * @return
	 */
	public List<BlastResult> fetchAvailableBlasts(Set<UniprotAc> uniprotAcs) throws BlastServiceException;

	/**
	 * Retrieves from DB
	 * 
	 * @param jobs
	 * @return
	 */
	public List<BlastResult> fetchAvailableBlasts(List<BlastJobEntity> jobs)throws BlastServiceException;

	/**
	 * Retrieves from DB the pending jobs.
	 * 
	 * @return
	 */
	public List<BlastJobEntity> fetchPendingJobs() throws BlastServiceException;
	
	public List<BlastJobEntity> fetchFailedJobs() throws BlastServiceException;
	
	public List<BlastJobEntity> fetchNotFoundJobs() throws BlastServiceException;

	/**
	 * Retrieves from DB
	 * 
	 * @param job
	 * @return
	 */
	public BlastResult fetchResult(BlastJobEntity job) throws BlastServiceException;

	//TODO: is this method really needed? 
	public List<BlastJobEntity> fetchJobEntities(Set<UniprotAc> uniprotAcs) throws BlastServiceException ;

	//public BlastJobEntity refreshDB();

//	public void refreshJob(BlastJobEntity job);

//	public void refreshJobs(List<BlastJobEntity> jobs);
	
	public void deleteJob(BlastJobEntity job) throws BlastServiceException;
	
	public void deleteJobs(List<BlastJobEntity> jobs) throws BlastServiceException;

}
