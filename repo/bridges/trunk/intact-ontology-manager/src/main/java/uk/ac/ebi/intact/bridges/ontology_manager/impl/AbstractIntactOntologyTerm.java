package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import psidev.psi.tools.ontology_manager.impl.OntologyTermImpl;
import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.LoadableOntologyTerm;
import uk.ac.ebi.ols.model.interfaces.Annotation;
import uk.ac.ebi.ols.model.interfaces.DbXref;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Abstract class fo intact ontology term.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public abstract class AbstractIntactOntologyTerm extends OntologyTermImpl implements IntactOntologyTermI, LoadableOntologyTerm {
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

    protected static final String OBSOLETE_DEF = "OBSOLETE";
    protected static final String DEFINITION_KEY = "definition";
    protected static final String COMMENT_KEY = "comment";
    protected static final String EXACT_KEY = "exact";
    protected static final String EXACT_SYNONYM_KEY = "exact_synonym";
    protected static final String XREF_DEFINITION_KEY = "xref_definition";
    protected static final String META_DATA_SEPARATOR = "_";
    protected static final String META_XREF_SEPARATOR = ":";

    protected static final int XREF_TYPE = 3;
    protected static final int XREF_DEFINITION_TYPE = 2;

    public final static Pattern MOD_REGEXP = Pattern.compile("MOD:[0-9]{5}+");
    public final static Pattern MI_REGEXP = Pattern.compile("MI:[0-9]{4}+");
    public final static Pattern ECO_REGEXP = Pattern.compile("ECO:[0-9]+");

    /**
     * Shortlabel of the cv object in intact
     */
    protected String shortLabel;

    /**
     * Fullname of the cv in Intact
     */
    protected String fullName;

    /**
     * Definition of the cv in intact
     */
    protected String definition;

    /**
     * URL of the Cv
     */
    protected String url;

    /**
     * Obsolete message
     */
    protected String obsoleteMessage;

    /**
     * The comments
     */
    protected Set<String> comments = new HashSet<String>();

    /**
     * The Cv xrefs
     */
    protected Set<TermDbXref> dbXrefs = new HashSet<TermDbXref>();

    /**
     * The cv annotations
     */
    protected Set<TermAnnotation> annotations = new HashSet<TermAnnotation>();

    /**
     * The cv aliases
     */
    protected Set<String> aliases = new HashSet<String>();

    protected String remappedTerm;
    protected Set<String> possibleTermsToRemapTo = new HashSet<String>();

    public AbstractIntactOntologyTerm(String acc) {
        super(acc);
    }

    public AbstractIntactOntologyTerm(String acc, String name) {
        super(acc, name);
    }

    @Override
    /**
     * This method will initialize the shortlabel, fullName, aliases, annotations, xrefs, etc from a Term object
     */
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

    @Override
    /**
     * This method will initialise the synonyms, aliases, definition, comments, obsolete message, url from the map of metadata
     */
    public void loadSynonymsFrom(Map metadata, boolean isObsolete) {
        if (metadata != null){
            for (Object key : metadata.keySet()){
                String keyName = (String) key;

                // definition
                if (DEFINITION_KEY.equalsIgnoreCase(keyName)){
                    String description = (String) metadata.get(keyName);
                    if (description != null){
                        processDefinition(description);
                    }
                }
                // comment
                else if (COMMENT_KEY.equalsIgnoreCase(keyName) || keyName.startsWith(COMMENT_KEY + META_DATA_SEPARATOR)){
                    String comment = (String) metadata.get(keyName);
                    this.comments.add(comment);
                }
                else {
                    String synonym = (String) metadata.get(keyName);
                    processSynonym(keyName, synonym);
                }
            }
        }

        if (shortLabel == null){
            processShortLabel();
        }
    }

    @Override
    /**
     * This method will initialize the xrefs of this object from the map of xrefs
     */
    public void loadXrefsFrom(Map xrefs) {

        if (xrefs != null){
            for (Object key : xrefs.keySet()){
                String keyName = (String) key;

                // xref definitions
                if (XREF_DEFINITION_KEY.equalsIgnoreCase(keyName) || keyName.startsWith(XREF_DEFINITION_KEY)) {
                    String xref = (String) xrefs.get(keyName);

                    if (xref.contains(META_XREF_SEPARATOR)){
                        String [] xrefDef = xref.split(META_XREF_SEPARATOR);
                        String database = null;
                        String accession = null;
                        String pubmedPrimary = null;

                        if (xrefDef.length == 2){
                            database = xrefDef[0];
                            accession = xrefDef[1].trim();
                        }
                        else if (xrefDef.length > 2){
                            database = xrefDef[0];
                            accession = xref.substring(database.length() + 1).trim();
                        }

                        if (database != null && accession != null){
                            pubmedPrimary = processXrefDefinition(xref, database, accession, pubmedPrimary);
                        }
                    }
                }
                else {
                    String xref = (String) xrefs.get(keyName);

                    if (xref.contains(META_XREF_SEPARATOR)){
                        String [] xrefDef = xref.split(META_XREF_SEPARATOR);
                        String database = null;
                        String accession = null;

                        if (xrefDef.length == 2){
                            database = xrefDef[0];
                            accession = xrefDef[1].trim();
                        }
                        else if (xrefDef.length > 2){
                            database = xrefDef[0];
                            accession = xref.substring(database.length() + 1).trim();
                        }

                        if (database != null && accession != null){
                            processXref(database, accession);
                        }
                    }
                    else {
                        processXref(null, xref);
                    }
                }
            }
        }
    }

    /**
     * Process a xref, given its database and accession
     * @param db : database
     * @param accession : database accession
     */
    protected abstract void processXref(String db, String accession);

    /**
     * Process the xrefs which are in the definition.
     * @param xref : the xref as a String
     * @param database : the database
     * @param accession : the accession
     * @param pubmedPrimary : the pubmed primary ID
     */
    protected abstract String processXrefDefinition(String xref, String database, String accession, String pubmedPrimary);

    /**
     * Process the synonyms of a term
     * @param term
     */
    protected abstract void processSynonyms(Term term);

    /**
     * Process the synonym, given its synonym type and value
     * @param synonymName : synonym type
     * @param synonym : synonym value
     */
    protected abstract void processSynonym(String synonymName, String synonym);

    /**
     * Initialize the shortlabel of this object from a term
     * @param term
     */
    protected void processShortLabel(Term term) {
        if (shortLabel == null){
            if ( term.getName() != null && term.getName().length() <= MAX_SHORT_LABEL_LEN ) {
                this.shortLabel = term.getName().toLowerCase();
            } else if ( term.getName() != null && term.getName().length() > MAX_SHORT_LABEL_LEN ) {
                this.shortLabel = term.getName().substring( 0, MAX_SHORT_LABEL_LEN ).toLowerCase();
            }
        }
    }

    protected void processShortLabel() {
        if (shortLabel == null){
            if ( fullName != null && fullName.length() <= MAX_SHORT_LABEL_LEN ) {
                this.shortLabel = fullName.toLowerCase();
            } else if ( fullName != null && fullName.length() > MAX_SHORT_LABEL_LEN ) {
                this.shortLabel = fullName.substring( 0, MAX_SHORT_LABEL_LEN ).toLowerCase();
            }
        }
    }

    /**
     * Process the xrefs of a term
     * @param term
     */
    protected void processXrefs(Term term) {
        Collection<DbXref> dbXrefs = term.getXrefs();

        if (dbXrefs != null){
            String pubmedPrimary = null;
            for (DbXref xref : dbXrefs){

                if (xref.getXrefType() == XREF_TYPE){
                    if (xref.getAccession() == null){
                        processXref(xref.getDbName(), xref.getDescription());
                    }
                    else {
                        processXref(xref.getDbName(), xref.getAccession());
                    }
                }
                else {
                    pubmedPrimary = processXrefDefinition(xref.toString(), xref.getDbName(), xref.getAccession(), pubmedPrimary);
                }
            }
        }
    }

    /**
     * Process the definition of a term
     * @param term
     */
    protected void processDefinition(Term term) {

        String definition = term.getDefinition();
        if (definition != null){
            processDefinition(definition);
        }
    }

    /**
     * Process the definition String
     * @param definition
     * @return
     */
    protected void processDefinition(String definition) {
        if ( definition.contains( LINE_BREAK ) ) {
            String[] defArray = definition.split( LINE_BREAK );

            String otherInfoString = null;

            if ( defArray.length == 2 ) {
                this.definition = defArray[0];
                otherInfoString = defArray[1];
                processInfoInDescription(definition, otherInfoString);
            } else if ( defArray.length > 2 ) {
                this.definition = defArray[0];

                for (int i = 1; i < defArray.length; i++){
                    otherInfoString = defArray[i];
                    processInfoInDescription(definition, otherInfoString);
                }
            }
        }
        else if (definition.contains( OBSOLETE_DEF )){
            String[] defArray = definition.split( OBSOLETE_DEF );

            if ( defArray.length == 2 ) {
                this.definition = defArray[0];
                this.obsoleteMessage = OBSOLETE_DEF + defArray[1];

                if (obsoleteMessage != null){
                    processObsoleteMessage();
                }
            } else if ( defArray.length > 2 ) {
                this.definition = defArray[0];
                this.obsoleteMessage = definition.substring(this.definition.length());

                if (obsoleteMessage != null){
                    processObsoleteMessage();
                }
            }
        }
        else {
            processInfoInDescription(definition, definition);
        }
    }

    /**
     * Process the other information in the description
     * @param definition
     * @param otherInfoString
     * @return true if an obsolete annotation has been added
     */
    protected void processInfoInDescription(String definition, String otherInfoString) {

        // obsolete message
        if ( otherInfoString.startsWith( OBSOLETE_DEF )) {

            this.obsoleteMessage = otherInfoString;

            if (otherInfoString != null){
                processObsoleteMessage();
            }
        }
        else {
            processOtherInfoInDescription(definition, otherInfoString);
        }
    }

    protected abstract void processOtherInfoInDescription(String definition, String otherInfoString);
    protected abstract void processObsoleteMessage();

    /**
     * Process the annotations of a term
     * @param term
     */
    protected void processAnnotations(Term term) {
        Collection<Annotation> annotations = term.getAnnotations();

        if (annotations != null){
            for (Annotation annot : annotations){
                // only one comment with type null
                if (annot.getAnnotationType() != null && COMMENT_KEY.equals(annot.getAnnotationType())){
                    this.comments.add(annot.getAnnotationStringValue());
                }
            }
        }
    }

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
    public Set<String> getComments() {
        return this.comments;
    }

    @Override
    public Set<String> getAliases() {
        return this.aliases;
    }

    public String getRemappedTerm() {
        return remappedTerm;
    }

    public Set<String> getPossibleTermsToRemapTo() {
        return possibleTermsToRemapTo;
    }
}
