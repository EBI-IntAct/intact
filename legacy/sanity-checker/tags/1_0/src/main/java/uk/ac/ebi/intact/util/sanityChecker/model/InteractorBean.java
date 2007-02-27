/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker.model;

import java.sql.Timestamp;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorBean extends AnnotatedBean {

    private String objclass;
    private String interactiontype_ac;
    private String biosource_ac;
    private String crc64;

    public InteractorBean() {
    }

    public String getObjclass() {
        return objclass;
    }

    public void setObjclass(String objclass) {
        this.objclass = objclass;
    }

    public String getInteractiontype_ac() {
        return interactiontype_ac;
    }

    public void setInteractiontype_ac(String interactiontype_ac) {
        this.interactiontype_ac = interactiontype_ac;
    }

    public String getBiosource_ac() {
        return biosource_ac;
    }

    public void setBiosource_ac(String biosource_ac) {
        this.biosource_ac = biosource_ac;
    }

    public String getCrc64() {
        return crc64;
    }

    public void setCrc64(String crc64) {
        this.crc64 = crc64;
    }

}
