package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.util.Collection;
import java.util.regex.Matcher;

/**
 * Ontology term for PSI-MOD
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class ModOntologyTerm extends AbstractIntactOntologyTerm{
    private static final String SHORTLABEL_IDENTIFIER = "Short label curated by PSI-MOD"; //
    private static final String DELTAMASS_ALIAS_IDENTIFIER = "Label from MS DeltaMass";  //
    private static final String MOD_ALIAS_IDENTIFIER = "Alternate name curated by PSI-MOD"; //
    private static final String OMSSA_IDENTIFIER = "Short label from OMSSA"; //
    private static final String MS_IDENTIFIER = "Agreed label from MS community"; //
    private static final String RESID_IDENTIFIER = "Alternate name from RESID"; //
    private static final String RESID_MISNOMER_IDENTIFIER = "Misnomer tagged alternate name from RESID";
    private static final String RESID_NAME_IDENTIFIER = "Name from RESID"; //
    private static final String RESID_SYSTEMATIC_IDENTIFIER = "Systematic name from RESID";   //
    private static final String UNIMOD_IDENTIFIER = "Alternate name from UniMod"; //
    private static final String UNIMOD_DESCRIPTION_IDENTIFIER = "Description (full_name) from UniMod"; //
    private static final String UNIMOD_INTERIM_IDENTIFIER = "Interim label from UniMod";
    private static final String UNIPROT_FEATURE_IDENTIFIER = "Protein feature description from UniProtKB";

    private static final String UNIMOD = "unimod";
    private static final String UNIMOD_MI_REF = "MI:1015";

    private static final String DELTAMASS = "deltamass";
    private static final String DELTAMASS_MI_REF = "MI:1014";

    private static final String CHEBI = "chebi";
    private static final String CHEBI_MI_REF = "MI:0474";

    private static final String REMAP = "REMAP TO";
    private static final String MAP = "MAP TO";

    public ModOntologyTerm(String acc, String name) {
        super(acc, name);
        this.fullName = name;
    }

    public ModOntologyTerm(String acc) {
        super(acc);
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
        else if ( RESID.equalsIgnoreCase(database) ) {
            TermDbXref resXref = new TermDbXref(RESID, RESID_MI_REF, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(resXref);
        } /*else if ( UNIMOD.equalsIgnoreCase(database) ) {
            TermDbXref unimodRef = new TermDbXref(UNIMOD, UNIMOD_MI_REF, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(unimodRef); // MOD xref
        } else if ( DELTAMASS.equalsIgnoreCase(database) ) {
            TermDbXref deltaMassRef = new TermDbXref(DELTAMASS, DELTAMASS_MI_REF, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(deltaMassRef);  // MOD xref
        } */else if ( CHEBI.equalsIgnoreCase(database) ) {
            TermDbXref chebiRef = new TermDbXref(CHEBI, CHEBI_MI_REF, accession, SEE_ALSO, SEE_ALSO_MI_REF);
            this.dbXrefs.add(chebiRef);  // MOD xref
        } else if ( URL.equalsIgnoreCase(database) ) {
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
                    if (SHORTLABEL_IDENTIFIER.equalsIgnoreCase(synonymType.getName())){
                        this.shortLabel = synonym.getSynonym().toLowerCase();
                    }
                    else if (MOD_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_MISNOMER_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_NAME_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_SYSTEMATIC_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || UNIPROT_FEATURE_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || EXACT_KEY.equalsIgnoreCase(synonymType.getName())){
                        this.aliases.add(synonym.getSynonym());
                    }
                    /*else if (DELTAMASS_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || MOD_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || OMSSA_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || MS_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_MISNOMER_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_NAME_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || RESID_SYSTEMATIC_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || UNIMOD_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || UNIMOD_DESCRIPTION_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || UNIMOD_INTERIM_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || UNIPROT_FEATURE_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
                            || EXACT_KEY.equalsIgnoreCase(synonymType.getName())){
                        this.aliases.add(synonym.getSynonym());
                    }*/
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
        /*else if (synonymName.startsWith(DELTAMASS_ALIAS_IDENTIFIER + META_DATA_SEPARATOR) || DELTAMASS_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }*/
        else if (synonymName.startsWith(MOD_ALIAS_IDENTIFIER + META_DATA_SEPARATOR) || MOD_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        /*else if (synonymName.startsWith(OMSSA_IDENTIFIER + META_DATA_SEPARATOR) || OMSSA_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(MS_IDENTIFIER + META_DATA_SEPARATOR) || MS_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }*/
        else if (synonymName.startsWith(RESID_IDENTIFIER + META_DATA_SEPARATOR) || RESID_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(RESID_MISNOMER_IDENTIFIER + META_DATA_SEPARATOR) || RESID_MISNOMER_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(RESID_NAME_IDENTIFIER + META_DATA_SEPARATOR) || RESID_NAME_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(RESID_SYSTEMATIC_IDENTIFIER + META_DATA_SEPARATOR) || RESID_SYSTEMATIC_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        /*else if (synonymName.startsWith(UNIMOD_IDENTIFIER + META_DATA_SEPARATOR) || UNIMOD_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(UNIMOD_DESCRIPTION_IDENTIFIER + META_DATA_SEPARATOR) || UNIMOD_DESCRIPTION_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(UNIMOD_INTERIM_IDENTIFIER + META_DATA_SEPARATOR) || UNIMOD_INTERIM_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }*/
        else if (synonymName.startsWith(UNIPROT_FEATURE_IDENTIFIER + META_DATA_SEPARATOR) || UNIPROT_FEATURE_IDENTIFIER.equalsIgnoreCase(synonymName)){
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
        String upperObsoleteMessage = this.obsoleteMessage.toUpperCase();
        String remappingString = null;

        if (upperObsoleteMessage.contains(MAP)){
            remappingString = upperObsoleteMessage.substring(upperObsoleteMessage.indexOf(MAP) + MAP.length());
        }
        else if (upperObsoleteMessage.contains(REMAP)){
            remappingString = upperObsoleteMessage.substring(upperObsoleteMessage.indexOf(REMAP) + REMAP.length());
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
}
