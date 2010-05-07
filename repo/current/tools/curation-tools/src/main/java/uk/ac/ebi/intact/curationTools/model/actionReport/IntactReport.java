package uk.ac.ebi.intact.curationTools.model.actionReport;

import java.util.HashSet;
import java.util.Set;

/**
 * This report aims at storing the information and results of a query on the IntAct database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public class IntactReport extends ActionReport {

    /**
     * The unique IntAct accession matching the CRC64
     */
    private String intactid;

     /**
     * The list of IntAct entries matching the CRC64
     */
    private Set<String> possibleIntactIds = new HashSet<String>();

    /**
     * create a new IntactReport
     * @param name : the name of the action
     */
    public IntactReport(ActionName name) {
        super(name);
        this.intactid = null;
    }

    /**
     *
     * @return the unique intactId
     */
    public String getIntactid() {
        return intactid;
    }

    /**
     * set the unique intact accession
     * @param intactid : the intact accession
     */
    public void setIntactid(String intactid) {
        this.intactid = intactid;
    }

    /**
     *
     * @return  the list of possible Intact accessions
     */
    public Set<String> getPossibleIntactIds() {
        return this.possibleIntactIds;
    }

    /**
     * add a new possible intact accession
     * @param intactid : possible intact accession
     */
    public void addPossibleIntactid(String intactid) {
        this.possibleIntactIds.add(intactid);
    }
}
