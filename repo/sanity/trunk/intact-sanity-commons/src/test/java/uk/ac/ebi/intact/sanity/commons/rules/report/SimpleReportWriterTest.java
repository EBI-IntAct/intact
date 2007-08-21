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
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleReportWriterTest extends IntactBasicTestCase {

    @Test
    public void write_default() throws Exception {
        List<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for (int i=0; i<5; i++) {
            Protein prot = getMockBuilder().createProteinRandom();
            prot.setAc("PROT-"+i);
            prot.setUpdated(new Date());
            prot.setUpdator("peter");
            messages.add(new GeneralMessage("description1", MessageLevel.NORMAL, "suggestion1", prot));
        }

        for (int i=0; i<3; i++) {
            Experiment exp = getMockBuilder().createExperimentRandom(1);
            exp.setAc("EXP-"+i);
            exp.setUpdated(new Date());
            exp.setUpdator("anne");
            messages.add(new GeneralMessage("description2", MessageLevel.MINOR, "suggestion2", exp));
        }

        Writer writer = new StringWriter();

        SimpleReportWriter reportWriter = new SimpleReportWriter(writer);
        reportWriter.writeReport(messages);

        int lineCount = writer.toString().split(System.getProperty("line.separator")).length;

        Assert.assertEquals(17, lineCount);
    }
}