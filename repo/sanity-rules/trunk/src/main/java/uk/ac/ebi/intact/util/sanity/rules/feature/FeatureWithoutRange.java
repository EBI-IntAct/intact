/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.feature;

import uk.ac.ebi.intact.util.sanity.rules.Rule;
import uk.ac.ebi.intact.util.sanity.rules.util.MethodArgumentValidator;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.util.sanity.annotation.SanityRule;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Range;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Feature.class)

public class FeatureWithoutRange implements Rule {

    private static final String DESCRIPTION = "This/those Feature(s) have no range. ";
    private static final String SUGGESTION = "Edit the feature(s) and add a range.";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityCheckerException {
        MethodArgumentValidator.isValidArgument(intactObject, Feature.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Feature feature = (Feature) intactObject;
        Collection<Range> ranges = feature.getRanges();
        if(0 == ranges.size()){
            messages.add(new GeneralMessage(DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, feature));
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