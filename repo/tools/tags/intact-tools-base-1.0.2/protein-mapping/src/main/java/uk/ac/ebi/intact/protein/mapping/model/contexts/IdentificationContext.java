package uk.ac.ebi.intact.protein.mapping.model.contexts;

import uk.ac.ebi.intact.model.BioSource;

import javax.persistence.MappedSuperclass;

/**
 * An Identification context is the context of the protein to identify with the information about the protein
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */
@MappedSuperclass
public class IdentificationContext {

    /**
     * the sequence of the protein
     */
    private String sequence;

    /**
     * the identifier of the protein
     */
    private String identifier;

    /**
     * the database mi number or name of the identifier
     */
    private String databaseForIdentifier;

    /**
     * the organism of the protein
     */
    private BioSource organism;

    /**
     * the gene name o the protein
     */
    private String gene_name;

    /**
     * the protein name
     */
    private String protein_name;

    /**
     * a name for the protein
     */
    private String globalName;

    /**
     * Create a new IdentificationContext
     */
    public IdentificationContext(){
        this.sequence = null;
        this.identifier = null;
        this.organism = null;
        this.gene_name = null;
        this.protein_name = null;
        this.globalName = null;
        this.databaseForIdentifier = null;
    }

    /**
     * Create a new identification context with sequence, identifier and its database MI number, organism, gene name and protein name
     * @param sequence
     * @param identifier
     * @param organism
     * @param gene_name
     * @param protein_name
     */
    public IdentificationContext(String sequence, String identifier, String databaseMINumber, BioSource organism, String gene_name, String protein_name){
        this.sequence = sequence;
        this.identifier = identifier;
        this.organism = organism;
        this.gene_name = gene_name;
        this.protein_name = protein_name;
        this.globalName = null;
        this.databaseForIdentifier = databaseMINumber;
    }

    /**
     * Create a new context from a previous one
     * @param context : the previous context
     */
    public IdentificationContext(IdentificationContext context){
        this.sequence = context.getSequence();
        this.identifier = context.getIdentifier();
        this.organism = context.getOrganism();
        this.gene_name = context.getGene_name();
        this.protein_name = context.getProtein_name();
        this.globalName = context.getGlobalName();
        this.databaseForIdentifier = context.getDatabaseForIdentifier();
    }

    /**
     * Create a new IdentificationContext with sequence, organism and name
     * @param sequence
     * @param identifier
     * @param organism
     * @param name
     */
    public IdentificationContext(String sequence, String identifier, String databaseMi, BioSource organism, String name){
        this.sequence = sequence;
        this.identifier = identifier;
        this.organism = organism;
        this.globalName = name;
        this.databaseForIdentifier = databaseMi;
    }

    /**
     *
     * @return the database MI number or name for the identifier of the protein
     */
    public String getDatabaseForIdentifier() {
        return databaseForIdentifier;
    }

    /**
     * set the database MI number
     * @param databaseForIdentifier : the database MI number
     */
    public void setDatabaseForIdentifier(String databaseForIdentifier) {
        this.databaseForIdentifier = databaseForIdentifier;
    }

    /**
     * clean the current object
     */
    public void clean(){
        this.sequence = null;
        this.identifier = null;
        this.organism = null;
        this.gene_name = null;
        this.protein_name = null;
        this.globalName = null;
        this.databaseForIdentifier = null;
    }

    /**
     *
     * @return the sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     *
     * @return  the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return the organism
     */
    public BioSource getOrganism() {
        return organism;
    }

    /**
     *
     * @return the gene name
     */
    public String getGene_name() {
        return gene_name;
    }

    /**
     *
     * @return the protein name
     */
    public String getProtein_name() {
        return protein_name;
    }

    /**
     * set the sequence
     * @param sequence
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * set the identifier
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * set the organism
     * @param organism
     */
    public void setOrganism(BioSource organism) {
        this.organism = organism;
    }

    /**
     * set the gene name
     * @param gene_name
     */
    public void setGene_name(String gene_name) {
        this.gene_name = gene_name;
    }

    /**
     * set the protein name
     * @param protein_name
     */
    public void setProtein_name(String protein_name) {
        this.protein_name = protein_name;
    }

    /**
     *
     * @return the general name
     */
    public String getGlobalName() {
        return globalName;
    }

    /**
     * set the general name of the protein
     * @param globalName
     */
    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }
    
}
