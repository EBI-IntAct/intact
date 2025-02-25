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

import java.io.StringWriter;
import java.io.Writer;

/**
 * SimpleReportWriter tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleReportWriterTest extends AbstractReportTestCase {

    @Test
    public void write_default() throws Exception {
        Writer writer = new StringWriter();

        SimpleReportWriter reportWriter = new SimpleReportWriter(writer);
        reportWriter.write(getDefaultSanityReport());

        int lineCount = writer.toString().split(System.getProperty("line.separator")).length;

        Assert.assertEquals(23, lineCount);
    }

    @Test
    public void write_filtered() throws Exception {
        Writer writer = new StringWriter();

        SimpleReportWriter reportWriter = new SimpleReportWriter(writer);
        reportWriter.write(getDefaultSanityReport(), new CreatorReportFilter("anne"));

        int lineCount = writer.toString().split(System.getProperty("line.separator")).length;

        Assert.assertEquals(10, lineCount);
    }

    @Test
    public void write_alternative() throws Exception {
        Writer writer = new StringWriter();

        SimpleReportWriter reportWriter = new SimpleReportWriter(writer);
        reportWriter.write(getAlternativeSanityReport(), new CreatorReportFilter("anne"));

        int lineCount = writer.toString().split(System.getProperty("line.separator")).length;

        Assert.assertEquals(10, lineCount);
    }
}