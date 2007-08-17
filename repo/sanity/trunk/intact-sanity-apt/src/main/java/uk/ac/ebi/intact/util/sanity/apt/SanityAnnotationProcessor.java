/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.apt;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.DeclarationVisitors;

import java.util.Set;
import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import uk.ac.ebi.intact.util.sanity.xmlPropertyFile.Generator;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class SanityAnnotationProcessor implements AnnotationProcessor {

    private final AnnotationProcessorEnvironment env;
    private final Set<AnnotationTypeDeclaration> atds;

    public SanityAnnotationProcessor(Set<AnnotationTypeDeclaration> atds,
                                     AnnotationProcessorEnvironment env) {
        this.atds = atds;
        this.env = env;
        this.env.getMessager().printNotice("Starting annotation process");

    }

    public void process() {
        SanityRuleVisitor visitor = new SanityRuleVisitor();
        PrintAnnotationVisitor printAnnotationVisitor = new PrintAnnotationVisitor();
        for (AnnotationTypeDeclaration atd : atds) {
            env.getMessager().printNotice("Collecting annotation "+atd);
            Collection<Declaration> decls = env.getDeclarationsAnnotatedWith(atd);
            for (Declaration decl : decls) {
                decl.accept(DeclarationVisitors.getDeclarationScanner(visitor, DeclarationVisitors.NO_OP));
            }
        }

        try {
            visitor.print();

            HashMap<String,Collection<String>> target2rules = visitor.getTarget2rules();
            Generator xmlGenerator = new Generator();
            Document propertiesDoc = xmlGenerator.createPropertiesDocument(target2rules);
            xmlGenerator.writeDomDocToXmlFile(propertiesDoc, "src/main/resources/META-INF/sanityRule.xml");

////            Set keySet = target2rules.keySet();
////            Iterator iterator = keySet.iterator();
////            System.out.println("WRITTING TO FILE");
////            try {
////                BufferedWriter out = new BufferedWriter(new FileWriter("/sanityRule.properties"));
////                DOMImplementationImpl impl = new DOMImplementationImpl();
////                while(iterator.hasNext()){
////                    String target = (String) iterator.next();
////                    Collection<String> rules = target2rules.get(target);
////                    System.out.println(target);
////                    for(String arule : rules){
////                        System.out.println("\t " + arule);
////                        out.write(target + "=" + arule + "\n");
////                    }
////                }
////                out.close();
////
//
//            } catch (IOException e) {
//                throw new IOException("The sanityRule.properties file couldn't be created : " + e.getMessage());
//            }
//            printAnnotationVisitor.print();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private Document createBlankDocumnent(){
        System.out.println("Creating Balnk Document...");
        Document doc = null;
        try{
            //Create instance of DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //Get the DocumentBuilder
            DocumentBuilder parser = factory.newDocumentBuilder();
            //Create blank DOM Document
                doc = parser.newDocument();
            Element targetList = doc.createElement("targetList");
            doc.appendChild(targetList);
        }catch(Exception e){
              System.out.println(e.getMessage());
            }
        return doc;

    }

    private Element createTargetElement(String name){
        Document doc = createBlankDocumnent();
        Element targetElement = doc.createElement("target");
        return targetElement;
    }



}