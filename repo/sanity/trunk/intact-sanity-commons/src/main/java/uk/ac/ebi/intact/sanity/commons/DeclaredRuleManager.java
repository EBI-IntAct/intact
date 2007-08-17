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
public class DeclaredRuleManager {

    public static final String RULES_XML_PATH = "/META-INF/sanity-rules.xml";

    private static ThreadLocal<DeclaredRuleManager> instance = new ThreadLocal<DeclaredRuleManager>() {
        @Override
        protected DeclaredRuleManager initialValue() {
            InputStream is = DeclaredRuleManager.class.getResourceAsStream(RULES_XML_PATH);

            DeclaredRules rules = null;
            try {
                rules = readRulesXml(is);
            } catch (JAXBException e) {
                throw new SanityRuleException(e);
            }
            return new DeclaredRuleManager(rules.getDeclaredRule());
        }
    };

    public static DeclaredRuleManager getInstance() {
        return instance.get();
    }

    private List<DeclaredRule> availableDeclaredRules;

    private DeclaredRuleManager(List<DeclaredRule> availableDeclaredRules) {
        this.availableDeclaredRules = availableDeclaredRules;
    }

    public List<DeclaredRule> getDeclaredRulesForTarget(String targetClass) {
        List<DeclaredRule> rules = new ArrayList<DeclaredRule>();

        for (DeclaredRule rule : rules) {
            if (rule.getTargetClass().equals(targetClass)) {
                rules.add(rule);
            }
        }

        return rules;
    }

    public Set<String> getAvailableTargetClasses() {
        Set<String> targets = new HashSet<String>();

        for (DeclaredRule rule : availableDeclaredRules) {
            targets.add(rule.getTargetClass());
        }

        return targets;
    }


    public static DeclaredRules readRulesXml(InputStream is) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(DeclaredRules.class.getPackage().getName());
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (DeclaredRules) unmarshaller.unmarshal(is);
    }

    public static void writeRulesXml(DeclaredRules rules, Writer writer) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(DeclaredRules.class.getPackage().getName());
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(rules, writer);
    }

    public List<DeclaredRule> getAvailableDeclaredRules() {
        return availableDeclaredRules;
    }
}