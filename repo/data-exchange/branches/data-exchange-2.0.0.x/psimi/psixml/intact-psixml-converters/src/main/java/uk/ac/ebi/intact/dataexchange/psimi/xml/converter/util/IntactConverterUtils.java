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
package uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.xml.model.Attribute;
import psidev.psi.mi.xml.model.AttributeContainer;
import psidev.psi.mi.xml.model.DbReference;
import psidev.psi.mi.xml.model.Names;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared.AliasConverter;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared.AnnotationConverter;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared.XrefConverter;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.ConverterContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import java.util.Collection;
import java.util.Random;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactConverterUtils {

    private static final Log log = LogFactory.getLog(IntactConverterUtils.class);

    private static final int SHORT_LABEL_LENGTH = 20;

    private IntactConverterUtils() {
    }

    public static void populateNames(Names names, AnnotatedObject<?, ?> annotatedObject) {
        String shortLabel = getShortLabelFromNames(names);

        if (names == null && (annotatedObject instanceof Experiment) ) {
            shortLabel = createExperimentTempShortLabel();
        }

        if ( ! ( annotatedObject instanceof Institution ) ) {
            if ( shortLabel != null ) {
                shortLabel = shortLabel.toLowerCase();
            }
        }

        annotatedObject.setShortLabel(shortLabel);

        if (names != null) {
            annotatedObject.setFullName(names.getFullName());

            Class<?> aliasClass = AnnotatedObjectUtils.getAliasClassType(annotatedObject.getClass());
            AliasConverter aliasConverter = new AliasConverter(getInstitution(annotatedObject), aliasClass);

            populateAliases(names.getAliases(), annotatedObject, aliasConverter);
        }
    }

    public static <X extends Xref> void populateXref(psidev.psi.mi.xml.model.Xref psiXref, AnnotatedObject<X, ?> annotatedObject, XrefConverter<X> xrefConverter) {
        if (psiXref == null) {
            return;
        }

        if (psiXref.getPrimaryRef() != null) {
            addXref(psiXref.getPrimaryRef(), annotatedObject, xrefConverter);
        }

        for (DbReference secondaryRef : psiXref.getSecondaryRef()) {
            addXref(secondaryRef, annotatedObject, xrefConverter);
        }
    }

    private static <X extends Xref> void addXref(DbReference dbReference, AnnotatedObject<X, ?> annotatedObject, XrefConverter<X> xrefConverter) {
        X xref = xrefConverter.psiToIntact(dbReference);
        annotatedObject.addXref(xref);

        if (annotatedObject instanceof Institution) {
            xref.setOwner((Institution)annotatedObject);
        } else if (xref instanceof CvObjectXref) {
            ((CvObjectXref)xref).prepareParentMi();
        }
    }

    public static <A extends Alias> void populateAliases(Collection<psidev.psi.mi.xml.model.Alias> psiAliases, AnnotatedObject<?, A> annotatedObject, AliasConverter<A> aliasConverter) {
        if (psiAliases == null) {
            return;
        }

        for (psidev.psi.mi.xml.model.Alias psiAlias : psiAliases) {
            if (psiAlias.hasValue()) {
                A alias = aliasConverter.psiToIntact(psiAlias);
                annotatedObject.addAlias(alias);

                if (annotatedObject instanceof Institution) {
                    alias.setOwner((Institution) annotatedObject);
                }
            } else {
                if (log.isWarnEnabled()) log.warn("Alias without value in location: "+ ConverterContext.getInstance().getLocation().getCurrentLocation().pathFromRootAsString());
            }
        }
    }

    public static void populateAnnotations(AttributeContainer attributeContainer, Annotated annotated, Institution institution) {
        AnnotationConverter annotationConverter = new AnnotationConverter(institution);

        if (attributeContainer.hasAttributes()) {
            for (Attribute attribute : attributeContainer.getAttributes()) {
                Annotation annotation = annotationConverter.psiToIntact(attribute);
                annotation.setOwner(institution);

                if (!annotated.getAnnotations().contains(annotation)) {
                    annotated.getAnnotations().add(annotation);
                }
            }
        }
    }

    public static CvXrefQualifier createCvXrefQualifier(Institution institution, DbReference dbReference) {
        String xrefType = dbReference.getRefType();
        CvXrefQualifier xrefQual = null;

        if (xrefType != null) {
            xrefQual = new CvXrefQualifier(institution, xrefType);
        }

        return xrefQual;
    }

    public static String getShortLabelFromNames(Names names) {
        if (names == null) {
            return IntactConverterUtils.createTempShortLabel();
        }

        String shortLabel = names.getShortLabel();
        String fullName = names.getFullName();

        // If the short label is null, but not the full name, use the full name as short label.
        // Truncate the full name if its length > SHORT_LABEL_LENGTH
        if (shortLabel == null) {
            if (fullName != null) {
                if (log.isWarnEnabled()) log.warn("Short label is null. Using full name as short label: " + fullName);
                shortLabel = fullName;

            } else {
                throw new NullPointerException("Both fullName and shortLabel are null");
            }
        }

        if (shortLabel.length() > SHORT_LABEL_LENGTH) {
            shortLabel = shortLabel.substring(0, SHORT_LABEL_LENGTH);

            if (log.isWarnEnabled()) {
                String msg = "\tFull name to short label truncated to: '" + shortLabel+"'";
                if (ConverterContext.getInstance().getLocation() != null && ConverterContext.getInstance().getLocation().getCurrentLocation() != null) {
                    msg = msg + " in location: "+ ConverterContext.getInstance().getLocation().getCurrentLocation().pathFromRootAsString();
                }
                log.warn(msg);
            }
        }

        return shortLabel;
    }

    public static String createTempShortLabel() {
        return InteractionUtils.INTERACTION_TEMP_LABEL_PREFIX + Math.abs(new Random().nextInt());
    }

    public static String createExperimentTempShortLabel() {
        return new IntactMockBuilder().randomString(5)+"-0000";
    }

    @Deprecated
    public static boolean isTempShortLabel(String label) {
        return InteractionUtils.isTemporaryLabel(label);
    }

    protected static Institution getInstitution(AnnotatedObject ao) {
        if (ao instanceof Institution) {
            return (Institution)ao;
        } else {
            return ao.getOwner();
        }
    }
}