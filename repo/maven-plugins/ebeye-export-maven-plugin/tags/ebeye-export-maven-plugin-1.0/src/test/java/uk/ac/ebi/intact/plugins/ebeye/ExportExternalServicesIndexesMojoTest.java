/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins.ebeye;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.File;

/**
 * ExportExternalServicesIndexesMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since <pre>11/28/2006</pre>
 * @version 1.0
 */
public class ExportExternalServicesIndexesMojoTest extends AbstractMojoTestCase {

    public void testGetProject() throws Exception {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/ebi-search-engine-config.xml" );

        ExportExternalServicesIndexesMojo mojo =
                (ExportExternalServicesIndexesMojo) lookupMojo( "generate-ebi-indexes", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();
    }
}