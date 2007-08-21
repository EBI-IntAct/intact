/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Allow to send a basic mail message to a set of recipients.
 * That class requires:
 *
 *    - Java Mail (http://java.sun.com/products/javamail/)
 *    - Java Activation Framework (http://java.sun.com/products/javabeans/glasgow/jaf.html)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class MailSender {

    public static final String MAIL_FILE_NAME= "mail.html";
    private static final String NEW_LINE = "<BR>";

    private Properties properties;

    public static final Properties EBI_SETTINGS;
    public static final Properties GMAIL_SETTINGS;

    static {
        EBI_SETTINGS = new Properties();
        EBI_SETTINGS.put( "mail.smtp.host", "mailserv.ebi.ac.uk" );
    }

    static {
        GMAIL_SETTINGS = new Properties();
        GMAIL_SETTINGS.put("mail.smtp.host", "smtp.gmail.com");
        GMAIL_SETTINGS.put("mail.smtp.auth", "true");
        GMAIL_SETTINGS.put("mail.debug", "false");
        GMAIL_SETTINGS.put("mail.smtp.port", "465");
        GMAIL_SETTINGS.put("mail.smtp.socketFactory.port", "465");
        GMAIL_SETTINGS.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        GMAIL_SETTINGS.put("mail.smtp.socketFactory.fallback", "false");
    }

    @Deprecated
    public MailSender() {
        this(EBI_SETTINGS);
    }

    public MailSender(String smptHost) {
        this(EBI_SETTINGS);
        EBI_SETTINGS.put("mail.smtp.host", smptHost);
    }

    public MailSender(Properties properties) {
        this.properties = properties;
    }

    /**
     * Send a mail to a set of recipients.
     *
     * @param recipients list of mail adresses
     * @param subject    subject of the mail
     * @param message    content of the mail
     * @param from       who wrote that mail
     *
     * @throws MessagingException if the message can't be sent.
     */
    public void postMail( String recipients[ ], String subject, String message, String from ) throws MessagingException {
        Session session = Session.getInstance(properties);
        postMail(session, recipients, subject, message, from, null);
    }

    /**
     * Send a mail to a set of recipients.
     *
     * @param recipients list of mail adresses
     * @param subject    subject of the mail
     * @param message    content of the mail
     * @param from       who wrote that mail
     *
     * @throws MessagingException if the message can't be sent.
     */
    public void postMailSSL( String recipients[ ], String subject, String message, String from, final PasswordAuthentication auth ) throws MessagingException {
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return auth;
                    }
                });
        postMail(session, recipients, subject, message, from, null);
    }

    /**
     * Send a mail to a set of recipients.
     *
     * @param recipients list of mail adresses
     * @param subject    subject of the mail
     * @param message    content of the mail
     * @param from       who wrote that mail
     *
     * @throws MessagingException if the message can't be sent.
     */
    public void postMailSSL( String recipients[ ], String subject, String message, String from, final PasswordAuthentication auth, File ... fileAttachment ) throws MessagingException {
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return auth;
                    }
                });
        postMail(session, recipients, subject, message, from, fileAttachment);
    }

    /**
     * Send a mail to a set of recipients.
     *
     * @param session the session to use
     * @param recipients list of mail adresses
     * @param subject    subject of the mail
     * @param message    content of the mail
     * @param from       who wrote that mail
     *
     * @throws MessagingException if the message can't be sent.
     */
    public void postMail( Session session, String recipients[ ], String subject, String message, String from, File ... fileAttachments ) throws MessagingException {
        // create a message
        Message msg = new MimeMessage( session );

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress( from );
        msg.setFrom( addressFrom );

        InternetAddress[] addressTo = new InternetAddress[ recipients.length ];
        for( int i = 0; i < recipients.length; i++ ) {
            addressTo[ i ] = new InternetAddress( recipients[ i ] );
        }
        msg.setRecipients( Message.RecipientType.TO, addressTo );

        // Optional : You can also set your custom headers in the Email if you Want
        //  msg.addHeader( "MyHeaderName", "myHeaderValue" );

        // Setting the Subject and Content Type
        msg.setSubject( subject );
        //msg.setContent( message, "text/html" );

        // create the message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        //fill message
        messageBodyPart.setContent( message, "text/html" );

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        if (fileAttachments != null) {
            for (File fileAttachment : fileAttachments) {
                messageBodyPart = new MimeBodyPart();
                DataSource source =
                        new FileDataSource(fileAttachment);
                messageBodyPart.setDataHandler(
                        new DataHandler(source));
                messageBodyPart.setFileName(fileAttachment.getName());
                multipart.addBodyPart(messageBodyPart);
            }
        }

        // Put parts in message
        msg.setContent(multipart);

        try {
           Transport.send( msg );
        } catch (Exception e_send) {
            //save message in file on server
            System.out.println("Error sending mail" + e_send);
            e_send.printStackTrace();
            FileOutputStream out;
            PrintStream p;
            System.out.println("Saving mail in file " + MAIL_FILE_NAME);
            try
            {
               out = new FileOutputStream(MAIL_FILE_NAME);
               p = new PrintStream( out );
               p.print("Recipients: " + NEW_LINE);
                for( int i = 0; i < recipients.length; i++ ) {
                   p.print(recipients[i]);
                }
               p.print(NEW_LINE + NEW_LINE);
               p.print("Subject: " + NEW_LINE + subject.toString() + NEW_LINE + NEW_LINE);
               p.print("Message: " + NEW_LINE + message.toString()  + NEW_LINE + NEW_LINE);
               p.close();
            }
            catch (Exception e_writing_file)
            {
               System.err.println ("Error writing to file " + MAIL_FILE_NAME + " " + e_writing_file);
            }
        }
    }


    /**
     * D E M O
     *
     * @param args
     * @throws MessagingException
     */
    public static void main( String[] args ) throws MessagingException {
        MailSender mailer = new MailSender(GMAIL_SETTINGS);
        String[] recipients = {"baranda@ebi.ac.uk"};
        PasswordAuthentication auth = new PasswordAuthentication("brunoaranda", "aradel03");
        mailer.postMailSSL( recipients, "test from java", "<br>content</br>", "baranda@ebi.ac.uk", auth, new File("F:\\projectes\\intact-current\\sanity\\intact-sanity-commons\\src\\main\\resources\\META-INF\\xsl\\test.html"), new File("F:\\projectes\\intact-current\\commons\\intact-commons\\src\\main\\java\\uk\\ac\\ebi\\intact\\util\\MailSender.java") );
    }
}
