/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.visitor;

import uk.ac.ebi.intact.model.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactVisitor {

    void visitIntactObject(IntactObject intactObject);

    void visitAnnotatedObject(AnnotatedObject annotatedObject);

    /////////////////////////////////////////////////////
    // IntactObject visitors

    void visitAnnotation(Annotation annotation);

    void visitAlias(Alias alias);

    void visitXref(Xref xref);

    void visitRange(Range range);


    /////////////////////////////////////////////////////
    // AnnotatedObjects

    void visitInstitution(Institution institution);

    void visitCvObject(CvObject cvObject);

    void visitExperiment(Experiment experiment);

    void visitFeature(Feature feature);

    void visitComponent(Component component);

    void visitInteraction(Interaction interaction);

    void visitInteractor(Interactor interactor);

    void visitBioSource(BioSource bioSource);

    void visitPublication(Publication publication);
}