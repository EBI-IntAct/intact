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
package uk.ac.ebi.intact.sanity.rules.protein;

import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.commons.rules.XrefMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks if the uniprot AC for a protein has the correct form
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinUniprotAc implements Rule<ProteinImpl> {

    private static final String UNIPROT_AC_REGEXP = "[A-Z][0-9][A-Z0-9]{3}[0-9]|[A-Z][0-9][A-Z0-9]{3}[0-9]-[0-9]+|[A-Z][0-9][A-Z0-9]{3}[0-9]-PRO_[0-9]{10}";

    private static final String DESCRIPTION = "Proteins with unexpected Uniprot AC";
    private static final String SUGGESTION = "Correct the uniprot AC, so it matches the form: "+UNIPROT_AC_REGEXP;

    public Collection<GeneralMessage> check(ProteinImpl protein) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        InteractorXref uniprotXref = ProteinUtils.getUniprotXref(protein);

        if (uniprotXref != null) {
            String uniprotAc = uniprotXref.getPrimaryId();

            if (!uniprotAc.matches(UNIPROT_AC_REGEXP)) {
                messages.add(new XrefMessage(DESCRIPTION, MessageLevel.MAJOR, SUGGESTION, protein, uniprotXref));
            }
        }

        return messages;
    }
}