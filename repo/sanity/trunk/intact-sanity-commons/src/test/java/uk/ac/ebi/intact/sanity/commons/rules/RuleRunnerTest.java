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
package uk.ac.ebi.intact.sanity.commons.rules;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.sanity.commons.DeclaredRuleManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * RuleRunner Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RuleRunnerTest extends IntactBasicTestCase {

    @After
    public void clear() throws Exception {
        RuleRunnerReport.getInstance().clear();
    }

    @Test
    @Ignore
    // Because having 2 sanity-rules.xml in the classpath got some other test to fail.
    public void runAvailable_default() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom( 1 );

        RuleRunner.runAvailableRules( Arrays.asList( exp ) );

        final Collection<GeneralMessage> collection = RuleRunnerReport.getInstance().getMessages();
        Assert.assertEquals( 1, collection.size() );
        final GeneralMessage message = collection.iterator().next();
        Assert.assertEquals( MessageLevel.WARNING, message.getMessageDefinition().getLevel() );

        DeclaredRuleManager.close();
    }
}