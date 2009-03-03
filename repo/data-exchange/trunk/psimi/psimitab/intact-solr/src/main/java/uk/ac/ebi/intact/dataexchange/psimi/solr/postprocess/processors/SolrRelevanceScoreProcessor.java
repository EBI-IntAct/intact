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

import org.apache.solr.common.SolrInputDocument;

import java.util.Collection;
import java.util.Properties;
import java.io.IOException;

import uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.relevancescore.IntactRelevanceScoreCalculator;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;
import uk.ac.ebi.intact.dataexchange.psimi.solr.FieldNames;

/**
 * Processor that adds RelevanceScore to the SolrInputDocument
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SolrRelevanceScoreProcessor implements SolrDocumentProcessor{

    private SolrDocumentConverter converter = new SolrDocumentConverter();
    IntactRelevanceScoreCalculator relevanceScoreCalculator;

    public SolrRelevanceScoreProcessor() throws IOException {
        this.relevanceScoreCalculator = new IntactRelevanceScoreCalculator( );
    }

    public SolrRelevanceScoreProcessor( Properties rscProperties) {
        this.relevanceScoreCalculator = new IntactRelevanceScoreCalculator( rscProperties);
    }

    public SolrRelevanceScoreProcessor( IntactRelevanceScoreCalculator relevanceScoreCalculator ) {
        this.relevanceScoreCalculator = relevanceScoreCalculator;
    }

    /**
     * Calculates the relevancescore and add to the solrInputDocument
     * @param solrInputDocuments Collection of solrInputDocuments
     */
    public void process( Collection<SolrInputDocument> solrInputDocuments ) {
        for ( SolrInputDocument solrInputDocument : solrInputDocuments ) {
            String relevanceScore = relevanceScoreCalculator.calculateScore( solrInputDocument, converter);
            solrInputDocument.addField( FieldNames.RELEVANCE_SCORE,relevanceScore);
        }
    }
}
