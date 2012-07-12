package uk.ac.ebi.intact.bridges.ontology_manager.interfaces;

import psidev.psi.tools.ontology_manager.interfaces.OntologyAccessTemplate;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.IntactOntologyTermBuilder;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Extension of OntologyAcessTemplate for intact
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11/11/11</pre>
 */

public interface IntactOntologyAccess extends OntologyAccessTemplate<IntactOntologyTermI> {

    public String getOntologyID();
    public String getDatabaseIdentifier();
    public String getParentFromOtherOntology();
    public Collection<IntactOntologyTermI> getRootTerms();
    public Pattern getDatabaseRegexp();
    public IntactOntologyTermBuilder getOntologyTermBuilder();
}
