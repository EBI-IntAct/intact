/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.model;

/**
 * That class .
 * <attributeList>
 * <attribute name="comment">CAV1 was expressed as GST fusion in E. coli.</attribute>
 * <attribute name="comment">CAV1 was expressed in MDCK cells.</attribute>
 * <attribute name="remark">aa 61-101 of CAV1 are sufficient</attribute>
 * </attributeList>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class AnnotationTag {

    private final String type;
    private final String text;


    //////////////////////////////////
    // Constructor

    public AnnotationTag( final String type, final String text ) {

        if( type == null || "".equals( type ) ) {
            throw new IllegalArgumentException( "You must give a non null/empty type for an annotation" );
        }

        // instead of throwing an exception, we will filter all annotation without text.
//        if( text == null || "".equals( text ) ) {
//            throw new IllegalArgumentException( "You must give a non null/empty text for an annotation" );
//        }

        this.type = type;
        this.text = text;
    }


    //////////////////////////////////
    // Getters

    public boolean hasText() {
        return !( text == null || "".equals( text ) );
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }


    //////////////////////////////////
    // Equality

    public boolean equals( final Object o ) {
        if( this == o ) {
            return true;
        }
        if( !( o instanceof AnnotationTag ) ) {
            return false;
        }

        final AnnotationTag annotationTag = (AnnotationTag) o;

        if( !text.equals( annotationTag.text ) ) {
            return false;
        }
        if( !type.equals( annotationTag.type ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = type.hashCode();
        result = 29 * result + text.hashCode();
        return result;
    }


    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append( "AnnotationTag" );
        buf.append( "{text=" ).append( text );
        buf.append( ",type=" ).append( type );
        buf.append( '}' );
        return buf.toString();
    }
}
