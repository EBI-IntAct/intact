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
package uk.ac.ebi.intact.editor.services;

import uk.ac.ebi.intact.jami.dao.IntactBaseDao;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.annotation.Resource;


/**
 * IntAct editor abstract service class.
 *
 * @version $Id: AbstractEditorService.java 20630 2014-08-22 08:10:26Z mdumousseau@yahoo.com $
 */
public abstract class AbstractEditorService implements EditorService {

    @Resource(name = "intactDao")
    private IntactDao intactDao;

    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    public IntactDao getIntactDao() {
        return intactDao;
    }

    public IntactTransactionSynchronization getAfterCommitExecutor() {
        return afterCommitExecutor;
    }

    protected void attachDaoToTransactionManager(){
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());
    }

    protected <T extends Auditable> void updateIntactObject(T intactObject, IntactBaseDao<T> dao) throws SynchronizerException,
            FinderException, PersisterException {
        try{
            dao.update(intactObject);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
    }

    protected <T extends Auditable> void persistIntactObject(T intactObject, IntactBaseDao<T> dao) throws SynchronizerException,
            FinderException, PersisterException {
        try{
            dao.persist(intactObject);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
    }

    protected <T extends Auditable,I> T synchronizeIntactObject(I intactObject, IntactDbSynchronizer<I,T> synchronizer, boolean persist) throws SynchronizerException,
            FinderException, PersisterException {
        try{
            return synchronizer.synchronize(intactObject, persist);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            throw e;
        }
    }

    protected <T extends Auditable> T reattachIntactObjectIfTransient(T intactObject, IntactBaseDao<T> dao){
        // merge current user because detached
        if (dao.isTransient(intactObject)){
            return getIntactDao().getEntityManager().merge(intactObject);
        }

        return intactObject;
    }
}