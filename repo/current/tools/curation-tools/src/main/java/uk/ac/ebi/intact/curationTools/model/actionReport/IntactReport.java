package uk.ac.ebi.intact.curationTools.model.actionReport;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public class IntactReport extends ActionReport {

    private String intactid;

    private Set<String> possibleIntactIds = new HashSet<String>();

    public IntactReport(ActionName name) {
        super(name);
    }

    public String getIntactid() {
        return intactid;
    }

    public void setIntactid(String intactid) {
        this.intactid = intactid;
    }

    public Set<String> getPossibleIntactIds() {
        return this.possibleIntactIds;
    }

    public void addPossibleIntactid(String intactid) {
        this.possibleIntactIds.add(intactid);
    }
}
