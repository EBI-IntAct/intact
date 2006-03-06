/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.rangeChecker;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.sanityChecker.MessageSender;
import uk.ac.ebi.intact.util.sanityChecker.ReportTopic;
import uk.ac.ebi.intact.util.sanityChecker.SanityCheckerHelper;
import uk.ac.ebi.intact.util.sanityChecker.model.IntactBean;
import uk.ac.ebi.intact.util.sanityChecker.model.RangeBean;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;


/**
 * This class has been done to automatically remapped the range sequence.
 *
 * -----------------------
 * E X P L A N A T I ON :
 * -----------------------
 *
 * What happens :
 * ---------------
 *   1 ==> a curator create a feature and define a range
 *   2 ==> automatically the range sequence is calculated from the protein sequence and the range fromIntervalStart and
 *         range fromCvFuzzyType
 *   3 ==> we decide to run a protein update which change some of the protein sequence
 *
 * Problem :
 * ---------
 *   the ranges corresponding to the proteins which sequences changed during the protein update won't correspond
 *   anymore to the actual protein sequence stored in the database.
 *
 * What we need to do :
 * --------------------
 *
 * We need when it's possible to remapp automatically those ranges sequences or reset the
 * fromIntervalStart, toIntervalStart, fromIntervalEnd, toIntervalEnd
 * OR
 * we need to tell the curators that the sequence couldn't be remapped automatically and that
 * therefore they will need to do it manually.
 *
 * When is it possible to remapp the range sequence automatically?
 * ---------------------------------------------------------------
 *
 * The automatic remapped of the sequence is possible only when the change on the protein sequence is just a remooving or
 * or a re-adding the first Methionine amino-acid.
 *
 * How are ranges sequences calculated and how do we automatically remapped?
 * ----------------------------------------------------------------------
 *
 * 1. If the cvFuzzyType is N-terminal(MI:0340 , EBI-448297) or undetermined (MI:0339 , EBI-448295) the range sequence will
 * corresponds to the first 100 amino-acids of the protein sequence.
 * 2. If the cvFuzzyType is C-terminal (MI:0334 , EBI-448301) the range sequence will corresponds to the last 100 amino-
 * acids of the protein sequence.
 * 3. Otherwise it will be the 100 first amino-acid starting from FromIntervalStart in the protein sequence.
 *
 * In case 1. and 2. if the first M is added or remooved after a protein update we want to recalculate the range
 * sequence in case 3 we want reset the FromIntervalStart, ToIntervalStart, FromIntervalEnd, ToIntervalEnd in order that
 * taking the new value of the range we can recalculate the range sequence and obtain the same sequence then the one
 * stored in the database.
 *
 *
 * How should I use this class?
 * -----------------------------
 *
 * Just instantiate an object RangeChecker and call the method check with as a parameter a collection of all the protein
 * from which you want to check the range sequence.
 * RangeChecker rangeChecker = new RangeChecker();
 * rangeChecker.check(proteins); //Protein being a collection of Protein
 *
 * What will the call of the method do?
 * ------------------------------------
 *
 * As we said it will remap when possible the range sequence but will as well create 3 reports files :
 *  1. mAdded.report
 *     this will contain all the information about the automatic remappings for the ranges in the following situation :
 *     The first M of the protein sequence was not there when the range was created but has been added since then
 *  2. mSupp.report
 *     this will contain all the information about the automatic remappings for the ranges in the following situation :
 *     The first M of the protein sequence was there when the range was created but has been suppressed since then
 *  3. notEqual.report
 *     this will contain all the information about the ranges that couldn't be remapped automatically.
 *
 * This method will as well send and email to all the concerned curators and administrator and this e-mail will look
 * like :
 *
 * Instance name: d003
 *
 * Could not find an email adress for user: intact
 *
 * This/those Range(s) are associated to a sequence which does not corresponds to the protein sequence. And the Range Sequence couldn't be remapped automatically
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Interaction Ac     	Protein Ac     	Feature Ac     	ToIntervalStart     	FromIntervalEnd     	RangeBean Ac     	Date     	User
 * EBI-39441     	EBI-3573     	EBI-611251     	0     	0     	EBI-611252     	2005-07-04 09:36:01.0     	INTACT
 * EBI-491985     	EBI-29160     	EBI-491987     	1     	1     	EBI-491988     	2005-02-08 16:24:03.0     	INTACT
 *
 * This/those Range(s) were created when the first Methionine was there, since then the Methionine had been remooved from the Protein Sequence. The Range Sequence has been remapped.
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Interaction Ac     	Protein Ac     	Feature Ac     	ToIntervalStart     	FromIntervalEnd     	RangeBean Ac     	Date     	User
 * EBI-491630     	EBI-28157     	EBI-491633     	221     	3     	EBI-491634     	2005-02-08 12:17:46.0     	INTACT
 * EBI-491644     	EBI-28157     	EBI-491647     	221     	3     	EBI-491648     	2005-02-08 12:25:28.0     	INTACT

 *  This/those Range(s) were created when the first Methionine was not there, since then the Methionine had been added to the Protein Sequence. The Range Sequence has been remapped.
 * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Interaction Ac     	Protein Ac     	Feature Ac     	ToIntervalStart     	FromIntervalEnd     	RangeBean Ac     	Date     	User
 * EBI-591635     	EBI-350527     	EBI-591729     	0     	0     	EBI-591730     	2005-05-27 06:49:36.0     	INTACT
 * EBI-591635     	EBI-350527     	EBI-616473     	0     	0     	EBI-616474     	2005-07-04 09:37:57.0     	INTACT
 *
 *
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class RangeChecker {
    int equal=0;
    int mSuppCount = 0;
    int mAddedCount = 0;
    int notEqual=0;
    String mAddedChangeReport = new String();
    String mSuppChangeReport = new String();
    String notEqualReport;
    Institution owner=new Institution("EBI");
    MessageSender messageSender= new MessageSender();

    public static void main(String[] args) {
//        System.out.println("RangeChecker.main");

        RangeChecker rangeChecker = new RangeChecker();
        IntactHelper intactHelper;
        try {
            intactHelper = new IntactHelper();
            Collection proteins = intactHelper.search(Interactor.class,"objclass", ProteinImpl.class.getName());
//            Collection proteins = intactHelper.search(Interactor.class,"ac","EBI-80426");//"EBI-28157");
            rangeChecker.check(proteins);
            intactHelper.closeStore();
        } catch (IntactException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param proteins
     */
    public void check(Collection proteins){

        if(proteins == null){
            throw new IllegalArgumentException("The parameter 'protein' should not be null");
        }

        RangeChecker rangeChecker = new RangeChecker();
        IntactHelper intactHelper;
        try {
            intactHelper = new IntactHelper();
            for (Iterator iterator = proteins.iterator(); iterator.hasNext();) {
                Protein protein = (Protein) iterator.next();
                rangeChecker.checkRange(protein, intactHelper/*, null*/);
            }

            intactHelper.closeStore();

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("mAdded.report"));
                out.write(rangeChecker.mAddedChangeReport);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("mSupp.report"));
                out.write(rangeChecker.mSuppChangeReport);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("notEqual.report"));
                out.write(rangeChecker.notEqual);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                rangeChecker.messageSender.postEmails("RANGE CHECKER");

            } catch ( MessagingException e ) {
                // scould not send emails, then how error ...
                //e.printStackTrace();

            }

        } catch (IntactException e) {
            e.printStackTrace();
        }

    }


    /**
     * This method return all the ranges related to a specific protein
     * @param protein Protein for which you want to be return all the ranges.
     * @param intactHelper
     * @return  a collection containing all the ranges associated to this protein
     * @throws IntactException
     */
    private Collection getRangesFromProtein(Protein protein, IntactHelper intactHelper) throws IntactException {
        //System.out.println("RangeChecker.getRangesFromProtein");
        Collection allRanges = new ArrayList();

        Collection components = intactHelper.search(Component.class, "interactor_ac", protein.getAc());
        for (Iterator iterator = components.iterator(); iterator.hasNext();) {
            Component component =  (Component) iterator.next();
            Collection features = component.getBindingDomains();
            for (Iterator iterator1 = features.iterator(); iterator1.hasNext();) {
                Feature feature =  (Feature) iterator1.next();
                Collection ranges = feature.getRanges();
                allRanges.addAll(ranges);
            }
        }

        return allRanges;
    }

    /**
     * Having a range with a particular FromIntervalStart, FromCvFuzzyType and given a protein sequence this method will
     * return the range sequence we expect to found.
     * @param proteinSeq The sequence of the protein the range given in parameter is related to
     * @param range the range you want to get the expected range sequence
     * @return the expected range sequence
     */
    private String getExpectedRangeSequence(String proteinSeq, Range range){
        // We create an artificial range called seqCalculator which will just be used to calculate the sequence to
        // return using the method setSequence and getSequence of a range.
        Range seqCalculator = new Range(owner,range.getFromIntervalStart(),range.getToIntervalStart(),proteinSeq);
        seqCalculator.setFromCvFuzzyType(range.getFromCvFuzzyType());
        seqCalculator.setSequence(proteinSeq);

        return seqCalculator.getSequence();

    }

    /**
     * In this function we suppose that when the range was created the first Methione was there but has been remooved
     * since then from the protein sequence. So we calculate the range sequence after having re-add this
     * methionine to the protein sequence.
     * @param proteinSeq
     * @param range
     * @return return the range sequence after having adding the first M to the protein sequence
     */
    private String getRangeSeqMSupp(String proteinSeq, Range range){
        // We create an artificial range called seqCalculator which will just be used to calculate the sequence to
        // return using the method setSequence and getSequence of a range.
        Range seqCalculator = new Range(owner,range.getFromIntervalStart(),range.getToIntervalStart(),"M"+proteinSeq);

        seqCalculator.setToCvFuzzyType(range.getToCvFuzzyType());
        seqCalculator.setFromCvFuzzyType(range.getFromCvFuzzyType());
        seqCalculator.setFromIntervalEnd(range.getFromIntervalEnd());
        seqCalculator.setFromIntervalStart(range.getFromIntervalStart());
        seqCalculator.setToIntervalEnd(range.getToIntervalEnd());
        seqCalculator.setToIntervalStart(range.getToIntervalStart());

        seqCalculator.setSequence("M"+proteinSeq);

        return seqCalculator.getSequence();

    }


    /**
     * In this function we suppose that when the range was created the first Methione was not there but has been added
     * since then to the protein sequence. So we calculate the range sequence after having suppressed the first M
     * assuming that it is a M methionine to the protein sequence.
     * @param proteinSeq
     * @param range
     * @return return the range sequence after having remooved the first amino-acid
     */
    private String getRangeSeqMadded(String proteinSeq, Range range, Institution owner){
        // We create an artificial range called seqCalculator which will just be used to calculate the sequence to
        // return using the method setSequence and getSequence of a range.
        Range seqCalculator = new Range(owner,range.getFromIntervalStart(),range.getToIntervalStart(),proteinSeq.substring(1,proteinSeq.length()));

        seqCalculator.setToCvFuzzyType(range.getToCvFuzzyType());
        seqCalculator.setFromCvFuzzyType(range.getFromCvFuzzyType());
        seqCalculator.setFromIntervalEnd(range.getFromIntervalEnd());
        seqCalculator.setFromIntervalStart(range.getFromIntervalStart());
        seqCalculator.setToIntervalEnd(range.getToIntervalEnd());
        seqCalculator.setToIntervalStart(range.getToIntervalStart());


        seqCalculator.setSequence(proteinSeq.substring(1,proteinSeq.length()));

        return seqCalculator.getSequence();
    }

    private void checkRange(Protein protein, IntactHelper intactHelper) throws IntactException {

        String proteinSeq = protein.getSequence();

        if(proteinSeq != null){
            Collection ranges = getRangesFromProtein(protein, intactHelper);
            for (Iterator iterator = ranges.iterator(); iterator.hasNext();) {
                Range range =  (Range) iterator.next();

                String rangeSeqStored = range.getSequence();

                if(rangeSeqStored!=null){
                    String expectedRangeSeq = getExpectedRangeSequence(proteinSeq,range);
                    if(expectedRangeSeq.equals(rangeSeqStored)){
                        equal++;
                    }
                    else{
                        int fromIntervalStart = range.getFromIntervalStart();
                        int fromIntervalEnd = range.getFromIntervalEnd();
                        int toIntervalStart = range.getToIntervalStart();
                        int toIntervalEnd = range.getToIntervalEnd();
                        CvFuzzyType fromCvFuzzyType = range.getFromCvFuzzyType();

                        // Here we assume that the protein sequence had it's first methionine when the range was created
                        //  but was later remooved during a protein update.
                        String mSupp = getRangeSeqMSupp(proteinSeq,range);

                        if(rangeSeqStored.equals(mSupp)){

                            SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
                            try {
                                sch.addMapping(RangeBean.class, "select c.interactor_ac, c.interaction_ac, i.ac, i.updated, i.userstamp, i.feature_ac, i.fromintervalstart, i.fromintervalend, i.fromfuzzytype_ac, i.tofuzzytype_ac, i.fromintervalend, i.tointervalend, i.fromintervalstart, i.tointervalstart " +
                                        "from ia_range i, ia_feature f, ia_component c " +
                                        "where i.feature_ac = f.ac " +
                                        "and f.component_ac=c.ac " +
                                        "and i.ac = ?");

                                RangeBean rangeBean = (RangeBean) sch.getBeans(RangeBean.class, range.getAc()).get(0);
                                messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_SAVED_BY_SUPPRESSING_THE_M , rangeBean);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            mSuppCount++;
                            mSuppChangeReport=mSuppChangeReport +
                                    "\n\nProtein Ac: " + protein.getAc()+
                                    "\tRange Ac:" + range.getAc() +
                                    "\tRange from fuzzy type: " + range.getFromCvFuzzyType() +
                                    "\tMadded";
                                  //M A L D KJGJDKKSK
                                  //1 2 3 4
                            System.out.println("mSuppCount = " + mSuppCount);
                            if((fromCvFuzzyType!=null && !(fromCvFuzzyType.isCTerminal() || fromCvFuzzyType.isNTerminal() || fromCvFuzzyType.isUndetermined())) || fromCvFuzzyType==null){
                                if(fromIntervalStart != 0){
                                    range.setFromIntervalStart(fromIntervalStart-1);
                                    mSuppChangeReport = mSuppChangeReport +
                                            "\n-1 fis: " + fromIntervalStart;
                                }
                                if(fromIntervalEnd != 0) {
                                    range.setFromIntervalEnd(fromIntervalEnd-1);
                                    mSuppChangeReport = mSuppChangeReport +
                                            "\t-1 fie: " + fromIntervalEnd;
                                }
                                if(toIntervalStart != 0) {
                                    range.setToIntervalStart(toIntervalStart-1);
                                    mSuppChangeReport = mSuppChangeReport +
                                            "\t-1 tis: " + toIntervalStart;
                                }
                                if (toIntervalEnd != 0){
                                    range.setToIntervalEnd(toIntervalEnd-1);
                                    mSuppChangeReport = mSuppChangeReport +
                                            "\t-1 tie: " + toIntervalEnd;
                                }
                            }
                            System.out.println("range.getSequence before = " +range.getSequence());
                            range.setSequence(proteinSeq);

                            intactHelper.update(range);
                            mSuppChangeReport = mSuppChangeReport +
                                    "\nPrevious range: " + rangeSeqStored +
                                    "\nNew range     : " + range.getSequence() + "\n\n";

                        }
                        else{
                            // Here we assume that the protein sequence hadn't it's first methionine when the range was
                            // created but was later added during a protein update.
                            String mAdded = getRangeSeqMadded(proteinSeq,range,owner);
                            if(rangeSeqStored.equals(mAdded)){

                                SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
                                try {
                                    sch.addMapping(RangeBean.class, "select c.interactor_ac, c.interaction_ac, i.ac, i.updated, i.userstamp, i.feature_ac, i.fromintervalstart, i.fromintervalend, i.fromfuzzytype_ac, i.tofuzzytype_ac, i.fromintervalend, i.tointervalend, i.fromintervalstart, i.tointervalstart " +
                                        "from ia_range i, ia_feature f, ia_component c " +
                                        "where i.feature_ac = f.ac " +
                                        "and f.component_ac=c.ac " +
                                        "and i.ac = ?");
                                    RangeBean rangeBean = (RangeBean) sch.getBeans(RangeBean.class, range.getAc()).get(0);
                                    messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_SAVED_BY_ADDING_THE_M , rangeBean);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                mAddedCount++;
                                mAddedChangeReport=mAddedChangeReport +
                                        "\n\nProtein Ac: " + protein.getAc()+
                                        "\tRange Ac:" + range.getAc() +
                                        "\tRange from fuzzy type: " + range.getFromCvFuzzyType() +
                                        "\tMsup";
                                //if fromCvFyzzyType == null ? Search if is exists in database
                                // Check that is fromCvFuzzyType == undetermined, fromIntervalStart... == 0

                                if((fromCvFuzzyType!=null && !(fromCvFuzzyType.isCTerminal() || fromCvFuzzyType.isNTerminal() || fromCvFuzzyType.isUndetermined())) || fromCvFuzzyType==null){
                                    mAddedChangeReport = mAddedChangeReport +
                                            "\n+1 fis: " + fromIntervalStart +
                                            "\t fie: " + fromIntervalEnd +
                                            "\t tis: " + toIntervalStart +
                                            "\t tie: " + toIntervalEnd;
                                    range.setFromIntervalStart(fromIntervalStart+1);
                                    range.setFromIntervalEnd(fromIntervalEnd+1);
                                    range.setToIntervalStart(toIntervalStart+1);
                                    range.setToIntervalEnd(toIntervalEnd+1);
                                }
                                range.setSequence(proteinSeq);
                                intactHelper.update(range);

                                mAddedChangeReport = mAddedChangeReport +
                                        "\nPrevious range: " + rangeSeqStored +
                                        "\nNew range     : " + range.getSequence()+ "\n";
                            }
                            else {
                                notEqual++;
                                SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);
                                try {
                                    sch.addMapping(RangeBean.class, "select c.interactor_ac, c.interaction_ac, i.ac, i.updated, i.userstamp, i.feature_ac, i.fromintervalstart, i.fromintervalend, i.fromfuzzytype_ac, i.tofuzzytype_ac, i.fromintervalend, i.tointervalend, i.fromintervalstart, i.tointervalstart " +
                                        "from ia_range i, ia_feature f, ia_component c " +
                                        "where i.feature_ac = f.ac " +
                                        "and f.component_ac=c.ac " +
                                        "and i.ac = ?");
                                    RangeBean rangeBean = (RangeBean) sch.getBeans(RangeBean.class, range.getAc()).get(0);
                                    messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_NOT_EQUAL_TO_PROTEIN_SEQ , rangeBean);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                notEqualReport = notEqualReport +
                                        "\n\nProtein Ac: " + protein.getAc()+
                                        "\tRange Ac:" + range.getAc() +
                                        "\nRange seq stored: " + rangeSeqStored +
                                        "\nRange seq expect: " + expectedRangeSeq + "\n\n";


                            }
                            mAdded=null;
                        }
                        mSupp=null;
                        fromCvFuzzyType=null;
                    }
                }
                range=null;
                rangeSeqStored=null;
            }
            ranges.clear();
        }
        proteinSeq=null;
    }
}