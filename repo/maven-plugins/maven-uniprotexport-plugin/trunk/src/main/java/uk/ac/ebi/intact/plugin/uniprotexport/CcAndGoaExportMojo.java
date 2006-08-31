/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.util.MemoryMonitor;
import uk.ac.ebi.intact.util.Utilities;
import uk.ac.ebi.intact.util.uniprotExport.CCLineExport;
import uk.ac.ebi.intact.util.uniprotExport.CcLineExportProgressThread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Creates a file with the uniprot comments for the interactions and a file with the GOA Lines
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 *
 * @goal cc-goa
 * @phase process-resources
 */
public class CcAndGoaExportMojo extends UniprotExportAbstractMojo
{
    /**
     * Name of the uniprot comments file
     * @parameter default-value="uniprotcomments.dat"
     * @required
     */
    protected String uniprotCommentsFilename;

    /**
     * Name of the goa file
     * @parameter default-value="gene_association.goa_intact"
     * @required
     */
    protected String goaFilename;

    /**
     * If true, the GOA File will be Gzipped
     * @parameter default-value="false"
     */
    protected boolean gzipGoa;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("CcAndGoaExportMojo in action");

        initialize();

        File ccExportFile = new File(targetPath, uniprotCommentsFilename);
        File goaExportFile = new File(targetPath, goaFilename);

        getLog().info( "CC export (uniprot comments) will be saved in: " + ccExportFile );

        if (ccExportFile.exists() && !overwrite)
        {
            throw new MojoExecutionException("CC Export file already exist and overwrite is set to false: "+ccExportFile);
        }

        getLog().info( "GOA export will be saved in: " + goaExportFile );

        if (goaExportFile.exists() && !overwrite)
        {
            throw new MojoExecutionException("GOA Export file already exist and overwrite is set to false: "+goaExportFile);
        }

        new MemoryMonitor();

        try
        {
            File drExportFile = getUniprotLinksFile();
            getLog().info("Loading uniprot IDs from file: "+drExportFile);

            if (!drExportFile.exists())
            {
                throw new MojoExecutionException("File with uniprot links (DR export) not found");
            }

            Set<String> uniprotIDs = CCLineExport.getEligibleProteinsFromFile( drExportFile.toString() );
            getLog().info( uniprotIDs.size() + " DR protein IDs loaded.");

            BufferedWriter ccWriter = new BufferedWriter(new FileWriter(ccExportFile));
            BufferedWriter goaWriter = new BufferedWriter(new FileWriter(goaExportFile));

            CCLineExport exporter = new CCLineExport( ccWriter, goaWriter );

            // thread to check progress
            CcLineExportProgressThread progressThread = new CcLineExportProgressThread(exporter, uniprotIDs.size(), System.out);
            progressThread.setSecondsWithinChecks(60);
            progressThread.start();

            // launch the CC export
            exporter.generateCCLines( uniprotIDs );

            ccWriter.close();
            goaWriter.close();

            writeLineToSummary("CC Lines: "+exporter.getCcLineCount());
            writeLineToSummary("GOA Lines: "+exporter.getGoaLineCount());
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Problem exporting CC and GOA", e);
        }

        try
        {
            if (gzipGoa)
            {
                getLog().debug("Gzipping GOA File");
                Utilities.gzip(goaExportFile, new File(goaExportFile.getParent(), goaExportFile.getName()+".gz"), true);
            }
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Failed gzipping the GOA file", e);
        }
    }
}
