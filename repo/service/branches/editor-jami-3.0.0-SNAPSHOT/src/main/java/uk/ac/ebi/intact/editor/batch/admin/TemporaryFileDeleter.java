package uk.ac.ebi.intact.editor.batch.admin;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import psidev.psi.mi.jami.batch.SimpleJobListener;

import java.io.File;
import java.util.List;

/**
 * This job listener will delete any temporary file created with job id successful
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/12/14</pre>
 */

public class TemporaryFileDeleter extends SimpleJobListener{

    private List<String> filesToDeletes;

    public void afterJob(JobExecution jobExecution) {
        super.afterJob(jobExecution);

        if (jobExecution.getExitStatus() != null && jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)){
             for (String fileName : filesToDeletes){
                 File file = new File(fileName);
                 if (file.exists()){
                     file.delete();
                 }
             }
        }
    }

    public List<String> getFilesToDeletes() {
        return filesToDeletes;
    }

    public void setFilesToDeletes(List<String> filesToDeletes) {
        this.filesToDeletes = filesToDeletes;
    }
}
