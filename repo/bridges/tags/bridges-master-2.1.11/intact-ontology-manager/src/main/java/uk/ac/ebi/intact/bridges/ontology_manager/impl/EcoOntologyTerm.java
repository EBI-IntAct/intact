package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.util.Collection;

/**
 * Ontology term for ECO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class EcoOntologyTerm extends AbstractIntactOntologyTerm{
    private static final String RELATED = "RELATED";
    private static final String BROAD = "BROAD";
    private static final String NARROW = "NARROW";

    public EcoOntologyTerm(String acc, String name) {
        super(acc, name);
        this.fullName = name;
    }

    public EcoOntologyTerm(String acc) {
        super(acc);
    }

    @Override
    protected void processXref(String db, String accession) {
        // nothing to do
    }

    @Override
    protected String processXrefDefinition(String xref, String database, String accession, String pubmedPrimary) {
        if ( URL.equalsIgnoreCase(database) ) {
            this.url = accession;
        }

        return pubmedPrimary;
    }

    @Override
    protected void processSynonyms(Term term) {
        Collection<TermSynonym> synonyms = term.getSynonyms();

        if (synonyms != null){
            for (TermSynonym synonym : synonyms){
                Term synonymType = synonym.getSynonymType();
                //PSI-MOD-label for MOD
                if (synonymType != null){
                    if (BROAD.equalsIgnoreCase(synonymType.getName())
                            || RELATED.equalsIgnoreCase(synonymType.getName())
                            || NARROW.equalsIgnoreCase(synonymType.getName())
                            || EXACT_KEY.equalsIgnoreCase(synonymType.getName())){
                        this.aliases.add(synonym.getSynonym());
                    }
                }
            }
        }
    }

    @Override
    protected void processSynonym(String synonymName, String synonym) {
        if (synonymName.startsWith(EXACT_SYNONYM_KEY + META_DATA_SEPARATOR) || EXACT_SYNONYM_KEY.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(BROAD + META_DATA_SEPARATOR) || BROAD.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(RELATED + META_DATA_SEPARATOR) || RELATED.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(NARROW + META_DATA_SEPARATOR) || NARROW.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
    }

    protected void processOtherInfoInDescription(String definition, String otherInfoString) {

        // simple definition
        if (definition.startsWith(otherInfoString)){
            this.definition = otherInfoString;
        }
        else {
            this.definition += LINE_BREAK + otherInfoString;
        }
    }

    @Override
    protected void processObsoleteMessage() {
        // nothing to do here
    }
}
