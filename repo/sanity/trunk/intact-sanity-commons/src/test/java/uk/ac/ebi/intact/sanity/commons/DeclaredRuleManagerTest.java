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
package uk.ac.ebi.intact.sanity.commons;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Experiment;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DeclaredRuleManagerTest extends IntactBasicTestCase {

    @Test
    public void runAvailable_default() throws Exception {
        DeclaredRuleManager manager = DeclaredRuleManager.getInstance();

        Assert.assertEquals(1, manager.getAvailableDeclaredRules().size());
        Assert.assertEquals(1, manager.getAvailableTargetClasses().size());
        Assert.assertEquals(1, manager.getDeclaredRulesForTarget(Experiment.class).size());
        Assert.assertEquals(1, manager.getDeclaredRulesForTarget(AnnotatedObject.class).size());
        Assert.assertEquals(0, manager.getDeclaredRulesForTarget(BioSource.class).size());

        DeclaredRuleManager.close();
    }

}