/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.editor.services.dashboard;

import org.primefaces.model.LazyDataModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.editor.util.LazyDataModelFactory;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

/**
 */
@Service
public class DashboardQueryService extends AbstractEditorService {

    public DashboardQueryService() {
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactPublication> loadAllPublications(String additionalSql){
         return  LazyDataModelFactory.createLazyDataModel(getIntactDao().getEntityManager(),
                 "select p from IntactPublication p left join fetch p.dbXrefs as x where " + additionalSql,
                 "select count(distinct p.ac) from IntactPublication p where " + additionalSql, "p", "updated", false);
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactPublication> loadPublicationsOwnedBy(String userLogin, String additionalSql){
        return LazyDataModelFactory.createLazyDataModel(getIntactDao().getEntityManager(),
                "select p from IntactPublication p left join fetch p.dbXrefs as x where upper(p.currentOwner.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")",
                "select count(distinct p.ac) from IntactPublication p where upper(p.currentOwner.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")", "p", "updated", false
        );
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactPublication> loadPublicationsReviewedBy(String userLogin, String additionalSql){
        return LazyDataModelFactory.createLazyDataModel(getIntactDao().getEntityManager(),
                "select p from IntactPublication p left join fetch p.dbXrefs as x where upper(p.currentReviewer.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")",
                "select count(distinct p.ac) from IntactPublication p where upper(p.currentReviewer.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")", "p", "updated", false
        );
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactComplex> loadAllComplexes(String additionalSql){
        return LazyDataModelFactory.createLazyDataModel(getIntactDao().getEntityManager(),
                "select p from IntactComplex p where " + additionalSql,
                "select count(distinct p.ac) from IntactComplex p where " + additionalSql, "p", "updated", false);
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactComplex> loadComplexesOwnedBy(String userLogin, String additionalSql){
        return LazyDataModelFactory.createLazyDataModel(getIntactDao().getEntityManager(),
                "select p from IntactComplex p where upper(p.currentOwner.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")",
                "select count(distinct p.ac) from IntactComplex p where upper(p.currentOwner.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")", "p", "updated", false
        );
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactComplex> loadComplexesReviewedBy(String userLogin, String additionalSql){
        return LazyDataModelFactory.createLazyDataModel(getIntactDao().getEntityManager(),
                "select p from IntactComplex p where upper(p.currentReviewer.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")",
                "select count(distinct p.ac) from IntactComplex p where upper(p.currentReviewer.login) = '" + userLogin + "'" +
                        " and (" + additionalSql + ")", "p", "updated", false
        );
    }
}
