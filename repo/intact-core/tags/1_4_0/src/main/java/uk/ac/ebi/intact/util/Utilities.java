/*
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Various IntAct related utilities. If a pice of code is used more
 * than once, but does not really belong to a specific class, it
 * should become part of this class.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk) et at.
 * @version $Id$
 */
public class Utilities {

    private Utilities() {
        // no instantiable
    }

    /** Initialise parameters from the properties file.
	@param aParameterName The file name of the properties file
	from which to read.

	This parameter must be given on the command line as
	for example -Dconfig=pathname
     */
    public static Properties getProperties(String aParameterName) throws IOException{

	// get properties
	Properties properties = new Properties();
	FileInputStream in = new FileInputStream(System.getProperty(aParameterName));
	properties.load(in);
	in.close();

	return properties;
    }

    /** compares two objects.
     * This is a workaround for the case where o is null
     * and o.equals(p) returns exceptions.
     *
     * @param o
     * @param p
     */
    public static boolean equals(Object o, Object p){
        if (o == p) return true;
        if (null == o && null != p) return false;
        return (o.equals(p));
    }

    /**
     * Compresses a file using GZIP
     * @param sourceFile the file to compress
     * @param destFile the zipped file
     * @param deleteOriginalFile if true, the original file is deleted and only the gzipped file remains
     * @throws IOException thrown if there is a problem finding or writing the files
     */
    public static void gzip(File sourceFile, File destFile, boolean deleteOriginalFile) throws IOException
    {
        // Create the GZIP output stream
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(destFile));

        // Open the input file
        FileInputStream in = new FileInputStream(sourceFile);

        // Transfer bytes from the input file to the GZIP output stream
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();

        // Complete the GZIP file
        out.finish();
        out.close();

        if (deleteOriginalFile)
        {
            sourceFile.delete();
        }
    }

    /**
     * Compresses a file using GZIP
     *
     * @param sourceFiles         the files to include in the zip
     * @param destFile the zipped file
     * @param deleteOriginalFiles if true, the original file is deleted and only the gzipped file remains
     * @throws IOException thrown if there is a problem finding or writing the files
     */
    public static void zip(File[] sourceFiles, File destFile, boolean deleteOriginalFiles) throws IOException
    {

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        // Create the ZIP file
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destFile));

        // Compress the files
        for (File sourceFile : sourceFiles)
        {
            FileInputStream in = new FileInputStream(sourceFile);

            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(sourceFile.toString()));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }

            // Complete the entry
            out.closeEntry();
            in.close();
        }

        // Complete the ZIP file
        out.close();

        if (deleteOriginalFiles)
        {
            for (File sourceFile : sourceFiles)
            {
                sourceFile.delete();
            }
        }
    }
}


