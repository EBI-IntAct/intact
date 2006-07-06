/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.setupGoDensity;

import uk.ac.ebi.intact.application.goDensity.business.dag.CvGoDag;
import uk.ac.ebi.intact.application.goDensity.exception.DatabaseEmptyException;
import uk.ac.ebi.intact.application.goDensity.exception.KeyNotFoundException;
import uk.ac.ebi.intact.business.IntactException;

import java.sql.SQLException;

/**
 * Insert of the go graph in goDensity specific tables (database)
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class SetupGoDensityTables {

    /**
     * import of the go graph within intact (CvGoNodes) to goDensity specific tables
     * @param args no arguments
     */
    public static void main(String[] args) {

        System.out.println("Import of GO graph in goDensity specific tables.");
        System.out.println("Import started at: " + new java.util.Date());
        System.out.println("Import will take about 1-3 hours");

        CvGoDag dag = null;

        try {
            dag = new CvGoDag();
        } catch (DatabaseEmptyException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IntactException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        dag.toDb();

        System.out.println("Import finished at: " + new java.util.Date());
    }
}
