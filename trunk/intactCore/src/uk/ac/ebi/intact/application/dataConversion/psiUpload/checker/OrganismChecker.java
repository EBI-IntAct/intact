/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.OrganismTag;
import uk.ac.ebi.intact.util.BioSourceFactory;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class OrganismChecker extends AbstractOrganismChecker {

    public static void check( final OrganismTag organism,
                              final BioSourceFactory bioSourceFactory ) {

        final String taxid = organism.getTaxId();
        checkOnTaxid( taxid, bioSourceFactory );
    }
}
