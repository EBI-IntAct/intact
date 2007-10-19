/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.util;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Protein;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class CommonMethods {

    public static boolean isNoUniprotUpdate(Protein protein){
        Collection<Annotation> annotations = protein.getAnnotations();
        for(Annotation annotation : annotations){
            if(CvTopic.NON_UNIPROT.equals(annotation.getCvTopic().getShortLabel())){
                return true ;
            }
        }
        return false;
    }
}