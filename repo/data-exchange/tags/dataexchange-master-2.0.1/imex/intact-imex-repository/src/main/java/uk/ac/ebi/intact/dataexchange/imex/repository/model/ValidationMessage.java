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
package uk.ac.ebi.intact.dataexchange.imex.repository.model;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@DiscriminatorValue("VAL")
public class ValidationMessage extends Message {

    public ValidationMessage() {
        super();
    }

    public ValidationMessage(String message, MessageLevel level) {
        super(message, level);
    }

    public ValidationMessage(String message, MessageLevel level, Throwable throwable) {
        super(message, level, throwable);
    }
}