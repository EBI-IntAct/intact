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

package intact.exercise.chapter2.exercise2;

import psidev.psi.mi.tab.PsimiTabException;

import java.io.File;
import java.io.IOException;

/**
 * Question 1: Can you write a class that reads a MITAB data file/stream and print out the count of interactions parsed?
 *
 */
public class Q1_ReadWholeFile {
    public static void main( String[] args ) throws PsimiTabException, IOException {

        // Prepare the input MITAB file
        File intputFile = new File( Q1_ReadWholeFile.class.getResource( "/samples/mitab/18189341.txt" ).getFile() );

        System.out.println( "Reading MITAB data from: " + intputFile.getAbsolutePath() );

        // TODO start here - Instantiate the reader class that supports the Standard MITAB data format and read the file

    }
}