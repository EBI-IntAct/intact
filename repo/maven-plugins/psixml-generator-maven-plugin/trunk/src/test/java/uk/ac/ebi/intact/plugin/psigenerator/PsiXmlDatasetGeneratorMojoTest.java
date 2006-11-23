/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.dbutil.update.UpdateTargetSpecies;

import java.io.File;

/**
 * Test production of the dataset xml files.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09/10/2006</pre>
 */
public class PsiXmlDatasetGeneratorMojoTest extends AbstractMojoTestCase {
    public void testSimpleGeneration() throws Exception {

        // update target species
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        UpdateTargetSpecies.update(System.out, false);
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/config-only-dataset.xml" );

        PsiXmlGeneratorMojo mojo = (PsiXmlGeneratorMojo) lookupMojo( "psi", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();
    }
}