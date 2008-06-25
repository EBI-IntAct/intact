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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.commons.util.diff.Diff;

import java.util.List;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DiffUtilsTest {

    @Test
    public void calculateIndexShift() throws Exception {
        final String str1 = "ABCD";
        final String str2 = "A00000000CD";

        final List<Diff> diffs = DiffUtils.diff(str1, str2);

        int originalIndex = 2;

        char charIn1 = str1.charAt(originalIndex);

        int indexIn2 = DiffUtils.calculateIndexShift(diffs, originalIndex);

        char sameCharIn2 = str2.charAt(indexIn2);

        Assert.assertEquals(charIn1, sameCharIn2);
    }

    @Test
    public void calculateIndexShift2() throws Exception {
        final String str1 = "ABCD";
        final String str2 = "A";

        final List<Diff> diffs = DiffUtils.diff(str1, str2);

        int indexIn2 = DiffUtils.calculateIndexShift(diffs, 2);
        Assert.assertEquals(-1, indexIn2);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void calculateIndexShift3() throws Exception {
        final String str1 = "ABCD";
        final String str2 = "A";

        final List<Diff> diffs = DiffUtils.diff(str1, str2);

        DiffUtils.calculateIndexShift(diffs, 200);
    }

    @Test
    public void calculateIndexShift4() throws Exception {
        final String str1 = "ABCD";
        final String str2 = "ABCDZZZZ";

        final List<Diff> diffs = DiffUtils.diff(str1, str2);

        int indexIn2 = DiffUtils.calculateIndexShift(diffs, 2);
        Assert.assertEquals(2, indexIn2);
    }

    @Test
    public void calculateIndexShift5() throws Exception {
        final String str1 = "ABCD";
        final String str2 = "ABCZZZD";

        final List<Diff> diffs = DiffUtils.diff(str1, str2);

        int indexIn2 = DiffUtils.calculateIndexShift(diffs, 2);
        Assert.assertEquals(2, indexIn2);
    }
}
