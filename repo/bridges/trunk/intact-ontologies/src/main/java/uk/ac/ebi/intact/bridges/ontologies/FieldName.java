package uk.ac.ebi.intact.bridges.ontologies;

/**
 * Names of the fields in the lucene directory.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface FieldName {

    static final String ONTOLOGY = "ontology";
    static final String PARENT_ID = "pid";
    static final String PARENT_NAME = "pname";
    static final String PARENT_NAME_SORTABLE = "pname_sort";
    static final String CHILDREN_ID = "cid";
    static final String CHILDREN_NAME = "cname"; 
    static final String CHILDREN_NAME_SORTABLE = "cname_sort"; 
    static final String RELATIONSHIP_TYPE = "reltype";
    static final String RELATIONSHIP_CYCLIC = "cyclic"; 
}
