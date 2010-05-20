package uk.ac.ebi.intact.curationTools.model.results;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
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
public class BlastResults extends BlastProtein {

    private Long idBlast;

    private int taxId;

    public BlastResults() {
        taxId = 0;
    }

    public BlastResults(BlastProtein protein) {
        if( protein == null ) {
            throw new IllegalArgumentException( "You must give a non null protein" );
        }
        setVariablesFrom(protein);
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")
    @SequenceGenerator(name="SEQ_STORE", sequenceName="my_sequence" )    
    public Long getIdBlast() {
        return idBlast;
    }

    public void setIdBlast(Long idBlast) {
        this.idBlast = idBlast;
    }

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

    @Column(name = "taxId")
    public int getTaxId() {
        return taxId;
    }

    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    @Column(name = "protein_ac", nullable = false)
    public String getAccession() {
        return super.getAccession();
    }

    @Column(name = "start_query", nullable = false)
    public int getStartQuery() {
        return super.getStartQuery();
    }

    @Column(name = "end_query", nullable = false)
    public int getEndQuery() {
        return super.getEndQuery();
    }

    public void setStartQuery(int startQuery) {
        super.setStartQuery(startQuery);
    }

    public void setEndQuery(int endQuery) {
        super.setEndQuery(endQuery);
    }

    @Transient
    public UniprotProtein getUniprotProtein() {
        return super.getUniprotProtein();
    }

    public void setUniprotProtein(UniprotProtein prot) {
        super.setUniprotProtein(prot);
    }

    @Transient
    public String getSequence() {
        return super.getSequence();
    }

    @Column(name = "database", nullable = false)
    public String getDatabase() {
        return super.getDatabase();
    }

    @Column(name = "identity", nullable = false)
    public float getIdentity() {
        return super.getIdentity();
    }

    @Column(name = "start_match", nullable = false)
    public int getStartMatch() {
        return super.getStartMatch();
    }

    @Column(name = "start_end", nullable = false)
    public int getEndMatch() {
        return super.getEndMatch();
    }

    public void setAccession(String accession) {
        super.setAccession(accession);
    }

    public void setSequence(String sequence) {
        super.setSequence(sequence);
    }

    public void setIdentity(float identity) {
        super.setIdentity(identity);
    }

    public void setDatabase(String database) {
        super.setDatabase(database);
    }

    public void setStartMatch(int startMatch) {
        super.setStartMatch(startMatch);
    }

    public void setEndMatch(int endMatch) {
        super.setEndMatch(endMatch);
    }

    public String getDescription() {
        return super.getDescription();
    }

    @Column(name = "protein_description")
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Transient
    public String getAlignment() {
        return super.getAlignment();
    }

    public void setAlignment(String alignment) {
        super.setAlignment(alignment);
    }
}
