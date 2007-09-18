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
import uk.ac.ebi.intact.sanity.commons.rules.report.XmlReportWriter;
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
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    public SanityReportMailer(SanityCheckConfig sanityConfig) {
        this.sanityConfig = sanityConfig;
    }

    public void mailReports(SanityReport report) throws IOException, MessagingException {
        mailReports(report, sanityConfig.getEmailSubjectPrefix());
    }

    public void mailReports(SanityReport report, String subjectPrefix) throws IOException, MessagingException {
        Set<String> allCurators = new HashSet<String>();
        allCurators.addAll(SanityReportUtils.getInsaneCreatorNames(report));
        allCurators.addAll(SanityReportUtils.getInsaneUpdatorNames(report));

        Map<String,SanityReport> insaneCreatorReports = SanityReportUtils.createPersonalizedReportsByCreator(report);
        Map<String,File> insaneCreatorFiles = reportToTempFile(insaneCreatorReports, "created");
        Map<String,File> insaneCreatorFilesXml = reportToTempFileXml(insaneCreatorReports, "created");

        Map<String,SanityReport> insaneUpdatorReports = SanityReportUtils.createPersonalizedReportsByUpdator(report);
        Map<String,File> insaneUpdatorFiles = reportToTempFile(insaneUpdatorReports, "updated");
        Map<String,File> insaneUpdatorFilesXml = reportToTempFileXml(insaneUpdatorReports, "updated");

        String from = "intact-check@ebi.ac.uk";

        if (subjectPrefix == null) subjectPrefix = "";

        if (sanityConfig.isEnableUserMails()) {
            for (String curatorName : allCurators) {
                if (log.isDebugEnabled()) log.debug("Sending sanity check mail to curator: "+curatorName);

                Curator curator = sanityConfig.getCurator(curatorName);

                if (curator == null){
                    log.warn("Curator not found: "+curatorName+". Mail won't be sent.");
                    continue;
                }

                SanityReport creatorReport = insaneCreatorReports.get(curatorName);
                SanityReport updatorReport = insaneUpdatorReports.get(curatorName);

                Collection<File> curatorFiles = new ArrayList<File>();

                File creatorReportFile = insaneCreatorFiles.get(curatorName);
                if (creatorReportFile != null) curatorFiles.add(creatorReportFile);

                File creatorReportFileXml = insaneCreatorFilesXml.get(curatorName);
                if (creatorReportFileXml != null) curatorFiles.add(creatorReportFileXml);

                File updatorReportFile = insaneUpdatorFiles.get(curatorName);
                if (updatorReportFile != null) curatorFiles.add(updatorReportFile);

                File updatorReportFileXml = insaneUpdatorFilesXml.get(curatorName);
                if (updatorReportFileXml != null) curatorFiles.add(updatorReportFileXml);

                String message = null;
                if (!SanityReportUtils.getAllInsaneObject(creatorReport).isEmpty()) {
                    message = reportToHtml(creatorReport);
                } else {
                    message = reportToHtml(updatorReport);
                }

                String subject = subjectPrefix+"Sanity Check ("+curatorName+") - "+SanityReportUtils.getAllInsaneObject(creatorReport).size()+" errors";
                String recipient = curator.getEmail();

                MailSender mailSender = new MailSender(sanityConfig.getMailerProperties());
                mailSender.postMail(new String[] {recipient}, subject, message, from, curatorFiles.toArray(new File[curatorFiles.size()]));
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Mails to curators are disabled. These is the insane curator list: Created("+insaneCreatorReports.keySet()+"), Updated("+insaneUpdatorReports.keySet()+")");
            }
        }

        if (sanityConfig.isEnableAdminMails()) {
            String subject = subjectPrefix+"Sanity Check (ADMIN) - "+SanityReportUtils.getAllInsaneObject(report).size()+" errors";
            String globalMessage = reportToHtml(report);

            Collection<String> adminEMails = getAdminEmails();

            if (log.isDebugEnabled()) log.debug("Sending sanity check mail to admins: "+adminEMails);

            String[] recipients = adminEMails.toArray(new String[adminEMails.size()]);

            Collection<File> fileAttachments = new ArrayList<File>(insaneCreatorFiles.values());
            fileAttachments.add(reportToTempFile("admin", report, "all"));
            fileAttachments.add(reportToTempFileXml("admin", report, "all"));

            File[] attachments = fileAttachments.toArray(new File[insaneCreatorFiles.values().size()]);

            MailSender mailSender = new MailSender(sanityConfig.getMailerProperties());
            mailSender.postMail(recipients, subject, globalMessage, from, attachments);
        }
    }

    protected String reportToHtml(SanityReport report) throws IOException {
        Writer writer = new StringWriter();
        ReportWriter reportWriter = new HtmlReportWriter(writer);
        reportWriter.write(report);
        return writer.toString();
    }

    protected File reportToTempFile(String name, SanityReport report, String suffix) throws IOException {
        File tmpDir = new File(TMP_DIR);

        File tempFile = new File(tmpDir, name+"-"+suffix+".html");
        tempFile.deleteOnExit();

        Writer writer = new FileWriter(tempFile);
        ReportWriter reportWriter = new HtmlReportWriter(writer);
        reportWriter.write(report);
        writer.flush();

        return tempFile;
    }

     protected File reportToTempFileXml(String name, SanityReport report, String suffix) throws IOException {
        File tmpDir = new File(TMP_DIR);

        File tempFile = new File(tmpDir, name+"-"+suffix+".xml");
        tempFile.deleteOnExit();

        Writer writer = new FileWriter(tempFile);
        ReportWriter reportWriter = new XmlReportWriter(writer);
        reportWriter.write(report);
        writer.flush();

        return tempFile;
    }

    protected Map<String,File> reportToTempFile(Map<String,SanityReport> insaneCuratorReports, String suffix) throws IOException {
        Map<String,File> insaneCuratorFiles = new HashMap<String,File>(insaneCuratorReports.size());

        for (Map.Entry<String,SanityReport> entry : insaneCuratorReports.entrySet()) {
            insaneCuratorFiles.put(entry.getKey(), reportToTempFile(entry.getKey(), entry.getValue(), suffix));
        }

        return insaneCuratorFiles;
    }

    protected Map<String,File> reportToTempFileXml(Map<String,SanityReport> insaneCuratorReports, String prefix) throws IOException {
        Map<String,File> insaneCuratorFilesXml = new HashMap<String,File>(insaneCuratorReports.size());

        for (Map.Entry<String,SanityReport> entry : insaneCuratorReports.entrySet()) {
            insaneCuratorFilesXml.put(entry.getKey(), reportToTempFileXml(entry.getKey(), entry.getValue(), prefix));
        }

        return insaneCuratorFilesXml;
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