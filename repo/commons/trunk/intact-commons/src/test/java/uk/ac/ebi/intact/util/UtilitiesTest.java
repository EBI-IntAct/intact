/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.util;

import org.junit.Test;
import static org.junit.Assert.*;


import java.io.File;

/**
 * Test for <code>UtilitiesTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/21/2006
 */
public class UtilitiesTest {

    @Test
    public void gzip() throws Exception {
        File fileToGzip = new File(UtilitiesTest.class.getResource("/uk/ac/ebi/intact/util/FileToGzip.txt").getFile());

        File gzippedFile = new File(fileToGzip.getParent(), fileToGzip.getName()+".gz");
        assertFalse(gzippedFile.exists());

        // gzip without delete
        Utilities.gzip(fileToGzip, gzippedFile, false);
        assertTrue("Original file should exist", fileToGzip.exists());
        assertTrue("Gzipped file should exist", gzippedFile.exists());

        // remove the gzipped file
        gzippedFile.delete();
        assertFalse(gzippedFile.exists());

        // gzip deleting
        Utilities.gzip(fileToGzip, gzippedFile, true);
        assertFalse("Original file should be deleted", fileToGzip.exists());
        assertTrue("Gzipped file should exist", gzippedFile.exists());

        // remove the gzipped file
        gzippedFile.delete();
        assertFalse(gzippedFile.exists());
    }

    @Test
    public void zip() throws Exception {
       File fileToZip1 = new File(UtilitiesTest.class.getResource("/uk/ac/ebi/intact/util/FileToZip1.txt").getFile());
       File fileToZip2 = new File(UtilitiesTest.class.getResource("/uk/ac/ebi/intact/util/FileToZip2.txt").getFile());

        File zippedFile = new File(fileToZip1.getParent(), "FilesZipped.zip");
        assertFalse(zippedFile.exists());

        Utilities.zip(new File[] {fileToZip1, fileToZip2}, zippedFile, false);
        assertTrue("Original file should exist", fileToZip1.exists());
        assertTrue("Original file should exist", fileToZip2.exists());
        assertTrue("Gzipped file should exist", zippedFile.exists());

        zippedFile.delete();
        assertFalse(zippedFile.exists());

        Utilities.zip(new File[] {fileToZip1, fileToZip2}, zippedFile, true);
        assertFalse("Original file should be deleted", fileToZip1.exists());
        assertFalse("Original file should be deleted", fileToZip2.exists());
        assertTrue("Gzipped file should exist", zippedFile.exists());

        zippedFile.delete();
        assertFalse(zippedFile.exists());
    }
}
