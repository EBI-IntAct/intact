/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.setupGoDensity;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.GoTools;
import uk.ac.ebi.intact.model.CvGoNode;

import java.util.Date;

/**
 * Insert GO Term from flatfile (e.g. component.ontolgies)
 * 1MByte file takes for import about 4hours!
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class InsertGoFromFlatfile {

    /**
     * Import CvGoNodes from a GO flatfile.
     * @param args path2File
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Please add path2goFlatFile as argument!");
            System.exit(0);
        }

        try {
            System.out.println(new Date());
            System.out.println(args[0] + " will be imported now.");

            IntactHelper helper = new IntactHelper();
            GoTools.insertGoDag(CvGoNode.class, helper, args[0]);

            System.out.println(args[0] + " was successfully imported");
            System.out.println(new Date());
        } catch (IntactException e) {
            System.out.println("error setting up IntactHelper " + e);
        } catch (Exception e) {
            System.out.println("insert of GO dag failed " + e);
        }
    }
}
