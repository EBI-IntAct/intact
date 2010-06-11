/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.wswublast.model.UniprotAc;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DAO for the job entries in the wswublast DB.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre>
 *        12 Sep 2007
 *        </pre>
 */
public class BlastJobDao {
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( BlastJobDao.class );
    private static Connection conn;
    private static PreparedStatement insertStat;
    private static PreparedStatement selectWhereIdStat;
    private static PreparedStatement selectWhereTimestampStat;
    private static PreparedStatement selectWhereUniprotAcStat;
    private static PreparedStatement selectWhereUniproAcLatestDateStat;

    private static PreparedStatement updateJobStat;
    private static PreparedStatement selectWhereStatusStat;

    private static PreparedStatement deleteByIdStat;
    private static PreparedStatement deleteByAcStat;

    private static Statement stat;

    private static String tableName;

    /**
     * Constructor
     *
     * @throws BlastJdbcException
     */
    public BlastJobDao( File dbFolder, String tableName ) throws BlastJdbcException {
        BlastDb blastDb = new BlastDb( dbFolder );
        conn = blastDb.getConnection();
        try {
            stat = conn.createStatement();
            BlastJobDao.tableName = tableName;
            blastDb.createJobTable( conn, tableName );
            initPreparedStat();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }

    }

    public void close() throws BlastJdbcException {
        try {
            conn.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    // /////////////////
    // public Methods
    /**
     * Deletes all the entries in the JOB table.
     *
     * @throws BlastJdbcException
     */
    public void deleteJobs() throws BlastJdbcException {
        try {
            String delete = "DELETE FROM " + tableName + ";";
            stat.execute( delete );
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    public void deleteJob( BlastJobEntity job ) throws BlastJdbcException {
        if ( job == null ) {
            throw new NullPointerException( "Job must not be null!" );
        }
        deleteJobById( job.getJobid() );
    }

    public void deleteJobs( List<BlastJobEntity> jobs ) throws BlastJdbcException {
        if ( jobs == null ) {
            throw new NullPointerException( "Jobs list must not be null!" );
        }
        if ( jobs.size() == 0 ) {
            throw new IllegalArgumentException( "Jobs list must not be empty!" );
        }
        for ( BlastJobEntity jobEntity : jobs ) {
            deleteJob( jobEntity );
        }
    }

    /**
     * Deletes the JOB entry with the specified job id.
     *
     * @param jobid
     * @throws BlastJdbcException
     */
    public void deleteJobById( String jobid ) throws BlastJdbcException {
        if ( jobid == null ) {
            throw new NullPointerException( "Jobid must not be null!" );
        }
        try {
            deleteByIdStat.setString( 1, jobid );

            int result = deleteByIdStat.executeUpdate();
            if ( result != 1 && log.isWarnEnabled()) {
                log.warn( "Delete statement failed! " + deleteByIdStat );
            }
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    /**
     * Deletes the JOB entry with the specified uniprotAc.
     *
     * @param jobids of jobids
     * @throws BlastJdbcException
     */
    public void deleteJobsByIds( Set<String> jobids ) throws BlastJdbcException {
        if ( jobids == null ) {
            throw new NullPointerException( "Jobid list must not be null!" );
        }
        if ( jobids.size() == 0 ) {
            throw new IllegalArgumentException( "Jobid list must not be empty!" );
        }
        for ( String jobid : jobids ) {
            deleteJobById( jobid );
        }
    }

    /**
     * Deletes the JOB entry with the specified uniprotAc.
     *
     * @param uniprotAc
     * @throws BlastJdbcException
     */
    public void deleteJobByAc( UniprotAc uniprotAc ) throws BlastJdbcException {
        if ( uniprotAc == null ) {
            throw new NullPointerException( "UniprotAc must not be null!" );
        }
        if ( uniprotAc.getAcNr() == null ) {
            throw new NullPointerException( "UniprotAc.acNr must not be null!" );
        }
        try {
            deleteByAcStat.setString( 1, uniprotAc.getAcNr() );

            int result = deleteByAcStat.executeUpdate();
            if ( result != 1 && log.isWarnEnabled() ) {
                log.warn( "Delete statement failed! " + deleteByAcStat );
            }
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    /**
     * Deletes the JOB entries with the specified uniprotAcs.
     *
     * @param uniprotAcs of uniprotAcs
     * @throws BlastJdbcException
     */
    public void deleteJobsByAcs( Set<UniprotAc> uniprotAcs ) throws BlastJdbcException {
        if ( uniprotAcs == null ) {
            throw new NullPointerException( "UniprotAcs list must not be null!" );
        }
        if ( uniprotAcs.size() == 0 ) {
            throw new IllegalArgumentException( "UniprotAcs list must not be empty!" );
        }
        for ( UniprotAc uniprotAc : uniprotAcs ) {
            deleteJobByAc( uniprotAc );
        }
    }

    /**
     * Saves the specified jobEntity in the DB.
     *
     * @param job
     * @throws BlastJdbcException
     */
    public void saveJob( BlastJobEntity job ) throws BlastJdbcException {
        if ( job == null ) {
            throw new NullPointerException( "Job must not be null!" );
        }
        if ( job.getJobid() == null || job.getUniprotAc() == null || job.getTimestamp() == null ) {
            throw new IllegalArgumentException( "Job mut contain not null data, please check the data!" );
        }
        if ( existsInDb( new UniprotAc( job.getUniprotAc() ) ) ) {
            throw new IllegalArgumentException( "UniprotAc already in the Db!" );
        }
        try {
            insertStat.setString( 1, job.getJobid() );
            insertStat.setString( 2, job.getUniprotAc() );
            insertStat.setObject( 3, job.getSequence().getBytes());
            insertStat.setString( 4, job.getStatus().toString() );
            insertStat.setString( 5, job.getResultPath() );
            insertStat.setTimestamp( 6, job.getTimestamp() );
            insertStat.execute();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    /**
     * Saves the list of jobEntities to the DB.
     *
     * @param jobs
     * @throws BlastJdbcException
     */
    public void saveJobs( List<BlastJobEntity> jobs ) throws BlastJdbcException {
        if ( jobs == null ) {
            throw new NullPointerException( "Jobs-list must not be null!" );
        }
        if ( jobs.size() == 0 ) {
            throw new IllegalArgumentException( "Jobs list must not be empty!" );
        }
        for ( BlastJobEntity job : jobs ) {
            saveJob( job );
        }
    }

    public void updateJob( BlastJobEntity job ) throws BlastJdbcException {
        if ( job == null ) {
            throw new NullPointerException( "Job must not be null!" );
        }
        if ( job.getJobid() == null || job.getUniprotAc() == null || job.getResultPath() == null
             || job.getTimestamp() == null ) {
            throw new IllegalArgumentException( "Job mut contain not null data, please check the data!" );
        }
        if ( existsInDb( job.getJobid(), job.getUniprotAc() ) ) {
            try {
                updateJobStat.setObject( 1, job.getSequence().getBytes() );
                updateJobStat.setString( 2, job.getStatus().toString() );
                updateJobStat.setTimestamp( 3, job.getTimestamp() );
                updateJobStat.setString( 4, job.getResultPath() );
                updateJobStat.setString( 5, job.getJobid() );
                updateJobStat.setString( 6, job.getUniprotAc() );
                updateJobStat.execute();
            } catch ( SQLException e ) {
                throw new BlastJdbcException( e );
            }
        } else {
            throw new IllegalArgumentException( "Job not in the Db!" );
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
            ResultSet rs = stat.executeQuery( "SELECT * FROM " + tableName );

            while ( rs.next() ) {
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }
                BlastJobStatus status = getStatus( rs.getString( "status" ) );
                Blob seqBlob = rs.getBlob( "sequence" );
                String sequence = new String(seqBlob.getBytes( 1, (int) seqBlob.length() ));

                BlastJobEntity newJob = new BlastJobEntity( rs.getString( "jobid" ), rs.getString( "uniprotAc" ),
                        sequence, status, new File( path ), rs.getTimestamp( "timestamp" ) );
                jobs.add( newJob );
            }

            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
        return jobs;
    }

    public int countJobs() throws BlastJdbcException {
        int nr = -1;
        try {
            ResultSet rs = stat.executeQuery( "SELECT count(*) FROM " + tableName );
            while ( rs.next() ) {
                nr = rs.getInt( 1 );
            }
            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
        return nr;
    }

    public int countJobs( BlastJobStatus status ) throws BlastJdbcException {
        int nr = -1;
        try {
            ResultSet rs = stat.executeQuery( "SELECT count(*) FROM " + tableName + " WHERE status ='"
                                              + status.toString() + "'" );
            while ( rs.next() ) {
                nr = rs.getInt( 1 );
            }
            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
        return nr;
    }

    /**
     * Retrieves the job with the specified jobid
     *
     * @param jobid
     * @return BlastJobEntity, or null
     * @throws BlastJdbcException
     */
    public BlastJobEntity getJobById( String jobid ) throws BlastJdbcException {
        if ( jobid == null ) {
            throw new NullPointerException( "Jobid must not be null!" );
        }
        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
        try {
            selectWhereIdStat.setString( 1, jobid );

            ResultSet rs = selectWhereIdStat.executeQuery();
            while ( rs.next() ) {
                String jobId = rs.getString( "jobid" );
                String uniAc = rs.getString( "uniprotAc" );
                Blob seqBlob =  rs.getBlob( "sequence" );
                String seq = new String(seqBlob.getBytes( 1, (int)seqBlob.length() ));
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }

                BlastJobStatus status = getStatus( rs.getString( "status" ) );
                BlastJobEntity newJob = new BlastJobEntity( jobId, uniAc, seq, status, new File( path ), rs
                        .getTimestamp( "timestamp" ) );
                jobs.add( newJob );
            }

            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }

        if ( jobs.size() == 0 ) {
            return null;
        }
        if ( jobs.size() > 1 && log.isWarnEnabled()) {
            log.warn( "Make sure the jobId is unique key for the db !!!" );
        }
        return jobs.get( 0 );
    }

    public List<BlastJobEntity> getJobsByAc( Set<UniprotAc> uniprotAcs ) throws BlastJdbcException {
        if ( uniprotAcs == null ) {
            throw new NullPointerException( "UniprotAcs list must not be null!" );
        }
        if ( uniprotAcs.size() == 0 ) {
            throw new IllegalArgumentException( "UniprotAcs list must not be empty!" );
        }

        List<BlastJobEntity> results = new ArrayList<BlastJobEntity>( uniprotAcs.size() );

        for ( UniprotAc ac : uniprotAcs ) {
            BlastJobEntity job = getJobByAc( ac );
            if ( job != null ) {
                results.add( job );
            }
        }
        return results;
    }

    public List<BlastJobEntity> getJobsById( Set<String> jobids ) throws BlastJdbcException {
        if ( jobids == null ) {
            throw new NullPointerException( "Jobid list must not be null!" );
        }
        if ( jobids.size() == 0 ) {
            throw new IllegalArgumentException( "Jobid list must not be empty!" );
        }
        List<BlastJobEntity> results = new ArrayList<BlastJobEntity>();

        for ( String id : jobids ) {
            BlastJobEntity job = getJobById( id );
            if ( job != null ) {
                results.add( job );
            }
        }
        return results;
    }

    /**
     * Retrieves the job with the specified uniprotAc
     *
     * @param uniprotAc
     * @return BlastJobEntity, can be null
     * @throws BlastJdbcException
     */
    public BlastJobEntity getJobByAc( UniprotAc uniprotAc ) throws BlastJdbcException {
        if ( uniprotAc == null ) {
            throw new NullPointerException( "UniprotAc must not be null!" );
        }
        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>(2);
        try {
            selectWhereUniprotAcStat.setString( 1, uniprotAc.getAcNr() );

            ResultSet rs = selectWhereUniprotAcStat.executeQuery();
            while ( rs.next() ) {
                String jobId = rs.getString( "jobid" );
                String uniAc = rs.getString( "uniprotAc" );
                Blob seqBlob = rs.getBlob( "sequence" );
                String seq = new String(seqBlob.getBytes(1, (int)seqBlob.length() ));
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }

                BlastJobStatus status = getStatus( rs.getString( "status" ) );
                BlastJobEntity newJob = new BlastJobEntity( jobId, uniAc, seq, status, new File( path ), rs
                        .getTimestamp( "timestamp" ) );
                jobs.add( newJob );
                if (log.isTraceEnabled()){
                    log.trace( "Found uniprotAc in Blast Db: " + newJob );
                }
            }

            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }

        if ( jobs.size() == 0 ) {
            return null;
        }
        if ( jobs.size() > 1 && log.isWarnEnabled()) {
            log.warn( "Make sure the uniprotAc("+uniprotAc+") is unique for the DB, more than only one job found!!!" );
        }

        return jobs.get( 0 );
    }

    /**
     * to test
     *
     * @return list of wswublast job entities
     * @throws BlastJdbcException
     */
    public List<BlastJobEntity> getJobsByStatus( BlastJobStatus status ) throws BlastJdbcException {
        if ( status == null ) {
            throw new IllegalArgumentException( "Status must not be null!" );
        }
        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
        try {
            selectWhereStatusStat.setString( 1, status.toString() );
            ResultSet rs = selectWhereStatusStat.executeQuery();
            while ( rs.next() ) {
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }

                BlastJobStatus statusFromDb = getStatus( rs.getString( "status" ) );
                Blob seqBlob = rs.getBlob( "sequence" );
                String sequence = new String(seqBlob.getBytes( 1, (int)seqBlob.length() ));
                BlastJobEntity newJob = new BlastJobEntity( rs.getString( "jobid" ), rs.getString( "uniprotAc" ), 
                        sequence, statusFromDb, new File( path ), rs.getTimestamp( "timestamp" ) );
                jobs.add( newJob );
            }

            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }

        return jobs;
    }

    public List<BlastJobEntity> getNrJobsByStatus( int nr, BlastJobStatus status ) throws BlastJdbcException {
        if ( status == null ) {
            throw new IllegalArgumentException( "Status must not be null!" );
        }
        int totalJobs = countJobs( status );
        if ( log.isTraceEnabled() ) {
            log.trace( "totalNrJobs (" + status + "): " + totalJobs );
        }
        if ( totalJobs < nr ) {
            nr = totalJobs;
        }
        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>( nr );
        try {
            selectWhereStatusStat.setString( 1, status.toString() );
            ResultSet rs = selectWhereStatusStat.executeQuery();
            int i = 0;
            while ( rs.next() && i < nr ) {
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }

                BlastJobStatus statusFromDb = getStatus( rs.getString( "status" ) );
                Blob seqBlob = rs.getBlob( "sequence" );
                String sequence = new String(seqBlob.getBytes( 1, (int) seqBlob.length() ));
                BlastJobEntity newJob = new BlastJobEntity( rs.getString( "jobid" ), rs.getString( "uniprotAc" ),
                        sequence, statusFromDb, new File( path ), rs.getTimestamp( "timestamp" ) );
                jobs.add( newJob );
                i++;
            }

            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }

        return jobs;
    }

    /**
     * Retrieves the jobEntities with the specified Timestamp ('yyyy-mm-dd
     * hh:mm:ss')
     *
     * @param timestamp
     * @return list of jobEntities, never null, but can be empty
     * @throws BlastJdbcException
     */
    public List<BlastJobEntity> getJobsByTimestamp( Timestamp timestamp ) throws BlastJdbcException {
        if ( timestamp == null ) {
            throw new NullPointerException( "Timestamp must not be null!" );
        }
        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
        try {
            selectWhereTimestampStat.setTimestamp( 1, timestamp );

            ResultSet rs = selectWhereTimestampStat.executeQuery();
            while ( rs.next() ) {
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }

                BlastJobStatus status = getStatus( rs.getString( "status" ) );
                Blob seqBlob = rs.getBlob("sequence");
                String sequence = new String(seqBlob.getBytes( 1, (int) seqBlob.length() ));
                BlastJobEntity newJob = new BlastJobEntity( rs.getString( "jobid" ), rs.getString( "uniprotAc" ),
                        sequence, status, new File( path ), rs.getTimestamp( "timestamp" ) );
                jobs.add( newJob );
            }

            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }

        return jobs;
    }

    /**
     * Retrieves the jobEntity with the specified uniprotAc and the latest
     * submission date
     *
     * @param uniprotAc
     * @return a wswublast job entity, can be null
     * @throws BlastJdbcException
     */
    //TODO: do I really need this? if not, remove
    public BlastJobEntity getJobByAcAndLatestDate( String uniprotAc ) throws BlastJdbcException {
        if ( uniprotAc == null ) {
            throw new NullPointerException( "UniprotAc must not be null!" );
        }
        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
        try {
            selectWhereUniproAcLatestDateStat.setString( 1, uniprotAc );

            ResultSet rs = selectWhereUniproAcLatestDateStat.executeQuery();
            while ( rs.next() ) {
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }

                BlastJobStatus status = getStatus( rs.getString( "status" ) );
                Blob seqBlob = rs.getBlob( "sequence" );
                String sequence = new String (seqBlob.getBytes( 1, (int)seqBlob.length() ));
                BlastJobEntity newJob = new BlastJobEntity( rs.getString( "jobid" ), rs.getString( "uniprotAc" ),
                        sequence, status, new File( path ), rs.getTimestamp( "timestamp" ) );
                jobs.add( newJob );
            }

            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }

        if ( jobs.size() == 0 ) {
            return null;
        }
        if ( jobs.size() > 1 && log.isWarnEnabled()) {
            log.warn( "There are more than one jobs submitted on the same day with the same uniprotAc("+uniprotAc+")!!!" );
        }
        return jobs.get( 0 );
    }

    public void exportCSV( File csvFile ) throws BlastJdbcException {
        try {
            Writer w = new FileWriter( csvFile );
            ResultSet rs = stat.executeQuery( "CALL CSVWRITE('" + csvFile.getPath() + "', 'SELECT * FROM " + tableName
                                              + "');" );
            rs.close();
            w.close();             
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        } catch ( IOException e ) {
            throw new BlastJdbcException( e );
        }
    }

    public void importCSV( File csvFile ) throws BlastJdbcException {
        try {
            ResultSet rs = stat.executeQuery( "SELECT * FROM CSVREAD('" + csvFile.getPath() + "');" );
            // ResultSet rs = Csv.read("test.csv", null, null);
            ResultSetMetaData meta = rs.getMetaData();
            while ( rs.next() ) {
                String path = rs.getString( "resultPath" );
                if ( path == null ) {
                    path = "tmpAux";
                }

                Timestamp time = rs.getTimestamp( "timestamp" );

                BlastJobStatus status = getStatus( rs.getString( "status" ) );
                Blob seqBlob = rs.getBlob( "sequence" );
                String sequence = new String(seqBlob.getBytes( 1, (int) seqBlob.length() ));
                BlastJobEntity newJob = new BlastJobEntity( rs.getString( "jobid" ), rs.getString( "uniprotAc" ),
                                                            sequence, status, new File( path ), time );
                try {
                    saveJob( newJob );
                } catch ( IllegalArgumentException e ) {
                    String msg = e.getMessage();
                    if ( !msg.contains( "UniprotAc already in the Db!" ) ) {
                        throw e;
                    }
                }
            }
            rs.close();
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    // /////////////////
    // private Methods
    private void initPreparedStat() throws BlastJdbcException {
        try {
            String insertSql = "INSERT INTO " + tableName
                               + "(jobid, uniprotAc, sequence, status, resultPath, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
            insertStat = conn.prepareStatement( insertSql );
            String selectSql = "SELECT * FROM " + tableName + " WHERE jobid = ?";
            selectWhereIdStat = conn.prepareStatement( selectSql );
            String selectTimestampSql = "SELECT * FROM " + tableName + " WHERE timestamp = ?";
            selectWhereTimestampStat = conn.prepareStatement( selectTimestampSql );
            String selectAcSql = "SELECT * FROM " + tableName + " Where uniprotAc = ?";
            selectWhereUniprotAcStat = conn.prepareStatement( selectAcSql );
            String selectAcTimestampSql = "SELECT * FROM " + tableName
                                          + " WHERE timestamp = (SELECT MAX(timestamp) FROM " + tableName + " WHERE uniprotAc = ?)";
            selectWhereUniproAcLatestDateStat = conn.prepareStatement( selectAcTimestampSql );
            String selectPendingSql = "SELECt * FROM " + tableName + " WHERE status =?;";
            selectWhereStatusStat = conn.prepareStatement( selectPendingSql );

            String updateJobSql = "UPDATE " + tableName
                                  + " SET sequence= ?, status= ?, timestamp =?, resultPath =? WHERE jobid= ? AND uniprotAc=?;";
            updateJobStat = conn.prepareStatement( updateJobSql );

            String deleteByIdSql = "DELETE FROM " + tableName + " WHERE jobid = ?";
            deleteByIdStat = conn.prepareStatement( deleteByIdSql );

            String deleteByAcSql = "DELETE FROM " + tableName + " WHERE uniprotAc = ?";
            deleteByAcStat = conn.prepareStatement( deleteByAcSql );

        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    private BlastJobStatus getStatus( String status ) {
        if ( BlastJobStatus.RUNNING.toString().equals( status ) ) {
            return BlastJobStatus.RUNNING;
        }else if ( BlastJobStatus.FINISHED.toString().equals( status ) ) {
            return BlastJobStatus.FINISHED;
        } else if ( BlastJobStatus.FAILURE.toString().equals( status ) ) {
            return BlastJobStatus.FAILURE;
        } else if ( BlastJobStatus.NOT_FOUND.toString().equals( status ) ) {
            return BlastJobStatus.NOT_FOUND;
        }
        else if ( BlastJobStatus.ERROR.toString().equals( status ) ) {
            return BlastJobStatus.ERROR;
        }
        return null;
    }

    private boolean existsInDb( UniprotAc uniprotAc ) throws BlastJdbcException {
        BlastJobEntity job = getJobByAc( uniprotAc );
        return job != null;
    }

    private boolean existsInDb(String jobid, String uniprotAc) throws BlastJdbcException {
		BlastJobEntity job = getJobById(jobid);
        return job != null && job.getUniprotAc().equalsIgnoreCase(uniprotAc);
    }
}
