package uk.ac.ebi.intact.bridges.ontologies;

/**
 * Names of the fields in the lucene directory.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface FieldName {

    String ONTOLOGY = "ontology";
    String PARENT_ID = "pid";
    String PARENT_NAME = "pname";
    String PARENT_NAME_SORTABLE = "pname_sort";
    String PARENT_SYNONYMS = "psynonyms";
    String CHILDREN_ID = "cid";
    String CHILDREN_NAME = "cname"; 
    String CHILDREN_NAME_SORTABLE = "cname_sort"; 
    String CHILDREN_SYNONYMS = "csynonyms"; 
    String RELATIONSHIP_TYPE = "reltype";
    String RELATIONSHIP_CYCLIC = "cyclic"; 
}
