/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.range;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.sanity.check.MessageSender;
import uk.ac.ebi.intact.sanity.check.ReportTopic;
import uk.ac.ebi.intact.sanity.check.SanityCheckerHelper;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.model.RangeBean;

import javax.mail.MessagingException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
 * or
 * or a re-adding the first Methionine amino-acid.
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
public class RangeChecker
{

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



    public RangeChecker(){

    }

    public RangeChecker( SanityCheckConfig sanityConfig )
    {
//        this.messageSender = new MessageSender(sanityConfig);
    }

    public RangeChecker( MessageSender messageSender )
    {
//        this.messageSender = messageSender;
    }

    /**
     * Returns a list of Interactor AC that have at least one Range.
     *
     * @return a non null list of interactor.
     */
    public static List<String> loadProteinAcs( ) throws IntactException, SQLException {
        List<String> acs = new ArrayList<String>();

        Connection connection = getJdbcConnection();
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();

            rs = statement.executeQuery( "SELECT distinct p.ac\n" +
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

        return acs;
    }

    /**
     * @param proteins
     */
    public void check( Collection<String> proteins ) throws IntactException, SQLException, IOException {
        initializeBuffers();

        if ( proteins == null ) {
            throw new IllegalArgumentException( "The parameter 'protein' should not be null" );
        }

        int count = 0;
        for (String ac : proteins)
        {
            System.out.println("Check range for protein ac : " + ac);
            Polymer polymer = getDaoFactory().getPolymerDao().getByAc(ac);
//            if(polymer!= null){
//                System.out.println("\t...protein loaded");
//            }
            checkRange(polymer);

            count++;

//            if ((count % 10) == 0)
//            {
//
//                System.out.print(".");
//                System.out.flush();
//
//                if ((count % 500) == 0)
//                {
//                    System.out.println("\t" + count);
//                }
//            }
        }

        fillBuffers();
        
        closeBuffers();

//        try {
//            messageSender.postEmails( "RANGE CHECKER" );
//
//        } catch ( MessagingException e ) {
//            System.out.println( "We failed to send reports by email. They were saved in local files (check *.report)." );
//        }
    }

    private void fillBuffers() throws IOException {
        try {
            mAdded.write( mAddedChangeReport.toString() );
            mAdded.flush();
        } catch ( IOException e ) {
            throw new IOException("Could not write to file : mAdded.report");
        }

        try {
            mSupp.write( mSuppChangeReport.toString() );
            mSupp.flush();
        } catch ( IOException e ) {
            throw new IOException("Could not write to file : mSupp.report");
        }

        try {
            notEqual.write( notEqualReport.toString() );
            notEqual.flush();
        } catch ( IOException e ) {
            throw new IOException("Could not write to file : notEqual.report");
        }
    }

    private void initializeBuffers() throws IOException {
        try {
           mAdded = new BufferedWriter( new FileWriter( "mAdded.report",true ) );
        } catch ( IOException e ) {
            throw new IOException("Could not initialize file : mAdded.report");
        }

        try {
            mSupp = new BufferedWriter( new FileWriter( "mSupp.report",true ) );
        } catch ( IOException e ) {
            throw new IOException("Could not initialize file : mSupp.report");
        }

        try {
            FileWriter fileWriter = new FileWriter( "notEqual.report",true );
//            System.out.println("fileWriter. = " + fileWriter.);
            notEqual = new BufferedWriter( fileWriter );

        } catch ( IOException e ) {
            throw new IOException("Could not initialize file : notEqual.report");
        }
    }

    private void closeBuffers() throws IOException {
        try {
           mAdded.close();
        } catch ( IOException e ) {
            throw new IOException("Could not close file : mAdded.report");
        }

        try {
            mSupp.close();
        } catch ( IOException e ) {
            throw new IOException("Could not close file : mSupp.report");
        }

        try {
            notEqual.close();
        } catch ( IOException e ) {
            throw new IOException("Could not close file : notEqual.report");
        }
    }

    /**
     * This method return all the ranges related to a specific polymer
     *
     * @param polymer      Protein for which you want to be return all the ranges.
     *
     * @return a collection containing all the ranges associated to this polymer
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     */
    private Collection getRangesFromPolymer( Polymer polymer ) throws IntactException {
        //System.out.println("RangeChecker.getRangesFromProtein");
        Collection allRanges = new ArrayList();

        Collection components = getDaoFactory().getComponentDao().getByInteractorAc( polymer.getAc() );
        for ( Iterator iterator = components.iterator(); iterator.hasNext(); ) {
            Component component = (Component) iterator.next();
            Collection features = component.getBindingDomains();
            for ( Iterator iterator1 = features.iterator(); iterator1.hasNext(); ) {
                Feature feature = (Feature) iterator1.next();
                Collection ranges = feature.getRanges();
                allRanges.addAll( ranges );
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
     *
     * @return the expected range sequence
     */
    private String getExpectedRangeSequence( String proteinSeq, Range range ) {
        // We create an artificial range called seqCalculator which will just be used to calculate the sequence to
        // return using the method setSequence and getSequence of a range.
        Range seqCalculator = new Range( owner, range.getFromIntervalStart(), range.getToIntervalStart(), proteinSeq );
        seqCalculator.setFromCvFuzzyType( range.getFromCvFuzzyType() );
        seqCalculator.setToCvFuzzyType(range.getToCvFuzzyType());
        seqCalculator.setSequence( proteinSeq );
        seqCalculator.prepareSequence(proteinSeq);
        return seqCalculator.getSequence();

    }

    /**
     * In this function we suppose that when the range was created the first Methione was there but has been remooved
     * since then from the protein sequence. So we calculate the range sequence after having re-add this methionine to
     * the protein sequence.
     *
     * @param proteinSeq
     * @param range
     *
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

        seqCalculator.setSequence( "M" + proteinSeq );

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
     *
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


        seqCalculator.setSequence( proteinSeq.substring( 1, proteinSeq.length() ) );

        return seqCalculator.getSequence();
    }

    /**
     * Return the unique instance of the SanityCheckerHelper.
     *
     * @return an instance of SanityCherckerHelper.
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     * @throws java.sql.SQLException
     */
    private SanityCheckerHelper getCheckerHelper() throws IntactException, SQLException {
        if ( sch != null ) {
            // return cache
            return sch;
        }

        // build it
        sch = new SanityCheckerHelper();
        sch.addMapping(RangeBean.class, "select c.interactor_ac, c.interaction_ac, r.ac, r.updated, r.userstamp, " +
                "       r.feature_ac, r.fromintervalstart, r.fromintervalend, r.fromfuzzytype_ac, " +
                "       r.tofuzzytype_ac, r.fromintervalend, r.tointervalend, r.fromintervalstart, " +
                "       r.tointervalstart, r.created_user " +
                "from ia_range r, ia_feature f, ia_component c " +
                "where r.feature_ac = f.ac " +
                "      and f.component_ac = c.ac " +
                "      and r.ac = ?" );
        return sch;
    }

    private void checkRange( Polymer polymer ) throws IntactException, SQLException {

        String polymerSequence = polymer.getSequence();

        if ( polymerSequence != null ) {
            Collection ranges = getRangesFromPolymer( polymer );
            for ( Iterator iterator = ranges.iterator(); iterator.hasNext(); ) {
                Range range = (Range) iterator.next();
                System.out.println("\trange.getAc() = " + range.getAc());
                String rangeSeqStored = range.getSequence();
//                System.out.println("\trangeSeqStored = " + rangeSeqStored);
                if ( rangeSeqStored != null ) {
                    String expectedRangeSeq = getExpectedRangeSequence( polymerSequence, range );
//                    System.out.println("\texpectedRangeSeq = " + expectedRangeSeq);
                    if(expectedRangeSeq == null){
//                        System.out.println("\t...Could not automatically set range");
                        notEqualCount++;

                        SanityCheckerHelper sch = getCheckerHelper();
                        RangeBean rangeBean = sch.getBeans(RangeBean.class, range.getAc() ).get( 0 );
//                                messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_NOT_EQUAL_TO_PROTEIN_SEQ, rangeBean );

                        notEqualReport.append( "\n\nProtein Ac: " ).append( polymer.getAc() );
                        notEqualReport.append( "\tRange Ac:" ).append( range.getAc() );
                        notEqualReport.append( "\nRange seq stored: " ).append( rangeSeqStored );
                        notEqualReport.append( "\nRange seq expect: " ).append( expectedRangeSeq ).append( "\n\n" );

                    }else if ( expectedRangeSeq.equals( rangeSeqStored ) ) {
                        System.out.println("\t...Sequence are equal.");
                        equal++;
                    } else {
                        int fromIntervalStart = range.getFromIntervalStart();
                        int fromIntervalEnd = range.getFromIntervalEnd();
                        int toIntervalStart = range.getToIntervalStart();
                        int toIntervalEnd = range.getToIntervalEnd();
                        CvFuzzyType fromCvFuzzyType = range.getFromCvFuzzyType();

                        // Here we suppose that the polymer sequence had it's first methionine when the range was created
                        // but was later removed during a polymer update.
                        String mSupp = getRangeSeqMSupp( polymerSequence, range );

                        if ( rangeSeqStored.equals( mSupp ) ) {
                            System.out.println("\t...an M was suppressed");

                            SanityCheckerHelper sch = getCheckerHelper( );
                            RangeBean rangeBean = (RangeBean) sch.getBeans(RangeBean.class, range.getAc() ).get( 0 );
//                            messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_SAVED_BY_SUPPRESSING_THE_M, rangeBean );

                            mSuppCount++;
                            mSuppChangeReport.append( "\n\nProtein Ac: " ).append( polymer.getAc() );
                            mSuppChangeReport.append( "\tRange Ac:" ).append( range.getAc() );
                            mSuppChangeReport.append( "\tRange from fuzzy type: " ).append( range.getFromCvFuzzyType() );
                            mSuppChangeReport.append( "\tMadded" );

                            //M A L D KJGJDKKSK
                            //1 2 3 4
//                            System.out.println( "mSuppCount = " + mSuppCount );
                            if ( ( fromCvFuzzyType != null && !( fromCvFuzzyType.isCTerminal() || fromCvFuzzyType.isNTerminal() || fromCvFuzzyType.isUndetermined() ) ) || fromCvFuzzyType == null )
                            {
                                if ( fromIntervalStart != 0 ) {
//                                    System.out.println("\tfromIntervalStart = " + (fromIntervalStart-1));
                                    range.setFromIntervalStart( fromIntervalStart - 1 );
                                    mSuppChangeReport.append( "\n-1 fis: " ).append( fromIntervalStart );
                                }
                                if ( fromIntervalEnd != 0 ) {
//                                    System.out.println("\tfromIntervalEnd = " + (fromIntervalEnd-1));
                                    range.setFromIntervalEnd( fromIntervalEnd - 1 );
                                    mSuppChangeReport.append( "\t-1 fie: " ).append( fromIntervalEnd );
                                }
                                if ( toIntervalStart != 0 ) {
//                                    System.out.println("\ttoIntervalStart = " + (toIntervalStart-1));
                                    range.setToIntervalStart( toIntervalStart - 1 );
                                    mSuppChangeReport.append( "\t-1 tis: " ).append( toIntervalStart );
                                }
                                if ( toIntervalEnd != 0 ) {
//                                    System.out.println("\ttoIntervalEnd = " + (toIntervalEnd-1));
                                    range.setToIntervalEnd( toIntervalEnd - 1 );
                                    mSuppChangeReport.append( "\t-1 tie: " ).append( toIntervalEnd );
                                }
                            }
//                            System.out.println( "range.getSequence before = " + range.getSequence() );
                            range.setSequence( range.prepareSequence(polymerSequence) );

                            getDaoFactory().getRangeDao().update( range );

                            mSuppChangeReport.append( "\nPrevious range: " ).append( rangeSeqStored );
                            mSuppChangeReport.append( "\nNew range     : " ).append( range.getSequence() ).append( "\n\n" );

                        } else {
                            // Here we suppose that the polymer sequence hadn't it's first methionine when the range was
                            // created but was later added during a polymer update.
                            String mAdded = getRangeSeqMadded( polymerSequence, range, owner );
                            if ( rangeSeqStored.equals( mAdded ) ) {
                                System.out.println("\t...an M was added");

                                SanityCheckerHelper sch = getCheckerHelper( );
                                RangeBean rangeBean = (RangeBean) sch.getBeans(RangeBean.class, range.getAc() ).get( 0 );
//                                messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_SAVED_BY_ADDING_THE_M, rangeBean );

                                mAddedCount++;

                                mAddedChangeReport.append( "\n\nProtein Ac: " ).append( polymer.getAc() );
                                mAddedChangeReport.append( "\tRange Ac:" ).append( range.getAc() );
                                mAddedChangeReport.append( "\tRange from fuzzy type: " ).append( range.getFromCvFuzzyType() );
                                mAddedChangeReport.append( "\tMsup" );

                                //if fromCvFyzzyType == null ? Search if is exists in database
                                // Check that is fromCvFuzzyType == undetermined, fromIntervalStart... == 0

                                if ( ( fromCvFuzzyType != null &&
                                        !( fromCvFuzzyType.isCTerminal() || fromCvFuzzyType.isNTerminal() || fromCvFuzzyType.isUndetermined() ) ) || fromCvFuzzyType == null )
                                {
                                    mAddedChangeReport.append( "\n+1 fis: " ).append( fromIntervalStart );
                                    mAddedChangeReport.append( "\t fie: " ).append( fromIntervalEnd );
                                    mAddedChangeReport.append( "\t tis: " ).append( toIntervalStart );
                                    mAddedChangeReport.append( "\t tie: " ).append( toIntervalEnd );

//                                    System.out.println("\t...fromIntervalStart = " + (fromIntervalStart + 1));
//                                    System.out.println("\t...fromIntervalEnd = " + (fromIntervalEnd + 1));
//                                    System.out.println("\t...toIntervalEnd = " + (toIntervalEnd + 1));
//                                    System.out.println("\t...toIntervalStart = " + (toIntervalStart + 1));

                                    range.setFromIntervalStart( fromIntervalStart + 1 );
                                    range.setFromIntervalEnd( fromIntervalEnd + 1 );
                                    range.setToIntervalStart( toIntervalStart + 1 );
                                    range.setToIntervalEnd( toIntervalEnd + 1 );
                                }

                                range.setSequence( range.prepareSequence(polymerSequence) );
                                getDaoFactory().getRangeDao().update( range );

                                mAddedChangeReport.append( "\nPrevious range: " ).append( rangeSeqStored );
                                mAddedChangeReport.append( "\nNew range     : " ).append( range.getSequence() ).append( "\n" );

                            } else {
//                                System.out.println("\t...Could not automatically set range");
                                notEqualCount++;

                                SanityCheckerHelper sch = getCheckerHelper();
                                RangeBean rangeBean = sch.getBeans(RangeBean.class, range.getAc() ).get( 0 );
//                                messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_NOT_EQUAL_TO_PROTEIN_SEQ, rangeBean );

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
                        System.err.print( "ERROR - Polymer '" + polymer.getShortLabel() + "' (" + polymer.getAc() + ")" +
                                " has a range (" + range.getAc() + ") without sequence ... " );
                        System.out.flush();
                        range.setSequence(range.prepareSequence(polymerSequence));
                        String s = range.getSequence();
                        System.out.println("s = " + s);
                        if ( s != null && s.length() > 0 ) {
                            System.out.println("\t...range seq was null but was added.");
                            getDaoFactory().getRangeDao().saveOrUpdate( range );
//                            System.out.println( "Fixed." );
//                            System.out.println( "Range.getSequence() [length=" + range.getSequence().length() + "]: " + range.getSequence() );
                        } else {
                            System.out.println("\t...range seq was null and could not be calculated.");
//                            System.out.println( "NOT fixed." );
                            System.out.println( "\t...WARNING - Could not generate a sequence for that range " + range.getAc() );
                        }

//                        System.out.println( range );
                    }
                }
                range = null;
                rangeSeqStored = null;
            }
            ranges.clear();
        }
        polymerSequence = null;
    }

    private static Connection getJdbcConnection( ) {
        return getDaoFactory().connection();
    }

    private static DaoFactory getDaoFactory( ) {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    public void checkRangeEntireDatabase() throws SQLException, IntactTransactionException, IOException {
        int proteinCount;
//        int CHUNK_SIZE = 1;
        int CHUNK_SIZE = 1;

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        proteinCount = proteinDao.countAll();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


        // FOR TESTING START
//        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
//        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
//        Protein prot = proteinDao.getByAc("EBI-301077");
//        RangeChecker rangeChecker = new RangeChecker();
//        Collection<String> acs = new ArrayList<String>();
//        acs.add(prot.getAc());
//        rangeChecker.check(acs);
//        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
//        int j = 1;
//        if (j == 1){
//            return;
//        }
        // FOR TESTING END


        int iterationCount = proteinCount/CHUNK_SIZE;
        for(int i=0; i<=iterationCount ; i++){
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
            Collection<ProteinImpl> proteins;

            proteins = proteinDao.getAll((i*CHUNK_SIZE), CHUNK_SIZE);

            Collection<String> proteinAcs = new ArrayList<String>();
            for(Protein protein : proteins){
                proteinAcs.add(protein.getAc());
            }

            RangeChecker rangeChecker = new RangeChecker();
            rangeChecker.check(proteinAcs);
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
    }
}