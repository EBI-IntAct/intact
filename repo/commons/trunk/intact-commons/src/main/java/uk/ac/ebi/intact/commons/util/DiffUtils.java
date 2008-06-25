/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.commons.util;

import uk.ac.ebi.intact.commons.util.diff.Diff;
import uk.ac.ebi.intact.commons.util.diff.Operation;
import uk.ac.ebi.intact.commons.util.diff.DiffCalculator;

import java.util.List;
import java.util.LinkedList;

/**
 * Calculate diffs between strings and other methods to manipulate the differences. 
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DiffUtils {

    private DiffUtils() {}

    /**
     * Checks the differences between two strings.
     * @param str1 String 1
     * @param str2 String 2
     * @return A list of Diff objects with the differences
     */
    public static List<Diff> diff(String str1, String str2) {
        DiffCalculator diffCalculator = new DiffCalculator();
        return diffCalculator.calculateDiffs(str1, str2);
    }

    /**
     * Filters, creating a copy, a list of Diffs by operation.
     * @param diffs The list to filter
     * @param operation The operation to look for 
     * @return The filtered list
     */
    public static List<Diff> filterDiffsByOperation(List<Diff> diffs, Operation operation) {
        List<Diff> filteredDiffs = new LinkedList<Diff>();

        for (Diff diff : diffs) {
            if (operation == diff.getOperation()) {
                filteredDiffs.add(diff);
            }
        }

        return filteredDiffs;
    }

    /**
     * Taking into account the differences, calculates what the index should be in the
     * second string by providing an index for the first string.
     * @param diffs Differences for string1 and string2
     * @param originalIndex The index in string1
     * @return The index in string2, corresponding to the index in string1
     */
    public static int calculateIndexShift(List<Diff> diffs, int originalIndex) {
        if (diffs == null || diffs.isEmpty()) {
            throw new IllegalArgumentException("List of Diffs is null or empty");
        }

        int index = originalIndex;
        int diffEnd = -1;

        for (Diff diff : diffs) {
            int diffStart = (diff.getIndexInString1() > -1)? diff.getIndexInString1() : diff.getIndexInString2();

            final int diffLength = diff.getText().length();
            diffEnd = (diffStart == -1)? -1 : diffStart+diffLength-1;

            if (diffStart <= index && diffEnd <= (index+ diffLength)) {

                switch (diff.getOperation()) {
                    case INSERT:
                        index += diffLength;
                        break;
                    case DELETE:
                        index -= diffLength;
                }

            } else if (diffStart <= index && diffEnd > (index+diffLength)) {

                switch (diff.getOperation()) {
                    case INSERT:
                        index += ((index+ diffLength)-diffStart);
                        break;
                    case DELETE:
                        return -1;
                }
            }
        }

        if (diffEnd > -1 && diffEnd < originalIndex) {
            throw new IndexOutOfBoundsException("Passed original index is outside the expected range: "+originalIndex+" (max:"+diffEnd+")");
        }

        return index;
    }
}
