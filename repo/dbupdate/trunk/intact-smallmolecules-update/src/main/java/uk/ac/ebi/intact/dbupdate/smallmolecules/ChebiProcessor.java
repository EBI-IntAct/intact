/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dbupdate.smallmolecules;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.visitor.IntactObjectTraverser;
import uk.ac.ebi.intact.model.visitor.DefaultTraverser;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.dataexchange.enricher.standard.InteractorEnricher;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactTestException;
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.util.List;
import java.util.Collection;

/**
 * Implementation class for SmallMoleculeProcessor.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public class ChebiProcessor implements SmallMoleculeProcessor {

    private InteractorEnricher enricher;

    public ChebiProcessor() {
        enricher = InteractorEnricher.getInstance();
    }

    /**
     * Interates through all the Acs and get the smallmolecule from the database
     * and calls the enricher to enrich the small molecule
     *
     * @param smallMoleculeAcs list of smallmolecule acs
     * @throws SmallMoleculeUpdatorException
     */
    public void updateByAcs( List<String> smallMoleculeAcs ) throws SmallMoleculeUpdatorException {

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        final DaoFactory daoFactory = dataContext.getDaoFactory();

        for ( String smallMoleculeAc : smallMoleculeAcs ) {
            dataContext.beginTransaction();

            final SmallMoleculeImpl smallMolecule = daoFactory.getInteractorDao( SmallMoleculeImpl.class ).getByAc( smallMoleculeAc );
            // load annotations (to avoid lazyinitializationexceptions later)
            smallMolecule.getXrefs().size();
            smallMolecule.getAnnotations().size();

            enricher.enrich( smallMolecule );

            final Collection<Annotation> interactorTypeAnnotationCollection = smallMolecule.getCvInteractorType().getAnnotations();
            final Collection<Annotation> smallmoleculeAnnotationCollection = smallMolecule.getAnnotations();

            persistAnnotations( interactorTypeAnnotationCollection );
            persistAnnotations( smallmoleculeAnnotationCollection );
            PersisterHelper.saveOrUpdate( smallMolecule );
            commitTransaction();
        }
    }

    private void persistAnnotations( Collection<Annotation> annotationCollection ) {

        for ( Annotation annotation : annotationCollection ) {
            if ( annotation.getAc() == null ) {
                persistCvTopic( annotation );
            }
        }
    }

    private void persistCvTopic( Annotation annotation ) {
        if ( annotation.getCvTopic() != null && annotation.getCvTopic().getAc() == null ) {
            PersisterHelper.saveOrUpdate( annotation.getCvTopic() );
        }
    }

    protected void commitTransaction() throws SmallMoleculeUpdatorException {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        if ( dataContext.isTransactionActive() ) {
            try {
                dataContext.commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new IntactTestException( e );
            }
        }
    }
}