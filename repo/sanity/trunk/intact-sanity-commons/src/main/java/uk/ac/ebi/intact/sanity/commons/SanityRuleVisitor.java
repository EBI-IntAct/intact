/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons;

import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import uk.ac.ebi.intact.sanity.commons.annotation.Ignore;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;

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

    private List<DeclaredRule> rules;

    public SanityRuleVisitor() {
        this.rules = new ArrayList<DeclaredRule>();
    }

    @Override
    public void visitClassDeclaration(ClassDeclaration classDeclaration) {
        visitRule(classDeclaration);
    }

    protected void visitRule(ClassDeclaration classDeclaration)
    {
        Ignore ignoredRule = classDeclaration.getAnnotation(Ignore.class);

        if (ignoredRule != null) {
            // skip processing the rule
            return;
        }

        SanityRule sanityRule = classDeclaration.getAnnotation(SanityRule.class);

        String targetClassName = null;

        for (AnnotationMirror annotMirror : classDeclaration.getAnnotationMirrors()) {
            for (AnnotationTypeElementDeclaration ated : annotMirror.getElementValues().keySet()) {
                if (ated.getSimpleName().equals("target")) {
                    AnnotationValue annotValue = annotMirror.getElementValues().get(ated);
                    targetClassName = annotValue.getValue().toString();
                    break;
                }
            }
        }

        String ruleClassName = classDeclaration.getQualifiedName();
        String name = sanityRule.name();

        if (name.length() == 0) {
            name = ruleClassName;
        }

        DeclaredRule rule = new DeclaredRule();
        rule.setRuleClass(ruleClassName);
        rule.setTargetClass(targetClassName);
        rule.setRuleName(name);

        rules.add(rule);
    }


    public List<DeclaredRule> getRules() {
        return rules;
    }
}
