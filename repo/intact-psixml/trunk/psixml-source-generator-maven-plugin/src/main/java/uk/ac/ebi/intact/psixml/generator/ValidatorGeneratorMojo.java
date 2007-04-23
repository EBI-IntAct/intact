/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.psixml.generator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.artifact.Artifact;
import org.apache.velocity.VelocityContext;
import uk.ac.ebi.intact.plugin.IntactAbstractMojo;
import uk.ac.ebi.intact.psixml.generator.builder.SourceBuilder;
import uk.ac.ebi.intact.psixml.generator.builder.SourceBuilderContext;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 * Change this comments and the goal name accordingly
 *
 * @goal generate-validators
 * @phase generate-sources
 */
public class ValidatorGeneratorMojo
        extends IntactAbstractMojo {

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * project-helper instance, used to make addition of resources
     * simpler.
     *
     * @component
     */
    private MavenProjectHelper helper;

    /**
     * Path where the classes will be generated
     *
     * @parameter default-value="${project.build.directory}/target/generated"
     * @required
     */
    private String targetPath;

    /**
     * Path where the classes will be generated
     *
     * @parameter default-value="uk.ac.ebi.intact.psixml.generated"
     * @required
     */
    private String generatedPackage;

    /**
     * Path where the classes will be generated
     *
     * @parameter default-value="uk.ac.ebi.intact.psixml.generator.AnnotationSourceBuilder"
     * @required
     */
    private String sourceBuilderClass;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void execute()
            throws MojoExecutionException, MojoFailureException {
        File tempDir = new File(targetPath);

        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // create velocity context
        VelocityContext context = new VelocityContext();
        context.put("mojo", this);
        context.put("artifactId", project.getArtifactId());
        context.put("version", project.getVersion());

        SourceBuilderContext sbContext = new SourceBuilderContext(context, generatedPackage, new File(targetPath));
        sbContext.setDependencyJars(getDependencyJars());

        SourceBuilder builder = null;
        try {
            builder = newInstanceOfSourceBuilder(sourceBuilderClass);
        } catch (Exception e) {
            throw new MojoExecutionException("Problem instantiating class for name: " + sourceBuilderClass, e);
        }

        try {
            builder.generateClasses(sbContext);
        } catch (Exception e) {
            throw new MojoExecutionException("Problem creating class from template", e);
        }

        if (targetPath != null) {
            // Adding the resources
            List includes = Collections.singletonList("*/**");
            List excludes = null;

            helper.addResource(project, targetPath, includes, excludes);
        }

    }

    private static SourceBuilder newInstanceOfSourceBuilder(String sourceBuilderClassName) throws Exception {
        Class sourceBuilderClass = Class.forName(sourceBuilderClassName);

        return (SourceBuilder) sourceBuilderClass.newInstance();
    }

    private File[] getDependencyJars()
    {
        Set<Artifact> artifacts = project.getArtifacts();

        File[] dependencyJars = new File[artifacts.size()];
        int i=0;

        for (Artifact artifact : artifacts)
        {
            dependencyJars[i] = artifact.getFile();
            i++;
        }

        return dependencyJars;
    }

    /**
     * Implementation of abstract method from superclass
     */
    public MavenProject getProject() {
        return project;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getSourceBuilderClass() {
        return sourceBuilderClass;
    }
}
