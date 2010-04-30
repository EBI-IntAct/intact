package uk.ac.ebi.intact.curationTools.model.actionReport;

import uk.ac.ebi.intact.model.ProteinImpl;

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
    protected Set<ProteinImpl> listOfProteins = new HashSet<ProteinImpl>();
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

    public Set<ProteinImpl> getIntactMatchingProteins(){
        return this.listOfProteins;
    }

    public void addIntactMatchingProtein(ProteinImpl prot){
        this.listOfProteins.add(prot);
    }

    public String getQuerySequence() {
        return querySequence;
    }

    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
}
