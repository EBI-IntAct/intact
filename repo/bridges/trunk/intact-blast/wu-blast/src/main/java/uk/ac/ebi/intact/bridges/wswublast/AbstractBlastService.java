/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.wswublast.client.WuBlastClientException;
import uk.ac.ebi.intact.bridges.wswublast.failure.FailureMethods;
import uk.ac.ebi.intact.bridges.wswublast.failure.FailureStrategy;
import uk.ac.ebi.intact.bridges.wswublast.failure.WaitAndRetry;
import uk.ac.ebi.intact.bridges.wswublast.jdbc.BlastJdbcException;
import uk.ac.ebi.intact.bridges.wswublast.jdbc.BlastJobDao;
import uk.ac.ebi.intact.bridges.wswublast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.wswublast.model.*;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingReader;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingException;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.EBIApplicationResult;

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

/**
 * Main implementation of the BlastService strategy.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre> 12 Sep 2007  </pre>
 */
public abstract class AbstractBlastService implements BlastService {
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( AbstractBlastService.class );


    private BlastJobDao blastJobDao;
    private File workDir;
    // nr of proteins to be blasted at a time
    private int nrSubmission;

    // for error handling
    //  private int nrTries = 0;
    private int nrMaxTries = 10;
    //TODO: create the failureStrategy (processFailure(method)
    private FailureStrategy failS;

    // protected boolean isXmlFormat;

    // ///////////////
    // Constructor

    AbstractBlastService( File dbFolder, String tableName, int nrPerSubmission ) throws BlastServiceException {
        try {
            blastJobDao = new BlastJobDao( dbFolder, tableName );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
        nrSubmission = nrPerSubmission;
        // TODO: remove after test
        failS = new WaitAndRetry( 10 );
    }

    public void setNrMaxTries( int nrMaxTries ) {
        this.nrMaxTries = nrMaxTries;
    }

    // ///////////////
    // Getters/setters
    public void setWorkDir( File workDir ) {
        if ( !workDir.exists() ) {
            throw new IllegalArgumentException( "WorkDir must exist! " + workDir.getPath() );
        }
        if ( !workDir.isDirectory() ) {
            throw new IllegalArgumentException( "WorkDir must be a directory! " + workDir.getPath() );
        }
        if ( !workDir.canWrite() ) {
            throw new IllegalArgumentException( "WorkDir must be writable! " + workDir.getPath() );
        }
        this.workDir = workDir;
    }

    // ///////////////
    // inherited

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#exportCsv(java.io.File)
      */

    public void exportCsv( File csvFile ) throws BlastServiceException {
        try {
            blastJobDao.exportCSV( csvFile );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#importCsv(java.io.File)
      */
    public void importCsv( File csvFile ) throws BlastServiceException {
        try {
            blastJobDao.importCSV( csvFile );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.confidence.wswublast.BlastStrategy#submitJob(java.lang.String)
      */
    /**
     * return BlastJobEntity, can be null
     */
    public BlastJobEntity submitJob( UniprotAc uniprotAc) throws BlastServiceException {
        if ( uniprotAc == null ) {
            throw new IllegalArgumentException( "uniprot ac must not be null!" );
        }
        if ( uniprotAc.getAcNr() == null ) {
            throw new IllegalArgumentException( "UniprotAc.getAcNr() must not be null!" );
        }
        return submitJob( new BlastInput( uniprotAc ) );
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#submitJob(uk.ac.ebi.intact.bridges.wswublast.model.BlastInput)
      */
    public BlastJobEntity submitJob( BlastInput blastInput ) throws BlastServiceException {
        long startTime = System.currentTimeMillis() / 1000;
        if ( blastInput == null || blastInput.getUniprotAc() == null ) {
            throw new IllegalArgumentException( "BlastInput(Uniprot, {Seq}) must not be null!" );
        }
        if ( blastInput.getUniprotAc().getAcNr() == null ) {
            throw new IllegalArgumentException( "UniprotAc.getAcNr() must not be null!" );
        }
        if ( blastInput.getSequence() == null ) {
            if ( log.isWarnEnabled() ) {
                log.warn( "BlastInput(" + blastInput.getUniprotAc() + ") has no sequence!" );
            }
        }
        try {
            BlastJobEntity jobEntity = blastJobDao.getJobByAc( blastInput.getUniprotAc() );
            if ( jobEntity != null ) {
                if ( log.isTraceEnabled() ) {
                    log.trace( "found Job(" + blastInput.getUniprotAc() + ") took: " + ( ( System.currentTimeMillis() / 1000 ) - startTime ) + " sec" );
                }
                return jobEntity;
            } else {
                try {
                    int nr = availableNrToSubmit();
                    while ( nr < 1 ) {
                        Thread.sleep( 5000 );
                        refreshDb( BlastJobStatus.RUNNING );
                        nr = availableNrToSubmit();
                    }
                    Job job = runBlast( blastInput );
                    if ( log.isTraceEnabled() ) {
                        int runnningJobs = blastJobDao.countJobs( BlastJobStatus.RUNNING );
                        log.trace( "after submit: #runningJobs: " + runnningJobs );
                    }
                    BlastJobEntity tmp = saveSubmittedJob( job );
                    if ( log.isTraceEnabled() ) {
                        log.trace( "submitted new job (" + blastInput.getUniprotAc() + ") took: " + ( ( System.currentTimeMillis() / 1000 ) - startTime ) + " sec" );
                    }
                    return tmp;
                } catch ( WuBlastClientException e ) {
                    if ( log.isWarnEnabled() ) {
                        log.warn( "WuBlastClientException" + e.toString() );
                    }
                    if ( log.isTraceEnabled() ) {
                        log.trace( "submit -> blastclientException: " + e.toString() );
                    }
                    if ( failS.getTries( FailureMethods.SUBMITJOB ) < nrMaxTries ) {
                        BlastJobEntity job = null;
                        try {
                            failS.incTries( FailureMethods.SUBMITJOB, 1 );
                            Thread.sleep( 5000 ); //5 sec
                            job = submitJob( blastInput );
                        } catch ( InterruptedException e1 ) {
                            throw new BlastServiceException( e);
                        }
                        failS.resetTries( FailureMethods.SUBMITJOB );
                        return job;
                    } else {
                        failS.resetTries( FailureMethods.SUBMITJOB );
                        Job job = new Job( "blastFailed-" + blastInput.getUniprotAc(), blastInput );
                        job.setStatus( BlastJobStatus.FAILURE );
                        return saveSubmittedJob( job );
                    }
                    //TODO: make sure the error handling works, throw new BlastServiceException( e );
                } catch ( InterruptedException e ) {
                    throw new BlastServiceException( e );
                }
            }
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /*
    * (non-Javadoc)
    *
    * @see uk.ac.ebi.intact.confidence.wswublast.BlastStrategy#submitJobs(java.util.Set)
    */
    /**
     * @param uniprotAcs of uniprotAcs
     * @throws BlastServiceException
     * @result List<BlastJobEntity> , never null
     */
    // TODO: return an iterator for larger sets of results
    // FIXME: make shure it runs properly
    public List<BlastJobEntity> submitJobs( Set<UniprotAc> uniprotAcs ) throws BlastServiceException {
        if ( uniprotAcs == null ) {
            throw new IllegalArgumentException( "UniprotAcs must not be null!" );
        }
        if ( uniprotAcs.size() == 0 ) {
            throw new IllegalArgumentException( "UniprotAcs mut not be empty!" );
        }
        try {
            List<BlastJobEntity> jobEntities = blastJobDao.getJobsByAc( uniprotAcs );
            if ( jobEntities.size() == uniprotAcs.size() ) {
                return jobEntities;
            }
            Set<UniprotAc> prots;
            if ( jobEntities.size() == 0 ) {
                prots = uniprotAcs;
            } else {
                prots = notIncluded( jobEntities, uniprotAcs );
            }
            while ( prots.size() != 0 ) {
                int nr = waitForFreeSubmissionSlots();
                jobEntities.addAll( runNrBlast( prots, nr ) );
            }
            return jobEntities;
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    private int waitForFreeSubmissionSlots() throws BlastServiceException {
        int nr = availableNrToSubmit();
        while ( nr == 0 ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            refreshDb( BlastJobStatus.RUNNING );
            nr = availableNrToSubmit();
        }
        return nr;
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#fetchAvailableBlast(uk.ac.ebi.intact.bridges.wswublast.model.UniprotAc)
      */
    /**
     * @return BlastResult, can be null, if the uniprotAc is not in the DB
     */
    public BlastResult fetchAvailableBlast( UniprotAc uniprotAc ) throws BlastServiceException {
        long startTime = System.currentTimeMillis() / 1000;
        if ( uniprotAc == null ) {
            throw new IllegalArgumentException( "UniprotAc must not be null!" );
        }
        BlastJobEntity blastJobEntity;
        try {
            blastJobEntity = blastJobDao.getJobByAc( uniprotAc );
            if ( blastJobEntity != null ) {
                if ( log.isTraceEnabled() ) {
                    log.trace( "found: " + blastJobEntity );
                    long endTime = System.currentTimeMillis() / 1000;
                    log.trace( "fetchAvailableBlast(" + uniprotAc + ")  took: " + ( endTime - startTime ) + " sec" );
                }
                return fetchAvailableBlast( blastJobEntity );
            } else {
                if ( log.isTraceEnabled() ) {
                    long endTime = System.currentTimeMillis() / 1000;
                    log.trace( "fetchAvailableBlast(" + uniprotAc + ")  took: " + ( endTime - startTime ) + " sec" );
                }
                return null;
            }
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#fetchAvailableBlast(uk.ac.ebi.intact.bridges.wswublast.jdbc.BlastJobEntity)
      */
    public BlastResult fetchAvailableBlast( BlastJobEntity blastJobEntity ) throws BlastServiceException {
        if ( blastJobEntity == null ) {
            throw new IllegalArgumentException( "Job must not be null!" );
        }
        try {
            BlastJobEntity jobEntity = blastJobDao.getJobById( blastJobEntity.getJobid() );
            if ( jobEntity != null ) {
                if ( BlastJobStatus.FAILURE.equals( jobEntity.getStatus() )
                     || BlastJobStatus.NOT_FOUND.equals( jobEntity.getStatus() ) ) {
                    return new BlastResult( blastJobEntity.getUniprotAc(), new ArrayList<Hit>( 0 ) );
                } else {
                    if ( !BlastJobStatus.FINISHED.equals( jobEntity.getStatus() ) ) {
                        refreshJob( blastJobEntity );
                    }
                    return fetchResult( blastJobEntity );
                }
            } else {
                if ( log.isTraceEnabled() ) {
                    log.trace( "JobEntity not in DB: " + jobEntity );
                }
            }
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
        return null;
    }

    /**
     * Retrieves the available results from the DB
     *
     * @param uniprotAcs of uniprotAcs(string)
     * @return list of BlastResults
     * @throws BlastServiceException
     */
    // TODO: return an iterator for larger sets (ex. larger than 20)
    public List<BlastResult> fetchAvailableBlasts( Set<UniprotAc> uniprotAcs ) throws BlastServiceException {
        if ( uniprotAcs == null ) {
            throw new IllegalArgumentException( "UniprotAcs must not be null!" );
        }
        if ( uniprotAcs.size() == 0 ) {
            throw new IllegalArgumentException( "UniprotAcs mut not be empty!" );
        }
        List<BlastJobEntity> blastJobEntities;
        try {
            blastJobEntities = blastJobDao.getJobsByAc( uniprotAcs );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
        return fetchAvailableBlasts( blastJobEntities );
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.confidence.wswublast.BlastStrategy#fetchAvailableBlasts(java.util.List)
      */
    /**
     * Retrieves the available results from the DB
     *
     * @param jobs of blastJobEntities
     * @return list of BlastResults
     * @throws BlastServiceException
     */
    // TODO: return an iterator for larger sets (ex. larger than 20)
    public List<BlastResult> fetchAvailableBlasts( List<BlastJobEntity> jobs ) throws BlastServiceException {
        if ( jobs == null ) {
            throw new IllegalArgumentException( "Jobs list must not be null!" );
        }
        if ( jobs.size() == 0 ) {
            throw new IllegalArgumentException( "Jobs list mut not be empty!" );
        }

        List<BlastResult> results = new ArrayList<BlastResult>( jobs.size() );
        refreshJobs( jobs );
        try {
            for ( BlastJobEntity jobEntity : jobs ) {
                BlastJobEntity job = blastJobDao.getJobById( jobEntity.getJobid() );
                if ( job != null ) {
                    if ( BlastJobStatus.FINISHED.equals( job.getStatus() ) ) {
                        BlastResult res = fetchResult( job );
                        if (res != null){
                            results.add( res );
                        }
                    } else if ( BlastJobStatus.FAILURE.equals( job.getStatus() )
                                || BlastJobStatus.NOT_FOUND.equals( job.getStatus() ) ) {
                        BlastResult res = new BlastResult( job.getUniprotAc(), new ArrayList<Hit>( 0 ) );
                        if (res != null){
                            results.add( res );
                        }
                    }
                } else {
                    if ( log.isTraceEnabled() ) {
                        log.trace( "JobEntity not in DB: " + jobEntity );
                    }
                }
            }
            return results;
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /**
     * Retrieves the BlastResult from the jobEntity, without refreshing the blastJobEntity
     * from DB / Blast.
     *
     * @param blastJobEntity : a BlastJobEntity-object
     * @return the BlastResult
     * @throws BlastServiceException
     */
    public BlastResult fetchResult( BlastJobEntity blastJobEntity ) throws BlastServiceException {
        if ( blastJobEntity == null ) {
            throw new NullPointerException( "JobEntity most be not null!" );
        }
        if ( !BlastJobStatus.FINISHED.equals( blastJobEntity.getStatus() ) || blastJobEntity.getResultPath() == null ) {
            refreshJob( blastJobEntity );
            if ( !BlastJobStatus.FINISHED.equals( blastJobEntity.getStatus() ) ) {
                return null;
            }
        }

        // File resultDbFile = blastJobEntity.getResult();
        // TODO: is this the final solution?
        File blastFile = new File( this.workDir, blastJobEntity.getUniprotAc() + ".xml" );
        if ( !blastFile.exists() ) {
            if ( log.isWarnEnabled() ) {
                log.warn( "file not found in the archiveDir : " + blastFile.getPath() );
            }
            return null;
        }
        try {
            return processOutput( blastFile );
        } catch ( WuBlastClientException e ) {
            throw new BlastServiceException( e );
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#fetchFailedJobs()
      */
    public List<BlastJobEntity> fetchFailedJobs() throws BlastServiceException {
        rerun( BlastJobStatus.FAILURE );
        rerun( BlastJobStatus.NOT_FOUND );
        try {
            return blastJobDao.getJobsByStatus( BlastJobStatus.FAILURE );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#fetchNotFoundJobs()
      */
    public List<BlastJobEntity> fetchNotFoundJobs() throws BlastServiceException {
        // refreshDb();  // TODO: refresh pending, Running, Not_found, Failed
        //i do not need a refresh here
        try {
            return blastJobDao.getJobsByStatus( BlastJobStatus.NOT_FOUND );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see uk.ac.ebi.intact.bridges.wswublast.BlastService#fetchRunningJobs()
      */
    public List<BlastJobEntity> fetchRunningJobs() throws BlastServiceException {
        refreshDb( BlastJobStatus.RUNNING );
        try {
            return blastJobDao.getJobsByStatus( BlastJobStatus.RUNNING );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /**
     * For the specified list of proteins it retrieves the jobEntities.
     *
     * @param uniprotAcs : a set of UniprotAc -objects
     * @return a list of BlastJobEntities of not null members
     */
    // TODO: return an iterator for larger sets (ex. larger than 20)
    public List<BlastJobEntity> fetchJobEntities( Set<UniprotAc> uniprotAcs ) throws BlastServiceException {
        if ( uniprotAcs == null ) {
            throw new IllegalArgumentException( "UniprotAcs list must not be null!" );
        }
        if ( uniprotAcs.size() == 0 ) {
            throw new IllegalArgumentException( "UniprotAcs list mut not be empty!" );
        }
        try {
            return blastJobDao.getJobsByAc( uniprotAcs );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    public void deleteJob( BlastJobEntity job ) throws BlastServiceException {
        try {
            blastJobDao.deleteJobById( job.getJobid() );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    public void deleteJobs( List<BlastJobEntity> jobs ) throws BlastServiceException {
        for ( BlastJobEntity job : jobs ) {
            deleteJob( job );
        }
    }

    public void deleteJobsAll() throws BlastServiceException {
        try {
            blastJobDao.deleteJobs();
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    public void close() throws BlastServiceException {
        try {
            blastJobDao.close();
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    // //////////////////////
    // private Methods
    public boolean okToSubmit( int nr ) throws BlastServiceException {
        if ( nr < 1 ) {
            return false;
        }
        int available = availableNrToSubmit();
        return ( available >= nr );
    }

    private int availableNrToSubmit() throws BlastServiceException {
        try {
            int runningJobs = blastJobDao.countJobs( BlastJobStatus.RUNNING );
            if ( log.isTraceEnabled() ) {
                log.trace( "#running jobs: " + runningJobs );
            }
            return nrSubmission - runningJobs;
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    private List<BlastJobEntity> runNrBlast( Set<UniprotAc> uniprotAcs, int allowedNrRunningJobs )
            throws BlastServiceException {
        List<BlastJobEntity> jobEntities = new ArrayList<BlastJobEntity>( allowedNrRunningJobs );
        try {
            Set<UniprotAc> forSubmission = new HashSet<UniprotAc>( allowedNrRunningJobs );
            if ( allowedNrRunningJobs > uniprotAcs.size() ) {
                allowedNrRunningJobs = uniprotAcs.size();
            }
            for ( int i = 0; i < allowedNrRunningJobs; i++ ) {
                forSubmission.add( ( UniprotAc ) uniprotAcs.toArray()[i] );
            }

            List<Job> jobs = runBlast( forSubmission );
            jobEntities.addAll( saveSubmittedJobs( jobs ) );
            uniprotAcs.removeAll( forSubmission );

        } catch ( WuBlastClientException e ) {
            if ( failS.getTries( FailureMethods.RUN_BLAST_ONLY_NR_A_TIME ) < nrMaxTries ) {
                failS.incTries( FailureMethods.RUN_BLAST_ONLY_NR_A_TIME, 1 );
                try {
                    Thread.sleep( 5000 ); // 5 sec
                } catch ( InterruptedException e1 ) {
                    e1.printStackTrace();
                }
                runNrBlast( uniprotAcs, allowedNrRunningJobs );
                failS.resetTries( FailureMethods.SUBMITJOB );
            } else {
                failS.resetTries( FailureMethods.RUN_BLAST_ONLY_NR_A_TIME );
                return jobEntities;
            }
            // TODO: make sure that this error handling works throw new BlastServiceException( e );
        }
        return jobEntities;
    }

    public void refreshJob( BlastJobEntity jobEntity ) throws BlastServiceException {
        if ( jobEntity == null ) {
            throw new NullPointerException( "JobEntity most be not null!" );
        }
        if ( log.isTraceEnabled() ) {
            log.trace( "refreshing job " + jobEntity );
        }
        if ( BlastJobStatus.FAILURE.equals( jobEntity.getStatus() ) || BlastJobStatus.NOT_FOUND.equals( jobEntity.getStatus() ) ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Job NOT_FOUND or FAILED! " + jobEntity.getJobid() + ":" + jobEntity.getUniprotAc() );
            }
        } else {

            if ( !BlastJobStatus.FINISHED.equals( jobEntity.getStatus() ) ) {
                Job clientJob = extractClientJob( jobEntity );
                try {
                    BlastJobStatus status = checkStatus( clientJob );
                    if ( BlastJobStatus.FINISHED.equals( status ) ) {
                        BlastOutput result = getResult( clientJob );
                        saveResult( jobEntity, result );
                    }
                    if ( BlastJobStatus.NOT_FOUND.equals( status ) || BlastJobStatus.FAILURE.equals( status ) ) {
                        if ( log.isTraceEnabled() ) {
                            log.trace( "JobWS not found or failed! " + jobEntity.getJobid() + ":" + jobEntity.getUniprotAc() );
                        }
                    }
                } catch ( WuBlastClientException e ) {
                    //TODO: handle this exception : thread.wait(5000) + recall method
                    if ( failS.getTries( FailureMethods.REFRESHJOB ) < nrMaxTries ) {
                        failS.incTries( FailureMethods.REFRESHJOB, 1 );
                        try {
                            Thread.sleep( 5000 ); // 5 sec
                        } catch ( InterruptedException e1 ) {
                            throw new BlastServiceException( "thread.sleep interrupted", e1 );
                        }
                        refreshJob( jobEntity );
                        failS.resetTries( FailureMethods.REFRESHJOB );
                    }
                    //throw new BlastServiceException( job.toString(), e );
                }
            }
        }
    }

    private void refreshJobs( List<BlastJobEntity> jobs ) throws BlastServiceException {
        for ( BlastJobEntity jobEntity : jobs ) {
            // TODO: could skip the ifs
            if ( jobEntity.getStatus().equals( BlastJobStatus.RUNNING ) ) {
                refreshJob( jobEntity );
            }
        }
    }

    public void refreshDb() throws BlastServiceException {
        rerun( BlastJobStatus.FAILURE );
        rerun( BlastJobStatus.NOT_FOUND );
        refreshDb( BlastJobStatus.RUNNING );
    }

    private void rerun( BlastJobStatus status ) throws BlastServiceException {
        try {
            if ( log.isTraceEnabled() ) {
                int nr = blastJobDao.countJobs( status );
                log.trace( "rerunning: " + nr + " " + status + " jobs" );
            }

            Set<BlastJobEntity> jobs = new HashSet<BlastJobEntity>();
            jobs.addAll( blastJobDao.getJobsByStatus( status ) );
            int i = 0;
            for ( BlastJobEntity jobEntity : jobs ) {
                i++;
                if ( log.isTraceEnabled() ) {
                    log.trace( "Deleting job(" + i + "): " + jobEntity );
                }
                blastJobDao.deleteJob( jobEntity );
                BlastJobEntity newJob;
                if ( jobEntity.getSequence() != null && !jobEntity.getSequence().equalsIgnoreCase( "" ) ) {
                    newJob = submitJob( new BlastInput( new UniprotAc( jobEntity.getUniprotAc() ), new Sequence( jobEntity.getSequence() ) ) );
                } else {
                    newJob = submitJob( new UniprotAc( jobEntity.getUniprotAc() ) );
                }
                if ( log.isTraceEnabled() ) {
                    log.trace( "New job submitted: " + newJob );
                }
            }
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    // TODO: refresh only 10 at a time, but for now there should not be more
    // than 20 running at a time
    private void refreshDb( BlastJobStatus status ) throws BlastServiceException {
        /*
 int atATime = 15;
        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
*/
        try {
            List<BlastJobEntity> jobs = blastJobDao.getJobsByStatus( status );// blastJobDao.getNrJobsByStatus(atATime,
            // status);
            // while (jobs.size() != 0) {
            for ( BlastJobEntity jobEntity : jobs ) {
                refreshJob( jobEntity );
            }
            if ( log.isTraceEnabled() ) {
                int jobsR = blastJobDao.countJobs( BlastJobStatus.RUNNING );
                log.trace( "running jobs: " + jobsR );
            }
            // jobs = blastJobDao.getNrJobsByStatus(atATime, status);
            // }
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    private Job extractClientJob( BlastJobEntity job ) {
        if ( job == null ) {
            throw new NullPointerException( "JobEntity most be not null!" );
        }
        UniprotAc uniAc = new UniprotAc( job.getUniprotAc() );
        String seqStr = job.getSequence();
        if (seqStr != null || seqStr != ""){
           Sequence seq = new Sequence(job.getSequence());
            BlastInput blastInput = new BlastInput(uniAc, seq);
            return new Job(job.getJobid(), blastInput);
        } else {
            BlastInput blastInput = new BlastInput( uniAc );
            return new Job( job.getJobid(), blastInput );
        }

    }

    /**
     * Saves the wswublast job with the current date in the DB
     *
     * @param job : a Job -object
     * @return BlastJobEntity
     * @throws BlastServiceException : wrapper for the WS -exception
     */
    private BlastJobEntity saveSubmittedJob( Job job ) throws BlastServiceException {
        if ( job == null ) {
            throw new IllegalArgumentException( "Job must not be null!" );
        }
        Timestamp timestamp = getCurrentDate();
        String seq = "";
        if ( job.getBlastInput().getSequence() != null ) {
            seq = job.getBlastInput().getSequence().getSeq();
        }
        BlastJobEntity blastJobEntity = new BlastJobEntity( job.getId(), job.getBlastInput().getUniprotAc().getAcNr(), seq,
                                                            job.getStatus(), null, timestamp );
         try {
             if ( job.getStatus().equals( BlastJobStatus.FINISHED ) ) {
                 BlastOutput blastOutput = getResult( job );
                 File resultFile = writeResultsToWorkDir( blastJobEntity, blastOutput );
                 if ( resultFile == null ) {
                     throw new NullPointerException( "ResultFile must not be null!" );
                 }

                 if ( resultOK( resultFile ) ) {
                     blastJobEntity.setStatus( BlastJobStatus.FINISHED );
                 } else {
                     blastJobEntity.setStatus( BlastJobStatus.FAILURE );
                 }

                 blastJobEntity.setResult( resultFile );
             }
         } catch ( WuBlastClientException e ) {
            throw new BlastServiceException( e);
         }

        try {
            blastJobDao.saveJob( blastJobEntity );
            if ( log.isTraceEnabled() ) {
                log.trace( "job: " + job.toString() + " saved to DB" );
            }

            return blastJobEntity;
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        }
    }

    /**
     * Saves the wswublast job with the current date in the DB
     *
     * @param jobs : a list of Job -Objects
     * @return List<BlastJobEntity>
     * @throws BlastServiceException : a wrapper for the WS - exception
     */
    private List<BlastJobEntity> saveSubmittedJobs( List<Job> jobs ) throws BlastServiceException {
        List<BlastJobEntity> submitted = new ArrayList<BlastJobEntity>( jobs.size() );
        for ( Job job : jobs ) {
            submitted.add( saveSubmittedJob( job ) );
        }
        return submitted;
    }

    private void saveResult( BlastJobEntity blastJobEntity, BlastOutput blastOutput ) throws BlastServiceException {
        // if the status was done but when getting the blastOutput
        // the web service throws and OutOfMemoryError, the blastOutput == null
        try {
            if ( blastOutput != null ) {
                File resultFile = writeResultsToWorkDir( blastJobEntity, blastOutput );
                if ( resultFile == null ) {
                    throw new NullPointerException( "ResultFile must not be null!" );
                }

                if (resultOK(resultFile)) {
                    blastJobEntity.setStatus( BlastJobStatus.FINISHED );
                } else {
                    blastJobEntity.setStatus( BlastJobStatus.FAILURE );
                }

                blastJobEntity.setResult( resultFile );

            } else {
                blastJobEntity.setStatus( BlastJobStatus.FAILURE );
            }
            blastJobDao.updateJob( blastJobEntity );
        } catch ( BlastJdbcException e ) {
            throw new BlastServiceException( e );
        } 
    }

    private boolean resultOK( File resultFile ) throws BlastServiceException {
         BlastMappingReader bmr = new BlastMappingReader();
        try {
            EBIApplicationResult result = bmr.read(resultFile);
            return result != null;
        } catch ( BlastMappingException e ) {
            throw new BlastServiceException(e);
        }
    }

    // //////////////////////
    // private Methods

    private static Timestamp getCurrentDate() {
        Date today = new Date( System.currentTimeMillis() );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
        sdf.format( today );
        return new Timestamp( today.getTime() );
    }

    private Set<UniprotAc> notIncluded( List<BlastJobEntity> results, Set<UniprotAc> proteins ) {
        Set<UniprotAc> resultProt = new HashSet<UniprotAc>( results.size() );
        for ( BlastJobEntity jobEntity : results ) {
            resultProt.add( new UniprotAc( jobEntity.getUniprotAc() ) );
        }
        proteins.removeAll( resultProt );
        return proteins;
    }

    private File writeResultsToWorkDir( BlastJobEntity job, BlastOutput result ) throws BlastServiceException {
        String fileName = job.getUniprotAc() + ( result.isXmlFormat() ? ".xml" : ".txt" );
        File tmpFile = new File( workDir.getPath(), fileName );
        try {
            writeResults( result.getResult(), new FileWriter( tmpFile ) );
        } catch ( IOException e ) {
            throw new BlastServiceException( e );
        }
        return tmpFile;
    }
    
    private void writeResults( String result, Writer writer ) throws BlastServiceException {
        try {
            writer.append( result );
            writer.close();
        } catch ( IOException e ) {
            throw new BlastServiceException( e );
        }
    }

    // ////////////////
    // abstract Methods
    protected abstract List<Job> runBlast( Set<UniprotAc> uniprotAcs ) throws WuBlastClientException;

  //  protected abstract Job runBlast( UniprotAc uniprotAc ) throws WuBlastClientException;

    protected abstract Job runBlast( BlastInput blastInput ) throws WuBlastClientException;

    protected abstract BlastJobStatus checkStatus( Job job ) throws WuBlastClientException;

    protected abstract BlastOutput getResult( Job job ) throws WuBlastClientException;

    protected abstract BlastResult processOutput( File blastFile ) throws WuBlastClientException;
}