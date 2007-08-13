package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.correctionassigner.Assigner;

import java.io.IOException;
import java.sql.SQLException;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @goal assigner
 * @phase process-resources
 */
public class AssignerMojo extends AbstractSanityMojo {

    protected void executeSanityMojo(SanityCheckConfig sanityConfig) throws MojoExecutionException, MojoFailureException, IOException {
        try {
            Assigner assigner = new Assigner(sanityConfig, false);
            assigner.assign();
        }
        catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Problem executing the correction assigner", e);
        }
    }
}