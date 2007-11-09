/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.io.File;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PsiXmlGeneratorMojoTest extends AbstractPsiXmlGeneratorTestCase {

     @Test
    public void testCheck() throws Exception {
        String filename = PsiXmlGeneratorMojoTest.class.getResource( "/configs/simple-config.xml" ).getFile();
        Assert.assertNotNull( filename );
        File pluginXmlFile = new File( filename );

        PsiXmlGeneratorMojo mojo = (PsiXmlGeneratorMojo) lookupMojo( "psi", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File(PsiXmlGeneratorMojoTest.class.getResource("/test-hibernate.cfg.xml").getFile());

        initializeDatabaseContent( mojo.hibernateConfig );

        // execute Mojo
        mojo.execute();
    }

}