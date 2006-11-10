/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins.dbupdate;

import static uk.ac.ebi.intact.dbutil.mine.MineDatabaseFill.buildDatabase;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Example mojo. This mojo is executed when the goal "mine" is called.
 *
 * @goal mine
 * @phase process-resources
 */
public class UpdateMiNeTablesMojo extends IntactHibernateMojo {

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException {
        try {
            buildDatabase();
        } catch ( SQLException e ) {
            throw new MojoExecutionException( "SQL error while building the MiNe table. cf. nested Exception !", e );
        }
    }
}