/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AcGenerator {
    private static int i = 1;

    private static final String PREFIX = "EBI-";


    private AcGenerator() {
    }

    public static String getNextVal(){
        int j = i;
        i++;

        return PREFIX + Integer.toString(j);
    }
}