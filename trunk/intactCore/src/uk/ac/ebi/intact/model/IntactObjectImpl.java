/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This is the top level class for all intact model object.
 *
 * @author intact team
 * @version $Id$
 */
public abstract class IntactObjectImpl implements IntactObject, Serializable {

    /**
     * This is used in toString() in order to be platform compatible.
     */
    protected final String NEW_LINE = System.getProperty("line.separator");



    ///////////////////////////////////////
    //attributes

    /**
     * TODO: comments
     */
    private String ojbConcreteClass;


    /**
     * The unique accession number of an object. This is
     * defined as protected to allow concrete subclasses to generate
     * copies if required.
     */
    protected String ac;

    /**
     * Creation date of an object.
     * The type is java.sql.Date, not java.util.Data,
     * for database compatibility.
     */
    private Timestamp created;

    /**
     * The last update of the object.
     * The type is java.sql.Date, not java.util.Data,
     * for database compatibility.
     */
    private Timestamp updated;

    /**
     * Protected constructor for use by subclasses - used to set
     * the creation data for instances
     */
    protected IntactObjectImpl() {

        ojbConcreteClass = this.getClass().getName();
        created = new java.sql.Timestamp(System.currentTimeMillis());
        updated = new java.sql.Timestamp(System.currentTimeMillis());
    }


    ///////////////////////////////////////
    //access methods for attributes

    public String getAc() {
        return ac;
    }

    /**
     * This method should not be used by applications, as the AC
     * is a primary key which is auto-generated. If we move
     * to an application server it may then be needed.
     * @param ac
     * @deprecated No replacement - should not be used by applications
     */
    public void setAc(String ac) {
        this.ac = ac;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(java.util.Date created) {

        if(created != null) {

            this.created = new Timestamp(created.getTime());
        }
        else {

            //needed for object-based search
            this.created = null;
        }
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(java.util.Date updated) {

        if(updated != null) {

            this.updated = new Timestamp(updated.getTime());
        }
        else {

            //needed for object-based search
            this.updated = null;
        }
    }



    ///////////////////////////////////////
    // instance methods

    public String toString() {
        return this.ac;
    }

} // end IntactObject




