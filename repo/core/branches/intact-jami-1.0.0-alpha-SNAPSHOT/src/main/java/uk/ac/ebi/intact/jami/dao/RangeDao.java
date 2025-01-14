package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactRange;

import java.util.Collection;

/**
 * DAO for ranges
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public interface RangeDao extends IntactBaseDao<IntactRange>{

    public IntactRange getByAc(String ac);

    public Collection<IntactRange> getByIsLinkProperty(boolean isLinked);

    public Collection<IntactRange> getByStartStatus(String statusName, String statusMI);

    public Collection<IntactRange> getByEndStatus(String statusName, String statusMI);

    public Collection<IntactRange> getByStartAndEndStatus(String startName, String startMI, String endName, String endMI);

    public Collection<IntactCvTerm> getByResultingSequenceXref(String primaryId);

    public Collection<IntactCvTerm> getByResultingSequenceXrefLike(String primaryId);

    public Collection<IntactCvTerm> getByResultingSequenceXref(String dbName, String dbMI, String primaryId);

    public Collection<IntactCvTerm> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<IntactCvTerm> getByResultingSequenceXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactCvTerm> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);
}
