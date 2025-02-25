/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.confidence.main;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.confidence.dataRetriever.uniprot.UniprotDataRetriever;
import uk.ac.ebi.intact.confidence.filter.GOAFilter;
import uk.ac.ebi.intact.confidence.filter.GOAFilterMapImpl;
import uk.ac.ebi.intact.confidence.global.GlobalTestData;
import uk.ac.ebi.intact.confidence.model.*;
import uk.ac.ebi.intact.confidence.model.io.BinaryInteractionAttributesWriter;
import uk.ac.ebi.intact.confidence.model.io.BinaryInteractionReader;
import uk.ac.ebi.intact.confidence.model.io.ProteinAnnotationWriter;
import uk.ac.ebi.intact.confidence.model.io.impl.BinaryInteractionAttributesWriterImpl;
import uk.ac.ebi.intact.confidence.model.io.impl.BinaryInteractionReaderImpl;
import uk.ac.ebi.intact.confidence.model.io.impl.GisAttributesWriterImpl;
import uk.ac.ebi.intact.confidence.model.io.impl.ProteinAnnotationWriterImpl;
import uk.ac.ebi.intact.confidence.utils.CombineToAttribs;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * TODO comment that class header
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.6.0
 *        <pre>
 *        12-Dec-2007
 *        </pre>
 */
public class WorkFlowTest {

     @Test
    public void testReadBinaryInts_writeAnno() throws Exception {
        BinaryInteractionReader bir = new BinaryInteractionReaderImpl();
        File inFile = new File(WorkFlowTest.class.getResource( "conf_set.txt").getPath());
         File goaFile = new File( WorkFlowTest.class.getResource( "goaTest.txt" ).getPath());

        File goOutFile = new File( GlobalTestData.getTargetDirectory(), "conf_set_go.txt");
        if (goOutFile.exists()){
            goOutFile.delete();
        }
        ProteinAnnotationWriter paw = new ProteinAnnotationWriterImpl();

        File goAttribFile = new File(GlobalTestData.getTargetDirectory(), "conf_set_go_attr_out.txt");
        if (goAttribFile.exists()){
            goAttribFile.delete();
        }

        File gisFile = new File(GlobalTestData.getTargetDirectory(), "gis_model_out.txt");
         if (gisFile.exists()){
            gisFile.delete();
        }

        Iterator<BinaryInteraction> birIter = bir.iterate( inFile);
        GOAFilter filter = new GOAFilterMapImpl();
         filter.initialize( goaFile );
        while(birIter.hasNext()){
            BinaryInteraction biInt = birIter.next();
            UniprotDataRetriever udr = new UniprotDataRetriever();
            Set<Identifier> gosA = udr.getGOs( biInt.getFirstId());
            Set<Identifier> gosB = udr.getGOs(biInt.getSecondId());
            filter.filterGO( biInt.getFirstId(), gosA );
            filter.filterGO(biInt.getSecondId(), gosB);
            ProteinAnnotation pA = new ProteinAnnotation( biInt.getFirstId(), gosA);
            ProteinAnnotation pB = new ProteinAnnotation( biInt.getSecondId(), gosB);
            //writing the annotation
            paw.append(pA, goOutFile);
            paw.append(pB, goOutFile);

            //combining the annotation
            List<Attribute> attribs = CombineToAttribs.combine(gosA, gosB );
            BinaryInteractionAttributes biAttr = new BinaryInteractionAttributes( biInt.getFirstId(), biInt.getSecondId(), attribs, Confidence.UNKNOWN);
            BinaryInteractionAttributesWriter biaw = new BinaryInteractionAttributesWriterImpl();
            biaw.append( biAttr, goAttribFile);

            BinaryInteractionAttributesWriter biawGis = new GisAttributesWriterImpl();
            biawGis.append( biAttr, gisFile);
        }
         Assert.assertTrue( goOutFile.exists() );
         Assert.assertTrue( goAttribFile.exists() );
         Assert.assertTrue( gisFile.exists() );
    }

//    @Test
//    public void givenLcAndHc_getAttribs() throws Exception {
//
//    }


}
