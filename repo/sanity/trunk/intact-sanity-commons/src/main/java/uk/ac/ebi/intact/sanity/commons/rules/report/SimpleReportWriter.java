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

import uk.ac.ebi.intact.sanity.commons.*;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SimpleReportWriter extends ReportWriter {

    protected static final String NEW_LINE = System.getProperty( "line.separator" );

    private Writer writer;

    public SimpleReportWriter(Writer writer) {
        this.writer = writer;
    }

    protected void writeReport(SanityReport report) throws IOException {
        if (report.getDatabase() != null) {
            writer.write("# Instance name: "+report.getDatabase());
            writer.write(NEW_LINE);
        }

        for (ReportAttribute attribute : report.getReportAttribute()) {
            writer.write("# "+attribute.getName()+": "+attribute.getValue());
            writer.write(NEW_LINE);
        }

        for (SanityResult sanityResult : report.getSanityResult()) {
            writer.write(prepareSanityResult(sanityResult).toString());
            writer.write(NEW_LINE);
        }

        if (report.getSanityResult().isEmpty()) {
            writer.write(NEW_LINE);
            writer.write("No rules failed.");
            writer.write(NEW_LINE);
        }
    }

    protected StringBuilder prepareSanityResult(SanityResult sanityResult) {
        StringBuilder sb = new StringBuilder();

        MessageLevel level = MessageLevel.valueOf(sanityResult.getLevel());

        char levelChar;

        switch (level) {
            case MAJOR:
                levelChar = '#';
                break;
            case NORMAL:
                levelChar = '=';
                break;
            case MINOR:
                levelChar = '-';
                break;
            default:
                levelChar = '?';
        }

        sb.append(separator(levelChar)).append(NEW_LINE);
        sb.append(levelChar).append(" ").append(sanityResult.getDescription()).append(NEW_LINE);
        sb.append(levelChar).append(" Suggestion: ").append(sanityResult.getSuggestion()).append(NEW_LINE);
        sb.append(levelChar).append(" Level: ").append(sanityResult.getLevel());
        sb.append(NEW_LINE);
        sb.append(separator(levelChar)).append(NEW_LINE);

        sb.append("AC").append("\t").append("Label").append("\t").append("Created").append("\t").append("Created by").append("Updated").append("\t").append("Updated by").append("\t").append("Owner");

        for (Field field : sanityResult.getInsaneObject().iterator().next().getField()) {
            sb.append("\t").append(field.getName());
        }
        sb.append(NEW_LINE);

        sb.append("--").append("\t").append("-----").append("\t").append("----").append("\t").append("----");
        for (Field field : sanityResult.getInsaneObject().iterator().next().getField()) {
            StringBuilder underline = new StringBuilder();
            for (int i=0; i<field.getName().length(); i++) {
                underline.append('-');
            }
            sb.append("\t").append(underline.toString());
        }
        sb.append(NEW_LINE);

        for (InsaneObject insaneObject : sanityResult.getInsaneObject()) {
            sb.append(insaneObject.getAc());

            sb.append("\t");

            if (insaneObject.getShortlabel() != null) {
                sb.append(insaneObject.getShortlabel());
            } else {
                sb.append("-");
            }

            sb.append("\t");
            sb.append(SimpleDateFormat.getInstance().format(insaneObject.getCreated().toGregorianCalendar().getTime()));
            sb.append("\t");
            sb.append(insaneObject.getCreator());

            sb.append("\t");
            sb.append(SimpleDateFormat.getInstance().format(insaneObject.getUpdated().toGregorianCalendar().getTime()));
            sb.append("\t");
            sb.append(insaneObject.getUpdator());

            sb.append("\t");
            sb.append(insaneObject.getOwner());

            for (Field field : insaneObject.getField()) {
                sb.append("\t");
                if (field.getValue() != null) {
                    sb.append(field.getValue());
                } else {
                    sb.append("-");
                }
            }

            sb.append(NEW_LINE);
        }

        return sb;
    }

    protected StringBuilder separator(char charSeparator) {
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<80; i++) {
           sb.append(charSeparator);
        }

        return sb;
    }
}