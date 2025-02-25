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

package intact.exercise.chapter5.exercise2;

import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.io.InputStream;
import java.util.Collection;

/**
 * Question 3: You could use the MITAB Java library to parse the results stream into a data model.
 * How would you read the whole result using the psidev.psi.mi.tab.PsimiTabReader class?
 * Could you print the identifiers for molecules A and B?
 *
 * @see org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient
 * @see psidev.psi.mi.tab.PsimiTabReader
 * @see psidev.psi.mi.tab.PsimiTabReader#read(java.io.InputStream)
 */
public class Q3_ClientAndMitab {

    public static void main(String[] args) throws Exception {
        // get a REST URl from the registry http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=STATUS

        PsicquicSimpleClient client = new PsicquicSimpleClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/");

        // instantiate the reader
        PsimiTabReader mitabReader = new PsimiTabReader();

        InputStream result = null; // how to get the result from PSICQUIC?

        Collection<BinaryInteraction> binaryInteractions = null; // what to put here?

        System.out.println("Interactions found: "+binaryInteractions.size());

        printBinaryInteractions(binaryInteractions);
    }

    private static void printBinaryInteractions(Collection<BinaryInteraction> binaryInteractions) {
        // print the first identifiers for molecule A and B

    }

    private static String getFirstIdentifier( Collection<CrossReference> identifiers ) {
        // print only the first identifier
        return null;
    }

}
