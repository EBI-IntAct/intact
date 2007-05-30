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
import static org.easymock.classextension.EasyMock.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import psidev.psi.mi.xml.PsimiXmlWriter;
import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.EntrySet;
import psidev.psi.mi.xml.model.Interaction;
import psidev.psi.mi.xml.model.Names;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.psixml.commons.model.IntactEntry;

import java.util.Arrays;
import java.util.Collections;

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
    }

    @Ignore
    public void generalRountrip() throws Exception {
        Interaction mockInteraction = createNiceMock(Interaction.class);
        uk.ac.ebi.intact.model.Interaction mockIntactInteraction = createNiceMock(uk.ac.ebi.intact.model.Interaction.class);
        InteractionConverter mockInteractionConverter = createNiceMock(InteractionConverter.class);

        expect(entry.getInteractions()).andReturn(Arrays.asList(mockInteraction, mockInteraction)).once();
        expect(mockInteraction.getNames()).andReturn(createNiceMock(Names.class)).anyTimes();
        expect(mockInteraction.getExperiments()).andReturn(Collections.EMPTY_LIST).once();
        expect(mockInteraction.getParticipants()).andReturn(Collections.EMPTY_LIST).once();
        expect(mockInteraction.getInteractionTypes()).andReturn(Collections.EMPTY_LIST).once();
        expect(mockInteractionConverter.psiToIntact(mockInteraction)).andReturn(new uk.ac.ebi.intact.model.InteractionImpl()).atLeastOnce();

        replay(entry);
        replay(mockInteractionConverter);
        replay(mockInteraction);


        IntactEntry intactEntry = entryConverter.psiToIntact(entry);
        Entry convertedEntry = entryConverter.intactToPsi(intactEntry);


        verify(entry);
        verify(mockInteractionConverter);

        assertEquals(2, convertedEntry.getInteractions());
    }

    @Test
    public void entryToIntactDefault() throws Exception {

        //InputStream is = EntryConverterTest.class.getResourceAsStream(INTACT_FILE);
        //PsimiXmlReader reader = new PsimiXmlReader();
        //EntrySet entrySet = reader.read(is);

        //EntryConverter entryConverter = new EntryConverter(IntactContext.getCurrentInstance().getInstitution());

        //Entry psiEntry = entrySet.getEntries().iterator().next();

        PsiMockFactory.setMinChildren(1);
        PsiMockFactory.setMaxChildren(1);

        Entry psiEntry = PsiMockFactory.createMockEntry();

        PsimiXmlWriter writer = new PsimiXmlWriter();
        System.out.println(writer.getAsString(new EntrySet(Arrays.asList(psiEntry), 2, 3, 5)));

        IntactEntry intactEntry = entryConverter.psiToIntact(psiEntry);

        Entry convEntry = entryConverter.intactToPsi(intactEntry);
        EntrySet convEntrySet = new EntrySet(Arrays.asList(convEntry), 2, 5, 3);

        System.out.println(writer.getAsString(convEntrySet));

        /*
     System.out.println("Interactions: " + intactEntry.getInteractions().size());

     for (Interaction inter : intactEntry.getInteractions()) {
         System.out.println(inter);

         for (Xref xref : inter.getXrefs()) {
             System.out.println("\t\tXref: " + xref);
         }

         CvInteractionType intType = inter.getCvInteractionType();
         if (intType != null) {
             System.out.println("\t\tCV Interaction type: " + intType);
             for (Xref xref : intType.getXrefs()) {
                 System.out.println("\t\t\tXref: " + xref);
             }
         }

         System.out.println("\tExperiment: " + inter.getExperiments().iterator().next());
     }
        */
    }
}