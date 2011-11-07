package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Ontology term for PSI-MI
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class MiOntologyTerm extends AbstractIntactOntologyTerm {
    private static final String SHORTLABEL_IDENTIFIER = "Unique short label curated by PSI-MI";
    private static final String ALIAS_IDENTIFIER = "Alternate label curated by PSI-MI";

    private static final String PMID_APPLICATION = "PMID for application instance";

    private static final String GO = "go";
    private static final String GO_MI_REF = "MI:0448";

    private static final String SO = "so";
    private static final String SO_MI_REF = "MI:0601";

    private static final String HTTP_DEF = "http";

    private static final String XREF_VALIDATION_REGEXP = "id-validation-regexp";
    private static final String XREF_VALIDATION_REGEXP_MI_REF = "MI:0628";

    private static final String SEARCH_URL = "search-url";
    private static final String SEARCH_URL_MI_REF = "MI:0615";

    private static final String REMAP = "remap to";
    private static final String MAP = "map to";
    private static final String REPLACE = "replace by";

    public MiOntologyTerm(String acc) {
        super(acc);
    }

    public MiOntologyTerm(String acc, String name) {
        super(acc, name);
        this.fullName = name;
    }

    protected void processOtherInfoInDescription(String definition, String otherInfoString) {

        // URL
        if ( otherInfoString.startsWith( HTTP_DEF ) ) {

            this.url = otherInfoString;

        }
        // simple definition
        else {
            if (definition.startsWith(otherInfoString)){
                this.definition += otherInfoString;
            }
            else {
                this.definition += LINE_BREAK + otherInfoString;
            }
        }
    }

    @Override
    protected void processObsoleteMessage() {
        String lowerObsoleteMessage = this.obsoleteMessage.toLowerCase();
        String remappingString = null;

        if (lowerObsoleteMessage.contains(MAP)){
            remappingString = lowerObsoleteMessage.substring(lowerObsoleteMessage.indexOf(MAP) + MAP.length());
        }
        else if (lowerObsoleteMessage.contains(REMAP)){
            remappingString = lowerObsoleteMessage.substring(lowerObsoleteMessage.indexOf(REMAP) + REMAP.length());
        }
        else if (lowerObsoleteMessage.contains(REPLACE)){
            remappingString = lowerObsoleteMessage.substring(lowerObsoleteMessage.indexOf(REPLACE) + REPLACE.length());
        }

        if (remappingString != null){
            Matcher miMatcher = MI_REGEXP.matcher(remappingString);
            Matcher modMatcher = MOD_REGEXP.matcher(remappingString);

            while (miMatcher.find()){
                this.possibleTermsToRemapTo.add(miMatcher.group());
            }

            while (modMatcher.find()){
                this.possibleTermsToRemapTo.add(modMatcher.group());
            }

            if (this.possibleTermsToRemapTo.size() == 1){
                this.remappedTerm = this.possibleTermsToRemapTo.iterator().next();

                // we do not need the remapped term to be kept twice
                this.possibleTermsToRemapTo.clear();
            }
        }
        else {
            Matcher miMatcher = MI_REGEXP.matcher(lowerObsoleteMessage);
            Matcher modMatcher = MOD_REGEXP.matcher(lowerObsoleteMessage);

            while (miMatcher.find()){
                this.possibleTermsToRemapTo.add(miMatcher.group());
            }

            while (modMatcher.find()){
                this.possibleTermsToRemapTo.add(modMatcher.group());
            }
        }
    }

    protected void processSynonyms(Term term) {
        Collection<TermSynonym> synonyms = term.getSynonyms();

        for (TermSynonym synonym : synonyms){
            Term synonymType = synonym.getSynonymType();
            //PSI-MOD-label for MOD
            if (synonymType != null){
                if (SHORTLABEL_IDENTIFIER.equalsIgnoreCase(synonymType.getName())){
                    this.shortLabel = synonym.getSynonym();
                }
                else if (ALIAS_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                        || EXACT_KEY.equalsIgnoreCase(synonymType.getName())){
                    this.aliases.add(synonym.getSynonym());
                }
            }
        }
    }

    @Override
    protected void processSynonym(String synonymName, String synonym) {

        if (synonymName.startsWith(EXACT_SYNONYM_KEY + META_DATA_SEPARATOR) || EXACT_SYNONYM_KEY.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(ALIAS_IDENTIFIER + META_DATA_SEPARATOR) || ALIAS_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
    }

    @Override
    protected void processXref(String db, String accession) {
        // xref validation regexp
        if (XREF_VALIDATION_REGEXP.equalsIgnoreCase(db)){

            TermAnnotation validation = new TermAnnotation(XREF_VALIDATION_REGEXP, XREF_VALIDATION_REGEXP_MI_REF, accession.trim());  // MI xref
            this.annotations.add(validation);
        }
        // search url
        else if (SEARCH_URL.equalsIgnoreCase(db)){
            TermAnnotation validation = new TermAnnotation(SEARCH_URL, SEARCH_URL_MI_REF, accession);  // MI xref
            this.annotations.add(validation);
        }
    }

    @Override
    protected void processXrefDefinition(String xref, String database, String accession, Set<String> resIdRefs, String pubmedPrimary) {

        if ( PMID.equalsIgnoreCase(database) ) {
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
        else if ( PUBMED.equalsIgnoreCase(database) ) {
            TermDbXref pubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, accession, METHOD_REFERENCE, METHOD_REFERENCE_MI_REF);
            this.dbXrefs.add(pubmedRef);
        }
        else if ( PMID_APPLICATION.equalsIgnoreCase(database) ) {
            TermDbXref pubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(pubmedRef); // MI not MOD
        } else if ( GO.equalsIgnoreCase(database) ) {
            TermDbXref goRef = new TermDbXref(GO, GO_MI_REF, database + ":" + accession, IDENTITY, IDENTITY_MI_REF);
            this.dbXrefs.add(goRef); // MI not MOD
        } else if ( RESID.equalsIgnoreCase(database) ) {
            resIdRefs.add(xref);
        } else if ( SO.equalsIgnoreCase(database) ) {
            TermDbXref soRef = new TermDbXref(SO, SO_MI_REF, database + ":" + accession, IDENTITY, IDENTITY_MI_REF);
            this.dbXrefs.add(soRef);  // MI not MOD
        }
    }
}
