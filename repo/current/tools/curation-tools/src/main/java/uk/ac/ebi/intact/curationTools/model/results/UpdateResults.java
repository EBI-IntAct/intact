package uk.ac.ebi.intact.curationTools.model.results;

import uk.ac.ebi.intact.curationTools.model.HibernatePersistent;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;

import javax.persistence.*;

/**
 * An UpdateResult contains all the results and ActionReports of the update process of a protein
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2010</pre>
 */
@Entity
@Table( name = "ia_update_result" )
public class UpdateResults extends IdentificationResults implements HibernatePersistent{

    /**
     * The unique id of this object
     */
    private Long idResult;

    /**
     * The intact accession of the protein to update
     */
    private String intactAccession;

    /**
     * Create a new UpdateResult instance
     */
    public UpdateResults(){
        super();
        this.intactAccession = null;
    }

    /**
     *
     * @return the unique id
     */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")
    @SequenceGenerator(name="SEQ_STORE", sequenceName="my_sequence" )
    public Long getId() {
        return idResult;
    }

    /**
     * Set an unique id
     * @param idResult
     */
    public void setId(Long idResult) {
        this.idResult = idResult;
    }

    /**
     *
     * @return  the intact accession
     */
    @Column(name = "intact_ac", nullable = false, length = 30)
    public String getIntactAccession() {
        return intactAccession;
    }

    /**
     * set the intact accession
     * @param intactAccession
     */
    public void setIntactAccession(String intactAccession) {
        this.intactAccession = intactAccession;
    }

    /**
     * Add an actionReport and set this object as parent
     * @param report : action report
     */
     public void addActionReport(ActionReport report){
         report.setUpdateResult(this);
        super.addActionReport(report);
    }
}
