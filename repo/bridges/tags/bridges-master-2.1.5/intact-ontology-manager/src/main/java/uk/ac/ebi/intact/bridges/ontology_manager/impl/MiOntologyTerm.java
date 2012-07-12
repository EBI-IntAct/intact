package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.util.Collection;
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

    private static final String REMAP = "REMAP TO";
    private static final String MAP = "MAP TO";
    private static final String REPLACE = "REPLACE BY";

    private static final String QUOTE = "&quot;";

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
        else if (otherInfoString.contains( HTTP_DEF )){
            String[] defArray = otherInfoString.split( HTTP_DEF );
            String def = null;

            if ( defArray.length == 2 ) {
                def = defArray[0];
                this.url = HTTP_DEF + defArray[1];

            } else if ( defArray.length > 2 ) {
                def = defArray[0];
                this.url = otherInfoString.substring(def.length());
            }

            if (this.definition == null){
                this.definition = otherInfoString;
            }
            else {
                this.definition += LINE_BREAK + def;
            }
        }
        // simple definition
        else {
            if (definition.startsWith(otherInfoString)){
                this.definition = otherInfoString;
            }
            else {
                this.definition += LINE_BREAK + otherInfoString;
            }
        }
    }

    @Override
    protected void processObsoleteMessage() {
        String upperObsoleteMessage = this.obsoleteMessage.toUpperCase();
        String remappingString = null;

        if (upperObsoleteMessage.contains(MAP)){
            remappingString = upperObsoleteMessage.substring(upperObsoleteMessage.indexOf(MAP) + MAP.length());
        }
        else if (upperObsoleteMessage.contains(REMAP)){
            remappingString = upperObsoleteMessage.substring(upperObsoleteMessage.indexOf(REMAP) + REMAP.length());
        }
        else if (upperObsoleteMessage.contains(REPLACE)){
            remappingString = upperObsoleteMessage.substring(upperObsoleteMessage.indexOf(REPLACE) + REPLACE.length());
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
            Matcher miMatcher = MI_REGEXP.matcher(upperObsoleteMessage);
            Matcher modMatcher = MOD_REGEXP.matcher(upperObsoleteMessage);

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

        if (synonyms != null){
            for (TermSynonym synonym : synonyms){
                Term synonymType = synonym.getSynonymType();
                //PSI-MOD-label for MOD
                if (synonymType != null){
                    if (SHORTLABEL_IDENTIFIER.equalsIgnoreCase(synonymType.getName())){
                        this.shortLabel = synonym.getSynonym().toLowerCase();
                    }
                    else if (ALIAS_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || EXACT_KEY.equalsIgnoreCase(synonymType.getName())){
                        this.aliases.add(synonym.getSynonym());
                    }
                }
            }
        }
    }

    @Override
    protected void processSynonym(String synonymName, String synonym) {

        if (synonymName.startsWith(SHORTLABEL_IDENTIFIER + META_DATA_SEPARATOR)){
            this.shortLabel = synonym.toLowerCase();
        }
        else if (synonymName.startsWith(EXACT_SYNONYM_KEY + META_DATA_SEPARATOR) || EXACT_SYNONYM_KEY.equalsIgnoreCase(synonymName)){
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

            String annotationText = accession.trim();

            if (annotationText.startsWith(QUOTE)){
                annotationText = annotationText.substring(QUOTE.length());
            }
            if (annotationText.endsWith(QUOTE)){
                annotationText = annotationText.substring(0, annotationText.indexOf(QUOTE));
            }

            TermAnnotation validation = new TermAnnotation(XREF_VALIDATION_REGEXP, XREF_VALIDATION_REGEXP_MI_REF, annotationText);  // MI xref
            this.annotations.add(validation);
        }
        // search url
        else if (db == null && accession.startsWith(SEARCH_URL)){
            String url = accession.substring(SEARCH_URL.length());

            if (url.startsWith("\\")){
                url = url.substring(1);
            }
            if (url.endsWith("\\")){
                url = url.substring(0, url.length() - 1);
            }

            TermAnnotation validation = new TermAnnotation(SEARCH_URL, SEARCH_URL_MI_REF, url);  // MI xref
            this.annotations.add(validation);
        }
        else if (db.equalsIgnoreCase(SEARCH_URL)){
            String url = accession.trim();

            TermAnnotation validation = new TermAnnotation(SEARCH_URL, SEARCH_URL_MI_REF, url);  // MI xref
            this.annotations.add(validation);
        }
        else if (db.startsWith(SEARCH_URL)){
            String prefix = db.substring(SEARCH_URL.length());
            String url = prefix + META_XREF_SEPARATOR + accession;

            if (url.startsWith("\"")){
                url = url.substring(1);
            }
            if (url.endsWith("\"")){
                url = url.substring(0, url.length() - 1);
            }

            TermAnnotation validation = new TermAnnotation(SEARCH_URL, SEARCH_URL_MI_REF, url);  // MI xref
            this.annotations.add(validation);
        }
    }

    @Override
    protected String processXrefDefinition(String xref, String database, String accession, String pubmedPrimary) {

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
        else if ( PMID_APPLICATION.equalsIgnoreCase(database) ) {
            TermDbXref pubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(pubmedRef); // MI not MOD
        } else if ( GO.equalsIgnoreCase(database) ) {
            TermDbXref goRef = new TermDbXref(GO, GO_MI_REF, database + ":" + accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(goRef); // MI not MOD
        } else if ( RESID.equalsIgnoreCase(database) ) {
            TermDbXref resXref = new TermDbXref(RESID, RESID_MI_REF, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(resXref);
        } else if ( SO.equalsIgnoreCase(database) ) {
            TermDbXref soRef = new TermDbXref(SO, SO_MI_REF, database + ":" + accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(soRef);  // MI not MOD
        }

        return pubmedPrimary;
    }
}
