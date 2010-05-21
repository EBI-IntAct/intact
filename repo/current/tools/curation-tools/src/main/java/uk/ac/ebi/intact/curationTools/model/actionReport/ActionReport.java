package uk.ac.ebi.intact.curationTools.model.actionReport;

import org.hibernate.annotations.CollectionOfElements;
import uk.ac.ebi.intact.curationTools.model.HibernatePersistent;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;

import javax.persistence.*;
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
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="objclass", discriminatorType=DiscriminatorType.STRING, length = 100)
@DiscriminatorValue("uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport")
@Table( name = "ia_protein_action" )
public class ActionReport implements HibernatePersistent{

    private Long idAction;

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

    protected UpdateResults updateResult;

    /**
     * Create a new report for an action with a specific name
     * @param name the naem of the action
     */
    public ActionReport(ActionName name){
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")
    @SequenceGenerator(name="SEQ_STORE", sequenceName="my_sequence" )
    public Long getId() {
        return idAction;
    }

    public void setId(Long idAction) {
        this.idAction = idAction;
    }

    /**
     *
     * @return the name of the action
     */
    @Column(name = "name", nullable = false)
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
    @CollectionOfElements
    @JoinTable(name = "ia_action2warning", joinColumns = @JoinColumn(name="action_id"))
    @Column(name = "warnings", nullable = false)
    // TODO change the annotation with @elementCollection when we will change the version of hibernate
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
    @Transient
    public Set<String> getPossibleAccessions(){
        return this.possibleAccessions;
    }

    @Column(name = "possible_uniprot", nullable = true, length = 500)
    public String getListOfPossibleAccessions(){

        if (this.possibleAccessions.isEmpty()){
            return null;
        }
        StringBuffer concatenedList = new StringBuffer( 1064 );

        for (String prot : this.possibleAccessions){
            concatenedList.append(prot+";");
        }

        if (concatenedList.length() > 0){
            concatenedList.deleteCharAt(concatenedList.length() - 1);
        }

        return concatenedList.toString();
    }

    public void setListOfPossibleAccessions(String possibleAccessions){
        this.possibleAccessions.clear();

        if (possibleAccessions != null){
            if (possibleAccessions.contains(";")){
                String [] list = possibleAccessions.split(";");

                for (String s : list){
                    this.possibleAccessions.add(s);
                }
            }
            else {
                this.possibleAccessions.add(possibleAccessions);
            }
        }
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

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
    @Transient
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @return the status label of this action. Can be FAILED, TO_BE_REVIEWED or COMPLETED. However, if the status of this object
     * is null and/or its label is null, this method return NONE
     */
    @Column(name = "status", length = 15, nullable = false)
    public String getStatusLabel() {
        if (this.status == null){
            return StatusLabel.NONE.toString();
        }
        else {
            if (this.status.getLabel() == null){
                return StatusLabel.NONE.toString();
            }
            else {
                return this.status.getLabel().toString();
            }
        }
    }

    public void setStatusLabel(String label){
        StatusLabel l = null;

        if (label != null){
            if (label.equalsIgnoreCase("failed")){
                l = StatusLabel.FAILED;
            }
            else if (label.equalsIgnoreCase("completed")){
                l = StatusLabel.COMPLETED;
            }
            else if (label.equalsIgnoreCase("to_be_reviewed")){
                l = StatusLabel.TO_BE_REVIEWED;
            }
            else if (label.equalsIgnoreCase("none")){
                l =StatusLabel.NONE;
            }
            else {
                throw new IllegalArgumentException("The status label " + label + " is not valid and can only be either completed, failed, to_be_reviewed or none.");
            }
        }

        if (this.status == null){
            status = new Status(l, null);
        }
        else {
            status.setLabel(l);
        }
    }

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
    @Column(name = "description", nullable = true)
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
    @Basic
    public boolean isASwissprotEntry(){
        return this.isASwissprotEntry;
    }

    /**
     * set the isASwissprotEntry value
     * @param isSwissprot : boolean value
     */
    public void setASwissprotEntry(boolean isSwissprot){
        this.isASwissprotEntry = isSwissprot;
    }

    @ManyToOne
    @JoinColumn(name="result_id")
    public UpdateResults getUpdateResult() {
        return updateResult;
    }

    public void setUpdateResult(UpdateResults result) {
        this.updateResult = result;
    }
}
