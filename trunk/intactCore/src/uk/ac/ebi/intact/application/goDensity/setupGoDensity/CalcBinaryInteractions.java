/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.setupGoDensity;

import uk.ac.ebi.intact.application.goDensity.business.binaryInteractions.CalcBinInteractionData;
import uk.ac.ebi.intact.application.goDensity.exception.KeyNotFoundException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.sql.SQLException;

/**
 *
 * Calculates binary interactions from Intact
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class CalcBinaryInteractions {

    /**
     * Calculates binary interactions according to the arguments.
     * @param args "true true false" for our approach to calculate binary interactions; for details:
     * @see uk.ac.ebi.intact.application.goDensity.business.binaryInteractions.CalcBinInteractionData
     */
    public static void main(String[] args) {

        // args[] = for biological correct data with spokes model use:
        // true true false
        String explain =
                "* !Argument Error! please use arguments as described here: \n" +
                "* How to generate the binaryInteractionData?                        \n" +
                "* 1st param: if there is a bait for a Interaction (Complex) then:   \n" +
                "*   - use \"true\" for crossproduct (spokes model)                  \n" +
                "*   - use \"false\" for allVsAll (clique model)                     \n" +
                "* 2nd param: if there is no bait for a Interaction (Complex) then:  \n" +
                "*   - use \"true\" for crossproduct (choose random bait)            \n" +
                "*   - use \"false\" allVsAll (clique model)                         \n" +
                "* 3rd param: if there are more baits for this Interaction (Complex) \n" +
                "*     btw: no real biological sense, but for the case there are ...)\n" +
                "*   - use \"true\" for connect if more bait: clique on bait-bait    \n" +
                "*   - use \"false\" don't connect bait-bait                         \n";

        if (args.length != 3) {
            System.out.println(explain);
            System.exit(0);
        }

        boolean first = true;
        boolean second = true;
        boolean third = false;

        // Argument 1
        if (args[0].equals("true")) {
            first = true;
            System.out.println("true");
        } else if (args[0].equals("false")) {
            first = false;
            System.out.println("false");
        } else {
            System.out.println("Argument No:1 has an error! Must be true or false\n");
            System.out.println("explain = " + explain);
            System.exit(0);
        }

        // Argument 2
        if (args[1].equals("true")) {
            second = true;
            System.out.println("true");
        } else if (args[1].equals("false")) {
            second = false;
            System.out.println("false");
        } else {
            System.out.println("Argument No:2 has an error! Must be true or false\n");
            System.out.println("explain = " + explain);
            System.exit(0);
        }

        // Argument 3
        if (args[2].equals("true")) {
            third = true;
            System.out.println("true");
        } else if (args[2].equals("false")) {
            third = false;
            System.out.println("false");
        } else {
            System.out.println("Argument No:3 has an error! Must be true or false\n");
            System.out.println("explain = " + explain);
            System.exit(0);
        }

        CalcBinInteractionData binary = null;

        try {
            IntactHelper helper = new IntactHelper();
            binary = new CalcBinInteractionData(helper, first, second, third);
            System.out.println("calculation started at " + new java.util.Date());
        } catch (IntactException e) {
            e.printStackTrace();
        }

        try {
            binary.dbPopulate(false);
            System.out.println("db population started at " + new java.util.Date());
        } catch (SQLException e) {
            e.printStackTrace();  // direct output to be seen @ shellscripts
        } catch (IntactException e) {
            e.printStackTrace();  // direct output to be seen @ shellscripts
        } catch (KeyNotFoundException e) {
            e.printStackTrace();  // direct output to be seen @ shellscripts
        }

        System.out.println("finished at " + new java.util.Date());

    }
}
