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
package uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.processors;

import org.junit.Test;
import org.junit.Assert;
import uk.ac.ebi.intact.dataexchange.psimi.solr.*;
import uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.relevancescore.IntactSolrRelevanceScoreCalculatorTest;

import java.util.*;

/**
 * Class to test SolrPostProcessor.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.3
 */
public class SolrIndexProcessorTest extends AbstractSolrJettyRunnableTestCase {


    @Test
    public void testRelevanceScoreProcess() throws Exception {
        indexFromClasspath( "/mitab_samples/intactWithRigSlim.txt", false );
        IntactSolrSearcher searcher = new IntactSolrSearcher( getTestSolrServer() );

        //search for all
        String searchQuery = "*:*";
        final SolrSearchResult allDocuments = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 10, allDocuments.getTotalCount() );

        //before processing
        searchQuery = "relevancescore:N*";
        final SolrSearchResult resultBeforeProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 0, resultBeforeProcessing.getTotalCount() );

        //start the processing  by getting all
        SolrIndexProcessor indexProcessor = new SolrIndexProcessor( getTestSolrServer() );
        indexProcessor.registerProcessor( new SolrRelevanceScoreProcessor( ) );
        indexProcessor.startProcessing();

        //after processing by adding relevance score
        searchQuery = "relevancescore:N*";
        final SolrSearchResult afterProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 10, afterProcessing.getTotalCount() );

    }


    @Test
    public void testEvidencesProcess() throws Exception {
        indexFromClasspath( "/mitab_samples/intactWithRigSlim.txt", false );
        IntactSolrSearcher searcher = new IntactSolrSearcher( getTestSolrServer() );

        //search for all
        String searchQuery = "*:*";
        final SolrSearchResult allDocuments = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 10, allDocuments.getTotalCount() );

        //before processing
        searchQuery = "evidences:[* TO *]";
        final SolrSearchResult resultBeforeProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 0, resultBeforeProcessing.getTotalCount() );

        //start to add evidences by getting all
        SolrIndexProcessor indexProcessor = new SolrIndexProcessor( getTestSolrServer() );
        indexProcessor.registerProcessor( new SolrEvidenceCountProcessor() );
        indexProcessor.startProcessing();

        //after processing by adding relevance score
        searchQuery = "evidences:[* TO *]";
        final SolrSearchResult afterProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 10, afterProcessing.getTotalCount() );

    }

    @Test
    public void processAll() throws Exception {
        indexFromClasspath( "/mitab_samples/intactWithRigSlim.txt", false );
        IntactSolrSearcher searcher = new IntactSolrSearcher( getTestSolrServer() );

        //search for all
        String searchQuery = "*:*";
        final SolrSearchResult allDocuments = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 10, allDocuments.getTotalCount() );

        //before processing
        searchQuery = "relevancescore:N*";
        SolrSearchResult resultBeforeProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 0, resultBeforeProcessing.getTotalCount() );
        searchQuery = "evidences:[* TO *]";
        resultBeforeProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 0, resultBeforeProcessing.getTotalCount() );

        //start the processing  by getting all
        SolrIndexProcessor indexProcessor = new SolrIndexProcessor( getTestSolrServer() );
        indexProcessor.registerProcessor( new SolrRelevanceScoreProcessor( ) );
        indexProcessor.registerProcessor( new SolrEvidenceCountProcessor() );
        indexProcessor.startProcessing();

        //after processing by adding relevance score
        searchQuery = "relevancescore:N*";
        SolrSearchResult afterProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 10, afterProcessing.getTotalCount() );
        searchQuery = "evidences:[* TO *]";
        afterProcessing = searcher.search( searchQuery, 0, 20 );
        Assert.assertEquals( 10, afterProcessing.getTotalCount() );
    }


}
