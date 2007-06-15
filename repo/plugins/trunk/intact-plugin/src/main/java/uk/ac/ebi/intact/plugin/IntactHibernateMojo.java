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
package uk.ac.ebi.intact.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.config.impl.InMemoryDataConfig;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.io.File;
import java.io.IOException;

/**
 * Base class for plugins that require hibernate access
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactHibernateMojo extends IntactAbstractMojo {

    /**
     * @parameter
     */
    private boolean dryRun;

    /**
     * Close the session factory after executing the plugin
     *
     * @parameter default-value="true"
     */
    private boolean closeSessionFactory;

    private boolean initialized;

    public void execute() throws MojoExecutionException, MojoFailureException {

        initializeHibernate();

        // start a transaction
        IntactContext context = IntactContext.getCurrentInstance();
        context.getDataContext().beginTransaction();

        try {
            executeIntactMojo();
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Problems executing Mojo", e );
        }


        try {
            context.getDataContext().commitAllActiveTransactions();
        } catch ( IntactTransactionException e ) {
            throw new MojoExecutionException( "Failed to commit active transactions.", e );
        }

        if ( closeSessionFactory ) {
            context.getConfig().getDefaultDataConfig().closeSessionFactory();
        }
    }

    protected abstract void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException;

    protected void initializeHibernate() throws MojoExecutionException {
        if ( initialized ) {
            return;
        }

        File hibernateConfig = getHibernateConfig();

        IntactSession session = new StandaloneSession();
        DataConfig dataConfig = getDataConfig();

        if (dataConfig == null) {
            if ( hibernateConfig == null ) {
                if ( getProject() != null ) {
                    hibernateConfig = new File( getDirectory(), "hibernate/config/hibernate.cfg.xml" );
                } else {
                    hibernateConfig = new File( "target/hibernate/config/hibernate.cfg.xml" );
                }

                if (hibernateConfig.exists()) {
                    getLog().info( "Using hibernate cfg file: " + hibernateConfig );
                    dataConfig = new CustomCoreDataConfig( "PluginHibernateConfig", hibernateConfig, session );
                } else {
                    getLog().info( "Using hibernate with a database in memory (no hibernate.cfg.xml file provided)" );
                    dataConfig = new InMemoryDataConfig(session);
                }
            } else {
                getLog().warn("Both hibernate file and data config are not nulls. Will use the data config");
            }
        }

        getLog().info( "Initializing data config: "+dataConfig.getName() );

        dataConfig.initialize();
        IntactContext.initContext( dataConfig, session );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        try {

            getLog().info( "Database instance: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbName() );

        }
        catch ( Exception e ) {
            throw new MojoExecutionException( "Error loading database name", e );
        }

        getLog().info( "User: " + IntactContext.getCurrentInstance().getUserContext().getUserId() );

        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch ( IntactTransactionException e ) {
            throw new MojoExecutionException( "Failed to commit transaction.", e );
        }

        initialized = true;
    }

    public DataConfig getDataConfig() {
        return null;
    }

    public File getHibernateConfig() {
        return null;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun( boolean dryRun ) {
        this.dryRun = dryRun;
    }
}
