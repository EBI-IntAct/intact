package uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.processors;

import org.apache.solr.common.SolrInputDocument;

import java.util.Collection;

/**
 * Interface for all the Solr Processors
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public interface SolrDocumentProcessor {

    public void process( Collection<SolrInputDocument> solrInputDocuments);
}
