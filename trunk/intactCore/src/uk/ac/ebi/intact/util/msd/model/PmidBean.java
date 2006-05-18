package uk.ac.ebi.intact.util.msd.model;

/**
 * Created by IntelliJ IDEA.
 * User: krobbe
 * Date: 23-Mar-2006
 * Time: 15:47:24
 * To change this template use File | Settings | File Templates.
 */
public class PmidBean {
    private String pdbCode;
    private String pmid;
    private String ordinal;

    public String getPdbCode() {
        return pdbCode;
    }

    public void setPdbCode(String pdbCode) {
        this.pdbCode = pdbCode;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(String ordinal) {
        this.ordinal = ordinal;
    }
}
