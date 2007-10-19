/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.cvobject;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.cvDatabases.PubmedMock;
import uk.ac.ebi.intact.mocks.cvInteractions.CoSedimentationMock;
import uk.ac.ebi.intact.mocks.cvInteractions.CvInteractionWithNoAnnotationMock;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class CvInteractionWithoutUniprotDrExportTest {

    @Test
    public void check() throws Exception {
        // Check that if we give the check method a cvInteraction with no annotation it return a message
        CvInteraction cvInteraction = CvInteractionWithNoAnnotationMock.getMock();
        CvInteractionWithoutUniprotDrExport rule = new CvInteractionWithoutUniprotDrExport();
        Collection<GeneralMessage> messages = rule.check(cvInteraction);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(MessageDefinition.INTERACTION_DETECTION_WITHOUT_UNIPROT_EXPORT,message.getMessageDefinition());
        }

        // Check that if we give the check method a cvInteraction with the uniprot-dr-export annotation, it does not
        // return any message
        cvInteraction = CoSedimentationMock.getMock();
        messages = rule.check(cvInteraction);
        assertEquals(0,messages.size());

        // Check that if we give the check method a cvDatabase  it does not
        // return any message
        CvDatabase pubmed = PubmedMock.getMock();
        messages = rule.check(pubmed);
        assertEquals(0,messages.size());
    }
}