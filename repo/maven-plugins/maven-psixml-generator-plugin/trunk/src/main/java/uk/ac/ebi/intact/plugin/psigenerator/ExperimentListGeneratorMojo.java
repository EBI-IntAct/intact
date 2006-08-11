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

/**
 * Generates the list of experiments
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:ExperimentListGeneratorMojo.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since <pre>11-Aug-2006</pre>
 *
 * @goal classification
 * @phase process-resources
 */
public class ExperimentListGeneratorMojo extends PsiXmlGeneratorAbstractMojo
{

    private static final Log log = LogFactory.getLog(ExperimentListGeneratorMojo.class);

    public void execute() throws MojoExecutionException, MojoFailureException
    {
       log.info("ExperimentListGeneratorMojo in action");

        writeClassificationBySpeciesToFile();
        writeClassificationByPublicationsToFile();
    }
}
