package uk.ac.ebi.intact.editor.batch.admin;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.dataexchange.dbimporter.writer.IntactInteractionMixDbImporter;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 * Editor extension of db importer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/15</pre>
 */

public class EditorMixDbImporter extends IntactInteractionMixDbImporter implements StepExecutionListener {

    @Override
    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void beforeStep(StepExecution stepExecution) {
        getInteractionEvidenceService().getIntactDao().getEntityManager().clear();
        getComplexService().getIntactDao().getEntityManager().clear();
        getModelledInteractionService().getIntactDao().getEntityManager().clear();

        String userLogin = stepExecution.getJobParameters().getString("user.login");
        User user = getInteractionEvidenceService().getIntactDao().getUserDao().getByLogin(userLogin);
        if (user != null){
            getInteractionEvidenceService().getIntactDao().getUserContext().setUser(user);
            getComplexService().getIntactDao().getUserContext().setUser(user);
            getModelledInteractionService().getIntactDao().getUserContext().setUser(user);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
