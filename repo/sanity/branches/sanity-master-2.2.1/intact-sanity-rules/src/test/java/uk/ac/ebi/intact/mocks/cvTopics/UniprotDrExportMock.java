/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvTopics;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class UniprotDrExportMock {
    private static final String USED_IN_CLASS_DESCR = CvInteraction.class.getName();
    public static CvTopic getMock(){
        CvTopic uniprotDrExport = CvObjectMock.getMock(CvTopic.class,CvTopic.UNIPROT_DR_EXPORT, "uniprot-dr-export");

        uniprotDrExport = (CvTopic) IntactObjectSetter.setBasicObject(uniprotDrExport);

        Annotation usedInClass = AnnotationMock.getMock(UsedInClassMock.getMock(),USED_IN_CLASS_DESCR );
        uniprotDrExport.addAnnotation(usedInClass);

        return uniprotDrExport;
    }

}