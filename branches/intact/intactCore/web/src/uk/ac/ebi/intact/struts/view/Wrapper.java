/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

import org.apache.taglibs.display.TableDecorator;

/**
 * This class is a decorator of the ListObjects that we keep in our List. This
 * class provides a number of methods for formatting data, creating dynamic
 * links, and exercising some aspects of the display:table API functionality
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class Wrapper extends TableDecorator {

    /**
     * Creates a new Wrapper decorator who's job is to reformat some of the
     * data located in our ListObject's.
     */
    public Wrapper() {
        super();
    }
}
