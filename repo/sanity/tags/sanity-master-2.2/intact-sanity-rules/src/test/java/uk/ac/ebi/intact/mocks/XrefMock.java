/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class XrefMock {

    public static<T extends Xref> T getMock(Class<T> clazz, CvDatabase cvDatabase, CvXrefQualifier xrefQual, String primaryId) {
        T xref = null;
        try {
            xref = clazz.newInstance();
        } catch (InstantiationException e) {
            //If you get this Exception, so badly handled you're really unlucky, it should not happen. :-)
            e.printStackTrace();
            assert(false);
        } catch (IllegalAccessException e) {
            //If you get this Exception, so badly handled you're really unlucky, it should not happen. :-)
            e.printStackTrace();
            assert(false);
        }
        xref.setCvDatabase(cvDatabase);
        xref.setCvXrefQualifier(xrefQual);
        xref.setPrimaryId(primaryId);
        return xref;
    }

    public static<T extends Xref> T getMock(Class<T> clazz, CvDatabase cvDatabase, CvXrefQualifier xrefQual, String primaryId, String secondaryId, String databaseRelease) throws IllegalAccessException, InstantiationException {

        T xref = getMock(clazz, cvDatabase, xrefQual,primaryId);
        xref.setSecondaryId(secondaryId);
        xref.setDbRelease(databaseRelease);
        xref.setCreated(DateGetter.getCreatedDate());
        xref.setUpdated(DateGetter.getUpdatedDate());
        xref.setCreator("ARTHUR");
        xref.setUpdator("HENRY");
        return xref;
    }


    
}