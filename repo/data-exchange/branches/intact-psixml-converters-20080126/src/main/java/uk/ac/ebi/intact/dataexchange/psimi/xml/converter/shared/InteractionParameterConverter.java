/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared;

import psidev.psi.mi.xml.model.*;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.AbstractIntactPsiConverter;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.IntactConverterUtils;
import uk.ac.ebi.intact.model.*;


/**
 * TODO comment that class header
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @version $Id$
 * @since TODO specify the maven artifact version
 *        <pre>
 *               27-Feb-2008
 *               </pre>
 */
public class InteractionParameterConverter extends AbstractIntactPsiConverter<uk.ac.ebi.intact.model.InteractionParameter, psidev.psi.mi.xml.model.Parameter> {

    public InteractionParameterConverter( Institution institution ) {
        super( institution );
    }

    public uk.ac.ebi.intact.model.InteractionParameter psiToIntact( psidev.psi.mi.xml.model.Parameter psiObject ) {

        Double factor = psiObject.getFactor();

        CvParameterType cvParameterType = new CvParameterType (getInstitution(), psiObject.getTerm());
        cvParameterType.setMiIdentifier(psiObject.getTermAc());

        InteractionParameter parameter = new InteractionParameter( getInstitution(), cvParameterType, factor);

        if ((psiObject.getUnit() != null) || (psiObject.getUnitAc() != null)){
            CvParameterUnit cvParameterUnit = new CvParameterUnit(getInstitution(), psiObject.getUnit());
            cvParameterUnit.setMiIdentifier(psiObject.getUnitAc());
            parameter.setCvParameterUnit(cvParameterUnit);
        }

        if (psiObject.hasBase() ) {
            Integer base = psiObject.getBase();
            parameter.setBase(base);
        }
        if(psiObject.hasExponent()) {
            Integer exponent = psiObject.getExponent();
            parameter.setExponent(exponent);
        }
        if (psiObject.hasUncertainty()){
            Double uncertainty = psiObject.getUncertainty();
            parameter.setUncertainty(uncertainty);
        }
        if (psiObject.hasExperiment()){
            ExperimentConverter experimentConverter = new ExperimentConverter(getInstitution());
            Experiment experiment = experimentConverter.psiToIntact(psiObject.getExperiment());
            parameter.setExperiment(experiment);
        }
        return parameter;
    }

    public psidev.psi.mi.xml.model.Parameter intactToPsi( uk.ac.ebi.intact.model.InteractionParameter intactObject ) {

        psidev.psi.mi.xml.model.Parameter parameter = new psidev.psi.mi.xml.model.Parameter(intactObject.getCvParameterType().getShortLabel(), intactObject.getFactor());
        parameter.setTermAc(intactObject.getCvParameterType().getMiIdentifier());
        if (intactObject.getBase() != null) {
            parameter.setBase(intactObject.getBase());
        }
        if (intactObject.getBase() != null) {
            parameter.setBase(intactObject.getBase());
        }
        if (intactObject.getExponent() != null) {
            parameter.setExponent(intactObject.getExponent());
        }
        if (intactObject.getUncertainty() != null) {
            parameter.setUncertainty(intactObject.getUncertainty());
        }
        if (intactObject.getCvParameterUnit() != null) {
            parameter.setUnit(intactObject.getCvParameterUnit().getShortLabel());
            parameter.setUnitAc(intactObject.getCvParameterUnit().getMiIdentifier());
        }
        if(intactObject.getExperiment() != null) {
            ExperimentConverter experimentConverter = new ExperimentConverter(getInstitution());
            parameter.setExperiment(experimentConverter.intactToPsi(intactObject.getExperiment()));
        }
        return parameter;
    }

}