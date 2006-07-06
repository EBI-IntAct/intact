/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.business.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * GoFlatfile2Set can be used to parse a GO flatfile
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class GoFlatfile2Set {

    /**
     * used to parse a GO flatfile and returns all GO:IDs as Strings in a Set.
     * @param sourceFile of the GO flatfile (e.g. data/GoSlim.ontology).
     * Only the first GO:ID within one line is included in the Set!
     * @return a Set of all GO:IDs as Strings
     * @throws IOException
     */
    public static Set goFlatfile2Set(String sourceFile) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(sourceFile));
        Set set = new HashSet();
        String line;
        while (null != (line = in.readLine())) {

            // The empty line indicates the end of the record. Return parsed record.
            if (line.matches(".*GO:.*")) {
                int start = line.indexOf("GO:");
                String goId = line.substring(start, start + 10);
                set.add(goId);
            }
        }
        return set;
    }

    // =======================================================================
    // Test method
    // =======================================================================

    /*
    public static void main(String[] args) {
        try {
            Set set = goFlatfile2Set("/home/markus/praktikum/intactCore/data/generic.0208");
            System.out.println("set = " + set);
        } catch (IOException e) {
            System.out.println("error: " + e);
        }
    }
    */
}
