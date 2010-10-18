/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.ols;

import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;
import uk.ac.ebi.ook.Constants;

import java.util.Map;

/**
 * Client for the Search Web Service
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class OlsClient {

    private static final String DEFAULT_URL = "http://www.ebi.ac.uk/ontology-lookup/services/OntologyQuery?wsdl";
    private static final String URL =         "http://www.ebi.ac.uk/ontology-lookup/OntologyQuery.wsdl"; 
    /**
     * Stub to handle the search web service
     */
    private Query ontologyQuery;

    /**
     * Prepare the web service.
     */
    public OlsClient() {

        try {
            //QueryService olsService = new QueryService(new URL(DEFAULT_URL), new QName("http://www.ebi.ac.uk/ontology-lookup/OntologyQuery", "QueryService"));
            //this.ontologyQuery = olsService.getOntologyQuery();

            QueryService locator = new QueryServiceLocator();
            this.ontologyQuery = locator.getOntologyQuery();
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    public Query getOntologyQuery() {
        return ontologyQuery;
    }

    public static void main(String[] args) throws Exception {
        //System.out.println("Version: "+new OlsClient().getOntologyQuery().getVersion());
        //System.out.println("Test2: "+new OlsClient().getOntologyQuery().getOntologyNames());

        final OlsClient client = new OlsClient();

//        Map map = client.getOntologyQuery().getTermChildren("MI:0001", "MI", 1, new int[]{0, 1, 2, 3, 4, 5, 6});
//        System.out.println(map);
//
//        Query ontologyQuery = client.getOntologyQuery();
//        String exampleTerm = ontologyQuery.getTermById("MI:0001", "MI");
//        System.out.println(exampleTerm);

        final String taxid = "10090";

        final String eukariote = client.getOntologyQuery().getTermById( taxid, "NEWT" );
        System.out.println( taxid+"'s name: " + eukariote );
        
        Map children = client.getOntologyQuery().getTermChildren( taxid, "NEWT", 2,
                                                                  new int[]{
                                                                          Constants.IS_A_RELATION_TYPE_ID,
                                                                          Constants.PART_OF_RELATION_TYPE_ID
                                                                  } );
        System.out.println( "Children of "+ taxid +": " + children.size() );
        for ( Object o : children.keySet() ) {
            System.out.println( o + " -> " + children.get( o ) );
        }

        Map relationship = client.getOntologyQuery().getTermRelations( taxid, "NEWT" );
        System.out.println( "Relationship of "+ taxid +": "+ relationship.size() );
        for ( Object o : relationship.keySet() ) {
            System.out.println( o + " -> " + relationship.get( o ) );
        }


        final Map metadata = client.getOntologyQuery().getTermMetadata( taxid, "NEWT" );
        System.out.println( "Metadata of "+ taxid +": "+ metadata.size() );
        for ( Object o : metadata.keySet() ) {
            System.out.println( o + " -> " + metadata.get( o ) );
        }

        /////////////////////////////////////////////////////////////////////////////////////

//        final String mi = client.getOntologyQuery().getTermById( "MI:0001", "MI" );
//        System.out.println( "term = " + mi );
//
//        children = client.getOntologyQuery().getTermChildren( "MI:0001", "MI", 2,
//                                                              new int[]{
//                                                                      Constants.IS_A_RELATION_TYPE_ID,
//                                                                      Constants.PART_OF_RELATION_TYPE_ID
//                                                              } );
//        System.out.println( "Children of \"MI:0001\":" + children.size() );
//        for ( Object o : children.keySet() ) {
//            System.out.println( o + " -> " + children.get( o ) );
//        }
    }
}
