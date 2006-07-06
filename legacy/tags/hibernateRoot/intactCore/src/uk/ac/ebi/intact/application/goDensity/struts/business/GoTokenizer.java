/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.struts.business;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Tokenizer for GO:ID terms.
 * Seperates terms by "," or ";"
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class GoTokenizer {

    /**
     * @param goIds String of GO:IDs which are separated by ";" or ",".
     * @return a Set of GO:IDs according to the imput
     */
    public static Set getGoIdTokens(String goIds) {
        String goIdsExSpaces = goIds.replaceAll(" ", "");
        String goIdsExSpacesExKomma = goIdsExSpaces.replaceAll(",", ";");
        StringTokenizer st = new StringTokenizer(goIdsExSpacesExKomma, ";");
        Set set = new HashSet(st.countTokens());
        while (st.hasMoreTokens()) {
            set.add(st.nextToken().toUpperCase());
        }
        return set; // GoId Strings
    }
}

