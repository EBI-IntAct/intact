/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.commons.util;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses the current Database connexion to create a list of Annotations' Topic not to be displayed in the public
 * interface. Those CvTopics are annotated with the term 'no-export'.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Jun-2005</pre>
 */
public class AnnotationFilter {

    protected transient static final Logger logger = Logger.getLogger( AnnotationFilter.class );

    /**
     * Keeps all CvTopics to be filtered out.
     */
    private Set<CvTopic> filteredTopics = null;

    private static AnnotationFilter ourInstance = new AnnotationFilter();

    public static AnnotationFilter getInstance() {
        return ourInstance;
    }

    /**
     * Loads all CvTopics that should not be shown on the public interface.
     */
    private AnnotationFilter() {

        IntactHelper helper = null;

        try {

            logger.debug( "Initializing which CvTopic should be filtered out." );

            helper = new IntactHelper();

            logger.debug( "Helper created (User: " + helper.getDbUserName() + " " +
                          "Database: " + helper.getDbName() + ")" );

            // search for the CvTopic no-export
            CvTopic noExport = helper.getObjectByLabel( CvTopic.class, CvTopic.NO_EXPORT );

            if ( noExport != null ) {

                // load all CvTopics
                Collection<CvTopic> cvTopics = helper.search( CvTopic.class, "ac", null );

                // select those that have an Annotation( no-export )
                for ( CvTopic cvTopic : cvTopics ) {
                    for ( Annotation annotation : cvTopic.getAnnotations() ) {
                        if ( noExport.equals( annotation.getCvTopic() ) ) {
                            if ( filteredTopics == null ) {
                                filteredTopics = new HashSet<CvTopic>( 8 );
                            }

                            logger.debug( "CvTopic( " + cvTopic.getShortLabel() + " )" );
                            filteredTopics.add( cvTopic );
                        }
                    }
                }
            }

            if ( filteredTopics == null ) {
                filteredTopics = Collections.EMPTY_SET;
            }

            logger.debug( filteredTopics.size() + " CvTopic" + ( filteredTopics.size() > 1 ? "s" : "" ) + " filtered." );

        } catch ( IntactException e ) {
            logger.error( "Could not load the CvTopic to filter out on the public view.", e );
        } catch ( SQLException e ) {
            e.printStackTrace();
        } catch ( LookupException e ) {
            e.printStackTrace();
        } finally {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                } catch ( IntactException e ) {
                    logger.error( "Could not close the IntactHeper.", e );
                }
            }

            // if the connection to the database failed, the set is still null.
            if ( filteredTopics == null ) {
                filteredTopics = Collections.EMPTY_SET;
            }
        }
    }

    /**
     * Checks if the given CvTopic is not to be exported.
     *
     * @param topic the CvTopic we want to know if we have to display.
     *
     * @return true if the given CvTopic isn't supposed to be shown on a public view, otherwise false.
     */
    public boolean isFilteredOut( CvTopic topic ) {

        return filteredTopics.contains( topic );
    }

    /**
     * Checks if the CvTopic of the given annotation is not to be exported.
     *
     * @param annotation the annotation we want to know if we have to display.
     *
     * @return true if an Annotation isn't supposed to be shown on a public view, otherwise false.
     */
    public boolean isFilteredOut( Annotation annotation ) {

        if ( annotation != null ) {

            // an annotation must have a CvTopic (non null)
            if ( filteredTopics.contains( annotation.getCvTopic() ) ) {
                return true;
            }
        }

        // no annotation available
        return false;
    }

    public Set getFilters() {
        Set result = new HashSet( filteredTopics.size() );

        for ( CvTopic cvTopic : filteredTopics ) {
            result.add( cvTopic );
        }

        return result;
    }
}
