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
package uk.ac.ebi.intact.plugins.psimitab.index;

import org.apache.lucene.store.FSDirectory;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;
import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.search.Searcher;

import java.io.File;

public class IndexerMojoTest extends AbstractMojoTestCase {

    @Test
    public void testIndex() throws Exception {
        File pluginXmlFile = new File(getBasedir(), "src/test/plugin-configs/index-config.xml");

        IndexerMojo mojo = (IndexerMojo) lookupMojo("index", pluginXmlFile);
        mojo.setLog(new SystemStreamLog());

        mojo.execute();

        SearchResult result = Searcher.search("P38974", FSDirectory.getDirectory(mojo.getIndexDirectory()));
        assertEquals(1, result.getInteractions().size());
    }
}
