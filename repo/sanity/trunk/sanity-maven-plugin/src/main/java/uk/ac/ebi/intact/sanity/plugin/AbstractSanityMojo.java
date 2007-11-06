package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Abstract MOJO used for sanity check and related tasks (eg. correction assigner...).
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractSanityMojo extends IntactHibernateMojo {
    
    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter
     * @required
     */
    protected List<Curator> curators;

    /**
     * @parameter
     * @required
     */
    protected String editorUrl;

    /**
     * @parameter
     */
    protected boolean enableAdminEmails;

    /**
     * @parameter
     */
    protected boolean enableUserEmails;

    /**
     * @parameter
     */
    protected String emailSubjectPrefix;

    /**
     * @parameter
     */
    protected File hibernateConfig;

    protected void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException {
        if (curators == null) {
            throw new MojoFailureException("No curators configured: curators are null");
        }

        if (curators.isEmpty()) {
            throw new MojoFailureException("No curators configured");
        }

        getLog().info("Curators found: " + curators.size());

        if (getLog().isDebugEnabled()) {
            for (uk.ac.ebi.intact.sanity.check.config.Curator curator : curators) {
                getLog().debug(curator.toString());
            }
        }

        SanityCheckConfig sanityConfig = new SanityCheckConfig(curators);
        sanityConfig.setEditorUrl(editorUrl);
        sanityConfig.setEnableAdminMails(enableAdminEmails);
        sanityConfig.setEnableUserMails(enableUserEmails);
        sanityConfig.setEmailSubjectPrefix(emailSubjectPrefix);

        executeSanityMojo(sanityConfig);
    }

    protected abstract void executeSanityMojo(SanityCheckConfig sanityConfig) throws MojoExecutionException, MojoFailureException, IOException;

    public MavenProject getProject() {
        return project;
    }

    public File getHibernateConfig() {
        return hibernateConfig;
    }

    public List<Curator> getCurators() {
        return Collections.unmodifiableList( curators );
    }

    public String getEditorUrl() {
        return editorUrl;
    }

    public boolean isEnableAdminEmails() {
        return enableAdminEmails;
    }

    public boolean isEnableUserEmails() {
        return enableUserEmails;
    }

    public String getEmailSubjectPrefix() {
        return emailSubjectPrefix;
    }
}