/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.UniprotMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.proteins.P08050Mock;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ProteinIdentityCountTest {

    @Test
    public void check() throws Exception {

        Protein protein = P08050Mock.getMock();
        ProteinIdentityCount rule = new ProteinIdentityCount();
        Collection<GeneralMessage> messages = rule.check(protein);
        assertEquals(0,messages.size());

        InteractorXref xref = XrefMock.getMock(InteractorXref.class, UniprotMock.getMock(), IdentityMock.getMock(), "P12345");
        protein.addXref(xref);
        messages = rule.check(protein);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(MessageDefinition.PROTEIN_UNIPROT_MULTIPLE_XREF, message.getMessageDefinition());
        }

        protein.setXrefs(new ArrayList<InteractorXref>());
        messages = rule.check(protein);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(MessageDefinition.PROTEIN_UNIPROT_NO_XREF, message.getMessageDefinition());
        }
    }



}