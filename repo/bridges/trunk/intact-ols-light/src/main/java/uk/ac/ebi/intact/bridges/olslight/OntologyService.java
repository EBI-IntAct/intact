package uk.ac.ebi.intact.bridges.olslight;

import java.util.Map;

/**
 * An ontology service contract.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public interface OntologyService {

    String getTermName( final OntologyId ontologyId, final String termId ) throws OntologyServiceException;

    Map<String, String> getTermDirectChildren( final OntologyId ontologyId, final String termId ) throws OntologyServiceException;

    Map<String, String> getTermChildren( final OntologyId ontologyId, final String termId, final int depth ) throws OntologyServiceException;
}
