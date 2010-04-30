package uk.ac.ebi.intact.curationTools.model.actionReport;

import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    protected Set<String> possibleAccessions = new HashSet<String>();

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

    public Set<String> getPossibleAccessions(){
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
