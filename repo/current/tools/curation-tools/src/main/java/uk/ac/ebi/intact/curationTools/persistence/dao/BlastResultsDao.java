package uk.ac.ebi.intact.curationTools.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.curationTools.model.results.BlastResults;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */
@Mockable
public interface BlastResultsDao extends UpdateBaseDao<BlastResults>{

    public List<BlastResults> getBlastResultsByIdentitySuperior(float identity);

    public List<BlastResults> getBlastResultsByActionIdAndIdentitySuperior(float identity, long actionId);

    public List<BlastResults> getAllSwissprotRemappingResults();

    public List<BlastResults> getAllSwissprotRemappingResultsFor(long actionId);

    public List<BlastResults> getSwissprotRemappingResultsByTremblAc(String tremblAc);

    public BlastResults getBlastResultById(long id);
}
