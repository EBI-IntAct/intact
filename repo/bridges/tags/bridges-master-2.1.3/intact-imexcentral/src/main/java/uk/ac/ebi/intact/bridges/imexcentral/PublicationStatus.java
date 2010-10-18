package uk.ac.ebi.intact.bridges.imexcentral;

/**
 * Various statuses an IMEx publication can have.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.2
 */
public enum PublicationStatus {
    NEW,
    RESERVED,
    INPROGRESS,
    RELEASED,
    DISCARDED,
    INCOMPLETE;
}
