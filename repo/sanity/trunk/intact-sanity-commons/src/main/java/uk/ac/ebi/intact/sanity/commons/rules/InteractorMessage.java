/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons.rules;

import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.Field;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Interactor message.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class InteractorMessage extends GeneralMessage {

    private Interactor interactor;

    public InteractorMessage(MessageDefinition messageDefinition, Interactor interactor) {
        super(messageDefinition, interactor);
        this.interactor = interactor;

        getInsaneObject().getField().addAll( fieldsForInteractor(interactor));
    }

    public Collection<Field> fieldsForInteractor(Interactor interactor) {
        Collection<Field> fields = new ArrayList<Field>();

        Field primaryIdField = new Field();
        primaryIdField.setName("Interactor type");
        final String type = (interactor.getCvInteractorType() == null ? "-" : interactor.getCvInteractorType().getShortLabel() );
        primaryIdField.setValue( type );

        return fields;
    }

    public Interactor getInteractor() {
        return interactor;
    }

    public void setInteractor(Interactor interactor) {
        this.interactor = interactor;
    }
}