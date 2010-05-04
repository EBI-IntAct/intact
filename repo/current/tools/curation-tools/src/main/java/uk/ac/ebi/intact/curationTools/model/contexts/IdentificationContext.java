package uk.ac.ebi.intact.curationTools.model.contexts;

import uk.ac.ebi.intact.model.BioSource;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class IdentificationContext {

    private String sequence;
    private String identifier;
    private BioSource organism;
    private String gene_name;
    private String protein_name;
    private String globalName;

    public IdentificationContext(){
        this.sequence = null;
        this.identifier = null;
        this.organism = null;
        this.gene_name = null;
        this.protein_name = null;
        this.globalName = null;
    }

    public IdentificationContext(String sequence, String identifier, BioSource organism, String gene_name, String protein_name){
        this.sequence = sequence;
        this.identifier = identifier;
        this.organism = organism;
        this.gene_name = gene_name;
        this.protein_name = protein_name;
        this.globalName = null;
    }

    public IdentificationContext(IdentificationContext context){
        this.sequence = context.getSequence();
        this.identifier = context.getIdentifier();
        this.organism = context.getOrganism();
        this.gene_name = context.getGene_name();
        this.protein_name = context.getProtein_name();
        this.globalName = context.getGlobalName();
    }

    public IdentificationContext(String sequence, String identifier, BioSource organism, String name){
        this.sequence = sequence;
        this.identifier = identifier;
        this.organism = organism;
        this.globalName = name;
    }

    public void clean(){
        this.sequence = null;
        this.identifier = null;
        this.organism = null;
        this.gene_name = null;
        this.protein_name = null;
        this.globalName = null;
    }

    public String getSequence() {
        return sequence;
    }

    public String getIdentifier() {
        return identifier;
    }

    public BioSource getOrganism() {
        return organism;
    }

    public String getGene_name() {
        return gene_name;
    }

    public String getProtein_name() {
        return protein_name;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setOrganism(BioSource organism) {
        this.organism = organism;
    }

    public void setGene_name(String gene_name) {
        this.gene_name = gene_name;
    }

    public void setProtein_name(String protein_name) {
        this.protein_name = protein_name;
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }
    
}
