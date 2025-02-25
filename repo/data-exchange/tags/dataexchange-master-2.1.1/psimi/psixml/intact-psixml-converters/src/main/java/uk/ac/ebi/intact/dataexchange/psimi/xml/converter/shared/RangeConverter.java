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

import psidev.psi.mi.xml.model.DbReference;
import psidev.psi.mi.xml.model.Interval;
import psidev.psi.mi.xml.model.Position;
import psidev.psi.mi.xml.model.RangeStatus;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.AbstractIntactPsiConverter;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.PsiConversionException;
import uk.ac.ebi.intact.model.CvFuzzyType;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Range;

/**
 * Range Converter.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RangeConverter extends AbstractIntactPsiConverter<Range, psidev.psi.mi.xml.model.Range> {

    public static final String CERTAIN = "MI:0335";
    public static final String MORE_THAN = "MI:0336";
    public static final String RANGE = "MI:0338";
    public static final String LESS_THAN = "MI:0337";

    public RangeConverter(Institution institution) {
        super(institution);
    }

    public Range psiToIntact(psidev.psi.mi.xml.model.Range psiObject) {
        Integer beginIntervalFrom = null;
        Integer beginIntervalTo = null;
        Integer endIntervalFrom = null;
        Integer endIntervalTo = null;

        if (psiObject.getBegin() != null) {
            final int begin = Long.valueOf(psiObject.getBegin().getPosition()).intValue();
            beginIntervalFrom = begin;
            beginIntervalTo = begin;
        }
        if (psiObject.getEnd() != null) {
            final int end = Long.valueOf(psiObject.getEnd().getPosition()).intValue();
            endIntervalFrom = end;
            endIntervalTo = end;
        }

        Interval beginInterval = psiObject.getBeginInterval();
        if (beginInterval != null) {
            beginIntervalFrom = Long.valueOf(beginInterval.getBegin()).intValue();
            beginIntervalTo = Long.valueOf(beginInterval.getEnd()).intValue();
        }

        Interval endInterval = psiObject.getEndInterval();
        if (endInterval != null) {
            endIntervalFrom = Long.valueOf(endInterval.getBegin()).intValue();
            endIntervalTo = Long.valueOf(endInterval.getEnd()).intValue();
        }

        String seq = null;

        if( ( beginIntervalFrom == null || endIntervalTo == null )
            && ( isRange( psiObject ) || isMoreThan( psiObject ) || isLessThan( psiObject ) || isCertain( psiObject ) ) ) {

            throw new PsiConversionException( "Cannot convert a Range of type range, less-than, more-than or certain without specific location (begin, end) to the IntAct data model." );

        } else if ( beginIntervalFrom == null || endIntervalTo == null ) {

            // set the values to 0 as this means undertermined in the IntAct model
            beginIntervalFrom = 0;
            beginIntervalTo = 0;
            endIntervalFrom = 0;
            endIntervalTo = 0;
        }

        Range range = new Range(getInstitution(), beginIntervalFrom, endIntervalTo, seq);
        range.setFromIntervalStart(beginIntervalFrom);
        range.setFromIntervalEnd(beginIntervalTo);
        range.setToIntervalStart(endIntervalFrom);
        range.setToIntervalEnd(endIntervalTo);

        CvObjectConverter<CvFuzzyType,RangeStatus> fuzzyTypeConverter =
                new CvObjectConverter<CvFuzzyType,RangeStatus>(getInstitution(), CvFuzzyType.class, RangeStatus.class);

        final RangeStatus startStatus = psiObject.getStartStatus();

        if (startStatus != null) {
            CvFuzzyType fromFuzzyType = fuzzyTypeConverter.psiToIntact(startStatus);
            range.setFromCvFuzzyType(fromFuzzyType);
        }

        final RangeStatus endStatus = psiObject.getEndStatus();

        if (endStatus != null) {
            CvFuzzyType toFuzzyType = fuzzyTypeConverter.psiToIntact(endStatus);
            range.setToCvFuzzyType(toFuzzyType);
        }

        return range;
    }

    private boolean isRange( psidev.psi.mi.xml.model.Range range ) {
        if ( isStatusOfType( range.getStartStatus(), RANGE ) || isStatusOfType( range.getEndStatus(), "MI:0338" ) ) {
            return true;
        }
        return false;
    }

    private boolean isLessThan( psidev.psi.mi.xml.model.Range range ) {

        if ( isStatusOfType( range.getStartStatus(), LESS_THAN ) || isStatusOfType( range.getEndStatus(), "MI:0337" ) ) {
            return true;
        }
        return false;
    }

    private boolean isMoreThan( psidev.psi.mi.xml.model.Range range ) {
        if ( isStatusOfType( range.getStartStatus(), MORE_THAN ) || isStatusOfType( range.getEndStatus(), MORE_THAN ) ) {
            return true;
        }
        return false;
    }

    private boolean isCertain( psidev.psi.mi.xml.model.Range range ) {
        if ( isStatusOfType( range.getStartStatus(), CERTAIN ) || isStatusOfType( range.getEndStatus(), CERTAIN ) ) {
            return true;
        }
        return false;
    }

    private boolean isStatusOfType( RangeStatus status, String psimiIdentifier ) {
        if ( status.getXref() != null ) {
            final DbReference ref = status.getXref().getPrimaryRef();
            return ref.getId().equals( psimiIdentifier );
        }
        return false;
    }

    public psidev.psi.mi.xml.model.Range intactToPsi(Range intactObject) {
        psidev.psi.mi.xml.model.Range psiRange = new psidev.psi.mi.xml.model.Range();

        long beginIntervalFrom = intactObject.getFromIntervalStart();
        long beginIntervalTo = intactObject.getFromIntervalEnd();
        long endIntervalFrom = intactObject.getToIntervalStart();
        long endIntervalTo = intactObject.getToIntervalEnd();

        if( beginIntervalFrom != 0 ) {
            psiRange.setBegin(new Position(beginIntervalFrom));
        }

        if( endIntervalTo != 0 ) {
            psiRange.setEnd(new Position(endIntervalTo));
        }

        if ( beginIntervalTo > beginIntervalFrom && beginIntervalTo != 0 ) {
            Interval beginInterval = new Interval(beginIntervalFrom, beginIntervalTo);
            psiRange.setBeginInterval(beginInterval);
        }

        if ( endIntervalTo > endIntervalFrom && endIntervalTo != 0 ) {
            Interval endInterval = new Interval(endIntervalFrom, endIntervalTo);
            psiRange.setEndInterval(endInterval);
        }

        CvObjectConverter<CvFuzzyType,RangeStatus> fuzzyTypeConverter =
                        new CvObjectConverter<CvFuzzyType,RangeStatus>( getInstitution(),
                                                                        CvFuzzyType.class,
                                                                        RangeStatus.class );

        final CvFuzzyType fromFuzzyType = intactObject.getFromCvFuzzyType();

        if (fromFuzzyType != null) {
            RangeStatus startStatus = fuzzyTypeConverter.intactToPsi(fromFuzzyType);
            psiRange.setStartStatus(startStatus);
        }

        final CvFuzzyType toFuzzyType = intactObject.getToCvFuzzyType();

        if (toFuzzyType != null) {
            RangeStatus endStatus = fuzzyTypeConverter.intactToPsi(toFuzzyType);
            psiRange.setEndStatus(endStatus);
        }

        return psiRange;
    }
}