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
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class MutationAnalysisFeature implements Rule{

    private static final String DESCRIPTION = "This/those Feature(s) are characterizing deletion of more then 2 " +
            "amino-acid and have their CvFeatureIdentification set to " + CvFeatureIdentification.DELETION_ANALYSIS;
    private static final String SUGGESTION = "";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityCheckerException {
        MethodArgumentValidator.isValidArgument(intactObject, Feature.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Feature feature = (Feature) intactObject;

        //feature.getCvFeatureType() can't return null
        CvObjectXref cvFeatureTypeIdentity = CvObjectUtils.getPsiMiIdentityXref(feature.getCvFeatureType());

        if(CvFeatureType.MUTATION_DECREASING_MI_REF.equals(cvFeatureTypeIdentity.getPrimaryId()) ||
                CvFeatureType.MUTATION_INCREASING_MI_REF.equals(cvFeatureTypeIdentity.getPrimaryId()) ||
                CvFeatureType.MUTATION.equals(cvFeatureTypeIdentity.getPrimaryId()) ||
                CvFeatureType.MUTATION_DISRUPTING_MI_REF.equals(cvFeatureTypeIdentity.getPrimaryId())
                ){
            Collection<Range> ranges = feature.getRanges();
            for(Range range : ranges){
                // tointervalstart - r.fromintervalend
                int width = range.getToIntervalStart() - range.getFromIntervalEnd();
                if(width > 2){
                    messages.add(new GeneralMessage(DESCRIPTION, GeneralMessage.AVERAGE_LEVEL,SUGGESTION, feature));
                }
            }
        }
        return messages;
    }
}