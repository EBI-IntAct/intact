/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Terms in this controlled vocabulary class qualify the association
 * between AnnotatedObject and Xref.
 * @intact.example identical
 * @intact.example homologue
 *
 * @author hhe
 */
public class CvXrefQualifier extends CvObject implements Editable {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     *
     */
    protected static Vector menuList = null;


} // end CvXrefQualifier




