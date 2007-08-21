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

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleReportWriter extends ReportWriter {

    protected static final String NEW_LINE = System.getProperty( "line.separator" );

    private Writer writer;

    public SimpleReportWriter(Writer writer) {
        this.writer = writer;
    }

    protected void writeReport(Collection<GeneralMessage> messages) throws IOException {
        Map<String,Collection<GeneralMessage>> messagesByDesc = groupMessagesByDescription(messages);

        for (String description : messagesByDesc.keySet()) {
             List<GeneralMessage> sortedMessages = sortMessagesByLevel(messagesByDesc.get(description));

             StringBuilder sb = prepareMessagesWithSameDescription(sortedMessages);
             writer.write(sb.toString());
        }
    }

    protected StringBuilder prepareMessagesWithSameDescription(Collection<GeneralMessage> messages) {
        StringBuilder sb = new StringBuilder();

        GeneralMessage firstMessage = messages.iterator().next();

        sb.append("##################################################").append(NEW_LINE);
        sb.append("# ").append(firstMessage.getDescription()).append(NEW_LINE);
        sb.append("# Suggestion: ").append(firstMessage.getProposedSolution());
        sb.append(NEW_LINE);
        sb.append("##################################################").append(NEW_LINE);

        for (GeneralMessage message : messages) {
            sb.append("[").append(message.getLevel()).append("] ");

            final IntactObject intactObject = message.getOutLaw();

            sb.append(intactObject.getAc());

            sb.append("\t");

            if (intactObject instanceof AnnotatedObject) {
                sb.append(((AnnotatedObject) intactObject).getShortLabel());
            } else {
                sb.append("-");
            }

            sb.append("\t");
            sb.append(SimpleDateFormat.getInstance().format(intactObject.getUpdated()));
            sb.append("\t");
            sb.append(intactObject.getUpdator());

            sb.append(NEW_LINE);
        }

        sb.append(NEW_LINE);

        return sb;
    }
}