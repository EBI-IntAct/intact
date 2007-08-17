/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.DeclarationVisitors;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    public void process()  {

        SanityRuleVisitor visitor = new SanityRuleVisitor();

        for (AnnotationTypeDeclaration atd : atds) {
            env.getMessager().printNotice("Collecting annotation "+atd);
            Collection<Declaration> decls = env.getDeclarationsAnnotatedWith(atd);
            for (Declaration decl : decls) {
                decl.accept(DeclarationVisitors.getDeclarationScanner(visitor, DeclarationVisitors.NO_OP));
            }
        }

        List<DeclaredRule> rules = visitor.getRules();

        try {
            File targetDir = createTargetDir();
            Writer writer = new FileWriter(targetDir);

            DeclaredRules jaxbRules = new DeclaredRules();
            jaxbRules.getDeclaredRule().addAll(rules);

            DeclaredRuleManager.writeRulesXml(jaxbRules, writer);

            writer.close();

        } catch (Exception e) {
           throw new SanityRuleException(e);
        }
    }


    private File createTargetDir() {
        String s = env.getOptions().get("-s");

        File targetDir = new File(s);

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        return targetDir;
    }



}