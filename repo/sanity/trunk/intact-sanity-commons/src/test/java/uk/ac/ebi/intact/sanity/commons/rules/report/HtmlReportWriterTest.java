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
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class HtmlReportWriterTest extends AbstractReportWriterTestCase {

    @Test
    public void write_default() throws Exception {
        Writer writer = new StringWriter();

//        SanityReport report = getDefaultSanityReport();
//        for (SanityResult res : report.getSanityResult()) {
//            for (InsaneObject insane : res.getInsaneObject()) {
//                if (new Random().nextBoolean()) {
//                insane.setUrl("http://www.ebi.ac.uk/intact");
//                }
//                Field field = new Field();
//                field.setName("fieldName");
//                field.setValue("fieldValue");
//                insane.getField().add(field);
//            }
//        }

        ReportWriter reportWriter = new HtmlReportWriter(writer);
        reportWriter.write(getDefaultSanityReport());

        int lineCount = writer.toString().split(System.getProperty("line.separator")).length;

//        try {
//            FileWriter w = new FileWriter("F:\\projectes\\intact-current\\sanity\\intact-sanity-commons\\src\\main\\resources\\META-INF\\xsl\\test.html");
//            w.write(writer.toString());
//            w.close();
//
////            MailSender mailSender = new MailSender(MailSender.GMAIL_SETTINGS);
////            PasswordAuthentication pass = new PasswordAuthentication("brunoaranda", "xxx");
////            mailSender.postMailSSL(new String[] {"baranda@ebi.ac.uk"}, "Test", writer.toString(), "baranda@ebi.ac.uk", pass);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

        Assert.assertEquals(70, lineCount);
    }

    @Test
    public void write_filtered() throws Exception {
        Writer writer = new StringWriter();

        ReportWriter reportWriter = new HtmlReportWriter(writer);
        reportWriter.write(getDefaultSanityReport(), new UserReportFilter("anne"));

        int lineCount = writer.toString().split(System.getProperty("line.separator")).length;

        Assert.assertEquals(40, lineCount);
    }
}