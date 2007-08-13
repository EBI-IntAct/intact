package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.correctionassigner.Assigner;
import uk.ac.ebi.intact.sanity.check.range.RangeChecker;

import java.io.IOException;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 * @goal rangecheck
 * @phase process-resources
 */
public class RangeCheckerMojo extends AbstractSanityMojo {

    protected void executeSanityMojo(SanityCheckConfig sanityConfig) throws MojoExecutionException, MojoFailureException, IOException {
        try {

            RangeChecker rangeChecker = new RangeChecker(sanityConfig);
            rangeChecker.check(RangeChecker.loadProteinAcs());
        }
        catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Problem executing the range checker", e);
        }
    }
}