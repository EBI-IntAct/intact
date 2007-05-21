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

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.psixml.persister.util.CacheContext;

import java.io.Serializable;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractService<T extends IntactObject> implements Serializable {

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

    public abstract void persist(T intactObject);
}