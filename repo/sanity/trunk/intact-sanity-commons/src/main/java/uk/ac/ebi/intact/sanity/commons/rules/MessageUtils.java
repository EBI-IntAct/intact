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

import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.SanityResult;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MessageUtils {

    private MessageUtils() {}

    public static Map<String,Collection<GeneralMessage>> groupMessagesByKey(Collection<GeneralMessage> messages) {
        Map<String,Collection<GeneralMessage>> messagesByKey = new HashMap<String,Collection<GeneralMessage>>();

        for (GeneralMessage message : messages) {
            String key = message.getMessageDefinition().getKey();
            if (messagesByKey.containsKey(key)) {
                messagesByKey.get(key).add(message);
            } else {
                Collection<GeneralMessage> messagesInDesc = new ArrayList<GeneralMessage>();
                messagesInDesc.add(message);
                messagesByKey.put(key, messagesInDesc);
            }
        }

        return messagesByKey;
    }

    public static List<GeneralMessage> sortMessagesByLevel(Collection<GeneralMessage> messages) {
        List<GeneralMessage> sortedMessages = new ArrayList<GeneralMessage>(messages);

        Collections.sort(sortedMessages, new Comparator<GeneralMessage>() {

            public int compare(GeneralMessage o1, GeneralMessage o2) {
                return o1.getMessageDefinition().getLevel().compareTo(o2.getMessageDefinition().getLevel());
            }
        });

        return sortedMessages;
    }

    public static SanityReport toSanityReport(Collection<GeneralMessage> messages) {
         SanityReport report = new SanityReport();

        Map<String,Collection<GeneralMessage>> messagesByKey = MessageUtils.groupMessagesByKey(messages);

        for (String description : messagesByKey.keySet()) {
             Collection<GeneralMessage> messagesInDesc = messagesByKey.get(description);

             // use the first message to get the level and suggestion
            GeneralMessage firstMessage = messagesInDesc.iterator().next();
            MessageLevel level = firstMessage.getMessageDefinition().getLevel();
            String suggestion = firstMessage.getMessageDefinition().getSuggestion();

            SanityResult sanityResult = new SanityResult();
            sanityResult.setDescription(description);
            sanityResult.setLevel(level.toString());
            sanityResult.setSuggestion(suggestion);
            report.getSanityResult().add(sanityResult);

            for (GeneralMessage message : messagesInDesc) {
                sanityResult.getInsaneObject().add(message.getInsaneObject());
            }

        }

        return report;
    }
}