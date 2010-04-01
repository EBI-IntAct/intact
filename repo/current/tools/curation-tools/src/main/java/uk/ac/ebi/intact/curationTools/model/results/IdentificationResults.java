package uk.ac.ebi.intact.curationTools.model.results;

import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Mar-2010</pre>
 */

public class IdentificationResults {

    private String finalUniprotId;
    private boolean isASwissprotEntry = false;
    private List<ActionReport> listOfActions = new ArrayList<ActionReport>();

    public IdentificationResults(){
        this.finalUniprotId = null;
    }

    public boolean isASwissprotEntry() {
        return isASwissprotEntry;
    }

    public void setIsASwissprotEntry(boolean isSwissprot){
        this.isASwissprotEntry = isSwissprot;
    }

    public void setUniprotId(String id){
        this.finalUniprotId = id;
    }

    public String getUniprotId(){
        return this.finalUniprotId;
    }

    public boolean hasUniqueUniprotId(){
        return this.finalUniprotId != null;
    }

    public List<ActionReport> getListOfActions(){
        return this.listOfActions;
    }

    public void addActionReport(ActionReport report){
        this.listOfActions.add(report);
    }

    public ActionReport getLastAction(){
        if (listOfActions.isEmpty()){
            return null;
        }
        return this.listOfActions.get(this.listOfActions.size() - 1);
    }
}
