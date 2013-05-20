package uk.ac.ebi.intact.protein.mapping.results.impl;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultMappingReport;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;

import java.util.ArrayList;
import java.util.List;


/**
 * This class contains all the results of the protein identification process.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Mar-2010</pre>
 */
public class DefaultIdentificationResults implements IdentificationResults<DefaultMappingReport>{

    /**
     * the unique uniprot id identifying the protein
     */
    private String finalUniprotId;

    /**
     * the list of actions done to identify the protein
     */
    private List<DefaultMappingReport> listOfActions = new ArrayList<DefaultMappingReport>();

    /**
     * Create a new Identificationresult
     */
    public DefaultIdentificationResults(){
        this.finalUniprotId = null;
    }

    public void setListOfActions(List<DefaultMappingReport> listOfActions) {
        this.listOfActions = listOfActions;
    }

    /**
     * set the final uniprot accession identifying the protein
     * @param id : uniprot accession
     */
    public void setFinalUniprotId(String id){
        this.finalUniprotId = id;
    }

    /**
     *
     * @return the final uniprot accession identifying the protein
     */
    public String getFinalUniprotId(){
        return this.finalUniprotId;
    }

    /**
     *
     * @return true if the unique uniprot id is not null
     */
    public boolean hasUniqueUniprotId(){
        return this.finalUniprotId != null;
    }

    /**
     *
     * @return the list of actions done to identify the protein
     */
    public List<DefaultMappingReport> getListOfActions(){
        return this.listOfActions;
    }

    /**
     * add a new action report to the list of reports
     * @param report : action report
     */
    public boolean addActionReport(DefaultMappingReport report){
        if (this.listOfActions.add(report)){
            return true;
        }
        return false;
    }

    public boolean removeActionReport(DefaultMappingReport report){
        if (this.listOfActions.remove(report)){
            return true;
        }
        return false;
    }

    /**
     *
     * @return the last action report added to this result
     */
    public DefaultMappingReport getLastAction(){
        if (listOfActions.isEmpty()){
            return null;
        }
        return this.listOfActions.get(this.listOfActions.size() - 1);
    }

    /**
     *
     * @param name : name of a specific action
     * @return the list of actions with this specific name which have been done to identify the protein
     */
    public List<DefaultMappingReport> getActionsByName(ActionName name){
        ArrayList<DefaultMappingReport> reports = new ArrayList<DefaultMappingReport>();

        for (DefaultMappingReport action : this.listOfActions){
            if (action.getName() != null && action.getName().equals(name)){
                reports.add(action);
            }
        }
        return reports;
    }

    @Override
    public boolean equals( Object o ) {
        if ( !super.equals(o) ) {
            return false;
        }

        final DefaultIdentificationResults results = (DefaultIdentificationResults) o;

        if ( finalUniprotId != null ) {
            if (!finalUniprotId.equals( results.getFinalUniprotId() )){
                return false;
            }
        }
        else if (results.getFinalUniprotId()!= null){
            return false;
        }

        return true;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        int code = 29;

        code = 29 * code + super.hashCode();

        if ( finalUniprotId != null ) {
            code = 29 * code + finalUniprotId.hashCode();
        }

        return code;
    }

    public boolean isIdenticalTo(Object o){

        if (!(o instanceof DefaultIdentificationResults)){
            return false;
        }

        final DefaultIdentificationResults results = (DefaultIdentificationResults) o;

        if ( finalUniprotId != null ) {
            if (!finalUniprotId.equals( results.getFinalUniprotId() )){
                return false;
            }
        }
        else if (results.getFinalUniprotId()!= null){
            return false;
        }

        return CollectionUtils.isEqualCollection(this.listOfActions, results.getListOfActions());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Identification result : [" + finalUniprotId != null ? finalUniprotId : "none");

        buffer.append("] \n");

        if (!listOfActions.isEmpty()){
            buffer.append(" List of Mapping Actions : ");

            for (MappingReport rep : listOfActions) {
                buffer.append(rep.toString() + " \n");
            }
        }

        return buffer.toString();
    }
}

