/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.protein;

import uk.ac.ebi.intact.util.sanity.rules.Rule;
import uk.ac.ebi.intact.util.sanity.rules.util.MethodArgumentValidator;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.util.Crc64;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Protein;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class NotValidCrc64 implements Rule {

    private static final String DESCRIPTION = "This those Proteins have a crc64 that does not correspond to their sequence.";
    private static final String SUGGESTION = "Ask a developper to fix that.";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityCheckerException {
        MethodArgumentValidator.isValidArgument(intactObject, Protein.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Protein protein = (Protein) intactObject;
        String sequence = protein.getSequence();
        String calculatedCrc64 = Crc64.getCrc64(sequence);
        String storedCrc64 = protein.getCrc64();
        if(!calculatedCrc64.equals(storedCrc64)){
            messages.add(new GeneralMessage(DESCRIPTION, GeneralMessage.LOW_LEVEL, SUGGESTION, protein));
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