package uk.ac.ebi.intact.sanity.check.range;

/**
 * Types of message returned by the range checker.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public enum RangeReportType {

    METHIONINE_ADDED,

    METHIONINE_REMOVED,

    NO_RANGE_SEQUENCE,

    RANGE_REMAPPING
    ;
}
