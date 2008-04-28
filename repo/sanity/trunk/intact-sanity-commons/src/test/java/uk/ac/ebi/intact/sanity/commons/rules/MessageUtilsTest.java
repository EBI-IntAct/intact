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
package uk.ac.ebi.intact.sanity.commons.rules;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.report.SanityReport;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MessageUtilsTest extends IntactBasicTestCase {

    @Test
    public void groupMessagesByDescription() throws Exception {
        List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i = 0; i < 5; i++) {
            messages.add(new GeneralMessage(MessageDefinition.BROKEN_URL, getMockBuilder().createProteinRandom()));
        }

        for (int i = 0; i < 3; i++) {
            messages.add(new GeneralMessage(MessageDefinition.PROTEIN_UNIPROT_NO_XREF, getMockBuilder().createProteinRandom()));
        }

        Map<String, Collection<GeneralMessage>> messagesByKey = MessageUtils.groupMessagesByKey(messages);

        Assert.assertEquals(2, messagesByKey.size());
        Assert.assertEquals(5, messagesByKey.get(MessageDefinition.BROKEN_URL.getKey()).size());
        Assert.assertEquals(3, messagesByKey.get(MessageDefinition.PROTEIN_UNIPROT_NO_XREF.getKey()).size());
    }

    @Test
    public void sortMessagesByLevel() throws Exception {
        List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i = 0; i < 3; i++) {
            messages.add(new GeneralMessage(MessageDefinition.ANNOTATION_WITH_WRONG_TOPIC, getMockBuilder().createAnnotationRandom()));
        }

        for (int i = 0; i < 5; i++) {
            messages.add(new GeneralMessage(MessageDefinition.EXPERIMENT_WITHOUT_INTERACTION, getMockBuilder().createExperimentEmpty()));
        }

        for (int i = 0; i < 1; i++) {
            messages.add(new GeneralMessage(MessageDefinition.BIOSOURCE_WITHOUT_NEWT_XREF, getMockBuilder().createBioSourceRandom()));
        }

        List<GeneralMessage> messagesByLevel = MessageUtils.sortMessagesByLevel(messages);

        Assert.assertEquals(9, messagesByLevel.size());

        boolean majorProcessed = false;
        boolean normalProcessed = false;

        for (GeneralMessage message : messagesByLevel) {
            if (!majorProcessed) {
                if (message.getMessageDefinition().getLevel() == MessageLevel.WARNING) {
                    majorProcessed = true;
                } else if (message.getMessageDefinition().getLevel() == MessageLevel.INFO) {
                    Assert.fail("Sorting failed");
                }
            } else if (!normalProcessed) {
                if (message.getMessageDefinition().getLevel() == MessageLevel.ERROR) {
                    Assert.fail("Sorting failed");
                } else if (message.getMessageDefinition().getLevel() == MessageLevel.INFO) {
                    normalProcessed = true;
                }
            } else {
                if (message.getMessageDefinition().getLevel() == MessageLevel.ERROR) {
                    Assert.fail("Sorting failed");
                } else if (message.getMessageDefinition().getLevel() == MessageLevel.WARNING) {
                    Assert.fail("Sorting failed");
                }
            }
        }
    }

    @Test
    public void toSanityReport() throws Exception{
         List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i=0; i<5; i++) {
            Protein prot = getMockBuilder().createProteinRandom();
            prot.setAc("PROT-"+i);
            prot.setUpdated(new Date());
            prot.setUpdator("peter");
            messages.add(new GeneralMessage(MessageDefinition.BROKEN_URL, prot));
        }

        for (int i=0; i<3; i++) {
            Experiment exp = getMockBuilder().createExperimentRandom(1);
            exp.setAc("EXP-"+i);
            exp.setUpdated(new Date());
            exp.setUpdator("anne");
            messages.add(new GeneralMessage(MessageDefinition.EXPERIMENT_ON_HOLD, exp));
        }

        SanityReport sanityReport = MessageUtils.toSanityReport(messages);

        Assert.assertEquals(2, sanityReport.getSanityResults().size());
        Assert.assertEquals(3, sanityReport.getSanityResults().get(0).getInsaneObjects().size());
        Assert.assertEquals(5, sanityReport.getSanityResults().get(1).getInsaneObjects().size());
    }


}