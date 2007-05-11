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

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.persistence.dao.ComponentDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.File;
import java.util.List;
import java.util.Iterator;

public class SplitComponentRoleMojoTest extends AbstractMojoTestCase {

    public void testSimpleGeneration() throws Exception {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-config.xml" );

        SplitComponentRoleMojo mojo = ( SplitComponentRoleMojo ) lookupMojo( "update", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        ComponentDao cdao = daoFactory.getComponentDao();
        final int componentCount = cdao.countAll();

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        if ( componentCount == 0 ) {
            fail( "No component found in the database. Make sure first that data were uploaded before running this test." );
        }

        int chunkSize = 10;
        int cur = 0;

        while ( cur < componentCount ) {

            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            cdao = daoFactory.getComponentDao();

            List<Component> components = cdao.getAll( cur, chunkSize );
            int i = 0;

            for ( Component component : components ) {

                i++;

                if ( ( i % 2 ) == 0 ) {
                    // remove experimental role
                    System.out.println( "reset experimental role" );
                    component.setCvExperimentalRole( null );
                    cdao.update( component );
                } else {
                    // remove biological role
                    System.out.println( "reset biological role" );
                    component.setCvBiologicalRole( null );
                    cdao.update( component );
                }

                cur++;
            }
            
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }


        mojo.execute();

        // now check that all components ahve both roles.

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        cdao = daoFactory.getComponentDao();
        Iterator<Component> ci = cdao.iterator();

        int c2 = cdao.countAll();
        assertEquals( "Count of component has changed after running the SplitComponentRoleMojo: " +
                      "was: " + componentCount + " after: " + c2, componentCount, c2 );

        while ( ci.hasNext() ) {
            Component component = ci.next();
            assertNotNull( "ExperimentalRole was null on component: " + component.getAc(), component.getCvExperimentalRole() );
            assertNotNull( "BiologicalRole was null on component: " + component.getAc(), component.getCvBiologicalRole() );
        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }
}
