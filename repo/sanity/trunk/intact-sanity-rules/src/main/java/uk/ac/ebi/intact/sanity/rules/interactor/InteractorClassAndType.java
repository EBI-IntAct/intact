/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interactor;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.InteractorMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Check on an interactor class and its CvInteractorType.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule(target = Interactor.class, group = { RuleGroup.INTACT, RuleGroup.IMEX })

public class InteractorClassAndType implements Rule<Interactor> {

    public Collection<GeneralMessage> check(Interactor interactor) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        if(interactor.getCvInteractorType() == null){

            messages.add(new InteractorMessage( MessageDefinition.INTERACTOR_WITH_MISSING_TYPE, interactor ));

        } else {

            final CvObjectXref xref = CvObjectUtils.getPsiMiIdentityXref( interactor.getCvInteractorType() );
            if( xref == null ) {

                messages.add(new InteractorMessage( MessageDefinition.INTERACTOR_WITH_INVALID_TYPE, interactor ));

            } else {
                final String mi = xref.getPrimaryId();

                if( (interactor instanceof Interaction) && !(mi.equals( CvInteractorType.INTERACTION_MI_REF) ) ) {

                    messages.add(new InteractorMessage( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH, interactor ));

                } else if( (interactor instanceof NucleicAcid) && !(mi.equals( CvInteractorType.NUCLEIC_ACID_MI_REF) ) ) {

                    messages.add(new InteractorMessage( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH, interactor ));

                } else if( (interactor instanceof Protein) && !(mi.equals( CvInteractorType.PROTEIN_MI_REF) ) ) {

                    messages.add(new InteractorMessage( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH, interactor ));

                } else if( (interactor instanceof SmallMolecule) && !(mi.equals( CvInteractorType.SMALL_MOLECULE_MI_REF) ) ) {

                    messages.add(new InteractorMessage( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH, interactor ));

                } else {

                    messages.add(new InteractorMessage( MessageDefinition.INTERACTOR_WITH_UNSUPPORTED_TYPE, interactor ));
                }
            }
        }
        return messages;
    }
}