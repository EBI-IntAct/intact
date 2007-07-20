/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;

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
        private UpdatedValue authorListValue;
        private UpdatedValue contactListValue;
        private UpdatedValue yearListValue;
        private UpdatedValue journalListValue;
        private UpdatedValue authorEmailValue;

        public UpdateReport() {
        }

        public boolean isAuthorListUpdated() {
            return authorListValue != null;
        }

        public boolean isContactUpdated() {
            return contactListValue != null;
        }

        public boolean isYearUpdated() {
            return yearListValue != null;
        }

        public boolean isJournalUpdated() {
            return journalListValue != null;
        }

        public boolean isAuthorEmailUpdated()
        {
            return authorEmailValue != null;
        }

        public UpdatedValue getAuthorListValue()
        {
            return authorListValue;
        }

        public void setAuthorListValue(UpdatedValue authorListValue)
        {
            this.authorListValue = authorListValue;
        }

        public UpdatedValue getContactListValue()
        {
            return contactListValue;
        }

        public void setContactListValue(UpdatedValue contactListValue)
        {
            this.contactListValue = contactListValue;
        }

        public UpdatedValue getYearListValue()
        {
            return yearListValue;
        }

        public void setYearListValue(UpdatedValue yearListValue)
        {
            this.yearListValue = yearListValue;
        }

        public UpdatedValue getJournalListValue()
        {
            return journalListValue;
        }

        public void setJournalListValue(UpdatedValue journalListValue)
        {
            this.journalListValue = journalListValue;
        }

        public UpdatedValue getAuthorEmailValue()
        {
            return authorEmailValue;
        }

        public void setAuthorEmailValue(UpdatedValue authorEmailValue)
        {
            this.authorEmailValue = authorEmailValue;
        }
    }

    public static class UpdatedValue
    {
        private String oldValue;
        private String newValue;

        public UpdatedValue(String newValue)
        {
            this.newValue = newValue;
        }

        public UpdatedValue(String oldValue, String newValue)
        {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getOldValue()
        {
            return oldValue;
        }

        public String getNewValue()
        {
            return newValue;
        }

        @Override
        public String toString()
        {
            return "[ "+oldValue+" ] -> [ "+newValue+"]";
        }
    }

    /**
     * Update the given Experiment according to the given pubmed ID.
     *
     * @param experiment the experiment to update
     * @param pubmedId   the pubmed from which we get the information
     *
     * @return an UpdateReport, never null.
     */
    public static UpdateReport update( Experiment experiment, String pubmedId ) throws IntactException {
        return update( experiment, pubmedId, false);
    }

    /**
     * Update the given Experiment according to the given pubmed ID.
     *
     * @param experiment the experiment to update
     * @param pubmedId   the pubmed from which we get the information
     * @param dryRun if true, do not modify the database (for simulation)
     *
     * @return an UpdateReport, never null.
     */
    public static UpdateReport update( Experiment experiment, String pubmedId, boolean dryRun ) throws IntactException {

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
                UpdatedValue uv = addUniqueAnnotation(  experiment, authorList, eaf.getAuthorList(), dryRun );
                report.setAuthorListValue(uv);
            }

            // journal
            if ( eaf.getJournal() != null && eaf.getJournal().length() != 0 ) {
                UpdatedValue uv = addUniqueAnnotation(  experiment, journal, eaf.getJournal(), dryRun );
                report.setJournalListValue(uv);
            }

            // year of publication
            if ( eaf.getYear() != -1 ) {
                UpdatedValue uv = addUniqueAnnotation(  experiment, year, Integer.toString( eaf.getYear() ), dryRun );
                report.setYearListValue(uv);
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

                    UpdatedValue uv = new UpdatedValue(eaf.getAuthorEmail());
                    report.setAuthorEmailValue(uv);
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
     */
    private static UpdatedValue addUniqueAnnotation( final Experiment experiment,
                                                final CvTopic topic,
                                                final String text,
                                                final boolean dryRun ) throws IntactException {

        UpdatedValue updatedValue = null;

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

                if (!dryRun)
                    IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().persist( annotation );

                experiment.addAnnotation( annotation );

                if (!dryRun)
                    IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().update( experiment );

                updatedValue = new UpdatedValue(text);

            } else {

                // there was at least one annotation

                // first, check if the annotation we want to have in that CvObject is already in
                Annotation newAnnotation = new Annotation( institution, topic );
                newAnnotation.setAnnotationText( text );

                if ( annotationByTopic.contains( newAnnotation ) ) {
                    // found it, then we just remove it from the list and we are done.
                    annotationByTopic.remove( newAnnotation );
                    updatedValue = new UpdatedValue(null, text);
                } else {
                    // not found, we recycle an existing annotation and delete all others
                    Iterator iterator = annotationByTopic.iterator();
                    Annotation annotation = (Annotation) iterator.next();
                    String oldText = annotation.getAnnotationText();
                    annotation.setAnnotationText( text );

                    if (!dryRun)
                        IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().update( annotation );

                    updatedValue = new UpdatedValue(oldText, text);

                    // remove it from the list as we are going to delete all other
                    iterator.remove();
                }
            }

            // if any annotation left, delete them as we want a unique one.
            for ( Iterator iterator = annotationByTopic.iterator(); iterator.hasNext(); ) {
                Annotation annotation = (Annotation) iterator.next();
                String _text = annotation.getAnnotationText();
                experiment.removeAnnotation( annotation );

                if (!dryRun)
                {
                    IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().update( experiment );
                    IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotationDao().delete( annotation );
                }

                updatedValue = new UpdatedValue(_text, null);
            }
        } // topic is not null

        return updatedValue;
    }
}