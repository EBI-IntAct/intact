/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.util.MethodArgumentValidator;
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

@SanityRule(target = Protein.class)

public class NotValidCrc64 implements Rule {

    private static final String DESCRIPTION = "This those Proteins have a crc64 that does not correspond to their sequence.";
    private static final String SUGGESTION = "Ask a developper to fix that.";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityRuleException {
        MethodArgumentValidator.isValidArgument(intactObject, Protein.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Protein protein = (Protein) intactObject;
        String sequence = protein.getSequence();
        if (sequence != null) {
            String calculatedCrc64 = Crc64.getCrc64(sequence);
            String storedCrc64 = protein.getCrc64();
            if(!calculatedCrc64.equals(storedCrc64)){
                messages.add(new GeneralMessage(DESCRIPTION, GeneralMessage.LOW_LEVEL, SUGGESTION, protein));
            }
        }
        return messages;
    }


    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getSuggestion() {
        return SUGGESTION;
    }
}