/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.apt.mockable;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import uk.ac.ebi.intact.annotation.Mockable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-Oct-2006</pre>
 */
public class MockableProcessorFactory implements AnnotationProcessorFactory {

    private static final Collection<String> SUPPORTED_ANNOTATIONS
            = Collections.unmodifiableCollection(Arrays.asList(Mockable.class.getName()));

    private static final Collection<String> SUPPORTED_OPTIONS =
            Arrays.asList("-AtargetDir");

    private MockableProcessor annotationProcessor = null;

    public Collection<String> supportedAnnotationTypes() {
        return SUPPORTED_ANNOTATIONS;
    }

    public Collection<String> supportedOptions() {
        return SUPPORTED_OPTIONS;
    }

    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> atds,
                                               AnnotationProcessorEnvironment env
    ) {
        if (annotationProcessor == null) {
            annotationProcessor = new MockableProcessor(atds, env);
        }
        return annotationProcessor;
    }

}