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

package intact.solution.psicquic.simpleclient;

import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Querying a PSICQUIC service and reading the results using the MITAB library.
 *
 * @see org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient
 * @see psidev.psi.mi.tab.PsimiTabReader
 * @see psidev.psi.mi.tab.PsimiTabReader#readLine(String)
 */
public class ClientAndMitabBetterMemory {

    public static void main(String[] args) throws Exception {
        // get a REST URl from the registry http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=STATUS

        PsicquicSimpleClient client = new PsicquicSimpleClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/");

        PsimiTabReader mitabReader = new PsimiTabReader(false);

        try {
            final InputStream result = client.getByQuery("brca2");

            BufferedReader in = new BufferedReader(new InputStreamReader(result));

            String line;

            while ((line = in.readLine()) != null) {
                BinaryInteraction binaryInteraction = mitabReader.readLine(line);

                printBinaryInteraction(binaryInteraction);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printBinaryInteraction(BinaryInteraction<?> binaryInteraction) {
        // print first ids for interactors and interaction
        CrossReference idA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next();
        CrossReference idB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next();
        CrossReference interactionAc = binaryInteraction.getInteractionAcs().iterator().next();

        System.out.println("Interaction "+interactionAc.getIdentifier()+": "+idA.getIdentifier()+" - "+idB.getIdentifier());
    }

}
