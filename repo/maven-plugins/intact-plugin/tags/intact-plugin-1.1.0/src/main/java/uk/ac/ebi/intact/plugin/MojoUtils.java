/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.plugin;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * Utilities for intact mojos
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 0.1
 */
public class MojoUtils
{
    private static final String NEW_LINE = System.getProperty( "line.separator" );

    private MojoUtils(){}

    /**
     * Prepares a file to be used, checking if it exists and creating the parent folder if necessary.
     * If the file exists, it will be overriden
     * @param file the file to check
     * @throws IOException
     */
    public static void prepareFile(File file) throws IOException
    {
        prepareFile(file, true, true);
    }

    /**
     * Prepares a file to be used, checking if it exists and creating the parent folder if necessary.
     * If the file exists, it will be overriden
     * @param file the file to check
     * @param createParentFolder if the parent of the file does not exist, it will create it
     * @throws IOException
     */
    public static void prepareFile(File file, boolean createParentFolder) throws IOException
    {
        prepareFile(file, createParentFolder, true);
    }

    /**
     * Prepares a file to be used, checking if it exists and creating the parent folder if necessary
     * @param file the file to check
     * @param createParentFolder if the parent of the file does not exist, it will create it
     * @param overwrite remove the file if it already exists
     * @throws IOException
     */
    public static void prepareFile(File file, boolean createParentFolder, boolean overwrite) throws IOException
    {
        if (file == null)
        {
            throw new NullPointerException("file cannot be null");
        }

        if (file.exists())
        {
            if (overwrite)
            {
                file.delete();
            }
            return;
        }

        if (createParentFolder)
        {
            if (file.isDirectory())
            {
                file.mkdirs();
            }
            else
            {
                file.getParentFile().mkdirs();
            }
        }
    }

    public static void writeHeaderToFile(String title, String description, File file) throws IOException
    {
        FileWriter writer = new FileWriter(file);

        writer.write("# "+title+" - "+new Date()+NEW_LINE);
        writer.write("#"+NEW_LINE);
        writer.write("# "+description+NEW_LINE);
        writer.write(NEW_LINE);

        writer.close();
    }

    public static void writeStandardHeaderToFile(String prefix, String description, MavenProject project, File file) throws IOException
    {
        String additionalInfo = "";

        if (project != null) // project can be null when testing using the plugin test logic
        {
            additionalInfo = " - "+project.getArtifactId()+", v. "+project.getVersion();
        }

        String title = prefix+additionalInfo; 
        writeHeaderToFile(title, description, file);
    }

    /**
     * Creates a temporary file from a resource. This is useful to avoid classloading issues
     * @param resourceUrl The stream to write in a file
     * @param prefix prefix of the temporary file created
     * @param suffix suffix of the temporary file created
     * @return the temporary file, with the content written
     * @throws java.io.IOException if there are problems writting the file
     */
    public static File createTempFileFromResource(URL resourceUrl, String prefix, String suffix) throws IOException
    {
        File temporaryFile = File.createTempFile(prefix,suffix);
        //temporaryFile.deleteOnExit();

        FileWriter writer = null;

        try
        {
            writer = new FileWriter(temporaryFile);
            IOUtil.copy(resourceUrl.openStream(), writer, "utf-8", 2048);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (writer != null)
                writer.close();
        }

        return temporaryFile;
    }
}
