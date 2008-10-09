/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import psidev.psi.mi.search.SearchResult;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.search.IntactPsimiTabIndexWriter;
import uk.ac.ebi.intact.psimitab.search.IntactSearchEngine;

import java.io.*;

/**
 * DrLineInteractionCountUpdatetMojo Tester.
 *
 * @version $Id$
 * @since 1.9.0
 */
public class DrLineInteractionCountUpdatetMojoTest extends AbstractMojoTestCase {

    private static final Log log = LogFactory.getLog( DrLineInteractionCountUpdatetMojoTest.class );

    private File getTargetDirectory() {
        String outputDirPath = getClass().getResource( "/" ).getFile();
        Assert.assertNotNull( outputDirPath );
        File outputDir = new File( outputDirPath );
        // we are in test-classes, move one up
        outputDir = outputDir.getParentFile();
        Assert.assertNotNull( outputDir );
        Assert.assertTrue( outputDir.isDirectory() );
        Assert.assertEquals( "target", outputDir.getName() );
        return outputDir;
    }

    public void testDrExport() throws Exception {

        // index a MITAB file into a Lucene index
        File luceneDirectory = new File( getTargetDirectory(), "index" );
        InputStream is = DrLineInteractionCountUpdatetMojoTest.class.getResourceAsStream( "/mitab-samples/dataset.tsv" );
        Assert.assertNotNull( is );
        IntactPsimiTabIndexWriter writer = new IntactPsimiTabIndexWriter();
        writer.index( luceneDirectory, is, true, true );

        IntactSearchEngine engine = new IntactSearchEngine( luceneDirectory );
        final SearchResult<IntactBinaryInteraction> result = engine.search( "Q04206", 0, Integer.MAX_VALUE );
        Assert.assertNotNull( result );
        Assert.assertEquals( 6, result.getTotalCount().intValue() );

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/dr-update-config.xml" );
        DrLineInteractionCountUpdatetMojo mojo =
                ( DrLineInteractionCountUpdatetMojo ) lookupMojo( "dr-update", pluginXmlFile );
        Assert.assertNotNull( mojo );

        // set parameters
        mojo.setTargetPath( getTargetDirectory().getAbsolutePath() );
        mojo.setUpdatedUniprotLinksFilename( "uniprotlinks.updated.dat" );
        mojo.setOverwrite( true );
        mojo.setIndexPath( luceneDirectory.getAbsolutePath() );
        mojo.setUniprotLinksFilename( DrLineInteractionCountUpdatetMojoTest.class.getResource( "/uniprotlinks3.dat" ).getFile() );

        // action !
        mojo.execute();

        // check output
        final File output = new File( getTargetDirectory(), "uniprotlinks.updated.dat" );
        Assert.assertTrue( output.exists() );
        BufferedReader in = new BufferedReader( new FileReader( output ) );
        String line;
        int count = 0;
        while ( ( line = in.readLine() ) != null ) {
            count++;
            if( line.startsWith( "Q04206" ) ) {
                Assert.assertTrue( line.endsWith( "\t6" ) );
            } else if( line.startsWith( "P25963" ) ) {
                Assert.assertTrue( line.endsWith( "\t1" ) );
            } else {
                fail( "Unexpected line found in the output file: " + line );
            }
        }
        Assert.assertEquals( 2, count );
        in.close();
    }
}
