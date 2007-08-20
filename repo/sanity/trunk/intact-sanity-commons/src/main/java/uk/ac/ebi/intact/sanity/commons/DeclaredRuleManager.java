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

import sun.misc.URLClassPath;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:RuleManager.java 9493 2007-08-17 14:02:49Z baranda $
 */
public class DeclaredRuleManager {

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
                throw new SanityRuleException("Found declared rule with a target class not found in the classpath: "+rule.getTargetClass());
            }

            if (targetClass.isAssignableFrom(ruleTargetClass)) {
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
        JAXBContext jc = JAXBContext.newInstance(Thread.currentThread().getContextClassLoader().loadClass(DeclaredRule.class.getName()));
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(rules, writer);
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
        // check dirs first
        for (String dir : getDirsFromStackTrace()) {
            File candidateFile = new File(dir, RULES_XML_PATH);

            if (candidateFile.isFile()) {
                DeclaredRules dr = readDeclaredRules(new FileInputStream(candidateFile));
                addAllDeclaredRules(dr.getDeclaredRule());
            }
        }

        for (String classPathFilePath : getClasspathElements()) {
            File file = new File(classPathFilePath);

            InputStream declaredRulesStream = null;

            if (file.isDirectory()) {
                File candidateFile = new File(file, RULES_XML_PATH);

                if (candidateFile.isFile()) {
                    declaredRulesStream = new FileInputStream(candidateFile);
                }
            } else {
                JarFile jarFile = new JarFile(classPathFilePath);

                // get the xml file from META-INF (if existing) - remove the first slash
                String rulesFile = RULES_XML_PATH;

                if (RULES_XML_PATH.startsWith("/")) {
                    rulesFile = RULES_XML_PATH.substring(1, RULES_XML_PATH.length());
                }

                JarEntry jarEntry = jarFile.getJarEntry(rulesFile);

                if (jarEntry != null) {
                    declaredRulesStream = jarFile.getInputStream(jarEntry);
                }
            }

            if (declaredRulesStream != null) {
                DeclaredRules dr = readDeclaredRules(declaredRulesStream);
                addAllDeclaredRules(dr.getDeclaredRule());
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