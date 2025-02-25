package uk.ac.ebi.intact.update.persistence;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.update.model.protein.mapping.results.PICRCrossReferences;

import java.util.List;

/**
 * This interface contains some methods to query the database and get specific PICRCrossReferences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Mockable
public interface PICRCrossReferencesDao extends UpdateBaseDao<PICRCrossReferences> {

    /**
     *
     * @param databaseName
     * @return the list of PICRCrossReferences with a specific database name
     */
    public List<PICRCrossReferences> getAllCrossReferencesByDatabaseName(String databaseName);

    /**
     *
     * @param databaseName
     * @param actionId
     * @return the list of PICRCrossReferences with a specific database name and attached to a specific action
     */
    public List<PICRCrossReferences> getCrossReferencesByDatabaseNameAndActionId(String databaseName, long actionId);
}
