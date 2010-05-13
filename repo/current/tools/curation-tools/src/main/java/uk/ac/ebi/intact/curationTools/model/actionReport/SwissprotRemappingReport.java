package uk.ac.ebi.intact.curationTools.model.actionReport;

/**
 * This report contains the results of a swissprot remapping process
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-May-2010</pre>
 */

public class SwissprotRemappingReport extends BlastReport {

    /**
     * The trembl accession to remap
     */
    private String tremblAccession;

    /**
     * Create a new SwissprotRemappingReport
     *
     * @param name : the name of the action
     */
    public SwissprotRemappingReport(ActionName name) {
        super(name);
        this.tremblAccession = null;
    }

    /**
     * set the trembl accession
     * @param tremblAccession : the accession of the trembl entry
     */
    public void setTremblAccession(String tremblAccession) {
        tremblAccession = tremblAccession;
    }

    /**
     *
     * @return
     */
    public String getTremblAccession() {
        return tremblAccession;
    }
}
