package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.util.Collection;
import java.util.Set;

/**
 * Ontology term for PSI-MOD
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class ModOntologyTerm extends AbstractIntactOntologyTerm{
    private static final String SHORTLABEL_IDENTIFIER = "Short label curated by PSI-MOD";
    private static final String DELTAMASS_ALIAS_IDENTIFIER = "Label from MS DeltaMass";
    private static final String MOD_ALIAS_IDENTIFIER = "Alternate name curated by PSI-MOD";
    private static final String OMSSA_IDENTIFIER = "Short label from OMSSA";
    private static final String MS_IDENTIFIER = "Agreed label from MS community";
    private static final String RESID_IDENTIFIER = "Alternate name from RESID";
    private static final String RESID_MISNOMER_IDENTIFIER = "Misnomer tagged alternate name from RESID";
    private static final String RESID_NAME_IDENTIFIER = "Name from RESID";
    private static final String RESID_SYSTEMATIC_IDENTIFIER = "Systematic name from RESID";
    private static final String UNIMOD_IDENTIFIER = "Alternate name from UniMod";
    private static final String UNIMOD_DESCRIPTION_IDENTIFIER = "Description (full_name) from UniMod";
    private static final String UNIMOD_INTERIM_IDENTIFIER = "Interim label from UniMod";
    private static final String UNIPROT_FEATURE_IDENTIFIER = "Protein feature description from UniProtKB";

    protected static final String UNIMOD = "unimod";
    protected static final String UNIMOD_MI_REF = "MI:1015";

    protected static final String DELTAMASS = "deltamass";
    protected static final String DELTAMASS_MI_REF = "MI:1014";

    protected static final String CHEBI = "chebi";
    protected static final String CHEBI_MI_REF = "MI:0474";

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
        else if ( RESID.equalsIgnoreCase(database) ) {
            resIdRefs.add(xref);
        } else if ( UNIMOD.equalsIgnoreCase(database) ) {
            TermDbXref unimodRef = new TermDbXref(UNIMOD, UNIMOD_MI_REF, accession, IDENTITY, IDENTITY_MI_REF);
            this.dbXrefs.add(unimodRef); // MOD xref
        } else if ( DELTAMASS.equalsIgnoreCase(database) ) {
            TermDbXref deltaMassRef = new TermDbXref(DELTAMASS, DELTAMASS_MI_REF, accession, IDENTITY, IDENTITY_MI_REF);
            this.dbXrefs.add(deltaMassRef);  // MOD xref
        } else if ( CHEBI.equalsIgnoreCase(database) ) {
            TermDbXref chebiRef = new TermDbXref(CHEBI, CHEBI_MI_REF, accession, IDENTITY, IDENTITY_MI_REF);
            this.dbXrefs.add(chebiRef);  // MOD xref
        } else if ( URL.equalsIgnoreCase(database) ) {
            TermAnnotation url = new TermAnnotation(URL, URL_MI_REF, accession);
            this.annotations.add(url);
        }
    }

    @Override
    protected void processSynonyms(Term term) {
        Collection<TermSynonym> synonyms = term.getSynonyms();

        for (TermSynonym synonym : synonyms){
            Term synonymType = synonym.getSynonymType();
            //PSI-MOD-label for MOD
            if (synonymType != null){
                if (SHORTLABEL_IDENTIFIER.equalsIgnoreCase(synonymType.getName())){
                    this.shortLabel = synonym.getSynonym();
                }
                else if (DELTAMASS_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymType.getName())
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
                }
            }
        }
    }

    @Override
    protected void processSynonym(String synonymName, String synonym) {
        if (synonymName.startsWith(EXACT_SYNONYM_KEY + META_DATA_SEPARATOR) || EXACT_SYNONYM_KEY.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(DELTAMASS_ALIAS_IDENTIFIER + META_DATA_SEPARATOR) || DELTAMASS_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(MOD_ALIAS_IDENTIFIER + META_DATA_SEPARATOR) || MOD_ALIAS_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(OMSSA_IDENTIFIER + META_DATA_SEPARATOR) || OMSSA_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(MS_IDENTIFIER + META_DATA_SEPARATOR) || MS_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
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
        else if (synonymName.startsWith(UNIMOD_IDENTIFIER + META_DATA_SEPARATOR) || UNIMOD_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(UNIMOD_DESCRIPTION_IDENTIFIER + META_DATA_SEPARATOR) || UNIMOD_DESCRIPTION_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(UNIMOD_INTERIM_IDENTIFIER + META_DATA_SEPARATOR) || UNIMOD_INTERIM_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
        else if (synonymName.startsWith(UNIPROT_FEATURE_IDENTIFIER + META_DATA_SEPARATOR) || UNIPROT_FEATURE_IDENTIFIER.equalsIgnoreCase(synonymName)){
            this.aliases.add(synonym);
        }
    }

    protected boolean processOtherInfoInDescription(String definition, String otherInfoString) {
        boolean hasObsolete = false;

        // obsolete message
        if ( otherInfoString.startsWith( OBSOLETE_DEF )) {
            hasObsolete = true;

            TermAnnotation obsolete = new TermAnnotation(OBSOLETE, OBSOLETE_MI_REF, otherInfoString);
            this.annotations.add(obsolete);

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
        return hasObsolete;
    }
}
