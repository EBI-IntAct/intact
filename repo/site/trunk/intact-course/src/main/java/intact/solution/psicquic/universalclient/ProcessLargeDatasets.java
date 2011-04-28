/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package intact.solution.psicquic.universalclient;

import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.util.Collection;

/**
 * Query the IntAct web service and process large amount of interactions.
 * <p/>
 * The aim here is to prevent the following exception from being thrown:
 * <pre>java.lang.OutOfMemoryError: Java heap space</pre>
 */
public class ProcessLargeDatasets {
    public static void main( String[] args ) throws Exception {
        String miqlQuery = "BBC1";

        // Instantiate the service client
        String soapServiceAddress = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/psicquic";

        UniversalPsicquicClient client = new UniversalPsicquicClient(soapServiceAddress);

        SearchResult<BinaryInteraction> results;

        int firstResult = 0;
        final int maxResults = 5;

        do {
            results = client.getByQuery(miqlQuery, firstResult, maxResults);

            System.out.println( "Retrieved page " + (firstResult+1) + ".." + (firstResult + maxResults) );

            int i = 0;
            for ( BinaryInteraction<?> binaryInteraction : results.getData() ) {
                i++;
                String idA = getFirstIdentifier( binaryInteraction.getInteractorA().getIdentifiers() );
                String idB = getFirstIdentifier( binaryInteraction.getInteractorB().getIdentifiers() );

                String interactionAc = getFirstIdentifier( binaryInteraction.getInteractionAcs() );

                System.out.println( "\tInteraction "+ i +"(" + interactionAc + "): " + idA + " interacts with " + idB );
            }

            firstResult += maxResults;

        } while( firstResult < results.getTotalCount() );
    }

    private static String getFirstIdentifier( Collection<CrossReference> identifiers ) {
        if ( !identifiers.isEmpty() ) {
            return identifiers.iterator().next().getIdentifier();
        }
        return null;
    }
}
