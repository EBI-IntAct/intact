package uk.ac.ebi.intact.protein.mapping.model.actionReport.impl;

import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.IntactReport;

import java.util.HashSet;
import java.util.Set;

/**
 * This report aims at storing the information and results of a query on the IntAct database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Apr-2010</pre>
 */

public class DefaultIntactReport extends DefaultMappingReport implements IntactReport{

    /**
     * The unique IntAct accession matching the CRC64
     */
    private String intactAc;

     /**
     * The list of IntAct entries matching the CRC64
     */
    private Set<String> possibleIntactIds = new HashSet<String>();

    /**
     * create a new DefaultIntactReport
     * @param name : the name of the action
     */
    public DefaultIntactReport(ActionName name) {
        super(name);
        this.intactAc = null;
    }

    /**
     *
     * @return the unique intactId
     */
    public String getIntactAc() {
        return intactAc;
    }

    /**
     * set the unique intact accession
     * @param intactAc : the intact accession
     */
    public void setIntactAc(String intactAc) {
        this.intactAc = intactAc;
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
    public void addPossibleIntactAc(String intactid) {
        this.possibleIntactIds.add(intactid);
    }
}
