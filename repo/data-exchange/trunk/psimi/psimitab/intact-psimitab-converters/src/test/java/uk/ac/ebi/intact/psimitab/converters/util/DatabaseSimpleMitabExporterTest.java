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
package uk.ac.ebi.intact.psimitab.converters.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.ProteinImpl;

import java.io.StringWriter;

/**
 * DatabaseSimpleMitabExporter Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.2
 */
public class DatabaseSimpleMitabExporterTest extends IntactBasicTestCase {

    private DatabaseSimpleMitabExporter exporter;

    @Before
    public void before() throws Exception {
        exporter = new DatabaseSimpleMitabExporter();
        SchemaUtils.createSchema();
    }

    @After
    public void after() throws Exception {
        exporter = null;
        IntactContext.closeCurrentInstance();
    }

    @Test
    public void exportInteractors() throws Exception {

        // this interaction is going to generate 2 binary i1 via the spoke expansion
        Interaction i1 = getMockBuilder().createInteraction("a1", "a2", "a3");

        // this interaction is going to be exported even though a1-a2 is already in the i1 above (no clustering)
        Interaction i2 = getMockBuilder().createInteraction("a1", "a2");

        // this interaction is going to be exported even though a1-a2 is already in the i1 above (no clustering)
        Interaction i3 = getMockBuilder().createInteraction("a4", "a2");

        final Interactor interactor = i1.getComponents().iterator().next().getInteractor();
        CvDatabase goDb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.GO_MI_REF, CvDatabase.GO);
        interactor.addXref(getMockBuilder().createXref(interactor, "GO:0007028", null, goDb));

        PersisterHelper.saveOrUpdate( i1, i2, i3);

        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getInteractorDao( ProteinImpl.class ).countAll());

        StringWriter mitabWriter = new StringWriter();

        exporter.exportAllInteractors(mitabWriter);

        mitabWriter.close();

        final String mitab = mitabWriter.toString();
        final String[] lines = mitab.split( "\n" );
        Assert.assertEquals(4, lines.length);
        for ( String line : lines ) {
            System.out.println( line );
        }
    }
}
