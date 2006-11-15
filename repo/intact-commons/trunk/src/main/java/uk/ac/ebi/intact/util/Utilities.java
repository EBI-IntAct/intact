/*
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    public static Properties getProperties(String aParameterName) throws IOException
    {

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

    /**
     * Uncompress gzipped files
     * @param gzippedFile The file to uncompress
     * @param destinationFile The resulting file
     */
    public static void gunzip(File gzippedFile, File destinationFile) throws IOException
    {
        int buffer = 2048;

        FileInputStream in = new FileInputStream(gzippedFile);
        GZIPInputStream zipin = new GZIPInputStream(in);

        byte[] data = new byte[buffer];

        // decompress the file
        FileOutputStream out = new FileOutputStream(destinationFile);
        int length;
        while ((length = zipin.read(data, 0, buffer)) != -1)
            out.write(data, 0, length);
        out.close();

        zipin.close();
    }

    /**
     * Uncompresses zipped files
     * @param zippedFile The file to uncompress
     */
    public static List<File> unzip(File zippedFile) throws IOException
    {
        return unzip(zippedFile, null);
    }

    /**
     * Uncompresses zipped files
     * @param zippedFile The file to uncompress
     * @param destinationDir Where to put the files
     */
    public static List<File> unzip(File zippedFile, File destinationDir) throws IOException
    {
        int buffer = 2048;

        List<File> unzippedFiles = new ArrayList<File>();

        BufferedOutputStream dest = null;
        BufferedInputStream is = null;
        ZipEntry entry;
        ZipFile zipfile = new ZipFile(zippedFile);
        Enumeration e = zipfile.entries();
        while (e.hasMoreElements())
        {
            entry = (ZipEntry) e.nextElement();

            is = new BufferedInputStream
                    (zipfile.getInputStream(entry));
            int count;
            byte data[] = new byte[buffer];

            File destFile;

            if (destinationDir != null)
            {
                destFile = new File(destinationDir, entry.getName());
            }
            else
            {
                destFile = new File(entry.getName());
            }

            FileOutputStream fos = new FileOutputStream(destFile);
            dest = new
                    BufferedOutputStream(fos, buffer);
            while ((count = is.read(data, 0, buffer))
                    != -1)
            {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
            is.close();

            unzippedFiles.add(destFile);
        }

        return unzippedFiles;
    }


}


