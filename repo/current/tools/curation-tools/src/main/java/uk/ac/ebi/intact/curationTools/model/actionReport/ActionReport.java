package uk.ac.ebi.intact.curationTools.model.actionReport;

import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class ActionReport {

    protected ActionName name;
    protected Status status;
    protected List<String> warnings = new ArrayList<String>();
    protected List<String> possibleAccessions = new ArrayList<String>();

    public ActionReport(ActionName name){
        this.name = name;
    }

    public ActionName getName(){
        return this.name;
    }

    public void setName(ActionName name){
        this.name = name;
    }

    public List<String> getWarnings(){
        return this.warnings;
    }

    public void addWarning(String warn){
        this.warnings.add(warn);
    }

    public List<String> getPossibleAccessions(){
        return this.possibleAccessions;
    }

    public void addPossibleAccession(String ac){
        this.possibleAccessions.add(ac);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
