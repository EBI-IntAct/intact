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
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvBiologicalRole;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.persistence.dao.ComponentDao;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * This mojo is executed when the goal "update" is called.
 *
 * @goal update
 * @phase process-resources
 */
public class SplitComponentRoleMojo extends IntactHibernateMojo {

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
     * Where the logs of the update are going to eb stored.
     *
     * @parameter default-value="${project.build.directory}/update-component-role.log"
     */
    private File logFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException {
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        MojoUtils.prepareFile( logFile );
        System.out.println( "Logs will be printed in: " + logFile.getAbsolutePath() );
        PrintStream log = new PrintStream( logFile );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        ComponentDao cdao = daoFactory.getComponentDao();

        CvObjectDao<CvBiologicalRole> bioDao = daoFactory.getCvObjectDao( CvBiologicalRole.class );
        CvObjectDao<CvExperimentalRole> expDao = daoFactory.getCvObjectDao( CvExperimentalRole.class );

        // 1. Load required CVs

        CvBiologicalRole unspecified = bioDao.getByPsiMiRef( CvBiologicalRole.UNSPECIFIED_PSI_REF );
        if ( unspecified == null ) {
            throw new MojoFailureException( "Could not find CvBiologicalRole by psi-mi id: " + CvBiologicalRole.UNSPECIFIED_PSI_REF );
        }

        CvExperimentalRole neutral = expDao.getByPsiMiRef( CvExperimentalRole.NEUTRAL_PSI_REF );
        if ( neutral == null ) {
            throw new MojoFailureException( "Could not find CvExperimentalRole by psi-mi id: " + CvExperimentalRole.NEUTRAL_PSI_REF );
        }

        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        cdao = daoFactory.getComponentDao();
        final int componentCount = cdao.countAll();

        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch ( IntactTransactionException e ) {
            throw new MojoExecutionException( "Failed to commit transaction.", e );
        }

        // 2. Update all Components

        int chunkSize = 10;
        int cur = 0;

        while ( cur < componentCount ) {

            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            cdao = daoFactory.getComponentDao();

            List<Component> components = cdao.getAll( cur, chunkSize );

            for ( Component component : components ) {

                CvBiologicalRole bio = component.getCvBiologicalRole();
                CvExperimentalRole exp = component.getCvExperimentalRole();

                if ( bio != null && exp != null ) {
                    // do nothing, it's up-to-date
                } else {
                    log.print( "\n\nUpdating Component: " + component.getAc() );

                    if ( bio == null ) {
                        log.print( "\n\tBiological role was null" );
                        component.setCvBiologicalRole( unspecified );
                        cdao.update( component );
                        log.print( "\n\tBiological role was set to unspecified" );
                    }

                    if ( exp == null ) {
                        log.print( "\n\tExperimental role was null" );
                        component.setCvExperimentalRole( neutral );
                        cdao.update( component );
                        log.print( "\n\tExperimental role was set to neutral" );
                    }
                }

                cur++;
            }

            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new MojoExecutionException( "Failed to commit transaction.", e );
            }
        }
    }

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
}