package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.sanity.check.SanityChecker;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;

import java.io.IOException;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * 
 * @goal sanity-check
 * @phase process-resources
 */
public class SanityCheckMojo extends AbstractSanityMojo {

    protected void executeSanityMojo(SanityCheckConfig sanityConfig) throws MojoExecutionException, MojoFailureException, IOException {
        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch (IntactTransactionException e) {
            throw new MojoExecutionException("Problem committing transaction?");
        }

        SanityChecker.executeSanityCheck(sanityConfig);
    }
}
