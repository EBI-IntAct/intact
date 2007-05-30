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
package uk.ac.ebi.intact.psixml.converter.shared;

import static junit.framework.Assert.assertEquals;
import static org.easymock.classextension.EasyMock.createNiceMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import psidev.psi.mi.xml.PsimiXmlReader;
import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.EntrySet;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.psixml.commons.model.IntactEntry;
import uk.ac.ebi.intact.psixml.converter.util.IdSequenceGenerator;

import java.io.InputStream;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class EntryConverterTest {

    private static final String INTACT_FILE = "/xml/intact_2006-07-19.xml";
    private static final String MINT_FILE = "/xml/mint_2006-07-18.xml";

    private Entry entry;
    private EntryConverter entryConverter;

    @Before
    public void setUp() throws Exception {
        entry = createNiceMock(Entry.class);
        entryConverter = new EntryConverter(createNiceMock(Institution.class));
    }

    @After
    public void tearDown() throws Exception {
        entry = null;
        entryConverter = null;

        IdSequenceGenerator.getInstance().reset();
    }

    @Ignore
    public void mockRountrip() throws Exception {

        Entry beforeRountripEntry = PsiMockFactory.createMockEntry();

        int idNum = IdSequenceGenerator.getInstance().currentId();
        IdSequenceGenerator.getInstance().reset();

        IntactEntry intactEntry = entryConverter.psiToIntact(beforeRountripEntry);
        Entry afterRoundtripEntry = entryConverter.intactToPsi(intactEntry);

        assertEquals(idNum, IdSequenceGenerator.getInstance().currentId());

    }

    @Test
    public void intactFileRoundtrip() throws Exception {

        InputStream is = EntryConverterTest.class.getResourceAsStream(INTACT_FILE);
        PsimiXmlReader reader = new PsimiXmlReader();
        EntrySet entrySet = reader.read(is);

        EntryConverter entryConverter = new EntryConverter(IntactContext.getCurrentInstance().getInstitution());

        Entry beforeRountripEntry = entrySet.getEntries().iterator().next();

        IntactEntry intactEntry = entryConverter.psiToIntact(beforeRountripEntry);
        Entry afterRoundtripEntry = entryConverter.intactToPsi(intactEntry);


        assertEquals(beforeRountripEntry.getInteractions().size(), afterRoundtripEntry.getInteractions().size());
        assertEquals(beforeRountripEntry.getExperiments().size(), afterRoundtripEntry.getExperiments().size());
        assertEquals(beforeRountripEntry.getInteractors().size(), afterRoundtripEntry.getInteractors().size());
    }
}