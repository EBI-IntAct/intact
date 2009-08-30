/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.methods.GetMethod;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.AnnotationMessage;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Rule that checks on broken URL.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = AnnotatedObject.class, group = {RuleGroup.INTACT, RuleGroup.IMEX} )

public class BrokenUrl extends Rule<AnnotatedObject> {

    public Collection<GeneralMessage> check( AnnotatedObject ao ) throws SanityRuleException {

        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if( ! isIgnored( ao, MessageDefinition.BROKEN_URL ) ) {
            for ( Annotation annotation : ao.getAnnotations() ) {
                String topicMi = null;
                CvObjectXref topicIdentityXref = CvObjectUtils.getPsiMiIdentityXref( annotation.getCvTopic() );
                if ( topicIdentityXref != null ) {
                    topicMi = topicIdentityXref.getPrimaryId();
                }
                if ( CvTopic.URL_MI_REF.equals( topicMi ) ) {

                    String urlString = annotation.getAnnotationText();

                    HttpURL httpUrl = null;

                    //Creating the httpUrl object corresponding to the the url string contained in the annotation
                    try {
                        httpUrl = new HttpURL( urlString );
                    } catch ( Exception e ) {
                        messages.add( new AnnotationMessage( MessageDefinition.BROKEN_URL, ao, annotation ) );
                        return messages;
                    }

                    // If httpUrl is not null, get the method corresponding to the uri, execute if and analyze the
                    // status code to know whether the url is valide or not.
                    if ( httpUrl != null ) {

                        HttpClient client = new HttpClient();
                        HttpMethod method = null;
                        int statusCode = -1;
                        try {
                            method = new GetMethod( urlString );
                            if ( method != null ) {
                                statusCode = client.executeMethod( method );
                            }
                        } catch ( Exception e ) {
                            messages.add( new AnnotationMessage( MessageDefinition.BROKEN_URL, ao, annotation ) );
                            return messages;
                        }

                        if ( statusCode != -1 ) {
                            if ( statusCode >= 300 && statusCode < 600 ) {
                                messages.add( new AnnotationMessage( MessageDefinition.BROKEN_URL, ao, annotation ) );
                                return messages;
                            }
                        }
                    }
                }
            }
        }

        return messages;
    }
}