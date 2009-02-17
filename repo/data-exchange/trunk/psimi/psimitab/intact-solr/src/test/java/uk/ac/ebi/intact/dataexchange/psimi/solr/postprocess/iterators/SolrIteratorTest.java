/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.iterators;

import uk.ac.ebi.intact.dataexchange.psimi.solr.TestSolrJettyRunner;
import uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrException;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import org.junit.Test;
import org.junit.Assert;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Tests to test SolrDocumentIterator and SolrRigIterator
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SolrIteratorTest extends TestSolrJettyRunner {

    @Test
    public void testIterator() throws IOException, IntactSolrException, SolrServerException {
        indexFromClasspath( "/mitab_samples/intact200.txt", true );
        SolrDocumentIterator solrIterator = new SolrDocumentIterator( getTestSolrServer() );

        int lineCount = 0;
        while ( solrIterator.hasNext() ) {
            solrIterator.nextMitabLine();
            lineCount++;
        }
        Assert.assertEquals( 200, lineCount );

        solrIterator = new SolrDocumentIterator( getTestSolrServer() );
        lineCount = 0;
        while ( solrIterator.hasNext() ) {
            solrIterator.nextRow();
            lineCount++;
        }
        Assert.assertEquals( 200, lineCount );

        solrIterator = new SolrDocumentIterator( getTestSolrServer() );
        lineCount = 0;
        while ( solrIterator.hasNext() ) {
            solrIterator.nextIntactBinaryInteraction();
            lineCount++;
        }
        Assert.assertEquals( 200, lineCount );
    }

    @Test
    public void testGroupingByRigWithBinaryInteraction() throws Exception {

        List<Integer> rigList = new ArrayList<Integer>();
        indexFromClasspath( "/mitab_samples/intactWithRigSlim.txt", false );
        SolrRigIterator solrRigIterator = new SolrRigIterator( getTestSolrServer() );
        int index = 0;
        while ( solrRigIterator.hasNext() ) {
            final Collection<IntactBinaryInteraction> interactionsGroupedByRig = solrRigIterator.nextIntactBinaryInteractionGroup();
            rigList.add( index, interactionsGroupedByRig.size() );
            index++;
        }
        Assert.assertEquals( 7, rigList.size() );

        Assert.assertEquals( 1, rigList.get( 0 ) );
        Assert.assertEquals( 3, rigList.get( 1 ) );
        Assert.assertEquals( 1, rigList.get( 2 ) );
        Assert.assertEquals( 1, rigList.get( 3 ) );
        Assert.assertEquals( 2, rigList.get( 4 ) );
        Assert.assertEquals( 1, rigList.get( 5 ) );
        Assert.assertEquals( 1, rigList.get( 6 ) );
    }

    @Test
    public void testGroupingByRigWithSolrDocuments() throws Exception {

        List<Integer> rigList = new ArrayList<Integer>();
        indexFromClasspath( "/mitab_samples/intactWithRigSlim.txt", false );

        SolrRigIterator solrRigIterator = new SolrRigIterator( getTestSolrServer() );
        int index = 0;
        while ( solrRigIterator.hasNext() ) {
            final Collection<SolrInputDocument> documentsGroupedByRig = solrRigIterator.nextSolrDocumentGroup();
            rigList.add( index, documentsGroupedByRig.size() );
            index++;
        }
        Assert.assertEquals( 7, rigList.size() );

        Assert.assertEquals( 1, rigList.get( 0 ) );
        Assert.assertEquals( 3, rigList.get( 1 ) );
        Assert.assertEquals( 1, rigList.get( 2 ) );
        Assert.assertEquals( 1, rigList.get( 3 ) );
        Assert.assertEquals( 2, rigList.get( 4 ) );
        Assert.assertEquals( 1, rigList.get( 5 ) );
        Assert.assertEquals( 1, rigList.get( 6 ) );
    }
}



