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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ReportWriterTest extends IntactBasicTestCase {

    @Test
    public void groupMessagesByDescription() throws Exception {
        List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i=0; i<5; i++) {
            messages.add(new GeneralMessage("description1", MessageLevel.NORMAL, "suggestion1", getMockBuilder().createProteinRandom()));
        }

        for (int i=0; i<3; i++) {
            messages.add(new GeneralMessage("description2", MessageLevel.NORMAL, "suggestion2", getMockBuilder().createProteinRandom()));
        }

        Map<String,Collection<GeneralMessage>> messagesByDesc = ReportWriter.groupMessagesByDescription(messages);
        
        Assert.assertEquals(2, messagesByDesc.size());
        Assert.assertEquals(5, messagesByDesc.get("description1").size());
        Assert.assertEquals(3, messagesByDesc.get("description2").size());
    }

    @Test
    public void sortMessagesByLevel() throws Exception {
        List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i=0; i<3; i++) {
            messages.add(new GeneralMessage("desc2", MessageLevel.NORMAL, "suggestion2", getMockBuilder().createProteinRandom()));
        }

        for (int i=0; i<5; i++) {
            messages.add(new GeneralMessage("desc", MessageLevel.MAJOR, "suggestion1", getMockBuilder().createProteinRandom()));
        }

        for (int i=0; i<1; i++) {
            messages.add(new GeneralMessage("desc3", MessageLevel.MINOR, "suggestion3", getMockBuilder().createProteinRandom()));
        }

        List<GeneralMessage> messagesByLevel = ReportWriter.sortMessagesByLevel(messages);

        Assert.assertEquals(9, messagesByLevel.size());

        boolean majorProcessed = false;
        boolean normalProcessed = false;

        for (GeneralMessage message : messagesByLevel) {
            if (!majorProcessed) {
                 if (message.getLevel() == MessageLevel.NORMAL) {
                    majorProcessed = true;
                } else if (message.getLevel() == MessageLevel.MINOR) {
                    Assert.fail("Sorting failed");
                }
            } else if (!normalProcessed) {
                if (message.getLevel() == MessageLevel.MAJOR) {
                    Assert.fail("Sorting failed");
                } else if (message.getLevel() == MessageLevel.MINOR) {
                    normalProcessed = true;
                }
            } else {
                if (message.getLevel() == MessageLevel.MAJOR) {
                    Assert.fail("Sorting failed");
                } else if (message.getLevel() == MessageLevel.NORMAL) {
                    Assert.fail("Sorting failed");
                }
            }
        }
    }
}