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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvObjectXref;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectPersister extends AbstractAnnotatedObjectPersister<CvObject> {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(CvObjectPersister.class);

     private static ThreadLocal<CvObjectPersister> instance = new ThreadLocal<CvObjectPersister>() {
        @Override
        protected CvObjectPersister initialValue() {
            return new CvObjectPersister();
        }
    };

    public static CvObjectPersister getInstance() {
        return instance.get();
    }

    protected CvObjectPersister() {
        super();
    }

    @Override
    protected void saveOrUpdateAttributes(CvObject intactObject) throws PersisterException {
        if (intactObject.getXrefs().isEmpty()) {
            log.warn("CvObject without Xrefs: "+intactObject.getShortLabel());
            //throw new PersisterException("Cannot save or update a CvObject without Xrefs");
        }

        for (CvObjectXref xref : intactObject.getXrefs()) {
            saveOrUpdate(xref.getCvDatabase());

            if (xref.getCvXrefQualifier() != null) {
                saveOrUpdate(xref.getCvXrefQualifier());
            }
        }
    }

    @Override
    protected CvObject fetchFromDataSource(CvObject intactObject) {
        return getIntactContext().getDataContext().getDaoFactory()
                .getCvObjectDao().getByShortLabel(intactObject.getClass(), intactObject.getShortLabel());
    }

}