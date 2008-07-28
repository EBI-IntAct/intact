/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class SanityAnnotationProcessorFactory implements AnnotationProcessorFactory {

    // Process only the SanityRule annotation
    private static final Collection<String> SUPPORTED_ANNOTATIONS
            = Collections.unmodifiableCollection(Arrays.asList(SanityRule.class.getName()));

    // No supported options
    private static final Collection<String> SUPPORTED_OPTIONS = Collections.emptyList();

    private SanityAnnotationProcessor annotationProcessor = null;

    public Collection<String> supportedAnnotationTypes() {
        return SUPPORTED_ANNOTATIONS;
    }

    public Collection<String> supportedOptions() {
        return SUPPORTED_OPTIONS;
    }

    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> atds,
                                               AnnotationProcessorEnvironment env) {

        if (annotationProcessor == null) {
            annotationProcessor = new SanityAnnotationProcessor(atds, env);
        }
        return annotationProcessor;
    }

}