/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Collection;

import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.plugin.uniprotexport.drcomparator.DrComparatorReport;
import uk.ac.ebi.intact.plugin.uniprotexport.drcomparator.DrComparator;

/**
 * Compares two DR files and reports the changes
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 *
 * @goal compare-dr
 */
public class DrFileComparatorMojo extends AbstractMojo
{

    protected static final String NEW_LINE = System.getProperty("line.separator");

    /**
    * File 1
    * @parameter
    * @required
    */
    private File newDrFile;

    /**
    * File 2
    * @parameter
    * @required
    */
    private File oldDrFile;

    /**
    * File that contains a list of the new IDs
    * @parameter default-value="target/uniprotexport/drFileComparator_new.log
    * @required
    */
    private File newIdsFile;

    /**
    * File that contains a list of the oldIds
    * @parameter default-value="target/uniprotexport/drFileComparator_removed.log
    * @required
    */
    private File removedIdsFile;


    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("DrFileComparatorMojo in action");

        checkIfExists(newDrFile);
        checkIfExists(oldDrFile);

        getLog().info("New IDs will be written to file: "+newIdsFile);
        getLog().info("Removed IDs will be written to file: "+removedIdsFile);


        if (!newIdsFile.getParentFile().exists())
        {
           newIdsFile.getParentFile().mkdirs();
        }

        if (!removedIdsFile.getParentFile().exists())
        {
           removedIdsFile.getParentFile().mkdirs(); 
        }

        getLog().info("Comparing and processing files...");

        try
        {
            DrComparatorReport report = DrComparator.compareFiles(newDrFile, oldDrFile);
            writeCollectionToFile(newIdsFile, report.getAdded());
            writeCollectionToFile(removedIdsFile, report.getRemoved());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Problem comparing files", e);
        }
    }


    private void checkIfExists(File file) throws MojoExecutionException
    {
        if (!file.exists())
        {
            throw new MojoExecutionException("File does not exist: "+file);
        }
    }

    private void writeCollectionToFile(File file, Collection<String> col) throws IOException
    {
        FileWriter writer = new FileWriter(file);

        int i=0;

        for (String id : col)
        {
            writer.write(id+NEW_LINE);
            i++;

            if (i % 100 == 0)
            {
                writer.flush();
            }
        }

        writer.close();
    }


    public File getNewDrFile()
    {
        return newDrFile;
    }

    public void setNewDrFile(File newDrFile)
    {
        this.newDrFile = newDrFile;
    }

    public File getOldDrFile()
    {
        return oldDrFile;
    }

    public void setOldDrFile(File oldDrFile)
    {
        this.oldDrFile = oldDrFile;
    }

    public File getNewIdsFile()
    {
        return newIdsFile;
    }

    public void setNewIdsFile(File newIdsFile)
    {
        this.newIdsFile = newIdsFile;
    }

    public File getRemovedIdsFile()
    {
        return removedIdsFile;
    }

    public void setRemovedIdsFile(File removedIdsFile)
    {
        this.removedIdsFile = removedIdsFile;
    }
}
