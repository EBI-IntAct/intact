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
package uk.ac.ebi.intact.plugins.ontology.index;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.Sort;
import org.junit.Test;

import java.io.File;

import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexSearcher;
import uk.ac.ebi.intact.bridges.ontologies.OntologyHits;
import uk.ac.ebi.intact.bridges.ontologies.FieldName;
import junit.framework.Assert;

/**
 * Class to test OntologyIndexerMojo
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class OntologyIndexerMojoTest extends AbstractMojoTestCase {

    @Test
    public void testOntologyIndex() throws Exception {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/ontologyindex-config.xml" );

        OntologyIndexerMojo mojo = ( OntologyIndexerMojo ) lookupMojo( "ontologyindex", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();
        Directory ontologiesIndex = FSDirectory.getDirectory( mojo.getIndexDirectory() );
        OntologyIndexSearcher ontologySearcher = new OntologyIndexSearcher( ontologiesIndex );

        //Test GO
        final OntologyHits ontologyHitsGO = ontologySearcher.searchByChildId( "GO:0030154", new Sort( FieldName.CHILDREN_NAME_SORTABLE ) );
        Assert.assertEquals( 1, ontologyHitsGO.length() );

        Assert.assertEquals( "go", ontologyHitsGO.doc( 0 ).getOntology() );
        Assert.assertEquals( "GO:0030154", ontologyHitsGO.doc( 0 ).getChildId() );
        Assert.assertEquals( "cell differentiation", ontologyHitsGO.doc( 0 ).getChildName() );

        //Test Chebi
        final OntologyHits ontologyHitsChebi = ontologySearcher.searchByChildId( "CHEBI:46195", new Sort( FieldName.CHILDREN_NAME_SORTABLE ) );
        Assert.assertEquals( 3, ontologyHitsChebi.length() );

        Assert.assertEquals( "chebi", ontologyHitsChebi.doc( 0 ).getOntology() );
        Assert.assertEquals( "CHEBI:46195", ontologyHitsChebi.doc( 0 ).getChildId() );
        Assert.assertEquals( "paracetamol", ontologyHitsChebi.doc( 0 ).getChildName() );

        // Test psimi
        final OntologyHits ontologyHitsPsimi = ontologySearcher.searchByChildId( "MI:0018", new Sort( FieldName.CHILDREN_NAME_SORTABLE ) );
        Assert.assertEquals( 1, ontologyHitsPsimi.length() );

        Assert.assertEquals( "psi-mi", ontologyHitsPsimi.doc( 0 ).getOntology() );
        Assert.assertEquals( "MI:0018", ontologyHitsPsimi.doc( 0 ).getChildId() );
        Assert.assertEquals( "two hybrid", ontologyHitsPsimi.doc( 0 ).getChildName() );

        //Test InterPro
        final OntologyHits ontologyHitsInterpro = ontologySearcher.searchByChildId( "IPR002168", new Sort( FieldName.CHILDREN_NAME_SORTABLE ) );
        Assert.assertEquals( 1, ontologyHitsInterpro.length() );

        Assert.assertEquals( "interpro", ontologyHitsInterpro.doc( 0 ).getOntology() );
        Assert.assertEquals( "IPR002168", ontologyHitsInterpro.doc( 0 ).getChildId() );
        Assert.assertEquals( "Lipase, GDXG, active site", ontologyHitsInterpro.doc( 0 ).getChildName() );

    }


}
