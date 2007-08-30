/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinUniprotAcTest extends IntactBasicTestCase {

    @Test
    public void rightAc() throws Exception {
        String wrongAc = "P12345";

        Protein prot = getMockBuilder().createProtein(wrongAc, wrongAc);

        Rule rule = new ProteinUniprotAc();
        Collection<GeneralMessage> messages = rule.check(prot);

        Assert.assertEquals(0, messages.size());
    }

    @Test
    public void wrongAc() throws Exception {
        String wrongAc = "BMP2K_HUMAN";

        Protein prot = getMockBuilder().createProtein(wrongAc, wrongAc);

        Rule rule = new ProteinUniprotAc();
        Collection<GeneralMessage> messages = rule.check(prot);

        Assert.assertEquals(1, messages.size());
    }

}