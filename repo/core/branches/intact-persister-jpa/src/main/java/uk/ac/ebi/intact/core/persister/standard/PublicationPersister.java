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
package uk.ac.ebi.intact.core.persister.standard;

import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Publication persister.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PublicationPersister extends AbstractAnnotatedObjectPersister<Publication>{

    private static ThreadLocal<PublicationPersister> instance = new ThreadLocal<PublicationPersister>() {
        @Override
        protected PublicationPersister initialValue() {
            return new PublicationPersister();
        }
    };

    public static PublicationPersister getInstance() {
        return instance.get();
    }

    public PublicationPersister() {
        super();
    }

    /**
     * TODO: check on the primary ref xref first
     */
    protected Publication fetchFromDataSource(Publication intactObject) {
        return getIntactContext().getDataContext().getDaoFactory()
                .getPublicationDao().getByShortLabel(intactObject.getShortLabel());
    }

    @Override
    protected void saveOrUpdateAttributes(Publication intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);

        for ( Experiment experiment : intactObject.getExperiments() ) {
            ExperimentPersister.getInstance().saveOrUpdate( experiment );
        }
    }

    @Override
    protected Publication syncAttributes(Publication intactObject) {

        Collection<Experiment> experiments = new ArrayList<Experiment>( intactObject.getExperiments().size() );
        for ( Experiment experiment : intactObject.getExperiments() ) {
            experiments.add( ExperimentPersister.getInstance().syncIfTransient( experiment ) );
        }
        intactObject.setExperiments( experiments );

        return super.syncAttributes( intactObject );
    }
}