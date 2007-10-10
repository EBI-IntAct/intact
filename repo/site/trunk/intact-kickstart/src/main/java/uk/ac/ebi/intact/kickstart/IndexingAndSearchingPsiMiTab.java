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

import org.apache.lucene.store.Directory;
import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.search.Searcher;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.io.InputStream;

/**
 * Example of how to index and search PSI-MITAB2.5 data.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */                         
public class IndexingAndSearchingPsiMiTab {

    public static void main(String[] args) throws Exception {

        // Load a PSI-MITAB2.5 file stored locally
        InputStream psimitabFileStream = IndexingAndSearchingPsiMiTab.class.getResourceAsStream("/16469705.txt");

        // The next statement creates a Lucene index in memory.
        // Use the alternative Searcher.buildIndex(File...) to create indexes in filesystem directories,
        // that you can re-use later again or use them in your applications.
        // An index in memory is only useful for testing/educational purposes.
        Directory directory = Searcher.buildIndexInMemory(psimitabFileStream, true, true);

        // After a directory has been created, you can use this directory (or any existing directory
        // in the filesystem) to execute your searches.
        SearchResult result = Searcher.search("Itch", directory);

        // We print some information about the interactions found
        System.out.println("Interactions found: "+result.getTotalCount());

        for (BinaryInteraction binaryInteraction : result.getInteractions()) {
            String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
            String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();

            System.out.println("\t"+interactorIdA+" interacts with "+interactorIdB);
        }
    }
}