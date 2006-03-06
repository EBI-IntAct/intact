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
import uk.ac.ebi.intact.model.*;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class MessageSender {

    private EditorUrlBuilder editorUrlBuilder = new EditorUrlBuilder();


    public static final String TIME;

    /**
     * Mapping user -> mail adress Map( lowercase(username), email )
     */
    private static Map usersEmails = new HashMap();

    private static final String NEW_LINE = "<BR>";//System.getProperty( "line.separator" );

    /**
     * Contains individual errors of curators as Map( user, Map( topic, Collection( message ) ) )
     */
    private Map allUsersReport = new HashMap();
    //private Map reportHeaderMaper
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
     * This addMessage is used by the checkHiddenAndObsoleteCv() method from the SanityChecked it permit to obtain a
     * message that look like that :
     *
     * This/those Cvs are annotated as hidden or obsolete but are actualy in used in ia_range as fromfuzzytype_ac
     *  ----------------------------------------------------------------------------------------------------------
     * Interaction Ac     	Interactor Ac     	Feature Ac     	Range Ac     	When     	User     	Cv Ac     	Cv Shortlabel
     * EBI-457078     	EBI-374862     	EBI-457085     	EBI-457086     	2005-12-19 16:05:51.0     	CLEROY     	EBI-769179     	cv-test
     *
     * @param topic
     * @param rangeBean
     * @param cv
     */

    public void addMessage (ReportTopic topic, RangeBean rangeBean, ControlledvocabBean cv ){
        String editorUrl;

        String user = rangeBean.getUserstamp();
        Timestamp date = rangeBean.getUpdated();


        String userMessageReport="";
        String adminMessageReport="";



        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            Range range = (Range) helper.getObjectByAc(Range.class, rangeBean.getAc());

            editorUrl = editorUrlBuilder.getEditorUrl("Interaction",rangeBean.getInteraction_ac());
            String[] rowValues = new String[8];
            rowValues[0] ="<a href="+ editorUrl + ">"+ rangeBean.getInteraction_ac() + "</a>";
            rowValues[1] = rangeBean.getInteractor_ac();
            rowValues[2] = rangeBean.getFeature_ac();
            rowValues[3] = rangeBean.getAc();
            rowValues[4] = "" + date;
            rowValues[5] =  user;
            rowValues[6] = cv.getAc();
            rowValues[7] = cv.getShortlabel();
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

        } catch (IntactException e) {
            e.printStackTrace();
        }


        addUserMessage(topic, user, userMessageReport, adminMessageReport);
        addAdminMessage(topic, adminMessageReport);

    }

    /**
     * This addMessage is used by the checkHiddenAndObsoleteCv() method from the SanityChecked it permit to obtain a
     * message that look like that :
     *
     *  This/those Cvs are annotated as hidden or obsolete but are actualy in used in a xref as Reference Qualifier
     *  -----------------------------------------------------------------------------------------------------------
     * XreferencedBean     	FeatureBean Ac     	Type     	Xref Ac     	Xref primaryId     	Database Ac     	Cv Ac     	Cv Shortlabel     	When     	User
    *  EBI-476945     	     	Experiment     	EBI-769158     	111     	EBI-705816     	EBI-769160
     * @param topic
     * @param intactBean
     * @param cv
     * @throws SQLException
     * @throws IntactException
     */
    public void addMessage( ReportTopic topic, IntactBean intactBean, ControlledvocabBean cv ) throws SQLException, IntactException {

        String editorUrl;// = editorUrlBuilder.getEditorUrl(intactBean);

        String user = intactBean.getUserstamp();
        Timestamp date = intactBean.getUpdated();


        String userMessageReport="";
        String adminMessageReport="";

        if (intactBean instanceof AliasBean){
            AliasBean aliasBean = (AliasBean) intactBean;
            String[] rowValues = new String[7];
            rowValues[0] = aliasBean.getAc();
            rowValues[1] = aliasBean.getParent_ac();
            rowValues[2] = aliasBean.getName();
            rowValues[3] = cv.getAc();
            rowValues[4] = cv.getShortlabel();
            rowValues[5] = "" + date;
            rowValues[6] = user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);
        }
        if (intactBean instanceof AnnotationBean){
            AnnotationBean annotationBean = (AnnotationBean) intactBean;
            IntactBean annotatedBean = SanityCheckerHelper.getAnnotatedBeanFromAnnotation(annotationBean.getAc());
            if (annotatedBean != null){
                editorUrl = editorUrlBuilder.getEditorUrl(annotatedBean);
                String[] rowValues = new String[6];
                rowValues[0] = "<a href="+ editorUrl + ">"+ annotatedBean.getAc()+ "</a>";
                rowValues[1] = getTypeFromIntactBean(annotatedBean);
                rowValues[2] = cv.getAc();
                rowValues[3] = cv.getShortlabel();
                rowValues[4] = "" + date;
                rowValues[5] = user;
                userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
                adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);   }
            else{
                System.out.println("annotationBean.getAc() = " + annotationBean.getAc());}
        }
        if (intactBean instanceof ExperimentBean){
            ExperimentBean experimentBean = (ExperimentBean) intactBean;
            editorUrl = editorUrlBuilder.getEditorUrl(experimentBean);
            String[] rowValues = new String[6];
            rowValues[0] = "<a href="+ editorUrl + ">"+ experimentBean.getAc()+ "</a>";
            rowValues[1] = experimentBean.getShortlabel();
            rowValues[2] = cv.getAc();
            rowValues[3] = cv.getShortlabel();
            rowValues[4] = "" + date;
            rowValues[5] = user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);
        }
        if (intactBean instanceof BioSourceBean) {
            BioSourceBean biosourceBean = (BioSourceBean) intactBean;
            editorUrl = editorUrlBuilder.getEditorUrl(biosourceBean);
            String[] rowValues = new String[7];
            rowValues[0] = "<a href="+ editorUrl + ">"+ biosourceBean.getAc()+ "</a>";
            rowValues[1] = biosourceBean.getShortlabel();
            rowValues[2] = cv.getAc();
            rowValues[3] = cv.getShortlabel();
            rowValues[4] = "" + date;
            rowValues[5] = user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

        }
        if (intactBean instanceof ComponentBean) {
            ComponentBean componentBean = (ComponentBean) intactBean;
            editorUrl = editorUrlBuilder.getEditorUrl(componentBean);
            String[] rowValues = new String[7];
            rowValues[0] = "<a href="+ editorUrl + ">"+ componentBean.getInteraction_ac() + "</a>";
            rowValues[1] = componentBean.getAc();
            rowValues[2] = componentBean.getInteractor_ac();
            rowValues[3] = cv.getAc();
            rowValues[4] = cv.getShortlabel();
            rowValues[5] = "" + date;
            rowValues[6] = user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);
        }
        if (intactBean instanceof InteractorBean){
            InteractorBean interactorBean = (InteractorBean) intactBean;
            editorUrl = editorUrlBuilder.getEditorUrl(interactorBean);
            String[] rowValues = new String[6];
            rowValues[0] = "<a href="+ editorUrl + ">"+ interactorBean.getAc() + "</a>";
            rowValues[1] = interactorBean.getShortlabel();
            rowValues[2] = cv.getAc();
            rowValues[3] = cv.getShortlabel();
            rowValues[4] = "" + date;
            rowValues[5] = user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

        }
        if(intactBean instanceof FeatureBean){
            FeatureBean featureBean = (FeatureBean) intactBean;
            InteractorBean relatedInteraction = getFeaturedInteraction(featureBean.getAc());
            editorUrl = editorUrlBuilder.getEditorUrl(relatedInteraction);
            String[] rowValues = new String[7];
            rowValues[0] = "<a href="+ editorUrl + ">"+ relatedInteraction.getAc() + "</a>";
            rowValues[1] = featureBean.getAc();
            rowValues[2] = featureBean.getShortlabel();
            rowValues[3] = cv.getAc();
            rowValues[4] = cv.getShortlabel();
            rowValues[5] = "" + date;
            rowValues[6] = "" + user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);


        }

        if(intactBean instanceof XrefBean ){
            //XREF_WITH_NON_VALID_PRIMARY_ID
            XrefBean xrefBean = (XrefBean) intactBean;
            IntactBean xreferencedBean = SanityCheckerHelper.getXreferencedObject(xrefBean);
            String[] rowValues = new String[9];
            if(xreferencedBean instanceof FeatureBean){
                FeatureBean featureBean = (FeatureBean) xreferencedBean;
                InteractorBean relatedInteraction = getFeaturedInteraction(featureBean.getAc());
                editorUrl = editorUrlBuilder.getEditorUrl(relatedInteraction);
                //still to test
                rowValues[0] ="<a href="+ editorUrl + ">"+ relatedInteraction.getAc() + "</a>";
                rowValues[1] =featureBean.getAc() ;
                rowValues[2]="";
            }
            else{
                String type = getTypeFromIntactBean(xreferencedBean);
                editorUrl = editorUrlBuilder.getEditorUrl(xreferencedBean);
                //tested
                //!!! put in an extra column with type?!
                rowValues[0] ="<a href="+ editorUrl + ">"+ xreferencedBean.getAc() + "</a>";
                rowValues[1] ="";
                rowValues[2] = getTypeFromIntactBean(xreferencedBean);
            }
            rowValues[3] =xrefBean.getAc() ;
            rowValues[4] =xrefBean.getPrimaryid();
            rowValues[5] = cv.getAc();
            rowValues[6] = cv.getShortlabel();
            rowValues[7] ="" + date;
            rowValues[8] =  user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);
        }
        addUserMessage(topic, user, userMessageReport, adminMessageReport);
        addAdminMessage(topic, adminMessageReport);
    }


    /**
     * This addMessage method is used to send the message corresponding to the ReportTopic INTERACTION_LINKED_TO_MORE_
     * THEN_ONE_EXPERIMENT.
     * The build message error we look like that :
     *
     *          Interaction linked to more then one experiment
     *          AC:EBI-1234     Shortlabel:foo     When: 10 jul 2005
     *              AC:EBI-2345         Shortlabel:foo     When:
     *              AC:EBI-3456         Shortlabel:foo     When:
     *              ...etc
     *
     * Therefore the first line should link to an Interaction page and the other lines to an Experiment page
     *
     * @param topic
     * @param interactionBean
     * @param experimentBeans
     * @throws SQLException
     */

    public void addMessage( ReportTopic topic, InteractorBean interactionBean, List experimentBeans ) throws SQLException {

        String user = interactionBean.getUserstamp();
        Timestamp date = interactionBean.getUpdated();

        StringBuffer sbUserMessageReport = new StringBuffer();
        StringBuffer sbAdminMessageReport = new StringBuffer();

        String interactionEditorUrl = editorUrlBuilder.getEditorUrl(interactionBean);
        String[] rowValues = new String[4];
        rowValues[0] ="<a href="+ interactionEditorUrl + ">"+  interactionBean.getAc() + "</a>";
        rowValues[1] =interactionBean.getShortlabel();
        rowValues[2] = "" + date;
        rowValues[3] = user;
        sbUserMessageReport.append(formatRow("html",rowValues,"values","userReport",true));
        sbAdminMessageReport.append(formatRow("html",rowValues,"values","adminReport",true));

        for (int i = 0; i < experimentBeans.size(); i++) {
            Object o =  experimentBeans.get(i);
            if(o instanceof ExperimentBean){

                ExperimentBean experimentBean = (ExperimentBean) o;

                String experimentEditorUrl = editorUrlBuilder.getEditorUrl(experimentBean);

                String experimentUser = experimentBean.getUserstamp();
                Timestamp experimentDate = experimentBean.getUpdated();
                String[] rowValues2 = new String[4];
                rowValues2[0] ="<a href="+ experimentEditorUrl + ">"+  experimentBean.getAc() + "</a>";
                rowValues2[1] =experimentBean.getShortlabel();
                rowValues2[2] = "" + experimentDate;
                rowValues2[3] = experimentUser;
                sbUserMessageReport.append(formatRow("html",rowValues2,"values","userReport",false));
                sbAdminMessageReport.append(formatRow("html",rowValues2,"values","adminReport",false));
            }


        }
        //add empty row to seperate the different blocks of an interaction and its linked experiments
        for (int i=0; i < 4; i++)  {
            rowValues[i]="";
        }
        sbUserMessageReport.append(formatRow("html",rowValues,"values","userReport",false));
        sbAdminMessageReport.append(formatRow("html",rowValues,"values","adminReport",false));

        addUserMessage(topic, user, sbUserMessageReport.toString(), sbAdminMessageReport.toString());
        addAdminMessage(topic, sbAdminMessageReport.toString());
    }

    public void addMessage (ReportTopic topic, RangeBean rangeBean){
        String editorUrl;

        String user = rangeBean.getUserstamp();
        Timestamp date = rangeBean.getUpdated();


        String userMessageReport="";
        String adminMessageReport="";



        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            Range range = (Range) helper.getObjectByAc(Range.class, rangeBean.getAc());

            editorUrl = editorUrlBuilder.getEditorUrl("Interaction",rangeBean.getInteraction_ac());
            String[] rowValues = new String[8];
            rowValues[0] ="<a href="+ editorUrl + ">"+ rangeBean.getInteraction_ac() + "</a>";
            rowValues[1] = rangeBean.getInteractor_ac();
            rowValues[2] = rangeBean.getFeature_ac();
            rowValues[3] = String.valueOf(range.getToIntervalStart());
            rowValues[4] = String.valueOf(range.getFromIntervalEnd());
            rowValues[5] = rangeBean.getAc();
            rowValues[6] = "" + date;
            rowValues[7] =  user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

        } catch (IntactException e) {
            e.printStackTrace();
        }


        addUserMessage(topic, user, userMessageReport, adminMessageReport);
        addAdminMessage(topic, adminMessageReport);

    }

    /**
     * Helper method to obtain userstamp info from a given record, and then if it has any to append the details to a
     * result buffer.
     *
     * @param topic the type of error we have dicovered for the given AnnotatedObject.
     * @param intactBean   The Intact object that user info is required for.
     *
    */
    public void addMessage( ReportTopic topic, IntactBean intactBean ) throws SQLException, IntactException {

        String editorUrl;// = editorUrlBuilder.getEditorUrl(intactBean);

        String user = intactBean.getUserstamp();
        Timestamp date = intactBean.getUpdated();


        String userMessageReport="";
        String adminMessageReport="";

        if(intactBean instanceof RangeBean){
            RangeBean rangeBean = (RangeBean) intactBean;
            IntactHelper helper = null;
            try {
                helper = new IntactHelper();
                Range range = (Range) helper.getObjectByAc(Range.class, rangeBean.getAc());

                editorUrl = editorUrlBuilder.getEditorUrl("Interaction",rangeBean.getInteraction_ac());
                String[] rowValues = new String[10];
                rowValues[0] ="<a href="+ editorUrl + ">"+ rangeBean.getInteraction_ac() + "</a>";
                rowValues[1] = rangeBean.getInteractor_ac();
                rowValues[2] = rangeBean.getFeature_ac(); // "" + date;
                rowValues[3] = String.valueOf(range.getFromIntervalEnd());
                rowValues[4] = String.valueOf(range.getToIntervalStart());
                rowValues[5] = String.valueOf((range.getToIntervalStart()-range.getFromIntervalEnd()));
                rowValues[6] = rangeBean.getAc();
                rowValues[7] = rangeBean.getShortlabel();
                rowValues[8] = "" + date;
                rowValues[9] =  user;
                userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
                adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

            } catch (IntactException e) {
                e.printStackTrace();
            }

        }else if(intactBean instanceof AnnotatedBean){
            AnnotatedBean annotatedBean = (AnnotatedBean) intactBean;
            editorUrl = editorUrlBuilder.getEditorUrl(annotatedBean);
            String[] rowValues;
            // If the intact term is a CvInteraction then we can not link it to the editor, as the editor can not edit
            // CvInteraction. So we do not put any href on the ac.
            if( intactBean instanceof ControlledvocabBean && CvInteraction.class.getName().equals(((ControlledvocabBean)intactBean).getObjclass())){
                rowValues = new String[5];
                rowValues[0] = annotatedBean.getAc();
                rowValues[1] =annotatedBean.getShortlabel();
                rowValues[2] = new String("CvInteraction");
                rowValues[3] = "" + date;
                rowValues[4] = user;
            }
            else{
                rowValues = new String[4];
                rowValues[0] ="<a href="+ editorUrl + ">"+ annotatedBean.getAc() + "</a>";
                rowValues[1] =annotatedBean.getShortlabel();
                rowValues[2] = "" + date;
                rowValues[3] = user;
            }
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

        }else if(intactBean instanceof XrefBean ){
            //XREF_WITH_NON_VALID_PRIMARY_ID
            XrefBean xrefBean = (XrefBean) intactBean;
            IntactBean xreferencedBean = SanityCheckerHelper.getXreferencedObject(xrefBean);
             String[] rowValues = new String[8];
            if(xreferencedBean instanceof FeatureBean){
                FeatureBean featureBean = (FeatureBean) xreferencedBean;
                InteractorBean relatedInteraction = getFeaturedInteraction(featureBean.getAc());
                editorUrl = editorUrlBuilder.getEditorUrl(relatedInteraction);
                  //still to test

                rowValues[0] ="<a href="+ editorUrl + ">"+ relatedInteraction.getAc() + "</a>";
                rowValues[1] =featureBean.getAc() ;
                rowValues[2]="";
            }
            else{
                String type = getTypeFromIntactBean(xreferencedBean);
                editorUrl = editorUrlBuilder.getEditorUrl(xreferencedBean);
                //tested
                //!!! put in an extra column with type?!
                rowValues[0] ="<a href="+ editorUrl + ">"+ xreferencedBean.getAc() + "</a>";
                rowValues[1] ="";
                rowValues[2] = getTypeFromIntactBean(xreferencedBean);
            }
            rowValues[3] =xrefBean.getAc() ;
            rowValues[4] =xrefBean.getPrimaryid();
            rowValues[5] =xrefBean.getDatabase_ac();
            rowValues[6] ="" + date;
            rowValues[7] =  user;
            userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
            adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);
        }
        addUserMessage(topic, user, userMessageReport, adminMessageReport);
        addAdminMessage(topic, adminMessageReport);
    }


     public void addMessage( ReportTopic topic, IntactBean intactBean, String message ) throws SQLException {

        String user = intactBean.getUserstamp();
        Timestamp date = intactBean.getUpdated();


        String userMessageReport="";
        String adminMessageReport="";
        // Build users report

        if(intactBean instanceof AnnotatedBean){
            AnnotatedBean annotatedBean = (AnnotatedBean) intactBean;

            String editorUrl = editorUrlBuilder.getEditorUrl(annotatedBean);
                 //still to test
                String[] rowValues = new String[4];
                rowValues[0] ="<a href="+ editorUrl + ">"+ annotatedBean.getAc() + "</a>";
                rowValues[1] = message;
                rowValues[2] = "" + date;
                rowValues[3] = user;

                userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
                adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

              //use for formatting header
//            adminMessageReport = "Interaction AC: " + annotatedBean.getAc() +
//                    "\n" + message +
//                    "\t When: " +  +
//                    "\t User: " +  + "\n";


        }
          addUserMessage(topic, user, userMessageReport, adminMessageReport);
          addAdminMessage(topic, adminMessageReport);
    }


    /**
     *
     * This method is used to build the message error corresponding to the ReportTopic DUPLICATED_PROTEIN. The message
     * will look like that :
     *
     *              Those proteins are duplicated :
     *              Protein List :
     *                  AC:EBI-1234      Shortlabel:foo      When:foo
     *                  AC:EBI-1234      Shortlabel:foo      When:foo
     *                  AC:EBI-1234      Shortlabel:foo      When:foo
     *                  AC:EBI-1234      Shortlabel:foo      When:foo
     *
     * Therefore, each line should be linked to a Protein Editor page.
     *
     * @param topic the type of error we have dicovered for the given AnnotatedObject.
     * @param intactBeans   The Intact object that user info is required for.
     *
     * @throws java.sql.SQLException thrown if there were DB problems
     */
    public void addMessage( ReportTopic topic, List intactBeans ) throws SQLException {
        //DUPLICATED_PROTEIN
        StringBuffer sbUserMessageReport = new StringBuffer();
        StringBuffer sbAdminMessageReport = new StringBuffer();
//        sbUserMessageReport.append("Protein List :").append(NEW_LINE);
//        sbAdminMessageReport.append("Protein List :").append(NEW_LINE);

        List users = new ArrayList();
        String[] rowValues = new String[4];
        for (int i = 0; i < intactBeans.size(); i++) {
            IntactBean intactBean =  (IntactBean) intactBeans.get(i);

            // Build users report
            if(intactBean instanceof InteractorBean){
                InteractorBean interactorBean = (InteractorBean) intactBean;
                String user = intactBean.getUserstamp();
                Timestamp date = intactBean.getUpdated();
                if(!users.contains(user)){
                    users.add(user);
                }

                String editorUrl= editorUrlBuilder.getEditorUrl(interactorBean);
                rowValues[0] ="<a href="+ editorUrl + ">"+ interactorBean.getAc() + "</a>";
                rowValues[1] = interactorBean.getShortlabel();
                rowValues[2] = "" + date;
                rowValues[3] = user;

                sbUserMessageReport.append(formatRow("html",rowValues,"values","userReport",false));
                sbAdminMessageReport.append(formatRow("html",rowValues,"values","adminReport",false));
            }
        }
        //add empty row to seperate the different blocks of proteins which have duplicated reference to UnitProt
        for (int i=0; i < 4; i++)  {
            rowValues[i]="";
        }
        sbUserMessageReport.append(formatRow("html",rowValues,"values","userReport",false));
        sbAdminMessageReport.append(formatRow("html",rowValues,"values","adminReport",false));
        for (int i = 0; i < users.size(); i++) {
            String user = (String) users.get(i);
             addUserMessage(topic, user, sbUserMessageReport.toString(), sbAdminMessageReport.toString());
        }
         addAdminMessage(topic, sbAdminMessageReport.toString());
    }

    /**
     * @param topic
     * @param annotationBean
     * @param topicShortlabel
     */
    public void addMessage( ReportTopic topic, AnnotationBean annotationBean,  String topicShortlabel) throws SQLException, IntactException {//( ReportTopic topic, AnnotationBean annotationBean, AnnotatedBean annotatedBean, String annotatedType, String topicShortlabel) throws SQLException, IntactException {
       //TOPICAC_NOT_VALID
        String user = annotationBean.getUserstamp();
        Timestamp date = annotationBean.getUpdated();

        AnnotatedBean annotatedBean = SanityCheckerHelper.getAnnotatedBeanFromAnnotation(annotationBean.getAc());

        String editorUrl = null;//editorUrlBuilder.getEditorUrl(annotatedBean);
        String annotatedBeanType = getTypeFromIntactBean(annotatedBean);
        String userMessageReport="";
        String adminMessageReport="";
        String[] rowValues = new String[8];
        // Build users report
        if(annotatedBean instanceof FeatureBean){
            FeatureBean featureBean = (FeatureBean) annotatedBean;
            InteractorBean interaction = getFeaturedInteraction(featureBean.getAc());
            editorUrl = editorUrlBuilder.getEditorUrl(interaction);
            //still to test
            rowValues[0] = featureBean.getAc();
            rowValues[1] =annotatedBeanType;
            rowValues[2] ="<a href="+ editorUrl + ">"+ interaction.getAc() + "</a>";
        } else {
            editorUrl = editorUrlBuilder.getEditorUrl(annotatedBean);
            rowValues[0] ="<a href="+ editorUrl + ">"+ annotatedBean.getAc() + "</a>";
            rowValues[1] =annotatedBeanType;
            rowValues[2] = "";
        }
        rowValues[3] =annotationBean.getAc();
        rowValues[4] =topicShortlabel;
        rowValues[5] =annotatedBean.getShortlabel();
        rowValues[6] ="" +  date;
        rowValues[7] =user;

        userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
        adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);
        addUserMessage(topic, user, userMessageReport, adminMessageReport);
        addAdminMessage(topic, adminMessageReport);
    }


    public void addMessage (AnnotatedBean annotatedBean, ReportTopic reportTopic) throws IntactException, SQLException {

        //FEATURE_WITHOUT_A_RANGE

        String userMessageReport = null;
        String adminMessageReport = null;

        String user = annotatedBean.getUserstamp();
        Timestamp date = annotatedBean.getUpdated();

        String[] rowValues = new String[4];
        FeatureBean featureBean = (FeatureBean) annotatedBean;
        InteractorBean interaction = getFeaturedInteraction(featureBean.getAc());
        String editorUrl=editorUrlBuilder.getEditorUrl(interaction);
        rowValues[0] = featureBean.getAc();
        rowValues[1] ="<a href="+ editorUrl + ">"+  interaction.getAc() + "</a>";
        rowValues[2] = "" + date;
        rowValues[3] = user;

        userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
        adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

        addUserMessage(reportTopic, user, userMessageReport, adminMessageReport);
        addAdminMessage(reportTopic, adminMessageReport);
    }

    public void addMessage (AnnotationBean annotationBean) throws IntactException, SQLException {

        //URL_NOT_VALID

        String userMessageReport = null;
        String adminMessageReport = null;

        AnnotatedBean annotatedBean = SanityCheckerHelper.getAnnotatedBeanFromAnnotation(annotationBean.getAc());
        String annotatedBeanType = getTypeFromIntactBean(annotatedBean);

        String user = annotatedBean.getUserstamp();
        Timestamp date = annotatedBean.getUpdated();

        String[] rowValues = new String[7];
        if(annotatedBean instanceof FeatureBean){
            FeatureBean featureBean = (FeatureBean) annotatedBean;
            InteractorBean interaction = getFeaturedInteraction(featureBean.getAc());
            //still to test
            String editorUrl=editorUrlBuilder.getEditorUrl(interaction);
            rowValues[0] = featureBean.getAc();
            rowValues[1] = annotatedBeanType;
            rowValues[2] ="<a href="+ editorUrl + ">"+  interaction.getAc() + "</a>";
        }else{
            //still to test
            String editorUrl=editorUrlBuilder.getEditorUrl(annotatedBean);
            rowValues[0] ="<a href="+ editorUrl + ">"+ annotatedBean.getAc() + "</a>";
            rowValues[1] = annotatedBeanType;
            rowValues[2] = "";
        }
        rowValues[3] = annotationBean.getDescription();
        rowValues[4] = annotationBean.getAc();
        rowValues[5] = "" + date;
        rowValues[6] = user;

        userMessageReport =  formatRow("html",rowValues,"values","userReport",false);
        adminMessageReport = formatRow("html",rowValues,"values","adminReport",false);

         addUserMessage(ReportTopic.URL_NOT_VALID, user, userMessageReport, adminMessageReport);
         addAdminMessage(ReportTopic.URL_NOT_VALID, adminMessageReport);
    }


    /**
     * post emails to the curators (their individual errors) and to the administrator (global list of errors)
     *
     * @throws javax.mail.MessagingException
     */
    public void postEmails(String mailObject) throws MessagingException, IntactException {

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
                fullReport.append("<table>");
                String header = getHeader(topic,"userReport");
                fullReport.append( header );
                Collection messages = (Collection) reportMessages.get( topic );


                // write individual messages of that topic.
                for ( Iterator iterator2 = messages.iterator(); iterator2.hasNext(); ) {
                    String message = (String) iterator2.next();

                    fullReport.append( message );
                    errorCount++;
                } // messages in the topic

                fullReport.append("</table>").append(NEW_LINE);
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
                                     mailObject + " - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
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
                fullReport.append("<table>");
                String header = getHeader(topic,"adminReport");
                fullReport.append( header );
                for ( Iterator iterator1 = messages.iterator(); iterator1.hasNext(); ) {
                    String message = (String) iterator1.next();
                    fullReport.append( message );
                    errorCount++;
                } // messages

                fullReport.append("</table>").append(NEW_LINE);
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
                         mailObject + " (ADMIN) - " + TIME + " (" + errorCount + " error" + ( errorCount > 1 ? "s" : "" ) + ")",
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


    /**
     * Given an IntactBean in parameter it will return a string corresponding to the type of the bean :
     * If the bean is an ExperimentBean it will return the string "Experiment".
     * Here is the mapping of done :
     *
     *                            I N T A C T B E A N    C L A S S    ==>   T Y P E
     *
     *      uk.ac.ebi.intact.util.sanityChecker.model.ExperimentBean  ==> "Experiment"
     *      uk.ac.ebi.intact.util.sanityChecker.model.InteractorBean  ==> "Protein" (if objclass is equal to uk.ac.ebi.intact.model.Protein)
     *                                                                ==> "Interaction" (if objclass is equal to uk.ac.ebi.intact.model.Interaction)
     *      uk.ac.ebi.intact.util.sanityChecker.model.BioSource       ==> "BioSource"
     *      uk.ac.ebi.intact.util.sanityChecker.model.Feature         ==> "Feature"
     *      uk.ac.ebi.intact.util.sanityChecker.model.Controlledvocab ==> "CvTopic" (if objclass is equal to uk.ac.ebi.intact.model.CvTopic)
     *                                                                ==> "CvCellType" (if objclass is equal to uk.ac.ebi.intact.model.CvCellType
     *                                                                ==> "CvDatabase" (if objclass is equal to uk.ac.ebi.intact.model.CvCellType
     *                                                                ==> "CvAliasType" (if objclass is equal to uk.ac.ebi.intact.model.CvCellType
     *                                                                ==> "CvTissue" (if objclass is equal to uk.ac.ebi.intact.model.CvCellType
     *                                                                ==> "CvFuzzyType" (if objclass is equal to uk.ac.ebi.intact.model.CvCellType
     *                                                                ==> "CvXrefQualifier" (if objclass is equal to uk.ac.ebi.intact.model.CvCellType
     *
     *      Protein
     * @param intactBean
     * @return type
     */
     public String getTypeFromIntactBean(IntactBean intactBean){

        String type = new String();
        int i_cor =0;

        if(intactBean instanceof ExperimentBean){
            type = "Experiment";
        }
        else if(intactBean instanceof InteractorBean){
            InteractorBean interactorBean = (InteractorBean) intactBean;
            String objclass = interactorBean.getObjclass();

            if(ProteinImpl.class.getName().equals(objclass)){
                type = "Protein";
            }else if ( InteractionImpl.class.getName().equals(objclass)){
                type = "Interaction";
            }
        }
        else if ( intactBean instanceof BioSourceBean ){
            type = "BioSource";
        }
        else if ( intactBean instanceof ControlledvocabBean ){

            ControlledvocabBean cvBean = (ControlledvocabBean) intactBean;

            String objclass = cvBean.getObjclass();

            if(CvTopic.class.getName().equals(objclass)){
                type = "CvTopic";
            }
            else if(CvAliasType.class.getName().equals(objclass)){
                type = "CvAliasType";
            }
            else if(CvCellType.class.getName().equals(objclass)){
                type = "CvCellTYpe";
            }
            else if(CvComponentRole.class.getName().equals(objclass)){
                type = "CvComponentRole";
            }
            else if(CvDatabase.class.getName().equals(objclass)){
                type = "CvDatabase";
            }
            else if(CvFuzzyType.class.getName().equals(objclass)){
                type = "CvFuzzyType";
            }
            else if(CvTissue.class.getName().equals(objclass)){
                type = "CvTissue";
            }
            else if(CvXrefQualifier.class.getName().equals(objclass)){
                type = "CvXrefQualifier";
            }
        }

        return type;
    }

    public void addUserMessage (ReportTopic topic, String user, String userMessageReport, String adminMessageReport){
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


    public void addAdminMessage (ReportTopic topic, String adminMessageReport){
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
     * Given a featureAc it returns an InteractorBean representing the Interaction where this Feature can be found.
     * @param featureAc
     * @return interactorBean
     * @throws IntactException
     * @throws SQLException
     */

    public InteractorBean getFeaturedInteraction (String featureAc) throws IntactException, SQLException {
         InteractorBean interactionBean = null;

         IntactHelper intactHelper = new IntactHelper();
         SanityCheckerHelper sch = new SanityCheckerHelper(intactHelper);

         sch.addMapping(InteractorBean.class,"select i.ac, i.objclass, i.userstamp, i.updated, i.fullname, i.shortlabel "+
                                         "from ia_interactor i, ia_component c, ia_feature f "+
                                         "where i.ac=c.interaction_ac and c.ac=f.component_ac and f.ac=?");

         List featuredInteractions = sch.getBeans(InteractorBean.class,featureAc);

         if(!featuredInteractions.isEmpty()){
            interactionBean = (InteractorBean) featuredInteractions.get(0);
         }
         intactHelper.closeStore();

         return interactionBean;
     }

        private String formatRow(String format, String[] rowValues, String rowType, String reportType, boolean italic){
        String columnCode="";
        if (rowType.equals("headers")) {
            columnCode = "th";
        }
        else if (rowType.equals("values")){
            columnCode = "td";
        }

        StringBuffer rowText = new StringBuffer();
        if (format.equalsIgnoreCase("html"))  {
            rowText.append("<tr>");

            int numberOfField=0;
            if (reportType.equals("userReport")) numberOfField=rowValues.length-1; //lastfield is filled with user
            else if (reportType.equals("adminReport")) numberOfField=rowValues.length;
            for (int i=0; i < numberOfField;i++) {
               rowText.append("<" + columnCode + " align=\"left\">");
               if (italic) rowText.append("<i>");
               rowText.append(rowValues[i] + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp"); // put in spaces when ready: );
               rowText.append("</"+ columnCode + ">");
               if (italic) rowText.append("</i>");
            }

            rowText.append("</tr>");
        }
        return rowText.toString();
    };

    private String getHeader(ReportTopic topic, String reportType) {

        String header="";

            if (topic.equals(ReportTopic.DELETION_INTERVAL_TO_LONG_TO_BE_CARACTERIZED_BY_DELETION_ANALYSIS_FEATURE_TYPE)){
                String[] rowValues = new String[10];
                rowValues[0] ="Interaction Ac";
                rowValues[1] ="Interactor Ac";
                rowValues[2] = "Feature Ac"; // "" + date;
                rowValues[3] = "FromIntervalStart";
                rowValues[4] = "ToIntervalEnd";
                rowValues[5] = "Deletion Size";
                rowValues[6] = "Range Ac";
                rowValues[7] = "Shortlabel";
                rowValues[8] = "When";
                rowValues[9] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }
            else if (topic.equals(ReportTopic.URL_NOT_VALID)){
                 // in case of feature it the extra column is filled
                String[] rowValues = new String[7];
                rowValues[0] ="AnnotatedBean Ac";
                rowValues[1] = "AnnotatedBean Type";
                rowValues[2] ="Interaction AC";  //only be filled in case of featureBean
                rowValues[3] = "Annotation Description (URI)";
                rowValues[4] = "Annotation AC";
                rowValues[5] = "When";
                rowValues[6] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }
             else if (topic.equals(ReportTopic.TOPICAC_NOT_VALID)){
              String[] rowValues = new String[8];
                rowValues[0] ="AnnotatedBean AC";
                rowValues[1] ="AnnotatedBeanType";
                rowValues[2] ="Interaction Ac";
                rowValues[3] ="Annotation AC";
                rowValues[4] ="TopicAc";
                rowValues[5] ="Shortlabel";
                rowValues[6] ="When";
                rowValues[7] ="User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }
            else if (topic.equals(ReportTopic.XREF_WITH_NON_VALID_PRIMARYID)){
                String[] rowValues = new String[8];
                rowValues[0] ="Interaction/type Ac";
                rowValues[1] ="Feature/ Ref Ac";
                rowValues[2] ="Type";
                rowValues[3] ="Xref AC" ;
                rowValues[4] ="PrimaryId";
                rowValues[5] ="Database_ac";
                rowValues[6] ="When";
                rowValues[7] ="User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if(topic.equals(ReportTopic.FEATURE_WITHOUT_A_RANGE)){
                String[] rowValues = new String[4];
                rowValues[0] ="FeatureBean Ac";
                rowValues[1] = "Interaction Ac";
                rowValues[2] ="When";
                rowValues[3] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);

            } else if (ReportTopic.RANGE_SEQUENCE_NOT_EQUAL_TO_PROTEIN_SEQ.equals(topic) ||
                       ReportTopic.RANGE_SEQUENCE_SAVED_BY_ADDING_THE_M.equals(topic) ||
                       ReportTopic.RANGE_SEQUENCE_SAVED_BY_SUPPRESSING_THE_M.equals(topic)){
                String[] rowValues = new String[8];
                rowValues[0] ="Interaction Ac";
                rowValues[1] = "Protein Ac";
                rowValues[2] ="Feature Ac";
                rowValues[3] = "ToIntervalStart";
                rowValues[4] = "FromIntervalEnd";
                rowValues[5] = "RangeBean Ac";
                rowValues[6] = "Date";
                rowValues[7] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }  else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_DATABASE_AC_IN_XREF.equals(topic) ||
                       ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_QUALIFIER_AC_IN_XREF.equals(topic)){
                String[] rowValues = new String[10];
                rowValues[0] ="XreferencedBean";
                rowValues[1] = "FeatureBean Ac";
                rowValues[2] ="Type";
                rowValues[3] ="Xref Ac";
                rowValues[4] = "Xref primaryId";
                //rowValues[5] ="Database Ac";
                rowValues[5] = "Cv Ac";
                rowValues[6] = "Cv Shortlabel";
                rowValues[7] ="When";
                rowValues[8] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_IDENTIFICATION_AC_IN_FEATURE.equals(topic) ||
                       ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_FEATURETYPE_AC_IN_FEATURE.equals(topic)){
                String[] rowValues = new String[7];
                rowValues[0] = "Interaction Ac";
                rowValues[1] = "Feature Ac";
                rowValues[2] = "Feature Shortlabel";
                rowValues[3] = "Cv Ac";
                rowValues[4] = "Cv ShortLabel";
                rowValues[5] = "When";
                rowValues[6] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_FROMFUZZYTYPE_AC_IN_RANGE.equals(topic) ||
                       ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_IN_USED_AS_TOFUZZYTYPE_AC_IN_RANGE.equals(topic)){

                String[] rowValues = new String[8];

                rowValues[0] = "Interaction Ac";
                rowValues[1] = "Interactor Ac";
                rowValues[2] = "Feature Ac";
                rowValues[3] = "Range Ac";
                rowValues[4] = "Cv Ac";
                rowValues[5] = "Cv Shortlabel";
                rowValues[6] = "When";
                rowValues[7] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_ROLE_IN_COMPONENT.equals(topic)){
                String[] rowValues = new String[7];
                rowValues[0] = "Interaction Ac";
                rowValues[1] = "Component Ac";
                rowValues[2] = "Interactor Ac";
                rowValues[3] = "Cv Ac";
                rowValues[4] = "Cv Shortlabel";
                rowValues[5] = "When";
                rowValues[6] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_INTERACTORTYPE_IN_INTERACTOR.equals(topic) ||
                       ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_INTERACTIONTYPE_IN_INTERACTOR.equals(topic) ||
                       ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_PROTEINFORM_IN_INTERACTOR.equals(topic) ){
                String[] rowValues = new String[6];
                rowValues[0] = "Interaction/Protein Ac";
                rowValues[1] = "Interaction/Protein Shortlabel";
                rowValues[2] = "Cv Ac";
                rowValues[3] = "Cv Shortlabel";
                rowValues[4] ="When";
                rowValues[5] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_CELLTYPEAC_IN_BIOSOURCE.equals(topic) ||
                       ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_TISSUEAC_IN_BIOSOURCE.equals(topic) ){
                String[] rowValues = new String[6];
                rowValues[0] = "BioSource Ac";
                rowValues[1] = "BioSource Shortlabel";
                rowValues[2] = "Cv Ac";
                rowValues[3] = "Cv Shortlabel";
                rowValues[4] = "When";
                rowValues[5] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_DETECTMETHODAC_IN_EXPERIMENT.equals(topic) ||
                       ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_IDENTMETHODAC_IN_EXPERIMENT.equals(topic) ){
                String[] rowValues = new String[6];
                rowValues[0] = "Experiment Ac";
                rowValues[1] = "Experiment Shortlabel";
                rowValues[2] = "Cv Ac";
                rowValues[3] = "Cv Shortlabel";
                rowValues[4] = "When";
                rowValues[5] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            } else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_TOPICAC_IN_ANNOTATION.equals(topic)){
                String[] rowValues = new String[6];
                rowValues[0] = "AnnotatedObject Ac ";
                rowValues[1] = "AnnotatedObject type";
                rowValues[2] = "Cv Ac";
                rowValues[3] = "Cv Shortlabel";
                rowValues[4] ="When";
                rowValues[5] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }else if (ReportTopic.HIDDEN_OR_OBSOLETE_CVOBJECT_USED_AS_ALIASTYPEAC_IN_ALIAS.equals(topic)){
                String[] rowValues = new String[7];
                rowValues[0] = "Alias Ac";
                rowValues[1] = "Alias ParentAc";
                rowValues[2] = "Alias Name";
                rowValues[3] = "Cv Ac";
                rowValues[4] = "Cv Shortlabel";
                rowValues[5] = "When";
                rowValues[6] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }else if (ReportTopic.CVINTERACTION_WITHOUT_ANNOTATION_UNIPROT_DR_EXPORT.equals(topic)){
                String[] rowValues = new String[5];
                rowValues[0] = "Ac";
                rowValues[1] = "Shortlabel ";
                rowValues[2] = "ObjClass";
                rowValues[3] = "When";
                rowValues[4] = "User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }
            else {
                String[] rowValues = new String[4];
                rowValues[0] ="AC";
                rowValues[1] ="Shortlabel";
                rowValues[2] ="When";
                rowValues[3] ="User";
                header = formatRow("html", rowValues, "headers",reportType,false);
            }

//(topic.equals(ReportTopic.DUPLICATED_PROTEIN)) has default structure


        //RANGE_SEQUENCE_SAVED_BY_SUPPRESSING_THE_M  out of order
        //PROTEIN_SEQUENCE_AND_RANGE_SEQUENCE_NOT_EQUAL out of order
        return header;
    }

}
