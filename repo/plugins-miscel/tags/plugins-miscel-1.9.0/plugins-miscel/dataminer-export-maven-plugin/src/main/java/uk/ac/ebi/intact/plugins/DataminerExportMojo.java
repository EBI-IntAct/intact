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
package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.hibernate.Session;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.BaseDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Exports a tab separated file based on the source-text annotation present in the database.
 *
 * @goal export
 * @phase process-resources
 */
public class DataminerExportMojo extends IntactHibernateMojo {

    public static final String TAB = "\t";
    public static final String NEW_LINE = "\n";

    public static final String SQL_REPORT = "SELECT x.primaryid as PMID, i2e.interaction_ac as interaction_ac, a.description as source_text " +
                                            "FROM   ia_annotation a, ia_int2annot e2a, ia_interactor i, ia_int2exp i2e, ia_experiment_xref x " +
                                            "WHERE  a.ac=e2a.annotation_ac " +
                                            "       AND e2a.interactor_ac=i.ac " +
                                            "       AND i.ac=i2e.interaction_ac " +
                                            "       AND x.parent_ac=i2e.experiment_ac " +
                                            "       AND a.topic_ac = 'EBI-878353' " +
                                            "ORDER BY x.primaryid, i2e.experiment_ac, i2e.interaction_ac";

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${project.build.directory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    protected File hibernateConfig;

    /**
     * Name of the export file to be created
     *
     * @property default-value="${maven.build.directory}/dataminer-export.txt"
     */
    private File exportFile;

    /////////////////////////
    // Getters and Setters

    public File getExportFile() {
        return exportFile;
    }

    public void setExportFile( File exportFile ) {
        this.exportFile = exportFile;
    }

    /////////////////////////////////
    // IntactHibernateMojo

    /**
     * Implementation of abstract method from superclass
     */
    public MavenProject getProject() {
        return project;
    }

    /**
     * Implementation of abstract method from superclass
     */
    public File getHibernateConfig() {
        return hibernateConfig;
    }

    ///////////////////////////////
    // Goal

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException {

        getLog().info( "Starting export" );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        BaseDao dao = daoFactory.getBaseDao();
        Connection con = ( ( Session ) dao.getSession() ).connection();

        // build query
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = con.createStatement();
            getLog().info( "Running SQL: " + SQL_REPORT );
            rs = statement.executeQuery( SQL_REPORT );

            // write tab-separated content
            getLog().info( "Writing SQL result to tab separated file: " + exportFile.getAbsolutePath() );
            int count = 0;

            try {
                BufferedWriter out = new BufferedWriter( new FileWriter( exportFile ) );

                // write header
                out.write( "Publication_ID" + TAB + "Interaction_AC" + TAB + "Source_Text" );

                while ( rs.next() ) {
                    count++;
                    out.write( rs.getString( 1 ) );
                    out.write( TAB );
                    out.write( rs.getString( 2 ) );
                    out.write( TAB );
                    out.write( rs.getString( 3 ) );
                    out.write( NEW_LINE );
                }

                out.flush();
                out.close();
            } catch ( IOException e ) {
                throw new MojoExecutionException( "Failed to write export file.", e );
            }

            getLog().info( "File closed: " + exportFile.getAbsolutePath() );
            getLog().info( "Line exported: " + count );

        } catch ( SQLException e ) {
            throw new MojoExecutionException( "Failure while accessing the database.", e );
        } finally {
            // close resources
            try {
                if ( rs != null ) {
                    rs.close();
                }

                if ( statement != null ) {
                    statement.close();
                }

                if ( con != null ) {
                    con.close();
                }
            } catch ( SQLException e ) {
                throw new MojoExecutionException( "Failure while closing database resources.", e );
            }
        }

        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch ( IntactTransactionException e ) {
            throw new MojoExecutionException( "Failed to close IntAct transaction after export.", e );
        }

        getLog().info( "Export completed." );
    }
}
