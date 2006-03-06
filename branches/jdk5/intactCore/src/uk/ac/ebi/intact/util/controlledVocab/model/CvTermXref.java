/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab.model;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Sep-2005</pre>
 */
public class CvTermXref {

    private String id;
    private String database;

    public CvTermXref( String id, String database ) {
        this.id = id;
        this.database = database;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        if ( id != null ) {
            id = id.trim();
        }
        this.id = id;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase( String database ) {
        if ( database != null ) {
            database = database.trim();
        }
        this.database = database;
    }
}