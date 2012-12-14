package uk.ac.ebi.intact.bridges.citexplore.util;

import uk.ac.ebi.intact.bridges.citexplore.exceptions.InvalidPubmedException;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id: PubmedIdChecker.java 10021 2007-10-17 13:34:50Z baranda $
 */
public class PubmedIdChecker {

    public static boolean isPubmedId(String pmid) {
        return pmid.matches("\\d+");
    }

    public static void ensureValidFormat(String pubmedId) {
        if (pubmedId == null) {
            throw new InvalidPubmedException(null, new NullPointerException("pubmedId"));
        }
        if (!isPubmedId(pubmedId)) {
            throw new InvalidPubmedException(pubmedId);
        }
    }
}
