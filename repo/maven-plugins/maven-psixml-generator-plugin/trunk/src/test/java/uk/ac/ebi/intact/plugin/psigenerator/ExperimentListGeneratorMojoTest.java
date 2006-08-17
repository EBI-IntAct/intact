/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.logging.SystemStreamLog;

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

            mojo.execute();
        }

}
