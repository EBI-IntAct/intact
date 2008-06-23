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
package uk.ac.ebi.intact.commons.util.diff;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import uk.ac.ebi.intact.commons.util.diff.Diff;
import uk.ac.ebi.intact.commons.util.diff.DiffCalculator;

import java.util.List;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DiffCalculatorTest {

    private DiffCalculator diffCalculator;

    @Before
    public void before() throws Exception {
        diffCalculator = new DiffCalculator();
    }

    @After
    public void after() throws Exception {
        diffCalculator = null;
    }

    @Test
    public void calculateDiffs1() throws Exception {
        final List<Diff> diffs = diffCalculator.calculateDiffs("0123", "023");

        Assert.assertEquals(3, diffs.size());

        Assert.assertEquals("0", diffs.get(0).getText());
        Assert.assertEquals(Operation.EQUAL, diffs.get(0).getOperation());
        Assert.assertEquals(0, diffs.get(0).getIndexInString1());
        Assert.assertEquals(0, diffs.get(0).getIndexInString2());

        Assert.assertEquals("1", diffs.get(1).getText());
        Assert.assertEquals(Operation.DELETE, diffs.get(1).getOperation());
        Assert.assertEquals(1, diffs.get(1).getIndexInString1());
        Assert.assertEquals(-1, diffs.get(1).getIndexInString2());

        Assert.assertEquals("23", diffs.get(2).getText());
        Assert.assertEquals(Operation.EQUAL, diffs.get(2).getOperation());
        Assert.assertEquals(2, diffs.get(2).getIndexInString1());
        Assert.assertEquals(1, diffs.get(2).getIndexInString2());
    }

    @Test
    public void calculateDiffs2() throws Exception {
        final List<Diff> diffs = diffCalculator.calculateDiffs("MSSTTL", "AMSSTTL");

        Assert.assertEquals(2, diffs.size());

        Assert.assertEquals("A", diffs.get(0).getText());
        Assert.assertEquals(Operation.INSERT, diffs.get(0).getOperation());
        Assert.assertEquals(-1, diffs.get(0).getIndexInString1());
        Assert.assertEquals(0, diffs.get(0).getIndexInString2());

        Assert.assertEquals("MSSTTL", diffs.get(1).getText());
        Assert.assertEquals(Operation.EQUAL, diffs.get(1).getOperation());
        Assert.assertEquals(0, diffs.get(1).getIndexInString1());
        Assert.assertEquals(1, diffs.get(1).getIndexInString2());
    }

}
