/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.model;

/**
 * Describes the source database the original UniProt entry is coming from.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public enum UniprotProteinType {

    TREMBL,

    SWISSPROT,

    UNKNOWN;

}