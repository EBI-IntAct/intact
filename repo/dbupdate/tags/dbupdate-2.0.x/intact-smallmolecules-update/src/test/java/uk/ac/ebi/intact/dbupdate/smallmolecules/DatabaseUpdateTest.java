/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.dbupdate.smallmolecules;

import org.junit.Test;
import org.junit.Ignore;
import uk.ac.ebi.intact.context.IntactContext;

import java.net.URL;
import java.io.File;

/**
 *
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public class DatabaseUpdateTest {

    @Ignore
    @Test
    public void zdev() throws Exception {
        final URL url = DatabaseUpdateTest.class.getResource( "/iweb2-hibernate.cfg.xml" );
        IntactContext.initStandaloneContext( new File( url.getFile() ) );

        SmallMoleculeUpdator updator = new SmallMoleculeUpdator( new ChebiProcessor() );

        updator.startUpdate();

        System.out.println( updator.getUpdateReport() );
    }
}
