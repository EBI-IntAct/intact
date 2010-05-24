package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.persistence.dao.ActionReportDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Repository
@Scope(org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
public class ActionReportDaoImpl<T extends ActionReport> extends UpdateBaseDaoImpl<T> implements ActionReportDao<T>{

    public ActionReportDaoImpl() {
        super((Class<T>) ActionReport.class);
    }

    public ActionReportDaoImpl( Class<T> entityClass, EntityManager entityManager ) {
        super( entityClass, entityManager);
    }

    public ActionReport getByReportId(long id) {
        final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar where ar.id = :id" );
        query.setParameter( "id", id);

        if (query.getResultList().isEmpty()){
            return null;
        }

        return (ActionReport) query.getResultList().iterator().next();
    }

    public List<ActionReport> getByActionName(ActionName name) {
        final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar where ar.name = :name" );
        query.setParameter( "name", name);

        return query.getResultList();
    }

    public List<ActionReport> getByReportStatus(StatusLabel status) {
        final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar where ar.statusLabel = :label" );
        query.setParameter( "label", status.toString());

        return query.getResultList();
    }

    public List<ActionReport> getAllReportsWithWarnings() {
        final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar join ar.warnings as warn" );

        return query.getResultList();
    }

    public List<ActionReport> getAllReportsWithSeveralPossibleUniprot() {
         final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar where ar.listOfPossibleAccessions <> null" );

        return query.getResultList();
    }

    public List<PICRReport> getAllPICRReports() {
        final Query query = getEntityManager().createQuery( "select ar from PICRReport as ar" );

        return query.getResultList();
    }

    public List<BlastReport> getAllBlastReports() {
        final Query query = getEntityManager().createQuery( "select ar from BlastReport as ar" );

        return query.getResultList();
    }

    public List<BlastReport> getAllSwissprotRemappingReports() {
        final Query query = getEntityManager().createQuery( "select ar from BlastReport as ar where ar.name = :name" );
        query.setParameter("name", ActionName.BLAST_Swissprot_Remapping);

        return query.getResultList();
    }

    public List<ActionReport> getReportsWithWarningsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar join ar.updateResult as res join ar.warnings as warn where res.id = :id" );
        query.setParameter( "id", id);

        return query.getResultList();
    }

    public List<ActionReport> getAllReportsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar join ar.updateResult as res where res.id = :id" );
        query.setParameter( "id", id);

        return query.getResultList();
    }

    public List<ActionReport> getReportsWithSeveralPossibleUniprotByResultId(long id) {
        final Query query = getEntityManager().createQuery( "select ar from ActionReport as ar join ar.updateResult as res where ar.listOfPossibleAccessions <> null and res.id = :id" );
        query.setParameter("id", id);

        return query.getResultList();
    }
}
