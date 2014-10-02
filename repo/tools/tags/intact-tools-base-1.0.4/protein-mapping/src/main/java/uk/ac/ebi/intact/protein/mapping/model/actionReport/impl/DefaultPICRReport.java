package uk.ac.ebi.intact.protein.mapping.model.actionReport.impl;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.PICRReport;
import uk.ac.ebi.intact.protein.mapping.results.PICRCrossReferences;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultPICRCrossReferences;

import java.util.HashSet;
import java.util.Set;

/**
 * This report aims at storing the information and results of a query on PICR
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */
public class DefaultPICRReport extends DefaultMappingReport implements PICRReport<DefaultPICRCrossReferences>{

    /**
     * the list of cross references that PICR could collect
     */
    private Set<DefaultPICRCrossReferences> crossReferences = new HashSet<DefaultPICRCrossReferences>();

    /**
     * Create a new DefaultPICRReport
     * @param name : name of the action
     */
    public DefaultPICRReport(ActionName name) {
        super(name);
    }

    /**
     *
     * @return the cross references
     */
    public Set<DefaultPICRCrossReferences> getCrossReferences(){
        return this.crossReferences;
    }

    /**
     * add a new cross reference
     * @param databaseName : database name
     * @param accession : accessions in the database
     */
    public void addCrossReference(String databaseName, String accession){
        boolean isADatabaseNamePresent = false;

        for (DefaultPICRCrossReferences c : this.crossReferences){
            if (c.getDatabase() != null){
                if (c.getDatabase().equalsIgnoreCase(databaseName)){
                    isADatabaseNamePresent = true;
                    c.addAccession(accession);
                }
            }
        }

        if (!isADatabaseNamePresent){
            DefaultPICRCrossReferences picrRefs = new DefaultPICRCrossReferences();
            picrRefs.setDatabase(databaseName);
            picrRefs.addAccession(accession);
        }
    }

    /**
     * Add a new PICRCrossReference instance to the list of references
     * @param refs : the PICRCrossReference instance to add
     */
    public void addPICRCrossReference(DefaultPICRCrossReferences refs){
         if (refs != null){
            this.crossReferences.add(refs);
         }
    }

    /**
     * Set the PICR cross references
     * @param crossReferences : set containing the PICR cross references
     */
    public void setCrossReferences(Set<DefaultPICRCrossReferences> crossReferences) {
        this.crossReferences = crossReferences;
    }

    @Override
    public boolean equals( Object o ) {
        return super.equals(o);
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        return super.hashCode();
    }

    @Override
    public boolean isIdenticalTo(Object o){

        if (!super.isIdenticalTo(o)){
            return false;
        }

        final DefaultPICRReport report = (DefaultPICRReport) o;

        return CollectionUtils.isEqualCollection(this.crossReferences, report.getCrossReferences());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(super.toString() + "\n");

        if (!crossReferences.isEmpty()){
            buffer.append("PICR references : [");

            for (PICRCrossReferences ref : crossReferences) {
                buffer.append(ref.toString() + " ; ");
            }

            buffer.append("\n");
        }

        return buffer.toString();
    }
}
