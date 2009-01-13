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

import psidev.psi.mi.search.SearchResult;
import uk.ac.ebi.intact.binarysearch.wsclient.BinarySearchServiceClient;

import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psicquic.wsclient.MitabPsicquicClient;
import org.hupo.psi.mi.psicquic.QueryResponse;
import org.hupo.psi.mi.psicquic.DbRef;
import org.hupo.psi.mi.psicquic.RequestInfo;
import org.hupo.psi.mi.psicquic.PsicquicService;

/**
 * This example does not need the database to work and shows how to access the EBI IntAct database
 * remotely.
 *
 */
public class RemotePsicquicSearch {

     public static void main(String[] args) throws Exception {

        // change the enpoint address as needed
        MitabPsicquicClient client = new MitabPsicquicClient("http://www.ebi.ac.uk/intact/psicquic/webservices/psicquic");

        SearchResult<IntactBinaryInteraction> result = client.getByInteractor("brca2", 0, 50);

        // Print the results in the console
        System.out.println("Interactions found: "+result.getTotalCount());

        for ( IntactBinaryInteraction binaryInteraction : result.getData()) {
            String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
            String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();

            String interactionAc = binaryInteraction.getInteractionAcs().iterator().next().getIdentifier();

            System.out.println("\tInteraction ("+interactionAc+"): "+interactorIdA+" interacts with "+interactorIdB);
        }

    }
}