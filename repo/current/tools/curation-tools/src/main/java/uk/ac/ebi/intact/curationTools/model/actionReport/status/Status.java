package uk.ac.ebi.intact.curationTools.model.actionReport.status;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class Status {

    private StatusLabel label;
    private String description;

    public Status(StatusLabel label, String description){
        this.label = label;
        this.description = description;
    }

    public StatusLabel getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public void setLabel(StatusLabel label) {
        this.label = label;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
