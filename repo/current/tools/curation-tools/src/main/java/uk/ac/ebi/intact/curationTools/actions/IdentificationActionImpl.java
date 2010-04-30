package uk.ac.ebi.intact.curationTools.actions;

import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public abstract class IdentificationActionImpl implements IdentificationAction {

    protected List<ActionReport> listOfReports = new ArrayList<ActionReport>();

    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }
}
