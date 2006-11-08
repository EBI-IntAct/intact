/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:IntactAbstractMojo.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since 0.1
 */
public abstract class IntactAbstractMojo extends AbstractMojo {

    protected static final String NEW_LINE = System.getProperty( "line.separator" );

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

    private Writer outputWriter;
    private Writer errorWriter;

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
            outputWriter = new FileWriter(outputFile);
        }
        return outputWriter;
    }

    public Writer getErrorWriter() throws IOException
    {
        if (errorWriter == null)
        {
            MojoUtils.prepareFile(outputFile, true);
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