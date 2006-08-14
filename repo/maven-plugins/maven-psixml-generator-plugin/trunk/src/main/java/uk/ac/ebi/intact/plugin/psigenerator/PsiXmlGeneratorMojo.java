/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.application.dataConversion.ExperimentListItem;
import uk.ac.ebi.intact.application.dataConversion.NewFileGenerator;
import uk.ac.ebi.intact.application.dataConversion.PsiVersion;
import uk.ac.ebi.intact.application.dataConversion.ZipFileGenerator;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.CvMapping;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Creates PSI XML files from the database
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:PsiXmlGeneratorMojo.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since <pre>04/08/2006</pre>
 *
 * @goal psi
 * @phase process-resources
 */
public class PsiXmlGeneratorMojo extends PsiXmlGeneratorAbstractMojo
{

    /**
    * File containing the Controlled Vocabularies reverse mapping
    * @parameter default-value="reverseMapping.txt"
    */
    protected File reverseMappingFile;

    /**
    * Psi Versions
    * @parameter
     * @required
    */
    protected List<Version> psiVersions;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("PsiXmlGeneratorMojo in action");

        getLog().debug("Reverse mapping file: "+getReverseMapping());

        getLog().info("Classifying and writing classification by species");
        writeClassificationBySpeciesToFile();

        getLog().info("Writing classifications by publications");
        writeClassificationByPublicationsToFile();

        CvMapping mapping = new CvMapping();
        mapping.loadFile(getReverseMapping());

        Collection<ExperimentListItem> items = generateAllClassifications();

        getLog().info("Going to generate "+items.size()+" PSI-MI xml files for each of this versions: "+psiVersions);

        // create xml files
        try
        {
            for (Version version : psiVersions)
            {
                int count = 0;

                getLog().info("Generating files for version: "+version);
                for (ExperimentListItem item : items)
                {
                    getLog().debug("Exporting: "+item+" (PSI: "+version.getNumber()+")");
                    NewFileGenerator.writePsiData(item, PsiVersion.valueOf(version.getNumber()), mapping,
                                                  new File(targetPath, version.getFolderName()), false);
                    count++;

                    if (count % 100 == 0)
                    {
                        getLog().info("Exported "+count+" experiments");
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Error creating Xml files: ", e);
        }

        if (zipXml)
        {
            getLog().info("Clustering and zipping files");
            ZipFileGenerator.clusterAllXmlFilesFromDirectory( targetPath, true );
        }
    }

    private File getReverseMapping()
    {
        // if the reverseMappingFile does not exist, use the internal one
        if (reverseMappingFile != null && reverseMappingFile.exists())
        {
            return reverseMappingFile;
        }

        return new File(PsiXmlGeneratorMojo.class.getResource("/reverseMapping.txt").getFile());
    }
}