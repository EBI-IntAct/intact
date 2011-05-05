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

package intact.exercise.chapter2.exercise3;

import psidev.psi.mi.search.index.PsimiIndexWriter;
import psidev.psi.mi.search.util.DefaultDocumentBuilder;

import java.io.File;
import java.io.InputStream;

/**
 * Question 1: This library offers the possibility to to index a MITAB dataset using Lucene.
 * This enables users to run local MIQL queries, thus easing data processing.
 * Write a program that indexes the provided MITAB data file.
 *
 * @see psidev.psi.mi.search.index.PsimiIndexWriter
 * @see psidev.psi.mi.search.index.PsimiIndexWriter#index(org.apache.lucene.store.Directory, java.io.InputStream, boolean, boolean)
 */
public class Q1_IndexMitabFile {
    public static void main( String[] args ) throws Exception {

        // Load a MITAB2.5 file stored locally
        InputStream psimitabFileStream = Q1_IndexMitabFile.class.getResourceAsStream("/samples/mitab/17129783.txt");

        PsimiIndexWriter indexWriter = new PsimiIndexWriter(new DefaultDocumentBuilder());
        File indexDirectory = new File( "mitab.index" );

        // TODO start here - index the file using the indexWriter
    }
}