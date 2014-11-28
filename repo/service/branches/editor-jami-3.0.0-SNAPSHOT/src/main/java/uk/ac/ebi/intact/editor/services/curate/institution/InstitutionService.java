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

    public synchronized void clearAll(){
        this.institutionSelectItems = null;
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public void loadInstitutions( ) {
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
}
