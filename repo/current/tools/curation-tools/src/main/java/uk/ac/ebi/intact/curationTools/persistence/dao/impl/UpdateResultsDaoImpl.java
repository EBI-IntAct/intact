package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.BlastResults;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateResultsDao;

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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public UpdateResults getUpdateResultsForProteinAc(String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsByNameAndProteinAc(ActionName name, String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsByNameAndResultId(ActionName name, long resultId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsByStatusAndProteinAc(StatusLabel status, String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsByStatusAndResultId(ActionName name, long resultId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsWithBlastResultsByProteinAc(String protAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsWithSwissprotRemappingResultsByProteinAc(String protAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsWithPICRCrossReferencesByProteinAc(String protAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsWithBlastResultsByResultsId(long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsWithSwissprotRemappingResultsByResultsId(long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsWithPICRCrossReferencesByResultsId(long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<BlastResults> getBlastResultsByProteinAc(String ac) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<BlastResults> getBlastResultsByProteinShortLabel(String ac) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<PICRCrossReferences> getCrossReferencesByProteinAc(String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getActionReportsWithWarningsByProteinAc(String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<BlastResults> getBlastResultsByProteinAcAndIdentitySuperior(float identity, String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<BlastResults> getAllSwissprotRemappingResultsFor(String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<PICRCrossReferences> getCrossReferencesByDatabaseNameAndProteinAc(String databaseName, String proteinAc) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
