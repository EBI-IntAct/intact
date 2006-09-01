/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Update experiment's annotation based on information available in the citation database for a specific pubmedId.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Apr-2006</pre>
 */
public class UpdateExperimentAnnotationsFromPudmed {

    /**
     * Reports on what has been updated in the process.
     */
    public static class UpdateReport {
        private boolean authorListUpdated = false;
        private boolean contactUpdated = false;
        private boolean yearUpdated = false;
        private boolean journalUpdated = false;

        public UpdateReport() {
        }

        public boolean isAuthorListUpdated() {
            return authorListUpdated;
        }

        public void setAuthorListUpdated( boolean authorListUpdated ) {
            this.authorListUpdated = authorListUpdated;
        }

        public boolean isContactUpdated() {
            return contactUpdated;
        }

        public void setContactUpdated( boolean contactUpdated ) {
            this.contactUpdated = contactUpdated;
        }

        public boolean isYearUpdated() {
            return yearUpdated;
        }

        public void setYearUpdated( boolean yearUpdated ) {
            this.yearUpdated = yearUpdated;
        }

        public boolean isJournalUpdated() {
            return journalUpdated;
        }

        public void setJournalUpdated( boolean journalUpdated ) {
            this.journalUpdated = journalUpdated;
        }
    }

    /**
     * Update the given Experiment according to the given pubmed ID.
     *
     * @param experiment the experiment to update
     * @param pubmedId   the pubmed from which we get the information
     *
     * @return an UpdateReport, never null.
     *
     * @throws IntactException
     */
    public static UpdateReport update( Experiment experiment, String pubmedId ) throws IntactException {

        ///////////////////////////
        // checking input params
        if ( experiment == null ) {
            throw new IllegalArgumentException( "you must give a non null experiment." );
        }

        if ( pubmedId == null ) {
            throw new IllegalArgumentException( "you must give a non null pubmed Id." );
        }

        ///////////////////////
        // starting update
        UpdateReport report = new UpdateReport();
        try {
            ExperimentAutoFill eaf = new ExperimentAutoFill( pubmedId );

            //////////////////////////////////////
            // Collecting necessary vocabularies
            CvObjectDao<CvTopic> cvTopicDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvTopic.class);

            CvTopic authorList = cvTopicDao.getByShortLabel(CvTopic.AUTHOR_LIST); // unique

            if ( authorList == null ) {
                throw new IntactException( "Could not find CvTopic(" + CvTopic.AUTHOR_LIST + ") in your intact node. abort update." );
            }

            CvTopic journal = cvTopicDao.getByShortLabel(CvTopic.JOURNAL );        // unique
            if ( journal == null ) {
                throw new IntactException( "Could not find CvTopic(" + CvTopic.JOURNAL + ") in your intact node. abort update." );
            }

            CvTopic year = cvTopicDao.getByShortLabel(CvTopic.PUBLICATION_YEAR );  // unique
            if ( year == null ) {
                throw new IntactException( "Could not find CvTopic(" + CvTopic.PUBLICATION_YEAR + ") in your intact node. abort update." );
            }

            CvTopic email = cvTopicDao.getByShortLabel(CvTopic.CONTACT_EMAIL );    // not unique
            if ( email == null ) {
                throw new IntactException( "Could not find CvTopic(" + CvTopic.CONTACT_EMAIL + ") in your intact node. abort update." );
            }

            ///////////////////////////////////////
            // Updating experiment's annotation

            // author-list
            if ( eaf.getAuthorList() != null && eaf.getAuthorList().length() != 0 ) {
                if ( addUniqueAnnotation(  experiment, authorList, eaf.getAuthorList() ) ) {
                    report.setAuthorListUpdated( true );
                }
            }

            // journal
            if ( eaf.getJournal() != null && eaf.getJournal().length() != 0 ) {
                if ( addUniqueAnnotation(  experiment, journal, eaf.getJournal() ) ) {
                    report.setJournalUpdated( true );
                }
            }

            // year of publication
            if ( eaf.getYear() != -1 ) {
                if ( addUniqueAnnotation(  experiment, year, Integer.toString( eaf.getYear() ) ) ) {
                    report.setYearUpdated( true );
                }
            }

            // email - if not there yet, add it.
            if ( eaf.getAuthorEmail() != null && eaf.getAuthorEmail().length() != 0 ) {
                Annotation annotation = new Annotation( IntactContext.getCurrentInstance().getInstitution(), email );
                annotation.setAnnotationText( eaf.getAuthorEmail() );
                if ( ! experiment.getAnnotations().contains( annotation ) ) {
                    // add it
                    IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().persist( annotation );
                    experiment.addAnnotation( annotation );
                    IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().update( experiment );
                    report.setContactUpdated( true );
                }
            }

        } catch ( PublicationNotFoundException e ) {
            throw new IntactException( "The given pubmed id could not be found in CDB (" + pubmedId + "). See nested Exception for more details.", e );
        } catch ( UnexpectedException e ) {
            throw new IntactException( "Error while looking up for publication details from CDB. See nested Exception for more details.", e );
        }

        return report;
    }

    /**
     * Selects from the given Annotation collection all items having the given CvTopic.
     *
     * @param annotations a collection we have to filter
     * @param topic       the filter
     *
     * @return a new collection containing all matching annotations
     */
    private static Collection<Annotation> select( Collection<Annotation> annotations, CvTopic topic ) {
        if ( annotations == null || annotations.isEmpty() ) {
            return Collections.EMPTY_LIST;
        }

        ArrayList list = new ArrayList( 2 );
        for ( Iterator<Annotation> iterator = annotations.iterator(); iterator.hasNext(); ) {
            Annotation annotation = iterator.next();
            if ( annotation.getCvTopic().equals( topic ) ) {
                list.add( annotation );
            }
        }

        return list;
    }

    /**
     * Add an annotation in a CvObject if it is not in there yet.
     * <p/>
     * The CvTopic and the text of the annotation are given as parameters so the methods is flexible.
     *
     * @param experiment the CvObject in which we want to add the annotation
     * @param topic      the topic of the annotation. must not be null.
     * @param text       the text of the annotation. Can be null.
     *
     * @throws IntactException if something goes wrong during the update.
     */
    private static boolean addUniqueAnnotation( final Experiment experiment,
                                                final CvTopic topic,
                                                final String text ) throws IntactException {

        boolean updated = false;

        if ( topic == null ) {
            throw new IllegalArgumentException( "ERROR - You must give a non null topic when updating term " + experiment.getShortLabel() );
        } else {

            // We allow only one annotation to carry the given topic,
            //   > if one if found, we update the text,
            //   > if more than one, we delete the excess.

            // select all annotation of that object filtered by topic
            Collection annotationByTopic = select( experiment.getAnnotations(), topic );

            Institution institution = IntactContext.getCurrentInstance().getInstitution();

            // update annotations
            if ( annotationByTopic.isEmpty() ) {

                // add a new one
                Annotation annotation = new Annotation( institution, topic );
                annotation.setAnnotationText( text );
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().persist( annotation );
                experiment.addAnnotation( annotation );
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().update( experiment );

                updated = true;

            } else {

                // there was at least one annotation

                // first, check if the annotation we want to have in that CvObject is already in
                Annotation newAnnotation = new Annotation( institution, topic );
                newAnnotation.setAnnotationText( text );

                if ( annotationByTopic.contains( newAnnotation ) ) {
                    // found it, then we just remove it from the list and we are done.
                    annotationByTopic.remove( newAnnotation );
                } else {
                    // not found, we recycle an existing annotation and delete all others
                    Iterator iterator = annotationByTopic.iterator();
                    Annotation annotation = (Annotation) iterator.next();
                    String oldText = annotation.getAnnotationText();
                    annotation.setAnnotationText( text );
                    IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().update( annotation );

                    updated = true;

                    // remove it from the list as we are going to delete all other
                    iterator.remove();
                }
            }

            // if any annotation left, delete them as we want a unique one.
            for ( Iterator iterator = annotationByTopic.iterator(); iterator.hasNext(); ) {
                Annotation annotation = (Annotation) iterator.next();
                String _text = annotation.getAnnotationText();
                experiment.removeAnnotation( annotation );
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().update( experiment );
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().delete( annotation );

                updated = true;
            }
        } // topic is not null

        return updated;
    }
}