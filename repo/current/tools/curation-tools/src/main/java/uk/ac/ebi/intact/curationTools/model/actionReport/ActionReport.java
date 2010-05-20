package uk.ac.ebi.intact.curationTools.model.actionReport;

import org.hibernate.annotations.CollectionOfElements;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;

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
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table( name = "ia_protein_action" )
public class ActionReport {

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
    public Long getIdAction() {
        return idAction;
    }

    public void setIdAction(Long idAction) {
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
    @CollectionOfElements
    @JoinTable(name = "ia_action2possUniprot", joinColumns = @JoinColumn(name="action_id"))
    @Column(name = "possible_uniprot", nullable = false)
    public Set<String> getPossibleAccessions(){
        return this.possibleAccessions;
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

    public void setStatusLabel(StatusLabel label){
        if (this.status == null){
            status = new Status(label, null);
        }
        else {
            status.setLabel(label);
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
    @Column(name = "description", nullable = false)
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
}
