/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.externalservices.searchengine;

import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.File;
import java.io.IOException;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Nov-2006</pre>
 */
public class InteractionIndexExporter extends AbstractIndexExporter {

    public static final String INDEX_NAME = "IntAct.Interaction";

    public InteractionIndexExporter( File output, String release ) {
        super( output, release );
    }

    public void exportEntry( AnnotatedObject object ) throws IOException {
    }

    public int getEntryCount() {
        return 0;
    }
}