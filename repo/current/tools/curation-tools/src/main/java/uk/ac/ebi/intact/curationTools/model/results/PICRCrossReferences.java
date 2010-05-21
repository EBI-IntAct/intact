package uk.ac.ebi.intact.curationTools.model.results;

import uk.ac.ebi.intact.curationTools.model.HibernatePersistent;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;

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
public class PICRCrossReferences implements HibernatePersistent{

    private long idXrefs;

    private String database;

    private Set<String> accessions = new HashSet<String>();

    private PICRReport picrReport;

    public PICRCrossReferences() {
        database = null;
    }

    @Column(name = "database", nullable = false)
    public String getDatabase() {
        return database;
    }

    @Transient
    public Set<String> getAccessions() {
        return accessions;
    }

    @Column(name = "accessions", nullable = false, length = 500)
    public String getListOfAccessions(){

        if (this.accessions.isEmpty()){
            return null;
        }
        StringBuffer concatenedList = new StringBuffer( 1064 );

        for (String ref : this.accessions){
            concatenedList.append(ref+";");
        }

        if (concatenedList.length() > 0){
            concatenedList.deleteCharAt(concatenedList.length() - 1);
        }

        return concatenedList.toString();
    }

    public void setListOfAccessions(String possibleAccessions){
        this.accessions.clear();

        if (possibleAccessions != null){
            if (possibleAccessions.contains(";")){
                String [] list = possibleAccessions.split(";");

                for (String s : list){
                    this.accessions.add(s);
                }
            }
            else {
                this.accessions.add(possibleAccessions);
            }
        }
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
    public Long getId() {
        return idXrefs;
    }

    public void setId(Long idXrefs) {
        this.idXrefs = idXrefs;
    }

    @ManyToOne
    @JoinColumn(name="picr_report_id")
    public PICRReport getPicrReport() {
        return picrReport;
    }

    public void setPicrReport(PICRReport picrReport) {
        this.picrReport = picrReport;
    }
}
