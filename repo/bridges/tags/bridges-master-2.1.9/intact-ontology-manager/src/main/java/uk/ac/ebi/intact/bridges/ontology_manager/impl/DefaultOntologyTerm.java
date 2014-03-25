package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.util.Collection;

/**
 * Default ontologyTerm
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/02/12</pre>
 */

public class DefaultOntologyTerm extends AbstractIntactOntologyTerm{
    public DefaultOntologyTerm(String acc) {
        super(acc);
    }

    public DefaultOntologyTerm(String acc, String name) {
        super(acc, name);
    }

    @Override
    protected void processXref(String db, String accession) {
        // nothing to do
    }

    @Override
    protected String processXrefDefinition(String xref, String database, String accession, String pubmedPrimary) {
        if ( PUBMED.equalsIgnoreCase(database) ) {
            if (pubmedPrimary == null){
                pubmedPrimary = xref;

                TermDbXref primaryPubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, accession, PRIMARY_REFERENCE, PRIMARY_REFERENCE_MI_REF);
                this.dbXrefs.add(primaryPubmedRef);
            }
            else {
                TermDbXref pubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, accession, METHOD_REFERENCE, METHOD_REFERENCE_MI_REF);
                this.dbXrefs.add(pubmedRef);
            }
        }
        else {
            TermDbXref resXref = new TermDbXref(database, null, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(resXref);
        }

        return pubmedPrimary;
    }

    @Override
    protected void processSynonyms(Term term) {
        Collection<TermSynonym> synonyms = term.getSynonyms();

        this.shortLabel = term.getName();

        if (synonyms != null){
            for (TermSynonym synonym : synonyms){
                Term synonymType = synonym.getSynonymType();
                if (synonymType != null){
                    this.aliases.add(synonym.getSynonym());
                }
            }
        }
    }

    @Override
    protected void processSynonym(String synonymName, String synonym) {
        this.aliases.add(synonym);
    }

    @Override
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
        // nothing to do
    }
}
