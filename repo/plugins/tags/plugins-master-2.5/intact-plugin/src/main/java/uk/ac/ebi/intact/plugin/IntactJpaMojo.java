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
import uk.ac.ebi.intact.core.context.IntactContext;

import java.io.File;
import java.io.IOException;

/**
 * Base class for plugins that require JPA (It does not start a transaction).
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactJpaMojo extends IntactAbstractMojo {

   

    /**
     * @parameter
     */
    private boolean dryRun;

    /**
     * Close the session factory after executing the plugin
     *
     * @parameter default-value="true"
     */
    private boolean closeSessionFactory = true;

    private boolean initialized;

    public void execute() throws MojoExecutionException, MojoFailureException {
        enableLogging();

        initializeJpa();

        try {
            executeIntactMojo();
        }
        catch ( IOException e ) {                                
            throw new MojoExecutionException( "Problems executing Mojo", e );
        }
    }

    protected abstract void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException;

    protected synchronized void initializeJpa() throws MojoExecutionException {
        if ( initialized ) {
            return;
        }

        System.out.println( "springConfig = " + getSpringConfig() );

        if( ! IntactContext.currentInstanceExists() ) {
            if(getSpringConfig() != null ) {
                final File springConfigFile = new File( getSpringConfig() );
                if( ! springConfigFile.exists() ) {
                    throw new  MojoExecutionException( "Could not find spring config file: " + springConfigFile.getAbsolutePath() );
                }
                getLog().info( "Initializing JPA using user provided configuration file: " + springConfigFile.toURI().toString() );
                IntactContext.initContext( new String[]{ springConfigFile.toURI().toString() } );
            } else {
                getLog().info( "Initializing IntactContext in memory (stand alone database)" );
                IntactContext.initStandaloneContextInMemory();
            }
        } else {
            getLog().info( "IntactContext was already initialized." );
        }

        try {
            getLog().info( "Database instance: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbName() );
        }
        catch ( Exception e ) {
            throw new MojoExecutionException( "Error loading database name", e );
        }

        getLog().info( "User: " + IntactContext.getCurrentInstance().getUserContext().getUserId() );

        initialized = true;
    }

    abstract public String getSpringConfig();

    abstract public void setSpringConfig( String springConfig );

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun( boolean dryRun ) {
        this.dryRun = dryRun;
    }

    public boolean isCloseSessionFactory() {
        return closeSessionFactory;
    }
}