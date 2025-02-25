/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.predict.business;

/**
 * This class implements Predict user for a Postgres database.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id: PredictUserPg.java 2358 2003-12-10 14:19:35Z smudali $
 */
public class PredictUserPg extends PredictUser {

    // Implement abstract methods.

    protected String getSpeciesSQL() {
        return "SELECT DISTINCT species FROM ia_payg;";
    }

    protected String getDbInfoSQL(String taxid) {
        return "SELECT nID FROM ia_payg WHERE really_used_as_bait='N' AND species =\'"
                + taxid + "\' ORDER BY indegree desc, qdegree DESC LIMIT 50;";
    }
}
