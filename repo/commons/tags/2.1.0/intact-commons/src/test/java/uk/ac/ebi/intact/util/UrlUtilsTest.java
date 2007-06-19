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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.filter.UrlFilter;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Test for <code>UrlUtilsTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/21/2006
 */
public class UrlUtilsTest extends TestCase
{
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testListFromUrlFolder_nonRecursive() throws Exception {
        List<URL> urls = UrlUtils.listFilesFromFolderUrl(UrlUtils.class.getResource(".."));
        assertTrue(urls.isEmpty());
    }

    public void testListFromUrlFolder_recursive() throws Exception {
        List<URL> urls = UrlUtils.listFilesFromFolderUrl(UrlUtils.class.getResource(".."), null, true);
        assertFalse(urls.isEmpty());
    }

    public void testListFromUrlFolder_recursive_customFilter() throws Exception {

        UrlFilter customFilter = new UrlFilter()
        {
            public boolean accept(URL url) throws IOException
            {
                return url.toString().endsWith(".test");
            }
        };

        List<URL> urls = UrlUtils.listFilesFromFolderUrl(UrlUtils.class.getResource(".."), customFilter, true);
        assertEquals(2, urls.size());
    }


    public static Test suite() {
        return new TestSuite(UrlUtilsTest.class);
    }
}
