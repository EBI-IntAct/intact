/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.experimentlistgenerator;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.Mojo;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:$
 * @since <pre>04/08/2006</pre>
 */
public class ExperimentListGeneratorTest extends AbstractMojoTestCase
{
        public void testSimpleGeneration()
            throws Exception
        {
            File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-config.xml" );

            ExperimentListGeneratorMojo mojo = (ExperimentListGeneratorMojo) lookupMojo( "generate-list", pluginXmlFile );

            mojo.execute();
        }


}
