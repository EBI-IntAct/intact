/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.range;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.PolymerDao;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.sanity.check.MessageSender;
import uk.ac.ebi.intact.sanity.check.SanityCheckerHelper;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.model.RangeBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * This class has been done to automatically remapped the range sequence.
 * <pre>
 * -----------------------
 * E X P L A N A T I ON :
 * -----------------------
 * <p/>
 * What happens :
 * ---------------
 *   1 ==> a curator create a feature and define a range
 *   2 ==> automatically the range sequence is calculated from the protein sequence and the range fromIntervalStart and
 *         range fromCvFuzzyType
 *   3 ==> we decide to run a protein update which change some of the protein sequence
 * <p/>
 * Problem :
 * ---------
 *   the ranges corresponding to the proteins which sequences changed during the protein update won't correspond
 *   anymore to the actual protein sequence stored in the database.
 * <p/>
 * What we need to do :
 * --------------------
 * <p/>
 * We need when it's possible to remapp automatically those ranges sequences or reset the
 * fromIntervalStart, toIntervalStart, fromIntervalEnd, toIntervalEnd
 * OR
 * we need to tell the curators that the sequence couldn't be remapped automatically and that
 * therefore they will need to do it manually.
 * <p/>
 * When is it possible to remapp the range sequence automatically?
 * ---------------------------------------------------------------
 * <p/>
 * The automatic remapped of the sequence is possible only when the change on the protein sequence is just a remooving
 * or a re-adding of the first Methionine amino-acid.
 * <p/>
 * How are ranges sequences calculated and how do we automatically remapped?
 * ----------------------------------------------------------------------
 * <p/>
 * 1. If the cvFuzzyType is N-terminal(MI:0340 , EBI-448297) or undetermined (MI:0339 , EBI-448295) the range sequence
 *    will corresponds to the first 100 amino-acids of the protein sequence.
 * 2. If the cvFuzzyType is C-terminal (MI:0334 , EBI-448301) the range sequence will corresponds to the last 100
 *    amino-acids of the protein sequence.
 * 3. Otherwise it will be the 100 first amino-acid starting from FromIntervalStart in the protein sequence.
 * <p/>
 * In case 1. and 2. if the first M is added or remooved after a protein update we want to recalculate the range
 * sequence in case 3 we want reset the FromIntervalStart, ToIntervalStart, FromIntervalEnd, ToIntervalEnd in order
 * that
 * taking the new value of the range we can recalculate the range sequence and obtain the same sequence then the one
 * stored in the database.
 * <p/>
 * <p/>
 * How should I use this class?
 * -----------------------------
 * <p/>
 * Just instantiate an object RangeChecker and call the method check with as a parameter a collection of all the
 * protein
 * from which you want to check the range sequence.
 * RangeChecker rangeChecker = new RangeChecker();
 * rangeChecker.check(proteins); //Protein being a collection of Protein
 * <p/>
 * <p/>
 * If you want to check the entire database ranges call checkRangeEntireDatabase()
 * <p/>
 * What will the call of the method do?
 * ------------------------------------
 * <p/>
 * As we said it will remap when possible the range sequence but will as well create 3 reports files :
 *  1. mAdded.report
 *     this will contain all the information about the automatic remappings for the ranges in the following situation :
 *     The first M of the protein sequence was not there when the range was created but has been added since then
 *  2. mSupp.report
 *     this will contain all the information about the automatic remappings for the ranges in the following situation :
 *     The first M of the protein sequence was there when the range was created but has been suppressed since then
 *  3. notEqual.report
 *     this will contain all the information about the ranges that couldn't be remapped automatically.
 * <p/>
 * This method will as well send and email to all the concerned curators and administrator and this e-mail will look
 * like :
 * <p/>
 * Instance name: d003
 * <p/>
 * Could not find an email adress for user: intact
 * <p/>
 * This/these Range(s) are associated to a sequence which does not corresponds to the protein sequence. And the Range
 * Sequence couldn't be remapped automatically
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Interaction Ac     	Protein Ac     	Feature Ac     	ToIntervalStart     	FromIntervalEnd     	RangeBean Ac     	Date
 *    	User
 * EBI-39441     	EBI-3573     	EBI-611251     	0     	0     	EBI-611252     	2005-07-04 09:36:01.0     	INTACT
 * EBI-491985     	EBI-29160     	EBI-491987     	1     	1     	EBI-491988     	2005-02-08 16:24:03.0     	INTACT
 * <p/>
 * This/these Range(s) were created when the first Methionine was there, since then the Methionine had been remooved
 * from the Protein Sequence. The Range Sequence has been remapped.
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Interaction Ac     	Protein Ac     	Feature Ac     	ToIntervalStart     	FromIntervalEnd     	RangeBean Ac     	Date
 *    	User
 * EBI-491630     	EBI-28157     	EBI-491633     	221     	3     	EBI-491634     	2005-02-08 12:17:46.0     	INTACT
 * EBI-491644     	EBI-28157     	EBI-491647     	221     	3     	EBI-491648     	2005-02-08 12:25:28.0     	INTACT
 * <p/>
 *  This/these Range(s) were created when the first Methionine was not there, since then the Methionine had been added
 * to the Protein Sequence. The Range Sequence has been remapped.
 * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Interaction Ac     	Protein Ac     	Feature Ac     	ToIntervalStart     	FromIntervalEnd     	RangeBean Ac     	Date
 *    	User
 * EBI-591635     	EBI-350527     	EBI-591729     	0     	0     	EBI-591730     	2005-05-27 06:49:36.0     	INTACT
 * EBI-591635     	EBI-350527     	EBI-616473     	0     	0     	EBI-616474     	2005-07-04 09:37:57.0     	INTACT
 * </pre>
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: RangeChecker.java,v 1.3 2006/04/13 12:38:21 skerrien Exp $
 */
public class RangeChecker {

    private static final Log log = LogFactory.getLog( RangeChecker.class );

    int equal = 0;
    int mSuppCount = 0;
    int mAddedCount = 0;
    int notEqualCount = 0;

    // Buffers containing the checker's report.
    StringBuffer mAddedChangeReport = new StringBuffer( 1024 * 1024 );
    StringBuffer mSuppChangeReport = new StringBuffer( 1024 * 1024 );
    StringBuffer notEqualReport = new StringBuffer( 1024 * 1024 );

    Institution owner = new Institution( "EBI" );

//    MessageSender messageSender;

    /**
     * Unique instance of SanityCheckerHelper.
     */
    private SanityCheckerHelper sch = null;

    private BufferedWriter notEqual = null;
    private BufferedWriter mAdded = null;
    private BufferedWriter mSupp = null;

    public RangeChecker() { }

    public RangeChecker( SanityCheckConfig sanityConfig ) {
//        this.messageSender = new MessageSender(sanityConfig);
    }

    public RangeChecker( MessageSender messageSender ) {
//        this.messageSender = messageSender;
    }

    /**
     * Returns a list of Interactor AC that have at least one Range.
     *
     * @return a non null list of interactor.
     */
    public static List<String> loadProteinAcs() throws RangeCheckerException {
        List<String> acs = new ArrayList<String>();

        try {
            Connection connection = getJdbcConnection();
            Statement statement = null;
            ResultSet rs = null;
            try {
                statement = connection.createStatement();

                rs = statement.executeQuery(
                        "SELECT distinct p.ac\n" +
                        "FROM ia_interactor p, ia_component c, ia_feature f, ia_range r\n" +
                        "WHERE     p.objclass not like '%Interaction%'\n" +
                        "      AND p.ac = c.interactor_ac\n" +
                        "      AND c.ac = f.component_ac\n" +
                        "      AND r.feature_ac = f.ac" );

                while ( rs.next() ) {
                    acs.add( rs.getString( 1 ) );
                }
            } finally {
                if ( rs != null ) {
                    rs.close();
                }
                if ( statement != null ) {
                    statement.close();
                }
            }

            if ( log.isDebugEnabled() ) {
                log.debug( "Loaded " + acs.size() + " Polymer's AC(s) having at least 1 feature." );
            }
        } catch ( SQLException e ) {
            throw new RangeCheckerException( e );
        }

        return acs;
    }

    /**
     * @param proteins
     */
    public Collection<RangeReport> check( Collection<String> proteins ) throws RangeCheckerException {

        Collection<RangeReport> reports = null;
        initializeBuffers();

        if ( proteins == null ) {
            throw new IllegalArgumentException( "Proteins should not be null" );
        }

        for ( String ac : proteins ) {
            System.out.println( "Checking range for polymer AC: " + ac );
            Polymer polymer = getDaoFactory().getPolymerDao().getByAc( ac );
            reports = checkRange( polymer );
        }

        fillBuffers();
        closeBuffers();

        return reports;
    }

    public void checkRangeEntireDatabase() throws RangeCheckerException {
        int proteinCount;
        int CHUNK_SIZE = 5;

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        proteinCount = proteinDao.countAll();
        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch ( IntactTransactionException e ) {
            throw new RangeCheckerException( e );
        }

        int iterationCount = proteinCount / CHUNK_SIZE;
        for ( int i = 0; i <= iterationCount; i++ ) {
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
            Collection<ProteinImpl> proteins;

            proteins = proteinDao.getAll( ( i * CHUNK_SIZE ), CHUNK_SIZE );

            Collection<String> proteinAcs = new ArrayList<String>();
            for ( Protein protein : proteins ) {
                proteinAcs.add( protein.getAc() );
            }

            RangeChecker rangeChecker = new RangeChecker();
            rangeChecker.check( proteinAcs );
            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new RangeCheckerException( e );
            }
        }
    }

    //////////////////////
    // Private methods

    private void fillBuffers() throws RangeCheckerException {
        try {
            mAdded.write( mAddedChangeReport.toString() );
            mAdded.flush();
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not write to file : mAdded.report", e );
        }

        try {
            mSupp.write( mSuppChangeReport.toString() );
            mSupp.flush();
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not write to file : mSupp.report", e );
        }

        try {
            notEqual.write( notEqualReport.toString() );
            notEqual.flush();
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not write to file : notEqual.report", e );
        }
    }

    private void initializeBuffers() throws RangeCheckerException {
        try {
            mAdded = new BufferedWriter( new FileWriter( "mAdded.report", true ) );
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not initialize file : mAdded.report", e );
        }

        try {
            mSupp = new BufferedWriter( new FileWriter( "mSupp.report", true ) );
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not initialize file : mSupp.report", e );
        }

        try {
            FileWriter fileWriter = new FileWriter( "notEqual.report", true );
            notEqual = new BufferedWriter( fileWriter );

        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not initialize file : notEqual.report", e );
        }
    }

    private void closeBuffers() throws RangeCheckerException {
        try {
            mAdded.close();
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not close file : mAdded.report", e );
        }

        try {
            mSupp.close();
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not close file : mSupp.report", e );
        }

        try {
            notEqual.close();
        } catch ( IOException e ) {
            throw new RangeCheckerException( "Could not close file : notEqual.report", e );
        }
    }

    /**
     * This method return all the ranges related to a specific polymer
     *
     * @param polymer Protein for which you want to be return all the ranges.
     * @return a non null collection containing all the ranges associated to this polymer
     */
    private Collection<Range> getRangesFromPolymer( Polymer polymer ) throws RangeCheckerException {
        Collection<Range> allRanges = new ArrayList<Range>();
        for ( Component component : polymer.getActiveInstances() ) {
            Collection<Feature> features = component.getBindingDomains();
            for ( Feature feature : features ) {
                allRanges.addAll( feature.getRanges() );
            }
        }
        return allRanges;
    }

    /**
     * Having a range with a particular FromIntervalStart, FromCvFuzzyType and given a protein sequence this method will
     * return the range sequence we expect to found.
     *
     * @param proteinSeq The sequence of the protein the range given in parameter is related to
     * @param range      the range you want to get the expected range sequence
     * @return the expected range sequence
     */
    private String getExpectedRangeSequence( String proteinSeq, Range range ) {
        // We create an artificial range called seqCalculator which will just be used to calculate the sequence to
        // return using the method setSequence and getSequence of a range.
        Range seqCalculator = new Range( owner, range.getFromIntervalStart(), range.getToIntervalStart(), proteinSeq );
        seqCalculator.setFromCvFuzzyType( range.getFromCvFuzzyType() );
        seqCalculator.setToCvFuzzyType( range.getToCvFuzzyType() );
        seqCalculator.setSequence( proteinSeq );
        seqCalculator.prepareSequence( proteinSeq );
        return seqCalculator.getSequence();

    }

    /**
     * In this function we suppose that when the range was created the first Methionine was there but has been removed
     * since then from the protein sequence. So we calculate the range sequence after having re-add this methionine to
     * the protein sequence.
     *
     * @param proteinSeq
     * @param range
     * @return return the range sequence after having adding the first M to the protein sequence
     */
    private String getRangeSeqMSupp( String proteinSeq, Range range ) {
        // We create an artificial range called seqCalculator which will just be used to calculate the sequence to
        // return using the method setSequence and getSequence of a range.
        Range seqCalculator = new Range( owner, range.getFromIntervalStart(), range.getToIntervalStart(), "M" + proteinSeq );

        seqCalculator.setToCvFuzzyType( range.getToCvFuzzyType() );
        seqCalculator.setFromCvFuzzyType( range.getFromCvFuzzyType() );
        seqCalculator.setFromIntervalEnd( range.getFromIntervalEnd() );
        seqCalculator.setFromIntervalStart( range.getFromIntervalStart() );
        seqCalculator.setToIntervalEnd( range.getToIntervalEnd() );
        seqCalculator.setToIntervalStart( range.getToIntervalStart() );

        seqCalculator.prepareSequence( "M" + proteinSeq );

        return seqCalculator.getSequence();
    }

    /**
     * In this function we suppose that when the range was created the first Methione was not there but has been added
     * since then to the protein sequence.
     * <p/>
     * So we calculate the range sequence after having suppressed the first M assuming that it is a M methionine to the
     * protein sequence.
     *
     * @param proteinSeq
     * @param range
     * @return return the range sequence after having remooved the first amino-acid
     */
    private String getRangeSeqMadded( String proteinSeq, Range range, Institution owner ) {
        // We create an artificial range called seqCalculator which will just be used to calculate the sequence to
        // return using the method setSequence and getSequence of a range.
        Range seqCalculator = new Range( owner, range.getFromIntervalStart(), range.getToIntervalStart(), proteinSeq.substring( 1, proteinSeq.length() ) );

        seqCalculator.setToCvFuzzyType( range.getToCvFuzzyType() );
        seqCalculator.setFromCvFuzzyType( range.getFromCvFuzzyType() );
        seqCalculator.setFromIntervalEnd( range.getFromIntervalEnd() );
        seqCalculator.setFromIntervalStart( range.getFromIntervalStart() );
        seqCalculator.setToIntervalEnd( range.getToIntervalEnd() );
        seqCalculator.setToIntervalStart( range.getToIntervalStart() );

        seqCalculator.prepareSequence( proteinSeq.substring( 1, proteinSeq.length() ) );

        return seqCalculator.getSequence();
    }

    /**
     * Return the unique instance of the SanityCheckerHelper.
     *
     * @return an instance of SanityCherckerHelper.
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     */
    private SanityCheckerHelper getCheckerHelper() throws RangeCheckerException {
        if ( sch != null ) {
            // return cache
            return sch;
        }

        // build it
        sch = new SanityCheckerHelper();
        try {
            sch.addMapping( RangeBean.class, "select c.interactor_ac, c.interaction_ac, r.ac, r.updated, r.userstamp, " +
                                             "       r.feature_ac, r.fromintervalstart, r.fromintervalend, r.fromfuzzytype_ac, " +
                                             "       r.tofuzzytype_ac, r.fromintervalend, r.tointervalend, r.fromintervalstart, " +
                                             "       r.tointervalstart, r.created_user " +
                                             "from ia_range r, ia_feature f, ia_component c " +
                                             "where r.feature_ac = f.ac " +
                                             "      and f.component_ac = c.ac " +
                                             "      and r.ac = ?" );
        } catch ( SQLException e ) {
            throw new RangeCheckerException( e );
        }
        return sch;
    }

    private Collection<RangeReport> checkRange( Polymer polymer ) throws RangeCheckerException {

        Collection<RangeReport> reports = new ArrayList<RangeReport>();

        String polymerSequence = polymer.getSequence();

        if ( polymerSequence != null ) {

            Collection<Range> ranges = getRangesFromPolymer( polymer );
            for ( Iterator<Range> iterator = ranges.iterator(); iterator.hasNext(); ) {
                Range range = iterator.next();

                if ( log.isDebugEnabled() ) {
                    log.debug( "\tProcessing Range AC: " + range.getAc() );
                }
                String rangeSeqStored = range.getSequence();

                if ( rangeSeqStored != null ) {
                    String expectedRangeSeq = getExpectedRangeSequence( polymerSequence, range );

                    if ( expectedRangeSeq == null ) {

                        // TODO why would it be null ??

                        notEqualCount++;

//                        SanityCheckerHelper sch = getCheckerHelper();
//                        RangeBean rangeBean = sch.getBeans( RangeBean.class, range.getAc() ).get( 0 );

                        RangeReport report = new RangeReport( RangeReportType.NO_RANGE_SEQUENCE, polymer, range, expectedRangeSeq );
                        report.setMessage( "The range stored is different from the one one can generate based on the current polymer's sequence." );
                        reports.add( report );

                        notEqualReport.append( "\n\nProtein Ac: " ).append( polymer.getAc() );
                        notEqualReport.append( "\tRange Ac:" ).append( range.getAc() );
                        notEqualReport.append( "\nRange seq stored: " ).append( rangeSeqStored );
                        notEqualReport.append( "\nRange seq expect: " ).append( expectedRangeSeq ).append( "\n\n" );

                    } else if ( expectedRangeSeq.equals( rangeSeqStored ) ) {

                        log.debug( "\t...Range sequence stored and re-generated are equal." );
                        equal++;

                    } else {

                        // expected range is not null and different from stored range sequence
                        int fromIntervalStart = range.getFromIntervalStart();
                        int fromIntervalEnd = range.getFromIntervalEnd();
                        int toIntervalStart = range.getToIntervalStart();
                        int toIntervalEnd = range.getToIntervalEnd();
                        CvFuzzyType fromCvFuzzyType = range.getFromCvFuzzyType();

                        // Here we suppose that the polymer sequence had it's first methionine when the
                        // range was created but was later removed during a polymer update.
                        String mSupp = getRangeSeqMSupp( polymerSequence, range );
                        if ( log.isDebugEnabled() ) {
                            log.debug( "Supposing that the leading Methionine was removed from the polymer sequence, the corresponding range's stored sequence would be: " + mSupp );
                        }

                        if ( rangeSeqStored.equals( mSupp ) ) {
                            if ( log.isDebugEnabled() ) {
                                log.debug( "Range expected and store are equal: Methionine was indeed removed." );
                            }
//                            SanityCheckerHelper sch = getCheckerHelper();
//                            RangeBean rangeBean = ( RangeBean ) sch.getBeans( RangeBean.class, range.getAc() ).get( 0 );

                            RangeReport report = new RangeReport( RangeReportType.METHIONINE_REMOVED, polymer, range, expectedRangeSeq );
                            report.setMessage( "Correction of the Range boundaries from " + getRangeBoundaries( range ) );

                            mSuppCount++;

                            mSuppChangeReport.append( "\n\nProtein Ac: " ).append( polymer.getAc() );
                            mSuppChangeReport.append( "\tRange Ac:" ).append( range.getAc() );
                            mSuppChangeReport.append( "\tRange from fuzzy type: " ).append( range.getFromCvFuzzyType() );
                            mSuppChangeReport.append( "\tMadded" );

                            if ( ( fromCvFuzzyType != null && !( fromCvFuzzyType.isCTerminal() || fromCvFuzzyType.isNTerminal() || fromCvFuzzyType.isUndetermined() ) ) || fromCvFuzzyType == null ) {
                                if ( fromIntervalStart != 0 ) {
                                    range.setFromIntervalStart( fromIntervalStart - 1 );
                                    mSuppChangeReport.append( "\ndecrement fromIntervalStart by 1: " ).append( fromIntervalStart );
                                }
                                if ( fromIntervalEnd != 0 ) {
                                    range.setFromIntervalEnd( fromIntervalEnd - 1 );
                                    mSuppChangeReport.append( "\tdecrement fromIntervalEnd by 1: " ).append( fromIntervalEnd );
                                }
                                if ( toIntervalStart != 0 ) {
                                    range.setToIntervalStart( toIntervalStart - 1 );
                                    mSuppChangeReport.append( "\tdecrement toIntervalStart by 1: " ).append( toIntervalStart );
                                }
                                if ( toIntervalEnd != 0 ) {
                                    range.setToIntervalEnd( toIntervalEnd - 1 );
                                    mSuppChangeReport.append( "\tdecrement toIntervalEnd by 1: " ).append( toIntervalEnd );
                                }
                            }

                            report.setMessage( report.getMessage() + " to " + getRangeBoundaries( range ) );

                            range.setSequence( range.prepareSequence( polymerSequence ) );
                            getDaoFactory().getRangeDao().update( range );

                            report.setRangeSeqUpdated( range.getSequence() );
                            report.setRemappingSucceeded( true );
                            reports.add( report );

                            mSuppChangeReport.append( "\nPrevious range: " ).append( rangeSeqStored );
                            mSuppChangeReport.append( "\nNew range     : " ).append( range.getSequence() ).append( "\n\n" );

                        } else {

                            // the check assuming that a methionine might have been removed failed.

                            // Here we suppose that the polymer sequence hadn't it's first methionine
                            // when the range was created but was later added during a polymer update.

                            String mAdded = getRangeSeqMadded( polymerSequence, range, owner );

                            if ( rangeSeqStored.equals( mAdded ) ) {

                                if ( log.isDebugEnabled() ) {
                                    log.debug( "Range expected and store are equal: Methionine was indeed added." );
                                }

                                RangeReport report = new RangeReport( RangeReportType.METHIONINE_ADDED, polymer, range, expectedRangeSeq );
                                report.setMessage( "Correction of the Range boundaries from " + getRangeBoundaries( range ) );

//                                SanityCheckerHelper sch = getCheckerHelper();
//                                RangeBean rangeBean = ( RangeBean ) sch.getBeans( RangeBean.class, range.getAc() ).get( 0 );

                                mAddedCount++;

                                mAddedChangeReport.append( "\n\nProtein Ac: " ).append( polymer.getAc() );
                                mAddedChangeReport.append( "\tRange Ac:" ).append( range.getAc() );
                                mAddedChangeReport.append( "\tRange from fuzzy type: " ).append( range.getFromCvFuzzyType() );
                                mAddedChangeReport.append( "\tMsup" );

                                //if fromCvFyzzyType == null ? Search if is exists in database
                                // Check that is fromCvFuzzyType == undetermined, fromIntervalStart... == 0

                                if ( ( fromCvFuzzyType != null &&
                                       !( fromCvFuzzyType.isCTerminal() || fromCvFuzzyType.isNTerminal() || fromCvFuzzyType.isUndetermined() ) ) || fromCvFuzzyType == null ) {
                                    mAddedChangeReport.append( "\n+1 fis: " ).append( fromIntervalStart );
                                    mAddedChangeReport.append( "\t fie: " ).append( fromIntervalEnd );
                                    mAddedChangeReport.append( "\t tis: " ).append( toIntervalStart );
                                    mAddedChangeReport.append( "\t tie: " ).append( toIntervalEnd );

                                    range.setFromIntervalStart( fromIntervalStart + 1 );
                                    range.setFromIntervalEnd( fromIntervalEnd + 1 );
                                    range.setToIntervalStart( toIntervalStart + 1 );
                                    range.setToIntervalEnd( toIntervalEnd + 1 );
                                }

                                range.setSequence( range.prepareSequence( polymerSequence ) );
                                getDaoFactory().getRangeDao().update( range );

                                report.setMessage( report.getMessage() + " to " + getRangeBoundaries( range ) );
                                report.setRangeSeqUpdated( range.getSequence() );
                                report.setRemappingSucceeded( true );
                                reports.add( report );

                                mAddedChangeReport.append( "\nPrevious range: " ).append( rangeSeqStored );
                                mAddedChangeReport.append( "\nNew range     : " ).append( range.getSequence() ).append( "\n" );

                            } else {

                                // TODO perform a local alignement and see if the feature can be remapped automatically
                                RangeReport report = new RangeReport( RangeReportType.RANGE_REMAPPING, polymer, range, expectedRangeSeq );
                                report.setMessage( "Correction of the Range boundaries from " + getRangeBoundaries( range ) );

                                // TODO check polymer is a protein 

                                if ( range.getFromCvFuzzyType() == null &&
                                     range.getToCvFuzzyType() == null &&
                                     range.getFromIntervalStart() != 0 &&
                                     range.getFromIntervalEnd() != 0 &&
                                     range.getToIntervalStart() != 0 &&
                                     range.getToIntervalEnd() != 0 ) {

                                    if ( range.getFromIntervalStart() == range.getFromIntervalEnd() &&
                                         range.getToIntervalStart() == range.getToIntervalEnd() ) {

                                        int start = range.getFromIntervalStart() - 1; // in Java Strings are indexed from 0
                                        int end = range.getToIntervalStart() - 1;

                                        if ( log.isDebugEnabled() ) {
                                            log.debug( "Range ["+(start + 1)+".."+(end + 1)+"]" );
                                        }

                                        //String formerSequence = getPreviousPolymerSequence( polymer );

                                        // Retreive the exact feature's sequence
                                        String rangeSequence;
                                        final int rangeLength = end - start + 1;
                                        if ( log.isDebugEnabled() ) log.debug( "Range (length:" + rangeLength );

                                        if ( rangeLength > Range.getMaxSequenceSize() ) {
                                            // max length of a range internal sequence is 100, so we only get part of the thing
                                            rangeSequence = range.getSequence();

                                            if ( log.isWarnEnabled() ) {
                                                log.warn( "Only the first " + Range.getMaxSequenceSize() +
                                                          "AA of the full range sequence was stored in the database" );

                                                // TODO do not remap but prepare a different message to the curator !!
                                            }

                                        } else {
                                            rangeSequence = range.getSequence().substring( 0, rangeLength );
                                        }

                                        if ( log.isDebugEnabled() ) log.debug( "Range sequence: " + rangeSequence );

                                        // Check that the current range did align to the former polymer's sequence
//                                        boolean rangeAlignFormerSequence = false;
//                                        List<Integer> matches = SequenceAlignementUtils.findExactMatches( formerSequence, rangeSequence );
//                                        if ( matches.isEmpty() ) {
//
//                                            if ( log.isDebugEnabled() )
//                                                log.debug( "Could not find a match between the previous protein sequence and the current range" );
//
//                                        } else {
//                                            boolean foundMatch = false;
//
//                                            if ( log.isDebugEnabled() )
//                                                log.debug( "Found " + matches.size() + " matching occurence(s) of the range in the sequence" );
//
//                                            for ( Integer matchBegin : matches ) {
//                                                final int matchEnd = matchBegin + rangeSequence.length();
//
//                                                if ( matchBegin == start && matchEnd == end ) {
//                                                    foundMatch = true;
//                                                    rangeAlignFormerSequence = true;
//                                                }
//                                            }
//
//                                            if ( foundMatch ) {
//                                                if ( log.isDebugEnabled() )
//                                                    log.debug( "Found a match between the previous protein sequence and the current range" );
//                                            } else {
//
//                                            }
//                                        }

                                        // if no, iterate over older sequence

                                        // if yes, align on the new sequence
//                                        if ( rangeAlignFormerSequence ) {
                                        List<Integer> matches = SequenceAlignementUtils.findExactMatches( polymerSequence, rangeSequence );

                                        if ( matches.isEmpty() ) {

                                            if ( log.isDebugEnabled() )
                                                log.debug( "Could not find a match between the current polymer's sequence and the range" );

                                        } else {

                                            if ( log.isDebugEnabled() )
                                                log.debug( "Found " + matches.size() + " matching occurence(s) of the range in the sequence" );

                                            if ( matches.size() == 1 ) {
                                                int matchBegin = matches.iterator().next();
                                                int matchEnd = matchBegin + rangeSequence.length();

                                                // updating the range
                                                range.setFromIntervalStart( matchBegin );
                                                range.setFromIntervalEnd( matchBegin );
                                                range.setToIntervalStart( matchEnd );
                                                range.setToIntervalEnd( matchEnd );

                                            } else {
                                                log.debug( "Found " + matches.size() + " matching occurence(s) of the range in the polymer's sequence, send report to curator" );
                                                int i = 1;
                                                for ( Integer match : matches ) {
                                                    String align = SequenceAlignementUtils.buildMatch( match, polymerSequence, rangeSequence );
                                                    log.debug( " Match  " + i + ":\n" + align + "\n\n" );
                                                }
                                            }
                                        }
//                                        }

                                        // if a single match, update the range and create a report.
                                        // location of the match on the older sequence will allow to calculate the
                                        // difference and then the adjustement on the range.

                                    } else {
                                        if ( log.isInfoEnabled() ) {
                                            log.info( "This range (" + range.getAc() + ") has either start or end position like [x..y] with x != y" );
                                        }
                                    }
                                } else {
                                    if ( log.isInfoEnabled() ) {
                                        log.info( "This range (" + range.getAc() + ") has either start or end equals to 0 or a fuzzy type set" );
                                    }
                                }


                                System.out.println( "\t...Could not automatically set range" );
                                notEqualCount++;

//                                SanityCheckerHelper sch = getCheckerHelper();
//                                RangeBean rangeBean = sch.getBeans( RangeBean.class, range.getAc() ).get( 0 );

                                notEqualReport.append( "\n\nProtein Ac: " ).append( polymer.getAc() );
                                notEqualReport.append( "\tRange Ac:" ).append( range.getAc() );
                                notEqualReport.append( "\nRange seq stored: " ).append( rangeSeqStored );
                                notEqualReport.append( "\nRange seq expect: " ).append( expectedRangeSeq ).append( "\n\n" );
                            }
                            mAdded = null;
                        }
                        mSupp = null;
                        fromCvFuzzyType = null;
                    }

                } else {

                    // no sequence in the range.
                    if ( polymerSequence != null && polymerSequence.length() > 0 ) {
                        // if the polymer has a sequence, set that missing sequence on the range.
                        log.error( "Polymer '" + polymer.getShortLabel() + "' (" + polymer.getAc() + ")" +
                                   " has a range (" + range.getAc() + ") without sequence! Fixing it now..." );
                        range.setSequence( range.prepareSequence( polymerSequence ) );
                        String s = range.getSequence();
                        log.error( "s = " + s );
                        if ( s != null && s.length() > 0 ) {
                            log.error( "\t...range seq was null but was added." );
                            getDaoFactory().getRangeDao().saveOrUpdate( range );
                        } else {
                            log.error( "\t...range seq was null and could not be calculated." );
                            log.error( "\t...WARNING - Could not generate a sequence for that range " + range.getAc() );
                        }
                    }
                }

                range = null;
                rangeSeqStored = null;
            }
            ranges.clear();

        } else {

            // sequence is null in the polymer. Let's check if there's a range defined on it.

        }
        polymerSequence = null;

        return reports;
    }

    private class AuditSequenceChunk implements Comparable<AuditSequenceChunk> {
        char event;
        Date updated;
        Date created;
        short sequenceIndex;
        String chunk;

        private AuditSequenceChunk( char event, Date updated, Date created, short sequenceIndex, String chunk ) {
            this.event = event;
            this.updated = updated;
            this.created = created;
            this.sequenceIndex = sequenceIndex;
            this.chunk = chunk;
        }

        public char getEvent() {
            return event;
        }

        public Date getUpdated() {
            return updated;
        }

        public Date getCreated() {
            return created;
        }

        public short getSequenceIndex() {
            return sequenceIndex;
        }

        public String getChunk() {
            return chunk;
        }

        public int compareTo( AuditSequenceChunk sc ) {

            // NULL data are older than anything else (they should be last in a list)
            if ( sc.getUpdated() == null ) {
                return Integer.MIN_VALUE;
            }

            if ( this.getUpdated() == null ) {
                return Integer.MAX_VALUE;
            }

            // sort by decreasing update date
            return sc.getUpdated().compareTo( this.getUpdated() );
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append( event );
            sb.append( " | " ).append( updated );
            sb.append( " | " ).append( created );
            sb.append( " | " ).append( sequenceIndex );
            sb.append( " | " ).append( chunk.substring( 0, 5 ) ).append( "..." );
            return sb.toString();
        }
    }

    private String getPreviousPolymerSequence( Polymer polymer ) throws RangeCheckerException {

        // TODO This should go to the PolymerDao (though it relies on audit tables !!)
        List<AuditSequenceChunk> chunks = new ArrayList<AuditSequenceChunk>();

        try {
            Connection connection = getJdbcConnection();
            Statement statement = null;
            ResultSet rs = null;
            try {
                statement = connection.createStatement();

                rs = statement.executeQuery(
                        "SELECT event, updated, created, sequence_index, sequence_chunk\n" +
                        "from ia_sequence_chunk_audit\n" +
                        "where parent_ac = '" + polymer.getAc() + "'\n" +
                        "      and event = 'U'\n" +
                        "order by updated, sequence_index" );

                while ( rs.next() ) {
                    AuditSequenceChunk sc = new AuditSequenceChunk( rs.getString( 1 ).charAt( 0 ),
                                                                    rs.getDate( 2 ),
                                                                    rs.getDate( 3 ),
                                                                    rs.getShort( 4 ),
                                                                    rs.getString( 5 ) );
                    chunks.add( sc );
                }

                Collections.sort( chunks );


            } finally {
                if ( rs != null ) {
                    rs.close();
                }
                if ( statement != null ) {
                    statement.close();
                }
            }

        } catch ( SQLException e ) {
            throw new RangeCheckerException( e );
        }

        // extract the sequence

        // 1. iterate over chunks and append until the date changes.
        StringBuilder sb = new StringBuilder();

        if ( !chunks.isEmpty() ) {
            Date curDate = chunks.get( 0 ).getUpdated();
            for ( AuditSequenceChunk chunk : chunks ) {
                if ( !curDate.equals( chunk.getUpdated() ) ) {
                    break;
                }
                System.out.println( "Appending chunk #" + chunk.getSequenceIndex() );
                sb.append( chunk.getChunk() );
            }
        } else {
            return null;
        }

        return sb.toString();
    }

    private String getRangeBoundaries( Range range ) {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );
        sb.append( range.getFromIntervalStart() );
        sb.append( ".." );
        sb.append( range.getFromIntervalEnd() );
        sb.append( " -> " );
        sb.append( range.getToIntervalStart() );
        sb.append( ".." );
        sb.append( range.getToIntervalEnd() );
        sb.append( ']' );
        return sb.toString();
    }

    private static Connection getJdbcConnection() {
        return getDaoFactory().connection();
    }

    private static DaoFactory getDaoFactory() {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    //////////////////////
    // D E M O

    public static void main( String[] args ) throws RangeCheckerException, IntactTransactionException, AlignementException {
        IntactContext.initStandaloneContext( new File( "C:/intact-cfg/iweb-hibernate.cfg.xml" ) );
        final PolymerDao<PolymerImpl> dao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getPolymerDao();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        final PolymerImpl polymer = dao.getByAc( "EBI-141" );
        RangeChecker rc = new RangeChecker();
        final String seq = rc.getPreviousPolymerSequence( polymer );
        String current = polymer.getSequence();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        System.out.println( "Seq: " + seq );

        // run a Smith-Waterman between them
        String out = SequenceAlignementUtils.alignSmithWaterman( current, "current", seq, "former" );
        System.out.println( out );
    }
}