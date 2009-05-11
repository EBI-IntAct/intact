/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Scope;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.model.Interaction;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.FlushModeType;

/**
 * Helper class to reduce the code needed to save or update an Annotated object.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PersisterHelper {

    @Autowired
    private CorePersister corePersister;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(PersisterHelper.class);

    public PersisterHelper() {}

    public static void saveOrUpdate( IntactEntry... intactEntries ) throws PersisterException {
        IntactContext.getCurrentInstance().getPersisterHelper().save(intactEntries);
    }

    public static PersisterStatistics saveOrUpdate( AnnotatedObject... annotatedObjects ) throws PersisterException {
        return IntactContext.getCurrentInstance().getPersisterHelper().save(annotatedObjects);
    }

    @Deprecated
    public static PersisterStatistics saveOrUpdate( CorePersister corePersister, AnnotatedObject... annotatedObjects ) throws PersisterException {
        return IntactContext.getCurrentInstance().getPersisterHelper().save(corePersister, annotatedObjects);
    }

    public void save( IntactEntry... intactEntries ) throws PersisterException {
        for ( IntactEntry intactEntry : intactEntries ) {
            for ( Interaction interaction : intactEntry.getInteractions() ) {
                saveOrUpdate( interaction );
            }
        }
    }

    @Transactional
    public PersisterStatistics save( AnnotatedObject... annotatedObjects ) throws PersisterException {
        return save(corePersister, annotatedObjects);
    }

    @Transactional
    public PersisterStatistics save( CorePersister corePersister, AnnotatedObject... annotatedObjects ) throws PersisterException {

        // during synchronization-persistence there is a lot of things going on, which involve
        // reading the database and persisting at the same time, but only flushing at the very end.
        // If autoflush, the entity manager will attempt a flush when not everything is ready, and it will fail.
        //boolean originalAutoFlush = dataContext.getDaoFactory().getDataConfig().isAutoFlush();
        //dataContext.getDaoFactory().getDataConfig().setAutoFlush(false);
        entityManager.setFlushMode(FlushModeType.COMMIT);

        corePersister.getStatistics().reset();

        try {
            for ( AnnotatedObject ao : annotatedObjects ) {
                corePersister.synchronize( ao );
            }
            corePersister.commit();
        } finally {
            //dataContext.getDaoFactory().getDataConfig().setAutoFlush(originalAutoFlush);
            entityManager.setFlushMode(FlushModeType.AUTO);
        }

        // we reload the annotated objects by its AC
        // note: if an object does not have one, it is probably a duplicate
        for ( AnnotatedObject ao : annotatedObjects ) {
            corePersister.reload( ao );
        }
        
        final PersisterStatistics stats = corePersister.getStatistics();

        if (log.isDebugEnabled()) log.debug(stats);

        return stats;

    }

    public CorePersister getCorePersister() {
        return corePersister;
    }
}