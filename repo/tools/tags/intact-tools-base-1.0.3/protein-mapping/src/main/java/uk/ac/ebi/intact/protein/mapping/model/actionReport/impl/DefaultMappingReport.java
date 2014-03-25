package uk.ac.ebi.intact.protein.mapping.model.actionReport.impl;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class contains all the information/ results that an action can store
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */
public class DefaultMappingReport implements MappingReport{

    /**
     * the name of the action
     */
    protected ActionName name;

    /**
     * the status of the action
     */
    protected Status status;

    /**
     * a list of warnings
     */
    protected List<String> warnings = new ArrayList<String>();

    /**
     * the list of possible uniprot proteins which need to be reviewed by a curator
     */
    protected Set<String> possibleAccessions = new HashSet<String>();

    /**
     * boolean value to know if the unique uniprot id that this action retrieved is a swissprot entry
     */
    private boolean isASwissprotEntry = false;

    /**
     * Create a new report for an action with a specific name
     * @param name the naem of the action
     */
    public DefaultMappingReport(ActionName name){
        this.name = name;
    }

    /**
     *
     * @return the name of the action
     */
    public ActionName getName(){
        return this.name;
    }

    /**
     * set a new name for this report
     * @param name : new name
     */
    public void setName(ActionName name){
        this.name = name;
    }

    /**
     *
     * @return the warnings
     */
    public List<String> getWarnings(){
        return this.warnings;
    }

    /**
     * add a warning to the list of warnings
     * @param warn : new warning
     */
    public void addWarning(String warn){
        this.warnings.add(warn);
    }

    /**
     *
     * @return the list of possible uniprot accessions
     */
    public Set<String> getPossibleAccessions(){
        return this.possibleAccessions;
    }

    /**
     * Set the warnings
     * @param warnings
     */
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    /**
     * set the possible accessions
     * @param possibleAccessions
     */
    public void setPossibleAccessions(Set<String> possibleAccessions) {
        this.possibleAccessions = possibleAccessions;
    }

    /**
     * add a new possible uniprot accession
     * @param ac : new uniprot accession
     */
    public void addPossibleAccession(String ac){
        this.possibleAccessions.add(ac);
    }

    /**
     *
     * @return the status of this action
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @return the status label of this action. Can be FAILED, TO_BE_REVIEWED or COMPLETED. However, if the status of this object
     * is null and/or its label is null, this method return NONE
     */
    public StatusLabel getStatusLabel() {
        if (this.status == null){
            return StatusLabel.NONE;
        }
        else {
            if (this.status.getLabel() == null){
                return StatusLabel.NONE;
            }
            else {
                return this.status.getLabel();
            }
        }
    }

    /**
     * Set the status label of this action
     * @param label : the label. (COMPLETED, TO_BE_REVIEWED or FAILED)
     */
    public void setStatusLabel(StatusLabel label){

        if (label != null){
            if (this.status == null){
                status = new Status(label, null);
            }
            else {
                status.setLabel(label);
            }
        }
    }

    /**
     * Set the description of this action
     * @param description : the status description
     */
    public void setStatusDescription(String description){
        if (this.status == null){
            status = new Status(StatusLabel.NONE, description);
        }
        else {
            status.setDescription(description);
        }
    }


    /**
     *
     * @return the status description of this action.
     */
    public String getStatusDescription() {
        if (this.status == null){
            return null;
        }
        else {
            return this.status.getDescription();
        }
    }

    /**
     * set the status of this action
     * @param status : the status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     *
     * @return the isASwissprotEntry boolean
     */
    public boolean isASwissprotEntry(){
        return this.isASwissprotEntry;
    }

    /**
     * set the isASwissprotEntry value
     * @param isSwissprot : boolean value
     */
    public void setIsASwissprotEntry(boolean isSwissprot){
        this.isASwissprotEntry = isSwissprot;
    }

    @Override
    public boolean equals( Object o ) {
        if ( !super.equals(o) ) {
            return false;
        }

        final DefaultMappingReport report = (DefaultMappingReport) o;

        if ( name != null ) {
            if (!name.equals( report.getName() )){
                return false;
            }
        }
        else if (report.getName()!= null){
            return false;
        }

        if ( status != null ) {
            if (!status.equals( report.getStatus() )){
                return false;
            }
        }
        else if (report.getStatus()!= null){
            return false;
        }

        if (isASwissprotEntry != report.isASwissprotEntry()){
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

        if ( name != null ) {
            code = 29 * code + name.hashCode();
        }

        if ( status != null ) {
            code = 29 * code + status.hashCode();
        }

        code = 29 * code + Boolean.toString(isASwissprotEntry).hashCode();

        return code;
    }

    public boolean isIdenticalTo(Object o){

        if (!(o instanceof DefaultMappingReport)){
            return false;
        }

        final DefaultMappingReport report = (DefaultMappingReport) o;

        if ( name != null ) {
            if (!name.equals( report.getName() )){
                return false;
            }
        }
        else if (report.getName()!= null){
            return false;
        }

        if ( status != null ) {
            if (!status.equals( report.getStatus() )){
                return false;
            }
        }
        else if (report.getStatus()!= null){
            return false;
        }

        if (isASwissprotEntry != report.isASwissprotEntry()){
            return false;
        }

        if (!CollectionUtils.isEqualCollection(this.warnings, report.getWarnings())){
            return false;
        }

        return CollectionUtils.isEqualCollection(this.possibleAccessions, report.getPossibleAccessions());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Mapping report : [" + name != null ? name.toString() : "");

        buffer.append(", " + status != null ? status.toString() : "");

        buffer.append("] \n");

        if (!warnings.isEmpty()){
            buffer.append(" WARNINGS : ");

            for (String warn : warnings) {
                buffer.append(warn + " ; ");
            }

            buffer.append("\n");
        }

        buffer.append(" Is A Swissprot entry : " + isASwissprotEntry + "\n");

        if (!possibleAccessions.isEmpty()){
            buffer.append(" POSSIBLE ACCESSIONS : ");

            for (String acc : possibleAccessions) {
                buffer.append(acc + " ; ");
            }

            buffer.append("\n");
        }

        return buffer.toString();
    }
}
