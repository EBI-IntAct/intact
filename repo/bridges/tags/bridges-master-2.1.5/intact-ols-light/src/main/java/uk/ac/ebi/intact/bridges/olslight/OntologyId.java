package uk.ac.ebi.intact.bridges.olslight;

/**
 * Enum of supported ontologies from OLS.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public enum OntologyId {

    // Complete list can be found at: http://www.ebi.ac.uk/ontology-lookup/ontologyList.do

    PSI_MI( "MI" ),
    PSI_MOD( "MOD" ),
    PSI_MS( "MS" ),
    GENE_ONTOLOGY( "GO" ),
    TAXONOMY( "NEWT" );

    private String id;

    OntologyId( String ontologyId ) {
        this.id = ontologyId;
    }

    public String getId() {
        return id;
    }
}
