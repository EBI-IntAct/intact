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

/**
 * Question 1: Could you write the code to download a MITAB stream from PSICQUIC using the simple client?
 * Print the MITAB for the publication with pubmed 16189514 from IntAct in the console.
 *
 * @see org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient
 * @see org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient#getByQuery(String)
 *
 */
public class Q1_SimplePsicquicQuery {

     public static void main(String[] args) throws Exception {
        PsicquicSimpleClient client = new PsicquicSimpleClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/");

        // TODO start here

    }

}
