/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Base class with the common attributes for the Uniprot export mojos
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public abstract class UniprotExportAbstractMojo extends IntactHibernateMojo
{
    protected static final String NEW_LINE = System.getProperty("line.separator");

    /**
    * Project instance
    * @parameter default-value="${project}"
    * @readonly
    */
    protected MavenProject project;

    /**
    * File containing the species
    * @parameter default-value="target/uniprot-export"
    * @required
    */
    protected File targetPath;

    /**
     * @parameter default-value="target/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    protected File hibernateConfig;

    /**
     * @parameter default-value="target/uniprot-export/line_exported_summary.log"
     */
    protected File summaryFile;

    /**
     * Whether to update the existing project files or overwrite them.
     *
     * @parameter expression="${overwrite}" default-value="false"
     */
    protected boolean overwrite;

    /**
     * @parameter default-value="uniprotlinks.dat"
     * @required
     */
    private String uniprotLinksFilename;

    /**
     * @parameter default-value="target/uniprot-export/uniprotlinks.dat"
     */
    private File uniprotLinksFile;

    protected File getUniprotLinksFile()
    {
        if (uniprotLinksFile != null && uniprotLinksFile.exists())
        {
            return uniprotLinksFile;
        }

        return new File(targetPath,uniprotLinksFilename);
    }


    public void setUniprotLinksFile(File uniprotLinksFile)
    {
        this.uniprotLinksFile = uniprotLinksFile;
    }

    public void setUniprotLinksFilename(String uniprotLinksFilename)
    {
        this.uniprotLinksFilename = uniprotLinksFilename;
    }

    protected void writeLineToSummary(String line) throws IOException
    {
        FileWriter writer = new FileWriter(summaryFile, true);
        writer.write(line+NEW_LINE);
        writer.close();
    }

    protected void mkParentDirs(File file)
    {
        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();
        }
    }

    public MavenProject getProject()
    {
        return project;
    }

    public File getTargetPath()
    {
        return targetPath;
    }

    public File getHibernateConfig()
    {
        return hibernateConfig;
    }

    public File getSummaryFile()
    {
        return summaryFile;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public String getUniprotLinksFilename()
    {
        return uniprotLinksFilename;
    }
}
