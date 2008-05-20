/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.cvutils.model;

import org.obo.datamodel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.model.util.AliasUtils;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.business.IntactException;


import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * TODO comment that class header
 *
 * @author Prem Anand (prem@ebi.ack.uk)
 * @version $Id$
 */
public class CvObjectOntologyBuilder extends CvObjectOntology{
    //Initialises nonMiCvDatabase
    public CvObjectOntologyBuilder() {
        this.nonMiCvDatabase = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(),
                CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
    }//end constructor

    private static final Log log = LogFactory.getLog( CvObjectOntologyBuilder.class );

    private static Map<String,Class> mi2Class = new HashMap<String,Class>();
    //private static Map<Class,String> class2mi = new HashMap<Class,String>();
    private static final String MI_ROOT_IDENTIFIER="MI:0000";
    private static final String ALIAS_IDENTIFIER="PSI-MI-alternate";
    private static final String SHORTLABEL_IDENTIFIER="PSI-MI-short";

    private static OBOSession oboSession=null;
    private Map<String,String> obsoleteCvs;
    private boolean excludeObsolete=false;

    private CvDatabase nonMiCvDatabase;
    private Map<String,CvObject> processed;




    static{
        // DAG objects:  A Hashmap of MI and the CV(Type).class
        mi2Class.put("MI:0001",CvInteraction.class);
        mi2Class.put("MI:0190",CvInteractionType.class);
        mi2Class.put("MI:0002",CvIdentification.class);
        mi2Class.put("MI:0003",CvFeatureIdentification.class);
        mi2Class.put("MI:0116",CvFeatureType.class);
        mi2Class.put("MI:0313",CvInteractorType.class);
        mi2Class.put("MI:0346",CvExperimentalPreparation.class);
        mi2Class.put("MI:0333",CvFuzzyType.class);
        mi2Class.put("MI:0353",CvXrefQualifier.class );
        mi2Class.put("MI:0444",CvDatabase.class);
        mi2Class.put("MI:0495",CvExperimentalRole.class);
        mi2Class.put("MI:0500",CvBiologicalRole.class);
        mi2Class.put("MI:0300",CvAliasType.class );
        mi2Class.put("MI:0590",CvTopic.class);
        mi2Class.put("MI:0640",CvParameterType.class );
        mi2Class.put("MI:0647",CvParameterUnit.class);


    }

    public CvObjectOntologyBuilder(OBOSession oboSession_) {
        oboSession=oboSession_;
        this.processed = new HashMap<String,CvObject>();
        this.obsoleteCvs=new HashMap<String,String>();
    }

    /*
    *   The main method which converts the OBOOBject
    *   toCVObject.
    *
    * */
    public <T extends CvObject> T toCvObject(OBOObject oboObj) {
        T cvObject =null;
        try{
            log.info("ID    ->"+oboObj.getID());
            log.info("Name  ->"+oboObj.getName());
            log.info("Def   ->"+oboObj.getDefinition());
            log.info("Isobsolete   ->"+oboObj.isObsolete());

            //find the CvClass for any given MI identifier
            Class<T> cvClass=findCvClassforMI(oboObj.getID());

            //if CvClass is null then CvTopic.class is taken as default
            if(cvClass==null){
                if(oboObj.isObsolete()){

                    cvClass= (Class<T>) CvTopic.class;
                }
            }
            log.info("cvClass ->"+cvClass.getName());

            //Checks if the given object is already processed. If so, returns the CvObject
            String processedKey = cvKey(cvClass, oboObj.getID());
            log.info("processedKey before adding ->"+processedKey);

            if (processed.containsKey(processedKey)) {
                return (T) processed.get(processedKey);
            }

            final Institution institution = IntactContext.getCurrentInstance().getInstitution();
            log.info("Institution name  ->"+institution.getFullName());


            //Short label look for EXACT PSI-MI-short  in synonym tag OBO 1.2

            String shortLabel=calculateShortLabel(oboObj);
            log.info("shortLabel before   ->"+shortLabel);
            if(oboObj.isObsolete()){
                obsoleteCvs.put(oboObj.getID(),shortLabel);
            }




            cvObject = CvObjectUtils.createCvObject(institution,cvClass, null,shortLabel);
            log.info("shortLabel    after ->"+shortLabel);

            //comment from here

            cvObject.addXref(createIdentityXref(cvObject,oboObj.getID()));
            cvObject.setFullName(oboObj.getName());


            /********************************
             *Database and Qualifier Cv
             ********************************/

            Set<Dbxref> defDbXrefSet=oboObj.getDefDbxrefs();
            Object[] dbxrefArray=defDbXrefSet.toArray();
            log.info("dbxrefArray size "+dbxrefArray.length);

            //check if Unique Resid
            boolean uniqueResid;
            uniqueResid=checkIfUniqueResid(dbxrefArray);
            String[] termXref;
            if(dbxrefArray!=null && dbxrefArray.length==1){
                CvObjectXref xref;

                Dbxref defDbxref =(Dbxref) dbxrefArray[0];
                termXref = addQualifiersForFirstDbXreferences(defDbxref);
                xref = toXref(cvObject, termXref[0],termXref[1],termXref[2]);
                if (xref != null) {
                    cvObject.addXref(xref);
                }//end inner if
            } //end if e
            else if(dbxrefArray!=null && dbxrefArray.length>1){
                CvObjectXref xref;
                //more than one dbxreference
                //add the first one
                String firstDatabasexref;


                Dbxref defDbxref =(Dbxref) dbxrefArray[0];

                firstDatabasexref=defDbxref.getDatabase();
                termXref=addQualifiersForFirstDbXreferences(defDbxref);
                xref = toXref(cvObject, termXref[0],termXref[1],termXref[2]);
                if (xref != null) {
                    cvObject.addXref(xref);
                }//end inner if

                //add from second xrefs
                for(int i=1;i<dbxrefArray.length;i++){
                    termXref=null;
                    defDbxref =(Dbxref) dbxrefArray[i];
                    termXref=addQualifiersForOtherDbXreferences(defDbxref,firstDatabasexref,uniqueResid);
                    xref = toXref(cvObject, termXref[0],termXref[1],termXref[2]);
                    if (xref != null) {
                        cvObject.addXref(xref);
                    }//end inner if

                }//end for
            }  //end elseif
            else{
                log.info("No dbxreference");
            }



            /********************************
             *Definitions
             ********************************/

            // definition
            if (oboObj.getDefinition() != null) {

                String definition=oboObj.getDefinition();
                log.info("Definition "+oboObj.getDefinition());


                if(definition.contains("\n")){
                    String[] defArray=definition.split("\n");
                    log.info("DefArray length "+defArray.length);
                    if(defArray.length==2){
                        String prefixString = defArray[0];
                        String suffixString=defArray[1];

                        if(suffixString.startsWith("OBSOLETE") || oboObj.isObsolete()) {
                            log.info(" Def with OBSOLETE-> "+suffixString);

                            Annotation annot = toAnnotation(CvTopic.OBSOLETE,suffixString);
                            if (annot != null) {
                                cvObject.addAnnotation(annot);
                            }
                            CvTopic definitionTopicDef = CvObjectUtils.createCvObject(institution, CvTopic.class, null, CvTopic.DEFINITION);
                            cvObject.addAnnotation(new Annotation(institution, definitionTopicDef, prefixString));



                        }else if(suffixString.startsWith("http")){
                            log.info(" Def with url-> "+suffixString);
                            Annotation annot = toAnnotation(CvTopic.URL,suffixString);
                            if (annot != null) {
                                cvObject.addAnnotation(annot);
                            }


                            CvTopic definitionTopicDef = CvObjectUtils.createCvObject(institution, CvTopic.class, null, CvTopic.DEFINITION);
                            cvObject.addAnnotation(new Annotation(institution, definitionTopicDef, prefixString));


                        } else{
                            log.info(" New format "+suffixString);
                        }
                    } else{
                        log.info("-----something wrong here check------");
                    }


                }//end outer if
                else{

                    CvTopic definitionTopic = CvObjectUtils.createCvObject(institution, CvTopic.class, null, CvTopic.DEFINITION);
                    cvObject.addAnnotation(new Annotation(institution, definitionTopic, oboObj.getDefinition()));
                }
            }  //end of definition

            /********************************
             *XREF ANNOTATIONS
             ********************************/


            Set<Dbxref> dbxrefSet=oboObj.getDbxrefs();

            for (Dbxref dbxref : dbxrefSet) {
                log.info("dbxref " + dbxref.toString());

                String xref = dbxref.toString();
                if (xref.contains(CvTopic.XREF_VALIDATION_REGEXP)) {

                    Annotation annot = toAnnotation(CvTopic.XREF_VALIDATION_REGEXP, xref.split(":")[1]);
                    if (annot != null) {
                        cvObject.addAnnotation(annot);
                    }
                }//end if
                if (xref.contains(CvTopic.SEARCH_URL)) {

                    Annotation annot = toAnnotation(CvTopic.SEARCH_URL, dbxref.getDesc());
                    if (annot != null) {
                        cvObject.addAnnotation(annot);
                    }
                } //end if

            }  //end for

            /********************************
             *comment
             ********************************/
            if(oboObj.getComment()!=null && oboObj.getComment().length()>0){
                log.info("Comment->"+oboObj.getComment());
                Annotation annot = toAnnotation(CvTopic.COMMENT,oboObj.getComment());
                if (annot != null) {
                    cvObject.addAnnotation(annot);
                }
            } //end comment


            /********************************
             *Alias
             ********************************/


            Set<Synonym> syn=oboObj.getSynonyms();
            CvObjectAlias alias_=null;
            for (Iterator<Synonym> synonymIterator = syn.iterator(); synonymIterator.hasNext();) {
                String aliasName;
                Synonym synonym = synonymIterator.next();

                SynonymCategory synCat=synonym.getSynonymCategory();
                log.info("synCat ID : "+synCat.getID());

                if(synCat.getID()!=null && synCat.getID().equalsIgnoreCase(CvObjectOntologyBuilder.ALIAS_IDENTIFIER)){
                    aliasName=synonym.getText();
                    log.info("aliasName : "+aliasName);
                    alias_=(CvObjectAlias)toAlias(cvObject,aliasName);
                    cvObject.addAlias(alias_);
                } //end if
            } //end for


            log.info("--Processing finished for cvObject-- "+cvObject.getShortLabel()+"  MI "+cvObject.getMiIdentifier());

            processed.put(processedKey, cvObject);
            log.info("--Processed size "+processed.size());


            if (cvObject instanceof CvDagObject) {
                Collection<Link> childLinks =  oboObj.getChildren();
                log.info("Childlinks size   "+childLinks.size());
                Iterator<Link> linkIterator = childLinks.iterator();
                while (linkIterator.hasNext()) {
                    Link childLink = linkIterator.next();
                    log.info("Child ID  "+childLink.getID());

                    Pattern p = Pattern.compile("(MI:\\d+)-OBO_REL:is_a->(MI:\\d+)");
                    Matcher m = p.matcher(childLink.getID());
                    if(m.matches()){
                        log.info(" "+ m.group(1)+"  "+m.group(2));
                        if(m.group(2).equalsIgnoreCase(oboObj.getID())){
                            CvDagObject dagObject = (CvDagObject)cvObject;
                            OBOObject childObj = (OBOObject)oboSession.getObject(m.group(1));

                            // exclude obsoletes
                            if (excludeObsolete && childObj.isObsolete()) {
                                continue;
                            } //end if

                            addCvObjectToStatsIfObsolete(oboObj,cvObject);

                            dagObject.addChild((CvDagObject)toCvObject(childObj));
                        }//end if
                    }//end matches
                } //end for
            }//end if





        }catch(Exception ex){
            throw new IntactException("Exception converting to CvObject from OBOObject: "+oboObj.getID(), ex);
        }

        return cvObject;
    }

    private boolean checkIfUniqueResid(Object[] dbxrefArray) {
        int countResid=0;
        for(int i=0;i<dbxrefArray.length;i++){
            Dbxref defDbxref =(Dbxref) dbxrefArray[i];
            if(defDbxref.getDatabase().equalsIgnoreCase("RESID")){
                countResid++;
            }//end if

        }//end for
        if(countResid==1){
            return true;
        }else{
            return false;
        }

    }//end method

    private <T extends CvObject> String cvKey(Class<T> cvClass, String primaryId) {
        return cvClass.getSimpleName() + ":" + primaryId;
    }

    /*
    * A recursive method that traverse the tree up to fetch the parent Class
    * If the Class is not found incase of Obsolete entries where is_a relationship
    * is not given, then null is returned
    * */
    protected <T extends CvObject> Class<T> findCvClassforMI(String id) {


        Class<T>  cvClass=mi2Class.get(id);
        if(cvClass!=null){
            //then it is one of rootCv
            return cvClass;
        }else{
            OBOObject oboObj = (OBOObject)oboSession.getObject(id);
            Collection<Link> parentLinks=oboObj.getParents();
            for (Iterator<Link> linkIterator = parentLinks.iterator(); linkIterator.hasNext();) {
                Link parentLink = linkIterator.next();

                log.debug("Parent ID  "+parentLink.getID());

                String miIdentifierRight=parseToGetRightMI(parentLink.getID());//eg: MI:0436-OBO_REL:is_a->MI:0659
                String miIdentifierLeft=parseToGetLeftMI(parentLink.getID());//eg: MI:0436-OBO_REL:is_a->MI:0659

                log.debug("miIdentifierRight "+miIdentifierRight);
                log.debug("miIdentifierLeft "+miIdentifierLeft);

                if(miIdentifierLeft!=null && miIdentifierRight!=null&& miIdentifierLeft.equalsIgnoreCase(oboObj.getID())){

                    cvClass=mi2Class.get(miIdentifierRight);
                    if(cvClass!=null){
                        //then it is one of rootCv
                        return cvClass;
                    }//end if
                    else{
                        return findCvClassforMI(miIdentifierRight);
                    }
                }//end if

            }  //end for

        }
        return cvClass;
    } //end method

    /*
    *  Parses the given String and returns the MI identifier in
    * the left side of is_a
    *
    */
    private String parseToGetLeftMI(String relationString) {
        Pattern p = Pattern.compile("(MI:\\d+)-OBO_REL:is_a->(MI:\\d+)");
        Matcher m = p.matcher(relationString);
        if(m.matches()){
            // log.debug(" "+ m.group(1)+"  "+m.group(2));
            return m.group(1);
        }//end matches
        return null;
    }//end method

    /*
    *  Parses the given String and returns the MI identifier in
    * the right side of is_a
    *
    */
    private String parseToGetRightMI(String relationString) {
        Pattern p = Pattern.compile("(MI:\\d+)-OBO_REL:is_a->(MI:\\d+)");
        Matcher m = p.matcher(relationString);
        if(m.matches()){
            // log.debug(" "+ m.group(1)+"  "+m.group(2));
            return m.group(2);
        }//end matches
        return null;
    } //end method

    /*
    * This method is called if the Def: line has only one db xreference.
    *
    * */
    protected String[] addQualifiersForFirstDbXreferences(Dbxref defDbxref){

        if (defDbxref == null) {
            throw new NullPointerException("defDbxref is null");
        }

        //termXref[0] holds identifier_;termXref[1] holds qualifier_;termXref[2] holds database_;
        String[] termXref=new String[3];
        String database_=null,qualifier_=null,identifier_=null;
        log.info("defDbxref: "+defDbxref.getDatabase());
        log.info("defDbxref ID: "+defDbxref.getDatabaseID());

        if (defDbxref.getDatabase().equalsIgnoreCase("PMID")) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.PUBMED;
            qualifier_ = CvXrefQualifier.PRIMARY_REFERENCE;
        } else
        if (defDbxref.getDatabase().equalsIgnoreCase("PMID for application instance")) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.PUBMED;
            qualifier_ = CvXrefQualifier.SEE_ALSO;
        } else if (defDbxref.getDatabase().equalsIgnoreCase(CvDatabase.GO)) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.GO;
            qualifier_ = CvXrefQualifier.IDENTITY;
        } else if (defDbxref.getDatabase().equalsIgnoreCase(CvDatabase.RESID)) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.RESID;
            qualifier_ = CvXrefQualifier.SEE_ALSO;
        } else if (defDbxref.getDatabase().equalsIgnoreCase(CvDatabase.SO)) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.SO;
            qualifier_ = CvXrefQualifier.IDENTITY;
        } else if (defDbxref.getDatabase().equalsIgnoreCase("MOD")) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = "MOD";
            qualifier_ = CvXrefQualifier.IDENTITY;
        } else {
            throw new IllegalArgumentException("Unknown database: "+defDbxref.getDatabaseID()+" ("+defDbxref.getDatabase()+")");
        }

        termXref[0]=identifier_;
        termXref[1]=qualifier_;
        termXref[2]=database_;

        log.info("Returning identifier_:  "+identifier_+"  "+"  database_: "+database_+"  qualifier_:  "+qualifier_);
        return termXref;
    }   //end method

    /*
   * This method is called if the Def: line has more than one db xreference.
   * The qualifiers are assigned according to the rules set
   *
   * */
    protected String[] addQualifiersForOtherDbXreferences(Dbxref defDbxref,String firstDbxref,boolean uniqResid){
        //termXref[0] holds identifier_;termXref[1] holds qualifier_;termXref[2] holds database_;
        String[] termXref=new String[3];
        String database_=null,qualifier_=null,identifier_=null;
        log.debug("defDbxref: "+defDbxref.getDatabase());
        log.debug("defDbxref ID: "+defDbxref.getDatabaseID());


        if (defDbxref.getDatabase().equalsIgnoreCase("PMID")) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.PUBMED;

            if(firstDbxref.equalsIgnoreCase("PMID")){
                qualifier_=CvXrefQualifier.METHOD_REFERENCE;
            }else{
                qualifier_=CvXrefQualifier.PRIMARY_REFERENCE;
            }
        } else
        if (defDbxref.getDatabase().equalsIgnoreCase("PMID for application instance")) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.PUBMED;
            qualifier_ = CvXrefQualifier.SEE_ALSO;
        } else if (defDbxref.getDatabase().equalsIgnoreCase(CvDatabase.GO)) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.GO;
            qualifier_ = CvXrefQualifier.IDENTITY;
        } else if (defDbxref.getDatabase().equalsIgnoreCase(CvDatabase.RESID)) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.RESID;

            if(uniqResid){
                qualifier_=CvXrefQualifier.IDENTITY;
            }else{
                qualifier_=CvXrefQualifier.SEE_ALSO;
            }
        } else if (defDbxref.getDatabase().equalsIgnoreCase(CvDatabase.SO)) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = CvDatabase.SO;
            qualifier_ = CvXrefQualifier.IDENTITY;
        } else if (defDbxref.getDatabase().equalsIgnoreCase("MOD")) {
            identifier_ = defDbxref.getDatabaseID();
            database_ = "MOD";
            qualifier_ = CvXrefQualifier.IDENTITY;
        } else {
            throw new IllegalArgumentException("Unknown database: "+defDbxref.getDatabaseID()+" ("+defDbxref.getDatabase()+")");
        }

        termXref[0]=identifier_;
        termXref[1]=qualifier_;
        termXref[2]=database_;

        log.debug("Returning identifier_:  "+identifier_+"  "+"  database_: "+database_+"  qualifier_:  "+qualifier_);
        return termXref;
    }   //end method



    /*
   * This method returns the shortLabel for the term
   * If the name length is less than 20 characters, name is returned
   * If it is more than 20 characters, then the synonyms with EXACT PSI-MI-short is taken
   *
   * */

    private String calculateShortLabel(OBOObject oboObj) {
        String shortLabel = null;
        Set<Synonym> syn=oboObj.getSynonyms();
        for (Synonym synonym : syn) {
            SynonymCategory synCat = synonym.getSynonymCategory();
            log.debug("synCat ID : " + synCat.getID());
            if (synCat.getID() != null && synCat.getID().equalsIgnoreCase(CvObjectOntologyBuilder.SHORTLABEL_IDENTIFIER)) {
                shortLabel = synonym.getText();
                //another check just to reduce the length to 20 characters--rarely happens
                if(shortLabel!=null && shortLabel.length()>20){
                    log.info("@@@@shortLabel greater than 20@@@@@"+oboObj.getID());
                    shortLabel=shortLabel.substring(0,20);
                }//end if
            }//end for
        } //end for


        if(shortLabel==null){
            if(oboObj.getName()!=null && oboObj.getName().length()<=20){
                return oboObj.getName();
            }else if(oboObj.getName()!=null && oboObj.getName().length()>20){
                log.info("@@@@@No shortLabel@@@@@@@@@");
                return oboObj.getName().substring(0,20);
            }
        }
        return shortLabel;
    }  //end method


    protected CvObjectXref createIdentityXref(CvObject parent, String id) {
        CvObjectXref idXref;

        String primaryId = id;

        if (primaryId.startsWith("MI")) {
            idXref = XrefUtils.createIdentityXrefPsiMi(parent, id);
            idXref.prepareParentMi();
        }

        else {
            idXref = XrefUtils.createIdentityXref(parent, id, nonMiCvDatabase);
        }

        return idXref;
    } //end method

    /*
    protected <T extends CvObject> Collection<T> getRootCvObjects() {


        ArrayList<T> cvObjects=new ArrayList<T>();

        IdentifiedObject identifiedObject= oboSession.getObject(MI_ROOT_IDENTIFIER);


        if (identifiedObject instanceof OBOObject) {
            OBOObject obj = (OBOObject) identifiedObject;
            Collection<IdentifiedObject> oboObjCollection=getRootOBOObjects(obj);
            oboObjCollection.add(identifiedObject);// root identified object

            for (Iterator<IdentifiedObject> identifiedObjectIterator = oboObjCollection.iterator(); identifiedObjectIterator.hasNext();) {
                IdentifiedObject rootOboObject = identifiedObjectIterator.next();

                if(rootOboObject instanceof OBOObject){
                    OBOObject rootObj = (OBOObject) rootOboObject;
                    T cvObj= (T) toCvObject(rootObj);
                    cvObjects.add(cvObj);
                }

                // log.debug(rootOboObject.getID());

            }//end for

        } //end if
        return cvObjects;
    }//end method
    */



    public Collection<IdentifiedObject> getAllOBOObjects(){
        ArrayList<IdentifiedObject> allMIObjects=new ArrayList<IdentifiedObject>();
        Collection<IdentifiedObject> allOBOObjects=oboSession.getObjects();
        for (Iterator<IdentifiedObject> identifiedObjectIterator = allOBOObjects.iterator(); identifiedObjectIterator.hasNext();) {
            IdentifiedObject identifiedObject = identifiedObjectIterator.next();
            if(identifiedObject.getID().equalsIgnoreCase(MI_ROOT_IDENTIFIER)) {
                continue;
            }
            if(identifiedObject.getID().startsWith("MI:")){
                if (identifiedObject instanceof OBOObject) {
                    allMIObjects.add(identifiedObject);
                }//end if
            }  //end if
        }//end for


        return allMIObjects;
    }//end method


    public Collection<IdentifiedObject> getRootOBOObjects(){
        ArrayList<IdentifiedObject> rootOboObjects=new ArrayList<IdentifiedObject>();

        OBOObject rootObj = (OBOObject)oboSession.getObject(MI_ROOT_IDENTIFIER);
        Collection<Link> childLinks=rootObj.getChildren();

        for (Iterator<Link> linkIterator = childLinks.iterator(); linkIterator.hasNext();) {
            Link childLink = linkIterator.next();
            log.info("Child ID  "+childLink.getID());

            Pattern p = Pattern.compile("(MI:\\d+)-part_of->(MI:\\d+)");
            Matcher m = p.matcher(childLink.getID());
            if(m.matches()){
                log.info(" "+ m.group(1)+"  "+m.group(2));

                if(m.group(2).equalsIgnoreCase(MI_ROOT_IDENTIFIER)){
                    rootOboObjects.add(oboSession.getObject(m.group(1)));
                }

            }//end matches

        } //end for
        log.info("oboObjects "+rootOboObjects.size());
        return rootOboObjects;
    }//end method


    private CvObjectXref toXref(CvObject cvObj, String identifier_,String qualifier_,String database_) {
        Institution owner = IntactContext.getCurrentInstance().getInstitution();

        if (identifier_ == null) {
            throw new IllegalArgumentException("To create a xref, the identifier cannot be null");
        }

        CvXrefQualifier qualifierCv = null;

        if (qualifier_ != null) {
            qualifierCv = getCvObjectByLabel(CvXrefQualifier.class, qualifier_);
        } else {
            if (log.isWarnEnabled()) log.warn("No qualifier label found for: "+identifier_);
        }
        
        CvDatabase databaseCv = getCvObjectByLabel(CvDatabase.class, database_);

        log.info("qualifierCv  "+qualifierCv+ "   databaseCv  "+databaseCv);


        if(qualifierCv==null || databaseCv==null){
            if (CvDatabase.PUBMED.equalsIgnoreCase(database_)) {

                qualifierCv = CvObjectUtils.createCvObject(owner, CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF, qualifier_);
                databaseCv = CvObjectUtils.createCvObject(owner, CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED);
            } else if (CvDatabase.GO.equalsIgnoreCase(database_)) {
                qualifierCv = CvObjectUtils.createCvObject(owner, CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF, qualifier_);
                databaseCv = CvObjectUtils.createCvObject(owner, CvDatabase.class, CvDatabase.GO_MI_REF, CvDatabase.GO);
            } else if (CvDatabase.RESID.equalsIgnoreCase(database_)) {
                qualifierCv = CvObjectUtils.createCvObject(owner, CvXrefQualifier.class, CvXrefQualifier.SEE_ALSO_MI_REF, qualifier_);
                databaseCv = CvObjectUtils.createCvObject(owner, CvDatabase.class, CvDatabase.RESID_MI_REF, CvDatabase.RESID);
            } else if (CvDatabase.SO.equalsIgnoreCase(database_)) {
                qualifierCv = CvObjectUtils.createCvObject(owner, CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF, qualifier_);
                databaseCv = CvObjectUtils.createCvObject(owner, CvDatabase.class, CvDatabase.SO_MI_REF, CvDatabase.SO);
            } else if ("MOD".equalsIgnoreCase(database_)) {
                qualifierCv = CvObjectUtils.createCvObject(owner, CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF, qualifier_);
                databaseCv = CvObjectUtils.createCvObject(owner, CvDatabase.class, "MI:0897", "MOD");
            }
             else {
                log.error("Unexpected combination qualifier-database found on xref: " + qualifier_ + " - " + database_);
                return null;
            }

        }//end if

        log.debug("Returning from toXref: identifier_: "+identifier_+"  qualifierCv: "+qualifierCv+" databaseCv  "+databaseCv);
        return XrefUtils.createIdentityXref(cvObj, identifier_, qualifierCv, databaseCv);
    } //end method


    protected Alias toAlias(CvObject cvobj,String aliasName){
        Institution owner = IntactContext.getCurrentInstance().getInstitution();

        CvAliasType alias = getCvObjectByLabel(CvAliasType.class, CvAliasType.GO_SYNONYM);
        if(alias==null){
            log.info("alias ==null creating new");
            alias = CvObjectUtils.createCvObject(owner, CvAliasType.class, CvAliasType.GO_SYNONYM_MI_REF, CvAliasType.GO_SYNONYM);
        }

        String processedKey = cvKey(CvAliasType.class, alias.getMiIdentifier());
        processed.put(processedKey, alias);

        return AliasUtils.createAlias(cvobj,aliasName,alias);

    } //end alias

    protected Annotation toAnnotation(String cvTopic,String annotation) {
        Institution owner = IntactContext.getCurrentInstance().getInstitution();

        CvTopic topic = getCvObjectByLabel(CvTopic.class, cvTopic);

        if (topic == null) {
            if (CvTopic.URL.equalsIgnoreCase(cvTopic)) {
                topic = CvObjectUtils.createCvObject(owner, CvTopic.class, CvTopic.URL_MI_REF, CvTopic.URL);
            } else if (CvTopic.SEARCH_URL.equalsIgnoreCase(cvTopic)) {
                topic = CvObjectUtils.createCvObject(owner, CvTopic.class, CvTopic.SEARCH_URL_MI_REF, CvTopic.SEARCH_URL);
            } else if (CvTopic.XREF_VALIDATION_REGEXP.equalsIgnoreCase(cvTopic)) {
                topic = CvObjectUtils.createCvObject(owner, CvTopic.class, CvTopic.XREF_VALIDATION_REGEXP_MI_REF, CvTopic.XREF_VALIDATION_REGEXP);
            } else if (CvTopic.COMMENT.equalsIgnoreCase(cvTopic)) {
                topic = CvObjectUtils.createCvObject(owner, CvTopic.class, CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
            } else if (CvTopic.OBSOLETE.equalsIgnoreCase(cvTopic)) {
                topic = CvObjectUtils.createCvObject(owner, CvTopic.class, CvTopic.OBSOLETE_MI_REF, CvTopic.OBSOLETE);
                topic.setFullName(CvTopic.OBSOLETE);
            } else {
                log.error("Unexpected topic found on annotation: "+cvTopic);
                return null;
            }
        }

        log.debug("Returning from toAnnotation: owner: "+owner+"  topic: "+topic+" annotation  "+annotation);
        return new Annotation(owner, topic, annotation);
    }//end method



    protected <T extends CvObject> T getCvObjectByLabel(Class<T> cvObjectClass, String label) {

        if (label == null) {
            throw new NullPointerException("label is null");
        }

        log.info("Processed values size "+processed.size());
        for (CvObject cvObject : processed.values()) {

            //log.info("##Inside for loop "+cvObjectClass.getName()+"   "+cvObject.getClass()+"  label  "+label+"   short label " +cvObject.getShortLabel() );
            if (cvObjectClass.isAssignableFrom(cvObject.getClass()) && label.equals(cvObject.getShortLabel())) {

                log.info("########Already Present returning from getCvObjectByLabel : "+cvObjectClass+"   "+cvObject);
                return (T) cvObject;
            }
        }

        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getCvObjectDao(cvObjectClass).getByShortLabel(cvObjectClass, label);
    }  //end method


    private void addCvObjectToStatsIfObsolete(OBOObject oboObj,CvObject cvObj) {
        if (oboObj.isObsolete()) {
            //stats.getObsoleteCvs().put(oboObj.getID(), cvObj.getShortLabel());
            //implement later @todo
        }
    }//end method

    protected boolean isValidTerm(OBOObject oboObj) {
        return oboObj.getID().contains(":");
    } //end method


    public Collection<Class> getTypes() {
        Collection types = new ArrayList();

        for ( Iterator iterator = mi2Class.keySet().iterator(); iterator.hasNext(); ) {
            String key=(String)iterator.next();

            Class cvClass = (Class) mi2Class.get(key);
            types.add( cvClass );
        }

        return types;
    }//end method

    public Map<String, CvObject> getProcessed() {
        return processed;
    }

    public void setProcessed(Map<String, CvObject> processed) {
        this.processed = processed;
    }


}//end class
