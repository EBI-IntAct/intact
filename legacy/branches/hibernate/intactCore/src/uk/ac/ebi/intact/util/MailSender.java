/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

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

    private static String SMTP_HOST = null;
    public static final String SMTP_CONFIG_FILE = "/config/smtp.properties";
    public static final String MAIL_FILE_NAME= "mail.html";
    private static final String NEW_LINE = "<BR>";

    static {
        Properties props = PropertyLoader.load( SMTP_CONFIG_FILE );
        if (props != null) {
            SMTP_HOST = props.getProperty ("mail.smtp.host");
        } else {
            System.err.println ("Unable to open the properties file: " + SMTP_CONFIG_FILE);
        }
    }

    private Session session;

    public MailSender() {
        boolean debug = true; //false; !!! changed temp

        //Set the host smtp address
        Properties props = new Properties();
        if( null != SMTP_HOST ) {
            props.put( "mail.smtp.host", SMTP_HOST );
        }
        //props.put("mail.debug", "true"); //!!! temporarily
        // create some properties and get the default Session
         session = Session.getDefaultInstance( props, null );
       // session.setDebug( debug );
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
        msg.setContent( message, "text/html" );
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

        MailSender mailer = new MailSender();
        String[] recipients = {"skerrien@ebi.ac.uk"};
        mailer.postMail( recipients, "test from java", "content", "skerrien@ebi.ac.uk" );
    }
}
