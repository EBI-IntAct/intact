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
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collection;
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

    public synchronized void clearAll(){
        this.institutionSelectItems = null;
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public synchronized void loadInstitutions( ) {
        if ( log.isDebugEnabled() ) log.debug( "Loading Institutions" );

        clearAll();

        List<IntactSource> allInstitutions = getIntactDao().getSourceDao().getAllSorted(0,Integer.MAX_VALUE, "shortName", true);

        institutionSelectItems = new ArrayList<SelectItem>(allInstitutions.size());

        for (IntactSource institution : allInstitutions) {
            institutionSelectItems.add(new SelectItem(institution, institution.getShortName(), institution.getFullName()));
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public IntactSource findInstitutionByAc( String ac ) {
        return getIntactDao().getSourceDao().getByAc( ac );
    }

    public List<SelectItem> getInstitutionSelectItems() {
        return getInstitutionSelectItems(true);
    }

    public synchronized List<SelectItem> getInstitutionSelectItems(boolean addDefaultNoSelection) {
        if (institutionSelectItems == null){
            return null;
        }
        List<SelectItem> items = new ArrayList(institutionSelectItems);

        if (addDefaultNoSelection) {
            items.add( new SelectItem( null, "-- Select Institution --", "-- Select Institution --", false, false, true ) );
        }

        return items;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactSource initialiseSourceXrefs(IntactSource cv) {
        // reload IntactInteractionEvidence without flushing changes
        IntactSource reloaded = reattachIntactObjectIfTransient(cv, getIntactDao().getSourceDao());
        Collection<Xref> xrefs = reloaded.getDbXrefs();
        initialiseXrefs(xrefs);
        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactSource initialiseSourceAnnotations(IntactSource cv) {
        // reload IntactInteractionEvidence without flushing changes
        IntactSource reloaded = reattachIntactObjectIfTransient(cv, getIntactDao().getSourceDao());
        Collection<Annotation> annotations = reloaded.getDbAnnotations();
        initialiseAnnotations(annotations);
        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }


    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactSource initialiseSourceSynonyms(IntactSource cv) {
        // reload IntactInteractionEvidence without flushing changes
        IntactSource reloaded = reattachIntactObjectIfTransient(cv, getIntactDao().getSourceDao());
        Collection<Alias> aliases = reloaded.getSynonyms();
        initialiseAliases(aliases);
        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
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
        }

        return cv;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactSource reloadFullyInitialisedSource(IntactSource cv) {
        IntactSource reloaded = getIntactDao().getEntityManager().merge(cv);

        // initialise xrefs because are first tab visible
        initialiseXrefs(reloaded.getDbXrefs());
        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getDbAnnotations());

        getIntactDao().getEntityManager().detach(reloaded);

        return cv;
    }

    private void initialiseXrefs(Collection<Xref> xrefs) {
        for (Xref ref : xrefs){
            Hibernate.initialize(((IntactCvTerm) ref.getDatabase()).getDbAnnotations());
            Hibernate.initialize(((IntactCvTerm)ref.getDatabase()).getDbXrefs());
            if (ref.getQualifier() != null){
                Hibernate.initialize(((IntactCvTerm)ref.getQualifier()).getDbXrefs());
            }
        }
    }

    private void initialiseAnnotations(Collection<Annotation> annotations) {
        for (Annotation annot : annotations){
            Hibernate.initialize(((IntactCvTerm)annot.getTopic()).getDbAnnotations());
            Hibernate.initialize(((IntactCvTerm)annot.getTopic()).getDbXrefs());
        }
    }

    private void initialiseAliases(Collection<Alias> aliases) {
        for (Alias alias : aliases){
            if (alias.getType() != null){
                Hibernate.initialize(((IntactCvTerm)alias.getType()).getDbXrefs());
            }
        }
    }
}
