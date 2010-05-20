package uk.ac.ebi.intact.curationTools.model.results;

import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */
@Entity
@Table(name = "ia_picr_xrefs")
public class PICRCrossReferences {

    private long idXrefs;

    private String database;

    private Set<String> accessions = new HashSet<String>();

    public PICRCrossReferences() {
        database = null;
    }

    @Column(name = "database", nullable = false)
    public String getDatabase() {
        return database;
    }

    @CollectionOfElements
    @JoinTable(name = "ia_xrefs2xrefs_accessions", joinColumns = @JoinColumn(name="picr_id"))
    @Column(name = "picr_xrefs_accession", nullable = false)
    // TODO change the annotation with @elementCollection when we will change the version of hibernate
    public Set<String> getAccessions() {
        return accessions;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setAccessions(Set<String> accessions) {
        this.accessions = accessions;
    }

    public void addAccession(String accession){
         this.accessions.add(accession);
    }

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQ_STORE")
    @SequenceGenerator(name="SEQ_STORE", sequenceName="my_sequence" )
    public long getIdXrefs() {
        return idXrefs;
    }

    public void setIdXrefs(long idXrefs) {
        this.idXrefs = idXrefs;
    }
}
