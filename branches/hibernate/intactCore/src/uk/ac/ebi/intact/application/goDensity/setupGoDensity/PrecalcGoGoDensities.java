/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.setupGoDensity;

import uk.ac.ebi.intact.application.goDensity.business.dag.FastGoDag;
import uk.ac.ebi.intact.application.goDensity.business.data.GoFlatfile2Set;
import uk.ac.ebi.intact.application.goDensity.exception.GoIdNotInDagException;

import java.io.IOException;
import java.util.Date;

/**
 *
 * Precalculation of GO-GO-Densities according to a goFlatfile (e.g goSlim.ontology)
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class PrecalcGoGoDensities {

    /**
     * Import CvGoNodes from a GO flatfile.
     * @param args path2File
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Please add path2goFlatFile as argument which contains\n" +
                               "all GO:IDs which should be precalculated!");
            System.exit(0);
        }


        System.out.println(new Date());
        System.out.println("Precalculation will be executed according to the GO:ID's contained in " + args[0]);

        FastGoDag dag = FastGoDag.getInstance();
        try {
            dag.precalcDensity(GoFlatfile2Set.goFlatfile2Set(args[0]));
        } catch (GoIdNotInDagException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Precalculation done");
        System.out.println(new Date());

    }
}
