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
package uk.ac.ebi.intact.plugins.reactome;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import uk.ac.ebi.intact.plugins.reactome.ReactomeExportMojo;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;

public class ReactomeExportMojoTest extends AbstractMojoTestCase
{

    public void testSimpleGeneration() throws Exception {
        File hibernateConfig = new File( ReactomeExportMojoTest.class.getResource( "/test-hibernate.cfg.xml" ).getFile() );
        IntactContext.initStandaloneContext( hibernateConfig );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        final int count = daoFactory.getInteractionDao().getAll().size();
        Assert.assertEquals( "The database should be empty and currently contains some interactions", 0, count );

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        Interaction interaction = mockBuilder.createInteractionRandomBinary();

        CvTopic curatedComplex = mockBuilder.createCvObject(CvTopic.class, null, CvTopic.CURATED_COMPLEX);
        CvDatabase reactomeComplex = mockBuilder.createCvObject(CvDatabase.class, CvDatabase.REACTOME_COMPLEX_PSI_REF, CvDatabase.REACTOME_COMPLEX);
        CvXrefQualifier xrefQual = mockBuilder.createCvObject(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF, CvXrefQualifier.IDENTITY);

        interaction.addAnnotation(mockBuilder.createAnnotation("complex1", curatedComplex));
        interaction.addXref(mockBuilder.createXref(interaction, "complex1", xrefQual, reactomeComplex));

        PersisterHelper.saveOrUpdate(curatedComplex, reactomeComplex, interaction);


        // testing the mojo
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-config.xml" );

        ReactomeExportMojo mojo = (ReactomeExportMojo) lookupMojo( "reactome-exp", pluginXmlFile );
        mojo.setHibernateConfig( hibernateConfig );
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        // assertions
        File exportedReactomeFile = mojo.getReactomeExportedFile();

        BufferedReader in = new BufferedReader(new FileReader(exportedReactomeFile));
        String firstLine = in.readLine();

        Assert.assertTrue(firstLine.contains(IntactContext.getCurrentInstance().getConfig().getAcPrefix()));
        Assert.assertTrue(firstLine.contains("complex1"));

        Assert.assertNull(in.readLine());
    }
}
