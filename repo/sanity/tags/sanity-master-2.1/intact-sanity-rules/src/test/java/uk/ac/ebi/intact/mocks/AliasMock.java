/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.CvAliasType;
import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AliasMock {
    public static<T extends Alias> T getMock(Class<T> clazz, CvAliasType cvAliasType, AnnotatedObject annotatedObject) {
        T alias = null;
        try {
            alias = clazz.newInstance();
        } catch (InstantiationException e) {
            //If you get this Exception, so badly handled you're really unlucky, it should not happen. :-)
            e.printStackTrace();
            assert(false);
        } catch (IllegalAccessException e) {
            //If you get this Exception, so badly handled you're really unlucky, it should not happen. :-)
            e.printStackTrace();
            assert(false);
        }
        alias.setAc(AcGenerator.getNextVal());
            alias.setCreated(DateGetter.getCreatedDate());
            alias.setUpdated(DateGetter.getUpdatedDate());
            alias.setCreator("ARTHUR");
            alias.setUpdator("HENRY");
            alias.setOwner(InstitutionMock.getMock());
            alias.setCvAliasType(cvAliasType);
            alias.setParent(annotatedObject);
            return alias;
        }

}