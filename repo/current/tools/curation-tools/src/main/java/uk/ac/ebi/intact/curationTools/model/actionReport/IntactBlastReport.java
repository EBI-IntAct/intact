package uk.ac.ebi.intact.curationTools.model.actionReport;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class IntactBlastReport extends BlastReport {

    private String intactid;

    public IntactBlastReport(ActionName name) {
        super(name);
    }

    public String getIntactid() {
        return intactid;
    }

    public void setIntactid(String intactid) {
        this.intactid = intactid;
    }
}
