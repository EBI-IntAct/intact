/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.cvutils;

 
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.cvutils.model.AnnotationInfo;
import uk.ac.ebi.intact.dataexchange.cvutils.model.AnnotationInfoDataset;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service allowing to retreive annotation info dataset from the intact database.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
@Component
public class AnnotationInfoDatasetService {

    private static final int MAX_CV_BATCH_SIZE = 50;

    public AnnotationInfoDatasetService() {
    }

    /**
     * Extract from the IntAct database all CvObjects having at least one annotation having one of the given CvTopic.
     *
     * @param topics topic filter.
     * @return a non null dataset.
     * @throws AnnotationInfoDatasetServiceException
     *
     */
    public AnnotationInfoDataset retrieveAnnotationInfoDataset( Collection<CvTopic> topics ) throws AnnotationInfoDatasetServiceException {
        return retrieveAnnotationInfoDataset( topics, null );
    }

    /**
     * Extract from the IntAct database all CvObjects having at least one annotation having one of the given CvTopic.
     * If a non null date is given, we filter annotation so that we return only those that have been created or updated
     * after than date.
     *
     * @param topics topic filter.
     * @param after  only export terms that have been created or updated after this date
     * @return a non null dataset.
     * @throws AnnotationInfoDatasetServiceException
     *
     */
    @Transactional
    public AnnotationInfoDataset retrieveAnnotationInfoDataset( Collection<CvTopic> topics, Date after ) throws AnnotationInfoDatasetServiceException {

        AnnotationInfoDataset dataset = new AnnotationInfoDataset();

        DaoFactory daof = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final CvObjectDao<CvObject> cvDao = daof.getCvObjectDao();

        final int termCount = cvDao.countAll();
        int currentIdx = 0;

        do {
            final List<CvObject> terms = cvDao.getAll( currentIdx, MAX_CV_BATCH_SIZE );

            for ( CvObject cvObject : terms ) {
                final Collection<Annotation> annotations = AnnotatedObjectUtils.findAnnotationsByCvTopic( cvObject, topics );
                for ( Annotation annotation : annotations ) {

                    if ( after == null
                         || after.before( annotation.getUpdated() )
                         || after.before( annotation.getCreated() ) ) {

                        AnnotationInfo ai = new AnnotationInfo( cvObject.getShortLabel(),
                                                                cvObject.getFullName(),
                                                                CgLibUtil.getRealClassName( cvObject ).getName(),
                                                                cvObject.getIdentifier(),
                                                                annotation.getCvTopic().getShortLabel(),
                                                                annotation.getAnnotationText(),
                                                                false );
                        dataset.addCvAnnotation( ai );
                    }
                } // annotations
            }

            currentIdx += terms.size();

        } while ( currentIdx < termCount - 1 );

        return dataset;
    }
}
