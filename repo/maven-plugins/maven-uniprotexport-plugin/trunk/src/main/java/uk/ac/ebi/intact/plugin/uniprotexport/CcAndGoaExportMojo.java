/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.Set;

import uk.ac.ebi.intact.util.MemoryMonitor;
import uk.ac.ebi.intact.util.uniprotExport.DRLineExport;
import uk.ac.ebi.intact.util.uniprotExport.CCLineExport;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * TODO comment this!
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
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * @parameter default-value="uniprotcomments.dat"
     * @required
     */
    protected String uniprotCommentsFilename;

    /**
     * @parameter default-value="gene_association.goa_intact"
     * @required
     */
    protected String goaFilename;

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

        getLog().info( "GOA exportwill be saved in: " + goaExportFile );

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
            BufferedWriter goaWriter = new BufferedWriter(new FileWriter(ccExportFile));

            CCLineExport exporter = new CCLineExport( ccWriter, goaWriter );
            //exporter.setDebugEnabled( debugEnabled );
            //exporter.setDebugFileEnabled( debugFileEnabled );

            // launch the CC export
            exporter.generateCCLines( uniprotIDs );
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Problem exporting CC and GOA", e);
        }

    }
}
