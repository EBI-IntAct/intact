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
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.clone.IntactCloner;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentTest extends IntactBasicTestCase{

    @Test
    public void equals_differentCvInteraction() throws Exception {
        Experiment exp1 = getMockBuilder().createDeterministicExperiment();
        exp1.getBioSource().setTaxId("9606");
        exp1.setCvInteraction(getMockBuilder().createCvObject(CvInteraction.class, "MI:0028", "unknown"));

        Experiment exp2 = new IntactCloner().clone(exp1);
        exp2.setCvInteraction(getMockBuilder().createCvObject(CvInteraction.class, "MI:0808", "unknown"));

        Assert.assertFalse(exp1.equals(exp2));
    }

}