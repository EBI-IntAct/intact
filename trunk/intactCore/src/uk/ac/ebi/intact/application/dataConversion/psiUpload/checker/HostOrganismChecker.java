/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.HostOrganismTag;
import uk.ac.ebi.intact.util.BioSourceFactory;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class HostOrganismChecker extends AbstractOrganismChecker {

    public static void check( final HostOrganismTag hostOrganism,
                              final BioSourceFactory bioSourceFactory ) {

        final String taxid = hostOrganism.getTaxId();
        checkOnTaxid( taxid, bioSourceFactory );
    }
}
