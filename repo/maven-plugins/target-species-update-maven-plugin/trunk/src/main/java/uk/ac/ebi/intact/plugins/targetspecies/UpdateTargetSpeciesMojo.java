/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.plugins.targetspecies;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.log4j.Priority;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.plugins.UpdateAbstractMojo;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.util.LogUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * Example mojo. This mojo is executed when the goal "target-species" is called.
 *
 * @goal target-species
 * @phase process-resources
 */
public class UpdateTargetSpeciesMojo extends UpdateAbstractMojo {

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
            throws MojoExecutionException, MojoFailureException, IOException {
        LogUtils.setPrintSql(false);

        UpdateTargetSpecies updateTargetSpecies = new UpdateTargetSpecies();

        int firstResult = 0;
        int maxResults = 100;

        Collection<Experiment> experiments;

        do {
            beginTransaction();
            experiments = getDataContext().getDaoFactory().getExperimentDao().getAll(firstResult, maxResults);
            commitTransaction();

            for (Experiment experiment : experiments) {
                updateTargetSpecies.updateExperiment(experiment);
            }

            firstResult += maxResults;
        } while (!experiments.isEmpty());

    }

    protected DataContext getDataContext() {
        return IntactContext.getCurrentInstance().getDataContext();
    }

    protected void beginTransaction() {
        getDataContext().beginTransaction();
    }

    protected void commitTransaction() {
        try {
            getDataContext().commitTransaction();
        } catch (IntactTransactionException e) {
            throw new IntactException(e);
        }
    }

    protected Priority getLogPriority()
    {
        return Priority.DEBUG;
    }
}
