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
package uk.ac.ebi.intact.psixml.tools.extension;

import uk.ac.ebi.intact.psixml.tools.extension.annotation.PsiExtension;
import uk.ac.ebi.intact.psixml.tools.extension.annotation.PsiExtensionContext;
import uk.ac.ebi.intact.psixml.tools.generator.metadata.util.PsiReflectionUtils;
import uk.ac.ebi.intact.psixml.tools.validator.ValidationReport;

import java.lang.reflect.Field;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExtensionContext<T> {

    private T element;
    private ValidationReport validationReport;

    public ExtensionContext(T element, ValidationReport validationReport) {
        this.element = element;
        this.validationReport = validationReport;
    }

    public void injectIntoExtension(Object extension) throws ContextInjectionException {
        Class extensionClass = extension.getClass();

        if (!extensionClass.isAnnotationPresent(PsiExtension.class)) {
            throw new ContextInjectionException("Class " + extensionClass + " is not a valid PSI extension, since it does not have the @PsiExtension annotation");
        }

        List<Field> extensionContextFields = PsiReflectionUtils.fieldsWithAnnotation(extensionClass, PsiExtensionContext.class);

        for (Field extensionContextField : extensionContextFields) {
            try {
                extensionContextField.set(extension, this);
            } catch (IllegalAccessException e) {
                throw new ContextInjectionException(e, extension);
            }
        }
    }

    public Object getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public ValidationReport getValidationReport() {
        return validationReport;
    }

    public void setValidationReport(ValidationReport validationReport) {
        this.validationReport = validationReport;
    }
}