/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons.rules;

import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;

import java.util.Collection;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Definition of a Rule.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public abstract class Rule<T extends IntactObject> {

    private static final Log log = LogFactory.getLog( Rule.class );

    public static final String IGNORE_SANITY_RULE = "ignore-sanity-rule";

    public abstract Collection<GeneralMessage> check(T intactObject) throws SanityRuleException;

    /**
     * If this method returns true, the correcponsing rule should not add the message with the given key.
     * @param message the message that the rule can generate.
     * @return
     */
    public boolean isIgnored( T intactObject, final MessageDefinition message ){

        if( intactObject instanceof AnnotatedObject ) {
           AnnotatedObject ao = (AnnotatedObject) intactObject;
            for (Annotation annot : ao.getAnnotations() ) {
                if( IGNORE_SANITY_RULE.equals( annot.getCvTopic().getShortLabel() ) ) {
                    // format: "key - reason" or "key"
                    final String text = annot.getAnnotationText();
                    if( text != null && text.trim().startsWith( message.getKey() ) ) {
                        if (log.isDebugEnabled()) {
                            log.debug( intactObject.getClass().getSimpleName() +
                                    " annotated to ignore rules with key " + text  );
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }
}