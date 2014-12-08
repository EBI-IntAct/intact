/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.editor.converter;

import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.dao.ParticipantDao;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 */
@FacesConverter( value = "participantConverter", forClass = IntactParticipantEvidence.class )
public class ParticipantConverter implements Converter {

    @Override
    public Object getAsObject( FacesContext facesContext, UIComponent uiComponent, String ac ) throws ConverterException {
        if ( ac == null ) return null;
        IntactDao dao = ApplicationContextProvider.getBean("intactDao");
        ParticipantDao pDao = dao.getParticipantEvidenceDao();
        return pDao.getByAc(ac);
    }

    @Override
    public String getAsString( FacesContext facesContext, UIComponent uiComponent, Object o ) throws ConverterException {
        if ( o == null ) return null;

        if ( o instanceof IntactParticipantEvidence ) {
            IntactParticipantEvidence part = ( IntactParticipantEvidence ) o;
            return part.getAc();
        } else {
            throw new IllegalArgumentException( "Argument must be a modelled participant: " + o + " (" + o.getClass() + ")" );
        }
    }
}