package uk.ac.ebi.intact.curationTools.model.actionReport;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class IntactCrc64Report extends ActionReport {

    private String intactid;
    protected Set<String> listOfProteins = new HashSet<String>();
    protected String querySequence;

    public IntactCrc64Report(ActionName name) {
        super(name);
        this.intactid = null;
        this.querySequence = null;
    }

    public String getIntactid() {
        return intactid;
    }

    public void setIntactid(String intactid) {
        this.intactid = intactid;
    }

    public Set<String> getIntactMatchingProteins(){
        return this.listOfProteins;
    }

    public void addIntactMatchingProtein(String prot){
        this.listOfProteins.add(prot);
    }

    public String getQuerySequence() {
        return querySequence;
    }

    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
}
