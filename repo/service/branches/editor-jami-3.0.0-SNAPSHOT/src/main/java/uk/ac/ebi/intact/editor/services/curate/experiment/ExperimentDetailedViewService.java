/**
 * Copyright 2012 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.editor.services.curate.experiment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.editor.controller.curate.experiment.ExperimentWrapper;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;

import javax.annotation.Resource;

/**
 * Service for experimentalDetailsController
 */
@Service
public class ExperimentDetailedViewService extends AbstractEditorService {

    @Resource(name = "experimentEditorService")
    private ExperimentEditorService experimentEditorService;

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public ExperimentWrapper loadExperimentWrapperByAc( String ac ) {
        IntactExperiment experiment = experimentEditorService.loadExperimentByAc(ac);

        if (experiment != null) {
            return new ExperimentWrapper(experiment);
        } else {
            return null;
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public ExperimentWrapper loadExperimentWrapper( IntactExperiment experiment ) {
        IntactExperiment reloaded = reattachIntactObjectIfTransient(experiment, getIntactDao().getExperimentDao());

        ExperimentWrapper experimentWrapper = new ExperimentWrapper(reloaded);

        getIntactDao().getEntityManager().detach(reloaded);

        return experimentWrapper;
    }
}
