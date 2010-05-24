package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;
import uk.ac.ebi.intact.curationTools.persistence.dao.PICRCrossReferencesDAO;

import javax.persistence.Query;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */
@Repository
@Transactional(readOnly = true)
public class PICRCrossReferencesDAOImpl extends UpdateBaseDaoImpl<PICRCrossReferences> implements PICRCrossReferencesDAO{

    public PICRCrossReferencesDAOImpl() {
        super(PICRCrossReferences.class);
    }

    public List<PICRCrossReferences> getCrossReferencesByDatabaseName(String databaseName) {
        final Query query = getEntityManager().createQuery( "select pcr from PICRCrossReferences as pcr where pcr.database = :database" );
        query.setParameter( "database", databaseName);

        return query.getResultList();
    }

    public List<PICRCrossReferences> getCrossReferencesByDatabaseNameAndActionId(String databaseName, long actionId) {
        final Query query = getEntityManager().createQuery( "select pcr from PICRCrossReferences as pcr join pcr.picrReport as pr where pcr.database = :database and pr.id = :id" );
        query.setParameter( "database", databaseName);
        query.setParameter("id", actionId);

        return query.getResultList();
    }

    public List<PICRCrossReferences> getCrossReferencesByProteinAc(String protAc) {
        final Query query = getEntityManager().createQuery( "select pcr from PICRCrossReferences pcr join pcr.picrReport as picr join picr.updateResult as ur where ur.intactAccession = :ac" );
        query.setParameter( "ac", protAc);

        return query.getResultList();
    }

    public PICRCrossReferences getCrossReferenceWithId(long id) {
        final Query query = getEntityManager().createQuery( "select pcr from PICRCrossReferences as pcr where pcr.id = :id" );
        query.setParameter( "id", id);

        if (query.getResultList().isEmpty()){
             return null;
        }

        return (PICRCrossReferences) query.getResultList().iterator().next();
    }
}
