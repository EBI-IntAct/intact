package uk.ac.ebi.intact.dataexchange.psimi.solr;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import psidev.psi.mi.tab.model.builder.Row;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface SolrDocumentConverter {
    SolrInputDocument toSolrDocument(Row row);

    Row toRow(SolrDocument doc);
}
