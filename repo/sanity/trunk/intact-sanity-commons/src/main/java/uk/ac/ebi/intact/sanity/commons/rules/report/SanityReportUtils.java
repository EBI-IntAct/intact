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

import uk.ac.ebi.intact.sanity.commons.InsaneObject;
import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.SanityResult;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;

import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
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
        report.getReportAttribute().addAll(originalReport.getReportAttribute());

        for (SanityResult originalResult : originalReport.getSanityResult()) {
            report.getSanityResult().add(cloneSanityResult(originalResult));
        }

        return report;
    }

    protected static SanityResult cloneSanityResult(SanityResult originalResult) {
        SanityResult result = new SanityResult();
        result.setDescription(originalResult.getDescription());
        result.setLevel(originalResult.getLevel());
        result.setSuggestion(originalResult.getSuggestion());
        result.getInsaneObject().addAll(originalResult.getInsaneObject());

        return result;
    }

    /**
     * Modifies the provided report using the filters
     * @param report the report to filter
     * @param filters the varags filters to be used
     */
    public static void filterSanityReport(SanityReport report, ReportFilter ... filters) {
        for (Iterator<SanityResult> iterator = report.getSanityResult().iterator(); iterator.hasNext();) {
            SanityResult sanityResult = iterator.next();
            filterSanityResult(sanityResult, filters);

            if (sanityResult.getInsaneObject().isEmpty()) {
                iterator.remove();
            }
        }
    }

    protected static void filterSanityResult(SanityResult sanityResult, ReportFilter ... filters) {
        for (Iterator<InsaneObject> iterator = sanityResult.getInsaneObject().iterator(); iterator.hasNext();) {
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

        for (SanityResult result : report.getSanityResult()) {
            for (InsaneObject insaneObject : result.getInsaneObject()) {
                insaneCurators.add(insaneObject.getUpdator());
            }
        }

        return insaneCurators;
    }

    /**
     * Creates a Map with the SanityReport for each insane updator
     * @param report the original report
     * @return the map with the reports per updator
     */
    public static Map<String, SanityReport> createPersonalizedReports(SanityReport report) {
        Map<String, SanityReport> updatorReports = new HashMap<String, SanityReport>();
        Collection<String> insaneUpdatorNames = getInsaneUpdatorNames(report);

        for (String insaneUpdatorName : insaneUpdatorNames) {
            SanityReport updatorReport = cloneSanityReport(report);
            filterSanityReport(updatorReport, new UserReportFilter(insaneUpdatorName));

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

        for (SanityResult result : report.getSanityResult()) {
            insaneObjects.addAll(result.getInsaneObject());
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