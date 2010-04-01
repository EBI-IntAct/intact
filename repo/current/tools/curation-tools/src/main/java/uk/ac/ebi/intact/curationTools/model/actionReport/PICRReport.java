package uk.ac.ebi.intact.curationTools.model.actionReport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class PICRReport extends ActionReport{

    private HashMap<String, Set<String>> crossReferences = new HashMap<String, Set<String>>();
    private boolean isASwissprotEntry = false;

    public PICRReport(ActionName name) {
        super(name);
    }

    public HashMap<String, Set<String>> getCrossReferences(){
        return this.crossReferences;
    }

    public void addCrossReference(String databaseName, String accession){
        if (this.crossReferences.containsKey(databaseName)){
            Set<String> values = new HashSet<String>();
            values.add(accession);
            crossReferences.put(databaseName, values);
        }
        else {
            this.crossReferences.get(databaseName).add(accession);
        }
    }

    public boolean isAswissprotEntry(){
        return this.isASwissprotEntry;
    }

    public void setIsASwissprotEntry(boolean isSwissprot){
        this.isASwissprotEntry = isASwissprotEntry;
    }
}
