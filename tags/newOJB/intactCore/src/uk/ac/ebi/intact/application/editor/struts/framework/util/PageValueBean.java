/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This bean class is for constructing value parameter for HTML buttons.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class PageValueBean {

    /**
     * The pattern to extract action and the ac. The pattern matches
     * interaction|edit|EBI-xxxx
     */
    private static final Pattern ourValuePat =
            Pattern.compile("(\\w+)\\|(\\w+)\\|(.+)$");

    /**
     * The major (first) component.
     */
    private String myMajorComp;

    /**
     * The minor (second) component.
     */
    private String myMinorComp;

    /**
     * The AC component.
     */
    private String myAcComp;

    /**
     * Constructs from given values for major, minor and ac components.
     * @param major the major component.
     * @param minor the minor component.
     * @param ac the AC.
     */
    public PageValueBean(String major, String minor, String ac) {
        initialize(major, minor, ac);
    }

    /**
     * Constructs from a string.
     * @param str the string to construct an instance of this class.
     * @throws IllegalArgumentException if <code>str</code> is not a well formed
     * string.
     */
    public PageValueBean(String str) {
        Matcher matcher = ourValuePat.matcher(str);
        if (matcher.matches()) {
            initialize(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        else {
            throw new IllegalArgumentException("Unable to parse " + str);
        }
    }

    // Override to toString method of Object class.

    public String toString() {
        return "value=" + "\"" + myMajorComp + "|" + myMinorComp
                + "|" + myAcComp + "\"";
    }

    // Getter methods.

//    public String getMajorComp() {
//        return myMajorComp;
//    }

    public boolean isMajor(String major) {
        return myMajorComp.equals(major);
    }

    public boolean isMinor(String minor) {
        return myMinorComp.equals(minor);
    }

//    public static String getMinor(String str, String major) {
//        Matcher matcher = ourValuePat.matcher(str);
//        if (matcher.matches()) {
//            if (matcher.group(1).equals(major)) {
//                return matcher.group(2);
//            }
//        }
//        return null;
//    }

//    public static String getMinor(String str) {
//        Matcher matcher = ourValuePat.matcher(str);
//        if (matcher.matches()) {
//            return matcher.group(2);
//        }
//        return null;
//    }
//
//    public static boolean isMinor(String str, String minor) {
//        Matcher matcher = ourValuePat.matcher(str);
//        if (matcher.matches()) {
//            return minor.equals(matcher.group(2));
//        }
//        return false;
//    }

    public String getAc() {
        return myAcComp;
    }

    private void initialize(String major, String minor, String ac) {
        myMajorComp = major;
        myMinorComp = minor;
        myAcComp = ac;
    }
}
