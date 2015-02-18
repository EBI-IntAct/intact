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
package uk.ac.ebi.intact.editor.services.curate.institution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.editor.controller.curate.cloner.InstitutionCloner;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Service
@Lazy
public class InstitutionService extends AbstractEditorService {

    private static final Log log = LogFactory.getLog( InstitutionService.class );

    private List<SelectItem> institutionSelectItems;

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public void loadInstitutions( ) {
        if ( log.isDebugEnabled() ) log.debug( "Loading Institutions" );

        synchronized (institutionSelectItems) {
            this.institutionSelectItems = null;
            List<IntactSource> allInstitutions = getIntactDao().getSourceDao().getAllSorted(0, Integer.MAX_VALUE, "shortName", true);

            institutionSelectItems = new ArrayList<SelectItem>(allInstitutions.size());

            for (IntactSource institution : allInstitutions) {
                institutionSelectItems.add(new SelectItem(institution, institution.getShortName(), institution.getFullName()));
            }
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public IntactSource findInstitutionByAc( String ac ) {
        return getIntactDao().getSourceDao().getByAc( ac );
    }

    public List<SelectItem> getInstitutionSelectItems() {
        return getInstitutionSelectItems(true);
    }

    public List<SelectItem> getInstitutionSelectItems(boolean addDefaultNoSelection) {
        synchronized (institutionSelectItems) {
            if (institutionSelectItems == null){
                return null;
            }
            List<SelectItem> items = new ArrayList(institutionSelectItems);

            if (addDefaultNoSelection) {
                items.add( new SelectItem( null, "-- Select Institution --", "-- Select Institution --", false, false, true ) );
            }

            return items;
        }
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(IntactSource cv) {
        return getIntactDao().getSourceDao().countXrefsForSource(cv.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(IntactSource cv) {
        return getIntactDao().getSourceDao().countAnnotationsForSource(cv.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAliases(IntactSource cv) {
        return getIntactDao().getSourceDao().countSynonymsForSource(cv.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactSource loadSourceByAc(String ac) {
        IntactSource cv = getIntactDao().getEntityManager().find(IntactSource.class, ac);

        if (cv != null){
            // initialise xrefs because are first tab visible
            initialiseXrefs(cv.getDbXrefs());
            // initialise annotations because needs caution, url, etc
            initialiseAnnotations(cv.getDbAnnotations());
            // initialise aliases
            initialiseAliases(cv.getSynonyms());
        }

        return cv;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactSource reloadFullyInitialisedSource(IntactSource cv) {
        if (cv == null){
            return null;
        }
        IntactSource reloaded = null;
        if (areSourceCollectionsLazy(cv)
                && cv.getAc() != null
                && !getIntactDao().getEntityManager().contains(cv)){
            reloaded = loadSourceByAc(cv.getAc());
        }

        // we need first to merge with reloaded complex
        if (reloaded != null){
            // detach reloaded now so not changes will be committed
            getIntactDao().getEntityManager().detach(reloaded);
            InstitutionCloner cloner = new InstitutionCloner();
            cloner.copyInitialisedProperties(cv, reloaded);
            cv = reloaded;
        }

        // initialise xrefs because are first tab visible
        initialiseXrefs(cv.getDbXrefs());
        // initialise annotations because needs caution
        initialiseAnnotations(cv.getDbAnnotations());
        // initialise aliases
        initialiseAliases(cv.getSynonyms());

        return cv;
    }

    private boolean areSourceCollectionsLazy(IntactSource cv) {
        return !cv.areAnnotationsInitialized()
                || !cv.areXrefsInitialized()
                || !cv.areSynonymsInitialized();
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public boolean isSourceFullyLoaded(IntactSource cv){
        if (cv == null){
            return true;
        }
        return !areSourceCollectionsLazy(cv);
    }
}
