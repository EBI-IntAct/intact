/**
 * Copyright 2012 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.psimi.solr;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactMiScoreTest extends AbstractSolrTestCase {

    private IntactSolrSearcher searcher;
    
    /*@Before
    public void indexMitabFromClasspath() throws Exception {
        getIndexer().indexMitabFromClasspath("/mitab_samples/P37173_scored.txt", false);

        searcher = new IntactSolrSearcher( getSolrJettyRunner().getSolrServer( CoreNames.CORE_PUB ) );
    }*/
    
    @Test
    @Ignore
    public void rangeWithResults() throws Exception {
        final SolrSearchResult result = searcher.search("intact-miscore:[0.4 TO 0.7]", 0, Integer.MAX_VALUE);
        assertThat(result.getTotalCount(), is(equalTo(7L)));
    }

    @Test
    @Ignore
    public void negativeRange() throws Exception {
        final SolrSearchResult result = searcher.search("intact-miscore:[-0.4 TO -0.7]", 0, Integer.MAX_VALUE);
        assertThat(result.getTotalCount(), is(equalTo(0L)));
    }

    @Test
    @Ignore
    public void rangeTo1() throws Exception {
        final SolrSearchResult result = searcher.search("intact-miscore:[0.7 TO 1]", 0, Integer.MAX_VALUE);
        assertThat(result.getTotalCount(), is(equalTo(2L)));
    }

    @Test
    @Ignore
    public void rangeAll0To1() throws Exception {
        final SolrSearchResult result = searcher.search("intact-miscore:[0 TO 1]", 0, Integer.MAX_VALUE);
        assertThat(result.getTotalCount(), is(equalTo(13L)));
    }

    @Test
    @Ignore
    public void exactNumberResults() throws Exception {
        final SolrSearchResult result = searcher.search("intact-miscore:0.164309", 0, Integer.MAX_VALUE);
        assertThat(result.getTotalCount(), is(equalTo(1L)));
    }

    @Test
    @Ignore
    public void exactNumberNoResults() throws Exception {
        final SolrSearchResult result = searcher.search("intact-miscore:0.4", 0, Integer.MAX_VALUE);
        assertThat(result.getTotalCount(), is(equalTo(0L)));
    }

    @Test
    @Ignore
    public void notRangeQuery() throws Exception {
        final SolrSearchResult result = searcher.search("-intact-miscore:[0 TO 0.5]", 0, Integer.MAX_VALUE);
        assertThat(result.getTotalCount(), is(equalTo(2L)));
    }
}
