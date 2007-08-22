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
package uk.ac.ebi.intact.sanity.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.sanity.check.config.Curator;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.rules.report.HtmlReportWriter;
import uk.ac.ebi.intact.sanity.commons.rules.report.ReportWriter;
import uk.ac.ebi.intact.sanity.commons.rules.report.SanityReportUtils;
import uk.ac.ebi.intact.util.MailSender;

import javax.mail.MessagingException;
import java.io.*;
import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityReportMailer {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(SanityReportMailer.class);

    private SanityCheckConfig sanityConfig;

    public SanityReportMailer(SanityCheckConfig sanityConfig) {
        this.sanityConfig = sanityConfig;
    }

    public void mailReports(SanityReport report) throws IOException, MessagingException {
        Map<String,SanityReport> insaneCuratorReports = SanityReportUtils.createPersonalizedReports(report);
        Map<String,File> insaneCuratorFiles = reportToTempFile(insaneCuratorReports);

        String from = "intact-check@ebi.ac.uk";

        if (sanityConfig.isEnableUserMails()) {
            for (String curatorName : insaneCuratorFiles.keySet()) {
                if (log.isDebugEnabled()) log.debug("Sending sanity check mail to curator: "+curatorName);

                Curator curator = sanityConfig.getCurator(curatorName);
                SanityReport curatorReport = insaneCuratorReports.get(curatorName);
                File curatorReportFile = insaneCuratorFiles.get(curatorName);

                String message = reportToHtml(curatorReport);

                String subject = "Sanity Check ("+curatorName+") - "+SanityReportUtils.getAllInsaneObject(curatorReport).size()+" errors";
                String recipient = curator.getEmail();

                MailSender mailSender = new MailSender(MailSender.EBI_SETTINGS);
                mailSender.postMail(new String[] {recipient}, subject, message, from, curatorReportFile);
            }
        }

        if (sanityConfig.isEnableAdminMails()) {
            String subject = "Sanity Check (ADMIN) - "+SanityReportUtils.getAllInsaneObject(report).size()+" errors";
            String globalMessage = reportToHtml(report);

            Collection<String> adminEMails = getAdminEmails();

            if (log.isDebugEnabled()) log.debug("Sending sanity check mail to admins: "+adminEMails);

            String[] recipients = adminEMails.toArray(new String[adminEMails.size()]);

            Collection<File> fileAttachments = new ArrayList<File>(insaneCuratorFiles.values());
            fileAttachments.add(reportToTempFile("admin", report));
            File[] attachments = fileAttachments.toArray(new File[insaneCuratorFiles.values().size()]);

            MailSender mailSender = new MailSender(MailSender.EBI_SETTINGS);
            mailSender.postMail(recipients, subject, globalMessage, from, attachments);
        }
    }

    protected String reportToHtml(SanityReport report) throws IOException {
        Writer writer = new StringWriter();
        ReportWriter reportWriter = new HtmlReportWriter(writer);
        reportWriter.write(report);
        return writer.toString();
    }

    protected File reportToTempFile(String name, SanityReport report) throws IOException {
        File tempFile = File.createTempFile(name+"-", ".html");
        tempFile.deleteOnExit();

        Writer writer = new FileWriter(tempFile);
        ReportWriter reportWriter = new HtmlReportWriter(writer);
        reportWriter.write(report);
        writer.flush();

        return tempFile;
    }

    protected Map<String,File> reportToTempFile(Map<String,SanityReport> insaneCuratorReports) throws IOException {
        Map<String,File> insaneCuratorFiles = new HashMap<String,File>(insaneCuratorReports.size());

        for (Map.Entry<String,SanityReport> entry : insaneCuratorReports.entrySet()) {
            insaneCuratorFiles.put(entry.getKey(), reportToTempFile(entry.getKey(), entry.getValue()));
        }

        return insaneCuratorFiles;
    }

    protected Set<String> getAdminEmails() {
        Set<String> mails = new HashSet<String>();

        for (Curator curator : sanityConfig.getAllCurators()) {
            if (curator.isAdmin()) {
                mails.add(curator.getEmail());
            }
        }

        return mails;
    }

    
}