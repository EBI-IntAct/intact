package uk.ac.ebi.intact.protein.mapping.model.actionReport.impl;

import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.IntactCrc64Report;

/**
 * This report aims at storing IntAct results of a search in Intact of a specific CRC64
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class DefaultIntactCrc64Report extends DefaultIntactReport implements IntactCrc64Report{

    /**
     * The sequence used to query IntAct
     */
    protected String querySequence;

    /**
     * create a new DefaultIntactCrc64Report
     * @param name : the name of the report
     */
    public DefaultIntactCrc64Report(ActionName name) {
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
