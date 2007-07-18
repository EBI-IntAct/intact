/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.util.filter.UrlFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils to handle URL
 *
 * @author Bruno Aranda (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class UrlUtils
{

    /**
     * Non-recursive listing of the files in a URL
     * @param folderUrl The folder to read
     * @return the lists of URLs
     * @throws IOException
     */
    public static List<URL> listFilesFromFolderUrl(URL folderUrl) throws IOException
    {
        return listFilesFromFolderUrl(folderUrl, null, false);
    }

    /**
     * Lists the files available from a URL, filtering using the UrlFilter
     * @param folderUrl The folder to read
     * @param filter if you want to filter certain files
     * @param recursive whether the list is recursive or not (goes into directories)
     * @return the lists of URLs
     * @throws IOException
     */
   public static List<URL> listFilesFromFolderUrl(URL folderUrl, UrlFilter filter, boolean recursive) throws IOException
   {
       if (folderUrl == null)
       {
           throw new NullPointerException("Url cannot be null");
       }

       File localFile = new File(folderUrl.getFile());
       boolean isLocal = localFile.exists();

       if (!folderUrl.toString().endsWith("/"))
       {
           folderUrl = new URL(folderUrl.toString()+"/");
       }

       List<URL> urls = new ArrayList<URL>();

       InputStream is = folderUrl.openStream();

       BufferedReader r = new BufferedReader(new InputStreamReader(is));
       String line;
        while ((line = r.readLine())!= null)
        {
            if (line.trim().length() > 0)
            {
                URL url = urlFromLine(folderUrl, line);

                boolean isLocalDir = isLocal && new File(url.getFile()).isDirectory();

                if (isLocalDir || isRemoteDirectory(line))
                {
                    if (recursive)
                    {
                        URL childFolder = null;

                        if (isLocal)
                        {
                            childFolder = new File(localFile, line).toURL();
                        }
                        else
                        {
                           childFolder = new URL(urlFromLine(folderUrl, line).toString()+"/");
                        }
                        urls.addAll(listFilesFromFolderUrl(childFolder, filter, recursive));
                    }
                }
                else
                {
                    if (filter != null)
                    {
                        if (filter.accept(url))
                        {
                            urls.add(url);
                        }
                    }
                    else
                    {
                        urls.add(url);
                    }
                }
            }
        }

       return urls;
   }

    private static URL urlFromLine(URL folderUrl, String line) throws IOException
    {
        if (line == null)
        {
            return null;
        }

        String[] tokens = line.split("\\s");

        return new URL(folderUrl.toString()+tokens[tokens.length-1]);
    }

    private static boolean isRemoteDirectory(String line)
    {
        return line.startsWith("d");
    }

}
