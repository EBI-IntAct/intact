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

    public UpdateContext(){
        super();
    }

    public Set<String> getIdentifiers() {
        return identifiers;
    }

    public UpdateContext(String sequence, String identifier, BioSource organism, String gene_name, String protein_name) {
        super(sequence, identifier, organism, gene_name, protein_name);
        setIdentifier(null);
        this.identifiers.add(identifier);
    }

    public UpdateContext(String sequence, String identifier, BioSource organism, String name) {
        super(sequence, identifier, organism, name);
        setIdentifier(null);
        this.identifiers.add(identifier);
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
