package uk.ac.ebi.intact.curationTools.model.results;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.curationTools.model.HibernatePersistent;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

import javax.persistence.*;

/**
 * The annotated class containing the basic Blast results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-May-2010</pre>
 */
@Entity
@Table(name = "ia_blast_results")
public class BlastResults extends BlastProtein implements HibernatePersistent{

    /**
     * The unique id for this object
     */
    private Long idBlast;

    /**
     * the taxId of the protein identified in these blast results
     */
    private int taxId;

    /**
     * The trembl accession of the protein we tried to remap using the swissprot remapping process. Can be null if the blast has directly be
     * done on a anonymous sequence.
     */
    private String tremblAccession;

    /**
     * The parent of this object.
     */
    private BlastReport blastReport;

    /**
     * Create a new BlastResults instance
     */
    public BlastResults() {
        taxId = 0;
        this.tremblAccession = null;
    }

    /**
     * Create a new BlastResults instance from a previous BlastProtein instance
     * @param protein : the Blastprotein instance
     */
    public BlastResults(BlastProtein protein) {
        if( protein == null ) {
            throw new IllegalArgumentException( "You must give a non null protein" );
        }
        setVariablesFrom(protein);
        this.tremblAccession = null;
    }

    /**
     *
     * @return the unique id
     */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")
    @SequenceGenerator(name="SEQ_STORE", sequenceName="my_sequence" )    
    public Long getId() {
        return idBlast;
    }

    /**
     *
     * @return The trembl accession we want to remap using the swissprto remapping process, null if it is a blast from anonymous sequence
     */
    @Column(name = "trembl_accession_remapping", nullable = true, length = 20)
    public String getTremblAccession() {
        return tremblAccession;
    }

    /**
     * Set the trembl accession
     * @param tremblAccession
     */
    public void setTremblAccession(String tremblAccession) {
        this.tremblAccession = tremblAccession;
    }

    /**
     * Set the unique id
     * @param idBlast
     */
    public void setId(Long idBlast) {
        this.idBlast = idBlast;
    }

    /**
     * Initialises the variables of this object from the variables of a Blastprotein instance
     * @param protein
     */
    private void setVariablesFrom(BlastProtein protein){
        setAccession(protein.getAccession());
        setDatabase(protein.getDatabase());
        setEndMatch(protein.getEndMatch());
        setEndQuery(protein.getEndQuery());
        setStartMatch(protein.getStartMatch());
        setStartQuery(protein.getStartQuery());
        setIdentity(protein.getIdentity());
        setDescription(protein.getDescription());
        setSequence(protein.getSequence());
        setAlignment(protein.getAlignment());
        setUniprotProtein(protein.getUniprotProtein());

        if (protein.getUniprotProtein() != null){
            UniprotProtein p = protein.getUniprotProtein();

            if (p.getOrganism() != null){
                setTaxId(p.getOrganism().getTaxid());
            }
            else {
                taxId = 0;
            }
        }
        else {
            taxId = 0;
        }
    }

    /**
     *
     * @return The taxId
     */
    @Column(name = "taxId")
    public int getTaxId() {
        return taxId;
    }

    /**
     * Set the taxId
     * @param taxId
     */
    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    /**
     *
     * @return the accession of the protein
     */
    @Column(name = "protein_ac", nullable = false)
    public String getAccession() {
        return super.getAccession();
    }

    /**
     *
     * @return the start position of the alignment in the query
     */
    @Column(name = "start_query", nullable = false)
    public int getStartQuery() {
        return super.getStartQuery();
    }

    /**
     *
     * @return the end position of the alignment in the query
     */
    @Column(name = "end_query", nullable = false)
    public int getEndQuery() {
        return super.getEndQuery();
    }

    /**
     * Set the start position of the alignment in the query
     * @param startQuery
     */
    public void setStartQuery(int startQuery) {
        super.setStartQuery(startQuery);
    }

    /**
     * Set the end position of the alignment in the query
     * @param endQuery
     */
    public void setEndQuery(int endQuery) {
        super.setEndQuery(endQuery);
    }

    /**
     *
     * @return The uniprot protein
     */
    @Transient
    public UniprotProtein getUniprotProtein() {
        return super.getUniprotProtein();
    }

    /**
     * Set the uniprot protein
     * @param prot
     */
    public void setUniprotProtein(UniprotProtein prot) {
        super.setUniprotProtein(prot);
    }

    /**
     *
     * @return The sequence
     */
    @Lob
    @Column(name = "match_sequence")
    public String getSequence() {
        return super.getSequence();
    }

    /**
     *
     * @return  the database name
     */
    @Column(name = "database", nullable = false)
    public String getDatabase() {
        return super.getDatabase();
    }

    /**
     *
     * @return the identity
     */
    @Column(name = "identity", nullable = false)
    public float getIdentity() {
        return super.getIdentity();
    }

    /**
     *
     * @return the start match
     */
    @Column(name = "start_match", nullable = false)
    public int getStartMatch() {
        return super.getStartMatch();
    }

    /**
     *
     * @return  the end match
     */
    @Column(name = "end_match", nullable = false)
    public int getEndMatch() {
        return super.getEndMatch();
    }

    /**
     * Set the accession
     * @param accession
     */
    public void setAccession(String accession) {
        super.setAccession(accession);
    }

    /**
     * Set the sequence
     * @param sequence
     */
    public void setSequence(String sequence) {
        super.setSequence(sequence);
    }

    /**
     * Set the identity
     * @param identity
     */
    public void setIdentity(float identity) {
        super.setIdentity(identity);
    }

    /**
     * Set the database
     * @param database
     */
    public void setDatabase(String database) {
        super.setDatabase(database);
    }

    /**
     * Set the start match
     * @param startMatch
     */
    public void setStartMatch(int startMatch) {
        super.setStartMatch(startMatch);
    }

    /**
     * Set the end match
     * @param endMatch
     */
    public void setEndMatch(int endMatch) {
        super.setEndMatch(endMatch);
    }

    /**
     *
     * @return  the description
     */
    public String getDescription() {
        return super.getDescription();
    }

    /**
     * Set the description
     * @param description
     */
    @Column(name = "protein_description")
    public void setDescription(String description) {
        super.setDescription(description);
    }

    /**
     *
     * @return the alignment
     */
    @Lob
    @Column(name = "alignment")
    public String getAlignment() {
        return super.getAlignment();
    }

    /**
     * Set the alignment
     * @param alignment
     */
    public void setAlignment(String alignment) {
        super.setAlignment(alignment);
    }

    /**
     *
     * @return the parent report
     */
    @ManyToOne
    @JoinColumn(name = "blast_report_id")
    public BlastReport getBlastReport() {
        return blastReport;
    }

    /**
     * Set the parent report
     * @param blastReport
     */
    public void setBlastReport(BlastReport blastReport) {
        this.blastReport = blastReport;
    }
}
