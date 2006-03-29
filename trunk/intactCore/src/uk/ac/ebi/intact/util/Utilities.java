/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import java.io.*;
import java.util.*;

/** Various IntAct related utilities. If a pice of code is used more
 * than once, but does not really belong to a specific class, it
 * should become part of this class.
 */
public class Utilities {

    private Utilities() {
        // no instantiable
    };

    /** Initialise parameters from the properties file.
	@param aParameterName The file name of the properties file
	from which to read.

	This parameter must be given on the command line as
	for example -Dconfig=pathname
     */
    public static Properties getProperties(String aParameterName) throws IOException{

	// get properties
	Properties properties = new Properties();
	FileInputStream in = new FileInputStream(System.getProperty(aParameterName));
	properties.load(in);
	in.close();

	return properties;
    }

    /** compares two objects.
     * This is a workaround for the case where o is null
     * and o.equals(p) returns exceptions.
     *
     * @param o
     * @param p
     */
    public static boolean equals(Object o, Object p){
        if (o == p) return true;
        if (null == o && null != p) return false;
        return (o.equals(p));
    }
}


