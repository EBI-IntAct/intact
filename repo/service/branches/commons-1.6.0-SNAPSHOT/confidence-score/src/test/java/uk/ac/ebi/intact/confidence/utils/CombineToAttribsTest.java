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
package uk.ac.ebi.intact.confidence.utils;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.confidence.model.Attribute;
import uk.ac.ebi.intact.confidence.model.GoIdentifierImpl;
import uk.ac.ebi.intact.confidence.model.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test class for combining annotations to attributes.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.6.0
 */
public class CombineToAttribsTest {
     @Test
    public void testCombine() throws Exception {
        Set<Identifier> goA = new HashSet<Identifier>(3);
        goA.add( new GoIdentifierImpl("GO:0000001"));
        goA.add( new GoIdentifierImpl("GO:0000002"));
        goA.add( new GoIdentifierImpl("GO:0000003"));
        Set<Identifier> goB = new HashSet<Identifier>(5);
        goB.add( new GoIdentifierImpl("GO:0000001"));
        goB.add( new GoIdentifierImpl("GO:0000002"));
        goB.add( new GoIdentifierImpl("GO:0000004"));
        goB.add( new GoIdentifierImpl("GO:0000005"));
        List<Attribute> attrs = CombineToAttribs.combine( goA, goB);
        Assert.assertEquals( 9, attrs.size());
    }
}
