/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.editor.controller.curate.cloner;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.model.extension.InteractorAlias;
import uk.ac.ebi.intact.jami.model.extension.InteractorAnnotation;
import uk.ac.ebi.intact.jami.model.extension.InteractorXref;

import java.lang.reflect.InvocationTargetException;

/**
 * Editor specific cloning routine for interactors.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id: InteractionIntactCloner.java 14783 2010-07-29 12:52:28Z brunoaranda $
 * @since 2.0.1-SNAPSHOT
 */
public class InteractorCloner extends EditorCloner{


    public static Interactor cloneInteractor(Interactor interactor, IntactDao dao) {
        Interactor clone = null;
        try {
            clone = interactor.getClass().getConstructor(String.class).newInstance(interactor.getShortName());

            if (clone instanceof Auditable){
                initAuditProperties((Auditable)clone, dao);
            }

            clone.setShortName(interactor.getShortName());
            clone.setFullName(interactor.getFullName());
            clone.setInteractorType(interactor.getInteractorType());
            clone.setOrganism(interactor.getOrganism());
            clone.getChecksums().addAll(interactor.getChecksums());

            for (Object obj : interactor.getAliases()){
                Alias alias = (Alias)obj;
                clone.getAliases().add(new InteractorAlias(alias.getType(), alias.getName()));
            }

            for (Object obj: interactor.getIdentifiers()){
                Xref ref = (Xref)obj;
                clone.getIdentifiers().add(new InteractorXref(ref.getDatabase(), ref.getId(), ref.getVersion(), ref.getQualifier()));
            }

            for (Object obj : interactor.getXrefs()){
                Xref ref = (Xref)obj;
                clone.getXrefs().add(new InteractorXref(ref.getDatabase(), ref.getId(), ref.getVersion(), ref.getQualifier()));
            }

            for (Object obj : interactor.getAnnotations()){
                Annotation annotation = (Annotation)obj;
                clone.getAnnotations().add(new InteractorAnnotation(annotation.getTopic(), annotation.getValue()));
            }

            if (interactor instanceof Polymer){
                ((Polymer)clone).setSequence(((Polymer) interactor).getSequence());
            }
            else if (interactor instanceof InteractorPool){
                ((InteractorPool)clone).addAll((InteractorPool)interactor);
            }

            return clone;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot clone interactor "+interactor, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot clone interactor "+interactor, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Cannot clone interactor "+interactor, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot clone interactor "+interactor, e);
        }
    }
}

