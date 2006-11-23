/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;

import java.io.File;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public class CcAndGoaExportMojoTest extends AbstractMojoTestCase
{

    private static final Log log = LogFactory.getLog(CcAndGoaExportMojoTest.class);

    public void testCCAndGoaMojoExport() throws Exception
    {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/cc-goa-config.xml" );

        CcAndGoaExportMojo mojo = (CcAndGoaExportMojo) lookupMojo( "cc-goa", pluginXmlFile );
        mojo.setUniprotLinksFile(new File(CcAndGoaExportMojoTest.class.getResource("/uniprotlinks.dat").getFile()));

        mojo.execute();
    }

}
