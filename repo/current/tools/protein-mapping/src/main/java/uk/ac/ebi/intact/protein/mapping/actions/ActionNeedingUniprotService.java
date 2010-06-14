package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.kraken.util.IndexField;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;

/**
 * This class is the class to extend if the IdentificationAction  is using a uniprot service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2010</pre>
 */

public abstract class ActionNeedingUniprotService extends IdentificationActionImpl{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotNameSearchProcess.class );

    /**
     * The uniprot service
     */
    protected UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();

    /**
     * Create a query to get the uniprot entries with this organism taxId
     * @param organismName : the organism of the protein to identify
     * @return the query as a String
     */
    protected String buildTaxIdQuery(String organismName){
        String query = IndexField.NCBI_TAXON_ID + ":" + organismName;
        return query ;
    }

    /**
     * Add a filter on the Taxid in the initial query
     * @param initialquery : the initial query
     * @param organism  : the organism of the protein
     * @return the query as a String
     */
    protected String addTaxIdToQuery(String initialquery, String organism){
        String query = "(" + initialquery + ")" + " AND " + "(" + buildTaxIdQuery(organism) + ")";

        return query;
    }

    /**
     * Add a filter on the swissprot database in the initial query
     * @param initialquery : the initial query
     * @return the query as a String
     */
    protected String addFilterOnSwissprot(String initialquery){
        String query = "(" + initialquery + ")" + " AND " + "(" + IndexField.REVIEWED + ")";

        return query;
    }

    /**
     * Create the query instance
     * @param query : the query as a String
     * @return the query instance
     */
    protected Query getQueryFor(String query){
        return UniProtQueryBuilder.buildQuery(query);
    }
}
