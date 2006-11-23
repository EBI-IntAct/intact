/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.externalservices.searchengine;

import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.IOException;

/**
 * What an Index Exporter can do.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Nov-2006</pre>
 */
public interface IndexExporter<T extends AnnotatedObject> {

    public void exportHeader() throws IOException;

    public void exportEntryListStart() throws IOException;

    public void exportEntry( T object ) throws IOException;

    public void exportEntryListEnd() throws IOException;

    public void exportFooter() throws IOException;

    public int getEntryCount();
}