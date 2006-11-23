/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:PsiXmlGeneratorCleanMojoTest.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since <pre>04/08/2006</pre>
 */
public class PsiXmlGeneratorCleanMojoTest extends AbstractMojoTestCase
{
        public void testClean()
            throws Exception
        {
            File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/clean-config.xml" );

            File basedir = new File( getBasedir(), "target/psixml-test/clean" );
            if ( basedir.exists() )
            {
                FileUtils.deleteDirectory( basedir );
            }
            assertTrue( "Prepare test base directory", basedir.mkdirs() );

            File speciesFile = new File( basedir, "classification_by_species.txt" );
            assertTrue( "Test creation of species files", speciesFile.createNewFile() );

            File publicationsFile = new File( basedir, "classification_by_publications.txt" );
            assertTrue( "Test creation of publication files", publicationsFile.createNewFile() );

            Mojo mojo = lookupMojo( "clean", pluginXmlFile );

            mojo.execute();

            assertFalse( "Test of classification by species file", speciesFile.exists() );

            assertFalse( "Test of classification by publications file", publicationsFile.exists() );
        }


}
