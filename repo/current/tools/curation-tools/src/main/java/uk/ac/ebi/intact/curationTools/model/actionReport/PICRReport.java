package uk.ac.ebi.intact.curationTools.model.actionReport;

import org.hibernate.annotations.CollectionOfElements;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This report aims at storing the information and results of a query on PICR
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */
@Entity
@Table( name = "ia_picr_report" )
public class PICRReport extends ActionReport{

    /**
     * the list of cross references that PICR could collect
     */
    private Set<PICRCrossReferences> crossReferences = new HashSet<PICRCrossReferences>();

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
    @CollectionOfElements
    @JoinTable( name="ia_picr2xrefs", joinColumns = @JoinColumn(name="picr_id"))
    @org.hibernate.annotations.MapKey( columns = { @Column( name="xref_database" ) } )
    @Column( name="picr_xref_accessions", nullable = false)
    public Set<PICRCrossReferences> getCrossReferences(){
        return this.crossReferences;
    }

    /**
     * add a new cross reference
     * @param databaseName : database name
     * @param accession : accessions in the database
     */
    public void addCrossReference(String databaseName, String accession){
        boolean isADatabaseNamePresent = false;

        for (PICRCrossReferences c : this.crossReferences){
            if (c.getDatabase() != null){
                if (c.getDatabase().equalsIgnoreCase(databaseName)){
                    isADatabaseNamePresent = true;
                    c.addAccession(accession);
                }
            }
        }

        if (!isADatabaseNamePresent){
            PICRCrossReferences picrRefs = new PICRCrossReferences();
            picrRefs.setDatabase(databaseName);
            picrRefs.addAccession(accession);
        }
    }

    public void setCrossReferences(Set<PICRCrossReferences> crossReferences) {
        this.crossReferences = crossReferences;
    }
}
