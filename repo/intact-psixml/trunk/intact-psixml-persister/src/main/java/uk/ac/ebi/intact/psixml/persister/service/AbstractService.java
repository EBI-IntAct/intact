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
package uk.ac.ebi.intact.psixml.persister.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.psixml.persister.key.Key;
import uk.ac.ebi.intact.psixml.persister.util.CacheContext;

import java.io.Serializable;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractService<T extends AnnotatedObject, K extends Key> implements Serializable {

    private IntactContext intactContext;
    private CacheContext cacheContext;

    protected AbstractService(IntactContext intactContext) {
        this.intactContext = intactContext;
        this.cacheContext = CacheContext.getInstance(intactContext);
    }

    protected CacheContext getCacheContext() {
        return cacheContext;
    }

    protected IntactContext getIntactContext() {
        return intactContext;
    }

    public T get(K key) {
        Element elem = getCache(key.getElement().getObjectValue().getClass())
                .get(key.getElement().getObjectKey());

        T intactObject = null;

        if (elem != null) {
            intactObject = (T) elem.getValue();
        } else {
            T intactObjectFromDb = fetchFromDb(key);

            if (intactObjectFromDb != null) {
                intactObject = intactObjectFromDb;
            }

            getCache(key.getElement().getObjectValue().getClass())
                    .put(key.getElement());
        }

        return intactObject;
    }

    public abstract void persist(T objectToPersist);

    protected abstract T fetchFromDb(K key);

    protected Cache getCache(Class objectType) {
        return getCacheContext().cacheFor(objectType);
    }
}