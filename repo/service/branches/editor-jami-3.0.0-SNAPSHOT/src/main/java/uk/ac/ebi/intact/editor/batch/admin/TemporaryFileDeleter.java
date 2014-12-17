package uk.ac.ebi.intact.editor.batch.admin;

import org.springframework.batch.core.JobExecution;
import psidev.psi.mi.jami.batch.SimpleJobListener;

/**
 * This job listener will delete any temporary file created with job id successful
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/12/14</pre>
 */

public class TemporaryFileDeleter extends SimpleJobListener{


    public void afterJob(JobExecution jobExecution) {
        super.afterJob(jobExecution);
    }
}
