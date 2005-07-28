/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

import uk.ac.ebi.intact.util.sanityChecker.model.*;
import uk.ac.ebi.intact.util.MailSender;
import uk.ac.ebi.intact.util.PropertyLoader;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.IntactObject;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class MessageSender {




    public static final String TIME;

    /**
     * Mapping user -> mail adress Map( lowercase(username), email )
     */
    private static Map usersEmails = new HashMap();


    private static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Contains individual errors of curators as Map( user, Map( topic, Collection( message ) ) )
     */
    private Map allUsersReport = new HashMap();

    /**
     * Contains all error for admin as Map( Topic, Collection(Message) )
     */
    private Map adminReport = new HashMap();


    /**
     * List of user name that can't be mapped to a mail adress
     */
    private static Set unknownUsers = new HashSet();

    /**
     * List of admin mail adress
     */
    private static Collection adminsEmails = new HashSet();

    /**
     * Configuration file from which we get the lists of curators and admins.
     */
    public static final String SANITY_CHECK_CONFIG_FILE = "/config/sanityCheck.properties";

    /**
     * Prefix of the curator key from the properties file.
     */
    public static final String CURATOR = "curator.";

    /**
     * Prefix of the admin key from the properties file.
     */
    public static final String ADMIN = "admin.";


    static {
        Properties props = PropertyLoader.load( SANITY_CHECK_CONFIG_FILE );
        if ( props != null ) {
            int index;
            for ( Iterator iterator = props.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();


                index = key.indexOf( CURATOR );
                if ( index != -1 ) {
                    String userstamp = key.substring( index + CURATOR.length() );
                    String curatorMail = (String) props.get( key );
                    usersEmails.put( userstamp, curatorMail );
                } else {
                    // is it an admin then ?
                    index = key.indexOf( "admin." );
                    if ( index != -1 ) {
                        // store it
                        String adminMail = (String) props.get( key );
                        adminsEmails.add( adminMail );
                    }
                }
            } // keys
        } else {

            System.err.println( "Unable to open the properties file: " + SANITY_CHECK_CONFIG_FILE );
        }

        // format the current time
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd@HH.mm" );
        TIME = formatter.format( date );
    }

    /**
     * Helper method to obtain userstamp info from a given record, and then if it has any to append the details to a
     * result buffer.
     *
     * @param topic the type of error we have dicovered for the given AnnotatedObject.
     * @param intactBean   The Intact object that user info is required for.
     *
     * @throws java.sql.SQLException thrown if there were DB problems
     */
    public void addMessage( ReportTopic topic, IntactBean intactBean ) throws SQLException {

        String user = intactBean.getUserstamp();
        Timestamp date = intactBean.getTimestamp();


        String userMessageReport="";
        String adminMessageReport="";
        // Build users report

        if(intactBean instanceof InteractorBean){
            InteractorBean interactorBean = (InteractorBean) intactBean;


            userMessageReport = "AC: " + interactorBean.getAc() +
                    "\t Shortlabel: " + interactorBean.getShortlabel() +
                    "\t When: " + date;
            adminMessageReport = "AC: " + interactorBean.getAc() +
                    "\t Shortlabel: " + interactorBean.getShortlabel() +
                    "\t User: " + user +
                    "\t When: " + date;

        }else if(intactBean instanceof ExperimentBean){
            ExperimentBean experimentBean = (ExperimentBean) intactBean;


            userMessageReport = "AC: " + experimentBean.getAc() +
                    "\t Shortlabel: " + experimentBean.getShortlabel() +
                    "\t When: " + date;
            adminMessageReport = "AC: " + experimentBean.getAc() +
                    "\t Shortlabel: " + experimentBean.getShortlabel() +
                    "\t User: " + user +
                    "\t When: " + date;

        }else if (intactBean instanceof BioSourceBean){
            BioSourceBean bioSourceBean = (BioSourceBean) intactBean;

            userMessageReport = "AC: " + bioSourceBean.getAc() +
                    "\t Shortlabel: " + bioSourceBean.getShortlabel() +
                    "\t When: " + date;
            adminMessageReport = "AC: " + bioSourceBean.getAc() +
                    "\t Shortlabel: " + bioSourceBean.getShortlabel() +
                    "\t User: " + user +
                    "\t When: " + date;

        }


        if ( user != null && !( user.trim().length() == 0 ) ) {

            // add new message to the user
            Map userReport = (Map) allUsersReport.get( user );
            if ( userReport == null ) {
                userReport = new HashMap();
            }

            Collection topicMessages = (Collection) userReport.get( topic );
            if ( topicMessages == null ) {
                topicMessages = new ArrayList();

                // add the messages to the topic
                userReport.put( topic, topicMessages );
            }

            // add the message to the topic
            topicMessages.add( userMessageReport );

            // add the user's messages
            allUsersReport.put( user, userReport );
        } else {

            System.err.println( "No user found for object: " + userMessageReport );
        }


        /*  // build admin admin report
        String adminMessageReport = "AC: " + obj.getAc() +
        "\t Shortlabel: " + obj.getShortLabel() +
        "\t User: " + user +
        "\t When: " + date;
        */
        Collection topicMessages = (Collection) adminReport.get( topic );
        if ( topicMessages == null ) {
            topicMessages = new ArrayList();

            // add the messages to the topic
            adminReport.put( topic, topicMessages );
        }

        // add the message to the topic
        topicMessages.add( adminMessageReport );
    }



    /**
     * Helper method to obtain userstamp info from a given record, and then if it has any to append the details to a
     * result buffer.
     *
     * @param topic the type of error we have dicovered for the given AnnotatedObject.
     * @param intactBeans   The Intact object that user info is required for.
     *
     * @throws java.sql.SQLException thrown if there were DB problems
     */
    public void addMessage( ReportTopic topic, List intactBeans ) throws SQLException {

        String userMessageReport="Protein List : \n";
        String adminMessageReport="Protein List : \n";

        List users = new ArrayList();

        for (int i = 0; i < intactBeans.size(); i++) {
            IntactBean intactBean =  (IntactBean) intactBeans.get(i);


            // Build users report

            if(intactBean instanceof InteractorBean){
                InteractorBean interactorBean = (InteractorBean) intactBean;

                String user = intactBean.getUserstamp();
                Timestamp date = intactBean.getTimestamp();

                if(!users.contains(user)){
                    users.add(user);
                }

                userMessageReport = userMessageReport +
                        "\t AC: " + interactorBean.getAc() +
                        "\t Shortlabel: " + interactorBean.getShortlabel() +
                        "\t When: " + date + "\n";
                adminMessageReport =  adminMessageReport +
                        "\t AC: " + interactorBean.getAc() +
                        "\t Shortlabel: " + interactorBean.getShortlabel() +
                        "\t User: " + user +
                        "\t When: " + date + "\n";

            }

        }



        for (int i = 0; i < users.size(); i++) {
            String user = (String) users.get(i);

            if ( user != null && !( user.trim().length() == 0 ) ) {

                // add new message to the user
                Map userReport = (Map) allUsersReport.get( user );
                if ( userReport == null ) {
                    userReport = new HashMap();
                }

                Collection topicMessages = (Collection) userReport.get( topic );
                if ( topicMessages == null ) {
                    topicMessages = new ArrayList();

                    // add the messages to the topic
                    userReport.put( topic, topicMessages );
                }

                // add the message to the topic
                topicMessages.add( userMessageReport );

                // add the user's messages
                allUsersReport.put( user, userReport );
            } else {

                System.err.println( "No user found for object: " + userMessageReport );
            }
        }



        /*  // build admin admin report
        String adminMessageReport = "AC: " + obj.getAc() +
        "\t Shortlabel: " + obj.getShortLabel() +
        "\t User: " + user +
        "\t When: " + date;
        */
        Collection topicMessages = (Collection) adminReport.get( topic );
        if ( topicMessages == null ) {
            topicMessages = new ArrayList();

            // add the messages to the topic
            adminReport.put( topic, topicMessages );
        }

        // add the message to the topic
        topicMessages.add( adminMessageReport );
    }

    /**
     *   This method create the adminReportMessage and the userReportMessage for a not valide URI.
     *
     * @param toAnnotBeans ResultSet containing the ac of object annotated by the annotation given in parameter
     * @param annotationBean
     */
    public void annotationMessage (List toAnnotBeans, AnnotationBean annotationBean) throws IntactException, SQLException {

        String annotatedBeanType = null;
        String user = null;
        Timestamp date = null;
        String userMessageReport = null;
        String adminMessageReport = null;
        IntactHelper helper = new IntactHelper();
        SanityCheckerHelper sch = new SanityCheckerHelper(helper);

        sch.addMapping(ExperimentBean.class,"SELECT ac, userstamp, timestamp, shortlabel " +
                                            "FROM ia_experiment " +
                                            "WHERE ac = ?");
        sch.addMapping(BioSourceBean.class,"SELECT ac, userstamp, timestamp, shortlabel " +
                                           "FROM ia_biosource " +
                                           "WHERE ac = ?");
        sch.addMapping(InteractorBean.class,"SELECT ac, userstamp, timestamp, shortlabel "+
                                            "FROM ia_interactor " +
                                            "where ac = ?");
        sch.addMapping(FeatureBean.class,"SELECT ac, userstamp, timestamp, shortlabel " +
                                         "FROM ia_feature " +
                                         "WHERE ac = ?");
        sch.addMapping(ControlledvocabBean.class,"SELECT ac, userstamp, timestamp, shortlabel "+
                                                 "FROM ia_controlledvocab " +
                                                 "WHERE ac = ?");

        //for (int i = 0; i < toAnnotBeans.size(); i++) {
        if(!toAnnotBeans.isEmpty()){
            IntactBean intactBean =  (IntactBean) toAnnotBeans.get(0);
            List annotatedBeans = new ArrayList();
            if(intactBean instanceof Exp2AnnotBean){
                annotatedBeans = sch.getBeans(ExperimentBean.class,((Exp2AnnotBean)intactBean).getExperiment_ac());
                annotatedBeanType = "Experiment";
            } else if (intactBean instanceof Bs2AnnotBean){
                annotatedBeans = sch.getBeans(BioSourceBean.class,((Bs2AnnotBean)intactBean).getBiosource_ac());
                annotatedBeanType = "Biosource" ;
            } else if (intactBean instanceof Int2AnnotBean){
                annotatedBeans = sch.getBeans(InteractorBean.class,((Int2AnnotBean)intactBean).getInteractor_ac());
                annotatedBeanType = "Interactor";
            } else if (intactBean instanceof Feature2AnnotBean){
                annotatedBeans = sch.getBeans(FeatureBean.class,((Feature2AnnotBean)intactBean).getFeature_ac());
                annotatedBeanType = "Feature";
            } else if (intactBean instanceof CvObject2AnnotBean){
                annotatedBeans = sch.getBeans(ControlledvocabBean.class,((CvObject2AnnotBean)intactBean).getCvobject_ac());
                annotatedBeanType = "Controlledvocab";
            }

            //for (int j = 0; j < annotatedBeans.size(); j++) {
            if(!annotatedBeans.isEmpty()){
                AnnotatedBean annotatedBean =  (AnnotatedBean) annotatedBeans.get(0);

                user = annotatedBean.getUserstamp();
                date = annotatedBean.getTimestamp();

                userMessageReport =     "Annotation Description (URI): " + annotationBean.getDescription() +
                        "\t Annotation AC: " + annotationBean.getAc()+
                        "\t This uri is used to annotate the " + annotatedBeanType + " with ac = " + annotatedBean.getAc() +
                        "\t Date: " + date +
                        "\n";

                adminMessageReport =     "Annotation Description (URI): " + annotationBean.getDescription()+
                        "\t Annotation AC: " + annotationBean.getAc()+
                        "\t This uri is used to annotate the " + annotatedBeanType + " with ac = " + annotatedBean.getAc() +
                        "\t User: " + user +
                        "\t Date: " + date +
                        "\n";
                if ( user != null && !( user.trim().length() == 0 ) ) {

                    // add new message to the user
                    Map userReport = (Map) allUsersReport.get( user );
                    if ( userReport == null ) {
                        userReport = new HashMap();
                    }

                    Collection topicMessages = (Collection) userReport.get( ReportTopic.URL_NOT_VALID );
                    if ( topicMessages == null ) {
                        topicMessages = new ArrayList();

                        // add the messages to the topic
                        userReport.put( ReportTopic.URL_NOT_VALID, topicMessages );
                    }

                    // add the message to the topic
                    topicMessages.add( userMessageReport );

                    // add the user's messages
                    allUsersReport.put( user, userReport );
                } else {
                    System.err.println( "No user found for object: " + userMessageReport );
                }


                Collection topicMessages = (Collection) adminReport.get( ReportTopic.URL_NOT_VALID );
                if ( topicMessages == null ) {
                    topicMessages = new ArrayList();

                    // add the messages to the topic
                    adminReport.put( ReportTopic.URL_NOT_VALID, topicMessages );
                }

                // add the message to the topic
                topicMessages.add( adminMessageReport );
            }
        }


    }


    /**
     * post emails to the curators (their individual errors) and to the administrator (global list of errors)
     *
     * @throws javax.mail.MessagingException
     */
    public void postEmails() throws MessagingException, IntactException {

        MailSender mailer = new MailSender();

        // send individual mail to curators
        for ( Iterator iterator = allUsersReport.keySet().iterator(); iterator.hasNext(); ) {
            String user = (String) iterator.next();

            Map reportMessages = (Map) allUsersReport.get( user );
            StringBuffer fullReport = new StringBuffer( 256 );
            int errorCount = 0;

            for ( Iterator iterator1 = reportMessages.keySet().iterator(); iterator1.hasNext(); ) {
                ReportTopic topic = (ReportTopic) iterator1.next();

                fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
                Collection messages = (Collection) reportMessages.get( topic );

                // write individual messages of that topic.
                for ( Iterator iterator2 = messages.iterator(); iterator2.hasNext(); ) {
                    String message = (String) iterator2.next();

                    fullReport.append( message ).append( NEW_LINE );
                    errorCount++;
                } // messages in the topic

                fullReport.append( NEW_LINE );
            } // topics

            // don't send mail to curator if no errors
            if ( errorCount > 0 ) {

                System.out.println( "Send individual report to " + user + "( " + user + ")" );
                String email = (String) usersEmails.get( user.toLowerCase() );

                if ( email != null ) {
                    String[] recipients = new String[ 1 ];
                    recipients[ 0 ] = email;

                    // send mail
                    mailer.postMail( recipients,
                                     "SANITY CHECK - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
                                     fullReport.toString(),
                                     "cleroy@ebi.ac.uk" );
                    System.out.println("FULL REPORT for User : " + fullReport.toString());
                } else {

                    // keep track of unknown users
                    unknownUsers.add( user.toLowerCase() );

                    System.err.println( "Could not find that user, here is the content of his report:" );
                    System.err.println( fullReport.toString() );

                }
            }

        } // users

        // send summary of all individual mail to admin
        StringBuffer fullReport = new StringBuffer( 256 );
        IntactHelper helper=null;
        try {
            helper=getIntactHelper();
            fullReport.append( "Instance name: " + helper.getDbName() );
            fullReport.append( NEW_LINE ).append( NEW_LINE );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        // generate error message is some users have not been found
        if ( !unknownUsers.isEmpty() ) {
            if ( unknownUsers.size() == 1 ) {

                fullReport.append( "Could not find an email adress for user: " + unknownUsers.iterator().next() );

            } else {
                // more than one, then generate a list
                fullReport.append( "Could not find email adress for the following list of users: " ).append( NEW_LINE );
                for ( Iterator iterator = unknownUsers.iterator(); iterator.hasNext(); ) {
                    String user = (String) iterator.next();
                    fullReport.append( user ).append( NEW_LINE );
                }
            }

            fullReport.append( NEW_LINE ).append( NEW_LINE );
            System.out.println("FULL REPORT for User : " + fullReport.toString());
        }

        // generate full report
        int errorCount = 0;
        if ( !adminReport.isEmpty() ) {

            for ( Iterator iterator = adminReport.keySet().iterator(); iterator.hasNext(); ) {
                ReportTopic topic = (ReportTopic) iterator.next();

                Collection messages = (Collection) adminReport.get( topic );
                fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
                for ( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
                    String message = (String) iterator1.next();
                    fullReport.append( message ).append( NEW_LINE );
                    errorCount++;
                } // messages

                fullReport.append( NEW_LINE );
            } // topics

        } else {

            fullReport.append( "No curation error to report." );

        }

        // Send mail to the administrator
        String[] recipients = new String[ adminsEmails.size() ];
        int i = 0;
        for ( Iterator iterator = adminsEmails.iterator(); iterator.hasNext(); ) {
            String email = (String) iterator.next();
            recipients[ i++ ] = email;
        }

        // always send mail to admin, even if no errors
        mailer.postMail( recipients,
                         "SANITY CHECK (ADMIN) - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
                         fullReport.toString(),
                         "cleroy@ebi.ac.uk" );
        System.out.println("FULL REPORT for Admin : " + fullReport.toString());

    }

    private String getFullReportOutput() {

        StringBuffer fullReport = new StringBuffer( 256 );

        for ( Iterator iterator = adminReport.keySet().iterator(); iterator.hasNext(); ) {
            ReportTopic topic = (ReportTopic) iterator.next();

            Collection messages = (Collection) adminReport.get( topic );
            fullReport.append( topic.getUnderlinedTitle() ).append( NEW_LINE );
            for ( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
                String message = (String) iterator1.next();
                fullReport.append( message ).append( NEW_LINE );
            } // messages

            fullReport.append( NEW_LINE );
        } // topics

        return fullReport.toString();
    }

    public IntactHelper getIntactHelper() throws IntactException {
        IntactHelper helper = new IntactHelper();
        // Install termination hook, that allows to close cleanly the db connexion if the user hits CTRL+C.
        Runtime.getRuntime().addShutdownHook( new DatabaseConnexionShutdownHook( helper ) );
        return helper;
    }

    /**
     * Service termination hook (gets called when the JVM terminates from a signal). eg.
     * <pre>
     * IntactHelper helper = new IntactHelper();
     * DatabaseConnexionShutdownHook dcsh = new DatabaseConnexionShutdownHook( helper );
     * Runtime.getRuntime().addShutdownHook( sh );
     * </pre>
     */
    private static class DatabaseConnexionShutdownHook extends Thread {

        private IntactHelper helper;

        public DatabaseConnexionShutdownHook( IntactHelper helper ) {
            super();
            this.helper = helper;
        }

        public void run() {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                    System.out.println( "Connexion to the database closed." );
                } catch ( IntactException e ) {
                    System.err.println( "Could not close the connexion to the database." );
                    e.printStackTrace();
                }
            }
        }
    }

}
