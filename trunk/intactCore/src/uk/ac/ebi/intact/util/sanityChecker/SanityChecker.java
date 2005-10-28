/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import uk.ac.ebi.intact.util.sanityChecker.model.*;
import uk.ac.ebi.intact.util.Crc64;
import uk.ac.ebi.intact.application.commons.util.AnnotationSection;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

import javax.mail.MessagingException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.io.IOException;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.ojb.broker.accesslayer.LookupException;



/**
 * TODO comment it.
 *
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class
        SanityChecker {
    private int duplicProt = 0;
    private SanityCheckerHelper sch;

    private EditorUrlBuilder editorUrlBuilder;

    /*
    * Those are the several SanityCheckerHelper to help retrieving the data in the database
    * I have done several sanityCheckerHelper object because you can associate only one sql request to each type of bean
    * (InteractorBean, ExperimentBean ...etc). But for the same type of Bean the different checkMethod will need
    * different type of sql request. So in order to avoid confusion I have created several Sanity check helper.
    * If the name of the sanityCheckHelper is onHoldSch if means that it is used in the method ExperimentIsOnHold and
    * InteractionIsOnHold. If the name is sch12 it means that it is used to check the rule 12.
    */
    private SanityCheckerHelper superCuratedSch;
    private SanityCheckerHelper deletionFeatureSch;
    private SanityCheckerHelper onHoldSch;
    private SanityCheckerHelper toBeReviewedSch;
    private SanityCheckerHelper noUniprotUpdateSch;
    private SanityCheckerHelper sch12;
    private SanityCheckerHelper sch13;
    private SanityCheckerHelper retrieveObjectSch;
    private SanityCheckerHelper oneIntOneExpSch;
    private SanityCheckerHelper hasValidPrimaryIdSch;
    private SanityCheckerHelper annotationTopic;
    private SanityCheckerHelper rangeSeqSch;
    private SanityCheckerHelper objectFromAc;
    /* ControlledvocabBean */
    private static ControlledvocabBean onHoldCvBean;
    private static ControlledvocabBean toBeReviewedCvBean;
    private static ControlledvocabBean acceptedCvBean;
    private static ControlledvocabBean noUniprotUpdateCvBean;

    private static ControlledvocabBean neutralCvBean;
    private static ControlledvocabBean baitCvBean;
    private static ControlledvocabBean preyCvBean;
    private static ControlledvocabBean enzymeCvBean;
    private static ControlledvocabBean enzymeTargetCvBean;
    private static ControlledvocabBean selfCvBean;
    private static ControlledvocabBean unspecifiedCvBean;
    private static ControlledvocabBean cTerminalCvBean;
    private static ControlledvocabBean nTerminalCvBean;
    private static ControlledvocabBean undeterminedCvBean;

    /**
     * Xref databases
     */
    private static ControlledvocabBean uniprotDatabaseCvBean;

    private static ControlledvocabBean pubmedDatabaseCvBean;

    private static ControlledvocabBean newtDatabaseCvBean;

    /**
     * Describe wether an Xref is related the primary SPTR AC (identityCrefQualifier) or not (secondaryXrefQualifier)
     */
    private static ControlledvocabBean primaryReferenceXrefQualifierCvBean;

    private static ControlledvocabBean identityXrefQualifierCvBean;

    /**
     * Is in charge to send the message to the curators and the admin
     */
    private MessageSender messageSender;

    private AnnotationSection annotationSection;

    private Map cvTopics;

    private IntactHelper helper;


    public SanityChecker() throws IntactException, SQLException {

        editorUrlBuilder = new EditorUrlBuilder();

        helper = new IntactHelper();

        objectFromAc = new SanityCheckerHelper(helper);
        objectFromAc.addMapping(ExperimentBean.class, "select ac, shortlabel, fullname, timestamp, userstamp, detectmethod_ac, identmethod_ac, biosource_ac from ia_experiment where ac = ?");
        objectFromAc.addMapping(InteractorBean.class, "select ac, shortlabel, fullname, crc64, timestamp, userstamp, biosource_ac, interactiontype_ac, objclass from ia_interactor where ac = ? ");


        deletionFeatureSch = new SanityCheckerHelper(helper);
        deletionFeatureSch.addMapping(RangeBean.class, " select i.userstamp, i.timestamp, c.interaction_ac, c.interactor_ac, r.feature_ac , r.ac , r.fromintervalend, r.tointervalstart "+
                                                       " from ia_interactor i, ia_feature f, ia_range r, ia_controlledvocab ident, ia_component c, ia_controlledvocab type "+
                                                       " where i.ac = c.interaction_ac and "+
                                                             " c.ac=f.component_ac and " +
	                                                         " f.identification_ac = ident.ac and " +
                                                             " f.featuretype_ac = type.ac and " +
                                                             " ident.shortlabel = 'deletion analysis' and " +
                                                             " (type.shortlabel = 'mutation' or type.shortlabel = 'mutation decreasing' or type.shortlabel = 'mutation increasing') and " +
                                                             " f.ac = r.feature_ac and "+
                                                             " (r.tointervalstart - r.fromintervalend) > ? ");

        rangeSeqSch = new SanityCheckerHelper(helper);
        rangeSeqSch.addMapping(RangeBean.class,"select r.ac, fromintervalstart, fromfuzzytype_ac, sequence, f.component_ac " +
                                               "from ia_range r, ia_feature f, ia_component c, ia_interactor i " +
                                               "where i.ac=c.interactor_ac and " +
                                               "c.ac=f.component_ac and " +
                                               "f.ac=r.feature_ac and " +
                                               "sequence is not null and "+
                                               "i.ac = ?");

        rangeSeqSch.addMapping(ComponentBean.class, "select interaction_ac from ia_component where ac=?");
        rangeSeqSch.addMapping(InteractorBean.class, "select ac, userstamp, timestamp, objclass from ia_interactor where ac=?");

        this.retrieveObjectSch = new SanityCheckerHelper(helper);

        retrieveObjectSch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac FROM ia_exp2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(Bs2AnnotBean.class, "SELECT biosource_ac FROM ia_biosource2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(Int2AnnotBean.class, "SELECT interactor_ac FROM ia_int2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(CvObject2AnnotBean.class, "SELECT cvobject_ac FROM ia_cvobject2annot WHERE annotation_ac=?");
        retrieveObjectSch.addMapping(Feature2AnnotBean.class, "SELECT feature_ac FROM ia_feature2annot WHERE annotation_ac=?");

        this.hasValidPrimaryIdSch=new SanityCheckerHelper(helper);
        hasValidPrimaryIdSch.addMapping(AnnotationBean.class, "select a.description " +
                                                              "from ia_annotation a, ia_cvobject2annot c2a " +
                                                              "where c2a.cvobject_ac = ? and "+//in (select ac from ia_controlledvocab where objclass like '" + CvDatabase.class.getName() + "') and " +
                                                              "c2a.annotation_ac=a.ac and " +
                                                              "a.topic_ac=(select ac from ia_controlledvocab where shortlabel='" + CvTopic.XREF_VALIDATION_REGEXP + "')");

        this.oneIntOneExpSch = new SanityCheckerHelper(helper);
        /*oneIntOneExpSch.addMapping(Int2ExpBean.class,"select interaction_ac, experiment_ac "+
        "from ia_int2exp "+
        "where interaction_ac = ? and "+
        "interaction_ac in ( select interaction_ac "+
        "from ia_int2exp "+
        "group by interaction_ac "+
        "having count(experiment_ac) > 1)");  */

        oneIntOneExpSch.addMapping(Int2ExpBean.class,"select interaction_ac, experiment_ac "+
                                                     "from ia_int2exp "+
                                                     "where interaction_ac like ? and "+
                                                     "interaction_ac in ( select interaction_ac "+
                                                     "from ia_int2exp "+
                                                     "group by interaction_ac "+
                                                     "having count(experiment_ac) > 1)");

        //oneIntOneExpSch.addMapping(Int2ExpBean.class, )
        oneIntOneExpSch.addMapping(ExperimentBean.class, "select ac, shortlabel, timestamp, userstamp "+
                                                         "from ia_experiment "+
                                                         "where ac = ? ");
        oneIntOneExpSch.addMapping(InteractorBean.class, "select ac, objclass, shortlabel, timestamp, userstamp "+
                                                         "from ia_interactor "+
                                                         "where ac = ? ");

        this.sch12=new SanityCheckerHelper(helper);
        sch12.addMapping(ExperimentBean.class,"select e.ac " +
                                              "from ia_controlledvocab c, ia_experiment e " +
                                              "where c.ac=e.detectmethod_ac and  " +
                                              "e.ac = ? and " +
                                              "c.ac in (select ac " +
                                              "from ia_controlledvocab " +
                                              "where objclass = '" + CvInteraction.class.getName() + "')");

        this.sch13=new SanityCheckerHelper(helper);
        sch13.addMapping(ExperimentBean.class,"select e.ac " +
                                              "from ia_controlledvocab c, ia_experiment e " +
                                              "where c.ac=e.identmethod_ac and  " +
                                              "e.ac = ? and " +
                                              "c.ac in (select ac " +
                                              "from ia_controlledvocab " +
                                              "where objclass = '" + CvIdentification.class.getName() + "')");

        cvTopics = new HashMap();
        this.annotationTopic = new SanityCheckerHelper(helper);
        annotationTopic.addMapping(ControlledvocabBean.class,"select shortlabel, ac, objclass from ia_controlledvocab where objclass = '"+ CvTopic.class.getName() +"' and ac like ?");

        List cvTopicBeans = annotationTopic.getBeans(ControlledvocabBean.class,"%");
        for (int i = 0; i < cvTopicBeans.size(); i++) {
            ControlledvocabBean cvBean =  (ControlledvocabBean) cvTopicBeans.get(i);
            cvTopics.put(cvBean.getAc(), cvBean.getShortlabel());
        }

        this.sch = new SanityCheckerHelper(helper);
        sch.addMapping(ControlledvocabBean.class, "SELECT ac, objclass FROM ia_controlledvocab WHERE shortlabel = ?");
        List cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.ON_HOLD);
        this.onHoldCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.TO_BE_REVIEWED);
        toBeReviewedCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.ACCEPTED);
        acceptedCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvDatabase.NEWT);
        newtDatabaseCvBean = (ControlledvocabBean) cvs.get(0);

        cvs = sch.getBeans(ControlledvocabBean.class,CvTopic.NON_UNIPROT);
        noUniprotUpdateCvBean = (ControlledvocabBean) cvs.get(0);

        sch.addMapping(ControlledvocabBean.class, "SELECT ac, objclass FROM ia_controlledvocab WHERE ac IN ( SELECT parent_ac FROM ia_xref WHERE primaryid = ? )" );
        neutralCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0497").get(0);
        baitCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0496" ).get(0);
        preyCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0498" ).get(0);
        enzymeCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0501" ).get(0);
        enzymeTargetCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0502" ).get(0);
        selfCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0503" ).get(0);
        unspecifiedCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0499" ).get(0);

        uniprotDatabaseCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class,"MI:0486" ).get(0);

        pubmedDatabaseCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class,"MI:0446" ).get(0);

        primaryReferenceXrefQualifierCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class,"MI:0358" ).get(0);

        identityXrefQualifierCvBean = (ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0356" ).get(0);

        cTerminalCvBean=(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0334" ).get(0);

        nTerminalCvBean=(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0340" ).get(0);

        undeterminedCvBean=(ControlledvocabBean) sch.getBeans(ControlledvocabBean.class, "MI:0339" ).get(0);


        this.onHoldSch = new SanityCheckerHelper(helper);
        onHoldSch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac "+
                                                  "FROM ia_exp2annot where experiment_ac = ? "+
                                                  "AND annotation_ac IN "+
                                                  "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");
        onHoldSch.addMapping(Int2AnnotBean.class, "SELECT interactor_ac "+
                                                  "FROM ia_int2annot where interactor_ac = ? "+
                                                  "AND annotation_ac IN "+
                                                  "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");

        this.toBeReviewedSch =  new SanityCheckerHelper(helper);
        toBeReviewedSch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac "+
                                                        "FROM ia_exp2annot where experiment_ac = ? "+
                                                        "AND annotation_ac IN "+
                                                        "(SELECT ac FROM ia_annotation WHERE topic_ac='" + toBeReviewedCvBean.getAc() +"')");

        noUniprotUpdateSch= new SanityCheckerHelper(helper);
        noUniprotUpdateSch.addMapping(Exp2AnnotBean.class, "SELECT interactor_ac "+
                                                           "FROM ia_int2annot where interactor_ac = ? "+
                                                           "AND annotation_ac IN "+
                                                           "(SELECT ac FROM ia_annotation WHERE topic_ac='" + noUniprotUpdateCvBean.getAc() +"')");

        sch.addMapping(Int2AnnotBean.class, "SELECT interactor_ac "+
                                            "FROM ia_int2annot where interactor_ac = ? "+
                                            "AND annotation_ac IN "+
                                            "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");

        sch.addMapping(Exp2AnnotBean.class, "SELECT experiment_ac "+
                                            "FROM ia_exp2annot where experiment_ac = ? "+
                                            "AND annotation_ac IN "+
                                            "(SELECT ac FROM ia_annotation WHERE topic_ac='" + onHoldCvBean.getAc() +"')");

        sch.addMapping(Int2ExpBean.class, "SELECT experiment_ac " +
                                          "FROM ia_int2exp " +
                                          "WHERE interaction_ac = ?");

        sch.addMapping(InteractorBean.class, "SELECT ac, objclass, userstamp, timestamp " +
                                             "FROM ia_interactor " +
                                             "WHERE ac = ? " +
                                             "AND interactiontype_ac IS NULL");

        sch.addMapping(ComponentBean.class,"SELECT interactor_ac, role, stoichiometry " +
                                           "FROM ia_component " +
                                           "WHERE interaction_ac = ? ");

        sch.addMapping(ExperimentBean.class,"SELECT biosource_ac, ac, shortlabel, userstamp, timestamp " +
                                            "FROM ia_experiment " +
                                            "WHERE ac like ?");

        sch.addMapping(XrefBean.class, "SELECT ac, database_ac, qualifier_ac, userstamp, timestamp "+
                                       "FROM ia_xref "+
                                       "WHERE parent_ac = ?");

        sch.addMapping(BioSourceBean.class,"SELECT ac, shortlabel, taxid, userstamp, timestamp " +
                                           "FROM ia_biosource " +
                                           "WHERE ac like ?");

        sch.addMapping(SequenceChunkBean.class, "select sequence_chunk, sequence_index " +
                                                "from ia_sequence_chunk " +
                                                "where parent_ac = ? " +
                                                "order by sequence_index");

        superCuratedSch = new SanityCheckerHelper(helper);
        superCuratedSch.addMapping(ExperimentBean.class,"select ac, userstamp, timestamp, shortlabel from ia_experiment where ac not in " +
	                                                        "(select e.ac " +
	                                                            "from ia_experiment e, ia_exp2annot e2a, ia_annotation a " +
	                                                            "where e.ac=e2a.experiment_ac and " +
	                                                            "e2a.annotation_ac=a.ac and " +
	                                                            "a.topic_ac in ('"+acceptedCvBean.getAc()+"','"+toBeReviewedCvBean.getAc()+"')) "+
                                                                "and to_date(created,'DD-MON-YYYY HH24:MI:SS') >  to_date('01-Sep-2005:00:00:00','DD-MON-YYYY:HH24:MI:SS') and ac like ? ");


        messageSender = new MessageSender();

        annotationSection = new AnnotationSection();


    }

    public boolean interactionIsOnHold(String ac ) throws SQLException {
        boolean onHold=false;
        List int2AnnotBeans = onHoldSch.getBeans(Int2AnnotBean.class,ac);
        if(!int2AnnotBeans.isEmpty()){
            onHold=true;
        }

        return onHold;
    }

    /**
     * This function search if this experiment is associated to any on-hold annotation. If any, onHold is set to true
     *
     * @param ac The string corresponding to the ac of an experiment
     * @return Return the boolean true if the experiment is on hold and false if it is not on hold
     * @throws SQLException
     */
    public boolean experimentIsOnHold(String ac ) throws SQLException {
        boolean onHold=false;
        List exp2AnnotBeans = onHoldSch.getBeans(Exp2AnnotBean.class,ac);
        if(!exp2AnnotBeans.isEmpty()){
            onHold=true;
        }
        return onHold;
    }

    /**
     * This function search if a protein (interactor having ProteinImpl as objclass) is associated to a no-uniprot-update
     * annotation. If it is, the boolean noUniprotUpdate is set to true
     * @param ac corresponding to a protein
     * @return Return the boolean true if the protein is a no-uniprot-update and false if it is not
     * @throws SQLException
     */

    public boolean noUniprotUpdate(String ac) throws SQLException {
        boolean noUniprotUpdate=false;
        List exp2AnnotBeans = noUniprotUpdateSch.getBeans(Exp2AnnotBean.class,ac);
        if(!exp2AnnotBeans.isEmpty()){
            noUniprotUpdate=true;
        }
        return noUniprotUpdate;

    }

    /**
     * This function search if this experiment is associated to any to-be-reviewed annotation. If any, onHold is set
     * to true
     *
     * @param ac The string corresponding to the ac of an experiment
     * @return Return the boolean true if the experiment is to-be-reviewed and false if it is not.
     * @throws SQLException
     */

    public boolean experimentToBeReviewed(String ac ) throws SQLException {
        boolean toBeReviewed=false;
        List exp2AnnotBeans = toBeReviewedSch.getBeans(Exp2AnnotBean.class,ac);
        if(!exp2AnnotBeans.isEmpty()){
            toBeReviewed=true;
        }
        return toBeReviewed;
    }


    /**
     * Performs Interaction checks
     *
     * @param interactorBeans List containing interactorBean having objclass equal to InteractionImpl
     * @throws IntactException if there was a search problem
     * @throws SQLException
     */

    public void checkInteractionsBaitAndPrey( List interactorBeans ) throws IntactException, SQLException {
        System.out.println( "Checking on Interactions (rule 6) ..." );

        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactionBean =  (InteractorBean) interactorBeans.get(i);
            String interactionAc =  interactionBean.getAc();

            if(false == interactionIsOnHold(interactionAc)){
                List componentBeans = sch.getBeans(ComponentBean.class,interactionAc);
                int preyCount = 0,
                        baitCount = 0,
                        enzymeCount = 0,
                        enzymeTargetCount = 0,
                        neutralCount = 0,
                        selfCount = 0,
                        unspecifiedCount = 0;
                float selfStoichiometry = 0;
                float neutralStoichiometry = 0;

                for (int j = 0; j < componentBeans.size(); j++) {
                    ComponentBean componentBean =  (ComponentBean) componentBeans.get(j);

                    if ( baitCvBean.getAc().equals( componentBean.getRole() ) ) {
                        baitCount++;
                    } else if ( preyCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        preyCount++;
                    } else if ( enzymeCvBean.getAc().equals( componentBean.getRole() ) ) {
                        enzymeCount++;
                    } else if ( enzymeTargetCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        enzymeTargetCount++;
                    } else if ( neutralCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        neutralCount++;
                        neutralStoichiometry = componentBean.getStoichiometry();
                    } else if ( selfCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        selfCount++;
                        selfStoichiometry = componentBean.getStoichiometry();
                    } else if ( unspecifiedCvBean.getAc().equals( componentBean.getRole()  ) ) {
                        unspecifiedCount++;
                    }
                }

                int baitPrey = ( baitCount + preyCount > 0 ? 1 : 0 );
                int enzymeTarget = ( enzymeCount + enzymeTargetCount > 0 ? 1 : 0 );
                int neutral = ( neutralCount > 0 ? 1 : 0 );
                int self = ( selfCount > 0 ? 1 : 0 );
                int unspecified = ( unspecifiedCount > 0 ? 1 : 0 );

                // count the number of categories used.
                int categoryCount = baitPrey + neutral + enzymeTarget + self + unspecified;

                switch ( categoryCount ) {
                    case 0:
                        // none of those categories
                        System.out.println("Interaction " +interactionAc + " with no categories");
                        messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_CATEGORIES, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean));//, editorUrlBuilder.getEditorUrl(interationBean));
                        break;

                    case 1:
                        // exactly 1 category
                        if ( baitPrey == 1 ) {
                            // bait-prey
                            if ( baitCount == 0 ) {
                                System.out.println("Interaction " +interactionAc + "  with no bait");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_BAIT, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                            } else if ( preyCount == 0 ) {
                                System.out.println("Interaction " +interactionAc + "  with no prey");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_PREY, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                            }

                        } else if ( enzymeTarget == 1 ) {
                            // enzyme - enzymeTarget
                            if ( enzymeCount == 0 ) {
                                System.out.println("Interaction  " +interactionAc + " with no enzyme");

                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_ENZYME, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean));
                            } else if ( enzymeTargetCount == 0 ) {
                                System.out.println("Interaction " +interactionAc + "  with no enzyme target");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_ENZYME_TARGET, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean));
                            }

                        } else if ( self == 1 ) {
                            // it has to be > 1
                            if ( selfCount > 1 ) {
                                System.out.println("Interaction " +interactionAc + "  with more then 2 self protein");
                                messageSender.addMessage( ReportTopic.INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN, interactionBean);//,editorUrlBuilder.getEditorUrl(interactionBean));
                            } else { // = 1
                                if ( selfStoichiometry < 1F ) {
                                    System.out.println("Interaction " +interactionAc + "  self protein and stoichiometry lower than 2");
                                    //  messageSender.addMessage( ReportTopic.INTERACTION_WITH_SELF_PROTEIN_AND_STOICHIOMETRY_LOWER_THAN_2, interactionBean);
                                }
                            }

                        } else {
                            // neutral
                            if ( neutralCount == 1 ) {
                                if ( neutralStoichiometry < 2 ) {
                                    System.out.println("Interaction  " +interactionAc + "  with only one neutral");
                                    messageSender.addMessage( ReportTopic.INTERACTION_WITH_ONLY_ONE_NEUTRAL, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean));
                                }
                            }
                        }
                        break;

                    default:
                        // > 1 : mixed up categories !
                        System.out.println("Interaction  " +interactionAc + "  with mixed component categories");
                        messageSender.addMessage( ReportTopic.INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean));
                } // switch
            }
        }
        //check 7
    }



    public void experimentNotSuperCurated() throws SQLException, IntactException {
        List ExperimentBeans = superCuratedSch.getBeans(ExperimentBean.class, "%" );
        for (int i = 0; i < ExperimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) ExperimentBeans.get(i);
            messageSender.addMessage(ReportTopic.EXPERIMENT_NOT_ACCEPTED_NOT_TO_BE_REVIEWED,experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean));
    }
    }

    /**
     * Performs Interaction checks (Check if the interaction has components)
     * @param interactorBeans List containing interactorBean having objclass equal to InteractionImpl
     * @throws SQLException
     */
    public void checkComponentOfInteractions( List interactorBeans ) throws SQLException, IntactException {
        System.out.println( "Checking on Components (rules 5 and 6) ..." );

        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactionBean =  (InteractorBean) interactorBeans.get(i);
            String interactionAc =  interactionBean.getAc();


            if(false == interactionIsOnHold(interactionAc)){
                List componentBeans = sch.getBeans(ComponentBean.class,interactionAc);

                if ( componentBeans.size() == 0 ) {
                    //Interaction has no Components!! This is in fact test 5...
                    System.out.println("Interaction has no component");
                    messageSender.addMessage( ReportTopic.NO_PROTEIN_CHECK, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                }
            }
        }
    }


    /**
     * Check if an interactor is a Protein and not an Interaction
     * @param interactorAc String corresponding to an interactor ac
     * @return True if the interactor is a protein, false if it is not
     * @throws SQLException
     */

    public boolean isInstanceOfProtein(String interactorAc) throws SQLException {
        boolean instanceOfProtein = false;
        InteractorBean interactorBean = (InteractorBean) sch.getBeans(InteractorBean.class,interactorAc).get(0);
        if(Protein.class.equals(interactorBean.getObjclass())){
            instanceOfProtein=true;
        }
        return instanceOfProtein;
    }

    /**
     * Performs protein check
     * @param interactorBeans List containing interactorBean having objclass equal to ProteinImpl
     * @throws SQLException
     */

    public void checkProtein( List interactorBeans ) throws SQLException, IntactException {

        //System.out.println( "Checking on Proteins (rules 14 and 16) ..." );

        //checks 14
        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean proteinBean =  (InteractorBean) interactorBeans.get(i);
            List xrefBeans = sch.getBeans(XrefBean.class, proteinBean.getAc());
            int count=0;
            for (int j = 0; j < xrefBeans.size(); j++) {
                XrefBean xrefBean = (XrefBean) xrefBeans.get(j);
                if ( uniprotDatabaseCvBean.getAc().equals( xrefBean.getDatabase_ac() ) && identityXrefQualifierCvBean.getAc().equals(xrefBean.getQualifier_ac())) {
                    count++;
                }
            }
            if (count == 0){
                if(!noUniprotUpdate(proteinBean.getAc())){
                    messageSender.addMessage( ReportTopic.PROTEIN_WITH_NO_UNIPROT_IDENTITY, proteinBean);//, editorUrlBuilder.getEditorUrl(proteinBean) );
                }
            } else if ( count > 1 ) {
                messageSender.addMessage( ReportTopic.PROTEIN_WITH_MORE_THAN_ONE_UNIPROT_IDENTITY, proteinBean);//, editorUrlBuilder.getEditorUrl(proteinBean) );
            }
        }
    }

    /**
     * Performs protein check
     * @param interactorBeans List containing interactorBean having objclass equal to ProteinImpl
     * @throws SQLException
     */

    public void checkInteractionsComplete(List interactorBeans) throws SQLException, IntactException {
        System.out.println( "Checking on Interactions (rule 7) ..." );

        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactionBean =  (InteractorBean) interactorBeans.get(i);
            String interactionAc =  interactionBean.getAc();

            if(false == interactionIsOnHold(interactionAc)){

                //check 7
                if ( sch.getBeans(Int2ExpBean.class,interactionAc).isEmpty() ) {
                    //record it.....
                    System.out.println("Interaction "+interactionAc + " with no experiment");
                    messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_EXPERIMENT, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                }
                //check 9
                if (false == sch.getBeans(InteractorBean.class, interactionAc).isEmpty() ) {
                    System.out.println("Interaction "+interactionAc + " has no interaction type");

                    messageSender.addMessage( ReportTopic.INTERACTION_WITH_NO_CVINTERACTIONTYPE, interactionBean);//, editorUrlBuilder.getEditorUrl(interactionBean) );
                }
                //check 10
                // 2005-04-14: on-hold ... might be re-introduced later.
//                if( interaction.getBioSource() == null ) {
//                    addMessage( INTERACTION_WITH_NO_ORGANISM, interaction );
//                }

            }

        }

    }


    /**
     * Performs checks on Experiments.
     * @param experimentBeans  List containing experiment beans
     * @throws IntactException
     * @throws SQLException
     */
    public void checkExperiment( List experimentBeans ) throws IntactException, SQLException {

        for (int i = 0; i < experimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) experimentBeans.get(i);

            if ( !experimentIsOnHold( experimentBean.getAc() ) ) {

                sch.addMapping(ExperimentBean.class, "select e.ac from ia_experiment e, ia_int2exp i  where e.ac = ? and e.ac=i.experiment_ac");// in (SELECT experiment_ac FROM ia_int2exp)");

                //check 8
                if(sch.getBeans(ExperimentBean.class, experimentBean.getAc()).isEmpty()){
                    messageSender.addMessage(ReportTopic.EXPERIMENT_WITHOUT_INTERACTIONS, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }

                //check 11
                if (experimentBean.getBiosource_ac()==(null)){
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_ORGANISM, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }

                //check 12
                if(sch12.getBeans(ExperimentBean.class, experimentBean.getAc()).isEmpty()){
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_CVINTERACTION, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }
                //check 13
                if(sch13.getBeans(ExperimentBean.class, experimentBean.getAc()).isEmpty()){
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_CVIDENTIFICATION, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }
            }
        }

    }

    /**
     * Performs check on experiments
     * @param experimentBeans List containing experiment beans
     * @throws IntactException
     * @throws SQLException
     */
    public void checkExperimentsPubmedIds( List experimentBeans ) throws IntactException, SQLException {

        for (int i = 0; i < experimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) experimentBeans.get(i);
            if ( !experimentIsOnHold(experimentBean.getAc()) ) {
                int pubmedCount = 0;
                int pubmedPrimaryCount = 0;
                List xrefBeans = sch.getBeans(XrefBean.class,experimentBean.getAc());
                for (int j = 0; j < xrefBeans.size(); j++) {
                    XrefBean xrefBean = (XrefBean) xrefBeans.get(j);
                    System.out.println("xref cvDb = " + xrefBean.getDatabase_ac());
                    System.out.println("xref qualifier = " + xrefBean.getQualifier_ac());
                    System.out.println();

                    if ( pubmedDatabaseCvBean.getAc().equals( xrefBean.getDatabase_ac() ) ) {
                        pubmedCount++;
                        if ( primaryReferenceXrefQualifierCvBean.getAc().equals( xrefBean.getQualifier_ac() ) ) {
                            pubmedPrimaryCount++;
                        }
                    }
                }
                if ( pubmedCount == 0 ) {
                    //record it.....
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_PUBMED, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }

                if ( pubmedPrimaryCount < 1 ) {
                    //record it.....
                    messageSender.addMessage( ReportTopic.EXPERIMENT_WITHOUT_PUBMED_PRIMARY_REFERENCE, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean) );
                }
            }// experimentIsOnHold
        }

    }

    /**
     * Checks We have so far: -----------------------
     * <p/>
     * 1.  Any Experiment lacking a PubMed ID 2.  Any PubMed ID in Experiment DBXref without qualifier=Primary-reference
     * 3.  Any Interaction containing a bait but not a prey protein 4.  Any Interaction containing a prey but not a bait
     * protein 5.  Any interaction with no protein attached 6.  Any interaction with 1 protein attached, stoichiometry=1
     * 7.  Any Interaction missing a link to an Experiment 8.  Any experiment (not on hold) with no Interaction linked
     * to it 9.  Any interaction missing CvInteractionType 10. Any interaction missing Organism 11. Any experiment (not
     * on hold) missing Organism 12. Any experiment (not on hold) missing CvInteraction 13. Any experiment (not on hold)
     * missing CvIdentification 14. Any proteins with no Xref with XrefQualifier(identity) and CvDatabase(uniprot) 15.
     * Any BioSource with a NULL or empty taxid. 16. Any proteins with more than one Xref with XrefQualifier(identity)
     * and CvDatabase(uniprot)
     * <p/>
     * To perform these checks we need to enhance the Helper/persistence code to handle more complex queries, ie to be
     * able to build Criteria and Query objects probably used in OJB (easiest to do). This is going to be needed anyway
     * so that we can handle more complex search queries later....
     */

    public void checkBioSource( List bioSourceBeans ) throws IntactException, SQLException {

        //check 15
        for (int i = 0; i < bioSourceBeans.size(); i++) {
            BioSourceBean bioSourceBean =  (BioSourceBean) bioSourceBeans.get(i);
            if ( bioSourceBean.getTaxid() == null || "".equals( bioSourceBean.getTaxid() ) ) {
                messageSender.addMessage( ReportTopic.BIOSOURCE_WITH_NO_TAXID, bioSourceBean);//, editorUrlBuilder.getEditorUrl(bioSourceBean) );
            }
        }

    }


    public void duplicatedProtein (XrefBean xrefBean) throws SQLException {

        List interactorBeans = sch.getBeans(InteractorBean.class, xrefBean.getPrimaryid());
        List bioSourceAc = new ArrayList();
        List duplicatedInteractorBeans = new ArrayList();


        InteractorBean firstInteractor =  (InteractorBean) interactorBeans.get(0);
        bioSourceAc.add(firstInteractor.getBiosource_ac());
        duplicatedInteractorBeans.add(firstInteractor);

        for (int i = 1; i < interactorBeans.size(); i++) {
            InteractorBean interactorBean =  (InteractorBean) interactorBeans.get(i);
            if(bioSourceAc.contains(interactorBean.getBiosource_ac())){
                duplicatedInteractorBeans.add(interactorBean);
                System.out.println("Duplicated prot, interactor_ac = " + interactorBean.getAc());
            }else{
                bioSourceAc.add(interactorBean.getBiosource_ac());
            }
        }

        if(duplicatedInteractorBeans.size()>1){
            messageSender.addMessage(ReportTopic.DUPLICATED_PROTEIN,duplicatedInteractorBeans);
            duplicProt = duplicProt+duplicatedInteractorBeans.size();
        }


    }

    /**
     * This method check whether the url contained in the annotation are valide. To do so, it uses the HttpClient
     * library (commons-httpclient-3.0-rc2.ja, http://jakarta.apache.org/commons/httpclient). This library depends on
     * two other libraries ( commons-codec-1.4-dev.jar and junit.jar). If the url is not valide then it retrieves the
     * first object this annotation is linked to and send the message to the owner of this object. This is to avoid 100
     * errors to be sent if the same url is linked to 100 objects.
     * @param annotationBeans List of annotationBean linked to the cvTopic with shortlabel "url"
     * @throws SQLException
     * @throws IntactException
     */

    public void checkURL(List annotationBeans) throws SQLException, IntactException {//Collection annotations){

        for (int i = 0; i < annotationBeans.size(); i++) {
            AnnotationBean annotationBean =  (AnnotationBean) annotationBeans.get(i);

            String urlString=annotationBean.getDescription();

            HttpURL httpUrl= null;


            //Creating the httpUrl object corresponding to the the url string contained in the annotation
            try {
                httpUrl = new HttpURL(urlString);
            } catch (URIException e) {
                // e.printStackTrace();
                //retrieveObject(annotationBean);
                messageSender.addMessage(annotationBean);
                //System.out.println("couldn't create httpURL uri" + urlString);
            }

            // If httpUrl is not null, get the method corresponding to the uri, execute if and analyze the
            // status code to know whether the url is valide or not.
            if(httpUrl!=null){
                HttpClient client = new HttpClient();
                HttpMethod method=null;
                try{
                    method = new GetMethod(urlString);
                }catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                    //System.out.println("Couldn't get method uri" + urlString);
                    //retrieveObject(annotationBean);
                    messageSender.addMessage(annotationBean);
                }
                int statusCode = -1;
                if(method!=null){
                    try {
                        statusCode = client.executeMethod(method);
                    } catch (IOException e) {
                        //retrieveObject(annotationBean);
                        messageSender.addMessage(annotationBean);
                    }
                    if(statusCode!=-1){
                        if(statusCode >= 300 && statusCode <600) {
                            //retrieveObject(annotationBean);
                            messageSender.addMessage(annotationBean);
                        }
                    }
                }
            }

        }
    }

//    /**
//     * Permit to retrieve to which object is link an annotation link
//     * @param annotationBean
//     * @throws IntactException
//     * @throws SQLException
//     */
//    public void retrieveObject(AnnotationBean annotationBean) throws IntactException, SQLException {
//
//
//        List exp2AnnotBeans = retrieveObjectSch.getBeans(Exp2AnnotBean.class, annotationBean.getAc());
//        List bs2AnnotBeans = retrieveObjectSch.getBeans(Bs2AnnotBean.class, annotationBean.getAc());
//        List int2AnnotBeans = retrieveObjectSch.getBeans(Int2AnnotBean.class, annotationBean.getAc());
//        List cvObject2AnnotBeans = retrieveObjectSch.getBeans(CvObject2AnnotBean.class, annotationBean.getAc());
//        List feature2AnnotBeans = retrieveObjectSch.getBeans(Feature2AnnotBean.class, annotationBean.getAc());
//
//        if(!exp2AnnotBeans.isEmpty() ){
//            messageSender.annotationMessage(exp2AnnotBeans,annotationBean);
//        } else if(!bs2AnnotBeans.isEmpty() ){
//            messageSender.annotationMessage(bs2AnnotBeans,annotationBean);
//        } else if(!int2AnnotBeans.isEmpty() ){
//            messageSender.annotationMessage(int2AnnotBeans,annotationBean);
//        } else if(!cvObject2AnnotBeans.isEmpty()){
//            messageSender.annotationMessage(cvObject2AnnotBeans,annotationBean);
//        } else if(!feature2AnnotBeans.isEmpty() ){
//            messageSender.annotationMessage(feature2AnnotBeans,annotationBean);
//        }
//    }

    /**
     * This function retrieve all the sequence chunk corresponding to a protein and associate them in the good order to
     * rebuild the sequence
     *
     * @param proteinAc String ==> ac of a protein
     * @return
     * @throws SQLException
     */

    public String getProteinSequence(String proteinAc) throws SQLException {
        String sequence = new String();
        List sequenceChunkBeans = sch.getBeans(SequenceChunkBean.class,proteinAc);
        for (int i = 0; i < sequenceChunkBeans.size(); i++) {
            SequenceChunkBean sequenceChunkBean =  (SequenceChunkBean) sequenceChunkBeans.get(i);
            sequence=sequence + sequenceChunkBean.getSequence_chunk();
        }
        return sequence;
    }

    /**
     * Performs test on experiment (check if an experiment is to-be-reviewed or not)
     * @param experimentBeans
     * @throws SQLException
     */

    public void checkReviewed(List experimentBeans) throws SQLException, IntactException {

        for (int i = 0; i < experimentBeans.size(); i++) {
            ExperimentBean experimentBean =  (ExperimentBean) experimentBeans.get(i);
            if(experimentIsOnHold(experimentBean.getAc())){
                messageSender.addMessage( ReportTopic.EXPERIMENT_ON_HOLD, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean));
            }
            if(experimentToBeReviewed(experimentBean.getAc())){
                messageSender.addMessage(ReportTopic.EXPERIMENT_TO_BE_REVIEWED, experimentBean);//, editorUrlBuilder.getEditorUrl(experimentBean));
            }
        }
    }

    /**
     * Check if the crc64 stored in the database still corresponds to the crc64 calculated from the protein sequence
     * stored in the database
     * @param interactorBeans
     * @throws SQLException
     */
    public void checkCrc64(List interactorBeans) throws SQLException, IntactException {
        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactorBean =  (InteractorBean) interactorBeans.get(i);
            String sequence = getProteinSequence(interactorBean.getAc());
            String crc64Stored=interactorBean.getCrc64();
            if(sequence!=null && false==sequence.trim().equals("")){
                String crc64Calculated=Crc64.getCrc64(sequence);
                if(!crc64Calculated.equals(crc64Stored)){
                    messageSender.addMessage(ReportTopic.PROTEIN_WITH_WRONG_CRC64, interactorBean);//, editorUrlBuilder.getEditorUrl(interactorBean));
                }
            }
            else{
                if(crc64Stored!=null){
                    messageSender.addMessage(ReportTopic.PROTEIN_WITHOUT_A_SEQUENCE_BUT_WITH_AN_CRC64,interactorBean);//, editorUrlBuilder.getEditorUrl(interactorBean));
                }
            }

        }

    }

    /**
     * This method check that all biosource have at least one xref with a "Newt" taxid AND
     * with the xref qualifier equal to "identity"
     * It will not send any message when the taxid is < 0 because those are particular biosources
     * to tell that those experiments were done "in vitro" or that it was a "chemical synthesis"
     * (EBI-1318 or EBI-350168)
     *
     * @param bioSourceBeans A collection containing bioSources
     * @throws IntactException
     * @throws SQLException
     */
    public void checkNewt(List bioSourceBeans) throws IntactException, SQLException {

        System.out.println("Checking bioSource (existing newt identity xref) :");

        for (int i = 0; i < bioSourceBeans.size(); i++) {
            boolean hasNewtXref = false;
            BioSourceBean bioSourceBean = (BioSourceBean) bioSourceBeans.get(i);
            List xrefs = sch.getBeans(XrefBean.class, bioSourceBean.getAc());
            for (int j = 0; j < xrefs.size(); j++) {
                XrefBean xrefBean =  (XrefBean) xrefs.get(j);
                String xrefQualifier = xrefBean.getQualifier_ac().trim();
                String databaseAc = xrefBean.getDatabase_ac().trim();
                if(newtDatabaseCvBean.getAc().equals(databaseAc) && identityXrefQualifierCvBean.getAc().equals(xrefQualifier)){
                    hasNewtXref=true;
                }
            }
            if(false==hasNewtXref){
                int taxid = Integer.parseInt(bioSourceBean.getTaxid());
                if(taxid>=1){
                    messageSender.addMessage(ReportTopic.BIOSOURCE_WITH_NO_NEWT_XREF,bioSourceBean, editorUrlBuilder.getEditorUrl(bioSourceBean));
                }
            }
        }

    }

    /**
     * This check if there is any interaction linked to more then one experiment if any it will send a message
     * The sql request is the following one :
     *      select interaction_ac, experiment_ac
     *      from ia_int2exp
     *      where interaction_ac like '%' and
     *      interaction_ac in ( select interaction_ac
     *                          from ia_int2exp
     *                          group by interaction_ac
     *                          having count(experiment_ac) > 1)
     *
     * For exemple it can return a list of Int2ExpBean like that :
     *      INTERACTION_AC          EXPERIMENT_AC
     *      EBI-367255              EBI-367251
     *      EBI-367255              EBI-79369
     *      EBI-520663              EBI-495409
     *      EBI-520663              EBI-495685
     *
     * For each line you check if the interaction_ac was the same then for the previous line, if it was you add the
     * the experimentBean to the experimentBeans list corresponding to this interaction_ac.
     * If it is not the same (and if it is not the first line), you ask the messageSender to send an error message giving
     * in parameter the interactionBean and the list of all ExperimentBeans.
     *
     * You obtain this kind of message :
     *
     *      Interaction linked to more then one experiment
     *      ----------------------------------------------
     *      AC: EBI-367255	 Shortlabel: tra_2-tra_4	 User: INTACT	 When: 2004-06-15 15:37:50.0
     *          AC: EBI-79369		 Shortlabel: wang-2000-3		 User: SKERRIEN		 When: 2003-12-05 14:26:41.0
     *          AC: EBI-367251		 Shortlabel: wang-2001-3		 User: SMUDALI		 When: 2004-06-15 15:34:38.0
     *      AC: EBI-520663	 Shortlabel: mdc1-brca1-1	 User: INTACT	 When: 2005-03-21 10:08:52.0
     *          AC: EBI-495685		 Shortlabel: stewart-2003-4		 User: SMUDALI		 When: 2005-02-24 16:16:11.0
     *          AC: EBI-495409		 Shortlabel: stewart-2003-1		 User: ABRIDGE		 When: 2005-02-24 14:23:33.0
     *
     *
     * @throws SQLException
     */

    public void checkOneIntOneExp() throws SQLException {
        List int2ExpBeans = oneIntOneExpSch.getBeans(Int2ExpBean.class, "%");

        List experimentBeans = new ArrayList();
        InteractorBean interactionBean=new InteractorBean();
        String interactionAc="";

        for (int i = 0; i < int2ExpBeans.size(); i++) {
            Int2ExpBean int2ExpBean =  (Int2ExpBean) int2ExpBeans.get(i);
            String currentInteractionAc = int2ExpBean.getInteraction_ac();
            String currentExperimentAc = int2ExpBean.getExperiment_ac();
            if(!interactionAc.equals(currentInteractionAc)){
                if(!interactionAc.equals("")){
                    messageSender.addMessage(ReportTopic.INTERACTION_LINKED_TO_MORE_THEN_ONE_EXPERIMENT,interactionBean,experimentBeans);
                }
                interactionBean=retrieveInteractorFromAc(oneIntOneExpSch,currentInteractionAc);
                experimentBeans.clear();
                experimentBeans.add(retrieveExperimentFromAc(oneIntOneExpSch, currentExperimentAc));
                interactionAc=currentInteractionAc;
            }
            else{
                experimentBeans.add(retrieveExperimentFromAc(oneIntOneExpSch, currentExperimentAc));
            }
        } //end of for on int2ExpBeans list
        if(!int2ExpBeans.isEmpty()){
            messageSender.addMessage(ReportTopic.INTERACTION_LINKED_TO_MORE_THEN_ONE_EXPERIMENT,interactionBean,experimentBeans);
        }
    }

    public ExperimentBean retrieveExperimentFromAc (SanityCheckerHelper helper, String ac) throws SQLException {
        ExperimentBean experimentBean = new ExperimentBean();

        List experimentBeans = objectFromAc.getBeans(ExperimentBean.class, ac);
        for (int i = 0; i < experimentBeans.size(); i++) {
            experimentBean =  (ExperimentBean) experimentBeans.get(i);
        }
        return experimentBean;
    }

    public InteractorBean retrieveInteractorFromAc (SanityCheckerHelper helper, String ac) throws SQLException {
        InteractorBean interactorBean = new InteractorBean();
        List interactorBeans = objectFromAc.getBeans(InteractorBean.class, ac);
        for (int i = 0; i < interactorBeans.size(); i++) {
            interactorBean =  (InteractorBean) interactorBeans.get(i);
        }
        return interactorBean;
    }

    /**
     * @param protSeq
     * @param rangeBean
     * @return
     * @throws SQLException
     * @throws IntactException
     */
    public String generateNormalRangeSeq(String protSeq, RangeBean rangeBean) throws SQLException, IntactException {
        IntactHelper helperBis;
        String normalRangeSeq = new String();
        try{
        helperBis = new IntactHelper();

        if(protSeq!=null){

            String fromFuzzyTypeAc=  rangeBean.getFromfuzzytype_ac();

            /*
            * If fromFuzzyTypeAc == nTer or undetermined, the rule is that the range sequence corresponds to the
            * first N nucleotides.
            */
            if(nTerminalCvBean.getAc().equals(fromFuzzyTypeAc) || undeterminedCvBean.getAc().equals(fromFuzzyTypeAc)){
                if(protSeq.length()<100){
                    normalRangeSeq=protSeq;
                }else{
                    normalRangeSeq=protSeq.substring(0,100);
                }
            }
            /*
            * If fromFuzzyTypeAc == cTer,  the rule is that the range corresponds to the first N nucleotides.
            */
            else if (cTerminalCvBean.getAc().equals(fromFuzzyTypeAc)){
                if(protSeq.length()<100){
                    normalRangeSeq=protSeq;
                }else{
                    normalRangeSeq=protSeq.substring(protSeq.length()-100,protSeq.length());
                }
            }
            /*
            * Otherwise, the rule is that the range sequence must corresponds to the first N nucleotides starting
            * from "fromIntervalStart"
            */
            else{
                Range range = (Range) helperBis.getObjectByAc(Range.class,rangeBean.getAc());
                int fromIntervalStart=range.getFromIntervalStart();
                if(fromIntervalStart+100 > protSeq.length()){
                    /*
                    *                    1 2 3 4 5 6 7 8 ...etc
                    * Protein sequence : M S D E G A D K S L D T N T E F I I Q T R S R R S N A G N K
                    * Range sequence :           G A D K S L D T N T E F I I Q T R S R R S N A G N K
                    * The rule is that the M is position 1, S position 2...etc
                    * But in programming M will be postion 0, so to take the good part of the protein sequence we
                    * have to remove 1 to fromIntervalStart
                    */
                    normalRangeSeq = protSeq.substring(fromIntervalStart-1,protSeq.length());
                }
                else{
                    if(fromIntervalStart==0){
                        normalRangeSeq =protSeq.substring(fromIntervalStart, fromIntervalStart+100);
                    }else{
                        normalRangeSeq = protSeq.substring(fromIntervalStart-1,fromIntervalStart-1+100);
                    }
                }
            }
        }
            helperBis.closeStore();
        }
        catch(IntactException e){
            e.printStackTrace();

        }

        return normalRangeSeq;
    }

    /**
     * The translation of an Rna to a protein sequence always start with a Methionine symbolized with the letter M in
     * the protein sequence. This M can either stay or be removed during the post-translation modification.
     * At first, Uniprot was storing all the sequence keeping this first M.
     * Then they decided to remoove all of them.
     * Now they decided to pub them back and to add an annotation telling weather this M is remooved or not.
     *
     * This can lead to this situation :
     *
     * Day one : creation of the feature in Intact
     *                         1 2 3 4 5 6 7 8 ...etc
     *      Protein sequence : M S D E G A D K S L D T N T E F I I Q T R S R R S N A G N K
     *      Range sequence :           G A D K S L D T N T E F I I Q T R S R R S N A G N K
     *      FromIntervalStart : 5
     *
     * Day two : update of the sequence, uniprot decide to remove the M
     *                         1 2 3 4 5 6 7 8 ...etc
     *      Protein sequence : S D E G A D K S L D T N T E F I I Q T R S R R S N A G N K
     *      As you can see : S became number 1 but was number 2 before.
     *      So if we want to re-calculate the Range sequence on day 2, we have to remoove one from the "fromIntervalStart"
     *      Therefore fromIntervalStart become 4
     *
     * @param rangeBean
     * @param protSeq
     * @return rangeSeq
     */

    public String generateRangeSeqIfMRemooved(String protSeq, RangeBean rangeBean, String normalRangeSeq) /*throws IntactException*/ {

        boolean updatedNeeded = false;
        String rangeSeq = new String();
        String rangeSeqStored = rangeBean.getSequence();
        try{
            IntactHelper helperBis = new IntactHelper();

         Range range = (Range) helperBis.getObjectByAc(Range.class,rangeBean.getAc());
          System.out.println("\n------ Range"+range.getSequence());
         if(range.getSequence()==null){
                        System.out.println("METHOD");
                        System.out.println("Range ac = "+ rangeBean.getAc()+" was null from the beginning");
                        System.out.println("range Bean ac "+rangeBean.getAc());
                        System.out.println("&&&&&&&&&&");

                    }
        if(protSeq!=null){

            String fromFuzzyTypeAc = rangeBean.getFromfuzzytype_ac();

            /*
            * If fromFuzzyTypeAc == nTer or undetermined, the rule is that the range sequence corresponds to the
            * first N nucleotides.
            */
            if(nTerminalCvBean.getAc().equals(fromFuzzyTypeAc) || undeterminedCvBean.getAc().equals(fromFuzzyTypeAc)){
                if(protSeq.length()<100){
                    rangeSeq="M"+protSeq;
                    if(rangeSeq.equals(rangeSeqStored)){
                        System.out.println("1");
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                        System.out.println("previous seq "+range.getSequence());
                        System.out.println("replace by   "+ normalRangeSeq);
                        range.setSequence(protSeq);//normalRangeSeq);
                        //helper.update(range);
                        System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                        updatedNeeded=true;
                    }
                }else{
                    rangeSeq="M"+ protSeq.substring(0,99);
                    if(rangeSeq.equals(rangeSeqStored)){
                        System.out.println("2");
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                        System.out.println("previous seq "+range.getSequence());
                        System.out.println("replace by   "+normalRangeSeq);
                        range.setSequence(protSeq);//normalRangeSeq);
                        //helper.update(range);
                         updatedNeeded=true;
                        System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                    }
                }
            }
            /*
            * If fromFuzzyTypeAc == cTer,  the rule is that the range corresponds to the first N nucleotides.
            */
            else if (cTerminalCvBean.getAc().equals(fromFuzzyTypeAc)){
                if(protSeq.length()<100){
                    rangeSeq="M"+protSeq;
                    if(rangeSeq.equals(rangeSeqStored)){
                        System.out.println("3");
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                        System.out.println("previous seq "+range.getSequence());
                        System.out.println("replace by   "+normalRangeSeq);
                        range.setSequence(protSeq);//normalRangeSeq);
                        //helper.update(range);
                         updatedNeeded=true;
                        System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                    }
                }else{
                    rangeSeq=protSeq.substring(protSeq.length()-100,protSeq.length());
                    if(rangeSeqStored.equals(rangeSeq)){
                        System.out.println("4");
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                        System.out.println("previous seq "+range.getSequence());
                        System.out.println("replace by   "+normalRangeSeq);
                        range.setSequence(protSeq);//normalRangeSeq);
                        //helper.update(range);
                         updatedNeeded=true;
                        System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                    }
                }
            }
            /*
            * Otherwise, the rule is that the range sequence must corresponds to the first N nucleotides starting
            * from "fromIntervalStart"
            */
            else{
                //Range range = (Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                int fromIntervalStart=range.getFromIntervalStart();
                if(fromIntervalStart+100 > protSeq.length()){
                    /*
                    *                    1 2 3 4 5 6 7 8 ...etc
                    * Protein sequence : M S D E G A D K S L D T N T E F I I Q T R S R R S N A G N K
                    * Range sequence :           G A D K S L D T N T E F I I Q T R S R R S N A G N K
                    * The rule is that the M is position 1, S position 2...etc
                    * But in programming M will be postion 0, so to take the good part of the protein sequence we
                    * have to remove 1 to fromIntervalStart
                    */
                    if(fromIntervalStart==0){
                        rangeSeq = "M"+protSeq.substring(0,protSeq.length()); //protSeq.substring();
                        if(rangeSeq.equals(rangeSeqStored)){
                            System.out.println("5");
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setSequence(protSeq);//normalRangeSeq);
                            //helper.update(range);
                             updatedNeeded=true;
                            System.out.println("now seq is   "+range.getSequence());
                            System.out.println("eval seq is  "+normalRangeSeq);
                        }
                    } else {
                        rangeSeq = protSeq.substring(fromIntervalStart-1,protSeq.length());
                        if(rangeSeq==rangeSeqStored){
                            System.out.println("6");
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setFromIntervalStart(fromIntervalStart-1);
                            range.setFromIntervalEnd(range.getFromIntervalEnd()-1);
                            range.setToIntervalStart(range.getToIntervalStart()-1);
                            range.setToIntervalEnd(range.getToIntervalEnd()-1);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                            System.out.println("eval seq is  "+normalRangeSeq);

                            System.out.println("updating fromIntervalStart");
                            System.out.println("previous value :"+range.getFromIntervalStart());
                            System.out.println("replace by     " + (fromIntervalStart-1));
                            /*range.setFromIntervalStart(fromIntervalStart-1);

                            range.setFromIntervalEnd(range.getFromIntervalEnd()-1);
                            range.setToIntervalStart(range.getToIntervalStart()-1);
                            range.setToIntervalEnd(range.getToIntervalEnd()-1);*/
                            //helper.update(range);
                             updatedNeeded=true;
                            System.out.println("now fromIS   is ="+range.getFromIntervalStart());
                        }
                    }
                }
                else{
                    if(fromIntervalStart==0){
                        rangeSeq = "M"+protSeq.substring(fromIntervalStart,fromIntervalStart+99);
                        if(rangeSeq.equals(rangeSeqStored)){
                            System.out.println("7");
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                            System.out.println("eval seq is  "+normalRangeSeq);
                            //helper.update(range);
                             updatedNeeded=true;
                        }
                    }else{
                        rangeSeq = protSeq.substring(fromIntervalStart-1,fromIntervalStart-1+100);
                        if(rangeSeq.equals(rangeSeqStored)){
                            System.out.println("8");
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setFromIntervalStart(fromIntervalStart-1);
                            range.setFromIntervalEnd(range.getFromIntervalEnd()-1);
                            range.setToIntervalStart(range.getToIntervalStart()-1);
                            range.setToIntervalEnd(range.getToIntervalEnd()-1);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                            System.out.println("eval seq is  "+normalRangeSeq);

                            /*range.setFromIntervalStart(fromIntervalStart-1);
                            range.setFromIntervalEnd(range.getFromIntervalEnd()-1);
                            range.setToIntervalStart(range.getToIntervalStart()-1);
                            range.setToIntervalEnd(range.getToIntervalEnd()-1);*/
                            //helper.update(range);
                             updatedNeeded=true;
                        }
                    }
                }
            }
            if(updatedNeeded=true){
               // helperBis.update(range);
            }
           helperBis.closeStore();
        }
        }catch(IntactException e){
            e.printStackTrace();
        }

        return rangeSeq;

    }

    /**
     * The translation of an Rna to a protein sequence always start with a Methionine symbolized with the letter M in
     * the protein sequence. This M can either stay or be removed during the post-translation modification.
     * At first, Uniprot was storing all the sequence keeping this first M.
     * Then they decided to remoove all of them.
     * Now they decided to pub them back and to add an annotation telling weather this M is remooved or not.
     *
     * This can lead to this situation :
     *
     * Day one : creation of the feature in Intact
     *                         1 2 3 4 5 6 7 8 ...etc
     *      Protein sequence : S D E G A D K S L D T N T E F I I Q T R S R R S N A G N K
     *      Range sequence :          G A D K S L D T N T E F I I Q T R S R R S N A G N K
     *      FromIntervalStart : 4
     *
     * Day two : update of the sequence, uniprot decide to remove the M
     *                         1 2 3 4 5 6 7 8 ...etc
     *      Protein sequence : M S D E G A D K S L D T N T E F I I Q T R S R R S N A G N K
     *      As you can see : S became number 2 but was number 1 before.
     *      So if we want to re-calculate the Range sequence on day 2, we have to remoove one from the "fromIntervalStart"
     *      Therefore fromIntervalStart become 5
     *
     * @param rangeBean
     * @param protSeq
     * @return rangeSeq
     */

    public String generateRangeSeqIfMAdded(String protSeq, RangeBean rangeBean, String normalRangeSeq)/* throws IntactException, SQLException*/ {


        boolean  updatedNeeded=false;
        String rangeSeq = new String();
        String rangeSeqStored = rangeBean.getSequence();
        try{
            IntactHelper helperBis = new IntactHelper();
        Range range = (Range) helperBis.getObjectByAc(Range.class,rangeBean.getAc());
        System.out.println("\n------ Range"+range.getSequence());
            if(range.getSequence()==null){
                        System.out.println("METHOD");
                        System.out.println("Range ac = "+ rangeBean.getAc()+" was null from the beginning");
                        System.out.println("range Bean ac "+rangeBean.getAc());
                        System.out.println("&&&&&&&&&&");
                    }
        if(protSeq!=null){

            String fromFuzzyTypeAc=  rangeBean.getFromfuzzytype_ac();

            /*
            * If fromFuzzyTypeAc == nTer or undetermined, the rule is that the range sequence corresponds to the
            * first N nucleotides.
            */
            if(nTerminalCvBean.getAc().equals(fromFuzzyTypeAc) || undeterminedCvBean.getAc().equals(fromFuzzyTypeAc)){
                if(protSeq.length()<100){
                    rangeSeq = protSeq.substring(1,protSeq.length());
                    if(rangeSeqStored.equals(rangeSeq)){
                        System.out.println("9");
                        //Range range = (Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                        //helper.update(range);
                         updatedNeeded=true;
                    }
                }else{
                    if(protSeq.length()>=101){
                        rangeSeq = protSeq.substring(1,101);
                        if(rangeSeqStored.equals(rangeSeq)){
                            System.out.println("10");
                            //Range range = (Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                            System.out.println("eval seq is  "+normalRangeSeq);
                            //helper.update(range);
                             updatedNeeded=true;
                        }
                    }
                    else{
                        rangeSeq = protSeq.substring(1,protSeq.length());
                        if(rangeSeqStored.equals(rangeSeq)){
                            System.out.println("11");
                            //Range range = (Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                            System.out.println("eval seq is  "+normalRangeSeq);
                            //helper.update(range);
                             updatedNeeded=true;
                        }
                    }
                }
            }
            /*
            * If fromFuzzyTypeAc == cTer,  the rule is that the range corresponds to the first N nucleotides.
            */
            else if (cTerminalCvBean.getAc().equals(fromFuzzyTypeAc)){
                if(protSeq.length()<100){
                    rangeSeq=protSeq.substring(1,protSeq.length());
                    if(rangeSeqStored.equals(rangeSeq)){
                        System.out.println("12");
                        //Range range = (Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                        //helper.update(range);
                         updatedNeeded=true;
                    }
                }else{
                    //System.out.println("I'm updating range : "+range.getAc());
                    rangeSeq=protSeq.substring(protSeq.length()-100,protSeq.length());
                    if(rangeSeqStored.equals(rangeSeq)){
                        System.out.println("13");
                        //Range range = (Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                        //helper.update(range);
                         updatedNeeded=true;
                    }
                }
            }
            /*
            * Otherwise, the rule is that the range sequence must corresponds to the first N nucleotides starting
            * from "fromIntervalStart"
            */
            else{

                int fromIntervalStart=range.getFromIntervalStart();
                if(fromIntervalStart+100 > protSeq.length()){
                    /*
                    *                    1 2 3 4 5 6 7 8 ...etc
                    * Protein sequence : M S D E G A D K S L D T N T E F I I Q T R S R R S N A G N K
                    * Range sequence :           G A D K S L D T N T E F I I Q T R S R R S N A G N K
                    * The rule is that the M is position 1, S position 2...etc
                    * But in programming M will be postion 0, so to take the good part of the protein sequence we
                    * have to remove 1 to fromIntervalStart
                    */
                    rangeSeq =  protSeq.substring(fromIntervalStart,protSeq.length());
                    if(rangeSeqStored.equals(rangeSeq)){
                        System.out.println("14");
                        System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                        range.setFromIntervalStart(fromIntervalStart+1);
                        range.setFromIntervalEnd(range.getFromIntervalEnd()+1);
                        range.setToIntervalStart(range.getToIntervalStart()+1);
                        range.setToIntervalEnd(range.getToIntervalEnd()+1);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                        System.out.println("eval seq is  "+normalRangeSeq);
                        /*range.setFromIntervalStart(fromIntervalStart+1);
                        range.setFromIntervalEnd(range.getFromIntervalEnd()+1);
                        range.setToIntervalStart(range.getToIntervalStart()+1);
                        range.setToIntervalEnd(range.getToIntervalEnd()+1);*/
                        //helper.update(range);
                         updatedNeeded=true;
                    }

                }
                else{
                    if(fromIntervalStart==0){
                        rangeSeq = protSeq.substring(1, fromIntervalStart+101);
                        if(rangeSeqStored.equals(rangeSeq)){
                            System.out.println("15");
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            System.out.println("replace by   "+normalRangeSeq);
                            range.setFromIntervalStart(fromIntervalStart+1);
                            range.setFromIntervalEnd(range.getFromIntervalEnd()+1);
                            range.setToIntervalStart(range.getToIntervalStart()+1);
                            range.setToIntervalEnd(range.getToIntervalEnd()+1);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("eval seq is  "+normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                            /*range.setFromIntervalStart(fromIntervalStart+1);
                            range.setFromIntervalEnd(range.getFromIntervalEnd()+1);
                            range.setToIntervalStart(range.getToIntervalStart()+1);
                            range.setToIntervalEnd(range.getToIntervalEnd()+1);*/
                            //helper.update(range);
                             updatedNeeded=true;
                        }

                    }else{
                        rangeSeq =protSeq.substring(fromIntervalStart, fromIntervalStart+100);
                        if(rangeSeqStored.equals(rangeSeq)){
                            System.out.println("16");
                            System.out.println("I'm updating range : "+rangeBean.getAc());
                            System.out.println("previous seq "+range.getSequence());
                            //System.out.println("replace by   "+normalRangeSeq);
                            range.setFromIntervalStart(fromIntervalStart+1);
                            range.setFromIntervalEnd(range.getFromIntervalEnd()+1);
                            range.setToIntervalStart(range.getToIntervalStart()+1);
                            range.setToIntervalEnd(range.getToIntervalEnd()+1);
                            range.setSequence(protSeq);//normalRangeSeq);
                            System.out.println("now seq is   "+range.getSequence());
                            System.out.println("eval seq is  "+normalRangeSeq);
                            /*range.setFromIntervalStart(fromIntervalStart+1);
                            range.setFromIntervalEnd(range.getFromIntervalEnd()+1);
                            range.setToIntervalStart(range.getToIntervalStart()+1);
                            range.setToIntervalEnd(range.getToIntervalEnd()+1);*/
                            //helper.update(range);
                             updatedNeeded=true;
                        }
                    }
                }
            }
            if(updatedNeeded=true){
                //helperBis.update(range);
            }
            helperBis.closeStore();
        }
        }catch(IntactException e){
            e.printStackTrace();
        }


        return rangeSeq;

    }






    public void checkRangeSeqBis(List interactorBeans) throws SQLException, IntactException, CloneNotSupportedException {
        /*int savedByM=0;
        int mSupp=0;
        int mAdded=0;
        int equal=0;
        int notEqual=0;
        int fileNumber=0;

        String mSuppSequences = new String();
        String mAddedSequences = new String();*/

        for (int i = 0; i < interactorBeans.size(); i++) {
            InteractorBean interactorBean =  (InteractorBean) interactorBeans.get(i);
            List rangeBeans = rangeSeqSch.getBeans(RangeBean.class, interactorBean.getAc());
            String protSeq=getProteinSequence(interactorBean.getAc());
            if(protSeq!=null){
                for (int j = 0; j < rangeBeans.size(); j++) {

                    RangeBean rangeBean = (RangeBean) rangeBeans.get(j);
                    if(rangeBean.getSequence()==null){
                        System.out.println("&&&&&&&&&&");
                        System.out.println("RangeBean ac = "+ rangeBean.getAc()+" was null from the beginning");
                        System.out.println("&&&&&&&&&&");
                    }
                    Range range = (Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                    if(range.getSequence()==null){
                        System.out.println("&&&&&&&&&&");
                        System.out.println("Range ac = "+ rangeBean.getAc()+" was null from the beginning");
                        System.out.println("&&&&&&&&&&");
                    }

                    String rangeSeqTest=range.getSequence();
                    //Evaluating the 3 different kind of sequence
                    Range rangeToGetSeq = (Range) range.clone();
                           rangeToGetSeq.setSequence(protSeq);
                    String evaluatedRangeSeq=rangeToGetSeq.getSequence();//;generateNormalRangeSeq(protSeq,rangeBean);
                    if(evaluatedRangeSeq!= null && !evaluatedRangeSeq.equals(rangeSeqTest)) {
                        //System.out.println("\n");
                        //Range rangeBis=(Range) helper.getObjectByAc(Range.class,rangeBean.getAc());
                        evaluatedRangeSeq=range.getSequence();
                        String ifMAdded=generateRangeSeqIfMAdded(protSeq,rangeBean,evaluatedRangeSeq);
                        //System.out.println("\n");
                        String ifMRemooved=generateRangeSeqIfMRemooved(protSeq,rangeBean,evaluatedRangeSeq);

                        // Searching the Interaction_ac in which this interactor is involved, because to edit a feature
                        // via the Editor you need the interaction
                        List componentBeans = rangeSeqSch.getBeans(ComponentBean.class,rangeBean.getComponent_ac());
                        String interaction_ac=new String();
                        for (int k = 0; k < componentBeans.size(); k++) {
                            ComponentBean componentBean =  (ComponentBean) componentBeans.get(k);
                            interaction_ac=componentBean.getInteraction_ac();
                        }


                        List interactionBeans = rangeSeqSch.getBeans(InteractorBean.class, interaction_ac);
                        //System.out.println("We are going to search for the interacion ac = "+interaction_ac);
                        InteractorBean interactionBean = new InteractorBean();
                        interactionBean = (InteractorBean) interactionBeans.get(0);

                        String rangeSeq=rangeBean.getSequence();

                        if(rangeSeq!=null){
                            if(rangeBean.getSequence().equals(evaluatedRangeSeq)){
                                //equal++;
                            } else if(rangeBean.getSequence().equals(ifMAdded)){
                                //mSupp++;

                                String mAddedErrorMessage = "\tInteractor ac : "+interactorBean.getAc() + "\tComponent ac : "+rangeBean.getComponent_ac()+"\tFeature ac : "+rangeBean.getFeature_ac();
                                if(rangeBean.getFromfuzzytype_ac()==null){
                                    mAddedErrorMessage = mAddedErrorMessage + "\tNo From Fuzzy Type";
                                }else{
                                    mAddedErrorMessage = mAddedErrorMessage + "\tFuzzy Type : "+rangeBean.getFromfuzzytype_ac();
                                }
                                mAddedErrorMessage = mAddedErrorMessage + "\tFrom Interval Start : "+range.getFromIntervalStart();

                                messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_SAVED_BY_ADDING_THE_M,interactionBean, mAddedErrorMessage);//, editorUrlBuilder.getEditorUrl(interactionBean));
                            }
                            else if(rangeBean.getSequence().equals(ifMRemooved)){
                                //mAdded++;
                                String mSuppErrorMessage = "\tInteractor ac : "+interactorBean.getAc() + "\tComponent ac : "+rangeBean.getComponent_ac()+"\tFeature ac : "+rangeBean.getFeature_ac();
                                if(rangeBean.getFromfuzzytype_ac()==null){
                                    mSuppErrorMessage = mSuppErrorMessage + "\tNo From Fuzzy Type";
                                }else{
                                    mSuppErrorMessage = mSuppErrorMessage + "\tFuzzy Type : "+rangeBean.getFromfuzzytype_ac();
                                }
                                mSuppErrorMessage = mSuppErrorMessage + "\tFrom Interval Start : "+range.getFromIntervalStart();
                                messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_SAVED_BY_SUPPRESSING_THE_M,interactionBean, mSuppErrorMessage);//;, editorUrlBuilder.getEditorUrl(interactionBean));

                            }
                            else{
                                //notEqual++;
                                //fileNumber++;
                                String notEqualErrorMessage = "\tInteractor ac : "+interactorBean.getAc() + "\tComponent ac : "+rangeBean.getComponent_ac()+"\tFeature ac : "+rangeBean.getFeature_ac();
                                if(rangeBean.getFromfuzzytype_ac()==null){
                                    notEqualErrorMessage = notEqualErrorMessage + "\tNo From Fuzzy Type";
                                }else{
                                    notEqualErrorMessage = notEqualErrorMessage + "\tFuzzy Type : "+rangeBean.getFromfuzzytype_ac();
                                }
                                notEqualErrorMessage = notEqualErrorMessage + "\tFrom Interval Start : "+range.getFromIntervalStart();
                                notEqualErrorMessage = notEqualErrorMessage + "\nRange sequence stored in zpro : "+rangeSeq +"\n";
                                messageSender.addMessage(ReportTopic.RANGE_SEQUENCE_NOT_EQUAL_TO_PROTEIN_SEQ,interactionBean, notEqualErrorMessage);//, editorUrlBuilder.getEditorUrl(interactionBean));
                            }
                        }
                    }
                }
            }

        }

        /*System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("M added sequence ");
        System.out.println(mAddedSequences);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("M sup sequence ");
        System.out.println(mSuppSequences);
        System.out.println("");
        System.out.println("saved by M "+ savedByM);
        System.out.println("madd "+mAdded);
        System.out.println("msup "+mSupp);
        System.out.println("equal "+equal);
        System.out.println("not equal "+notEqual);*/


    }





























    /**
     * For each XrefBean this method will verify that the primaryId is valide
     *
     * @param xrefBeans a List containing all the xref of the database
     * @throws SQLException
     */

    public void hasValidPrimaryId(List xrefBeans) throws SQLException {

        for (int i = 0; i < xrefBeans.size(); i++) {
            XrefBean xrefBean =  (XrefBean) xrefBeans.get(i);
            if (xrefBean.getPrimaryid().equals("MA:1234")){
                String temporaryBreak = "";
            }


            //Get the annotationBean containing the regular expression wich describe the primaryid of the database ac
            List annotationBeans = hasValidPrimaryIdSch.getBeans(AnnotationBean.class,xrefBean.getDatabase_ac());

            for (int j = 0; j < annotationBeans.size(); j++) {

                AnnotationBean annotationBean =  (AnnotationBean) annotationBeans.get(j);
                // Get the regular expression (stored in the field description of the annotation
                String regexp=annotationBean.getDescription();
                // PrimaryId to check
                String primaryId = xrefBean.getPrimaryid();

                try {
                    // TODO escape special characters !!
                    Pattern pattern = Pattern.compile( regexp );

                    // validate the primaryId against that regular expression
                    Matcher matcher = pattern.matcher( primaryId );
                    // If the primaryId hadn't been validate against that regular expression send a message
                    if( false == matcher.matches() ) {
                        messageSender.addMessage(ReportTopic.XREF_WITH_NON_VALID_PRIMARYID,xrefBean);
                    }

                } catch ( Exception e ) {
                    // if the RegExp engine thrown an Exception, that may happen if the format is wrong.
                    // we just display it for debugging sake, but the Xref is declared valid.
                    //e.printStackTrace();
                }
            }
        }
    }
    public void checkAnnotations (List annotatedBeans, String annotatedType,List usableTopic) throws SQLException, IntactException {
        for (int i = 0; i < annotatedBeans.size(); i++) {
            AnnotatedBean annotatedBean =  (AnnotatedBean) annotatedBeans.get(i);

            if(Protein.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.timestamp, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_int2annot i2a " +
                                                                  "where a.ac=i2a.annotation_ac and "+
                                                                  "i2a.interactor_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, Protein.class.getName(),usableTopic);
            }
            else if(Interaction.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.timestamp, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_int2annot i2a " +
                                                                  "where a.ac=i2a.annotation_ac and "+
                                                                  "i2a.interactor_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, Interaction.class.getName(),usableTopic);
            }
            else if (Experiment.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.timestamp, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_exp2annot e2a " +
                                                                  "where a.ac=e2a.annotation_ac and "+
                                                                  "e2a.experiment_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, Experiment.class.getName(),usableTopic);
            }
            else if (BioSource.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.timestamp, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_biosource2annot bs2a " +
                                                                  "where a.ac=bs2a.annotation_ac and "+
                                                                  "bs2a.biosource_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, BioSource.class.getName(),usableTopic);

            } if (CvObject.class.getName().equals(annotatedType)){
                annotationTopic.addMapping(AnnotationBean.class,  "select a.ac, a.topic_ac, a.timestamp, a.userstamp " +
                                                                  "from ia_annotation a, " +
                                                                  "ia_cvobject2annot cv2a " +
                                                                  "where a.ac=cv2a.annotation_ac and "+
                                                                  "cv2a.cvobject_ac=?");
                List annotationBeans = annotationTopic.getBeans(AnnotationBean.class, annotatedBean.getAc());
                checkAnnotationTopic(annotationBeans, annotatedBean, CvObject.class.getName(),usableTopic);
            }

        }
    }


    public void checkAnnotationTopic (List annotations, AnnotatedBean annotatedBean, String annotatedType, List usableTopic) throws SQLException, IntactException {

        for (int i = 0; i < annotations.size(); i++) {
            AnnotationBean annotationBean =  (AnnotationBean) annotations.get(i);
            String topicShortlabel = (String) cvTopics.get(annotationBean.getTopic_ac());
            System.out.println("topicShortLabel " + topicShortlabel);
            for (int j = 0; j < usableTopic.size(); j++) {
                String s = (String) usableTopic.get(j);
                System.out.println("usable topic " +j + " = "+ s);
            }

            if(!usableTopic.contains(topicShortlabel)){
                //messageSender.addMessage(ReportTopic.TOPICAC_NOT_VALID,annotationBean, annotatedBean, annotatedType, topicShortlabel);//, editorUrlBuilder.getEditorUrl(annotatedBean));
                messageSender.addMessage(ReportTopic.TOPICAC_NOT_VALID,annotationBean, topicShortlabel);//, editorUrlBuilder.getEditorUrl(annotatedBean));

            }
        }
    }

    public void checkDeletionFeature(List rangeBeans ) throws SQLException, IntactException {
        for (int i = 0; i < rangeBeans.size(); i++) {
            RangeBean rangeBean =  (RangeBean) rangeBeans.get(i);
            messageSender.addMessage(ReportTopic.DELETION_INTERVAL_TO_LONG_TO_BE_CARACTERIZED_BY_DELETION_ANALYSIS_FEATURE_TYPE, rangeBean);
        }
    }

    public static void main(String[] args) throws SQLException, IntactException, LookupException, CloneNotSupportedException {


        SanityChecker scn = new SanityChecker();
        Set key = scn.cvTopics.keySet();
        for (Iterator iterator = key.iterator(); iterator.hasNext();) {
            String o =  (String) iterator.next();
            System.out.println("Key = "+o + " Value = " + scn.cvTopics.get(o));
        }
        System.out.println( "Helper created (User: " + scn.helper.getDbUserName() + " " +
                            "Database: " + scn.helper.getDbName() + ")" );




        List expUsableTopic = scn.annotationSection.getUsableTopics(Experiment.class.getName());
        expUsableTopic.add(CvTopic.ACCEPTED);
        expUsableTopic.add(CvTopic.TO_BE_REVIEWED);

        List intUsableTopic = scn.annotationSection.getUsableTopics(Interaction.class.getName());
        List protUsableTopic = scn.annotationSection.getUsableTopics(Protein.class.getName());
        List cvUsableTopic=scn.annotationSection.getUsableTopics(CvObject.class.getName());
        List bsUsableTopic=scn.annotationSection.getUsableTopics(BioSource.class.getName());

        /*
        *     Check on interactor
        */

        SanityCheckerHelper schIntAc = new SanityCheckerHelper(scn.helper);
        schIntAc.addMapping(InteractorBean.class,"SELECT ac, shortlabel, userstamp, timestamp, objclass "+
                                                 "FROM ia_interactor "+
                                                 "WHERE objclass = '"+InteractionImpl.class.getName()+ "'"  +
                                                " AND ac like ?");


        List interactorBeans = schIntAc.getBeans(InteractorBean.class, "EBI-%");
        System.out.println("The size of the list is :"+interactorBeans.size());
        scn.checkInteractionsComplete(interactorBeans);
        scn.checkInteractionsBaitAndPrey(interactorBeans);
        scn.checkComponentOfInteractions(interactorBeans);
        scn.checkOneIntOneExp();
       scn.checkAnnotations(interactorBeans, Interaction.class.getName(),intUsableTopic);

        /*
        *     Check on xref
        */
        schIntAc.addMapping(XrefBean.class, "select distinct primaryId "+
                                            "from ia_xref, ia_controlledvocab db, ia_controlledvocab q " +
                                            "where database_ac = db.ac and " +
                                            "db.shortlabel = ? and "+
                                            "qualifier_ac = q.ac and "+
                                            "q.shortlabel = 'identity' "+
                                            "group by primaryId "+
                                            "having count(primaryId) > 1");


        List xrefBeans = schIntAc.getBeans(XrefBean.class,CvDatabase.UNIPROT);

        scn.sch.addMapping(InteractorBean.class,"SELECT i.ac,i.objclass, i.shortlabel, i.biosource_ac, i.userstamp, i.timestamp "+
                                                "FROM ia_interactor i, ia_xref x "+
                                                "WHERE i.ac = x.parent_ac AND " +
                                                "0 = ( SELECT count(1) " +
                                                "FROM ia_annotation a, ia_int2annot i2a, ia_controlledvocab topic "+
                                                "WHERE i.ac = i2a.interactor_ac AND "+
                                                "i2a.annotation_ac = a.ac AND " +
                                                "a.topic_ac = topic.ac AND " +
                                                "topic.shortlabel = 'no-uniprot-update' ) AND "+
                                                "x.qualifier_ac = '" +identityXrefQualifierCvBean.getAc()+"' AND "+
                                                "x.primaryid=?");

        //scn.duplicatedProtein(xrefBeans);
        for (int i = 0; i < xrefBeans.size(); i++) {
            XrefBean xrefBean =  (XrefBean) xrefBeans.get(i);
            scn.duplicatedProtein(xrefBean);
        }

        schIntAc.addMapping(XrefBean.class,"select ac, userstamp, timestamp, database_ac, primaryid,parent_ac "+
                                           "from ia_xref "+
                                            "where ac like ?");
                                            //"where ac ='EBI-695273' and ac like ?");
        xrefBeans = schIntAc.getBeans(XrefBean.class, "%");
        scn.hasValidPrimaryId(xrefBeans);

        /*
        *     Check on Experiment
        */
       List experimentBeans = scn.sch.getBeans(ExperimentBean.class,"EBI-%");

        scn.checkReviewed(experimentBeans);

        scn.checkExperiment(experimentBeans);

        scn.checkExperimentsPubmedIds(experimentBeans);

        scn.checkAnnotations(experimentBeans, Experiment.class.getName(), expUsableTopic);

        scn.experimentNotSuperCurated();

        /*
        *     Check on BioSource
        */

        List bioSourceBeans = scn.sch.getBeans(BioSourceBean.class,"EBI-%");
        System.out.println("The size of bioSource list is " + bioSourceBeans.size());
        scn.checkBioSource(bioSourceBeans);
        scn.checkNewt(bioSourceBeans);
        //to be tested
        scn.checkAnnotations(bioSourceBeans, BioSource.class.getName(),bsUsableTopic);

        /*
        *     Check on protein
        */
//right now not actual using, as concerning checks appear to commented out

        schIntAc.addMapping(InteractorBean.class,"SELECT ac, crc64, shortlabel, userstamp, timestamp, objclass "+
                                                 "FROM ia_interactor "+
                                                 "WHERE objclass = '"+ProteinImpl.class.getName()+
                                                 "' AND ac like ?");

        List proteinBeans = schIntAc.getBeans(InteractorBean.class,"%");
        System.out.println("This/those Range(s) were created when the first Methionine was there, since then the Methionine had been remooved from the Protein Sequence. The Range Sequence has been remapped");
        /*schIntAc.addMapping(InteractorBean.class,"SELECT ac, crc64, shortlabel, userstamp, timestamp, objclass "+
                                                 "FROM ia_interactor "+
                                                 "WHERE objclass = ? "+
                                                 " AND ac in ('EBI-349787','EBI-375446','EBI-397048','EBI-515331','EBI-632461','EBI-527215')");
        List proteinBeans = schIntAc.getBeans(InteractorBean.class, ProteinImpl.class.getName());
        scn.checkRangeSeqBis(proteinBeans);

        System.out.println("\n\nThis/those Range(s) were created when the first Methionine was not there, since then the Methionine had been added");
        schIntAc.addMapping(InteractorBean.class,"SELECT ac, crc64, shortlabel, userstamp, timestamp, objclass "+
                                                 "FROM ia_interactor "+
                                                 "WHERE objclass = ? "+
                                                 " AND ac in ('EBI-10022','EBI-10218','EBI-101537','EBI-103438','EBI-106786','EBI-108311')");*/
        //proteinBeans = schIntAc.getBeans(InteractorBean.class, ProteinImpl.class.getName());
        //scn.checkRangeSeqBis(proteinBeans);


        scn.checkProtein(proteinBeans);
        scn.checkCrc64(proteinBeans);
        scn.checkAnnotations(proteinBeans, EditorMenuFactory.PROTEIN,protUsableTopic);

        //already working
        List ranges = scn.deletionFeatureSch.getBeans(RangeBean.class,"2");
        scn.checkDeletionFeature(ranges);

        /*
        *     Check on annotation
        */

            //tested
             schIntAc.addMapping(AnnotationBean.class, "SELECT ac, description, timestamp, userstamp "+
                                                       "FROM ia_annotation "+
                                                       "WHERE topic_ac = 'EBI-18' and ac like ?"   // + " and ac='EBI-375752'"
                                                        );

            List annotationBeans = schIntAc.getBeans(AnnotationBean.class,"EBI-%");
            System.out.println("There is " + annotationBeans.size() + "annotations");
            scn.checkURL(annotationBeans);
    //
    //        /*
    //        *    Check on controlledvocab
    //        */
    //
             schIntAc.addMapping(ControlledvocabBean.class,"SELECT ac, objclass, shortlabel, timestamp, userstamp "+
                                                           "FROM ia_controlledvocab "+
                                                           "WHERE ac = ?");
            List controlledvocabBeans = schIntAc.getBeans(ControlledvocabBean.class, "%");

        //scn.checkAnnotations(controlledvocabBeans, EditorMenuFactory.CV_PAGE,cvUsableTopic);

        // try to send emails
        try {
            scn.messageSender.postEmails();

        } catch ( MessagingException e ) {
            // scould not send emails, then how error ...
            //e.printStackTrace();

        }


        schIntAc=null;

        scn.helper.closeStore();
        scn=null;
    }

}