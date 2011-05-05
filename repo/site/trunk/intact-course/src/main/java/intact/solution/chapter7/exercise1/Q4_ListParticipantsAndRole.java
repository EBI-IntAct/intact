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

package intact.solution.chapter7.exercise1;

import psidev.psi.mi.xml.PsimiXmlLightweightReader;
import psidev.psi.mi.xml.PsimiXmlReaderException;
import psidev.psi.mi.xml.model.DbReference;
import psidev.psi.mi.xml.model.Interaction;
import psidev.psi.mi.xml.model.Interactor;
import psidev.psi.mi.xml.model.Participant;
import psidev.psi.mi.xml.xmlindex.IndexedEntry;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Iterate through interaction and display information about its participants (count, molecule type, id, role).
 */
public class Q4_ListParticipantsAndRole {
    public static void main( String[] args ) throws PsimiXmlReaderException {

        // The data file to be parsed by the API
        File inputFile = new File( Q4_ListParticipantsAndRole.class.getResource( "/samples/psixml25/17129783.xml" ).getFile() );

        // Create a reader
        PsimiXmlLightweightReader reader = new PsimiXmlLightweightReader( inputFile );

        // Read the whole file into an EntrySet
        final List<IndexedEntry> indexedEntries = reader.getIndexedEntries();

        // Show all interactions, their respective id and label
        for ( IndexedEntry entry : indexedEntries ) {
            final Iterator<Interaction> iterator = entry.unmarshallInteractionIterator();
            while ( iterator.hasNext() ) {
                Interaction interaction = iterator.next();

                final String label = interaction.getNames().getShortLabel();
                final int id = interaction.getId();
                final int participantCount = interaction.getParticipants().size();

                System.out.println( "Interaction " + id + ": " + label + " (" + participantCount + " participants)" );

                for ( Participant participant : interaction.getParticipants() ) {
                    final Interactor interactor = participant.getInteractor();
                    final String interactorTypeLabel = interactor.getInteractorType().getNames().getShortLabel();
                    final String identity = getIdentity( interactor );
                    final String role = participant.getBiologicalRole().getNames().getShortLabel();

                    System.out.println( "\t " + interactorTypeLabel + ": " + identity + " (" + role + ")" );
                }
            }
        }
    }

    private static String getIdentity( Interactor interactor ) {
        for ( DbReference ref : interactor.getXref().getAllDbReferences() ) {
            if ( "MI:0356".equals( ref.getRefTypeAc() ) || "identity".equals( ref.getRefType() ) ) {
                return ref.getId();
            }
        }
        return null;
    }
}
