package uk.ac.ebi.intact.curationTools.actions;

import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public interface IdentificationAction {

    public String runAction(IdentificationContext context) throws ActionProcessingException;

    public List<ActionReport> getListOfActionReports();
}
