/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.psimitab.search;

import org.apache.lucene.store.Directory;
import psidev.psi.mi.search.Searcher;
import psidev.psi.mi.tab.converter.txt2tab.MitabLineException;
import psidev.psi.mi.xml.converter.ConverterException;

import java.io.IOException;
import java.io.InputStream;

/**
 * PSIMITAB Test Helper.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: TestHelper.java 801 2007-10-09 14:01:14Z skerrien $
 */
public abstract class TestHelper {

    public static Directory createIndexFromResource(String resourcePath) throws IOException, ConverterException, MitabLineException {
        InputStream is = TestHelper.class.getResourceAsStream(resourcePath);
        return Searcher.buildIndexInMemory(is, true, true, new IntactPsimiTabIndexWriter());
    }    
}
