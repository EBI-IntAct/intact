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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.io.IOException;
import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class ReportWriter {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(ReportWriter.class);

    public void write(Collection<GeneralMessage> messages) throws IOException{
        writeReport(messages);
    }

    public void write(Collection<GeneralMessage> originalMessages, ReportFilter ... filters) throws IOException{
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if (log.isDebugEnabled()) log.debug("Messages before filtering: "+originalMessages.size());

        for (ReportFilter filter : filters) {
             if (log.isDebugEnabled()) log.debug("\tFiltering with filter: "+filter.getClass().getName());

            for (GeneralMessage originalMessage : originalMessages) {
                if (filter.accept(originalMessage)) {
                    messages.add(originalMessage);
                }
            }

            if (log.isDebugEnabled()) log.debug("\t\t"+messages.size()+" after filtering");
        }

        writeReport(messages);
    }

    protected abstract void writeReport(Collection<GeneralMessage> messages) throws IOException;

    protected static Map<String,Collection<GeneralMessage>> groupMessagesByDescription(Collection<GeneralMessage> messages) {
        Map<String,Collection<GeneralMessage>> messagesByDescription = new HashMap<String,Collection<GeneralMessage>>();

        for (GeneralMessage message : messages) {
            String description = message.getDescription();
            if (messagesByDescription.containsKey(description)) {
                messagesByDescription.get(description).add(message);
            } else {
                Collection<GeneralMessage> messagesInDesc = new ArrayList<GeneralMessage>();
                messagesInDesc.add(message);
                messagesByDescription.put(description, messagesInDesc);
            }
        }

        return messagesByDescription;
    }

    protected static List<GeneralMessage> sortMessagesByLevel(Collection<GeneralMessage> messages) {
        List<GeneralMessage> sortedMessages = new ArrayList<GeneralMessage>(messages);

        Collections.sort(sortedMessages, new Comparator<GeneralMessage>() {

            public int compare(GeneralMessage o1, GeneralMessage o2) {
                return o1.getLevel().compareTo(o2.getLevel());
            }
        });

        return sortedMessages;
    }
}