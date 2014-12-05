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
package uk.ac.ebi.intact.editor.services.curate.organism;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Service
@Lazy
public class BioSourceService extends AbstractEditorService {

    private static final Log log = LogFactory.getLog( BioSourceService.class );

    private Map<String, IntactOrganism> acOrganismMap;
    private Map<Integer, IntactOrganism> taxidOrganismMap;
    private List<SelectItem> bioSourceSelectItems;
    private List<SelectItem> organismSelectItems;

    private boolean isInitialised = false;

    public synchronized void clearAll(){
        if (isInitialised){
            this.acOrganismMap.clear();
            this.taxidOrganismMap.clear();
            this.bioSourceSelectItems=null;
            this.organismSelectItems=null;
            isInitialised=false;
        }
    }


    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAliases(IntactOrganism organis) {
        return getIntactDao().getOrganismDao().countAliasesForOrganism(organis.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactOrganism initialiseOrganismAliases(IntactOrganism interactor) {
        // reload interactor without flushing changes
        IntactOrganism reloaded = getIntactDao().getEntityManager().merge(interactor);
        Collection<Alias> aliases = reloaded.getAliases();
        initialiseAliases(aliases);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactOrganism loadOrganismByAc(String ac) {
        IntactOrganism organism = getIntactDao().getEntityManager().find(IntactOrganism.class, ac);

        // initialise aliases because first tab
        initialiseAliases(organism.getAliases());

        if (organism.getCellType() != null){
            initialiseCv(organism.getCellType());
        }
        if (organism.getTissue() != null){
            initialiseCv(organism.getTissue());
        }

        return organism;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactOrganism reloadFullyInitialisedOrganism(IntactOrganism organism) {
        IntactOrganism reloaded = getIntactDao().getEntityManager().merge(organism);

        // initialise aliases because first tab
        initialiseAliases(reloaded.getAliases());

        if (organism.getCellType() != null){
            initialiseCv(organism.getCellType());
        }
        if (organism.getTissue() != null){
            initialiseCv(organism.getTissue());
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public synchronized void loadData( ) {
        if ( log.isDebugEnabled() ) log.debug( "Loading BioSources" );

        List<IntactOrganism> allBioSources = getIntactDao().getOrganismDao().getAllSorted(0, Integer.MAX_VALUE, "commonName", true);

        bioSourceSelectItems = new ArrayList<SelectItem>(allBioSources.size());
        organismSelectItems = new ArrayList<SelectItem>(allBioSources.size());

        acOrganismMap = new HashMap<String, IntactOrganism>();
        taxidOrganismMap = new HashMap<Integer, IntactOrganism>();

        bioSourceSelectItems.add( new SelectItem( null, "-- Select BioSource --", "-- Select BioSource --", false, false, true ) );

        for (IntactOrganism bioSource : allBioSources) {

            SelectItem item = new SelectItem(bioSource, bioSource.getCommonName(), bioSource.getScientificName());
            bioSourceSelectItems.add(item);
            if (bioSource.getCellType() == null && bioSource.getTissue() == null){
                organismSelectItems.add(item);
            }

            if (bioSource.getCellType() != null){
                initialiseCv(bioSource.getCellType());
            }
            if (bioSource.getTissue() != null){
                initialiseCv(bioSource.getTissue());
            }
        }
    }


    public IntactOrganism findBioSourceByAc( String ac ) {
        return acOrganismMap.get(ac);
    }

    public List<SelectItem> getBioSourceSelectItems() {
        return bioSourceSelectItems;
    }

    public IntactOrganism findBiosourceByTaxid(Integer taxid) {
        return taxidOrganismMap.get(taxid);
    }

    public Map<String, IntactOrganism> getAcOrganismMap() {
        return acOrganismMap;
    }

    public Map<Integer, IntactOrganism> getTaxidOrganismMap() {
        return taxidOrganismMap;
    }

    public List<SelectItem> getOrganismSelectItems() {
        return organismSelectItems;
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    private void initialiseAliases(Collection<Alias> aliases) {
        for (Alias alias : aliases){
            if (alias.getType() != null){
                Hibernate.initialize(((IntactCvTerm) alias.getType()).getDbXrefs());
            }
        }
    }

    private void initialiseCv(CvTerm cv) {
        initialiseAnnotations(((IntactCvTerm)cv).getDbAnnotations());
        initialiseXrefs(((IntactCvTerm)cv).getDbXrefs());
    }

    private void initialiseXrefs(Collection<Xref> xrefs) {
        for (Xref ref : xrefs){
            Hibernate.initialize(((IntactCvTerm)ref.getDatabase()).getDbAnnotations());
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
}
