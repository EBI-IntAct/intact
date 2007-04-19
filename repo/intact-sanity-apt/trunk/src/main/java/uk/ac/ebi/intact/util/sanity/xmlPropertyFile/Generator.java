/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.xmlPropertyFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.io.File;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class Generator {
   public Document createBlankDocumnent(){
        System.out.println("Creating blank Document...");
        Document doc = null;
        try{
            //Create instance of DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //Get the DocumentBuilder
            DocumentBuilder parser = factory.newDocumentBuilder();
            //Create blank DOM Document
                doc = parser.newDocument();

        }catch(Exception e){
              System.out.println(e.getMessage());
            }
        return doc;

    }

    public Element createRootNode(Document doc){
        Element root = doc.createElement("targetList");
        return root;
    }

    public Element createTargetNode(Document doc, String target, Collection<String> rules){

        Element domTarget = doc.createElement("target");
        domTarget.setAttribute("id",target);
//        Element domTargetName = doc.createElement("targetName");
//        domTargetName.setTextContent(target);
//        domTarget.appendChild(domTargetName);

        Element domRuleList = doc.createElement("ruleList");
        for(String rule : rules){
            Element domRule = doc.createElement("rule");

            Element domRuleName = doc.createElement("ruleName");
            domRuleName.setTextContent(rule);
            domRule.appendChild(domRuleName);

            domRuleList.appendChild(domRule);
        }

        domTarget.appendChild(domRuleList);

        return domTarget;
    }



    public Document createPropertiesDocument(HashMap<String,Collection<String>> target2rules){

        Document doc = createBlankDocumnent();

        Element root = createRootNode(doc);

        Set keySet = target2rules.keySet();
        Iterator iterator = keySet.iterator();
        while(iterator.hasNext()){
            String target = (String) iterator.next();
            Collection<String> rules = target2rules.get(target);
            Element targetDom = createTargetNode(doc, target, rules);
            root.appendChild(targetDom);
        }

        doc.appendChild(root);

        return doc;

    }

    public void writeDomDocToXmlFile(Document doc, String fileName) throws TransformerException {

            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            File file = new File(fileName);
        System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
        System.out.println("file.getPath() = " + file.getPath());
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
    }


}
