/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast;

import uk.ac.ebi.intact.bridges.wswublast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastInput;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastResult;
import uk.ac.ebi.intact.bridges.wswublast.model.UniprotAc;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * BlastService strategy.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre>
 *        12 Sep 2007
 *        </pre>
 */
public interface BlastService {

    public void importCsv( File csvFile ) throws BlastServiceException;

    public void exportCsv( File csvFile ) throws BlastServiceException;

    public boolean okToSubmit( int nr ) throws BlastServiceException;

    public BlastJobEntity submitJob( UniprotAc uniprotAc ) throws BlastServiceException;

    public BlastJobEntity submitJob( BlastInput blastInput ) throws BlastServiceException;

    public List<BlastJobEntity> submitJobs( Set<UniprotAc> uniprotAcs ) throws BlastServiceException;

    public BlastResult fetchAvailableBlast( UniprotAc uniprotAc ) throws BlastServiceException;

    /**
     * Retrieves the result from the DB. If the jobs are running it refreshes the job and tries once more.
     *
     * @param job : a BlastJobEntity object
     * @return BlastResult
     * @throws BlastServiceException  : wrapper for the BlastJdbcException
     */
    public BlastResult fetchAvailableBlast( BlastJobEntity job ) throws BlastServiceException;

    /**
     * Retrieves the results from DB. If the jobs are running it refreshes the job and tries again to get the result.
     *
     * @param uniprotAcs : a set of UniprotAc objects
     * @return List<BlastResult> , not null
     * @throws BlastServiceException : wrapper for the BlastJdbcException
     */
    public List<BlastResult> fetchAvailableBlasts( Set<UniprotAc> uniprotAcs ) throws BlastServiceException;

    /**
     * Retrieves the results from DB. If the jobs are running it refreshes the job and tries again to get the result.
     *
     * @param jobs : a list of BlastJobEntity objects
     * @return List<BlastResult>
     * @throws BlastServiceException : wrapper for the BlastJdbcException
     */
    public List<BlastResult> fetchAvailableBlasts( List<BlastJobEntity> jobs ) throws BlastServiceException;

    /**
     *
     * @return   List<BlastJobEntity>
     * @throws BlastServiceException  : wrapps the BlastJdbcException
     */
    public List<BlastJobEntity> fetchFailedJobs() throws BlastServiceException;

    /**
     *
     * @return List<BlastJobEntity>
     * @throws BlastServiceException    : wrapps the BlastJdbcException
     */
    public List<BlastJobEntity> fetchNotFoundJobs() throws BlastServiceException;

    /**
     *
     * @return List<BlastJobEntity>
     * @throws BlastServiceException : wrapps the BlastJdbcException
     */
    public List<BlastJobEntity> fetchRunningJobs() throws BlastServiceException;

    public void refreshDb() throws BlastServiceException;
    public void refreshJob( BlastJobEntity jobEntity ) throws BlastServiceException; 

    /**
     * Retrieves from DB
     *
     * @param job : a BlstJobEntity object
     * @return BlastResult
     * @throws BlastServiceException : wrapps the BlastJdbcException
     */
    public BlastResult fetchResult( BlastJobEntity job ) throws BlastServiceException;

    //TODO: is this method really needed?
    public List<BlastJobEntity> fetchJobEntities( Set<UniprotAc> uniprotAcs ) throws BlastServiceException;

    public void deleteJob( BlastJobEntity job ) throws BlastServiceException;

    public void deleteJobs( List<BlastJobEntity> jobs ) throws BlastServiceException;

    public void deleteJobsAll() throws BlastServiceException;

	public void close() throws BlastServiceException;
}
