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
package uk.ac.ebi.intact.sanity.apt;

import uk.ac.ebi.intact.sanity.model.Rule;
import uk.ac.ebi.intact.sanity.model.Rules;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:RuleManager.java 9493 2007-08-17 14:02:49Z baranda $
 */
public class RuleManager {

    public static final String RULES_XML_PATH = "/META-INF/sanity-rules.xml";

    private static ThreadLocal<RuleManager> instance = new ThreadLocal<RuleManager>() {
        @Override
        protected RuleManager initialValue() {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(RULES_XML_PATH);

            Rules rules = null;
            try {
                rules = readRulesXml(is);
            } catch (JAXBException e) {
                throw new SanityRuleException(e);
            }
            return new RuleManager(rules.getRule());
        }
    };

    public static RuleManager getInstance() {
        return instance.get();
    }

    private List<Rule> availableRules;

    private RuleManager(List<Rule> availableRules) {
        this.availableRules = availableRules;
    }

    public List<Rule> getRulesForTarget(String targetClass) {
        List<Rule> rules = new ArrayList<Rule>();

        for (Rule rule : rules) {
            if (rule.getTargetClass().equals(targetClass)) {
                rules.add(rule);
            }
        }

        return rules;
    }

    public Set<String> getAvailableTargetClasses() {
        Set<String> targets = new HashSet<String>();

        for (Rule rule : availableRules) {
            targets.add(rule.getTargetClass());
        }

        return targets;
    }

    public static Rules readRulesXml(InputStream is) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Rules.class.getPackage().getName());
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (Rules) unmarshaller.unmarshal(is);
    }

    public static void writeRulesXml(Rules rules, Writer writer) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Rules.class.getPackage().getName());
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(rules, writer);
    }

    public List<Rule> getAvailableRules() {
        return availableRules;
    }
}