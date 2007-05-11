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
package uk.ac.ebi.intact.plugins.dbtest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.FileUtils;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.plugin.IntactAbstractMojo;
import uk.ac.ebi.intact.plugins.hibernateconfig.HibernateConfigCreatorMojo;
import uk.ac.ebi.intact.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Gets an existing H2 database with all the CVs loaded
 *
 * @goal h2-preload
 * @requiresDependencyResolution test
 * @phase generate-test-resources
 */
public class PreloadedH2Mojo
        extends IntactAbstractMojo {

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * project-helper instance, used to make addition of resources
     * simpler.
     *
     * @component
     */
    private MavenProjectHelper helper;

    /**
     * @parameter expression="${project.build.directory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;

    /**
     * Database path
     *
     * @parameter expression="${project.build.directory}/test-db
     */
    private File dbPath;

    /**
     * The scope where the file will be placed (possible values are "runtime" and "test").
     * If using "runtime" the config file will be available for runtime and tests. Default "test"
     *
     * @parameter default-value="test"
     */
    private String scope;

    /**
     * If true, don't preload the H2
     *
     * @parameter expression="${intact.h2preload.skip}" default-value="false"
     */
    private boolean skipPreload;

    private static final String TMP_DIR = System.getProperty( "java.io.tmpdir" );


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug( "Going to create hibernate config file: " + hibernateConfig );

        // 1. Create the hibernate.cfg.xml file automatically
        HibernateConfigCreatorMojo hibernateMojo = new HibernateConfigCreatorMojo();
        hibernateMojo.setTargetPath( hibernateConfig.getParent() );
        hibernateMojo.setFilename( hibernateConfig.getName() );
        hibernateMojo.setShowSql( false );
        hibernateMojo.setFormatSql( true );
        hibernateMojo.setHbm2ddlAuto( "none" );
        hibernateMojo.setScope( scope );
        hibernateMojo.setConnectionProviderClass( "org.hibernate.connection.C3P0ConnectionProvider" );

        hibernateMojo.setDriver( "org.h2.Driver" );
        hibernateMojo.setDialect( "org.hibernate.dialect.H2Dialect" );
        hibernateMojo.setUser( "sa" );
        hibernateMojo.setUrl( "jdbc:h2:" + dbPath );

        hibernateMojo.execute();

        // Adding the resources
        List includes = Collections.singletonList( hibernateConfig.getName() );
        List excludes = null;

        if ( scope.equalsIgnoreCase( "test" ) ) {
            // add the config only in test
            getLog().debug( "Adding hibernate config file to tests" );

            if ( project != null )
                helper.addTestResource( project, hibernateConfig.getParent(), includes, excludes );
        }

        if ( skipPreload ) {
            return;
        }

        // 2. Unzip and put the template database in the right place
        try {
            File zipDbFile;

            if ( getProject() != null ) {
                Artifact artifact = lookupZippedDb();
                zipDbFile = artifact.getFile();
            } else {
                // this is a hack, so the test works
                zipDbFile = new File( "src/main/resources/h2/h2db.zip" );
                getLog().warn( "MavenProject is null, using zip: " + zipDbFile );
            }

            getLog().debug( "Going to unzip: " + zipDbFile );

            List<File> dbFiles = Utilities.unzip( zipDbFile, new File( TMP_DIR ) );

            for ( File dbFile : dbFiles ) {
                moveToExpectedPath( dbFile );
            }
        }
        catch ( Throwable e ) {
            e.printStackTrace();
            throw new MojoExecutionException( "Problems creating database from template", e );
        }
    }

    private Artifact lookupZippedDb() {
        Set<Artifact> artifacts = project.getDependencyArtifacts();

        for ( Artifact artifact : artifacts ) {
            if ( artifact.getGroupId().equals( "uk.ac.ebi.intact.templates" )
                 && artifact.getArtifactId().equals( "h2db-with-cv" ) ) {
                return artifact;
            }
        }

        throw new IntactException( "To be able to run the 'h2-preload' goal " +
                                   "you need to declare the h2db dependency. \ne.g. \n\n" +
                                   "   <dependency>\n" +
                                   "      <groupId>uk.ac.ebi.intact.templates</groupId>\n" +
                                   "      <artifactId>h2db-with-cv</artifactId>\n" +
                                   "      <version>1.6.0-20070509</version> (or newer)\n" +
                                   "      <type>zip</type>\n" +
                                   "   </dependency>\n\n" );
    }

    private void moveToExpectedPath( File dbFile ) throws IOException {
        File parentDir = dbPath.getParentFile();
        String dbName = dbPath.getName();

        File destFile = new File( parentDir, dbFile.getName().replaceAll( "h2db\\d*", dbName ) );

        getLog().debug( "Creating file from template: " + destFile );

        FileUtils.copyFile( dbFile, destFile );
        dbFile.delete();
    }


    public MavenProject getProject() {
        return project;
    }

    public File getHibernateConfig() {
        return hibernateConfig;
    }

    public void setHibernateConfig( File hibernateConfig ) {
        this.hibernateConfig = hibernateConfig;
    }

    public File getDbPath() {
        return dbPath;
    }

    public void setDbPath( File dbPath ) {
        this.dbPath = dbPath;
    }

    public String getScope() {
        return scope;
    }

    public void setScope( String scope ) {
        this.scope = scope;
    }
}
