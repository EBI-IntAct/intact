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

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchange;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchangeFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 * Example of how to import data from PSI-MI XML 2.5 files.
 *
 * @version $Id$
 */
public class ImportPsiData {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, using the default configuration found in the file hsql.spring.xml..
        IntactContext.initContext(new String[] {"/META-INF/kickstart.spring.xml"});

        // Once an IntactContext has been initialized, we can access to it by getting the current instance
        IntactContext intactContext = IntactContext.getCurrentInstance();

        // we get a sample file from the resources folder of the project
        File fileToImport = new File(ImportPsiData.class.getResource("/intact_2006-07-19.xml").getFile());

        // we use the PsiExchange to import an XML into the database.
        PsiExchange psiExchange = PsiExchangeFactory.createPsiExchange(intactContext);
        psiExchange.importIntoIntact(new FileInputStream(fileToImport));
    }
}