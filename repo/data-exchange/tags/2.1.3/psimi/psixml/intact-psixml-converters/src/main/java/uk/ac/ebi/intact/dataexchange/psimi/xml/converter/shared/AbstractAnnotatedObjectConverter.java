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
package uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared;

import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.AbstractIntactPsiConverter;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.ConverterContext;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.ConversionCache;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.PsiConverterUtils;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;

import java.util.Collection;

/**
 * Abstract Annotated Object Converter.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractAnnotatedObjectConverter<A extends AnnotatedObject, T> extends AbstractIntactPsiConverter<A, T> {

    private Class<? extends A> intactClass;
    private Class<T> psiClass;
    private boolean newIntactObjectCreated;
    private boolean newPsiObjectCreated;

    public AbstractAnnotatedObjectConverter(Institution institution, Class<? extends A> intactClass, Class<T> psiClass) {
        super(institution);
        this.intactClass = intactClass;
        this.psiClass = psiClass;
    }

    public A psiToIntact(T psiObject) {
        A intactObject = (A) ConversionCache.getElement(psiObject);
        
        if (intactObject != null) {
            newIntactObjectCreated = false;
            return intactObject;
        }

        intactObject = newIntactObjectInstance(psiObject);

        if (!(intactObject instanceof Institution)) {
            intactObject.setOwner(getInstitution());
        }

        ConversionCache.putElement(psiObject, intactObject);

        newIntactObjectCreated = true;

        return intactObject;
    }

    public T intactToPsi(A intactObject) {
        T psiObject = (T) ConversionCache.getElement(intactObject);

        if (psiObject != null) {
            newPsiObjectCreated = false;
            return psiObject;
        }

        psiObject = newInstance(psiClass);
        PsiConverterUtils.populate(intactObject, psiObject, this);

        ConversionCache.putElement(intactObject, psiObject);

        newPsiObjectCreated = true;

        return psiObject;
    }

    protected A newIntactObjectInstance(T psiObject) {
        return newInstance(intactClass);
    }

    private static <T> T newInstance(Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    protected boolean isNewIntactObjectCreated() {
        return newIntactObjectCreated;
    }

    protected boolean isNewPsiObjectCreated() {
        return newPsiObjectCreated;
    }
}