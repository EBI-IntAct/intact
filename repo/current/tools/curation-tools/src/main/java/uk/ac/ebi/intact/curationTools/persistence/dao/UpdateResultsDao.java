package uk.ac.ebi.intact.curationTools.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */
@Mockable
public interface UpdateResultsDao extends UpdateBaseDao<UpdateResults> {

    public UpdateResults getUpdateResultsWithId(long id);

    public UpdateResults getUpdateResultsForProteinAc(String proteinAc);

    public List<ActionReport> getActionReportsWithWarningsByProteinAc(String proteinAc);

    public List<UpdateResults> getResultsContainingAction(ActionName name);

    public List<UpdateResults> getResultsContainingActionWithLabel(StatusLabel label);

    public List<UpdateResults> getUpdateResultsWithSwissprotRemapping();

    public List<UpdateResults> getSuccessfulUpdateResults();

    public List<UpdateResults> getUpdateResultsToBeReviewedByACurator();

    public List<UpdateResults> getProteinNotUpdatedBecauseNoSequenceAndNoIdentityXrefs();

    public List<UpdateResults> getUnsuccessfulUpdateResults();

    public List<UpdateResults> getUpdateResultsWithConflictBetweenSequenceAndIdentityXRefs();

    public List<UpdateResults> getUpdateResultsWithConflictBetweenSwissprotSequenceAndFeatureRanges();
}
