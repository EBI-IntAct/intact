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

import uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.iterators.SolrRigIterator;
import uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.iterators.SolrDocumentIterator;

import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * The main processor which registers all the processors and start processing the SolrInputDocuments
 * with each of the registered processors
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SolrIndexProcessor {

    private static final Log log = LogFactory.getLog( SolrIndexProcessor.class );

    Collection<SolrDocumentProcessor> processors = new ArrayList<SolrDocumentProcessor>();
    SolrServer solrServer;

    public SolrIndexProcessor( SolrServer solrServer ) {
        if ( solrServer == null ) {
            throw new NullPointerException( "You must give a non null solrServer" );
        }
        this.solrServer = solrServer;
    }

    public boolean registerProcessor( SolrDocumentProcessor solrDocumentProcessor ) {
        return processors.add( solrDocumentProcessor );
    }

    public void startProcessing() throws IOException, SolrServerException {
        if ( processors == null || processors.size() == 0 ) {
            log.warn( "No Processor is registered..Please do register the processors before starting the Processing" );
        }
        SolrRigIterator rigIterator = this.createRigIterator();
        while ( rigIterator.hasNext() ) {
            final Collection<SolrInputDocument> inputDocuments = rigIterator.nextSolrDocumentGroup();
            for ( SolrDocumentProcessor solrDocumentProcessor : processors ) {
                solrDocumentProcessor.process( inputDocuments );
            }//end for
            solrServer.add( inputDocuments );

        }//end while
        solrServer.commit();
    }

    protected SolrDocumentIterator createDocumentIterator() {
        return new SolrDocumentIterator( solrServer);
    }

    protected SolrRigIterator createRigIterator() {
        return new SolrRigIterator( solrServer );
    }

    public SolrServer getSolrServer() {
        return solrServer;
    }
}
