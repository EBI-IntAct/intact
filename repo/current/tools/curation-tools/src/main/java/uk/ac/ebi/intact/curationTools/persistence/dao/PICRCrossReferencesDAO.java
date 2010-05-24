package uk.ac.ebi.intact.curationTools.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Mockable
public interface PICRCrossReferencesDAO extends UpdateBaseDao<PICRCrossReferences>{

    public List<PICRCrossReferences> getCrossReferencesByDatabaseName(String databaseName);

    public List<PICRCrossReferences> getCrossReferencesByDatabaseNameAndActionId(String databaseName, long actionId);

    public List<PICRCrossReferences> getCrossReferencesByProteinAc(String protAc);    

    public PICRCrossReferences getCrossReferenceWithId(long id);
}
