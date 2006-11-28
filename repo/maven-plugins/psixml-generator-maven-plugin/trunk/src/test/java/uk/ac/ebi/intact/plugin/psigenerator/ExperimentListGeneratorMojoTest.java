/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;
import uk.ac.ebi.intact.application.dataConversion.ExperimentListGenerator;

import java.io.File;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:PsiXmlGeneratorMojoTest.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since <pre>04/08/2006</pre>
 */
public class ExperimentListGeneratorMojoTest extends AbstractMojoTestCase
{
        public void testSimpleGeneration() throws Exception
        {
            File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/experiment-list-generator-config.xml" );

            ExperimentListGeneratorMojo mojo = (ExperimentListGeneratorMojo) lookupMojo( "classification", pluginXmlFile );
            mojo.setLog(new SystemStreamLog());


            // clean files before execution
            if (mojo.getDatasetsFile().exists()) mojo.getDatasetsFile().delete();
            if (mojo.getSpeciesFile().exists()) mojo.getSpeciesFile().delete();
            if (mojo.getPublicationsFile().exists()) mojo.getPublicationsFile().delete();
            if (mojo.getNegativeExperimentsFile().exists()) mojo.getNegativeExperimentsFile().delete();
            if (mojo.getExperimentErrorFile().exists()) mojo.getExperimentErrorFile().delete();

            mojo.execute();

            ExperimentListGenerator gen = mojo.getExperimentListGenerator();

            assertEquals(true, mojo.getDatasetsFile().exists());
            assertEquals(true, mojo.getSpeciesFile().exists());
            assertEquals(true, mojo.getPublicationsFile().exists());
            assertEquals(true, mojo.getNegativeExperimentsFile().exists());
            assertEquals(true, mojo.getExperimentErrorFile().exists());

            assertTrue(gen.getExperimentWithErrors().isEmpty());
            
            assertEquals(2, gen.getNegativeExperiments().size());

            System.out.println(FileUtils.fileRead(mojo.getSpeciesFile()));
            System.out.println(FileUtils.fileRead(mojo.getPublicationsFile()));
            System.out.println(FileUtils.fileRead(mojo.getDatasetsFile()));
            System.out.println(FileUtils.fileRead(mojo.getNegativeExperimentsFile()));
            System.out.println(FileUtils.fileRead(mojo.getExperimentErrorFile()));


        }

}
