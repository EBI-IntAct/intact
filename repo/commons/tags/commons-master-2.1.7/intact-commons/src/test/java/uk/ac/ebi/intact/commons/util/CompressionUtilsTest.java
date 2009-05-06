/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.commons.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.File;

/**
 * Test for <code>CompressionUtilsTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/21/2006
 */
public class CompressionUtilsTest {

    @Test
    public void gzip() throws Exception {
        File fileToGzip = new File(CompressionUtilsTest.class.getResource("FileToGzip.txt").getFile());

        File gzippedFile = new File(fileToGzip.getParent(), fileToGzip.getName()+".gz");
        assertFalse(gzippedFile.exists());

        // gzip without delete
        CompressionUtils.gzip(fileToGzip, gzippedFile, false);
        assertTrue("Original file should exist", fileToGzip.exists());
        assertTrue("Gzipped file should exist", gzippedFile.exists());

        // remove the gzipped file
        gzippedFile.delete();
        assertFalse(gzippedFile.exists());

        // gzip deleting
        CompressionUtils.gzip(fileToGzip, gzippedFile, true);
        assertFalse("Original file should be deleted", fileToGzip.exists());
        assertTrue("Gzipped file should exist", gzippedFile.exists());

        // remove the gzipped file
        gzippedFile.delete();
        assertFalse(gzippedFile.exists());
    }

    @Test
    public void zip() throws Exception {
       File fileToZip1 = new File(CompressionUtilsTest.class.getResource("FileToZip1.txt").getFile());
       File fileToZip2 = new File(CompressionUtilsTest.class.getResource("FileToZip2.txt").getFile());

        File zippedFile = new File(fileToZip1.getParent(), "FilesZipped.zip");
        if(zippedFile.exists()){
            zippedFile.delete();
        }
        assertFalse(zippedFile.exists());

        CompressionUtils.zip(new File[] {fileToZip1, fileToZip2}, zippedFile, false);
        assertTrue("Original file should exist", fileToZip1.exists());
        assertTrue("Original file should exist", fileToZip2.exists());
        assertTrue("Gzipped file should exist", zippedFile.exists());

        zippedFile.delete();
        assertFalse(zippedFile.exists());

        CompressionUtils.zip(new File[] {fileToZip1, fileToZip2}, zippedFile, true);
        assertFalse("Original file should be deleted", fileToZip1.exists());
        assertFalse("Original file should be deleted", fileToZip2.exists());
        assertTrue("Gzipped file should exist", zippedFile.exists());

        zippedFile.delete();
        assertFalse(zippedFile.exists());
    }


    @Test
    public void zipWithFullPathName() throws Exception {
        File fileToZip1 = new File( CompressionUtilsTest.class.getResource( "FileToZip1.txt" ).getFile() );
        File fileToZip2 = new File( CompressionUtilsTest.class.getResource( "FileToZip2.txt" ).getFile() );

        File zippedFile = new File( fileToZip1.getParent(), "FilesZipped2.zip" );

        if ( zippedFile.exists() ) {
            zippedFile.delete();
        }
        assertFalse( zippedFile.exists() );

        //zip with fullpathname
        CompressionUtils.zip( new File[]{fileToZip1, fileToZip2}, zippedFile, false, false );
        assertTrue( "Original file should exist", fileToZip1.exists() );
        assertTrue( "Original file should exist", fileToZip2.exists() );
        assertTrue( "Gzipped file should exist", zippedFile.exists() );

        if ( fileToZip1.exists() ) {
            fileToZip1.delete();
        }

        if ( fileToZip2.exists() ) {
            fileToZip2.delete();
        }

        File desDirectory = zippedFile.getParentFile();
        CompressionUtils.unzip( zippedFile, desDirectory );
        assertTrue( "File should exist after unzipping ", fileToZip1.exists() );
        assertTrue( "File should exist after unzipping ", fileToZip2.exists() );


        if ( zippedFile.exists() ) {
            zippedFile.delete();
        }

    }
}
