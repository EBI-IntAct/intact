package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import psidev.psi.tools.ontology_manager.impl.OntologyTermImpl;
import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class fo intact ontology term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public abstract class AbstractIntactOntologyTerm extends OntologyTermImpl implements IntactOntologyTermI {
    protected static final String PMID = "PMID";

    protected static final String PUBMED = "pubmed";
    protected static final String PUBMED_MI_REF = "MI:0446";

    protected static final String PRIMARY_REFERENCE = "primary-reference";
    protected static final String PRIMARY_REFERENCE_MI_REF = "MI:0358";

    protected static final String SEE_ALSO = "see-also";
    protected static final String SEE_ALSO_MI_REF = "MI:0361";

    protected static final String IDENTITY = "identity";
    protected static final String IDENTITY_MI_REF = "MI:0356";

    protected static final String RESID = "resid";
    protected static final String RESID_MI_REF = "MI:0248";

    protected static final int MAX_SHORT_LABEL_LEN = 256;
    protected static final String LINE_BREAK = "\n";

    protected static final String OBSOLETE = "obsolete";
    protected static final String OBSOLETE_MI_REF = "MI:0431";

    protected static final String URL = "url";
    protected static final String URL_MI_REF = "MI:0614";

    protected static final String METHOD_REFERENCE = "method reference";
    protected static final String METHOD_REFERENCE_MI_REF = "MI:0357";

    protected String shortLabel;
    protected String fullName;
    protected String definition;
    protected String url;
    protected String obsoleteMessage;
    protected String searchUrl;
    protected String xrefValidationRegexp;
    protected String comment;
    protected Set<TermDbXref> dbXrefs = new HashSet<TermDbXref>();
    protected Set<TermAnnotation> annotations = new HashSet<TermAnnotation>();
    protected Set<String> aliases = new HashSet<String>();

    public AbstractIntactOntologyTerm(String acc) {
        super(acc);
    }

    public AbstractIntactOntologyTerm(String acc, String name) {
        super(acc, name);
    }

    public abstract void loadTermFrom (Term term);
    public abstract void loadSynonymsFrom (Map metadata);
    public abstract void loadXrefsFrom (Map xrefs);

    protected abstract void processSynonyms(Term term);
    protected abstract void processShortLabel(Term term);
    protected abstract void processXrefs(Term term);
    protected abstract void processDefinition(Term term);
    protected abstract void processAnnotations(Term term);

    @Override
    public String getShortLabel() {
        return this.shortLabel;
    }

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public Set<TermDbXref> getDbXrefs() {
        return this.dbXrefs;
    }

    @Override
    public Set<TermAnnotation> getAnnotations() {
        return this.annotations;
    }

    @Override
    public String getDefinition() {
        return this.definition;
    }

    @Override
    public String getObsoleteMessage() {
        return this.obsoleteMessage;
    }

    @Override
    public String getURL() {
        return this.url;
    }

    @Override
    public String getSearchUrl() {
        return this.searchUrl;
    }

    @Override
    public String getXrefValidationRegexp() {
        return this.xrefValidationRegexp;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public Set<String> getAliases() {
        return this.aliases;
    }
}
