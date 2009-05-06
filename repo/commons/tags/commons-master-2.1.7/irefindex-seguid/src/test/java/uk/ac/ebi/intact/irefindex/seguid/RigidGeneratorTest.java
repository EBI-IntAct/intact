/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.irefindex.seguid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Assert;

/**
 * TODO comment that class header
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class RigidGeneratorTest {

    @Test
    public void generateRigidForBinary() throws Exception {

        String P60785 = RogidGeneratorTest.getProteinSequence( "P60785" );
        String taxid1 = "83333";

        String P33025 = RogidGeneratorTest.getProteinSequence( "P33025" );
        String taxid2 = "83333";

        RigidGenerator rigidGenerator = new RigidGenerator();

        rigidGenerator.addSequence( P60785, taxid1 );
        rigidGenerator.addSequence( P33025, taxid2 );

        final String rigid = rigidGenerator.calculateRigid();
        Assert.assertEquals("+/DyxaNG0mklydsbUh12WcfudXI",rigid);

    }

    @Test
     public void generateRigidForNnary() throws Exception {

        String P60785 = RogidGeneratorTest.getProteinSequence( "P60785" );
        String taxid1 = "83333";

        String P33025 = RogidGeneratorTest.getProteinSequence( "P33025" );
        String taxid2 = "83333";

        String P67080 = RogidGeneratorTest.getProteinSequence( "P67080" );
        String taxid3 = "83333";

        RigidGenerator rigidGenerator = new RigidGenerator();

        rigidGenerator.addSequence( P60785, taxid1 );
        rigidGenerator.addSequence( P33025, taxid2 );
        rigidGenerator.addSequence( P67080, taxid3 );

        final String rigid = rigidGenerator.calculateRigid();
        Assert.assertEquals("8gCCeMoszv8/PVqk2BhyPCwf5ZI",rigid);

    }




}
