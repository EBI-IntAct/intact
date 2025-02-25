/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.sql.SQLException;

import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.impl.StandaloneSession;

/**
 * Base class with the common attributes for the Uniprot export mojos
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public abstract class UniprotExportAbstractMojo extends AbstractMojo
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

    private boolean initialized;

    protected void initialize() throws MojoExecutionException
    {
        if (initialized)
        {
            return;
        }

        getLog().info("Using hibernate cfg file: "+hibernateConfig);

        if (!hibernateConfig.exists())
        {
            throw new MojoExecutionException("No hibernate config file found: "+hibernateConfig);
        }

        if (!targetPath.exists())
        {
            targetPath.mkdirs();
        }

        // configure the context
        IntactSession session = new StandaloneSession();

        if (System.getProperty("institution") == null)
        {
            session.setInitParam(IntactConfigurator.INSTITUTION_LABEL, "ebi");
        }
        
        CustomCoreDataConfig testConfig = new CustomCoreDataConfig("PsiXmlGeneratorTest", hibernateConfig, session);
        testConfig.initialize();
        IntactContext.initContext(testConfig, session);

        try
        {
            getLog().info( "Database instance: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbName() );
        }
        catch (SQLException e)
        {
            throw new MojoExecutionException("Error loading database name", e);
        }

        getLog().info( "User: " + IntactContext.getCurrentInstance().getUserContext().getUserId() );

        initialized = true;
    }

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
}
