/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters.referenceFilter;

import java.util.List;

/**
 * Interface for defining a UniProt cross reference filter.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Oct-2006</pre>
 */
public interface CrossReferenceFilter {

    /**
     * identifies cross reference to be included based on their database name.
     *
     * @param database database name.
     *
     * @return true if the given database name is to be included, false otherwise.
     */
    public boolean isSelected( String database );

    /**
     * Return the list of databases that this filter will allow to enter in the model.
     *
     * @return a non null list of database name.
     */
    public List<String> getFilteredDatabases();
}