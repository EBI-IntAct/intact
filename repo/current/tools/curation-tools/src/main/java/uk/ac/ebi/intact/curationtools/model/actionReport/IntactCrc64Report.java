package uk.ac.ebi.intact.curationtools.model.actionReport;

/**
 * This report aims at storing IntAct results of a search in Intact of a specific CRC64
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class IntactCrc64Report extends IntactReport {

    /**
     * The sequence used to query IntAct
     */
    protected String querySequence;

    /**
     * create a new IntactCrc64Report
     * @param name : the name of the report
     */
    public IntactCrc64Report(ActionName name) {
        super(name);
        this.querySequence = null;
    }

    /**
     *
     * @return  the sequence used to query intact
     */
    public String getQuerySequence() {
        return querySequence;
    }

    /**
     * set the sequence used to query intact
     * @param querySequence : the sequence
     */
    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
}
