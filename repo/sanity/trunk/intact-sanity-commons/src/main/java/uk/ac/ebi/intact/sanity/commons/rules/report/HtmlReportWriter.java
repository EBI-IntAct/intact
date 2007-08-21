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

import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class HtmlReportWriter extends ReportWriter {

    private static final String DEFAULT_XSL = "/META-INF/xsl/default-sanity.xsl";

    private Writer writer;

    public HtmlReportWriter(Writer writer) {
        this.writer = writer;
    }

    protected void writeReport(SanityReport report) throws IOException {
        TransformerFactory xformFactory = TransformerFactory.newInstance();

        InputStream xslInputStream = HtmlReportWriter.class.getResourceAsStream(DEFAULT_XSL);
        Source xslSource = new StreamSource(xslInputStream);

        try {
            Transformer transformer = xformFactory.newTransformer(xslSource);

            JAXBContext jc = JAXBContext.newInstance(SanityReport.class.getPackage().getName());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Source source = new JAXBSource(marshaller, report);
            Result result = new StreamResult(writer);

            transformer.transform(source, result);

        } catch (Exception e) {
            throw new SanityRuleException("Problem writing HTML", e);
        }
        
    }
}