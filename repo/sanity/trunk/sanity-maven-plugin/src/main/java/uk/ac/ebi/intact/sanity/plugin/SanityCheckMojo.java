package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.sanity.check.SanityChecker;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;

import java.io.IOException;

/**
 * MOJO that runs the sanity check.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * 
 * @goal sanity-check
 * @phase process-resources
 */
public class SanityCheckMojo extends AbstractSanityMojo {

    protected void executeSanityMojo(SanityCheckConfig sanityConfig) throws MojoExecutionException {
        try {
            if (IntactContext.getCurrentInstance().getDataContext().isTransactionActive()) {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            }

            SanityChecker.executeSanityCheck(sanityConfig);
            
        } catch (Exception e) {
            throw new MojoExecutionException("Exception executing the sanity check", e);
        }
    }
}
