/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Alias;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class CvObjectMock {

    public static<T extends CvObject> T getMock(Class<T> clazz,String shortlabel, String fullname){
        T cvObject = null;
        try {
            cvObject = clazz.newInstance();
        } catch (InstantiationException e) {
            //If you get this Exception, so badly handled you're really unlucky, it should not happen. :-)
            e.printStackTrace();
            assert(false);
        } catch (IllegalAccessException e) {
            //If you get this Exception, so badly handled you're really unlucky, it should not happen. :-)
            e.printStackTrace();
            assert(false);
        }
        cvObject.setOwner(InstitutionMock.getMock());
        cvObject.setShortLabel(shortlabel);
        cvObject.setCreator("ANDREE");
        cvObject.setUpdator("LEON");
        cvObject.setAc(AcGenerator.getNextVal());
        cvObject.setFullName(fullname);
        cvObject.setCreated(DateGetter.getCreatedDate());
        cvObject.setUpdated(DateGetter.getUpdatedDate());
        return cvObject;
    }
}