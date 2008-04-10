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

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for <code>UrlUtilsTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/21/2006
 */
public class UrlUtilsTest {

    @Test
    public void listFromUrlFolder_nonRecursive() throws Exception {
        List<URL> urls = UrlUtils.listFilesFromFolderUrl(UrlUtils.class.getResource(".."));
        assertTrue(urls.isEmpty());
    }

    @Test
    public void listFromUrlFolder_recursive() throws Exception {
        List<URL> urls = UrlUtils.listFilesFromFolderUrl(UrlUtils.class.getResource(".."), null, true);
        assertFalse(urls.isEmpty());
    }

    @Test
    public void listFromUrlFolder_recursive_customFilter() throws Exception {

        UrlFilter customFilter = new UrlFilter()
        {
            public boolean accept(URL url) throws IOException
            {
                return url.toString().endsWith(".test");
            }
        };



        final URL resource = UrlUtilsTest.class.getResource( ".." );
        File parentDirectory = new File( resource.getFile() );
        // create 2 files
        createNewFile( parentDirectory, "foo.test" );
        createNewFile( parentDirectory, "bar.test" );

        List<URL> urls = UrlUtils.listFilesFromFolderUrl(resource, customFilter, true);
        assertEquals(2, urls.size());
    }

    private void createNewFile( File parent, String name ) throws Exception {
        assertTrue( parent.canWrite() );
        final File file = new File( parent, name );
        final boolean created = file.createNewFile();
        assertTrue( file.canRead() );
        assertEquals( 0, file.length() );
    }
}
