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
package uk.ac.ebi.intact.sanity.commons.rules.report;

import uk.ac.ebi.intact.sanity.commons.report.InsaneObject;
import uk.ac.ebi.intact.sanity.commons.report.SanityReport;
import uk.ac.ebi.intact.sanity.commons.report.SanityResult;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Different utilities to work with sanity reports
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityReportUtils {

    private SanityReportUtils() {
    }


    /**
     * Clones a sanity report
     * @param originalReport the report to clone
     * @return the cloned report
     */
    public static SanityReport cloneSanityReport(SanityReport originalReport) {
        SanityReport report = new SanityReport();
        report.setDatabase(originalReport.getDatabase());
        report.getReportAttributes().addAll(originalReport.getReportAttributes());

        for (SanityResult originalResult : originalReport.getSanityResults()) {
            report.getSanityResults().add(cloneSanityResult(originalResult));
        }

        return report;
    }

    protected static SanityResult cloneSanityResult(SanityResult originalResult) {
        SanityResult result = new SanityResult();
        result.setKey(originalResult.getKey());
        result.setTargetClass(originalResult.getTargetClass());
        result.setDescription(originalResult.getDescription());
        result.setLevel(originalResult.getLevel());
        result.setSuggestion(originalResult.getSuggestion());
        result.getInsaneObjects().addAll(originalResult.getInsaneObjects());

        return result;
    }

    /**
     * Modifies the provided report using the filters
     * @param report the report to filter
     * @param filters the varags filters to be used
     */
    public static void filterSanityReport(SanityReport report, ReportFilter ... filters) {
        for (Iterator<SanityResult> iterator = report.getSanityResults().iterator(); iterator.hasNext();) {
            SanityResult sanityResult = iterator.next();
            filterSanityResult(sanityResult, filters);

            if (sanityResult.getInsaneObjects().isEmpty()) {
                iterator.remove();
            }
        }
    }

    protected static void filterSanityResult(SanityResult sanityResult, ReportFilter ... filters) {
        for (Iterator<InsaneObject> iterator = sanityResult.getInsaneObjects().iterator(); iterator.hasNext();) {
            InsaneObject insaneObject = iterator.next();

            for (ReportFilter filter : filters) {
                if (!filter.accept(insaneObject)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Gets the names of the updators present in the insane objects
     * @param report the report to use
     * @return names of the insane updators
     */
    public static Set<String> getInsaneUpdatorNames(SanityReport report) {
        Set<String> insaneCurators = new HashSet<String>();

        for (SanityResult result : report.getSanityResults()) {
            for (InsaneObject insaneObject : result.getInsaneObjects()) {
                insaneCurators.add(insaneObject.getUpdator().trim());
            }
        }

        return insaneCurators;
    }

    /**
     * Gets the names of the creators present in the insane objects
     * @param report the report to use
     * @return names of the insane creators
     */
    public static Set<String> getInsaneCreatorNames(SanityReport report) {
        Set<String> insaneCurators = new HashSet<String>();

        for (SanityResult result : report.getSanityResults()) {
            for (InsaneObject insaneObject : result.getInsaneObjects()) {
                insaneCurators.add(insaneObject.getUpdator().trim());
            }
        }

        return insaneCurators;
    }

    /**
     * Creates a Map with the SanityReport for each insane creator
     * @param report the original report
     * @return the map with the reports per updator
     */
    public static Map<String, SanityReport> createPersonalizedReportsByCreator(SanityReport report) {
        Map<String, SanityReport> updatorReports = new HashMap<String, SanityReport>();
        Collection<String> insaneUpdatorNames = getInsaneCreatorNames(report);

        for (String insaneUpdatorName : insaneUpdatorNames) {
            SanityReport updatorReport = cloneSanityReport(report);
            filterSanityReport(updatorReport, new CreatorReportFilter(insaneUpdatorName));

            updatorReports.put(insaneUpdatorName, updatorReport);
        }

        return updatorReports;
    }

    /**
     * Creates a Map with the SanityReport for each insane updator
     * @param report the original report
     * @return the map with the reports per updator
     */
    public static Map<String, SanityReport> createPersonalizedReportsByUpdator(SanityReport report) {
        Map<String, SanityReport> updatorReports = new HashMap<String, SanityReport>();
        Collection<String> insaneUpdatorNames = getInsaneUpdatorNames(report);

        for (String insaneUpdatorName : insaneUpdatorNames) {
            SanityReport updatorReport = cloneSanityReport(report);
            filterSanityReport(updatorReport, new UpdatorReportFilter(insaneUpdatorName));

            updatorReports.put(insaneUpdatorName, updatorReport);
        }

        return updatorReports;
    }

    /**
     * Gets all the insane objects from a report
     * @param report the report to use
     * @return the insane objects in the report
     */
    public static Collection<InsaneObject> getAllInsaneObject(SanityReport report) {
        Collection<InsaneObject> insaneObjects = new ArrayList<InsaneObject>();

        for (SanityResult result : report.getSanityResults()) {
            insaneObjects.addAll(result.getInsaneObjects());
        }

        return insaneObjects;
    }

    /**
     * Prints a report in a print stream
     * @param report
     * @param ps
     */
    public static void printReport(SanityReport report, PrintStream ps) {
        Writer writer = new StringWriter();

        ReportWriter reportWriter = new SimpleReportWriter(writer);
        try {
            reportWriter.write(report);
        } catch (IOException e) {
            throw new SanityRuleException("Problem printing report", e);
        }

        ps.print(writer.toString());
    }
}