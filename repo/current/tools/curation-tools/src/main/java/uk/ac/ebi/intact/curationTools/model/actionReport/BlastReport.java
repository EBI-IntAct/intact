package uk.ac.ebi.intact.curationTools.model.actionReport;

import uk.ac.ebi.intact.bridges.blast.model.BlastProtein;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class BlastReport extends ActionReport{

    protected List<BlastProtein> listOfProteins = new ArrayList<BlastProtein>();
    protected String querySequence;

    public BlastReport(ActionName name){
         super(name);
        this.querySequence = null;
    }

    public List<BlastProtein> getBlastMatchingProteins(){
        return this.listOfProteins;
    }

    public void addBlastMatchingProtein(BlastProtein prot){
        this.listOfProteins.add(prot);
    }

    public String getQuerySequence() {
        return querySequence;
    }

    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
}
