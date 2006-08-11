/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class PsiXmlGeneratorTest extends AbstractMojoTestCase
{
        public void testSimpleGeneration()
            throws Exception
        {
            File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-config.xml" );

            PsiXmlGeneratorMojo mojo = (PsiXmlGeneratorMojo) lookupMojo( "psi", pluginXmlFile );

            mojo.execute();
        }


}
