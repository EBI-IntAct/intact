/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvTopics;

import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class UrlMock {
    private static final String SEPARATOR = ", ";
    private static final String USED_IN_CLASS_DESCR = Experiment.class.getName()
                                                      + SEPARATOR + Interaction.class.getName()
                                                      + SEPARATOR + CvObject.class.getName()
                                                      + SEPARATOR + BioSource.class.getName();
    public static CvTopic getMock(){
        CvTopic url = CvObjectMock.getMock(CvTopic.class,CvTopic.URL, "url");
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(), CvTopic.URL_MI_REF);
        url.addXref(xref);
        Annotation usedInClass = AnnotationMock.getMock(UsedInClassMock.getMock(),USED_IN_CLASS_DESCR );
        url.addAnnotation(usedInClass);
        return url;
    }
}