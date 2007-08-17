/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.apt;

import com.sun.mirror.declaration.*;
import com.sun.tools.apt.mirror.declaration.EnumConstantDeclarationImpl;

import java.util.Collection;
import java.util.Map;
/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

    public class PrintAnnotationVisitor extends AnnotationDeclarationVisitorCollector {

      public void print() {
        for (ClassDeclaration decl : getCollectedClassDeclations()) {
          printClassDeclaration(decl);
        }
      }

      private void printClassDeclaration(ClassDeclaration d) {
        System.out.println("Class simpleName    " + d.getSimpleName());
        System.out.println("Class package       " + d.getPackage());
        System.out.println("Class qualifiedName " + d.getQualifiedName());
        System.out.println("Class docComment    " + d.getDocComment());
        System.out.println("Class superclass    " + d.getSuperclass());
        printAnnotationMirrors(d.getAnnotationMirrors());
        printMethods(d);
        System.out.println("++++++++++++++++++++++++++++++++++++++");
      }

      private void printMethodDeclaration(MethodDeclaration d) {
        System.out.println("Method simpleName    " + d.getSimpleName());
        System.out.println("Method docComment    " + d.getDocComment());
        System.out.println("Method returnType    " + d.getReturnType());
        System.out.println("Method parameter     " + d.getParameters());
        System.out.println("Method declaringType " + d.getDeclaringType());
        printAnnotationMirrors(d.getAnnotationMirrors());
      }

      private void printAnnotationMirrors(Collection<AnnotationMirror> mirrors) {
        for (AnnotationMirror mirror : mirrors) {
          System.out.println("========================");
          Map<AnnotationTypeElementDeclaration,
              AnnotationValue> elementValues = mirror.getElementValues();
          printAnnotationTypeDeclaration(mirror.getAnnotationType().getDeclaration());
          for (AnnotationTypeElementDeclaration decl : mirror.getAnnotationType().getDeclaration().getMethods()) {
            System.out.println("-------------------");
            printAnnotationTypeElementDeclaration(decl);
            if (elementValues.containsKey(decl)) {
              AnnotationValue value = elementValues.get(decl);
              System.out.println("Type Element value=" + value.getValue());
            }

          }

        }
      }

      public void printMethods(ClassDeclaration d) {
        for (MethodDeclaration decl : getCollectedMethodDeclations()) {
          if (d.getQualifiedName().
              equals(decl.getDeclaringType().getQualifiedName())) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            printMethodDeclaration(decl);
          }
        }
        if (d.getSuperclass() != null) {
          printMethods(d.getSuperclass().getDeclaration());
        }
      }

      public void printAnnotationTypeDeclaration(AnnotationTypeDeclaration d) {
        System.out.println("Type qualifiedName " + d.getQualifiedName());
      }

      public void printAnnotationTypeElementDeclaration(AnnotationTypeElementDeclaration d) {

        System.out.println("Type Element simpleName    " + d.getSimpleName());
        System.out.println("Type Element returnType    " + d.getReturnType());
        if (d.getDefaultValue() != null) {
          System.out.println("Type Element defaultValue  " + d.getDefaultValue());
          if (d.getDefaultValue().getValue() instanceof EnumConstantDeclarationImpl) {
            EnumConstantDeclarationImpl impl = ((EnumConstantDeclarationImpl) d.getDefaultValue().getValue());
            System.out.println("Type Element Enum simple Name " + impl.getSimpleName());
            System.out.println("Type Element Enum type " + impl.getType());
          }
        }
      }

}