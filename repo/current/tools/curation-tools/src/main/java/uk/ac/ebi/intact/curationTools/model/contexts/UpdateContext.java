package uk.ac.ebi.intact.curationTools.model.contexts;

import uk.ac.ebi.intact.model.BioSource;

import java.util.HashSet;
import java.util.Set;

/**
 * This specific context is used for the update of the Intact proteins without any uniprot cross references.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */

public class UpdateContext extends IdentificationContext{

    /**
     * list of identifiers for the Intact protein
     */
    private Set<String> identifiers = new HashSet<String>();

    /**
     * the Intact accession of the protein
     */
    private String intactAccession;

    /**
     * create a new update context
     */
    public UpdateContext(){
        super();
        this.intactAccession = null;
    }

    /**
     * Create a new Context from a previous one
     * @param context : previous context
     */
    public UpdateContext(IdentificationContext context){
        super(context);
        this.intactAccession = null;
    }

    /**
     *
     * @return the list of identifiers
     */
    public Set<String> getIdentifiers() {
        return identifiers;
    }

    /**
     *
     * @return  the intact accession
     */
    public String getIntactAccession() {
        return intactAccession;
    }

    /**
     * set the Intact accession
     * @param intactAccession
     */
    public void setIntactAccession(String intactAccession) {
        this.intactAccession = intactAccession;
    }

    /**
     * Create a new update context with sequence, identifier, organism, gene name, protein name
     * @param sequence
     * @param identifier
     * @param organism
     * @param gene_name
     * @param protein_name
     */
    public UpdateContext(String sequence, String identifier, BioSource organism, String gene_name, String protein_name) {
        super(sequence, identifier, organism, gene_name, protein_name);
        setIdentifier(null);
        this.identifiers.add(identifier);
        this.intactAccession = null;
    }

    /**
     * Create an UpdateContext with a seuqence, identifier, organism and name
     * @param sequence
     * @param identifier
     * @param organism
     * @param name
     */
    public UpdateContext(String sequence, String identifier, BioSource organism, String name) {
        super(sequence, identifier, organism, name);
        setIdentifier(null);
        this.identifiers.add(identifier);
        this.intactAccession = null;
    }

    @Override
    public void clean() {
        super.clean();
        this.identifiers.clear();
    }

    @Override
    public String getIdentifier() {
        if (this.identifiers.isEmpty()){
            return null;
        }
        return this.identifiers.iterator().next();
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifiers.clear();
        this.identifiers.add(identifier);
    }

    public void addIdentifier(String identifier){
        this.identifiers.add(identifier);
    }
}
