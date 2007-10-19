/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;
import uk.ac.ebi.intact.util.Crc64;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Protein.class, group = RuleGroup.INTACT )
public class NotValidCrc64 implements Rule<Protein> {

    public Collection<GeneralMessage> check(Protein protein) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        String sequence = protein.getSequence();
        if (sequence != null) {
            String calculatedCrc64 = Crc64.getCrc64(sequence);
            String storedCrc64 = protein.getCrc64();
            if(!calculatedCrc64.equals(storedCrc64)){
                messages.add(new GeneralMessage(MessageDefinition.PROTEIN_INCORRECT_CRC64, protein));
            }
        }
        return messages;
    }
}