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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.misc.URLClassPath;
import uk.ac.ebi.intact.commons.util.ClassUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:RuleManager.java 9493 2007-08-17 14:02:49Z baranda $
 */
public class DeclaredRuleManager {

    private static final Log log = LogFactory.getLog(DeclaredRuleManager.class);

    public static final String RULES_XML_PATH = "/META-INF/sanity-rules.xml";

    private static ThreadLocal<DeclaredRuleManager> instance = new ThreadLocal<DeclaredRuleManager>();

    public static DeclaredRuleManager getInstance() {
        DeclaredRuleManager declaredRuleManager = instance.get();

        if (declaredRuleManager == null) {
            declaredRuleManager = new DeclaredRuleManager();
            instance.set(declaredRuleManager);
        }

        return declaredRuleManager;
    }

    public static void close() {
        instance.set(null);
    }

    private List<DeclaredRule> availableDeclaredRules = new ArrayList<DeclaredRule>();

    private DeclaredRuleManager() {
        try {
            loadDeclaredRulesFromClassPath();
        } catch (IOException e) {
            throw new SanityRuleException("Problem getting declared rules from classpath", e);
        }
    }

    public List<DeclaredRule> getDeclaredRulesForTarget(Class targetClass) {
        List<DeclaredRule> rules = new ArrayList<DeclaredRule>();

        for (DeclaredRule rule : availableDeclaredRules) {
            Class ruleTargetClass;
            try {
                ruleTargetClass = Class.forName(rule.getTargetClass());
            } catch (ClassNotFoundException e) {
                throw new SanityRuleException("Found declared rule with a target class not found in the classpath: " + rule.getTargetClass());
            }

            if (ruleTargetClass.isAssignableFrom(targetClass)) {
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

    public static void writeRulesXml(DeclaredRules rules, Writer writer) throws Exception {
        //JAXBContext jc = JAXBContext.newInstance(DeclaredRules.class.getPackage().getName());
        //Marshaller marshaller = jc.createMarshaller();
        //marshaller.marshal(rules, writer);

        // NOTE: We are using DOM here because it seems to classpath be a problem
        // with APT when invoking JAXB (with the above code)

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("declared-rules");
        document.appendChild(root);

        for (DeclaredRule declaredRule : rules.getDeclaredRule()) {
            Element decRuleNode = document.createElement("declared-rule");
            root.appendChild(decRuleNode);

            Element ruleNameNode = document.createElement("rule-name");
            decRuleNode.appendChild(ruleNameNode);
            ruleNameNode.appendChild(document.createTextNode(declaredRule.getRuleName()));

            Element ruleClassNode = document.createElement("rule-class");
            decRuleNode.appendChild(ruleClassNode);
            ruleClassNode.appendChild(document.createTextNode(declaredRule.getRuleClass()));

            Element targetClassNode = document.createElement("target-class");
            decRuleNode.appendChild(targetClassNode);
            targetClassNode.appendChild(document.createTextNode(declaredRule.getTargetClass()));
        }

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(document);
        trans.transform(source, result);
    }

    public List<DeclaredRule> getAvailableDeclaredRules() {
        return availableDeclaredRules;
    }

    public void addDeclaredRule(DeclaredRule declaredRule) {
        boolean exists = false;

        for (DeclaredRule availableRule : availableDeclaredRules) {
            if (availableRule.getRuleName().equals(declaredRule.getRuleName())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            availableDeclaredRules.add(declaredRule);
        }
    }

    public void addAllDeclaredRules(Collection<DeclaredRule> declaredRules) {
        for (DeclaredRule declaredRule : declaredRules) {
            addDeclaredRule(declaredRule);
        }
    }

    protected void loadDeclaredRulesFromClassPath() throws IOException {
        Collection<URL> resources = ClassUtils.searchResourcesInClasspath("META-INF/sanity-rules.xml");

        if (log.isDebugEnabled()) log.debug("Found " + resources.size() + " sanity-rules.xml files in the classpath");

        for (URL resource : resources) {
            try {
                DeclaredRules dr = readDeclaredRules(resource.openStream());
                addAllDeclaredRules(dr.getDeclaredRule());

                if (log.isDebugEnabled())
                    log.debug("Loaded " + dr.getDeclaredRule().size() + " declared rules from: " + resource);

            } catch (Throwable t) {
                throw new SanityRuleException("Problem reading declared rules from resource: " + resource, t);
            }
        }
    }

    protected static DeclaredRules readDeclaredRules(InputStream is) {
        if (is == null) {
            throw new IllegalStateException("No sanity rules file found: " + RULES_XML_PATH);
        }

        DeclaredRules rules = null;
        try {
            rules = readRulesXml(is);
        } catch (JAXBException e) {
            throw new SanityRuleException(e);
        }
        return rules;
    }

    /**
     * //TODO: this is copied copied from private methods in AnnotationUtil
     */
    private static Collection<String> getClasspathElements() {
        String classPath = System.getProperty("java.class.path");

        URL[] classpathElements = URLClassPath.pathToURLs(classPath);

        Set<String> classPathItems = new HashSet<String>();

        for (URL cpElem : classpathElements) {
            classPathItems.add(cpElem.getFile());
        }

        return classPathItems;
    }

    /**
     * Goes through the stacktrace to get the dirs where the classes are (when they are not in a jar)
     * //TODO: this is copied copied from private methods in AnnotationUtil
     */
    private static Collection<String> getDirsFromStackTrace() {
        Set<String> dirs = new HashSet<String>();

        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            String className = ste.getClassName();
            Class clazz = null;

            try {
                clazz = Class.forName(className);

                String resClass = "/" + className.replaceAll("\\.", "/") + ".class";

                URL resUrl = clazz.getResource(resClass);

                String completeDir = null;
                if (resUrl != null) {
                    completeDir = resUrl.getFile();

                    if (!completeDir.contains(".jar!")) {
                        String dir = completeDir.substring(0, completeDir.length() - resClass.length());

                        if (dir != null && new File(dir).isDirectory()) {
                            dirs.add(dir);
                        }
                    }
                }


            } catch (ClassNotFoundException e) {
                // nothing
            }
        }

        return dirs;
    }


}