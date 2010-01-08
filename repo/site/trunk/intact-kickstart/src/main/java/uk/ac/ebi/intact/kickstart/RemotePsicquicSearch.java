/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.kickstart;

import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.tab.model.BinaryInteraction;

/**
 * This example does not need the database to work and shows how to access the EBI IntAct database
 * remotely.
 *
 */
public class RemotePsicquicSearch {

     public static void main(String[] args) throws Exception {

        // change the endpoint address as needed
        UniversalPsicquicClient client = new UniversalPsicquicClient("http://www.ebi.ac.uk/intact/psicquic/webservices/psicquic");

        SearchResult<?> result = client.getByInteractor("brca2", 0, 50);

        // Print the results in the console
        System.out.println("Interactions found: "+result.getTotalCount());

        for ( BinaryInteraction<?> binaryInteraction : result.getData()) {
            String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
            String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();

            String interactionAc = binaryInteraction.getInteractionAcs().iterator().next().getIdentifier();

            System.out.println("\tInteraction ("+interactionAc+"): "+interactorIdA+" interacts with "+interactorIdB);
        }

    }
}