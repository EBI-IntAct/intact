/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;

import java.util.Collection;

/**
 * Utilities for updating Annotations.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.1.2
 */
public class AnnotationUpdaterUtils {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( AnnotationUpdaterUtils.class );

    /**
     * Add an annotation to an annotated object. <br> We check if that annotation is not already existing, if so, we
     * don't record it.
     *
     * @param current    the annotated object to which we want to add an Annotation.
     * @param annotation the annotation to add the Annotated object
     */
    public static void addNewAnnotation( AnnotatedObject current, final Annotation annotation ) {

        // TODO: what if an annotation is already existing ... should we use a single one ?
        // YES ! we should search dor it first and reuse existing Annotation

        // Make sure the alias does not yet exist in the object
        Collection<Annotation> annotations = current.getAnnotations();
        for ( Annotation anAnnotation : annotations ) {
            if ( anAnnotation.equals( annotation ) ) {
                return; // already in, exit
            }
        }

        // add the alias to the AnnotatedObject
        current.addAnnotation( annotation );

        try {
            IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().persist( annotation );
            log.debug( "ADD " + annotation + " to: " + current.getShortLabel() );
        } catch ( Exception e_alias ) {
            if ( log != null ) {
                log.error( "Error when creating an Annotation for protein " + current, e_alias );
            }
        }
    }
}