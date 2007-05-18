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
package uk.ac.ebi.intact.psixml.converter.shared;

import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.ExperimentalRole;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentalRoleConverter extends AbstractIntactPsiConverter<CvExperimentalRole, ExperimentalRole> {

    public ExperimentalRoleConverter(IntactContext intactContext, Entry parentEntry) {
        super(intactContext, parentEntry);
    }

    public CvExperimentalRole psiToIntact(ExperimentalRole psiObject) {
        String shortLabel = psiObject.getNames().getShortLabel();
        String fullName = psiObject.getNames().getFullName();

        CvExperimentalRole cv = new CvExperimentalRole(getInstitution(), shortLabel);
        cv.setFullName(fullName);

        return cv;
    }

    public ExperimentalRole intactToPsi(CvExperimentalRole intactObject) {
        throw new UnsupportedOperationException();
    }
}