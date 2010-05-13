package uk.ac.ebi.intact.curationTools.model.actionReport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This report aims at storing the information and results of a query on PICR
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */

public class PICRReport extends ActionReport{

    /**
     * the list of cross references that PICR could collect
     */
    private HashMap<String, Set<String>> crossReferences = new HashMap<String, Set<String>>();

    /**
     * Create a new PICRReport
     * @param name : name of the action
     */
    public PICRReport(ActionName name) {
        super(name);
    }

    /**
     *
     * @return the map containing the cross references
     */
    public HashMap<String, Set<String>> getCrossReferences(){
        return this.crossReferences;
    }

    /**
     * add a new cross reference
     * @param databaseName : database name
     * @param accession : accession in the database
     */
    public void addCrossReference(String databaseName, String accession){
        if (!this.crossReferences.containsKey(databaseName)){
            Set<String> values = new HashSet<String>();
            values.add(accession);
            crossReferences.put(databaseName, values);
        }
        else {
            this.crossReferences.get(databaseName).add(accession);
        }
    }
}
