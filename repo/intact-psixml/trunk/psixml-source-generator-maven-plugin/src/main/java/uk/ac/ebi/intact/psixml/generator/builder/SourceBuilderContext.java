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
package uk.ac.ebi.intact.psixml.generator.builder;

import org.apache.velocity.VelocityContext;

import java.io.File;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SourceBuilderContext {

    private File[] dependencyJars;
    private String generatedPackage;
    private File targetPath;
    private VelocityContext velocityContext;

    public SourceBuilderContext(VelocityContext context, String generatedPackage, File targetPath)
    {
        this.velocityContext = context;
        this.generatedPackage = generatedPackage;
        this.targetPath = targetPath;
    }

    public File getOutputDir()
    {
        String packageDir = generatedPackage.replaceAll("\\.", "/");
        File outputDir = new File(targetPath, packageDir);

        return outputDir;
    }

    public File[] getDependencyJars() {
        return dependencyJars;
    }

    public void setDependencyJars(File[] dependencyJars) {
        this.dependencyJars = dependencyJars;
    }

    public String getGeneratedPackage() {
        return generatedPackage;
    }

    public File getTargetPath() {
        return targetPath;
    }

    public VelocityContext getVelocityContext() {
        return velocityContext;
    }
}