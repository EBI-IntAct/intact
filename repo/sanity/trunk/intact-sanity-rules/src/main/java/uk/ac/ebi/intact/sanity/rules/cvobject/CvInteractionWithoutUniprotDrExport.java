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
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks that a CvInteraction has an annotation uniprot-dr-export.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule(target = CvObject.class, group = RuleGroup.INTACT )
public class CvInteractionWithoutUniprotDrExport implements Rule<CvObject> {

    private static final String DESCRIPTION = "This/these CvInteraction have uniprot-dr-export annotation";
    private static final String SUGGESTION = "Add a uniprot-dr-export annotation";

    public Collection<GeneralMessage> check(CvObject cvObject) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if(cvObject instanceof CvInteraction){
            if (!hasUniprotDrExportAnnotation(cvObject)){
                messages.add(new GeneralMessage( MessageDefinition.INTERACTION_DETECTION_WITHOUT_UNIPROT_EXPORT,
                                                 cvObject ) );
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
}