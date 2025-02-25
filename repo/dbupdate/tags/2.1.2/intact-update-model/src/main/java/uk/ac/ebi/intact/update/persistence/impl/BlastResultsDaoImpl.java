package uk.ac.ebi.intact.update.persistence.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.update.model.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.update.persistence.BlastResultsDao;

import java.util.List;

/**
 * The basic implementation of BlastResultsDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Repository
@Transactional(readOnly = true)
@Lazy
public class BlastResultsDaoImpl extends UpdateBaseDaoImpl<BlastResults> implements BlastResultsDao {

    /**
     * Create a new BlastResultsDaoImpl
     */
    public BlastResultsDaoImpl() {
        super(BlastResults.class);
    }

    /**
     *
     * @param identity
     * @return
     */
    public List<BlastResults> getResultsByIdentitySuperior(float identity) {
        return getSession().createCriteria(BlastResults.class).add(Restrictions.ge("identity", identity)).list();
    }

    /**
     *
     * @param identity
     * @param actionId
     * @return
     */
    public List<BlastResults> getResultsByActionIdAndIdentitySuperior(float identity, long actionId) {
        return getSession().createCriteria(BlastResults.class).createAlias("blastReport", "b")
                .add(Restrictions.ge("identity", identity)).add(Restrictions.eq("b.id", actionId)).list();

    }

    /**
     *
     * @return
     */
    public List<BlastResults> getAllSwissprotRemappingResults() {
        return getSession().createCriteria(BlastResults.class).add(Restrictions.isNotNull("tremblAccession")).list();
    }

    /**
     *
     * @param actionId
     * @return
     */
    public List<BlastResults> getAllSwissprotRemappingResultsFor(long actionId) {
        return getSession().createCriteria(BlastResults.class).createAlias("blastReport", "b")
                .add(Restrictions.isNotNull("tremblAccession")).add(Restrictions.eq("b.id", actionId)).list();
    }

    /**
     *
     * @param tremblAc
     * @return
     */
    public List<BlastResults> getSwissprotRemappingResultsByTremblAc(String tremblAc) {
        return getSession().createCriteria(BlastResults.class).add(Restrictions.eq("tremblAccession", tremblAc)).list();
    }
}
