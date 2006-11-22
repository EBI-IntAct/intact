/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.plugins.dbtest;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class PsiXmlImportMojoTest extends AbstractMojoTestCase
{

    public void testSimpleGeneration() throws Throwable {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/preloadedH2-config.xml" );

        PreloadedH2Mojo mojo = (PreloadedH2Mojo) lookupMojo( "h2-preload", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();


        pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/psixml-import-config.xml" );

        PsiXmlImportMojo mojo2 = (PsiXmlImportMojo) lookupMojo( "import-psi", pluginXmlFile );
        mojo2.setLog( new SystemStreamLog() );

        mojo2.execute();
    }
}
