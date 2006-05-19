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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility methods for a publication.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2006</pre>
 */
public class PublicationHelper {

    /**
     * Simple representation of a Date.
     * <p/>
     * Will be used to name our IMEx files.
     */
    private static final SimpleDateFormat SIMPLE_DATE_FORMATER = new SimpleDateFormat( "yyyy-MM-dd" );

    public static final String IMEX_EXPORT_SEPARATOR = ":";

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

    /**
     * Build a well formatted imex-exported annotation.
     *
     * @param helper database access.
     * @param text   the text to append in the comment (format: YYYY-MM-DD: text).
     *
     * @return an annotation.
     *
     * @throws IntactException
     */
    public static Annotation buildImexExportedAnnotation( IntactHelper helper, String text ) throws IntactException {
        CvTopic imexExported = CvHelper.getImexExported( helper );
        Annotation annot = new Annotation( helper.getInstitution(), imexExported );
        String today = getTodaySimpleDate();

        String annotText = today;
        if ( text != null ) {
            annotText += IMEX_EXPORT_SEPARATOR + " " + text.trim();
        }

        annot.setAnnotationText( annotText );
        return annot;
    }

    /**
     * Return today's date in a simple format.
     *
     * @return
     */
    public static String getTodaySimpleDate() {
        return SIMPLE_DATE_FORMATER.format( new Date() ); // YYYY-MM-DD;
    }

    /**
     * Get a list of Annotation having CvTopic( imex-exported ) and sort them chonologicaly.
     *
     * @param helper      database access.
     * @param publication the publication of interrest.
     *
     * @return a sorted collection of exported date.
     *
     * @throws IntactException
     */
    public static List<Annotation> getExportHistory( IntactHelper helper, Publication publication ) throws IntactException {
        CvTopic imexExported = CvHelper.getImexExported( helper );

        List<Annotation> export = new ArrayList<Annotation>( publication.getAnnotations().size() );

        for ( Annotation annotation : publication.getAnnotations() ) {
            if ( imexExported.equals( annotation.getCvTopic() ) ) {
                export.add( annotation );
            }
        }

        // sort by date
        Collections.sort( export, new Comparator<Annotation>() {
            public int compare( Annotation o1, Annotation o2 ) {
                String t1 = o1.getAnnotationText();
                String t2 = o2.getAnnotationText();

                int idx1 = t1.indexOf( IMEX_EXPORT_SEPARATOR );
                if ( idx1 == -1 ) {
                    idx1 = t1.length();
                }
                String dt1 = t1.substring( 0, idx1 );

                int idx2 = t2.indexOf( IMEX_EXPORT_SEPARATOR );
                if ( idx2 == -1 ) {
                    idx2 = t2.length();
                }
                String dt2 = t2.substring( 0, idx2 );

                Date d1 = null;
                try {
                    d1 = SIMPLE_DATE_FORMATER.parse( dt1 );
                } catch ( ParseException e ) {
                    e.printStackTrace();
                }
                Date d2 = null;
                try {
                    d2 = SIMPLE_DATE_FORMATER.parse( dt2 );
                } catch ( ParseException e ) {
                    e.printStackTrace();
                }

                if ( d1 == null ) {
                    return -1;
                }

                if ( d2 == null ) {
                    return 1;
                }

                return d1.compareTo( d2 );
            }
        } );

        return export;
    }

    public static void showExportHistory( IntactHelper helper, Publication publication ) throws IntactException {

        List<Annotation> export = getExportHistory( helper, publication );

        // display
        for ( Annotation annotation : export ) {
            System.out.println( annotation.getAnnotationText() );
        }
    }

    public static void main( String[] args ) throws IntactException {

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            System.out.println( "Database: " + helper.getDbName() );

            Publication pub = new Publication( helper.getInstitution(), "123" );
            CvTopic imexExported = CvHelper.getImexExported( helper );

            Annotation a1 = new Annotation( helper.getInstitution(), imexExported, "2005-01-02" );
            Annotation a2 = new Annotation( helper.getInstitution(), imexExported, "2007-01-02" );
            Annotation a3 = new Annotation( helper.getInstitution(), imexExported, "2007-01-02: blablabla" );
            Annotation a4 = new Annotation( helper.getInstitution(), imexExported, "2003-01-02" );

            pub.addAnnotation( a1 );
            pub.addAnnotation( a2 );
            pub.addAnnotation( a3 );
            pub.addAnnotation( a4 );

            showExportHistory( helper, pub );

        } finally {
            if ( helper != null ) {
                helper.closeStore();
            }
        }

    }

}