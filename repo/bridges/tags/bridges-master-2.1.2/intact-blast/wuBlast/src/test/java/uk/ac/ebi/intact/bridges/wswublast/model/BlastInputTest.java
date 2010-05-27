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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.bridges.wswublast.model;

import org.junit.Test;
import org.junit.Assert;

/**
 * Test class for BlastInput.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 *        <pre>
 *        15-Jan-2008
 *        </pre>
 */
public class BlastInputTest {

    @Test
    public void equals() throws Exception {
        BlastInput biA = new BlastInput(new UniprotAc("P12345"));
        BlastInput biB = new BlastInput(new UniprotAc("P12345"));
        Assert.assertEquals( biA, biB );

        biA.setSequence( new Sequence( "qwert") );
        Assert.assertNotSame( biA, biB );

        biB.setSequence( biA.getSequence() );
        Assert.assertEquals( biA, biB );

        BlastInput biC = new BlastInput(new UniprotAc("Q12345"));
        Assert.assertNotSame( biA, biC );
        biC.setSequence( biA.getSequence() );
        Assert.assertNotSame( biA, biC );

    }

}
