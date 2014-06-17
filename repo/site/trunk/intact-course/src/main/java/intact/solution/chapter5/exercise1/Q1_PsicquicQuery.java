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
import java.util.List;

/**
 * Question 1: Could you write the code to query the interactions for brca2 from IntAct and print the identifiers for molecule A and B in the console?
 *
 * @see org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient
 * @see org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient#getByQuery(String, int, int)
 * @see psidev.psi.mi.tab.model.BinaryInteraction
 * @see psidev.psi.mi.tab.model.Interactor
 * @see psidev.psi.mi.tab.model.Interactor#getIdentifiers()
 * @see psidev.psi.mi.tab.model.CrossReference

 */
public class Q1_PsicquicQuery {

    public static void main(String[] args) throws Exception {
        // the universal client uses the SOAP service URL, obtainable from the Registry
        String soapServiceAddress = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/psicquic";

        UniversalPsicquicClient client = new UniversalPsicquicClient(soapServiceAddress);

        // search by brca2
        MitabSearchResult results = client.getByQuery("brca2", 0, Integer.MAX_VALUE);

        // Print the results in the console
        System.out.println( "Interactions found: " + results.getTotalCount() );

        List<BinaryInteraction> binaryInteractions = results.getData();

        printBinaryInteractions(binaryInteractions);
    }

    private static void printBinaryInteractions(Collection<BinaryInteraction> binaryInteractions) {

        for ( BinaryInteraction<?> binaryInteraction : binaryInteractions ) {
            String idA = getFirstIdentifier( binaryInteraction.getInteractorA().getIdentifiers() );
            String idB = getFirstIdentifier( binaryInteraction.getInteractorB().getIdentifiers() );

            String interactionAc = getFirstIdentifier( binaryInteraction.getInteractionAcs() );

            System.out.println( "\tInteraction (" + interactionAc + "): " + idA + " interacts with " + idB );
        }

    }

    private static String getFirstIdentifier( Collection<CrossReference> identifiers ) {
        if ( !identifiers.isEmpty() ) {
            return identifiers.iterator().next().getIdentifier();
        }
        return null;
    }
}
