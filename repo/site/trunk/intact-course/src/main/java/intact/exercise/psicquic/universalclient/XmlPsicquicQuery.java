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

package intact.exercise.psicquic.universalclient;

import org.hupo.psi.mi.psicquic.wsclient.XmlPsicquicClient;
import org.hupo.psi.mi.psicquic.wsclient.XmlSearchResult;
import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.EntrySet;
import psidev.psi.mi.xml.model.Interaction;
import psidev.psi.mi.xml.model.Participant;

/**
 * Download interactions from PSICQUIC, using the universal client.
 *
 * @see org.hupo.psi.mi.psicquic.wsclient.XmlPsicquicClient
 * @see org.hupo.psi.mi.psicquic.wsclient.XmlPsicquicClient#getByQuery(String, int, int)
 * @see org.hupo.psi.mi.psicquic.wsclient.XmlSearchResult
 * @see psidev.psi.mi.xml.model.EntrySet
 * @see psidev.psi.mi.xml.model.Entry
 * @see psidev.psi.mi.xml.model.Interaction
 * @see psidev.psi.mi.xml.model.Participant
 * @see psidev.psi.mi.xml.model.Interactor
 */
public class XmlPsicquicQuery {

    public static void main( String[] args ) throws Exception {
        // the universal client uses the SOAP service URL, obtainable from the Registry

        // Exercise: search for "bbc1" in IntAct

        String soapServiceAddress = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/psicquic";

        XmlPsicquicClient client = new XmlPsicquicClient( soapServiceAddress );
        final XmlSearchResult xmlResult = client.getByQuery( "bbc1", 0, Integer.MAX_VALUE );

        // Print the results in the console
        System.out.println( "Interactions found: " + xmlResult.getTotalResults() );

        final EntrySet entrySet = xmlResult.getEntrySet();

        printEntrySet( entrySet );

    }

    private static void printEntrySet( EntrySet entrySet ) {
        for ( Entry entry : entrySet.getEntries() ) {
            for ( Interaction interaction : entry.getInteractions() ) {
                System.out.println( "Interaction: " + interaction.getXref().getPrimaryRef().getId() );

                for ( Participant participant : interaction.getParticipants() ) {
                    System.out.println( "\tParticipant: " + participant.getInteractor().getXref().getPrimaryRef().getId() );
                }
            }
        }
    }
}
