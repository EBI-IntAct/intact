package uk.ac.ebi.intact.sanity.commons.rules;

/**
 * Definition of the prefixes for all message types.
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since 2.0.0
 */
public interface KeyPrefix {
    static final String ANNOTATED_OBJECT = "AO-";
    static final String EXPERIMENT = "EXP-";
    static final String INTERACTION = "INT-";
    static final String PROTEIN = "PROT-";
    static final String SMALL_MOLECULE = "SM-";
    static final String NUCLEIC_ACID = "NUC-";
    static final String CV = "CV-";
    static final String FEATURE = "FTR-";
    static final String PUBLICATION = "PUB-";
    static final String BIOSOURCE = "BS-";
}
