/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Properties;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:IntactAbstractMojo.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since 0.1
 */
public abstract class IntactAbstractMojo extends AbstractMojo {

    protected static final String NEW_LINE = System.getProperty( "line.separator" );

    private static final String PLUGIN_INFO_FILE = "plugin-info.properties";

    /**
     * Project instance
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * This is where build results go.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File directory;

    /**
     * This is where compiled classes go.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * This is where compiled test classes go.
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     * @readonly
     */
    private File testOutputDirectory;


    /**
     * Standard output file
     *
     * @parameter  expression="${intact.outputFile}
                   default-value="${project.build.directory}/output.log"
     * @required
     */
    private File outputFile;

    /**
     * Standard error file
     *
     * @parameter expression="${intact.errorFile}
     *            default-value="${project.build.directory}/error.log"
     * @required
     */
    private File errorFile;

    private PluginInfo pluginInfo;

    private Writer outputWriter;
    private Writer errorWriter;

    /**
     * Returns the PluginInfo created from the properties in a /plugin-info.properties file.
     * If this file does not exist, it returns null
     * @return the PluginInfo, or null
     */
    public PluginInfo getPluginInfo()
    {
        if (pluginInfo != null)
        {
            return pluginInfo;
        }
        
        File pluginFile = new File(IntactAbstractMojo.class.getResource(PLUGIN_INFO_FILE).getFile());

        if (pluginFile.exists())
        {
            Properties pluginProps = new Properties();

            try
            {
                pluginProps.load(new FileInputStream(pluginFile));

                String groupId = pluginProps.getProperty("plugin.groupId");
                String artifactId = pluginProps.getProperty("plugin.artifactId");
                String version = pluginProps.getProperty("version");

                PluginInfo info = new PluginInfo(groupId, artifactId, version);

                return info;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected void writeOutputln(String line) throws IOException
    {
        if (line == null)
        {
            return;
        }

        getOutputWriter().write(line+NEW_LINE);

    }

    protected void writeErrorln(String line) throws IOException
    {
        if (line == null)
        {
            return;
        }

        getErrorWriter().write(line+NEW_LINE);

    }

    public Writer getOutputWriter() throws IOException
    {
        if (outputWriter == null)
        {
            MojoUtils.prepareFile(outputFile, true);

            String title = "Standard output";
            if (getPluginInfo() != null)
            {
                title = title +" for "+pluginInfo.getArtifactId()+", v. "+pluginInfo.getVersion();
            }
            MojoUtils.writeHeaderToFile(title, "Default output file", outputFile);

            outputWriter = new FileWriter(outputFile);
        }
        return outputWriter;
    }

    public Writer getErrorWriter() throws IOException
    {
        if (errorWriter == null)
        {
            MojoUtils.prepareFile(outputFile, true);

            String title = "Standard error";
            if (getPluginInfo() != null)
            {
                title = title +" for "+pluginInfo.getArtifactId()+", v. "+pluginInfo.getVersion();
            }
            MojoUtils.writeHeaderToFile(title, "Default error file", outputFile);

            errorWriter = new FileWriter(errorFile);
        }

        return errorWriter;
    }


    public MavenProject getProject()
    {
        return project;
    }

    public void setProject(MavenProject project)
    {
        this.project = project;
    }

    public File getOutputFile()
    {
        return outputFile;
    }

    public File getErrorFile()
    {
        return errorFile;
    }


    public File getDirectory()
    {
        return directory;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public File getTestOutputDirectory()
    {
        return testOutputDirectory;
    }
}