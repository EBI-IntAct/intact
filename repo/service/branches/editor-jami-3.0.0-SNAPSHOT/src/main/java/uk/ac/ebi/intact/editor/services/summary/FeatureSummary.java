package uk.ac.ebi.intact.editor.services.summary;

/**
 * Summary of a feature
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/12/14</pre>
 */

public class FeatureSummary {
    private String ac;
    private String caution;
    private String internalRemark;
    private String shortName;

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public String getInternalRemark() {
        return internalRemark;
    }

    public void setInternalRemark(String internalRemark) {
        this.internalRemark = internalRemark;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
