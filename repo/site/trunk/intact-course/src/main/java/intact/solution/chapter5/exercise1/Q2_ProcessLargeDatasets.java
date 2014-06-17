/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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

package intact.solution.chapter5.exercise1;

import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import org.hupo.psi.mi.psicquic.wsclient.result.MitabSearchResult;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.util.Collection;

/**
 * Question 2: Access with SOAP to PSICQUIC services has a hard limit of 200 interactions per query.
 * Could you write some code to get all the interactions for pubmed 16189514 from IntAct,
 * which contains more than 2700 interactions?
 *
 * @see org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient
 * @see org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient#getByQuery(String, int, int)
 * @see psidev.psi.mi.search.SearchResult
 * @see psidev.psi.mi.search.SearchResult#getTotalCount()
 * @see psidev.psi.mi.tab.model.BinaryInteraction
 * @see psidev.psi.mi.tab.model.Interactor
 * @see psidev.psi.mi.tab.model.Interactor#getIdentifiers()
 * @see psidev.psi.mi.tab.model.CrossReference
 */
public class Q2_ProcessLargeDatasets {
    public static void main( String[] args ) throws Exception {
        String miqlQuery = "pubid:16189514";

        // Instantiate the service client
        String soapServiceAddress = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/psicquic";

        UniversalPsicquicClient client = new UniversalPsicquicClient(soapServiceAddress);

        MitabSearchResult results;

        System.out.println("Total results: "+client.getByQuery(miqlQuery, 0, 0).getTotalCount());

        int firstResult = 0;
        final int maxResults = 200;

        // paginate until you retrieve all the results

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
