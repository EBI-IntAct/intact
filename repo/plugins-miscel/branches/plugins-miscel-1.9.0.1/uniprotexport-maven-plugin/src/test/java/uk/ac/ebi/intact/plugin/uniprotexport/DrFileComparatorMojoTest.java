/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public class DrFileComparatorMojoTest extends AbstractMojoTestCase
{

    private static final Log log = LogFactory.getLog(DrFileComparatorMojoTest.class);

    public void testDrExport() throws Exception
    {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/dr-comparator-config.xml" );

        DrFileComparatorMojo mojo = (DrFileComparatorMojo) lookupMojo( "compare-dr", pluginXmlFile );
        mojo.setNewDrFile(new File(CcAndGoaExportMojoTest.class.getResource("/uniprotlinks.dat").getFile()));
        mojo.setOldDrFile(new File(CcAndGoaExportMojoTest.class.getResource("/uniprotlinks2.dat").getFile()));
        mojo.setNewIdsFile(new File(getBasedir(), "target/uniprotexport-test/drFileComparator_new.log"));
        mojo.setRemovedIdsFile(new File(getBasedir(), "target/uniprotexport-test/drFileComparator_removed.log"));

        mojo.execute();
    }

}
