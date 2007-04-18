/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.application.dataConversion.ZipFileGenerator;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Aug-2006</pre>
 *
 * @goal zip-xml
 * @phase process-resources
 */
public class ClusterXmlToZipMojo extends PsiXmlGeneratorAbstractMojo
{

    private static final Log log = LogFactory.getLog(ClusterXmlToZipMojo.class);

    public void executeIntactMojo() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("ClusterXmlToZipMojo in action");

        initialize();
        
        ZipFileGenerator.clusterAllXmlFilesFromDirectory( targetPath, true );
    }
}
