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
package uk.ac.ebi.intact.dataexchange.psimi.xml.exchange;

import psidev.psi.mi.xml.PsimiXmlReader;
import psidev.psi.mi.xml.model.EntrySet;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

import java.io.File;
import java.io.InputStream;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractPsiExchangeTest extends IntactBasicTestCase {

    private static final String INTACT_FILE = "/xml/intact_2006-07-19.xml";
    private static final String MINT_FILE = "/xml/mint_2006-07-18.xml";
    private static final String DIP_FILE = "/xml/dip_2006-11-01.xml";
    private static final String MOLCOLN_FILE = "/xml/MolcolnTest.xml";

    protected File getIntactFile() {
        return new File(AbstractPsiExchangeTest.class.getResource(INTACT_FILE).getFile());
    }

    protected File getMintFile() {
        return new File(AbstractPsiExchangeTest.class.getResource(MINT_FILE).getFile());
    }

    protected File getDipFile() {
        return new File(AbstractPsiExchangeTest.class.getResource(DIP_FILE).getFile());
    }

    protected InputStream getIntactStream() {
         return AbstractPsiExchangeTest.class.getResourceAsStream(INTACT_FILE);
    }

    protected InputStream getMintStream() {
         return AbstractPsiExchangeTest.class.getResourceAsStream(MINT_FILE);
    }

    protected InputStream getDipStream() {
         return AbstractPsiExchangeTest.class.getResourceAsStream(DIP_FILE);
    }

    protected InputStream getMolcolnStream() {
         return AbstractPsiExchangeTest.class.getResourceAsStream(MOLCOLN_FILE);
    }

    protected EntrySet getIntactEntrySet() throws Exception{
        PsimiXmlReader reader = new PsimiXmlReader();
        return reader.read(getIntactStream());
    }

    protected EntrySet getMintEntrySet() throws Exception{
        PsimiXmlReader reader = new PsimiXmlReader();
        return reader.read(getMintStream());
    }

    protected EntrySet getDipEntrySet() throws Exception{
        PsimiXmlReader reader = new PsimiXmlReader();
        return reader.read(getDipStream());
    }

     protected EntrySet getMolcolnEntrySet() throws Exception{
        PsimiXmlReader reader = new PsimiXmlReader();
        return reader.read(getMolcolnStream());
    }

}
