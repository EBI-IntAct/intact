/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.sanity.commons.rules.report;

import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.rules.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AbstractReportTestCase extends IntactBasicTestCase {

    protected SanityReport getDefaultSanityReport() {
        List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i=0; i<5; i++) {
            Protein prot = getMockBuilder().createProteinRandom();
            prot.setAc("PROT-"+i);
            prot.setCreated(new Date(new java.util.Random().nextInt()));
            prot.setUpdated(new Date());
            prot.setCreator("peter");
            prot.setUpdator("peter");
            prot.setOwner(new Institution("institution"));
            messages.add(new GeneralMessage("description1", MessageLevel.NORMAL, "suggestion1", prot));
        }

        for (int i=0; i<3; i++) {
            Experiment exp = getMockBuilder().createExperimentRandom(1);
            exp.setAc("EXP-"+i);
            exp.setCreated(new Date(new java.util.Random().nextInt()));
            exp.setUpdated(new Date());
            exp.setCreator("anne");
            exp.setUpdator("anne");
            exp.setOwner(new Institution("institution"));
            messages.add(new GeneralMessage("description2", MessageLevel.MINOR, "suggestion2", exp));
        }

        return MessageUtils.toSanityReport(messages);
    }

    protected SanityReport getAlternativeSanityReport() {
        List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i=0; i<5; i++) {
            Protein prot = getMockBuilder().createProteinRandom();
            prot.setAc("PROT-"+i);
            prot.setCreated(new Date(new java.util.Random().nextInt()));
            prot.setUpdated(new Date());
            prot.setCreator("peter");
            prot.setUpdator("peter");
            prot.setOwner(new Institution("institution"));
            messages.add(new XrefMessage("description1", MessageLevel.NORMAL, "suggestion1", prot, prot.getXrefs().iterator().next()));
        }

        for (int i=0; i<3; i++) {
            Experiment exp = getMockBuilder().createExperimentRandom(1);
            exp.setAc("EXP-"+i);
            exp.setCreated(new Date(new java.util.Random().nextInt()));
            exp.setUpdated(new Date());
            exp.setCreator("anne");
            exp.setUpdator("anne");
            exp.setOwner(new Institution("institution"));

            Annotation annot = getMockBuilder().createAnnotationRandom();
            exp.addAnnotation(annot);

            messages.add(new AnnotationMessage("description2", MessageLevel.MINOR, "suggestion2", exp, annot));
        }

        return MessageUtils.toSanityReport(messages);
    }

}