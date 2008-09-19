/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.*;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Checks on Annotation having a CvTopic not having an Annotation used-in-class.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */

@SanityRule(target = AnnotatedObject.class, group = RuleGroup.INTACT)
public class AnnotationNotUsingAnAppropriateCvTopic implements Rule<AnnotatedObject> {

    private static Map<String,Annotation> CACHE = new HashMap<String,Annotation>();

    public Collection<GeneralMessage> check(AnnotatedObject intactObject) throws SanityRuleException {

        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        
        Collection<Annotation> annotations = intactObject.getAnnotations();
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
                messages.add(new GeneralMessage( MessageDefinition.TOPIC_WITHOUT_USED_IN_CLASS, cvTopic ) );
            }else{
                // annotation.getAnnotationText() can't be null as this is checked in the getUsedInClassAnnotation
                // method.
                if(!usedInClass.getAnnotationText().contains(intactObject.getClass().getSimpleName())){
                    messages.add(new AnnotationMessage(MessageDefinition.ANNOTATION_WITH_WRONG_TOPIC,
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