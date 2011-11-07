package uk.ac.ebi.intact.bridges.ontology_manager.interfaces;

import psidev.psi.tools.ontology_manager.interfaces.OntologyTermI;
import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.Map;
import java.util.Set;

/**
 * Extension of OntologyTermI for intact
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public interface IntactOntologyTermI extends OntologyTermI {

    public String getShortLabel();
    public String getFullName();
    public Set<TermDbXref> getDbXrefs();
    public Set<TermAnnotation> getAnnotations();
    public String getDefinition();
    public String getObsoleteMessage();
    public String getURL();
    public Set<String> getComments();
    public Set<String> getAliases();
    public String getRemappedTerm();
    public Set<String> getPossibleTermsToRemapTo();

    public void loadTermFrom (Term term);
    public void loadSynonymsFrom (Map metadata, boolean isObsolete);
    public void loadXrefsFrom (Map xrefs);
}
