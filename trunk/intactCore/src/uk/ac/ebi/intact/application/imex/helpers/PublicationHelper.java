/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.imex.helpers;

import uk.ac.ebi.intact.application.imex.id.IMExIdTransformer;
import uk.ac.ebi.intact.application.imex.id.IMExRange;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Publication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for a publication.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2006</pre>
 */
public class PublicationHelper {

    public static boolean hasImexRange( IntactHelper helper, Publication publication ) {
        return false;
    }

    public static Publication loadPublication( IntactHelper helper, String pmid ) throws IntactException {

        System.out.println( "Searching for Publication by pmid: " + pmid );
        // create a publication
        Collection<Publication> publications = helper.search( Publication.class, "pmid", pmid );

        System.out.println( publications.size() + " publication(s) found." );
        for ( Publication publication : publications ) {
            System.out.println( publication );
            return publication;
        }

        return null;
    }

    /**
     * Add an annotation To a Publication. It decribes the requested range of IMEx ID from Key Assigner.
     *
     * @param helper      database access
     * @param publication the publication onto which we add a new annotation
     * @param imexRange   the range
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     */
    public static void addRequestedAnnotation( IntactHelper helper, Publication publication, IMExRange imexRange ) throws IntactException {

        CvTopic imexRangeRequested = CvHelper.getImexRangeRequested( helper );

        String simpleRange = IMExIdTransformer.formatSimpleRange( imexRange );
        Annotation requested = new Annotation( helper.getInstitution(), imexRangeRequested, simpleRange );

        if ( publication.getAnnotations().contains( requested ) ) {

            System.out.println( "That publication had already the Annotation(" + imexRangeRequested.getShortLabel() +
                                ", " + simpleRange + ")." );

        } else {
            // not in yet, add it...
            helper.create( requested );

            publication.addAnnotation( requested );
            helper.update( publication );

            System.out.println( "Added Annotation(" + imexRangeRequested.getShortLabel() + ", " + simpleRange +
                                ") to Publication(" + publication.getPmid() + ")" );
        }
    }

    /**
     * Collect all requested IDs on a specific publication and build a collection of IMExRange.
     *
     * @param helper      database access
     * @param publication the publication
     *
     * @return a non null collection of IMExRange, may be empty.
     *
     * @throws IntactException
     */
    public static List<IMExRange> getRequestedRanges( IntactHelper helper, Publication publication ) throws IntactException {
        CvTopic imexRangeRequested = CvHelper.getImexRangeRequested( helper );

        List<IMExRange> ranges = new ArrayList<IMExRange>( 2 );

        for ( Annotation annotation : publication.getAnnotations() ) {
            if ( imexRangeRequested.equals( annotation.getCvTopic() ) ) {
                // found one
                IMExRange range = IMExIdTransformer.parseSimpleRange( annotation.getAnnotationText() );
                ranges.add( range );
            }
        }

        return ranges;
    }

    /**
     * Collect all assigned IDs on a specific publication and build a collection of IMExRange.
     * <p/>
     * This assumes the publication has been updated correctly.
     *
     * @param helper      database access
     * @param publication the publication
     *
     * @return a non null collection of IMExRange, may be empty.
     *
     * @throws IntactException
     */
    public static List<IMExRange> getAssignedRanges( IntactHelper helper, Publication publication ) throws IntactException {
        CvTopic imexRangeAssigned = CvHelper.getImexRangeAssigned( helper );

        List<IMExRange> ranges = new ArrayList<IMExRange>( 2 );

        for ( Annotation annotation : publication.getAnnotations() ) {
            if ( imexRangeAssigned.equals( annotation.getCvTopic() ) ) {
                // found one
                IMExRange range = IMExIdTransformer.parseSimpleRange( annotation.getAnnotationText() );
                ranges.add( range );
            }
        }

        return ranges;
    }

    /**
     * Add an annotation To a Publication. It decribes the requested range of IMEx ID from Key Assigner.
     *
     * @param helper      database access
     * @param publication the publication onto which we add a new annotation
     * @param imexRange   the range
     *
     * @throws IntactException
     */
    public static void addAssignedAnnotation( IntactHelper helper, Publication publication, IMExRange imexRange ) throws IntactException {

        CvTopic imexRangeAssigned = CvHelper.getImexRangeAssigned( helper );

        String simpleRange = IMExIdTransformer.formatSimpleRange( imexRange );
        Annotation requested = new Annotation( helper.getInstitution(), imexRangeAssigned, simpleRange );

        if ( publication.getAnnotations().contains( requested ) ) {

            System.out.println( "That publication had already the Annotation(" + imexRangeAssigned.getShortLabel() +
                                ", " + simpleRange + ")." );

        } else {
            // not in yet, add it...
            helper.create( requested );

            publication.addAnnotation( requested );
            helper.update( publication );

            System.out.println( "Added Annotation(" + imexRangeAssigned.getShortLabel() + ", " + simpleRange +
                                ") to Publication(" + publication.getPmid() + ")" );
        }
    }
}