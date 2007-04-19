/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.apt;

import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.PackageDeclaration;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AnnotationDeclarationVisitorCollector extends SimpleDeclarationVisitor {

  private Set<MethodDeclaration> collectedMethodDeclations = new HashSet<MethodDeclaration>();
  private Set<ClassDeclaration> collectedClassDeclations = new HashSet<ClassDeclaration>();
  private Set<InterfaceDeclaration> collectedInterfaceDeclations = new HashSet<InterfaceDeclaration>();
  private List<PackageDeclaration> collectedPackageDeclations = new ArrayList<PackageDeclaration>();

  public Set<MethodDeclaration> getCollectedMethodDeclations() {
    return collectedMethodDeclations;
  }

  public Set<ClassDeclaration> getCollectedClassDeclations() {
    return collectedClassDeclations;
  }

  public Set<InterfaceDeclaration> getCollectedInterfaceDeclations() {
    return collectedInterfaceDeclations;
  }

  public List<PackageDeclaration> getCollectedPackageDeclations() {
    return collectedPackageDeclations;
  }


  public void visitMethodDeclaration(MethodDeclaration d) {
    if (!collectedMethodDeclations.contains(d)
        && !d.getAnnotationMirrors().isEmpty()) {
      collectedMethodDeclations.add(d);
    }
  }

  public void visitPackageDeclaration(PackageDeclaration
    d) {
    if (!collectedPackageDeclations.contains(d)
        && !d.getAnnotationMirrors().isEmpty()) {
      collectedPackageDeclations.add(d);
    }

  }
  public void visitInterfaceDeclaration(InterfaceDeclaration
    d) {
    // TODO why this needed????
    visitPackageDeclaration(d.getPackage());
    if (!collectedInterfaceDeclations.contains(d)
        && !d.getAnnotationMirrors().isEmpty()) {
      collectedInterfaceDeclations.add(d);
    }
  }


  public void visitClassDeclaration(ClassDeclaration d) {
    // TODO why this needed????
    visitPackageDeclaration(d.getPackage());

    if (!collectedClassDeclations.contains(d)
        && !d.getAnnotationMirrors().isEmpty()) {
      collectedClassDeclations.add(d);
    }
  }
}