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

import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.core.unit.IntactTestException;

import java.util.List;

/**
 * Small molecule updator.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public class SmallMoleculeUpdator {

    private SmallMoleculeProcessor smallMoleculeProcessor;

    public SmallMoleculeUpdator(SmallMoleculeProcessor smallMoleculeProcessor) {
        this.smallMoleculeProcessor = smallMoleculeProcessor;
    }

    public void startUpdate() throws SmallMoleculeUpdatorException, IntactTransactionException {

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        List<String> smallMoleculeAcs = dataContext.getDaoFactory().getEntityManager().createQuery( "select sm.ac from SmallMoleculeImpl sm order by sm.created" ).getResultList();
         //get list of smallmolecules and call updateByAcs
        smallMoleculeProcessor.updateByAcs(smallMoleculeAcs);
    }

    protected void commitTransaction() throws SmallMoleculeUpdatorException {
         DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        if (dataContext.isTransactionActive()) {
            try {
                dataContext.commitTransaction();
            } catch (IntactTransactionException e) {
                throw new IntactTestException(e);
            }
        }
    }
}
