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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
     * Prepares a file to be used, checking if it exists and creating the parent folder if necessary
     * @param file the file to check
     * @param createParentFolder if the parent of the file does not exist, it will create it
     * @throws IOException
     */
    public static void prepareFile(File file, boolean createParentFolder) throws IOException
    {
        if (file == null)
        {
            throw new NullPointerException("file cannot be null");
        }

        if (file.exists())
        {
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
        String title = prefix+" - "+project.getArtifactId()+", v. "+project.getVersion(); 
        writeHeaderToFile(title, description, file);
    }
}
