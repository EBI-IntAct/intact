package uk.ac.ebi.intact.dataexchange.psimi.solr;

/**
 * Names of the Fields.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface FieldNames {

    String ID_A = "idA";
    String ID_B = "idB";
    String ALTID_A = "altidA";
    String ALTID_B = "altidB";
    String ALIAS_A = "aliasA";
    String ALIAS_B = "aliasB";
    String DETMETHOD = "detmethod";
    String PUBAUTH = "pubauth";
    String PUBID = "pubid";
    String TAXID_A = "taxidA";
    String TAXID_B = "taxidB";
    String TYPE = "type";
    String SOURCE = "source";
    String INTERACTION_ID = "interaction_id";
    String CONFIDENCE = "confidence";
    String EXPERIMENTAL_ROLE_A = "experimentalRoleA";
    String EXPERIMENTAL_ROLE_B = "experimentalRoleB";
    String BIOLOGICAL_ROLE_A = "biologicalRoleA";
    String BIOLOGICAL_ROLE_B = "biologicalRoleB";
    String PROPERTIES_A = "propertiesA";
    String PROPERTIES_B = "propertiesB";
    String TYPE_A = "typeA";
    String TYPE_B = "typeB";
    String HOST_ORGANISM = "hostOrganism";
    String EXPANSION = "expansion";
    String DATASET = "dataset";
    String ANNOTATION_A = "annotationA";
    String ANNOTATION_B = "annotationB";
    String PARAMETER_A = "parameterA";
    String PARAMETER_B = "parameterB";
    String PARAMETER_INTERACTION = "parameterInteraction";

    String DB_GO = "go";
    String DB_INTERPRO = "interpro";
    String DB_PSIMI = "psi-mi";
    String DB_CHEBI = "chebi";

    String LINE = "line";

    String PKEY = "pkey";
    String RIGID = "rigid";
    String RELEVANCE_SCORE = "relevancescore";
    String EVIDENCES = "evidences";
}
