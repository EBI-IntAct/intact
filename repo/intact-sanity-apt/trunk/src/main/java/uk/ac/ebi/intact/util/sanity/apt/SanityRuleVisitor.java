/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.apt;

import com.sun.mirror.declaration.*;

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class SanityRuleVisitor extends AnnotationDeclarationVisitorCollector {

    HashMap<String,Collection<String>> target2rules = new HashMap();

    int counter = 1;

    public void print()
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("sanityAnnotation.txt"));
            out.write("aString");
            out.close();
        } catch (IOException e) {
        }
        System.out.println("\nSanity Rule:");
        System.out.println("--------------------");

        for (ClassDeclaration decl : this.getCollectedClassDeclations())
        {
            printClassSanityRule(decl);
        }

        System.out.println("--------------------");
        System.out.println(this.getCollectedMethodDeclations().size()+" methods with potential threats\n");
    }

    private void printClassSanityRule(ClassDeclaration d)
    {
        System.out.println("[PT-"+(counter++)+"] "+ d.getPackage()/*.getDeclaringType()*/ + "." + d.getSimpleName() +
                " (" + d.getPosition().line() + ")");
        String rule = d.getPackage()+ "." + d.getSimpleName();
        String targetOfTheRule = new String();


//        Collection<FieldDeclaration> fields = d.getFields();
//        for (FieldDeclaration field : fields ){
//            System.out.println(field.getConstantExpression());
//            System.out.println(field.getConstantValue().getClass().getName());
//        }
        for (AnnotationMirror annot : d.getAnnotationMirrors())
        {
            for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : annot.getElementValues().entrySet())
            {
                targetOfTheRule = "" + entry.getValue().getValue();
                System.out.println("\t"+entry.getKey().getSimpleName()+": "+entry.getValue().getValue());
            }
        }
        if(target2rules.containsKey(targetOfTheRule)){
            Collection<String> rules = target2rules.get(targetOfTheRule);
            rules.add(rule);
            target2rules.put(targetOfTheRule, rules);
        }else{
            Collection<String> rules = new ArrayList();
            rules.add(rule);
            target2rules.put(targetOfTheRule,rules);
        }



        /*
       System.out.println("Method simpleName    " + d.getSimpleName());
       System.out.println("Method docComment    " + d.getDocComment());
       System.out.println("Method returnType    " + d.getReturnType());
       System.out.println("Method parameter     " + d.getParameters());
       System.out.println("Method declaringType " + d.getDeclaringType());
       printAnnotationMirrors(d.getAnnotationMirrors()); */
    }


    public HashMap<String, Collection<String>> getTarget2rules() {
        return target2rules;
    }
}
