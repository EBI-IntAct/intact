package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.ols.model.interfaces.DbXref;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Ontology term for PSI-MOD
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class ModOntologyTerm extends AbstractIntactOntologyTerm{
    protected static final String UNIMOD = "unimod";
    protected static final String UNIMOD_MI_REF = "MI:1015";

    protected static final String DELTAMASS = "deltamass";
    protected static final String DELTAMASS_MI_REF = "MI:1014";

    protected static final String CHEBI = "chebi";
    protected static final String CHEBI_MI_REF = "MI:0474";

    public ModOntologyTerm(String acc, String name) {
        super(acc, name);
    }

    public ModOntologyTerm(String acc) {
        super(acc);
    }

    @Override
    public void loadTermFrom(Term term) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadSynonymsFrom(Map metadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadXrefsFrom(Map xrefs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void processSynonyms(Term term) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void processShortLabel(Term term) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
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
            else if ( RESID.equalsIgnoreCase(xref.getDbName()) ) {
                resIdXrefs.add(xref);
            } else if ( UNIMOD.equalsIgnoreCase(xref.getDbName()) ) {
                TermDbXref unimodRef = new TermDbXref(UNIMOD, UNIMOD_MI_REF, xref.getAccession(), IDENTITY, IDENTITY_MI_REF);
                this.dbXrefs.add(unimodRef); // MOD xref
            } else if ( DELTAMASS.equalsIgnoreCase(xref.getDbName()) ) {
                TermDbXref deltaMassRef = new TermDbXref(DELTAMASS, DELTAMASS_MI_REF, xref.getAccession(), IDENTITY, IDENTITY_MI_REF);
                this.dbXrefs.add(deltaMassRef);  // MOD xref
            } else if ( CHEBI.equalsIgnoreCase(xref.getDbName()) ) {
                TermDbXref chebiRef = new TermDbXref(CHEBI, CHEBI_MI_REF, xref.getAccession(), IDENTITY, IDENTITY_MI_REF);
                this.dbXrefs.add(chebiRef);  // MOD xref
            } else if ( URL.equalsIgnoreCase(xref.getDbName()) ) {
                TermAnnotation url = new TermAnnotation(URL, URL_MI_REF, xref.getAccession());
                this.annotations.add(url);
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

    @Override
    protected void processDefinition(Term term) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void processAnnotations(Term term) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
