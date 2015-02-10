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

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.dao.IntactBaseDao;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;


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
        // clear manager first to avaoid to have remaining objects from other transactions
        getIntactDao().getEntityManager().clear();

        try{
            dao.update(intactObject);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (Throwable e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw new PersisterException(e.getMessage(), e);
        }
    }

    protected <T extends Auditable> void persistIntactObject(T intactObject, IntactBaseDao<T> dao) throws SynchronizerException,
            FinderException, PersisterException {
        // clear manager first to avaoid to have remaining objects from other transactions
        getIntactDao().getEntityManager().clear();

        try{
            dao.persist(intactObject);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (Throwable e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw new PersisterException(e.getMessage(), e);
        }
    }

    protected <T extends Auditable> void deleteIntactObject(T intactObject, IntactBaseDao<T> dao) throws SynchronizerException,
            FinderException, PersisterException {
        // clear manager first to avaoid to have remaining objects from other transactions
        getIntactDao().getEntityManager().clear();

        try{
            dao.delete(intactObject);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (Throwable e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw new PersisterException(e.getMessage(), e);
        }
    }

    protected <T extends Auditable,I> T synchronizeIntactObject(I intactObject, IntactDbSynchronizer<I,T> synchronizer, boolean persist) throws SynchronizerException,
            FinderException, PersisterException {
        try{
            // clear manager first to avaoid to have remaining objects from other transactions
            getIntactDao().getEntityManager().clear();

            return synchronizer.synchronize(intactObject, persist);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (Throwable e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw new PersisterException(e.getMessage(), e);
        }
    }

    protected <T extends Auditable,I> T convertToPersistentIntactObject(I intactObject, IntactDbSynchronizer<I,T> synchronizer) throws SynchronizerException,
            FinderException, PersisterException {
        try{
            // clear manager first to avaoid to have remaining objects from other transactions
            getIntactDao().getEntityManager().clear();

            return synchronizer.convertToPersistentObject(intactObject);
        }
        catch (SynchronizerException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (FinderException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (PersisterException e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw e;
        }
        catch (Throwable e){
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().clear();
            throw new PersisterException(e.getMessage(), e);
        }
    }

    protected <T extends IntactPrimaryObject> T reattachIntactObjectIfTransient(T intactObject, IntactBaseDao<T> dao){
        // merge current user because detached
        if (dao.isTransient(intactObject) && intactObject.getAc() != null){
            return getIntactDao().getEntityManager().merge(intactObject);
        }

        return intactObject;
    }


    protected void initialiseXrefs(Collection<Xref> xrefs) {
        Map<CvTerm, CvTerm> cvMap = new IdentityMap();
        for (Xref ref : xrefs){
            CvTerm db = initialiseCvWithCache(ref.getDatabase(), cvMap);
            if (db != ref.getDatabase()){
                ((AbstractIntactXref)ref).setDatabase(db);
            }
            if (ref.getQualifier() != null){
                CvTerm qual = initialiseCvWithCache(ref.getQualifier(), cvMap);
                if (qual != ref.getQualifier()){
                    ((AbstractIntactXref)ref).setQualifier(qual);
                }
            }
        }
    }

    protected void initialiseAnnotations(Collection<Annotation> annotations) {
        Map<CvTerm, CvTerm> cvMap = new IdentityMap();
        for (Annotation annot : annotations){
            CvTerm type = initialiseCvWithCache(annot.getTopic(), cvMap);
            if (type != annot.getTopic()){
                ((AbstractIntactAnnotation)annot).setTopic(type);
            }
        }
    }

    protected void initialiseXrefs(Collection<Xref> xrefs, Map<CvTerm, CvTerm> cvMap) {
        for (Xref ref : xrefs){
            CvTerm db = initialiseCvWithCache(ref.getDatabase(), cvMap);
            if (db != ref.getDatabase()){
                ((AbstractIntactXref)ref).setDatabase(db);
            }
            if (ref.getQualifier() != null){
                CvTerm qual = initialiseCvWithCache(ref.getQualifier(), cvMap);
                if (qual != ref.getQualifier()){
                    ((AbstractIntactXref)ref).setQualifier(qual);
                }
            }
        }
    }

    protected void initialiseAnnotations(Collection<Annotation> annotations, Map<CvTerm, CvTerm> cvMap) {
        for (Annotation annot : annotations){
            CvTerm type = initialiseCvWithCache(annot.getTopic(), cvMap);
            if (type != annot.getTopic()){
                ((AbstractIntactAnnotation)annot).setTopic(type);
            }
        }
    }


    protected CvTerm initialiseCv(CvTerm cv) {
        if (!getIntactDao().getEntityManager().contains(cv)){
            cv = getIntactDao().getEntityManager().find(IntactCvTerm.class, ((IntactCvTerm)cv).getAc());
        }
        initialiseAnnotations(((IntactCvTerm) cv).getDbAnnotations());
        initialiseXrefs(((IntactCvTerm)cv).getDbXrefs());
        return cv;
    }

    protected CvTerm initialiseCvWithCache(CvTerm cv, Map<CvTerm,CvTerm> cvMap) {
        if (!getIntactDao().getEntityManager().contains(cv)){
            cv = getIntactDao().getEntityManager().find(IntactCvTerm.class, ((IntactCvTerm)cv).getAc());
        }
        if (cvMap.containsKey(cv)){
            return cvMap.get(cv);
        }
        else{
            cvMap.put(cv,cv);
        }
        initialiseAnnotations(((IntactCvTerm) cv).getDbAnnotations(), cvMap);
        initialiseXrefs(((IntactCvTerm)cv).getDbXrefs(), cvMap);
        return cv;
    }

    protected void initialiseAliases(Collection<Alias> aliases) {
        for (Alias alias : aliases){
            if (alias.getType() != null){
                CvTerm type = initialiseCv(alias.getType());
                if (type != alias.getType()){
                    ((AbstractIntactAlias)alias).setType(type);
                }
            }
        }
    }

    protected void initialiseParameters(Collection<? extends Parameter> parameters) {
        for (Parameter parameter : parameters){
            CvTerm type = initialiseCv(parameter.getType());
            if (type != parameter.getType()){
                ((AbstractIntactParameter)parameter).setType(type);
            }

            if (parameter.getUnit() != null){
                CvTerm unit = initialiseCv(parameter.getUnit());
                if (unit != parameter.getUnit()){
                    ((AbstractIntactParameter)parameter).setUnit(unit);
                }
            }
        }
    }

    protected void initialiseConfidence(Confidence det) {
        CvTerm type = initialiseCv(det.getType());
        if (type != det.getType()){
            ((AbstractIntactConfidence)det).setType(type);
        }
    }

    protected void initialisePosition(Position pos) {
        CvTerm reloaded = initialiseCv(pos.getStatus());
        if (reloaded != pos.getStatus()){
            ((IntactPosition)pos).setStatus(reloaded);
        }
    }

    protected void initialiseRanges(AbstractIntactFeature feature) {
        for (Object r : feature.getRanges()){
            AbstractIntactRange range = (AbstractIntactRange)r;

            initialisePosition(range.getStart());
            initialisePosition(range.getEnd());
            if (range.getResultingSequence() != null){
                initialiseXrefs(range.getResultingSequence().getXrefs());
            }
        }
    }
}