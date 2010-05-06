package uk.ac.ebi.intact.curationTools.model.contexts;

import uk.ac.ebi.intact.model.BioSource;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */

public class UpdateContext extends IdentificationContext{

    private Set<String> identifiers = new HashSet<String>();
    private String intactAccession;

    public UpdateContext(){
        super();
        this.intactAccession = null;
    }

    public UpdateContext(IdentificationContext context){
        super(context);
        this.intactAccession = null;
    }

    public Set<String> getIdentifiers() {
        return identifiers;
    }

    public String getIntactAccession() {
        return intactAccession;
    }

    public void setIntactAccession(String intactAccession) {
        this.intactAccession = intactAccession;
    }

    public UpdateContext(String sequence, String identifier, BioSource organism, String gene_name, String protein_name) {
        super(sequence, identifier, organism, gene_name, protein_name);
        setIdentifier(null);
        this.identifiers.add(identifier);
        this.intactAccession = null;
    }

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
