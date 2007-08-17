/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.AnnotationMessage;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.util.MethodArgumentValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Annotation.class)

public class AnnotationNotUsingAnAppropriateCvTopic implements Rule {

    private static final String CVTOPIC_NOT_APPROPRIATE_MSG_DESCRIPTION = "This/those object(s) have annotation using CvTopic which are hidden or obsolete Cvs";
    private static final String CVTOPIC_NOT_APPROPRIATE_MSG_SUGGESTION = "Change cvTopic";

    private static final String CVTOPIC_WITHOUT_USED_IN_CLASS_MSG_DESCRIPTION = "CvTopic having no used-in-class annotation";
    private static final String CVTOPIC_WITHOUT_USED_IN_CLASS_MSG_SUGGESTION = "Add a used-in-class annotation";

    private static Map<String,Annotation> CACHE = new HashMap();



    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityRuleException {

        MethodArgumentValidator.isValidArgument(intactObject, AnnotatedObject.class);

        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();


        AnnotatedObject annotatedObject = (AnnotatedObject) intactObject;
        Collection<Annotation> annotations = annotatedObject.getAnnotations();
        for(Annotation annotation  : annotations){
            CvTopic cvTopic = annotation.getCvTopic();
            Annotation usedInClass = null;
            if(CACHE.containsKey(cvTopic)){
                usedInClass = CACHE.get(cvTopic.getShortLabel());
            }else{
                usedInClass = getUsedInClassAnnotation(cvTopic);
            }
            if(usedInClass == null){
                //No usedInClass annotation for this cvTopic
                messages.add(new GeneralMessage(CVTOPIC_WITHOUT_USED_IN_CLASS_MSG_DESCRIPTION,
                        GeneralMessage.AVERAGE_LEVEL,
                        CVTOPIC_WITHOUT_USED_IN_CLASS_MSG_SUGGESTION,
                        cvTopic));
            }else{
                // annotation.getAnnotationText() can't be null as this is checked in the getUsedInClassAnnotation
                // method.
                if(!annotation.getAnnotationText().contains(intactObject.getClass().getSimpleName())){
                    messages.add(new AnnotationMessage(CVTOPIC_NOT_APPROPRIATE_MSG_DESCRIPTION,
                            GeneralMessage.LOW_LEVEL,
                            CVTOPIC_NOT_APPROPRIATE_MSG_SUGGESTION,
                            intactObject,
                            annotation));
                }
            }
        }
        return messages;

    }

    private Annotation getUsedInClassAnnotation(CvTopic cvTopic){
        Collection<Annotation> annotations = cvTopic.getAnnotations();
        for(Annotation annotation : annotations){
            if(CvTopic.USED_IN_CLASS.equals(annotation.getCvTopic().getShortLabel())){
                if(annotation.getAnnotationText() == null){
                    return null;
                }
                CACHE.put(cvTopic.getShortLabel(),annotation);
                return annotation;
            }
        }
        return null;
    }


}