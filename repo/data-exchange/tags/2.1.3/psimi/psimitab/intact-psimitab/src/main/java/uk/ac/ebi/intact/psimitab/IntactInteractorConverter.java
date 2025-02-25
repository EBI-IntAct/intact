/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.psimitab;

import psidev.psi.mi.tab.converter.tab2xml.XmlConversionException;
import psidev.psi.mi.tab.converter.xml2tab.InteractorConverter;
import psidev.psi.mi.tab.converter.xml2tab.TabConversionException;
import psidev.psi.mi.tab.converter.xml2tab.CrossReferenceConverter;
import psidev.psi.mi.tab.converter.IdentifierGenerator;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.xml.model.*;
import psidev.psi.mi.xml.model.Parameter;
import uk.ac.ebi.intact.psimitab.model.*;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Interactor converter for the extended interactor.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactInteractorConverter extends InteractorConverter<ExtendedInteractor> {

    private CrossReferenceConverter xrefConverter;

    public IntactInteractorConverter() {
        xrefConverter = new CrossReferenceConverter();
    }

    @Override
    protected ExtendedInteractor newInteractor(List<CrossReference> identifiers) {
        return new ExtendedInteractor(identifiers);
    }

    @Override
    public ExtendedInteractor toMitab(Interactor interactor) throws TabConversionException {
        ExtendedInteractor tabInteractor = (ExtendedInteractor) super.toMitab(interactor);

        return tabInteractor;
    }

    @Override
    public Interactor fromMitab(psidev.psi.mi.tab.model.Interactor tabInteractor) throws XmlConversionException {
        ExtendedInteractor extInteractor = (ExtendedInteractor) tabInteractor;

        Interactor interactor = super.fromMitab(extInteractor);

        Collection<DbReference> secondaryRefs = getSecondaryRefs(interactor,extInteractor.getProperties());
        //setting the secondary refs with properties
        Xref interactorXref = new Xref(interactor.getXref().getPrimaryRef(),secondaryRefs);
        interactor.setXref(interactorXref);
        return interactor;
    }

    public Participant buildParticipantA(psidev.psi.mi.xml.model.Interactor interactor,
                                         BinaryInteraction binaryInteraction,
                                         int index) throws XmlConversionException {

        IntactBinaryInteraction ibi = (IntactBinaryInteraction)binaryInteraction;
        return buildParticipant(interactor, ibi.getInteractorA(), index);
    }

    public Participant buildParticipantB(psidev.psi.mi.xml.model.Interactor interactor,
                                         BinaryInteraction binaryInteraction,
                                         int index) throws XmlConversionException {

        IntactBinaryInteraction ibi = (IntactBinaryInteraction)binaryInteraction;
        return buildParticipant(interactor, ibi.getInteractorB(), index);
    }

    private Participant buildParticipant(psidev.psi.mi.xml.model.Interactor interactor, ExtendedInteractor extInteractor, int index) throws XmlConversionException {
        Participant participant = new Participant();
        participant.setId( IdentifierGenerator.getInstance().nextId() );
        participant.setInteractor( interactor );


        if( ! extInteractor.getExperimentalRoles().isEmpty() ) {
            CrossReference expRoleXref = extInteractor.getExperimentalRoles().get(0);
            participant.getExperimentalRoles().add(xrefConverter.fromMitab(expRoleXref, ExperimentalRole.class));
        }

        if( ! extInteractor.getBiologicalRoles().isEmpty() ) {
            CrossReference bioRoleXref = extInteractor.getBiologicalRoles().get(0);
            participant.setBiologicalRole(xrefConverter.fromMitab(bioRoleXref, BiologicalRole.class));
        }

        CrossReference interactorType = extInteractor.getInteractorType();
        participant.getInteractor().setInteractorType(xrefConverter.fromMitab(interactorType, InteractorType.class));

        // annotations
        for (Annotation annotation : extInteractor.getAnnotations()) {
            Attribute attr = new Attribute(annotation.getType(), annotation.getText());
            final Collection<Attribute> attributes = participant.getInteractor().getAttributes();
            if( ! attributes.contains( attr )) {
                attributes.add(attr);
            }
        }

        // parameters
        for (uk.ac.ebi.intact.psimitab.model.Parameter parameter : extInteractor.getParameters()) {
            Parameter param = new Parameter(parameter.getType(), parameter.getFactor());
            param.setUnit(parameter.getUnit());
            param.setBase(parameter.getBase());
            param.setExponent(parameter.getExponent());
            participant.getParameters().add(param);
        }

        return participant;
    }

    private Collection<DbReference> getSecondaryRefs( Interactor interactor, List<CrossReference> properties ) {
        Collection<DbReference> refs = new ArrayList<DbReference>();
        for ( CrossReference property : properties ) {

            boolean isPrimaryRef = interactor.getXref().getPrimaryRef().getId().equals( property.getIdentifier() );

            if ( !isPrimaryRef ) {
                DbReference secDbRef = new DbReference();

                secDbRef.setDb( property.getDatabase() );
                secDbRef.setId( property.getIdentifier() );

                if ( property.getDatabase().equals( "interpro" ) ) {
                    secDbRef.setDbAc( "MI:0449" );
                } else if ( property.getDatabase().equals( "intact" ) ) {
                    secDbRef.setDbAc( "MI:0469" );
                } else if ( property.getDatabase().equals( "go" ) ) {
                    secDbRef.setDbAc( "MI:0448" );
                } else if ( property.getDatabase().equals( "uniprotkb" ) ) {
                    secDbRef.setDbAc( "MI:0486" );
                } else if ( property.getDatabase().equals( "drugbank" ) ) {
                    secDbRef.setDbAc( "MI:2002" );
                } else if ( property.getDatabase().equals( "pubmed" ) ) {
                    secDbRef.setDbAc( "MI:0446" );
                } else if ( property.getDatabase().equals( "ensembl" ) ) {
                    secDbRef.setDbAc( "MI:0476" );
                } else if ( property.getDatabase().equals( "refseq" ) ) {
                    secDbRef.setDbAc( "MI:0481" );
                } else if ( property.getDatabase().equals( "chebi" ) ) {
                    secDbRef.setDbAc( "MI:0474" );
                }

                if ( property.hasText() ) {
                    secDbRef.setSecondary( property.getText() );
                }

                refs.add( secDbRef );
            }
        }
        return refs;
    }

}
