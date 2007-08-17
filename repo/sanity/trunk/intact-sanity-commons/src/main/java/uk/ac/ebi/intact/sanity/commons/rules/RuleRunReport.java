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

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RuleRunReport {

    private static final ThreadLocal<RuleRunReport> instance = new ThreadLocal<RuleRunReport>() {
        @Override
        protected RuleRunReport initialValue() {
            return new RuleRunReport();
        }
    };

    public static RuleRunReport getInstance() {
        return instance.get();
    }

    private Collection<GeneralMessage> messages;

    public RuleRunReport() {
        this.messages = new ArrayList<GeneralMessage>();
    }

    public void addMessage(GeneralMessage message) {
        messages.add(message);
    }

    public void addMessages(Collection<GeneralMessage> message) {
        messages.addAll(message);
    }

    public Collection<GeneralMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}