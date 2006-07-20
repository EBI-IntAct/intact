/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.hibernate;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * Generates a properties file with the topic names as keys and the classes as values
 *
 * @goal generate-config
 * 
 * @phase generate-sources
 */
public class HibernateConfigCreatorMojo
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
     * @parameter default-value="/"
     * @required
     */
    private String targetPath;

    /**
     * @parameter default-value="hibernate.cfg.xml"
     * @required
     */
    private String filename;

    /**
     * @parameter default-value="false"
     * @required
     */
    private boolean showSql;

    /**
     * @parameter default-value="true"
     * @required
     */
    private boolean formatSql;

    /**
     * @parameter default-value="none"
     * @required
     */
    private String hbm2ddlAuto;

    /**
     * @parameter
     * @required
     */
    private String dialect;

    /**
     * @parameter
     * @required
     */
    private String driver;

    /**
     * @parameter
     * @required
     */
    private String url;

    /**
     * @parameter
     * @required
     */
    private String user;

    /**
     * @parameter
     */
    private String password;

    /**
     * @parameter default-value="org.hibernate.connection.C3P0ConnectionProvider"
     * @required
     */
    private String connectionProviderClass;

    public void execute() throws MojoExecutionException
    {
        getLog().info("Hibernate Mojo in action");

        // we get the first folder of the package
        String baseDirFromPackage = targetPath.substring(0, targetPath.indexOf("/"));

        // and remove it for the targetPath. We need this because later we will need the name
        // of the folder up to the first package folder, when adding the resource
        targetPath = targetPath.substring(targetPath.indexOf("/")+1, targetPath.length());

        File outputResourcesDir;

        outputResourcesDir = new File(project.getBuild().getOutputDirectory(), baseDirFromPackage);
        File tempDir = new File(outputResourcesDir, targetPath);

        if (!tempDir.exists())
        {
            tempDir.mkdirs();
        }

        File outputFile = new File(tempDir, filename);

        try
        {
            Writer writer = new FileWriter(outputFile);

            InputStream is = getClass().getResourceAsStream("hibernate.cfg.xml.template");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null)
            {
                line = filterLine(line);
                writer.write(line+"\n");
            }

            writer.close();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Could create file", e);
        }

         // Adding the resources
         List includes = Collections.singletonList(filename);
         List excludes = null;

        helper.addResource(project, outputResourcesDir.toString(), includes, excludes);

    }

    private String filterLine(String line)
    {
        line = line.replaceAll("\\$\\{db\\.dialect\\}", dialect);
        line = line.replaceAll("\\$\\{db\\.driver\\}", driver);
        line = line.replaceAll("\\$\\{db\\.url\\}", url);
        line = line.replaceAll("\\$\\{db\\.user\\}", user);
        line = line.replaceAll("\\$\\{db\\.password\\}", password);
        line = line.replaceAll("\\$\\{connection\\.provider_class\\}", connectionProviderClass);
        line = line.replaceAll("\\$\\{format_sql\\}", String.valueOf(formatSql));
        line = line.replaceAll("\\$\\{show_sql\\}", String.valueOf(showSql));
        line = line.replaceAll("\\$\\{hbm2ddl\\.auto\\}", hbm2ddlAuto);

        return line;
    }

}
