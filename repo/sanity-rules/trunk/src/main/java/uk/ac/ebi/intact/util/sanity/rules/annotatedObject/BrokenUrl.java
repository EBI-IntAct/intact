/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.annotatedObject;

import uk.ac.ebi.intact.util.sanity.rules.Rule;
import uk.ac.ebi.intact.util.sanity.rules.util.MethodArgumentValidator;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.util.sanity.rules.messages.AnnotationMessage;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class BrokenUrl  implements Rule {
    private static final String DESCRIPTION = "This/those Url(s) is/are not valid";
    private static final String SUGGESTION = "Could be just momentarily broken";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityCheckerException {
        MethodArgumentValidator.isValidArgument(intactObject, AnnotatedObject.class);

        AnnotatedObject annotatedObject = (AnnotatedObject) intactObject;

        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();



        Collection<Annotation> annotations = annotatedObject.getAnnotations();
        for(Annotation annotation : annotations){
            String topicMi = null;
            CvObjectXref topicIdentityXref = CvObjectUtils.getPsiMiIdentityXref(annotation.getCvTopic());
            if(topicIdentityXref != null){
                topicMi = topicIdentityXref.getPrimaryId();
            }
            if(CvTopic.URL_MI_REF.equals(topicMi)){

                String urlString = annotation.getAnnotationText();

                HttpURL httpUrl = null;

                //Creating the httpUrl object corresponding to the the url string contained in the annotation
                try {
                    httpUrl = new HttpURL( urlString );
                } catch ( URIException e ) {
                    messages.add(new GeneralMessage(DESCRIPTION,GeneralMessage.LOW_LEVEL,SUGGESTION, annotatedObject));
                    return messages;
                }

                // If httpUrl is not null, get the method corresponding to the uri, execute if and analyze the
                // status code to know whether the url is valide or not.
                if ( httpUrl != null ) {
                    HttpClient client = new HttpClient();
                    HttpMethod method = null;
                    try {
                        method = new GetMethod( urlString );
                    } catch ( IllegalArgumentException e ) {
                        messages.add(new AnnotationMessage(DESCRIPTION,GeneralMessage.LOW_LEVEL,SUGGESTION, annotatedObject,annotation));
                        return messages;//e.printStackTrace();
                        //System.out.println("Couldn't get method uri" + urlString);
                        //retrieveObject(annotationBean);
                    }
                    int statusCode = -1;
                    if ( method != null ) {
                        try {
                            statusCode = client.executeMethod( method );
                        } catch ( IOException e ) {
                            messages.add(new GeneralMessage(DESCRIPTION,GeneralMessage.LOW_LEVEL,SUGGESTION, annotatedObject));
                            return messages;
                        }
                        //retrieveObject(annotationBean);
                    }
                    if ( statusCode != -1 ) {
                        if ( statusCode >= 300 && statusCode < 600 ) {
                            messages.add(new GeneralMessage(DESCRIPTION,GeneralMessage.LOW_LEVEL,SUGGESTION, annotatedObject));
                            return messages;
                            //retrieveObject(annotationBean);
                        }
                    }
                }
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