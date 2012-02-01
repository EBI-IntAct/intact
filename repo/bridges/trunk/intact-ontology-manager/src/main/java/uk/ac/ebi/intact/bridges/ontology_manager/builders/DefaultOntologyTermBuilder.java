package uk.ac.ebi.intact.bridges.ontology_manager.builders;

import uk.ac.ebi.intact.bridges.ontology_manager.impl.DefaultOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Default ontologyTerm builder
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/02/12</pre>
 */

public class DefaultOntologyTermBuilder implements IntactOntologyTermBuilder{

    private String ontology_definition;
    private String full_name;
    private String short_name;

    private String databaseIdentifier = "MI:0444";
    private String mIParent;
    private Pattern databasePattern;

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(Term term) {
        DefaultOntologyTerm defaultTerm = new DefaultOntologyTerm(term.getIdentifier(), term.getName());
        defaultTerm.loadTermFrom(term);

        return defaultTerm;
    }

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(String accession, String name, Map metadata, Map xrefs, boolean isObsolete) {
        DefaultOntologyTerm defaultTerm = new DefaultOntologyTerm(accession, name);
        defaultTerm.loadSynonymsFrom(metadata, isObsolete);
        defaultTerm.loadXrefsFrom(xrefs);

        return defaultTerm;
    }

    @Override
    public IntactOboLoader createIntactOboLoader(File ontologyDirectory) {
        return new IntactOboLoader(ontologyDirectory, ontology_definition, full_name, short_name, this);
    }

    @Override
    public String getDatabaseIdentifier() {
        return databaseIdentifier;
    }

    @Override
    public String getParentFromOtherOntology() {
        return mIParent;
    }

    @Override
    public Pattern getDatabaseRegexp() {
        return databasePattern;
    }

    public String getOntology_definition() {
        return ontology_definition;
    }

    public void setOntology_definition(String ontology_definition) {
        this.ontology_definition = ontology_definition;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getmIParent() {
        return mIParent;
    }

    public void setmIParent(String mIParent) {
        this.mIParent = mIParent;
    }

    public Pattern getDatabasePattern() {
        return databasePattern;
    }

    public void setDatabasePattern(Pattern databasePattern) {
        this.databasePattern = databasePattern;
    }

    public void setDatabaseIdentifier(String databaseIdentifier) {
        this.databaseIdentifier = databaseIdentifier;
    }
}
