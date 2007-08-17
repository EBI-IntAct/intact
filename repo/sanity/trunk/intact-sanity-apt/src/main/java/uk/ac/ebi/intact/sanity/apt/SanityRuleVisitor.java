/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.apt;

import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import uk.ac.ebi.intact.sanity.apt.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.model.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class SanityRuleVisitor extends SimpleDeclarationVisitor {

    private List<Rule> rules;

    public SanityRuleVisitor() {
        this.rules = new ArrayList<Rule>();
    }

    @Override
    public void visitClassDeclaration(ClassDeclaration classDeclaration) {
        super.visitTypeDeclaration(classDeclaration);
    }

    protected void visitRule(ClassDeclaration classDeclaration)
    {
        SanityRule sanityRule = classDeclaration.getAnnotation(SanityRule.class);

        Class<?> targetClass = sanityRule.target();
        String ruleClassName = classDeclaration.getQualifiedName();
        String name = sanityRule.name();

        if (name.length() == 0) {
            name = ruleClassName;
        }

        Rule rule = new Rule();
        rule.setClazz(ruleClassName);
        rule.setTargetClass(targetClass.getName());
        rule.setName(name);

        /*
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
        System.out.println(this.getCollectedMethodDeclations().size()+" methods with potential threats\n");*/
    }



    public List<Rule> getRules() {
        return rules;
    }
}
