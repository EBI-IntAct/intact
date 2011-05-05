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

package intact.exercise.chapter5.exercise1;

import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.util.Collection;

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

        // TODO start here

    }

    private static void printBinaryInteractions(Collection<BinaryInteraction> binaryInteractions) {

        for ( BinaryInteraction<?> binaryInteraction : binaryInteractions ) {
            // TODO print the first identifiers of molecules A and B
        }

    }

    private static String getFirstIdentifier( Collection<CrossReference> identifiers ) {
        // TODO you will need to print only the first identifier

        return null; //obviously, change this
    }
}
