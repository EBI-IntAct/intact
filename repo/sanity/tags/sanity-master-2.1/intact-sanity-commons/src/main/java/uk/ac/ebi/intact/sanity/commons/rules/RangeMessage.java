/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons.rules;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.commons.report.Field;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Range message.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class RangeMessage extends GeneralMessage {
    private Range range;
    private Interactor interactor;

    public RangeMessage( MessageDefinition messageDefinition, IntactObject outLaw, Range range ) {
        super( messageDefinition, outLaw );
        this.setRange( range );

        getInsaneObject().getFields().addAll( fieldsForAnnotation( range ) );
    }

    public RangeMessage( MessageDefinition messageDefinition, IntactObject outLaw, Range range, Interactor interactor ) {
        this( messageDefinition, outLaw, range );
        this.interactor = interactor;

        getInsaneObject().getFields().addAll( fieldsForAnnotation( interactor ) );
    }

    private Collection<Field> fieldsForAnnotation( Interactor interactor ) {
        Collection<Field> fields = new ArrayList<Field>();

        addField( "Interactor AC", ( range.isUndetermined() ? "Yes" : "No" ), fields );

        final CvInteractorType type = interactor.getCvInteractorType();
        if ( type != null ) {
            addField( "Type", type.getShortLabel(), fields );
        }

        if ( interactor instanceof Polymer ) {
            Polymer polymer = ( Polymer ) interactor;
            final int length = ( polymer.getSequence() == null ? 0 : polymer.getSequence().length() );
            addField( "Seq. length", String.valueOf( length ), fields );
        }

        return fields;
    }

    public Collection<Field> fieldsForAnnotation( Range range ) {
        Collection<Field> fields = new ArrayList<Field>();

        addField( "Undetermined", ( range.isUndetermined() ? "Yes" : "No" ), fields );

        String from = "[" + range.getFromIntervalStart() + ".." + range.getFromIntervalEnd() + "]";
        addField( "From", from, fields );

        String fromFuzzy = null;
        if ( range.getFromCvFuzzyType() != null ) {
            fromFuzzy = range.getFromCvFuzzyType().getShortLabel();
        }
        addField( "From type", fromFuzzy, fields );

        String to = "[" + range.getToIntervalStart() + ".." + range.getToIntervalEnd() + "]";
        addField( "To", to, fields );

        String toFuzzy = null;
        if ( range.getToCvFuzzyType() != null ) {
            toFuzzy = range.getToCvFuzzyType().getShortLabel();
        }
        addField( "To type", toFuzzy, fields );

        return fields;
    }

    private void addField( String header, String value, Collection<Field> fields ) {
        Field f = new Field();
        f.setName( header );
        if ( value == null ) {
            f.setValue( "-" );
        } else {
            f.setValue( value );
        }
        fields.add( f );
    }

    public Range getRange() {
        return range;
    }

    public void setRange( Range range ) {
        this.range = range;
    }

    public Interactor getInteractor() {
        return interactor;
    }

    public void setInteractor( Interactor interactor ) {
        this.interactor = interactor;
    }
}