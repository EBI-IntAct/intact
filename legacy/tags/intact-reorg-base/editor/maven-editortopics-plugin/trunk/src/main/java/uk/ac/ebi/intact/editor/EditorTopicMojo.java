/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.editor;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.artifact.Artifact;
import uk.ac.ebi.intact.annotation.AnnotationUtil;
import uk.ac.ebi.intact.annotation.EditorTopic;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Collections;

/**
 * Generates a properties file with the topic names as keys and the classes as values
 *
 * @goal generate-topics
 * 
 * @phase process-sources
 */
public class EditorTopicMojo
        extends AbstractMojo
{

    /**
    * Project instance, used to add new source directory to the build.
    * @parameter default-value="${project}"
    * @required
    * @readonly
    */
    private MavenProject project;

    /**
    * The set of dependencies required by the project
    * @parameter default-value="${project.artifacts}"
    * @required
    * @readonly
    */
    private java.util.Set<Artifact> dependencies;

    /**
    * project-helper instance, used to make addition of resources
    * simpler.
    * @component
    */
    private MavenProjectHelper helper;

    /**
     * @parameter default-value="EditorTopics.properties"
     * @required
     */
    private String filename;

    /**
     * @parameter default-value="uk/ac/ebi/intact/application/editor"
     * @required
     */
    private String targetPath;


    public void execute() throws MojoExecutionException
    {
        getLog().info("Editor Topics Mojo in action");

        // we get the first folder of the package
        String baseDirFromPackage = targetPath.substring(0, targetPath.indexOf("/"));

        // and remove it for the targetPath. We need this because later we will need the name
        // of the folder up to the first package folder, when adding the resource
        targetPath = targetPath.substring(targetPath.indexOf("/")+1, targetPath.length());

        File outputResourcesDir = new File(project.getBuild().getOutputDirectory(), baseDirFromPackage);
        File tempDir = new File(outputResourcesDir, targetPath);

        if (!tempDir.exists())
        {
            tempDir.mkdirs();
        }

        File propertiesFile = new File(tempDir,filename);

        // going through the dependencies
        for (Artifact dependency : dependencies)
        {
            String depArtifactId = dependency.getArtifactId();

            // we search for the artifact intact-core
            if (depArtifactId.equals("intact-core"))
            {
                String depJar = dependency.getFile().toString();

                Properties properties = new Properties();

                try
                {
                    // Looking for the annotation
                    List<Class> classes = AnnotationUtil.getClassesWithAnnotationFromJar(EditorTopic.class, depJar);

                    for (Class clazz : classes)
                    {
                        String topicName = clazz.getSimpleName();

                        EditorTopic topic = (EditorTopic) clazz.getAnnotation(EditorTopic.class);

                        if (!topic.name().equals(EditorTopic.UNDEFINED))
                        {
                            topicName = topic.name();
                        }

                        properties.put(topicName, clazz.getName());
                    }


                    // properties file comments
                    StringBuffer fileComments = new StringBuffer();
                    fileComments.append("Editor Topics, from ");
                    fileComments.append(depArtifactId).append(" ");
                    fileComments.append(dependency.getVersion());

                    properties.store(new FileOutputStream(propertiesFile), fileComments.toString());

                    // Adding the resources
                    List includes = Collections.singletonList("*.properties");
                    List excludes = null;
                    helper.addResource(project, outputResourcesDir.toString(), includes, excludes);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                break;
            }
        }

    }

}
