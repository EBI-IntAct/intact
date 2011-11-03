package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.Annotation;
import uk.ac.ebi.ols.model.interfaces.DbXref;
import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Ontology term for PSI-MI
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class MiOntologyTerm extends AbstractIntactOntologyTerm {
    private static final String SHORTLABEL_IDENTIFIER = "PSI-MI:PSI-MI-short";
    private static final String ALIAS_IDENTIFIER = "PSI-MI:PSI-MI-alternate";

    private static final String PMID_APPLICATION = "PMID for application instance";

    private static final String GO = "go";
    private static final String GO_MI_REF = "MI:0448";

    private static final String SO = "so";
    private static final String SO_MI_REF = "MI:0601";

    protected static final String OBSOLETE_DEF = "OBSOLETE";
    protected static final String HTTP_DEF = "http";

    private static final String XREF_VALIDATION_REGEXP = "id-validation-regexp";
    private static final String XREF_VALIDATION_REGEXP_MI_REF = "MI:0628";

    protected static final String SEARCH_URL = "search-url";
    protected static final String SEARCH_URL_MI_REF = "MI:0615";

    public MiOntologyTerm(String acc) {
        super(acc);
    }

    public MiOntologyTerm(String acc, String name) {
        super(acc, name);
    }

    @Override
    public void loadTermFrom(Term term) {

        // full name
        this.fullName = term.getName();

        // load synonyms (alias and shortlabel)
        processSynonyms(term);

        // set shortlabel in case it was not set with synonyms
        processShortLabel(term);

        // load db xrefs
        processXrefs(term);

        // load definition, url, obsolete message
        processDefinition(term);

        // load annotations
        processAnnotations(term);
    }

    protected void processAnnotations(Term term) {
        Collection<Annotation> annotations = term.getAnnotations();

        for (Annotation annot : annotations){
            // only one comment with type null
            if (annot.getAnnotationType() == null){
                this.comment = annot.getAnnotationStringValue();
                break;
            }
        }
    }

    protected void processDefinition(Term term) {
        boolean hasObsoleteAnnotation = false;

        String definition = term.getDefinition();

        if ( definition.contains( LINE_BREAK ) ) {
            String[] defArray = definition.split( LINE_BREAK );

            String otherInfoString = "";

            if ( defArray.length == 2 ) {
                this.definition = defArray[0];
                otherInfoString = defArray[1];
            } else if ( defArray.length > 2 ) {
                this.definition = defArray[0];

                otherInfoString = definition.substring(this.definition.length());
            }

            // obsolete message
            if ( otherInfoString.startsWith( OBSOLETE_DEF )) {
                hasObsoleteAnnotation = true;

                TermAnnotation obsolete = new TermAnnotation(OBSOLETE, OBSOLETE_MI_REF, otherInfoString);
                this.annotations.add(obsolete);

            }
            // URL
            else if ( otherInfoString.startsWith( HTTP_DEF ) ) {

                TermAnnotation url = new TermAnnotation(URL, URL_MI_REF, otherInfoString);
                this.annotations.add(url);

            }
            // simple definition
            else {
                this.definition = definition;
            }
        }

        if (!hasObsoleteAnnotation && term.isObsolete()){
            TermAnnotation obsolete = new TermAnnotation(OBSOLETE, OBSOLETE_MI_REF, null);
            this.annotations.add(obsolete);
        }
    }

    protected void processXrefs(Term term) {
        Collection<DbXref> dbXrefs = term.getXrefs();

        DbXref pubmedPrimary = null;
        Collection<DbXref> resIdXrefs = new ArrayList<DbXref>(dbXrefs.size());

        for (DbXref xref : dbXrefs){
            if ( PMID.equalsIgnoreCase(xref.getDbName()) ) {
                if (pubmedPrimary == null){
                    pubmedPrimary = xref;

                    TermDbXref primaryPubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, xref.getAccession(), PRIMARY_REFERENCE, PRIMARY_REFERENCE_MI_REF);
                    this.dbXrefs.add(primaryPubmedRef);
                }
                else {
                    TermDbXref pubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, xref.getAccession(), METHOD_REFERENCE, METHOD_REFERENCE_MI_REF);
                    this.dbXrefs.add(pubmedRef);
                }
            }
            else if ( PUBMED.equalsIgnoreCase(xref.getDbName()) ) {
                TermDbXref pubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, xref.getAccession(), METHOD_REFERENCE, METHOD_REFERENCE_MI_REF);
                this.dbXrefs.add(pubmedRef);
            }
            else if ( PMID_APPLICATION.equalsIgnoreCase(xref.getDbName()) ) {
                TermDbXref pubmedRef = new TermDbXref(PUBMED, PUBMED_MI_REF, xref.getAccession(), SEE_ALSO, SEE_ALSO_MI_REF);
                this.dbXrefs.add(pubmedRef); // MI not MOD
            } else if ( GO.equalsIgnoreCase(xref.getDbName()) ) {
                TermDbXref goRef = new TermDbXref(GO, GO_MI_REF, xref.getDbName() + ":" + xref.getAccession(), IDENTITY, IDENTITY_MI_REF);
                this.dbXrefs.add(goRef); // MI not MOD
            } else if ( RESID.equalsIgnoreCase(xref.getDbName()) ) {
                resIdXrefs.add(xref);
            } else if ( SO.equalsIgnoreCase(xref.getDbName()) ) {
                TermDbXref soRef = new TermDbXref(SO, SO_MI_REF, xref.getDbName() + ":" + xref.getAccession(), IDENTITY, IDENTITY_MI_REF);
                this.dbXrefs.add(soRef);  // MI not MOD
            } else if ( XREF_VALIDATION_REGEXP.equalsIgnoreCase(xref.getDbName()) ) {
                TermAnnotation validation = new TermAnnotation(XREF_VALIDATION_REGEXP, XREF_VALIDATION_REGEXP_MI_REF, xref.getAccession().trim());  // MI xref
                this.annotations.add(validation);
            } else if ( SEARCH_URL.equalsIgnoreCase(xref.getDbName()) ) {
                TermAnnotation validation = new TermAnnotation(SEARCH_URL, SEARCH_URL_MI_REF, xref.getDescription());  // MI xref
                this.annotations.add(validation);
            }
        }

        if (resIdXrefs.size() == 1){
            DbXref residXref = resIdXrefs.iterator().next();

            TermDbXref residIdentity = new TermDbXref(RESID, RESID_MI_REF, residXref.getAccession(), IDENTITY, IDENTITY_MI_REF);
            this.dbXrefs.add(residIdentity);
        }
        else if (resIdXrefs.size() > 1){
            for (DbXref ref : resIdXrefs){
                TermDbXref resXref = new TermDbXref(RESID, RESID_MI_REF, ref.getAccession(), SEE_ALSO, SEE_ALSO_MI_REF);
                this.dbXrefs.add(resXref);
            }
        }
    }

    protected void processShortLabel(Term term) {
        if (shortLabel == null){
            if ( term.getName() != null && term.getName().length() <= MAX_SHORT_LABEL_LEN ) {
                this.shortLabel = term.getName();
            } else if ( term.getName() != null && term.getName().length() > MAX_SHORT_LABEL_LEN ) {
                this.shortLabel = term.getName().substring( 0, MAX_SHORT_LABEL_LEN );
            }
        }
    }

    protected void processSynonyms(Term term) {
        Collection<TermSynonym> synonyms = term.getSynonyms();

        for (TermSynonym synonym : synonyms){
            Term synonymType = synonym.getSynonymType();
            //PSI-MOD-label for MOD
            if (synonymType != null){
                if (SHORTLABEL_IDENTIFIER.equalsIgnoreCase(synonymType.getIdentifier())){
                    this.shortLabel = synonym.getSynonym();
                }
                else if (ALIAS_IDENTIFIER.equalsIgnoreCase(synonymType.getIdentifier())){
                    this.aliases.add(synonym.getSynonym());
                }
            }
        }
    }

    @Override
    public void loadSynonymsFrom(Map metadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadXrefsFrom(Map xrefs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
