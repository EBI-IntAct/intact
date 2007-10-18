/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.cvobject;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = CvObject.class)

public class CvInteractionWithoutUniprotDrExport implements Rule<CvObject> {

    private static final String DESCRIPTION = "This/these CvInteraction have uniprot-dr-export annotation";
    private static final String SUGGESTION = "Add a uniprot-dr-export annotation";

    public Collection<GeneralMessage> check(CvObject cvObject) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if(cvObject instanceof CvInteraction){
            if (!hasUniprotDrExportAnnotation(cvObject)){
                messages.add(new GeneralMessage(DESCRIPTION, MessageLevel.ERROR, SUGGESTION, cvObject ) );
            }
        }

        return messages;
    }

    private boolean hasUniprotDrExportAnnotation(CvObject cvObject ) {
        Collection<Annotation> annotations = cvObject.getAnnotations();
        for(Annotation annotation : annotations){
            if(CvTopic.UNIPROT_DR_EXPORT.equals(annotation.getCvTopic().getShortLabel())){
                return true;
            }
        }
        return false;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getSuggestion() {
        return SUGGESTION;
    }
}