package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains utility methods to check on features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2010</pre>
 */

public class FeatureUtils {

    /**
     *
     * @param protein : the protein to check
     * @return a set of the protein features containing bad ranges (overlapping or out of bound)
     */
    public static Set<Feature> getFeaturesWithBadRanges(Protein protein){
        Collection<Component> components = protein.getActiveInstances();
        Set<Feature> badFeatures = new HashSet<Feature>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    if (isABadRange(range, protein.getSequence())){
                        badFeatures.add(feature);
                        break;
                    }
                }
            }
        }

        return badFeatures;
    }

    /**
     *
     * @param protein : the protein to check
     * @return a set of the protein feature ranges which are overlapping or out of bound
     */
    public static Set<Range> getBadRanges(Protein protein){

        Collection<Component> components = protein.getActiveInstances();
        Set<Range> badRanges = new HashSet<Range>();

        for (Component component : components){
            Collection<Feature> features = component.getBindingDomains();

            for (Feature feature : features){
                Collection<Range> ranges = feature.getRanges();

                for (Range range : ranges){
                    if (isABadRange(range, protein.getSequence())){
                        badRanges.add(range);
                    }
                }
            }
        }

        return badRanges;
    }

    /**
     *
     * @param range : the range to check
     * @param sequence : the sequence of the protein
     * @return true if the range is within the sequence, coherent with its fuzzy type and not overlapping
     */
    public static boolean isABadRange(Range range, String sequence){
        return (getBadRangeInfo(range, sequence) != null);
    }

    /**
     *
     * @param range : the range to check
     * @param sequence : the sequence of the protein
     * @return true if the range is within the sequence, coherent with its fuzzy type and not overlapping
     */
    public static String getBadRangeInfo(Range range, String sequence){

        // a range null is not a valid range for a feature
        if (range == null){
            return "Range is null";
        }

        // get the start and end status of the range
        final CvFuzzyType startStatus = range.getFromCvFuzzyType();
        final CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (startStatus == null){
            return "The start status of the range is null and it is mandatory for PSI-MI.";
        }
        if (endStatus == null){
            return "The end status of the range is null and it is mandatory for PSI-MI.";
        }

        // If the range is the start status and the begin position (s) are not consistent, or the end status and the end position (s) are not consistent
        // or the start status is not consistent with the end status, the range is not valid
        final int fromIntervalStart = range.getFromIntervalStart();
        final int fromIntervalEnd = range.getFromIntervalEnd();
        final int toIntervalStart = range.getToIntervalStart();
        final int toIntervalEnd = range.getToIntervalEnd();

        String areRangePositionsAccordingToTypeOkStart = getRangePositionsAccordingToRangeTypeErrorMessage(startStatus, fromIntervalStart, fromIntervalEnd, sequence);

        String areRangePositionsAccordingToTypeOkEnd = getRangePositionsAccordingToRangeTypeErrorMessage(endStatus, toIntervalStart, toIntervalEnd, sequence);

        if (areRangePositionsAccordingToTypeOkStart != null) {
            return areRangePositionsAccordingToTypeOkStart;
        }
        if (areRangePositionsAccordingToTypeOkEnd != null) {
            return areRangePositionsAccordingToTypeOkEnd;
        }
        if (areRangeStatusInconsistent(startStatus, endStatus)){
            return "Start status "+startStatus.getShortLabel()+" and end status "+endStatus.getShortLabel()+" are inconsistent";
        }

        // if the range has not a position undetermined, C terminal region or N-terminal region, we check if the range positions are not overlapping
        if (!(startStatus.isCTerminalRegion() || startStatus.isUndetermined() || startStatus.isNTerminalRegion()) && !(endStatus.isCTerminalRegion() || endStatus.isUndetermined() || endStatus.isNTerminalRegion()) && areRangePositionsOverlapping(range)){
            return "The range positions overlap : " + startStatus.getShortLabel() + ":" + fromIntervalStart + "-" + fromIntervalEnd + "," + endStatus.getShortLabel() + ":" + toIntervalStart + "-" + toIntervalEnd;
        }

        return null;
    }

    /**
     * A position is out of bound if inferior or equal to 0 or superior to the sequence length.
     * @param start : the start position of the interval
     * @param end  : the end position of the interval
     * @param sequenceLength : the length of the sequence, 0 if the sequence is null
     * @return true if the start or the end is inferior or equal to 0 and if the start or the end is superior to the sequence length
     */
    public static boolean areRangePositionsOutOfBounds(int start, int end, int sequenceLength){
        return start <= 0 || end <= 0 || start > sequenceLength || end > sequenceLength;
    }

    /**
     * A range interval is invalid if the start is after the end
     * @param start : the start position of the interval
     * @param end : the end position of the interval
     * @return true if the start is after the end
     */
    public static boolean areRangePositionsInvalid(int start, int end){

        if (start > end){
            return true;
        }
        return false;
    }

    /**
     *
     * @param rangeType : the status of the position
     * @param start : the start of the position
     * @param end : the end of the position (equal to start if the range position is a single position and not an interval)
     * @param sequence : the sequence of the protein
     * @return true if the range positions and the position status are consistent
     */
    public static boolean areRangePositionsAccordingToRangeTypeOk(CvFuzzyType rangeType, int start, int end, String sequence){
        return (getRangePositionsAccordingToRangeTypeErrorMessage(rangeType, start, end, sequence) == null);
    }

    /**
     *
     * @param rangeType : the status of the position
     * @param start : the start of the position
     * @param end : the end of the position (equal to start if the range position is a single position and not an interval)
     * @param sequence : the sequence of the protein
     * @return message with the error. Null otherwise
     */
    public static String getRangePositionsAccordingToRangeTypeErrorMessage(CvFuzzyType rangeType, int start, int end, String sequence){

        if (rangeType == null){
            throw new IllegalArgumentException("It is not possible to check if the range status is compliant with the range positions because it is null and mandatory.");
        }

        // the sequence length is 0 if the sequence is null
        int sequenceLength = 0;

        if (sequence != null){
            sequenceLength = sequence.length();
        }

        // the position status is defined
        // undetermined position, we expect to have a position equal to 0 for both the start and the end
        if (rangeType.isUndetermined() || rangeType.isCTerminalRegion() || rangeType.isNTerminalRegion()){
            if (start != 0 || end != 0){
                return "Undetermined positions (undetermined, N-terminal region, C-terminal region) must always be 0. Actual positions : " + start + "-" + end;
            }
        }
        // n-terminal position : we expect to have a position equal to 1 for both the start and the end
        else if (rangeType.isNTerminal()){
            if (start != 1 || end != 1){
                return "N-terminal positions must always be 1. Actual positions : " + start + "-" + end;
            }
        }
        // c-terminal position : we expect to have a position equal to the sequence length (0 if the sequence is null) for both the start and the end
        else if (rangeType.isCTerminal()){
            if (sequenceLength == 0 && (start < 0 ||end < 0 || start != end)){
                return "C-terminal positions must always be superior to 0. Actual positions : " + start + "-" + end;
            }
            else if ((start != sequenceLength || end != sequenceLength) && sequenceLength > 0){
                return "C-terminal positions must always be equal to the length of the protein sequence. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
            }
        }
        // greater than position : we don't expect an interval for this position so the start should be equal to the end
        else if (rangeType.isGreaterThan()){
            if (start != end) {
                return "Greater than positions must always be a single position and here it is an interval. Actual positions : " + start + "-" + end;
            }

            // The sequence is null, all we can expect is at least a start superior to 0.
            if (sequenceLength == 0){
                if (start <= 0 ){
                    return "Greater than positions must always be strictly superior to 0. Actual positions : " + start + "-" + end;
                }
            }
            // The sequence is not null, we expect to have positions superior to 0 and STRICTLY inferior to the sequence length
            else {
                if (start >= sequenceLength || start <= 0 ){
                    return "Greater than positions must always be strictly superior to 0 and strictly inferior to the protein sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        // less than position : we don't expect an interval for this position so the start should be equal to the end
        else if (rangeType.isLessThan()){
            if (start != end) {
                return "Less than positions must always be a single position and here it is an interval. Actual positions : " + start + "-" + end;
            }
            // The sequence is null, all we can expect is at least a start STRICTLY superior to 1.
            if (sequenceLength == 0){
                if (start <= 1){
                    return "Less than positions must always be strictly superior to 1. Actual positions : " + start + "-" + end;
                }
            }
            // The sequence is not null, we expect to have positions STRICTLY superior to 1 and inferior or equal to the sequence length
            else {
                if (start <= 1 || start > sequenceLength) {
                    return "Less than positions must always be strictly superior to 1 and inferior or equal to the protein sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        // if the range position is certain or ragged-n-terminus, we expect to have the positions superior to 0 and inferior or
        // equal to the sequence length (only possible to check if the sequence is not null)
        // We don't expect any interval for this position so the start should be equal to the end
        else if (rangeType.isCertain() || rangeType.isRaggedNTerminus()){
            if (start != end) {
                return "Certain and ragged-n-terminus positions must always be a single position and here it is an interval. Actual positions : " + start + "-" + end;
            }

            if (sequenceLength == 0){
                if (start <= 0){
                    return "Certain and ragged-n-terminus positions must always be strictly superior to 0. Actual positions : " + start + "-" + end;
                }
            }
            else {
                if (areRangePositionsOutOfBounds(start, end, sequenceLength)){
                    return "Certain and ragged-n-terminus positions must always be strictly superior to 0 and inferior or equal to the protein sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        // the range status is not well known, so we allow the position to be an interval, we just check that the start and end are superior to 0 and inferior to the sequence
        // length (only possible to check if the sequence is not null)
        else {
            if (sequenceLength == 0){
                if (areRangePositionsInvalid(start, end) || start <= 0 || end <= 0){
                    return rangeType.getShortLabel() + " positions must always be strictly superior to 0 and the end must be superior or equal to the start. Actual positions : " + start + "-" + end;
                }
            }
            else {
                if (areRangePositionsInvalid(start, end)) {
                    return rangeType.getShortLabel() + " positions must always have an end superior or equal to the start. Actual positions : " + start + "-" + end;
                } else if (areRangePositionsOutOfBounds(start, end, sequenceLength)){
                    return rangeType.getShortLabel() + " positions must always be strictly superior to 0 and inferior or equal to the sequence length. Actual positions : " + start + "-" + end + ", sequence length " + sequenceLength;
                }
            }
        }
        return null;
    }

    /**
     * Checks if the interval positions are overlapping
     * @param fromStart
     * @param fromEnd
     * @param toStart
     * @param toEnd
     * @return true if the range intervals are overlapping
     */
    public static boolean arePositionsOverlapping(int fromStart, int fromEnd, int toStart, int toEnd){

        if (fromStart > toStart || fromEnd > toStart || fromStart > toEnd || fromEnd > toEnd){
            return true;
        }
        return false;
    }

    /**
     * Checks if the interval positions of the range are overlapping
     * @param range
     * @return true if the range intervals are overlapping
     */
    public static boolean areRangePositionsOverlapping(Range range){
        // get the range status
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (startStatus == null){
            throw new IllegalArgumentException("It is not possible to check if the start range status is compliant with the range positions because it is null and mandatory.");
        }

        if (endStatus == null){
            throw new IllegalArgumentException("It is not possible to check if the end range status is compliant with the range positions because it is null and mandatory.");
        }

        // both the end and the start have a specific status
        // in the specific case where the start is superior to a position and the end is inferior to another position, we need to check that the
        // range is not invalid because 'greater than' and 'less than' are both exclusive
        if (startStatus.isGreaterThan() && endStatus.isLessThan() && range.getToIntervalEnd() - range.getFromIntervalStart() < 2){
            return true;
        }
        // we have a greater than start position and the end position is equal to the start position
        else if (startStatus.isGreaterThan() && !endStatus.isGreaterThan() && range.getFromIntervalStart() == range.getToIntervalStart()){
            return true;
        }
        // we have a less than end position and the start position is equal to the start position
        else if (!startStatus.isLessThan() && endStatus.isLessThan() && range.getFromIntervalEnd() == range.getToIntervalEnd()){
            return true;
        }
        // As the range positions are 0 when the status is undetermined, we can only check if the ranges are not overlapping when both start and end are not undetermined
        else if (!(startStatus.isUndetermined() || startStatus.isNTerminalRegion() || startStatus.isCTerminalRegion()) && !(endStatus.isUndetermined() || endStatus.isCTerminalRegion() || endStatus.isNTerminalRegion())){
            return arePositionsOverlapping(range.getFromIntervalStart(), range.getFromIntervalEnd(), range.getToIntervalStart(), range.getToIntervalEnd());
        }

        return false;
    }

    /**
     *
     * @param startStatus : the status of the start position
     * @param endStatus : the status of the end position
     * @return  true if the range status are inconsistent (n-terminal is the end, c-terminal is the beginning)
     */
    public static boolean areRangeStatusInconsistent(CvFuzzyType startStatus, CvFuzzyType endStatus){

        if (startStatus == null){
            throw new IllegalArgumentException("It is not possible to check if the start range status is compliant with the range positions because it is null and mandatory.");
        }

        if (endStatus == null){
            throw new IllegalArgumentException("It is not possible to check if the end range status is compliant with the range positions because it is null and mandatory.");
        }

        // both status are not null
        // the start position is C-terminal but the end position is different from C-terminal
        if (startStatus.isCTerminal() && !endStatus.isCTerminal()){
            return true;
        }
        // the end position is N-terminal but the start position is different from N-terminal
        else if (endStatus.isNTerminal() && !startStatus.isNTerminal()){
            return true;
        }
        // the end status is C terminal region, the start status can only be C-terminal region or C-terminal
        else if (startStatus.isCTerminalRegion() && !(endStatus.isCTerminal() || endStatus.isCTerminalRegion())){
            return true;
        }
        // the start status is N terminal region, the end status can only be N-terminal region or N-terminal        
        else if (endStatus.isNTerminal() && !(startStatus.isNTerminal() || startStatus.isNTerminalRegion())){
            return true;
        }

        return false;
    }

    /**
     * If the range status is 'undetermined', the positions will be set to 0. If the range status is 'n-terminal', the positions will be 1
     * and finally if the range status is 'c-terminal' and the sequence is not null, the positions will be the sequence length
     * @param range : the range
     * @param proteinSequence : the sequence of the protein
     */
    public static void correctRangePositionsAccordingToType(Range range, String proteinSequence){
        CvFuzzyType startStatus = range.getFromCvFuzzyType();
        CvFuzzyType endStatus = range.getToCvFuzzyType();

        if (startStatus == null){
            throw new IllegalArgumentException("It is not possible to check if the start range status is compliant with the range positions because it is null and mandatory.");
        }

        if (endStatus == null){
            throw new IllegalArgumentException("It is not possible to check if the end range status is compliant with the range positions because it is null and mandatory.");
        }

        if (startStatus.isUndetermined() || startStatus.isCTerminalRegion() || startStatus.isNTerminalRegion()){
            range.setFromIntervalStart(0);
            range.setFromIntervalEnd(0);
        }
        else if (startStatus.isNTerminal()){
            range.setFromIntervalStart(1);
            range.setFromIntervalEnd(1);
        }
        else if (startStatus.isCTerminal()){
            if (proteinSequence != null){
                range.setFromIntervalStart(proteinSequence.length());
                range.setFromIntervalEnd(proteinSequence.length());
            }
            else {
               range.setFromIntervalStart(1);
                range.setFromIntervalEnd(1); 
            }
        }

        if (endStatus.isUndetermined() || endStatus.isCTerminalRegion() || endStatus.isNTerminalRegion()){
            range.setToIntervalStart(0);
            range.setToIntervalEnd(0);
        }
        else if (endStatus.isNTerminal()){
            range.setToIntervalStart(1);
            range.setToIntervalEnd(1);
        }
        else if (endStatus.isCTerminal()){
            if (proteinSequence != null){
                range.setToIntervalStart(proteinSequence.length());
                range.setToIntervalEnd(proteinSequence.length());
            }
            else {
               range.setToIntervalStart(1);
                range.setToIntervalEnd(1);
            }
        }
    }
}
