/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

import org.apache.taglibs.display.TableDecorator;

/**
 * This class is a decorator of the XreferenceBean to reformat data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XreferenceBeanWrapper extends TableDecorator {

    /**
     * Returns the hyperlink to delete a X'reference.
     */
    public String getDeleteLink() {
        String ac = ((XreferenceBean) super.getObject()).getAc();
        return "<a href=\"delXref.do?ac=" + ac + "\">Delete</a>";
    }
}
