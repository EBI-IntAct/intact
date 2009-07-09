/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dbupdate.smallmolecules;

import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.unit.IntactTestException;

import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Small molecule updator.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public class SmallMoleculeUpdator {

    private static final Log log = LogFactory.getLog( SmallMoleculeUpdator.class );

    private SmallMoleculeProcessor smallMoleculeProcessor;

    public SmallMoleculeUpdator(SmallMoleculeProcessor smallMoleculeProcessor) {
        this.smallMoleculeProcessor = smallMoleculeProcessor;
    }

    public void startUpdate() throws SmallMoleculeUpdatorException, IntactTransactionException {

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        final String hql = "select sm.ac from SmallMoleculeImpl sm order by sm.created";
        List<String> smallMoleculeAcs = dataContext.getDaoFactory().getEntityManager().createQuery( hql ).getResultList();

        if ( log.isDebugEnabled() ) {
            log.debug( "Found " + smallMoleculeAcs.size() + " small molecules in the database." );
        }

        smallMoleculeProcessor.updateByAcs(smallMoleculeAcs);
    }

    public SmallMoleculeUpdateReport getUpdateReport() {
        return smallMoleculeProcessor.getReport();
    }
}
