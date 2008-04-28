/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.proteins.P08050Mock;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.Collection;

/**
 * NotValidCrc64 Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class NotValidCrc64Test {

    @Test
    public void check() throws Exception {

        Protein protein = P08050Mock.getMock();
        NotValidCrc64 rule = new NotValidCrc64();
        Collection<GeneralMessage> messages = rule.check( protein );
        assertEquals( 0, messages.size() );

        protein.setSequence( "POISSON" );
        messages = rule.check( protein );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.PROTEIN_INCORRECT_CRC64, message.getMessageDefinition() );
        }


    }

}