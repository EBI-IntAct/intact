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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.obo.datamodel.IdentifiedObject;
import org.obo.datamodel.OBOObject;
import org.obo.datamodel.OBOSession;
import uk.ac.ebi.intact.dataexchange.cvutils.OboUtils;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvObjectAlias;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.Institution;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectOntologyBuilderTest {

    private static int counter=1;

    public static final Log log = LogFactory.getLog(CvObjectOntologyBuilderTest.class);

    @Test
    public void build_default() throws Exception {
        //URL url = CvObjectOntologyBuilderTest.class.getResource("/psi-mi25-next12-alias.obo");
        URL url = CvObjectOntologyBuilderTest.class.getResource("/psi-mi25-next12.obo");

        log.info("url "+url);

        OBOSession oboSession = OboUtils.createOBOSession(url);
        CvObjectOntologyBuilder ontologyBuilder = new CvObjectOntologyBuilder(oboSession);

        Collection<IdentifiedObject> rootOboObjects=ontologyBuilder.getRootOBOObjects();

        List<CvObject> rootsAndOrphans = new ArrayList<CvObject>();
        for (Iterator<IdentifiedObject> identifiedObjectIterator = rootOboObjects.iterator(); identifiedObjectIterator.hasNext();) {
            OBOObject rootObject = (OBOObject)identifiedObjectIterator.next();


            CvObject cvObjectRoot = ontologyBuilder.toCvObject(rootObject);
            rootsAndOrphans.add(cvObjectRoot);

        }//end for

        log.info("rootsAndOrphans size :"+rootsAndOrphans.size());




        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0436");
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0001");//root Cv interaction detection method
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0012");
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0192");//with GO
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0122");//with unique resid
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0460");   //def with url
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0021");   //def with OBSOLETE
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:2002");   //example with pubmed
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0108"); //example with comment subset PSI-MI slim
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:2120"); //example with comment subset Drugable
        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0031"); //example with alias

        //OBOObject testObj = (OBOObject)oboSession.getObject("MI:0244"); //example with 4 annotations + search-url

        //alias test
        /*  OBOObject testObj = (OBOObject)oboSession.getObject("MI:0303");
        CvObject cvObject=builder.toCvObject(testObj);
        testCvObject(cvObject);
        testObj = (OBOObject)oboSession.getObject("MI:0305");
        cvObject=builder.toCvObject(testObj);
        testCvObject(cvObject);

        */



        //log.info(" All OBO objects size  "+builder.getAllOBOObjects().size());
        /*
                Collection<CvObject> allCvObjects=new ArrayList<CvObject>();
                Collection<IdentifiedObject>  allOboObjects= builder.getAllOBOObjects();
                for (Iterator<IdentifiedObject> identifiedObjectIterator = allOboObjects.iterator(); identifiedObjectIterator.hasNext();) {
                    IdentifiedObject identifiedObject = identifiedObjectIterator.next();
                    OBOObject oboObject = (OBOObject)identifiedObject;
                    log.info("Converting oboObject "+oboObject);
                    CvObject cvObject=builder.toCvObject(oboObject);
                    allCvObjects.add(cvObject);
                    //testCvObject(cvObject);
                }




               log.info("allCvObjects size : "+allCvObjects.size());
                for (Iterator<CvObject> cvIterator = allCvObjects.iterator(); cvIterator.hasNext();) {
                    CvObject cvObject = cvIterator.next();
                    log.info(cvObject.getObjClass()+"  "+cvObject.getMiIdentifier());
                }

        */
        //Map<String,CvObject> processed=ontologyBuilder.getProcessed();
        // log.info("processed size : "+processed.size());




        /*
        Set<String> keys=processed.keySet();
        for (Iterator<String> stringIterator = keys.iterator(); stringIterator.hasNext();) {
            String s = stringIterator.next();
            if(s.contains("CvTopic"))
                log.info(s+"  "+processed.get(s).toString());
        }

        Set<String> keys_=processed.keySet();
        for (Iterator<String> stringIterator = keys_.iterator(); stringIterator.hasNext();) {
            String s = stringIterator.next();
            if(s.contains("CvDatabase"))
                log.info(s+"  "+processed.get(s).toString());
        }


        Set<String> keysAlias=processed.keySet();
        for (Iterator<String> stringIterator = keysAlias.iterator(); stringIterator.hasNext();) {
            String s = stringIterator.next();
            if(s.contains("CvAliasType"))
                log.info(s+"  "+processed.get(s).toString());
        }
       */
        //Assert.assertEquals(builder.findCvClassforMI("MI:0244"), uk.ac.ebi.intact.model.CvDatabase.class);//non-root object
        // Assert.assertEquals(builder.findCvClassforMI("MI:0003"), uk.ac.ebi.intact.model.CvFeatureIdentification.class);//root object
        // builder.getRootCvObjects();
        //Assert.assertEquals(16, builder.getRootCvObjects().size());
        //Assert.assertEquals(1000, builder.getAllCvObjects().size());

    } //end method


    public static void testCvObject(CvObject cvObject){


        log.info("******************"+counter+" CvObject Begin*****************************");
        counter++;
        String ac=cvObject.getAc();
        log.info("Ac->"+ac);

        String fullName=cvObject.getFullName();
        log.info("fullName->"+fullName);
        String miIdentifier=cvObject.getMiIdentifier();
        log.info("miIdentifier->"+miIdentifier);
        String objClass=cvObject.getObjClass();
        log.info("objClass->"+objClass);

        Institution owner=cvObject.getOwner();
        log.info("owner->"+owner);

        String shortLabel=cvObject.getShortLabel();
        log.info("shortLabel->"+shortLabel);

        if(cvObject.getShortLabel()==null || cvObject.getShortLabel().length()<1){
            System.exit(5);
        }



        Collection<uk.ac.ebi.intact.model.Annotation> annotations=cvObject.getAnnotations();
        int annoCount=1;
        for (Iterator<uk.ac.ebi.intact.model.Annotation> annotationIterator = annotations.iterator(); annotationIterator.hasNext();) {
            uk.ac.ebi.intact.model.Annotation annotation = annotationIterator.next();
            if(annotation!=null){
                log.info(annoCount+" AnnotationText->"+annotation.getAnnotationText());
                if(annotation.getCvTopic()!=null)
                    log.info(annoCount+" CvTopic->"+annotation.getCvTopic());

            } //end if
            annoCount++;
        } //end for


        Collection<CvObjectXref> xrefs=cvObject.getXrefs();
        int xrefCount=1;
        for (Iterator<CvObjectXref> cvObjectXrefIterator = xrefs.iterator(); cvObjectXrefIterator.hasNext();) {
            CvObjectXref cvObjectXref = cvObjectXrefIterator.next();
            log.info(xrefCount+" cvObjectXref CvDatabase-> "+cvObjectXref.getCvDatabase());
            log.info(xrefCount+" cvObjectXref CvXref Qualifier-> "+cvObjectXref.getCvXrefQualifier());
            log.info(xrefCount+" cvObjectXref CvXref PrimaryId-> "+cvObjectXref.getPrimaryId());


            xrefCount++;
        }//end for


        Collection<CvObjectAlias> aliases=cvObject.getAliases();
        int aliasCount=1;
        for (Iterator<CvObjectAlias> cvObjectAliasIterator = aliases.iterator(); cvObjectAliasIterator.hasNext();) {
            CvObjectAlias cvObjectAlias = cvObjectAliasIterator.next();

            log.info(aliasCount+" cvObjectAlias-> "+cvObjectAlias.getName()+"   "+cvObjectAlias.getParent().getShortLabel());

        } //end for


        log.info("******************CvObject End*****************************");
    } //end method





}//end class






