package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import psidev.psi.mi.tab.PsimiTabReader;
import uk.ac.ebi.intact.psimitab.IntActBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntActColumnHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * ConvertXmlToTabMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/04/2007</pre>
 */
public class ConvertXmlToTabMojoTest extends AbstractMojoTestCase {

    public void testExecute() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/xml2tab-simple-test.xml" );

        ConvertXmlToTabMojo mojo = ( ConvertXmlToTabMojo ) lookupEmptyMojo( "xml2tab", pluginXmlFile );

        File rootDir = new File( getBasedir(), "target" + File.separator + "test-classes" + File.separator + "xml-samples" );

        File tabFile = new File( rootDir.getAbsolutePath() + File.separator + "all.xls" );
//        File logFile = new File( rootDir.getAbsolutePath() + File.separator + "output_ConvertXmlToTabMojo1.log" );

        // set output file
        setVariableValueToObject( mojo, "tabFile", tabFile.getAbsolutePath() );

        // set input files
        Collection files = new ArrayList();
        files.add( rootDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "files", files );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }

        assertTrue( tabFile.exists() );

        int lineCount = 0;
        BufferedReader reader = new BufferedReader( new FileReader( tabFile ) );
        while ( reader.readLine() != null ) {
            lineCount++;
        }

        assertEquals( 9 + 1, lineCount );

    }

    public void testExecutewithIntActBinaryInteraction() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/xml2tab-simple-test.xml" );

        ConvertXmlToTabMojo mojo = ( ConvertXmlToTabMojo ) lookupEmptyMojo( "xml2tab", pluginXmlFile );

        File rootDir = new File( getBasedir(), "target" + File.separator + "test-classes" + File.separator + "xml-samples" );

        File tabFile = new File( rootDir.getAbsolutePath() + File.separator + "all-expanded.xls" );
        // File logFile = new File( rootDir.getAbsolutePath() + File.separator + "output.log" );

        // set output file
        setVariableValueToObject( mojo, "tabFile", tabFile.getAbsolutePath() );

        // set configuration
        setVariableValueToObject( mojo, "binaryInteractionClass", "uk.ac.ebi.intact.psimitab.IntActBinaryInteraction" );
        setVariableValueToObject( mojo, "columnHandler", "uk.ac.ebi.intact.psimitab.IntActColumnHandler" );

        // set input files
        Collection<String> files = new ArrayList<String>();
        files.add( rootDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "files", files );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }

        assertTrue( tabFile.exists() );
    }

    public void testwithoutAdditionalColumn() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/xml2tab-simple-test.xml" );

        ConvertXmlToTabMojo mojo = ( ConvertXmlToTabMojo ) lookupEmptyMojo( "xml2tab", pluginXmlFile );

        assertEquals( "lala2", mojo.getBinaryInteractionClass() );
        assertEquals( "foobar2", mojo.getColumnHandler() );

        String root = "target" + File.separator + "test-classes";

        File resourcesDir = new File( getBasedir(), root );
        ZipFile zipDir = new ZipFile( resourcesDir + File.separator + "pmid" + File.separator + "pmidMIF25.zip" );


        Enumeration entries = zipDir.entries();

        byte[] buffer = new byte[16384];
        int len;
        while ( entries.hasMoreElements() ) {
            ZipEntry entry = ( ZipEntry ) entries.nextElement();

            File zipFile = new File( resourcesDir, entry.getName() );

            int lastIndex = entry.getName().lastIndexOf( '/' );
            String entryFileName = entry.getName().substring( lastIndex + 1 );
            String internalPathToEntry = entry.getName().substring( 0, lastIndex + 1 );
            File dir = new File( resourcesDir, internalPathToEntry );
            if ( !dir.exists() ) {
                dir.mkdirs();
            }

            if ( !entry.isDirectory() ) {
                BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( new File( resourcesDir, entry.getName() ) ) );

                BufferedInputStream bis = new BufferedInputStream( zipDir.getInputStream( entry ) );

                while ( ( len = bis.read( buffer ) ) > 0 ) {
                    bos.write( buffer, 0, len );
                }

                bos.flush();
                bos.close();
                bis.close();
            }
        }


        File srcDir = new File( getBasedir(), root + File.separator + "pmid" );

        // set input files
        Collection files = new ArrayList();
        files.add( srcDir.getAbsolutePath() );


        setVariableValueToObject( mojo, "files", files );
        File tabFile = new File( srcDir.getAbsolutePath() + File.separator + "all.txt" );
        setVariableValueToObject( mojo, "tabFile", tabFile.getAbsolutePath() );

        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        PsimiTabReader reader = new PsimiTabReader( true );

        reader.setBinaryInteractionClass( IntActBinaryInteraction.class );
        reader.setColumnHandler( new IntActColumnHandler() );

        reader.read( tabFile );

    }

    public void testAdditionalColumn() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/xml2tab-simple-test.xml" );

        ConvertXmlToTabMojo mojo = ( ConvertXmlToTabMojo ) lookupEmptyMojo( "xml2tab", pluginXmlFile );

        String root = "target" + File.separator + "test-classes";

        File resourcesDir = new File( getBasedir(), root );
        ZipFile zipDir = new ZipFile( resourcesDir + File.separator + "pmid" + File.separator + "pmidMIF25.zip" );

        Enumeration entries = zipDir.entries();

        byte[] buffer = new byte[16384];
        int len;
        while ( entries.hasMoreElements() ) {
            ZipEntry entry = ( ZipEntry ) entries.nextElement();

            File zipFile = new File( resourcesDir, entry.getName() );

            int lastIndex = entry.getName().lastIndexOf( '/' );
            String entryFileName = entry.getName().substring( lastIndex + 1 );
            String internalPathToEntry = entry.getName().substring( 0, lastIndex + 1 );
            File dir = new File( resourcesDir, internalPathToEntry );
            if ( !dir.exists() ) {
                dir.mkdirs();
            }

            if ( !entry.isDirectory() ) {
                BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( new File( resourcesDir, entry.getName() ) ) );

                BufferedInputStream bis = new BufferedInputStream( zipDir.getInputStream( entry ) );

                while ( ( len = bis.read( buffer ) ) > 0 ) {
                    bos.write( buffer, 0, len );
                }

                bos.flush();
                bos.close();
                bis.close();
            }
        }


        File srcDir = new File( getBasedir(), root + File.separator + "pmid" );

        // set input files
        Collection<String> files = new ArrayList<String>();
        files.add( srcDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "files", files );

        File tabFile = new File( srcDir.getAbsolutePath() + File.separator + "all-extra.txt" );
        setVariableValueToObject( mojo, "tabFile", tabFile.getAbsolutePath() );
        setVariableValueToObject( mojo, "binaryInteractionClass", "uk.ac.ebi.intact.psimitab.IntActBinaryInteraction" );
        setVariableValueToObject( mojo, "columnHandler", "uk.ac.ebi.intact.psimitab.IntActColumnHandler" );

        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        PsimiTabReader reader = new PsimiTabReader( true );

        reader.setBinaryInteractionClass( IntActBinaryInteraction.class );
        reader.setColumnHandler( new IntActColumnHandler() );

        reader.read( tabFile );

    }

}
