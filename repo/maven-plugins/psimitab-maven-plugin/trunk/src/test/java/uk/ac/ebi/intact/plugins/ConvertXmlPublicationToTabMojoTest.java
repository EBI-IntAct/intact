package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.jboss.util.file.FilenameSuffixFilter;
import psidev.psi.mi.tab.PsimiTabReader;
import uk.ac.ebi.intact.psimitab.IntActBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntActColumnHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ConvertXmlPublicationToTabMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/05/2007</pre>
 */
public class ConvertXmlPublicationToTabMojoTest extends AbstractMojoTestCase {

    public void testExecute() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/pub2tab-simple-test.xml" );

        ConvertXmlPublicationToTabMojo mojo = ( ConvertXmlPublicationToTabMojo ) lookupEmptyMojo( "pub2tab", pluginXmlFile );

        String root = "target" + File.separator + "test-classes";
        File srcDir = new File( getBasedir(), root + File.separator + "xml-samples" );
        File targetDir = new File( getBasedir(), root + File.separator + "tab-output" );

        targetDir.mkdirs();

        setVariableValueToObject( mojo, "sourceDirectoryPath", srcDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "targetDirectoryPath", targetDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", targetDir.getAbsolutePath() + File.separator + "mitab.log" );

        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        // now check if all files were created sucessfuly
        assertTrue( new File( targetDir, "11283351.txt" ).exists() );
        assertTrue( new File( targetDir, "7568142.txt" ).exists() );
        assertTrue( new File( targetDir, "9070862.txt" ).exists() );

        File subDir = new File( targetDir, "sub-dir" );
        assertTrue( new File( subDir, "10366597.txt" ).exists() );
        assertTrue( new File( subDir, "9686597.txt" ).exists() );
    }

    public void testExecutewithIntActBinaryInteraction() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/pub2tab-simple-test.xml" );

        ConvertXmlPublicationToTabMojo mojo = ( ConvertXmlPublicationToTabMojo ) lookupEmptyMojo( "pub2tab", pluginXmlFile );

        assertEquals( "lala", mojo.getBinaryInteractionClass() );
        assertEquals( "foobar", mojo.getColumnHandler() );

        String root = "target" + File.separator + "test-classes";
        File srcDir = new File( getBasedir(), root + File.separator + "xml-samples" );
        File targetDir = new File( getBasedir(), root + File.separator + "tab-output" );

        targetDir.mkdirs();

        setVariableValueToObject( mojo, "sourceDirectoryPath", srcDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "targetDirectoryPath", targetDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", targetDir.getAbsolutePath() + File.separator + "mitab-expanded.log" );
        setVariableValueToObject( mojo, "binaryInteractionClass", "uk.ac.ebi.intact.psimitab.IntActBinaryInteraction" );
        setVariableValueToObject( mojo, "columnHandler", "uk.ac.ebi.intact.psimitab.IntActColumnHandler" );

        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        // now check if all files were created sucessfuly
        assertTrue( new File( targetDir, "11283351.txt" ).exists() );
        assertTrue( new File( targetDir, "7568142.txt" ).exists() );
        assertTrue( new File( targetDir, "9070862.txt" ).exists() );
        assertTrue( new File( targetDir, "14681455.txt" ).exists() );

        File subDir = new File( targetDir, "sub-dir" );
        assertTrue( new File( subDir, "10366597.txt" ).exists() );
        assertTrue( new File( subDir, "9686597.txt" ).exists() );
    }

    public void testwithoutAdditionalColumn() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/pub2tab-simple-test.xml" );

        ConvertXmlPublicationToTabMojo mojo = ( ConvertXmlPublicationToTabMojo ) lookupEmptyMojo( "pub2tab", pluginXmlFile );

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
        File targetDir = new File( getBasedir(), root + File.separator + "pmid-pub-tab-output" );
        targetDir.mkdirs();

        setVariableValueToObject( mojo, "sourceDirectoryPath", srcDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "targetDirectoryPath", targetDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", targetDir.getParentFile().getAbsolutePath() + File.separator + "pmid.log" );


        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        for ( int f = 0; f < targetDir.list().length; f++ ) {
            File subDir = new File( targetDir, targetDir.list()[f] );
            File[] fileList = subDir.listFiles( new FilenameSuffixFilter( ".txt" ) );


            for ( int i = 0; i < fileList.length; i++ ) {
                File file = fileList[i];

                if ( file.canRead() ) {
                    PsimiTabReader reader = new PsimiTabReader( true );

                    reader.setBinaryInteractionClass( IntActBinaryInteraction.class );
                    reader.setColumnHandler( new IntActColumnHandler() );

                    reader.read( file );
                    //System.out.println("Checking: " +file.getParentFile().getName()+File.separator + file.getName() +" contains only 15 columns");
                }
            }
        }
    }

    public void testAdditionalColumn() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/pub2tab-simple-test.xml" );

        ConvertXmlPublicationToTabMojo mojo = ( ConvertXmlPublicationToTabMojo ) lookupEmptyMojo( "pub2tab", pluginXmlFile );

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
        File targetDir = new File( getBasedir(), root + File.separator + "pmid-pub-tab-output-extra" );
        targetDir.mkdirs();

        setVariableValueToObject( mojo, "sourceDirectoryPath", srcDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "targetDirectoryPath", targetDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", targetDir.getParentFile().getAbsolutePath() + File.separator + "pmid.log" );
        setVariableValueToObject( mojo, "binaryInteractionClass", "uk.ac.ebi.intact.psimitab.IntActBinaryInteraction" );
        setVariableValueToObject( mojo, "columnHandler", "uk.ac.ebi.intact.psimitab.IntActColumnHandler" );

        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        for ( int f = 0; f < targetDir.list().length; f++ ) {
            File subDir = new File( targetDir, targetDir.list()[f] );

            File[] fileList = subDir.listFiles( new FilenameSuffixFilter( ".txt" ) );


            for ( int i = 0; i < fileList.length; i++ ) {
                File file = fileList[i];

                if ( file.canRead() ) {
                    PsimiTabReader reader = new PsimiTabReader( true );

                    reader.setBinaryInteractionClass( IntActBinaryInteraction.class );
                    reader.setColumnHandler( new IntActColumnHandler() );

                    reader.read( file );

                    //System.out.println("Checking: " +file.getParentFile().getName()+File.separator + file.getName() + " contains 15 columns + extra 7");
                }
            }
        }
    }

}
