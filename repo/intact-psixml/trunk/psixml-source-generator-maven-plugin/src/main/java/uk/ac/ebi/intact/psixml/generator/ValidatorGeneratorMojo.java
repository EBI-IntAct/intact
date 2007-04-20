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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.velocity.VelocityContext;
import psidev.psi.mi.annotations.PsiXmlElement;
import uk.ac.ebi.intact.annotation.AnnotationUtil;
import uk.ac.ebi.intact.plugin.IntactAbstractMojo;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 * Change this comments and the goal name accordingly
 *
 * @goal generate-validators
 * @phase process-resources
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
     * An example file
     *
     * @parameter default-value="${project.build.directory}/dummy.txt"
     */
    private File dummyFile;

    /**
     * Path where the classes will be generated
     *
     * @parameter default-value="${project.build.directory}/target/generated"
     * @required
     */
    private String targetPath;

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

        if (project != null) {
            context.put("artifactId", project.getArtifactId());
            context.put("version", project.getVersion());
        }

        // going through the dependencies
        for (Artifact artifact : (Collection<Artifact>) project.getArtifacts()) {
            String depJar = artifact.getFile().toString();

            System.out.println("Processing artifact: " + depJar);

            try {
                // Looking for the annotation
                List<Class> classes = AnnotationUtil.getClassesWithAnnotationFromJar(PsiXmlElement.class, depJar);

                for (Class clazz : classes) {
                    System.out.println(clazz);

                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*
        File outputFile = new File(tempDir, filename);

         // create a temporary copy of the template, so we avoid classpath issues later
        File templateFile = null;
        try
        {
            templateFile = createTempFileFromTemplate();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new MojoExecutionException("Problem creating temporary copy of the template", e);
        }

        Template template = null;
        try
        {
            Properties props = new Properties();
            props.setProperty( VelocityEngine.RESOURCE_LOADER, "file" );
            props.setProperty( "file."+VelocityEngine.RESOURCE_LOADER+".path",
                        templateFile.getParent() );

            Velocity.init(props);

            template = Velocity.getTemplate(templateFile.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MojoExecutionException("Couldn't get template: " + templateFile);
        }

        // write the resulting file with velocity
        try
        {
            Writer writer = new FileWriter(outputFile);
            template.merge(context, writer);
            writer.close();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Problem writing to file with name: " + outputFile, e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MojoExecutionException("Couldn't create generated classes", e);
        }
         */

        if (targetPath != null) {
            // Adding the resources
            List includes = Collections.singletonList("*/**");
            List excludes = null;

            helper.addResource(project, targetPath, includes, excludes);
        }

    }

    private File createTempFileFromTemplate() throws IOException {
        String templateFilename = "ElementValidator.vm";
        File temporaryFile = File.createTempFile("ElementValidator", ".vm");
        temporaryFile.deleteOnExit();

        FileWriter writer = null;

        try {
            InputStream is = getClass().getResourceAsStream(templateFilename);
            writer = new FileWriter(temporaryFile);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            writer.close();
        }

        return temporaryFile;
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
}
