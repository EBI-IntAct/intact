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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.sanity.commons.DeclaredRule;
import uk.ac.ebi.intact.sanity.commons.DeclaredRuleManager;
import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RuleRunner {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(RuleRunner.class);

    private RuleRunner() {}

    /**
     * Runs all the available rules found in the classpath and adds the messages in the
     * <code>RuleRunnerReport</code>. The <code>RuleRunnerReport</code> is a thread local class
     * and it is NOT cleared after executing the method, so this method can be run several times
     * adding the messages to the same instance.
     * @param intactObjects
     * @return
     */
    public static SanityReport runAvailableRules(Collection<? extends IntactObject> intactObjects) {
        final Set<String> availableGroups = DeclaredRuleManager.getInstance().getAvailableGroups();

        if (log.isDebugEnabled()) log.debug("Running all rules for "+intactObjects.size()+" objects, in rule groups "+availableGroups);

        return runAvailableRules(intactObjects, availableGroups.toArray(new String[availableGroups.size()]));
    }

    /**
     * Runs all the available rules found in the classpath and adds the messages in the
     * <code>RuleRunnerReport</code>. The <code>RuleRunnerReport</code> is a thread local class
     * and it is NOT cleared after executing the method, so this method can be run several times
     * adding the messages to the same instance.
     * @param intactObjects
     * @return
     */
    public static SanityReport runAvailableRules(Collection<? extends IntactObject> intactObjects, String... groupNames) {
        if (DeclaredRuleManager.getInstance().getAvailableDeclaredRules().isEmpty()) {
            throw new SanityRuleException("No declared rules found");
        }

        for (IntactObject intactObject : intactObjects) {
            List<DeclaredRule> declaredRules = DeclaredRuleManager.getInstance().getDeclaredRulesForTarget(intactObject.getClass(), groupNames);

            for (DeclaredRule declaredRule : declaredRules) {
                Rule rule = newRuleInstance(declaredRule);

                if (log.isDebugEnabled()) log.debug("Running rule: "+declaredRule.getRuleName()+" (target: "+declaredRule.getTargetClass()+")");

                Collection<GeneralMessage> messages = rule.check(intactObject);
                RuleRunnerReport.getInstance().addMessages(messages);
            }
        }

        return RuleRunnerReport.getInstance().toSanityReport();
    }

    public static Rule newRuleInstance(DeclaredRule declaredRule) {
        Rule rule = null;

        try {
            Class<?> ruleClass = Thread.currentThread().getContextClassLoader().loadClass(declaredRule.getRuleClass());
            rule = (Rule) ruleClass.newInstance();
        }
        catch (Exception e) {
            throw new SanityRuleException("Problem instantiating declared rule: " + declaredRule.getRuleName());
        }

        return rule;
    }

}