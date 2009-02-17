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

import java.util.Iterator;
import java.util.NoSuchElementException;

import uk.ac.ebi.intact.dataexchange.psimi.solr.IntactSolrException;
import uk.ac.ebi.intact.dataexchange.psimi.solr.FieldNames;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import psidev.psi.mi.tab.model.builder.Row;
import psidev.psi.mi.tab.model.BinaryInteraction;

/**
 * Iterator iterates over the solrdocuments one by one,
 * all the documents returned are sorted by RIG by default
 * and the documents are returned in chunks of 50
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SolrDocumentIterator implements SolrIterator<SolrDocument> {

    private SolrServer solrServer;
    private String query;

    private Iterator<org.apache.solr.common.SolrDocument> chunkSolrIterator;
    private SolrDocumentConverter converter = new SolrDocumentConverter();
    private int FIRSTRESULT;
    final private int MAXRESULTS = 50;

    public SolrDocumentIterator( SolrServer solrServer) {
            if ( solrServer == null ) {
                throw new NullPointerException( "You must give a non null solrServer" );
            }

            this.solrServer = solrServer;
            this.query = "*:*";
        }


    public SolrDocumentIterator( SolrServer solrServer,String query ) {
        if ( solrServer == null ) {
            throw new NullPointerException( "You must give a non null solrServer" );
        }

        if ( query == null ) {
            throw new NullPointerException( "You must give a non null query" );
        }
        this.solrServer = solrServer;
        this.query = query;
    }


    public org.apache.solr.common.SolrDocument next() {
        return chunkSolrIterator.next();
    }

    public Row nextRow() {
        if ( hasNext() ) {
            return converter.toRow( chunkSolrIterator.next() );
        }
        throw new NoSuchElementException( "No more elements to return");
    }

    public String nextMitabLine() {
        if ( hasNext() ) {
            return converter.toMitabLine( chunkSolrIterator.next() );
        }
        throw new NoSuchElementException( "No more elements to return");
    }

    public BinaryInteraction nextIntactBinaryInteraction() {
        if ( hasNext() ) {
            return converter.toBinaryInteraction( chunkSolrIterator.next() );
        }
        throw new NoSuchElementException( "No more elements to return");
    }

    /**
     * Checks if the chunkSolrIterator is initialized and hasNext(default implementation from Iterator)
     * else query the solr server the fetches the documents in chunks specified by the MAXRESULTS
     * @return true or false
     */
    public boolean hasNext()  {

        if ( chunkSolrIterator != null && chunkSolrIterator.hasNext() ) {
            return true;
        } else {
            //query and load SolrDocuments
            try {
                SolrQuery solrQuery = new SolrQuery( query );
                solrQuery.setStart( FIRSTRESULT );
                solrQuery.setRows( MAXRESULTS );
                solrQuery.setFields( FieldNames.LINE, FieldNames.RIGID);
                solrQuery.addSortField( FieldNames.RIGID, SolrQuery.ORDER.asc );

                QueryResponse queryResponse = solrServer.query( solrQuery );
                final SolrDocumentList documentList = queryResponse.getResults();
                chunkSolrIterator = documentList.iterator();
                FIRSTRESULT += MAXRESULTS;
            } catch ( SolrServerException e ) {
                throw new IntactSolrException( "Error thrown when retrieving data with the query  " + query, e );
            }
        }
        return chunkSolrIterator.hasNext();
    }

    public void remove() {
        throw new UnsupportedOperationException("This iterator is readonly");
    }

    public Iterator<SolrDocument> getChunkSolrIterator() {
        return chunkSolrIterator;
    }
}
