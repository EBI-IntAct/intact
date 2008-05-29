package uk.ac.ebi.intact.dataexchange.cvutils.model;

import org.bbop.dataadapter.DataAdapterException;
import org.obo.dataadapter.OBOAdapter;
import org.obo.dataadapter.OBOFileAdapter;
import org.obo.datamodel.*;
import org.obo.datamodel.impl.*;
import org.obo.history.HistoryGenerator;
import org.obo.history.HistoryList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.util.DebugUtil;
import uk.ac.ebi.intact.dataexchange.cvutils.SequenceManager;
import uk.ac.ebi.intact.dataexchange.cvutils.DownloadCVs;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: prem
 * Date: 23-May-2008
 * Time: 16:02:10
 * To change this template use File | Settings | File Templates.
 */
public class DownloadCvsExtended{

    //initialize logger
    protected final static Logger log = Logger.getLogger(DownloadCvsExtended.class);

    private static final String ALIAS_IDENTIFIER = "PSI-MI-alternate";
    private static final String SHORTLABEL_IDENTIFIER = "PSI-MI-short";

    private static ObjectFactory objFactory;
    public CvDatabase psi = null;
    public CvDatabase intact = null;
    public CvXrefQualifier identity = null;
    public CvTopic definitionTopic = null;
    public CvTopic obsolete = null;


    private static OBOSession oboSession;

    public DownloadCvsExtended(){
        // Initialises required vocabularies...
        /*
        psi = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvDatabase.class).getByXref(CvDatabase.PSI_MI_MI_REF);
        if (psi == null) {
            throw new IllegalArgumentException("Could not find PSI via MI reference: " + CvDatabase.PSI_MI_MI_REF);
        }

        intact = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvDatabase.class).getByXref(CvDatabase.INTACT_MI_REF);
        if (intact == null) {
            throw new IllegalArgumentException("Could not find IntAct via MI reference: " + CvDatabase.INTACT_MI_REF);
        }

        identity = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvXrefQualifier.class).getByXref(CvXrefQualifier.IDENTITY_MI_REF);
        if (identity == null) {
            throw new IllegalArgumentException("Could not find identity via MI reference: " + CvXrefQualifier.IDENTITY_MI_REF);
        }

        definitionTopic = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvTopic.class).getByShortLabel(CvTopic.DEFINITION);
        if (definitionTopic == null) {
            throw new IllegalArgumentException("Could not find definition by its name: " + CvTopic.DEFINITION);
        }

        obsolete = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvTopic.class).getByXref(CvTopic.OBSOLETE_MI_REF);
        if (obsolete == null) {
            throw new IllegalArgumentException("Could not find definition via MI reference: " + CvTopic.OBSOLETE_MI_REF);
        }
    */
    } //end constructor

    static{
        objFactory = new DefaultObjectFactory();
        oboSession = new OBOSessionImpl(objFactory);
    }




    public void addObject(OBOClass oboObj){
        oboSession.addObject(oboObj);
    } //end method




    public void writeOBOFile(OBOSession oboSession,File outFile) throws DataAdapterException, IOException{

        final OBOFileAdapter.OBOAdapterConfiguration config = new OBOFileAdapter.OBOAdapterConfiguration();
        //File outFile = File.createTempFile("test", ".obo",new File("C:/Development/intact/intact-cvutils-obo12/src/test/resources/temp"));
        config.setWritePath(outFile.getAbsolutePath());
        OBOFileAdapter adapter = new OBOFileAdapter();
        adapter.doOperation(OBOFileAdapter.WRITE_ONTOLOGY, config, oboSession);

    }//end method

    public OBOSession convertCvList2OBOSession(List<CvDagObject> allCvs){
        
        List<CvDagObject> allUniqCvs = removeCvsDuplicated(allCvs);

        Collections.sort(allUniqCvs, new Comparator() {
            public int compare(Object o1, Object o2) {
                CvObject cv1 = (CvObject) o1;
                CvObject cv2 = (CvObject) o2;

                String id1 = getIdentifier(cv1);
                String id2 = getIdentifier(cv2);

                return id1.compareTo(id2);
            }
        });
        int counter=1;
        for(CvDagObject cvDagObj: allUniqCvs){

            if(cvDagObj.getMiIdentifier()==null){
                throw new NullPointerException("No Identifier for the cvObject "+cvDagObj);
            }
            log.info(counter+"  "+cvDagObj.getMiIdentifier());

            oboSession.addObject(getRootObject());
            OBOClass oboObj = convertCv2OBO(cvDagObj);
            oboSession.addObject(oboObj);

            /*
            for(CvDagObject parentCv: cvDagObj.getParents()){
                log.info("ParentCv   "+parentCv.getMiIdentifier());
            }

            for(CvDagObject childCv: cvDagObj.getChildren()){
                log.info("ChildCv   "+childCv.getMiIdentifier());
            } */

            counter++;
        }

        return oboSession;
    }//end method

    public  List<CvDagObject>  removeCvsDuplicated(List<CvDagObject> allCvs){

        HashMap<String,CvDagObject> cvHash = new HashMap<String,CvDagObject>();
        List<CvDagObject> allUniqCvs=new ArrayList<CvDagObject>();
        for(CvDagObject cvObj: allCvs){
            cvHash.put(cvObj.getMiIdentifier(),cvObj);
        }


        for (Iterator<String> cvDagObjectIterator = cvHash.keySet().iterator(); cvDagObjectIterator.hasNext();) {
            CvDagObject cvDagObject =  cvHash.get(cvDagObjectIterator.next());
            allUniqCvs.add(cvDagObject);
        }

        return allUniqCvs;
    }//end of method

    public OBOClass convertCv2OBO(CvObject cvObj){

        OBOClass oboObj =null;

        if(cvObj instanceof CvDagObject){
            CvDagObject dagObj = (CvDagObject)cvObj;
            if(CvObjectUtils.getIdentity(dagObj)==null){
                throw new NullPointerException("Identifier is null");
            }

            oboObj = new OBOClassImpl(dagObj.getFullName(), dagObj.getMiIdentifier());
            //assign short label

            if(dagObj.getShortLabel()!=null){
                Synonym syn = createSynonym(dagObj.getShortLabel());
                oboObj.addSynonym(syn);
            }

            //assign Xrefs
            Collection<CvObjectXref> xrefs=dagObj.getXrefs();

            log.info("xrefs size "+xrefs.size());

            for (CvObjectXref xref : xrefs) {
                boolean isIdentity=false;
                CvXrefQualifier qualifier = xref.getCvXrefQualifier();
                CvDatabase database = xref.getCvDatabase();
                String qualMi;
                String dbMi;
                log.info("dagObj "+dagObj.getShortLabel()+" "+qualifier.getMiIdentifier());
                log.info("qualifier "+qualifier.getShortLabel()+" "+qualifier.getMiIdentifier());
                log.info("database "+database.getShortLabel()+" "+database.getMiIdentifier());

                if (qualifier != null && database != null &&
                        (qualMi = qualifier.getMiIdentifier()) != null &&
                        (dbMi = database.getMiIdentifier()) != null &&
                        qualMi.equals(CvXrefQualifier.IDENTITY_MI_REF) &&
                        dbMi.equals(CvDatabase.PSI_MI_MI_REF)) {
                    isIdentity=true;
                }//end if

                if(!isIdentity){

                    String dbx = "";

                    //check for pubmed
                    if(database.getShortLabel()!=null && database.getShortLabel().equals(CvDatabase.PUBMED)){
                        //dbxref.setDatabase("PMID");
                        dbx = "PMID";
                    }else{
                        // dbxref.setDatabase(database.getShortLabel());
                        dbx=  database.getShortLabel().toUpperCase();
                    }

                    Dbxref dbxref = new DbxrefImpl(dbx,xref.getPrimaryId());
                    dbxref.setType(Dbxref.DEFINITION);
                    //dbxref.setDatabaseID(xref.getPrimaryId());
                    oboObj.addDefDbxref(dbxref);
                }//end if

            } //end for

            //assign def   from Annotations
            Collection<Annotation> annotations = dagObj.getAnnotations();

            String definitionPrefix="";
            String definitionSuffix="";
            for(Annotation annotation : annotations){

                if(annotation.getCvTopic()!=null && annotation.getCvTopic().getShortLabel()!=null){
                    CvTopic cvTopic = annotation.getCvTopic();

                    if(cvTopic.getShortLabel().equalsIgnoreCase(CvTopic.DEFINITION)){
                        definitionPrefix = annotation.getAnnotationText();
                    }
                    else if(cvTopic.getShortLabel().equalsIgnoreCase(CvTopic.URL))  {
                        definitionSuffix="\n"+annotation.getAnnotationText();
                    }
                    else if(cvTopic.getShortLabel().equalsIgnoreCase(CvTopic.SEARCH_URL)){
                        String annotationText = annotation.getAnnotationText();
                        annotationText.replaceAll("\\\\","");
                        annotationText="\""+annotationText+"\"";
                        Dbxref dbxref = new DbxrefImpl(CvTopic.SEARCH_URL,annotationText);

                        oboObj.addDbxref(dbxref);
                    }
                    else if(cvTopic.getShortLabel().equalsIgnoreCase(CvTopic.XREF_VALIDATION_REGEXP)){
                        Dbxref dbxref = new DbxrefImpl(CvTopic.XREF_VALIDATION_REGEXP,annotation.getAnnotationText());
                        oboObj.addDbxref(dbxref);
                    }
                    else if(cvTopic.getShortLabel().equalsIgnoreCase(CvTopic.COMMENT)){
                        oboObj.setComment(annotation.getAnnotationText());
                    }
                    else if(cvTopic.getShortLabel().equalsIgnoreCase(CvTopic.OBSOLETE)){
                        oboObj.setObsolete(true);
                        definitionSuffix="\n"+annotation.getAnnotationText();
                    }
                    else{
                        log.info("Annotation don't fit anywhere-----");
                    }
                } //end if
            }//end for
            oboObj.setDefinition(definitionPrefix+definitionSuffix);
            //assign alias

            for(CvObjectAlias cvAlias : dagObj.getAliases()){
                Synonym altSyn = createAlias(cvAlias);
                oboObj.addSynonym(altSyn);

            }

            //add children and parents
            //check if root

            if(checkIfRootMI(dagObj.getMiIdentifier())){
                log.info("Root Classes "+ dagObj.getMiIdentifier());
                OBOClass rootObject = getRootObject();
                //Link linkToRoot = new OBORestrictionImpl(rootObject);
                Link linkToRoot = new OBORestrictionImpl(oboObj);
                OBOProperty oboProp = new OBOPropertyImpl("part_of");
                linkToRoot.setType(oboProp);
                // oboObj.addChild(linkToRoot);
                rootObject.addChild(linkToRoot);
            }

            List<CvDagObject> cvParents = (List)dagObj.getParents();

            Collections.sort(cvParents, new Comparator() {
                public int compare(Object o1, Object o2) {
                    CvObject cv1 = (CvObject) o1;
                    CvObject cv2 = (CvObject) o2;

                    String id1 = getIdentifier(cv1);
                    String id2 = getIdentifier(cv2);

                    return id1.compareTo(id2);
                }
            });

            for (CvDagObject cvParentObj : cvParents) {
                log.info("Adding parent is_a "+ cvParentObj.getMiIdentifier());
                OBOClass isA =  convertCv2OBO(cvParentObj);
                //Link linkToIsA = new OBORestrictionImpl(isA);
                Link linkToIsA = new OBORestrictionImpl(oboObj);
                linkToIsA.setType(OBOProperty.IS_A);
                //oboObj.addChild(linkToIsA);
                isA.addChild(linkToIsA);
            }//end for


        }//outermost if

        return oboObj;
    }//end method

    private OBOClass getRootObject(){
        /*
          [Term]
          id: MI:0000
          name: molecular interaction
          def: "Controlled vocabularies originally created for protein protein interactions, extended to other molecules interactions." [PMID:14755292]
          subset: Drugable
          subset: PSI-MI slim
          synonym: "mi" EXACT PSI-MI-short []
        */

        OBOClass rootObj = new OBOClassImpl("molecular interaction", "MI:0000");
        rootObj.setDefinition("Controlled vocabularies originally created for protein protein interactions, extended to other molecules interactions.");
        //[PMID:14755292]"
        Dbxref dbxref = new DbxrefImpl("PMID","14755292");
        dbxref.setType(Dbxref.DEFINITION);
        //dbxref.setDatabaseID(xref.getPrimaryId());
        rootObj.addDefDbxref(dbxref);
        Synonym syn = new SynonymImpl();
        syn.setText("mi");
        SynonymCategory synCat = new  SynonymCategoryImpl();
        synCat.setID(SHORTLABEL_IDENTIFIER);
        syn.setSynonymCategory(synCat);
        syn.setScope(1);
        rootObj.addSynonym(syn);

        return rootObj;
    }//end of method


    private boolean checkIfRootMI(String mi){
        for(Iterator<String> miIterator = CvObjectOntologyBuilder.mi2Class.keySet().iterator();miIterator.hasNext();){
            if(mi.equalsIgnoreCase(miIterator.next())){
                return true;
            } //end if
        }//end for
        return false;
    }//end method

    private Synonym createAlias(CvObjectAlias cvAlias) {
        Synonym syn = new SynonymImpl();
        syn.setText(cvAlias.getName());
        SynonymCategory synCat = new SynonymCategoryImpl();
        synCat.setID(ALIAS_IDENTIFIER);
        syn.setSynonymCategory(synCat);
        syn.setScope(1);
        return syn;
    } //end method

    private Synonym createSynonym(String shortLabel) {
        Synonym syn = new SynonymImpl();
        syn.setText(shortLabel);
        SynonymCategory synCat = new SynonymCategoryImpl();
        synCat.setID(SHORTLABEL_IDENTIFIER);
        syn.setSynonymCategory(synCat);
        syn.setScope(1);
        return syn;
    } //end method

    public static OBOSession getOboSession() {
        return oboSession;
    } //end method


    /**
     * Selects psi-mi reference (MI:xxxx) from the given CvObject Xrefs or otherwise an IntAct reference (IA:xxxx).
     *
     * @param cvObject
     *
     * @return an mi reference or an intact reference or null if none is found.
     */
    protected String getIdentifier(CvObject cvObject) {
        String mi = null;
        String ia = null;

        mi=cvObject.getMiIdentifier();
        if(mi==null){
            throw new NullPointerException("MI Null from getIdentifier");
        }
        //for testing later uncomment this
        /*
             for (Iterator<CvObjectXref> iterator = cvObject.getXrefs().iterator(); iterator.hasNext() && mi == null;) {
                 Xref xref = iterator.next();
                 if (identity.equals(xref.getCvXrefQualifier())) {
                     if (psi.equals(xref.getCvDatabase())) {
                         mi = xref.getPrimaryId();
                     } else if (intact.equals(xref.getCvDatabase())) {
                         ia = xref.getPrimaryId();
                         if (!ia.startsWith("IA:")) {
                             log.info("WARNING: CV Term '" + cvObject.getShortLabel() + "' has an intact identity malformed: " + ia);
                         }
                     }
                 }
             }
        */
        return (mi != null ? mi : ia);
    }


} //end class
