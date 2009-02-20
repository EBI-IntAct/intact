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
package uk.ac.ebi.intact.dataexchange.psimi.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * Convenience class to simplify searching.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSolrSearcher {

    private SolrServer solrServer;

    public IntactSolrSearcher(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public SolrSearchResult search(String query, Integer firstResult, Integer maxResults) throws IntactSolrException {
        if (query == null) throw new NullPointerException("Null query");
        
        if ("*".equals(query)) query = "*:*";

        SolrQuery solrQuery = new SolrQuery(query);

        if (firstResult != null) solrQuery.setStart(firstResult);

        if (maxResults != null) {
            solrQuery.setRows(maxResults);
        } else {
            solrQuery.setRows(Integer.MAX_VALUE);
        }

        return search(solrQuery);
    }

    public SolrSearchResult search(SolrQuery query) throws IntactSolrException {
        query.setFields(query.getFields()+", line, pkey");

        QueryResponse queryResponse;
        try {
            queryResponse = solrServer.query(query);
        } catch (SolrServerException e) {
            throw new IntactSolrException("Problem searching with query: "+query, e);
        }
        return new SolrSearchResult(queryResponse);
    }
}
