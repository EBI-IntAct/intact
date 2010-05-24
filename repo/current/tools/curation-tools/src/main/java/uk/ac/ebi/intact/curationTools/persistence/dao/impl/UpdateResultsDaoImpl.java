package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateResultsDao;

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
public class UpdateResultsDaoImpl extends UpdateBaseDaoImpl<UpdateResults> implements UpdateResultsDao {
    public UpdateResultsDaoImpl() {
        super(UpdateResults.class);
    }

    public UpdateResults getUpdateResultsWithId(long id) {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u where u.id = :id" );
        query.setParameter( "id", id);

        if (query.getResultList().isEmpty()){
            return null;
        }
        return (UpdateResults) query.getResultList().iterator().next();
    }

    public UpdateResults getUpdateResultsForProteinAc(String proteinAc) {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u where u.intactAccession = :proteinAc" );
        query.setParameter( "proteinAc", proteinAc);

        if (query.getResultList().isEmpty()){
            return null;
        }
        return (UpdateResults) query.getResultList().iterator().next();
    }

    public List<ActionReport> getActionReportsByNameAndProteinAc(ActionName name, String proteinAc) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.intactAccession = :proteinAc and a.name = :name" );
        query.setParameter( "proteinAc", proteinAc);
        query.setParameter( "name", name);

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsByNameAndResultId(ActionName name, long resultId) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.id = :id and a.name = :name" );
        query.setParameter( "id", resultId);
        query.setParameter( "name", name);

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsByStatusAndProteinAc(StatusLabel status, String proteinAc) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.intactAccession = :proteinAc and a.statusLabel = :status" );
        query.setParameter( "proteinAc", proteinAc);
        query.setParameter( "status", status.toString());

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsByStatusAndResultId(StatusLabel label, long resultId) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.id = :id and a.statusLabel = :label" );
        query.setParameter( "id", resultId);
        query.setParameter( "label", label.toString() );

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsWithBlastResultsByProteinAc(String protAc) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.intactAccession = :protAc and a.objClass = :class" );
        query.setParameter( "protAc", protAc);
        query.setParameter( "class", "uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport" );

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsWithSwissprotRemappingResultsByProteinAc(String protAc) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.intactAccession = :protAc and a.objClass = :class and a.name = :name" );
        query.setParameter( "protAc", protAc);
        query.setParameter("name", ActionName.BLAST_Swissprot_Remapping);
        query.setParameter( "class", "uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport" );

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsWithPICRCrossReferencesByProteinAc(String protAc) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.intactAccession = :protAc and a.objClass = :class" );
        query.setParameter( "protAc", protAc);
        query.setParameter( "class", "uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport" );

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsWithBlastResultsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.id = :id" );
        query.setParameter( "id", id);

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsWithSwissprotRemappingResultsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.id = :id and a.objClass = :class and a.name = :name" );
        query.setParameter( "id", id);
        query.setParameter("name", ActionName.BLAST_Swissprot_Remapping);
        query.setParameter( "class", "uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport" );

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsWithPICRCrossReferencesByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a where u.id = :id and a.objClass = :class" );
        query.setParameter( "id", id);
        query.setParameter( "class", "uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport" );

        return query.getResultList();
    }

    public List<ActionReport> getActionReportsWithWarningsByProteinAc(String proteinAc) {
        final Query query = getEntityManager().createQuery( "select a from UpdateResults as u join u.listOfActions as a join a.warnings as warn where u.intactAccession = :protac" );
        query.setParameter( "protac", proteinAc);

        return query.getResultList();
    }
}
