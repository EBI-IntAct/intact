/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.AnnotationTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;

import java.util.Collection;
import java.util.Iterator;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class Utilities {

    public static XrefTag getXrefByCvDatabase( final Collection xrefs,
                                               final String db ) {

        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            final XrefTag xref = (XrefTag) iterator.next();
            if( db.equals( xref.getDb() ) ) {
                return xref;
            }
        }
        return null;
    }

    public static XrefTag getXrefByCvDatabase( final Collection xrefs,
                                               final String db,
                                               final String primaryId ) {

        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            final XrefTag xref = (XrefTag) iterator.next();
            if( db.equals( xref.getDb() ) &&
                primaryId.equals( xref.getId() ) ) {
                return xref;
            }
        }
        return null;
    }

    public static AnnotationTag getAnnotationByType( final Collection annotations,
                                                     final String type ) {

        for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
            final AnnotationTag annotation = (AnnotationTag) iterator.next();
            if( type.equals( annotation.getType() ) ) {
                return annotation;
            }
        }
        return null;
    }
}
