package uk.ac.ebi.intact.application.dataConversion;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.io.File;

/**
 * PSI-specific implementation of DataBuilder interface. This class will
 * generate a PSI-format file from the data it is supplied with. The implementation
 * is based on the <code>Graph2MIF</code> application written by Henning Mersch.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class PsiDataBuilder implements DataBuilder {

    /**
     *  logger for proper information
     *  see config/log4j.properties for more informtation
     */
    static Logger logger = Logger.getLogger("PsiDataBuilder");

    //----------- copied from Graph2MIF ---------------------

    /**
     * Keep the global experiemnt definition.
     */
    private Element globalExperimentList;

    /**
     * Distinct set of experiment AC
     */
    private HashSet globalExperiments;

    /**
     * Keep the global interactor definition.
     */
    private Element globalInteractorList;

    /**
     * The Element holding all interactions in the current entry
     */
    private Element globalInteractionList;

    /**
     * Distinct set of Interactor AC
     */
    private HashSet globalInteractors;

    private Element psiEntrySet;
    //--------------------------------------------------
    /**
     * Holds the DOm representation of the data
     */
    private Document doc;

    private RE RE  = null;


    public PsiDataBuilder() {

        //initialise a DOM-level2 Document ...
        doc = newPsiDoc(false); //don't want to initialise source yet - done during processing!
        psiEntrySet = doc.getDocumentElement();
        //DOMImplementationImpl impl = new DOMImplementationImpl();
        //The root-element is entrySet ... with a lot of Attributes ...
        //doc = impl.createDocument("net:sf:psidev:mi", "entrySet", null);
        //psiEntrySet = doc.getDocumentElement();
        //psiEntrySet.setAttribute("xmlns", "net:sf:psidev:mi");
        //psiEntrySet.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        //psiEntrySet.setAttribute("xsi:schemaLocation", "net:sf:psidev:mi http://psidev.sourceforge.net/mi/xml/src/MIF.xsd");
        //psiEntrySet.setAttribute("level", "1");
        //psiEntrySet.setAttribute("version", "1");

        // Initialise regular expression
        try {
            RE = new RE("^\\d+");
        } catch (RESyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an initialised PSI Document object. This is useful for example
     * when processing lagre datasets and information may need to be generated
     * in segments.
     * @param sourceElementNeeded true if an Intact source element is wanted, false otherwise.
     * For example internal initialisation of this class will not need a source element as
     * it is generated during processing; however for large datasets a source element may be required
     * but without affecting the Document held internally by this class (which is what the
     * generation of a PSI entry alone will do.) NB this aspect will probably be refactored
     * at some point.
     * @return Document an newly initialised PSI Document, or null if the creation
     * failed. Note that the Document root is an EntrySet with a single Entry child that has
     * its source element inititialised.
     */
    public Document newPsiDoc(boolean sourceElementNeeded) {

        Element root = null;
        Document newDoc = null;
        DOMImplementationImpl impl = new DOMImplementationImpl();
        //The root-element is entrySet ... with a lot of Attributes ...
        newDoc = impl.createDocument("net:sf:psidev:mi", "entrySet", null);
        root = newDoc.getDocumentElement();
        root.setAttribute("xmlns", "net:sf:psidev:mi");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation", "net:sf:psidev:mi http://psidev.sourceforge.net/mi/xml/src/MIF.xsd");
        root.setAttribute("level", "1");
        root.setAttribute("version", "1");

        Element psiEntry = null;
        psiEntry = newDoc.createElement("entry");

        if(sourceElementNeeded) {
            // The source element
            try {
                Element psiSource = null;
                //getId/Label are names
                psiSource = newDoc.createElement("source");
                psiEntry.appendChild(psiSource);
                Node psiNames = newDoc.importNode(getNames("IntAct", "IntAct download"), true);  //generated in a different Document...
                psiSource.appendChild(psiNames);
                psiSource.setAttribute("releaseDate", getReleaseDate());
                root.appendChild(psiEntry);

                //now have a fully inititialised PSI doc with a blank entry+source Node

            } catch (ElementNotParseableException e) {
                logger.info("source/names failed (not required):" + e.getMessage());
            } //not required here - so dont worry
        }

        return newDoc;
    }

    /**
     * @see uk.ac.ebi.intact.application.dataConversion.DataBuilder
     */
    public void writeData(String fileName, Document docToWrite) throws DataConversionException {

        try {
            File f = new File(fileName);
            DOMSource source = null;

            //decide what is to be written
            if(docToWrite == null) {
                source = new DOMSource(doc);
            }
            else {
                source = new DOMSource(docToWrite);
            }

            // Use a Transformer for output
            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            //transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");


            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);

        } catch (TransformerConfigurationException tce) {
            throw new DataConversionException("Could not generate file - Transformer config error", tce);

        } catch (TransformerException te) {
            throw new DataConversionException("Could not generate file - Transformer error", te);

        }

    }



    //--------------- private methods originally copied from graph2MIF: may not need all, --------------------------
    //--------------- and need refactoring anyway. Some will already be modified...   -----------------------------------------

    /**
     * Generate a valid XML id from an IntAct shortlabel.
     * Normally using shortlabels is fine but in a few cases they may start with
     * a digit, which is not valid as an XML id, so prefix it with a random string.
     */
    private String getValidId(String id) {
        if (RE.match(id)){
            return ("ID-" + id);
        } else {
            return id;
        }
    }

    /**
     * Create a new entry element
     * @param sourceShortLabel
     * @param sourceFullName
     * @return DOM-Object, representing an <entry>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException
     * if PSIrequired Elements are missing within the object graph
     *
     */
    private Element psiEntry(String sourceShortLabel, String sourceFullName) throws ElementNotParseableException {

        //generate DOM-Element
        Element psiEntry = null;
        psiEntry = doc.createElement("entry");

        // The source element
        try {
            Element psiSource = null;
            //getId/Label are names
            psiSource = doc.createElement("source");
            psiEntry.appendChild(psiSource);
            Element psiNames = getNames(sourceShortLabel, sourceFullName);
            psiSource.appendChild(psiNames);
            psiSource.setAttribute("releaseDate", getReleaseDate());

        } catch (ElementNotParseableException e) {
            logger.info("source/names failed (not required):" + e.getMessage());
        } //not required here - so dont worry

        //set up the global lists to hold the Experiments and Interactors....
        globalExperimentList = doc.createElement("experimentList");
        psiEntry.appendChild(globalExperimentList);
        globalExperiments = new HashSet();

        globalInteractorList = doc.createElement("interactorList");
        psiEntry.appendChild(globalInteractorList);
        globalInteractors = new HashSet();

        globalInteractionList = doc.createElement("interactionList");
        psiEntry.appendChild(globalInteractionList);

        return psiEntry;
    }


    /**
     * Generate the PSI xml for a list of experiments
     * @param experiments to convert to PSI-Format
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException
     * if PSIrequired Elements are missing within the object graph
     *
     */
    public void processExperiments(Collection experiments) throws ElementNotParseableException {

        Element psiEntry = null;
        psiEntry = psiEntry("IntAct", "IntAct download");

        for (Iterator iterator = experiments.iterator(); iterator.hasNext();) {

            Object obj = iterator.next();

            Experiment exp = null;
            try {
                //just checking for now - know we have an Experiment....
                exp = (Experiment) obj;
                doInteractionList(exp);
            } catch (ElementNotParseableException e) {
                if (exp != null)
                    logger.warn("failed to process InteractionList for Experiment "
                            + exp.getShortLabel() + ":" + e.getMessage());
            }
            System.err.println(exp.getShortLabel());
        }
        System.err.println("");

        //returning result DOMObject
        if (!psiEntry.hasChildNodes()) {
            logger.warn("graph failed, no child elements.");
            throw new ElementNotParseableException("graph has no Child Elements");
        }

        // Append the entry to the root node
        psiEntrySet.appendChild(psiEntry);
    }

    /**
     * Used to obtain a PSI Document object that is initialised and ready
     * for use when for example later appending lists. Mainly used for processing
     * large experiment sets where the interactions need to be generated in speerate
     * files - this method allows for the generation of the 'root' PSI file.
     * @return The current Document held by the builder. If called first this will
     * provide an initialised Document; if called after generations have been made then
     * it will return the current state of the Document.
     * @throws ElementNotParseableException thron if somethingis wrong with the entry format
     */
    public Document getCurrentDocument() throws ElementNotParseableException {

        if(!psiEntrySet.hasChildNodes()) {
            //first call - just initialise with a root entry and return
            Element psiEntry = null;
            psiEntry = psiEntry("IntAct", "IntAct download");
            // Append the entry to the root node
            psiEntrySet.appendChild(psiEntry);

        }
         return doc;
    }

    /**
     * Provides the set of PSI entries currently generated.
     * @return an Element containing all the current PSI entries
     */
    public Element getEntrySet() {
        return psiEntrySet;
    }

    /**
     * Provides access in Document format to the current Interactor List.
     * @return A root Element for a tree containing the current InteractorList, or null if not yet built.
     */
    public Element getInteractorList() {

//        Document result = null;
//        Element root = null;
//        try {
//            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            result = db.newDocument();
//            root = result.createElement("interactor-root");    //sets up a place-holder for the list
//            //now just import the list we want into the new document and return
//            Node newChild = result.importNode(globalInteractorList, true); //built in a different Document!!
//            root.appendChild(newChild);
//        }
//        catch(ParserConfigurationException pe) {
//            throw new DataConversionException("Unable to build a Document!", pe);
//        }
//
//        if((root != null) & (root.hasChildNodes())) result.appendChild(root);
        return globalInteractorList;
    }

    /**
     * Provides access in Document format to the current Experiment List.
     * @return An Element containing the tree of current ExperimentList, or null if not yet built.
     */
    public Element getExperimentList() {

//        Document result = null;
//        Element root = null;
//        try {
//            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            result = db.newDocument();
//            root = result.createElement("experiment-root");    //sets up a place-holder for the list
//            //now just import the list we want into the new document and return
//            Node newChild = result.importNode(globalExperimentList, true); //built in a different Document!!
//            root.appendChild(newChild);
//        }
//        catch(ParserConfigurationException pe) {
//            throw new DataConversionException("Unable to build a Document!", pe);
//        }
//
//        if((root != null) & (root.hasChildNodes())) result.appendChild(root);
        return globalExperimentList;
    }

    /**
     * Produces an XML Document which contains only an interactionList. This
     * is most likely to be of use for experiments which have very large numbers
     * of Interactions (eg over 1000), and allows the caller to obtain XML in manageable
     * 'chunks' for further processing. It is up to the caller to provide a reasonably
     * well sized Collection - more than 'limit' (see below) will be rejected.
     * @param interactions A Collections of Interactions to be proceesed
     * @param limit The maximum number of interactions allowed to be processed
     * as a single chunk.
     * @return  an XML DOM Element containing the relevant elements for the Interactions
     * @throws ElementNotParseableException thrown if the Document could not be created.
     * @throws DataConversionException thrown if the parameter size is tooo big to be processed in one chunk.
     */
    public Element buildInteractionsOnly(Collection interactions, int limit) throws
                                                DataConversionException, ElementNotParseableException {

        if(interactions.size() > limit)
            throw new DataConversionException("Too many interactions to process!");
        //NB the globalExperimentList etc will not have been set here, but are used
        //further donw in the processing!!
        Element dummy = psiEntry("dummy1", "dummy2"); //dummy to initialise things
        Document result = null;
        Element list = null;
        try {
            //First set up a DOM tree to build the data in
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            result = db.newDocument();
            list = result.createElement("interactionList");    //sets up a place-holder for the list
            Element interactionElem = null;
            //create elements for each Interaction in turn and add them to the list..
            for (Iterator it = interactions.iterator(); it.hasNext();) {
                interactionElem = doInteraction((Interaction) it.next());
                Node newChild = result.importNode(interactionElem, true); //built in a different Document!!
                list.appendChild(newChild);
            }
        }
        catch (ElementNotParseableException e) {
            //just log it
            logger.info("InteractionList could not be built:" + e.getMessage());
        }
        catch(ParserConfigurationException pe) {
            throw new DataConversionException("Unable to build a Document!", pe);
        }

        //put the list into the document and return..
        //result.appendChild(list);
        //return result;
        return list;
    }

    /**
     * process list of interactions for an Experiment
     * @param exp to convert to PSI-Format
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.simpleGraph.Edge
     */
    private void doInteractionList(Experiment exp) throws ElementNotParseableException {

        //get the Interactions from the Experiment....
        Collection interactions = exp.getInteractions();
        //OPTIMIZATION POINT??
                //Here for giot all the 20000 Interactions get loaded,
                //when the call is made to iterator...
                //A Possibility:
                //If we know it is a big Exp, maybe we can process the Collection
                //in 'chunks', by maybe adopting the same solution as for
                //the search app when retrieving giot? Thus use the proxy solution
        //for Interactions so we only pick out the Interactions one at a time
        //from the Collection via their individual proxies...
        for (Iterator it = interactions.iterator(); it.hasNext();) {
            try {

                Element psiInteraction = doInteraction((Interaction) it.next());
                globalInteractionList.appendChild(psiInteraction);
            } catch (ElementNotParseableException e) {
                logger.info("InteractionList could not be built:" + e.getMessage());
            } // not required here - so dont worry
        }
    }


    /**
     * process Interaction
     * @param interaction to convert to PSI-Format
     * @return DOM-Object, representing a <proteinInteractor>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.simpleGraph.Edge
     */
    private Element doInteraction(Interaction interaction) throws ElementNotParseableException {
	System.err.print(".");

        //generate DOM-Element
        Element psiInteraction = doc.createElement("interaction");
        //local elements processing...
        //getID and getLabel are names
        try {
            Element psiNames = this.getPsiNamesOfAnnotatedObject(interaction);
            psiInteraction.appendChild(psiNames);
        } catch (ElementNotParseableException e) {
            logger.info("Interaction names failed (not required):" + e.getMessage());
        } //not required here - so dont worry
        //now do the Experiments the interaction is part of...
        Collection experiments = interaction.getExperiments();
        Element psiExperimentList = null;
        try {
            psiExperimentList = doExperiments(experiments);
            psiInteraction.appendChild(psiExperimentList);
        } catch (ElementNotParseableException e) {
            logger.warn("experiments failed (required):" + e.getMessage());
        }

        //Now do the Interaction's Components......
        Collection components = interaction.getComponents();
        /* IntAct has interactions with one component (and stoichiometry 2).
           these are not acceptable in PSI format for now.
           Don't generate the interaction at all.
           */
        if (components.size() < 2) {
            throw new ElementNotParseableException();
        } else {
            Element psiParticipantList = doc.createElement("participantList");
            psiInteraction.appendChild(psiParticipantList);
            try {
                components = interaction.getComponents();
                Collection elements = new ArrayList();
                Element elem = null;
                for (Iterator it = components.iterator(); it.hasNext();) {
                    elem = doComponent((Component) it.next());
                    elements.add(elem);
                }
                //add them to the Document tree...
                for (Iterator it = elements.iterator(); it.hasNext();) {
                    psiParticipantList.appendChild((Element) it.next());
                }

            } catch (ElementNotParseableException e) {
                logger.warn("component failed (required):" + e.getMessage());
            }
        }

        // TODO: shouldn't it be in the experiment scope ?
        //Now do the CvInteractions for each Experiment
        //(for some reason! (CL))...
        //NB This retrieves CvInteractions, NOT CvInteractionTypes!!
        //This is WRONG because CvInteraction defines the method used to detect
        //the interaction, NOT the type of interaction itself
        //(eg binary interaction, aggregation).
        //Collection interactionTypes = getRelInteractionTypes(experiments);

        //for (Iterator it = interactionTypes.iterator(); it.hasNext();) {
            //CvInteraction cvInteraction = (CvInteraction) it.next();
            //Element psiInteractionType = null;
            //try {

                //NB -This is the bit where the CvInteractionType Xref should be generated,
                //and this needs to always be mapped to primary ref MI:0191...
                //PROBLEM seems to be that we have a CvInteraction here,
                //NOT a CvInteractionType!!
                //psiInteractionType = doCvInteraction(cvInteraction);
                //psiInteraction.appendChild(psiInteractionType);
            //} catch (ElementNotParseableException e) {
                //logger.info("CvInteraction failed (not required):" + e.getMessage());
            //} // not one is required - so dont worry
        //}

        //----------------- CvInteractionType fix ----------------------------------
        //CL 23/04/04: Code fix to get at the CvInteractionType. This may be a
        //temporary fix as the PSI schema says an Interaction may have many
        //CvInteractionTypes - however the IntactCore model Interaction class only
        //has a single one. Something needs to be changed at some point, therefore...
        //Process the Interactions' CvInteractionType...
        CvInteractionType cvType = interaction.getCvInteractionType();

        //need to check it is there - if not skip ti.
        //NB PSI schema says interactionType is 0..many
        if(cvType != null) {
            try {
                Element psiInteractionType = null;
                psiInteractionType = doCvInteractionType(cvType);
                psiInteraction.appendChild(psiInteractionType);
            }
            catch (ElementNotParseableException e) {
                logger.info("CvInteractionType failed (not required):" + e.getMessage());
            } // not mandatory - safe to ignore
        }

        //Now do the Interaction's Xrefs...
        try {
            //every interaction has an Intact primary Xref - any other
            //Xrefs it has are generated as secondary ones.
            //NB this call generates an xref Element with a primary Intact ref
            //as a child (it is processed differently to other Xrefs as the
            //Intact one does not exist in data and we use the Interaction
            //details to build it)...
            Element psiXref = doIntactXref(interaction);
            Collection xrefs = interaction.getXrefs();
            Element secondaryRef = null;
            if(!xrefs.isEmpty()) {
                //put all the others in as secondary refs...
                for(Iterator it = xrefs.iterator(); it.hasNext();) {
                    secondaryRef = procSecondaryRef((Xref)it.next());
                    psiXref.appendChild(secondaryRef);
                }
            }
            psiInteraction.appendChild(psiXref);
        } catch (ElementNotParseableException e) {
            logger.info("xref failed (not required):" + e.getMessage());
        }

        //Now do the Interaction's Annotation...
        if (null != interaction.getAnnotations()) {
            try {
                Collection annotations = interaction.getAnnotations();
                Element psiAttributeList = doAnnotations(annotations);
                psiInteraction.appendChild(psiAttributeList);
            } catch (ElementNotParseableException e) {
                logger.info("no annotations (not required):" + e.getMessage());
            }
        }

        //returning result DOMObject
        if (!psiInteraction.hasChildNodes()) {
            logger.warn("edge failed, no child elements.");
            throw new ElementNotParseableException("Interaction has no Child Elements");
        }
        return psiInteraction;
    }


    /**
     * process experiments
     * @param experiments to convert to PSI-Format
     * @return DOM-Object, representing a <experimentList>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.Experiment
     */
    private Element doExperiments(Collection experiments) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiExperimentList = doc.createElement("experimentList");
        //local elements processing...
        Iterator experimentList = experiments.iterator();
        if (!experimentList.hasNext()) {
            logger.warn("experimentlist without one experiment -  failed (required):");
        }
        while (experimentList.hasNext()) {
            Experiment experiment = (Experiment) experimentList.next();
            try {
                if (!globalExperiments.contains(experiment.getAc())) {
                    Element psiExperimentDescription = doExperiment(experiment);
                    globalExperimentList.appendChild(psiExperimentDescription);
                    globalExperiments.add(experiment.getAc());
                }
                Element psiExperimentRef = doExperimentRef(experiment);
                psiExperimentList.appendChild(psiExperimentRef);
            } catch (ElementNotParseableException e) {
                logger.info("experiment failed (not required):" + e.getMessage());
            } //not required - so dont worry
        }
        //returning result DOMObject
        if (!psiExperimentList.hasChildNodes()) {
            logger.warn("experiments failed, no child elements.");
            throw new ElementNotParseableException("ExperimentList has no Child Elements");
        }
        return psiExperimentList;
    }

    /**
     * process experiment
     * @param experiment to convert to PSI-Format
     * @return DOM-Object, representing a <exteriment>
     * @see uk.ac.ebi.intact.model.Experiment
     */
    private Element doExperimentRef(Experiment experiment) {
        //generate DOM-Element
        Element psiExperimentRef = doc.createElement("experimentRef");
        psiExperimentRef.setAttribute("ref", experiment.getAc());
        return psiExperimentRef;
    }

    /**
     * process experiment
     * @param experiment to convert to PSI-Format
     * @return DOM-Object, representing a <exteriment>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.Experiment
     */
    private Element doExperiment(Experiment experiment) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiExperimentDescription = doc.createElement("experimentDescription");
        //local elements processing...
        //getFullName & getShortLabel
        try {
            Element psiNames = getPsiNamesOfAnnotatedObject(experiment);
            psiExperimentDescription.appendChild(psiNames);
        } catch (ElementNotParseableException e) {
            logger.info("names failed (not required):" + e.getMessage());
        } //not required here - so dont worry
        // getXref
        try {
            Element psiXrefPubMed = doXrefCollection(experiment.getXrefs(), "pubmed");
            Element psiBibref = doc.createElement("bibref");
            psiBibref.appendChild(psiXrefPubMed);
            psiExperimentDescription.appendChild(psiBibref);
        } catch (ElementNotParseableException e) {
            logger.info("xref(pubmed) failed (not required):" + e.getMessage());
        } //not required here - so dont worry
        try {
            Element psiXrefNotPubMed = doXrefCollectionSelectingNotPubMed(experiment.getXrefs());
            psiExperimentDescription.appendChild(psiXrefNotPubMed);
        } catch (ElementNotParseableException e) {
            logger.info("xref(not pubmed) failed (not required):" + e.getMessage());
        } //not required here - so dont worry

        // getBioSource()
        try {
            Element psiHostOrganism = doBioSourceAsHost(experiment.getBioSource());
            psiExperimentDescription.appendChild(psiHostOrganism);
        } catch (ElementNotParseableException e) {
            logger.info("biosource failed (not required):" + e.getMessage());
        } // not required here - so dont worry
        // getBioSourceAc() @todo
        // getCvIdentificationAc() @todo
        // getCvInteraction() is interactiondetection
        try {
            Element psiInteractionDetection = doCvInteractionDetection(experiment.getCvInteraction());
            psiExperimentDescription.appendChild(psiInteractionDetection);
        } catch (ElementNotParseableException e) {
            logger.warn("cvInteraction failed (required):" + e.getMessage());
        }
        // getCvIdentification()
        try {
            Element psiIdentification = doCvIdentification(experiment.getCvIdentification());
            psiExperimentDescription.appendChild(psiIdentification);
        } catch (ElementNotParseableException e) {
            logger.info("cvIdentification failed (not required):" + e.getMessage());
        } // not required here - so dont worry
        //
        // PSI - FeatureDetection is IntAct cvFeatureIdentification
        // Due to mapping problems this is not yet implemented
        // In IntAct a Feature is not part of Experiement so retireval would
        // be a Collection Experiement->Interaction->Component->Feature->cvFeatureIdentification
        // and than one has do decide which one should be represented in PSI ...
        // Feature isn't implemnted in PSI so we do not care
        //
        // getCvInteractionAc() @todo
        // getInteraction() @todo
        // getRelatedExperiment() @todo
        // getRelatedExperimentAc() @todo
        // getAnnotation @todo
        // getCurator @todo
        // getCuratorAc @todo
        // getMenuList @todo
        // getReference @todo
        // getAc
        psiExperimentDescription.setAttribute("id", experiment.getAc());
        // getCreated @todo
        // getEvidence - not defined
        // getOwner @todo
        // getOwnerAc @todo
        // getUpdated  @todo

        //Now do the Experiment's Annotation...
        if (null != experiment.getAnnotations()) {
            try {
                Collection annotations = experiment.getAnnotations();
                Element psiAttributeList = doAnnotations(annotations);
                psiExperimentDescription.appendChild(psiAttributeList);
            } catch (ElementNotParseableException e) {
                logger.info("no annotations (not required):" + e.getMessage());
            }
        }

        //returning result DOMObject
        if (!psiExperimentDescription.hasChildNodes()) {
            logger.warn("Experiment failed, no child elements.");
            throw new ElementNotParseableException("ExperimentDescription has no Child Elements");
        }
        return psiExperimentDescription;
    }

    /**
     * process cvInteractionDetection
     * @param cvInteractionDetection to convert to PSI-Format
     * @return DOM-Object, representing a <interactionDetection>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.CvInteraction
     */
    private Element doCvInteractionDetection(CvInteraction cvInteractionDetection)
            throws ElementNotParseableException {
        //generate DOM-Element

        Element psiInteractionDetection = doc.createElement("interactionDetection");

        //CvInteraction is not compulsory for an Experiment (at present)...
        if (cvInteractionDetection != null) {
            //local elements processing...
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(cvInteractionDetection);
                psiInteractionDetection.appendChild(psiNames);
            } catch (ElementNotParseableException e) {
                logger.warn("names failed (required):" + e.getMessage());
            }
            try {
                Element psiXref = doXrefCollection(cvInteractionDetection.getXrefs(), "psi-mi");
                psiInteractionDetection.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.warn("xref failed (required):" + e.getMessage());
            }
            //returning result DOMObject
            if (!psiInteractionDetection.hasChildNodes()) {
                logger.warn("cvInteractionDetection failed, no child elements.");
                throw new ElementNotParseableException("interactionDetection has no Child Elements");
            }
        }
        return psiInteractionDetection;
    }

    /**
     * process cvInteractionType
     * @param cvInteractionType to convert to PSI-Format
     * @return DOM-Object, representing a <interactionDetection>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.CvInteraction
     */
    private Element doCvInteractionType(CvInteractionType cvInteractionType) throws ElementNotParseableException {
        /* TODO: could be factorized with the doCvInteractionDetection if
           TODO: the name of the node is given in parameter.
         */
        //generate DOM-Element
        Element psiInteractionDetection = doc.createElement("interactionType");
        //possible CvInteraction is not defined......
        if (cvInteractionType != null) {
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(cvInteractionType);
                psiInteractionDetection.appendChild(psiNames);
            } catch (ElementNotParseableException e) {
                logger.warn("names failed (required):" + e.getMessage());
            }
            try {
                Element psiXref = doXrefCollection(cvInteractionType.getXrefs(), "psi-mi");
                psiInteractionDetection.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.warn("xref failed (required):" + e.getMessage());
            }
            //returning result DOMObject
            if (!psiInteractionDetection.hasChildNodes()) {
                logger.warn("cvInteractionType failed, no child elements.");
                throw new ElementNotParseableException("interactionDetection has no Child Elements");
            }
        }
        return psiInteractionDetection;
    }

    /**
     * process cvIdentification
     * @param cvIdentification to convert to PSI-Format
     * @return DOM-Object, representing a <participantDetection>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.CvIdentification
     */
    private Element doCvIdentification(CvIdentification cvIdentification) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiParticipantDetection = doc.createElement("participantDetection");
        //CvIdentification is not mandatory for an Experiment (at present)...
        if (cvIdentification != null) {
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(cvIdentification);
                psiParticipantDetection.appendChild(psiNames);
            } catch (ElementNotParseableException e) {
                logger.warn("names failed (required):" + e.getMessage());
            }
            try {
                Element psiXref = doXrefCollection(cvIdentification.getXrefs(), "psi-mi");
                psiParticipantDetection.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.warn("xref failed (required):" + e.getMessage());
            }
            //returning result DOMObject
            if (!psiParticipantDetection.hasChildNodes()) {
                logger.warn("cvIdentification failed, no child elements.");
                throw new ElementNotParseableException("ParticipantDetection has no Child Elements");
            }
        }
        return psiParticipantDetection;
    }

    /**
     * process component
     * @param component to convert to PSI-Format
     * @return DOM-Object, representing a <proteinParticipant>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.Component
     */
    private Element doComponent(Component component) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiProteinParticipant = doc.createElement("proteinParticipant");
        //local elements processing...
        //getInteractor()
        //possible the component is not specified....
        if (component != null) {
            try {
                Interactor interactor = component.getInteractor();
                if (!globalInteractors.contains(interactor.getAc())) {
                    Element psiProteinInteractor = doInteractor(interactor);
                    globalInteractorList.appendChild(psiProteinInteractor);
                    globalInteractors.add(interactor.getAc());
                }

                Element psiProteinInteractorRef = doInteractorRef(interactor);
                psiProteinParticipant.appendChild(psiProteinInteractorRef);
            } catch (ElementNotParseableException e) {
                logger.warn("interactor failed (required):" + e.getMessage());
            }
            //getBindingDomain
            try {
                Element psiFeatureList = doFeatureList(component.getBindingDomains());
                psiProteinParticipant.appendChild(psiFeatureList);
            } catch (ElementNotParseableException e) {
                logger.info("featureList failed (not required):" + e.getMessage());
            } //not required here - dont worry
            //getcvComponentRole
            Element psiRole = doRole(component.getCvComponentRole());
            psiProteinParticipant.appendChild(psiRole);
            // getCvComponentRoleAc() @todo
            // getExpressedIn() @todo
            // getExpressedInAc() @todo
            // getInteraction() @todo
            // getInteractionAc() @todo
            // getInteractorAc() @todo
            // getStoichiometry()  @todo
            // getCreated @todo
            // getEvidence @todo
            // getOwner @todo
            // getOwnerAc @todo
            // getUpdated @todo
            //returning result DOMObject
            if (!psiProteinParticipant.hasChildNodes()) {
                logger.warn("component failed, no child elements.");
                throw new ElementNotParseableException("proteinParticipant has no Child Elements");
            }
        }
        return psiProteinParticipant;
    }

    /**
     * process role
     * @param cvComponentRole to convert to PSI-Format
     * @return DOM-Object, representing a <featureList>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.CvComponentRole
     */
    private Element doRole(CvComponentRole cvComponentRole) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiRole = doc.createElement("role");
        //should be there, but you never know...
        if (cvComponentRole != null) {
            String role = cvComponentRole.getShortLabel();

            /* The IntAct CvComponentRole has more terms than the current PSI role attribute.
            Therefore some remapping needs to be done.
            */
            if (role.equals("agent")) {
                role = "bait";
            }
            if (role.equals("complex")) {
                role = "unspecified";
            }
            if (role.equals("self")) {
                role = "neutral";
            }
            if (role.equals("target")) {
                role = "prey";
            }

            psiRole.appendChild(doc.createTextNode(role));
            //returning result DOMObject
            if (!psiRole.hasChildNodes()) {
                logger.warn("cvComponentRole failed, no child elements.");
                throw new ElementNotParseableException("role has no Child Elements");
            }
        }
        return psiRole;
    }

    /**
     * process FeatureList
     * @param features to convert to PSI-Format
     * @return DOM-Object, representing a <featureList>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.Feature
     */
    private Element doFeatureList(Collection features) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiFeatureList = doc.createElement("featureList");
        //local elements processing...
        Iterator featureList = features.iterator();
        if (!featureList.hasNext()) {
            logger.warn("empty FeatureList failed (required):");
        }
        while (featureList.hasNext()) {
            Feature feature = (Feature) featureList.next();
            try {
                Element psiFeature = doFeature(feature);
                psiFeatureList.appendChild(psiFeature);
            } catch (ElementNotParseableException e) {
                logger.info("feature failed (not required):" + e.getMessage());
            } // not required here - so dont worry
        }
        //returning result DOMObject
        if (!psiFeatureList.hasChildNodes()) {
            logger.warn("featurelist failed, no child elements.");
            throw new ElementNotParseableException("FeatureList has no Child Elements");
        }
        return psiFeatureList;
    }

    /**
     * process Feature
     * @param feature to convert to PSI-Format
     * @return DOM-Object, representing a <featureList>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.Feature
     */
    private Element doFeature(Feature feature) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiFeature = doc.createElement("feature");
        //local elements processing...
        // getBoundDomain() @todo
        // getBoundDomainAc() @todo
        // getComponent() @todo
        // getComponentAc() @todo
        // getCvFeatureIdentificationAc() @todo
        // getProtein() @todo
        // getProteinAc() @todo
        // getXrefs()
        try {
            Element psiXref = doXref(feature.getXref());
            psiFeature.appendChild(psiXref);
        } catch (ElementNotParseableException e) {
            logger.info("xref failed (not required):" + e.getMessage());
        }  //not required here - so dont worry
        // getCvFeatureType()
        try {
            Element psiFeatureDescription = doCvFeatureType(feature.getCvFeatureType());
            psiFeature.appendChild(psiFeatureDescription);
        } catch (ElementNotParseableException e) {
            logger.info("cvFeatureTyoe failed (not required):" + e.getMessage());
        }  //not required here - so dont worry
        // getCvFeatureIdentification()
        try {
            Element psiFeatureDetection = doCvFeatureIdentification(feature.getCvFeatureIdentification());
            psiFeature.appendChild(psiFeatureDetection);
        } catch (ElementNotParseableException e) {
            logger.info("cvFeatureIdentification failed (not required):" + e.getMessage());
        }  //not required here - so dont worry
        // psi Location is not yet implemented - but required - so we have to throw an exception anyway
        try {
            Element psiLocation = doLocation();
            psiFeature.appendChild(psiLocation);
        } catch (ElementNotParseableException e) {
            logger.warn("location failed (required):" + e.getMessage());
        }
        // getRange()  @todo
        // getXrefAc() @todo
        // getAc @todo
        // getCreated @todo
        // getEvidence
        // getOwner @todo
        // getOwnerAc @todo
        //  getUpdated @todo
        //returning result DOMObject
        if (!psiFeature.hasChildNodes()) {
            logger.warn("feature failed, no child elements.");
            throw new ElementNotParseableException("Feature has no Child Elements");
        }
        return psiFeature;
    }

    /**
     * process Location - which is not yet implemented in IntAct
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.Feature (Binding Domain)
     */
    private Element doLocation() throws ElementNotParseableException {
        logger.warn("location failed (required): NOT IMPLEMENTED");
        throw new ElementNotParseableException("not implemented in IntAct");
    }

    /**
     * process cvFeatureType
     * @param cvFeatureType to convert to PSI-Format
     * @return DOM-Object, representing a <featureDescription>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.CvFeatureType
     */
    private Element doCvFeatureType(CvFeatureType cvFeatureType) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiFeatureDescription = doc.createElement("featureDescription");
        //CvFeatureType may not be present...
        if (cvFeatureType != null) {
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(cvFeatureType);
                psiFeatureDescription.appendChild(psiNames);
            } catch (ElementNotParseableException e) {
                logger.warn("names failed (required):" + e.getMessage());
            }
            try {
                Element psiXref = doXrefCollection(cvFeatureType.getXrefs(), "psi-mi");
                psiFeatureDescription.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.warn("xref failed (required):" + e.getMessage());
            }
            //returning result DOMObject
            if (!psiFeatureDescription.hasChildNodes()) {
                logger.warn("cvFeatureType failed, no child elements.");
                throw new ElementNotParseableException("FeatureDescription has no Child Elements");
            }
        }
        return psiFeatureDescription;
    }

    /**
     * process cvFeatureIdentification
     * @param cvFeatureIdentification to convert to PSI-Format
     * @return DOM-Object, representing a <featureDescription>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.CvFeatureIdentification
     */
    private Element doCvFeatureIdentification(CvFeatureIdentification cvFeatureIdentification) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiFeatureDetection = doc.createElement("featureDetection");
        //parameter may not be defined...
        if (cvFeatureIdentification != null) {
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(cvFeatureIdentification);
                psiFeatureDetection.appendChild(psiNames);
            } catch (ElementNotParseableException e) {
                logger.warn("cvFeatureIdentification failed (required):" + e.getMessage());
            }
            try {
                Element psiXref = doXrefCollection(cvFeatureIdentification.getXrefs(), "psi-mi");
                psiFeatureDetection.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.warn("xref failed (required):" + e.getMessage());
            }

            //returning result DOMObject
            if (!psiFeatureDetection.hasChildNodes()) {
                logger.warn("cvFeatureIdentification failed, no child elements.");
                throw new ElementNotParseableException("FeatureDetection has no Child Elements");
            }
        }
        return psiFeatureDetection;
    }

    /**
     * process interactor
     * @param interactor to convert to PSI-Format
     * @return DOM-Object, representing a <proteinInteractor>
     * @see uk.ac.ebi.intact.model.Interactor
     */
    private Element doInteractorRef(Interactor interactor) {
        //generate DOM-Element
        Element psiProteinInteractorRef = doc.createElement("proteinInteractorRef");
        //param may not be defined....
        if (interactor != null)
            psiProteinInteractorRef.setAttribute("ref", getValidId(interactor.getAc()));

        //returning result DOMObject
        return psiProteinInteractorRef;
    }

    /**
     * process interactor
     * @param interactor to convert to PSI-Format
     * @return DOM-Object, representing a <proteinInteractor>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if PSIrequired Elements are missing within graph
     * @see uk.ac.ebi.intact.model.Interactor
     */
    private Element doInteractor(Interactor interactor) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiProteinInteractor = doc.createElement("proteinInteractor");
        //local elements processing...
        //getBioSourceAc() @todo
        //getProduct() @todo
        //getAnnotation @todo
        //getCurator @todo
        //getCuratorAc @todo
        //getShortLabel, getFullName -> names
        //param may not be defined...
        if (interactor != null) {
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(interactor);
                psiProteinInteractor.appendChild(psiNames);
            } catch (ElementNotParseableException e) {
                logger.warn("names failed (required):" + e.getMessage());
            }
            //getMenuList @todo
            //getReference @todo
            //getXref
            try {
                Element psiXref = doXrefCollection(interactor.getXrefs(), "uniprot");
                psiProteinInteractor.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.info("xref failed (not required):" + e.getMessage());
            }  //not required here - so dont worry
            //getAc @todo
            psiProteinInteractor.setAttribute("id", getValidId(interactor.getAc()));

            //getCreated @todo
            //getEvidence @todo
            //getOwner @todo
            //getOwnerAc @todo
            //getBioSource()
            try {
                Element psiOrganism = doBioSource(interactor.getBioSource());
                psiProteinInteractor.appendChild(psiOrganism);
            } catch (ElementNotParseableException e) {
                logger.info("BioSource failed (not required):" + e.getMessage());
            } // not required here - so dont worry

            //sequence is optional, don't generate it for now
            /*try {
                Element psiSequence = doSequence((Protein) interactor);
                psiProteinInteractor.appendChild(psiSequence);
            } catch (ElementNotParseableException e) {
                logger.info("sequence failed (not required):" + e.getMessage());
            } //not required here - so dont worry
            */

            //returning result DOMObject
            if (!psiProteinInteractor.hasChildNodes()) {
                logger.warn("Interactor failed, no child elements.");
                throw new ElementNotParseableException("ProteinInteractor has no Child Elements");
            }
        }
        return psiProteinInteractor;
    }

    /**
     * processing protein
     * @param protein - Object  to convert to PSI-Format
     * @return DOM-Object, representing a <sequence>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Protein
     */
    private Element doSequence(Protein protein) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiSequence = doc.createElement("sequence");
        //param may not be there...
        if (protein != null) {

            if (protein.getSequence() != null) {
                psiSequence.appendChild(doc.createTextNode(protein.getSequence()));
                //returning result DOMObject
                if (!psiSequence.hasChildNodes()) {
                    logger.warn("protein failed, no child elements.");
                    throw new ElementNotParseableException("Sequence has no Child Elements");
                }
            } else {
                System.out.println("Protein " + protein.getShortLabel() + " has no sequence..");
            }
        }
        return psiSequence;
    }


    /**
     * This method gets an Collection of Xfres and, beacause PSI often does not allow a list of xrefs,
     * takes the 1st primary ref as psiPrimaryRef and all others as secondary Refs.
     * if no SPTR xref is found, make the first one the primary Xref.
     * This SPTR Xref is only valid for Interactor, however that function is used also with :
     * CvInteraction
     * CvIdentification
     * CvFeature
     * cvFeatureIdentification
     * Interactor
     * Interaction
     *
     * @param xrefs - Object  to convert to PSI-Format
     * @return DOM-Object, representing a <xref>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Xref
     */
    private Element doXrefCollection(Collection xrefs, String primaryRefLabel) throws ElementNotParseableException {
        // keep track of the existence of a primary Xref
        Xref primaryXref = null;

        //generate DOM-Element
        Element psiXref = doc.createElement("xref");


            //the first SPTR Intact Xref will become primary & secondary ref in PSI
            for (Iterator it = xrefs.iterator(); it.hasNext();) {
                Xref xref = (Xref) it.next();
                //check for parent being an interaction
                // - they should be handled differently...
                String dbShortLabel = xref.getCvDatabase().getShortLabel();
                try {
                    if ((dbShortLabel.equalsIgnoreCase(primaryRefLabel) && xref.getPrimaryId() != null) //SPTR found
                            || !it.hasNext()) { //if no SPTR found, take any (here we take last)
                        Element psiPrimaryRef = doPrimaryRef(xref);
                        psiXref.appendChild(psiPrimaryRef);
                        primaryXref = xref;
                        break;
                    }
                } catch (ElementNotParseableException e) { //dont worry - try next one
                    logger.warn("Xref without primary db or id found ! Ignoring !");
                }
            }

        /* secondary xrefs are optional, don't do them

        //the rest becomes secondary Refs
        Iterator iteratorSnd = xrefs.iterator();
        while (iteratorSnd.hasNext()) {
            Xref xref = (Xref) iteratorSnd.next();
            try {
                Element psiRef = null;
                if (primaryXref == null)
                    psiRef = doPrimaryRef(xref);
                else {
                    if (primaryXref == xref) break; // don't put the primary as secondary !
                    psiRef = procSecondaryRef(xref);
                }
                psiXref.appendChild(psiRef);
            } catch (ElementNotParseableException e) {
                logger.warn("Xref without primary db or id found ! Ignoring !"); // not required here - so dont worry
            }
        }
        */

        //returning result DOMObject
        if (!psiXref.hasChildNodes()) {
            logger.warn("xrefcollection failed, no child elements.");
            throw new ElementNotParseableException("Xref has no Child Elements");
        }
        return psiXref;
    }


    /**
     * processing protein
     * @param annotation to convert to PSI-Format
     * @return DOM-Object, representing a psi <attribute>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Protein
     */
    private Element doAnnotation(Annotation annotation) throws ElementNotParseableException {

        if (null == annotation){
            throw new ElementNotParseableException("Annotation is null");
        }

        //filter the object - NB if this grows any more, put into a filter method instead..
        if (annotation.getCvTopic().getShortLabel().equals("remark")){
            throw new ElementNotParseableException("Annotation with topic 'remark' not exported.");
        }

        if (annotation.getCvTopic().getShortLabel().equals("uniprot-dr-export")){
            throw new ElementNotParseableException("Annotation with topic 'uniprot-dr-export' not exported.");
        }
        //generate DOM-Element
        Element psiAttribute = doc.createElement("attribute");
        psiAttribute.setAttribute("name", annotation.getCvTopic().getShortLabel());
        if (null != annotation.getAnnotationText()) {
            psiAttribute.appendChild(doc.createTextNode(annotation.getAnnotationText()));
        } else {
            psiAttribute.appendChild(doc.createTextNode(""));
        }

        return psiAttribute;
    }

    /**
     * This method gets an Collection of annotations and returns it as an attributeList.
     * @param annotations to convert to a PSI attributelist.
     * @return DOM-Object, representing an <attributeList>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Annotation
     */
    private Element doAnnotations(Collection annotations)
            throws ElementNotParseableException {

        if (null == annotations) {
            throw new ElementNotParseableException("no annotation");
        }

        //generate DOM-Element
        Element psiAttributeList = doc.createElement("attributeList");

        for (Iterator it = annotations.iterator(); it.hasNext();) {
            Annotation annotation = (Annotation) it.next();
            try {
                Element psiAttribute = doAnnotation(annotation);
                psiAttributeList.appendChild(psiAttribute);
            } catch (ElementNotParseableException e) {
                // Do nothing, just ignore this annotation
            }
        }

        //returning result DOMObject
        if (!psiAttributeList.hasChildNodes()) {
            logger.info("no annotation");
            throw new ElementNotParseableException("No annotation.");
        }
        return psiAttributeList;
    }

    /**
     * This method gets a Xref and convert it to xref with one primaryRef
     * @param xref - Object  to convert to PSI-Format
     * @return DOM-Object, representing a <xref>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Xref
     */
    private Element doXref(Xref xref) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiXref = doc.createElement("xref");
        //local elements processing...
        try {
            Element psiPrimaryRef = doPrimaryRef(xref);
            psiXref.appendChild(psiPrimaryRef);
        } catch (ElementNotParseableException e) {
            logger.warn("Xref without primary db or id found ! Ignoring !");
        }
        //returning result DOMObject
        if (!psiXref.hasChildNodes()) {
            logger.warn("xref failed, no child elements.");
            throw new ElementNotParseableException("Xref has no Child Elements");
        }
        return psiXref;
    }
    /**
     * This method generates a PSI element for an Intact Xref. That is, for
     * Interactions which currently have no external DB references, we generate
     * a primary Xref to the Intact DB for them.
     * This code is really an intermediate 'modification' because the error is currently
     * in the data - we need to hav CV data for all Interactions in Intact which provides
     * this information. When the DB is corrected this code should be removed.
     * @param interaction - the Interaction we want an Intact reference for
     * @return DOM-Object, representing a <xref>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Xref
     */
    private Element doIntactXref(Interaction interaction) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiXref = doc.createElement("xref");
        Element psiPrimaryRef = doc.createElement("primaryRef");
        try {
            psiPrimaryRef.setAttribute("db", "intact");
            psiPrimaryRef.setAttribute("id", interaction.getAc()); //the interaction AC
            psiPrimaryRef.setAttribute("secondary", interaction.getShortLabel());
            psiXref.appendChild(psiPrimaryRef);

        } catch (NullPointerException e) {
            logger.warn("xref without db or id - failed (required):");
            throw new ElementNotParseableException("PrimaryRef without id or db");
        }

        //NB currently there is no DB release version available in data for Intact
        //though later this will need to be included if it exists
        //try {
            //psiPrimaryRef.setAttribute("version", xref.getDbRelease());
        //} catch (NullPointerException e) {
            //logger.info("no dbRelease");
        //} // not required here - so dont worry

        if (!psiXref.hasChildNodes()) {
            logger.warn("xref failed, no child elements.");
            throw new ElementNotParseableException("Xref has no Child Elements");
        }
        return psiXref;
    }


    /**
     * This method gets an Collection of Xfres and, beacause PSI often does not allow a list of xrefs,
     * takes the 1st primary ref as psiPrimaryRef,  and all others as secondary Refs.
     * But only from NOT PubMed-DB will be processed.
     * @param xrefs - Object  to convert to PSI-Format
     * @return DOM-Object, representing a <xref>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Xref
     */
    private Element doXrefCollectionSelectingNotPubMed(Collection xrefs) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiXref = doc.createElement("xref");
        //local elements processing...
        Iterator iteratorPrim = xrefs.iterator();
        while (iteratorPrim.hasNext()) {
            Xref xref = (Xref) iteratorPrim.next();
            try {
                if (!(xref.getCvDatabase().getShortLabel().equalsIgnoreCase("PubMed") && xref.getPrimaryId() != null)) { //PubMed found
                    Element psiPrimaryRef = doPrimaryRef(xref);
                    psiXref.appendChild(psiPrimaryRef);
                    xrefs.remove(xref); //this was already processed
                    break;
                }
            } catch (ElementNotParseableException e) { //dont worry - try next one
                logger.warn("Xref without primary db or id found ! Ignoring !");
            }

        }
        //if we could not create a primaryID - throw exception
        if (!psiXref.hasChildNodes()) {
            logger.warn("xrefcol failed, no child elements.");
            throw new ElementNotParseableException("couldn't generate primaryID - Xref not parseabel");
        }
        //the rest becomes secondary Refs
        Iterator iteratorSnd = xrefs.iterator();
        while (iteratorSnd.hasNext()) {
            Xref xref = (Xref) iteratorSnd.next();
            try {
                if (!(xref.getCvDatabase().getShortLabel().equalsIgnoreCase("PubMed") && xref.getPrimaryId() != null)) { //PubMed found
                    Element psiSecondaryRef = procSecondaryRef(xref);
                    psiXref.appendChild(psiSecondaryRef);
                }
            } catch (ElementNotParseableException e) {
                logger.warn("Xref without primary db or id found ! Ignoring !"); // not required here - so dont worry
            }

        }
        //returning result DOMObject
        if (!psiXref.hasChildNodes()) {
            logger.warn("xrefcol failed, no child elements.");
            throw new ElementNotParseableException("Xref has no Child Elements");
        }
        return psiXref;
    }

    /**
     * This method gets a Xref and returns a PSI PrimaryRef.
     * @param xref - Object  to convert to PSI-Format
     * @return DOM-Object, representing a <primaryRef>
     * @see uk.ac.ebi.intact.model.Xref
     */
    private Element doPrimaryRef(Xref xref) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiPrimaryRef = doc.createElement("primaryRef");
        //local elements processing...
        try {
            //NB for giot interaction elements, db=intact, id=the AC, secondary=the shortLabel
            //Q: does this apply to ALL interactions??
           // if(isInteraction) {
                //Currently all interaction data is assumed to come from our
                //intact DB - proteins etc can come from eg SPTR etc
                //So for interactions we need to set the Xref info differently....
              //  psiPrimaryRef.setAttribute("db", "intact");
              //  psiPrimaryRef.setAttribute("id", xref.getParentAc()); //the interaction AC
              //  psiPrimaryRef.setAttribute("secondary", label);
           // }
           // else {
                psiPrimaryRef.setAttribute("db", xref.getCvDatabase().getShortLabel());
                psiPrimaryRef.setAttribute("id", xref.getPrimaryId());
            //}
        } catch (NullPointerException e) {
            logger.warn("xref without db or id - failed (required):");
            throw new ElementNotParseableException("PrimaryRef without id or db");
        }
        try {
            //if(!isInteraction) psiPrimaryRef.setAttribute("secondary", xref.getSecondaryId());
            psiPrimaryRef.setAttribute("secondary", xref.getSecondaryId());
        } catch (NullPointerException e) {
            logger.info("no secondaryRef");
        } // not required here - so dont worry
        try {
            psiPrimaryRef.setAttribute("version", xref.getDbRelease());
        } catch (NullPointerException e) {
            logger.info("no dbRelease");
        } // not required here - so dont worry

        return psiPrimaryRef;
    }

    /**
     * This method gets a Xref and return a PSI SecondaryRef.
     * @param xref - Object  to convert to PSI-Format
     * @return DOM-Object, representing a <secondaryRef>
     * @see uk.ac.ebi.intact.model.Xref
     */
    private Element procSecondaryRef(Xref xref) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiSecondaryRef = doc.createElement("secondaryRef");
        //local elements processing...
        try {
            psiSecondaryRef.setAttribute("db", xref.getCvDatabase().getShortLabel());
            psiSecondaryRef.setAttribute("id", xref.getPrimaryId());
        } catch (NullPointerException e) {
            logger.warn("xref without db or id - failed (required):");
            throw new ElementNotParseableException("SecondaryRef without id or db");
        }
        try {
            psiSecondaryRef.setAttribute("secondary", xref.getSecondaryId());
        } catch (NullPointerException e) {
            logger.info("no secondaryId");
        } // not required here - so dont worry
        try {
            psiSecondaryRef.setAttribute("version", xref.getDbRelease());
        } catch (NullPointerException e) {
            logger.info("no dbRelease");
        } // not required here - so dont worry

        return psiSecondaryRef;
    }


////////////////THIS method th mainly the same like  procBioSource down ...psiElement diffs !
    /**
     * process bioSource
     * @param bioSource to convert to PSI-Format
     * @return DOM-Object, representing a <hostOrganism>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.BioSource
     */
    private Element doBioSourceAsHost(BioSource bioSource) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiOrganism = doc.createElement("hostOrganism");
        //local elements processing...
        //getShortLabel & getFullName are oragnism/names
        try {
            Element psiNames = getPsiNamesOfAnnotatedObject(bioSource);
            psiOrganism.appendChild(psiNames);
        } catch (ElementNotParseableException e) {
            logger.info("names failed (not required):" + e.getMessage());
        } //not required here - so dont worry
        //       getCvCellCycle()   @todo
        //       getCvCellCycleAc() @todo
        //       getCvCellType()
        if (null != bioSource.getCvCellType()) {
            try {
                Element psiCellType = doCvCelltype(bioSource.getCvCellType());
                psiOrganism.appendChild(psiCellType);
            } catch (ElementNotParseableException e) {
                logger.info("cvCellType failed (not required):" + e.getMessage());
            } //not required here - so dont worry

            //       getCvCellTypeAc()  @todo
            //       getCvCompartment()
        }
        if (null != bioSource.getCvCompartment()) {
            try {
                Element psiCompartment = doCvCompartment(bioSource.getCvCompartment());
                psiOrganism.appendChild(psiCompartment);
            } catch (ElementNotParseableException e) {
                logger.info("cvCompartment failed (not required):" + e.getMessage());
            }  //not required here - so dont worry
        }
        //       getCvCompartmentAc() @todo
        //       getCvDevelopmentalStage() @todo
        //       getCvDevelopmentalStageAc()    @todo
        //       getCvTissue()
        if (null != bioSource.getCvTissue()) {
            try {
                Element psiTissue = doCvTissue(bioSource.getCvTissue());
                psiOrganism.appendChild(psiTissue);
            } catch (ElementNotParseableException e) {
                logger.info("cvTissue failed (not required):" + e.getMessage());
            } //not required here - so dont worry
        }
        //       getCvTissueAc() @todo
        //       getTaxId()
        String taxId = bioSource.getTaxId();
        if ((null == taxId) || ("" == taxId)){
            taxId = "-1";
        }
        psiOrganism.setAttribute("ncbiTaxId", taxId);
        //       getAnnotation @todo
        //       getCurator @todo
        //       getCuratorAc @todo
        //       getMenuList @todo
        //       getReference @todo
        //       getXref @todo
        //       getCreated @todo
        //       getEvidence @todo
        //       getOwner @todo
        //       getOwnerAc @todo
        //       getUpdated @todo
        //returning result DOMObject
        if (!psiOrganism.hasChildNodes()) {
            logger.warn("bioSource failed, no child elements.");
            throw new ElementNotParseableException("Organism has no Child Elements");
        }
        return psiOrganism;
    }


    ////////////////THIS method th mainly the same like  procBioSourceAsHost above ...psiElement diffs !
    /**
     * process bioSource
     * @param bioSource to convert to PSI-Format
     * @return DOM-Object, representing a <organism>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.Xref
     */
    private Element doBioSource(BioSource bioSource) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiOrganism = doc.createElement("organism");
        //getShortLabel & getFullName are organism/names
        Element psiNames = null;
        try {
            psiNames = getPsiNamesOfAnnotatedObject(bioSource);
            psiOrganism.appendChild(psiNames);
        } catch (ElementNotParseableException e) {
            logger.info("names failed (not required):" + e.getMessage());
        } //not required here - so dont worry
        //       getCvCellCycle()   @todo
        //       getCvCellCycleAc() @todo
        //       getCvCellType()
        if (null != bioSource.getCvCellType()) {
            try {
                Element psiCellType = doCvCelltype(bioSource.getCvCellType());
                psiOrganism.appendChild(psiCellType);
            } catch (ElementNotParseableException e) {
                logger.info("cvCelltype failed (not required):" + e.getMessage());
            } //not required here - so dont worry
            catch (NullPointerException e) {
                logger.info("cvCelltype failed (not required):" + e.getMessage());
            }//not required here - so dont worry
        }
        //       getCvCellTypeAc()  @todo
        //       getCvCompartment()
        if (null != bioSource.getCvCompartment()) {
            try {
                Element psiCompartment = doCvCompartment(bioSource.getCvCompartment());
                psiOrganism.appendChild(psiCompartment);
            } catch (ElementNotParseableException e) {
                logger.info("cvCompartment failed (not required):" + e.getMessage());
            }//not required here - so dont worry
        }
        //       getCvDevelopmentalStage() @todo
        //       getCvDevelopmentalStageAc()    @todo
        //       getCvTissue()
        if (null != bioSource.getCvTissue()) {

            try {
                Element psiTissue = doCvTissue(bioSource.getCvTissue());
                psiOrganism.appendChild(psiTissue);
            } catch (ElementNotParseableException e) {
                logger.info("cvTissue failed (not required):" + e.getMessage());
            } //not required here - so dont worry
            catch (NullPointerException e) {
                logger.info("cvTissue failed (not required):" + e.getMessage());
            }//not required here - so dont worry
        }
        //       getCvTissueAc() @todo
        //       getTaxId()
        String taxId = bioSource.getTaxId();
        if ((null == taxId) || ("" == taxId)){
            taxId = "-1";
        }
        psiOrganism.setAttribute("ncbiTaxId", taxId);
        //       getAnnotation @todo
        //       getCurator @todo
        //       getCuratorAc @todo
        //       getMenuList @todo
        //       getReference @todo
        //       getXref @todo
        //       getCreated @todo
        //       getEvidence @todo
        //       getOwner @todo
        //       getOwnerAc @todo
        //       getUpdated @todo
        //returning result DOMObject
        if (!psiOrganism.hasChildNodes()) {
            logger.warn("organism failed, no child elements.");
            throw new ElementNotParseableException("Organism has no Child Elements");
        }
        return psiOrganism;
    }


    /**
     * process cvCellType
     * @param cvCellType to convert to PSI-Format
     * @return DOM-Object, representing a <cellType>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.CvCellType
     */
    private Element doCvCelltype(CvCellType cvCellType) throws ElementNotParseableException {
        //param may be undefined...
        if (cvCellType != null) {
            //generate DOM-Element
            Element psiCellType = doc.createElement("cellType");

            //PSI schema says this could have names, xref, attributeList
            //sub-elements. Presumably at least one of these exists if the
            //cvCellType object is present. Also attributeList does not exist in
            //the intact model so it is not handled here.
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(cvCellType);
                psiCellType.appendChild(psiNames);
            }
            catch (ElementNotParseableException e) {
                logger.info("names failed (not required):" + e.getMessage());
            } //not required here - so dont worry
            catch (NullPointerException e) {
                logger.warn("cvCelltype failed (required):" + e.getMessage());
            }

            //xrefs
            try {
                Element psiXref = doXrefCollection(cvCellType.getXrefs(), "psi-mi");
                psiCellType.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.warn("xref failed (required):" + e.getMessage());
            }
            //returning result DOMObject
            if (!psiCellType.hasChildNodes()) {
                logger.warn("cvCellType failed, no child elements.");
                throw new ElementNotParseableException("CellType has no Child Elements");
            }
            return psiCellType;
        } else {
            return null;
        }
    }

    /**
     * process cvCompartment
     * @param cvCompartment to convert to PSI-Format
     * @return DOM-Object, representing a <compartment>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.CvCompartment
     */
    private Element doCvCompartment(CvCompartment cvCompartment) throws ElementNotParseableException {
        //local elements processing...
        if (cvCompartment != null) {
            //generate DOM-Element
            Element psiCompartment = doc.createElement("compartment");
            try {

                psiCompartment.appendChild(doc.createTextNode(cvCompartment.getShortLabel()));
            } catch (NullPointerException e) {
                logger.warn("cvCompartment failed (required):" + e.getMessage());
            }
            //returning result DOMObject
            if (!psiCompartment.hasChildNodes()) {
                logger.warn("cvCompartment failed, no child elements.");
                throw new ElementNotParseableException("Compartment has no Child Elements");
            }
            return psiCompartment;
        } else {
            return null;
        }
    }

    /**
     * process cvTissue
     * @param cvTissue to convert to PSI-Format
     * @return DOM-Object, representing a <tissue>
     * @exception  uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException if it is not defined
     * @see uk.ac.ebi.intact.model.CvTissue
     */
    private Element doCvTissue(CvTissue cvTissue) throws ElementNotParseableException {
        //local elements processing...
        if (cvTissue != null) {
            //generate DOM-Element
            Element psiTissue = doc.createElement("tissue");

            //PSI schema says this could have names, xref, attributeList
            //sub-elements. Presumably at least one of these exists if the
            //cvTissue object is present. Also attributeList does not exist in
            //the intact model so it is not handled here.
            try {
                Element psiNames = getPsiNamesOfAnnotatedObject(cvTissue);
                psiTissue.appendChild(psiNames);
            }
            catch (ElementNotParseableException e) {
                logger.info("names failed (not required):" + e.getMessage());
            } //not required here - so dont worry
            catch (NullPointerException e) {
                logger.warn("cvTissue failed (required):" + e.getMessage());
            }

            //xrefs
            try {
                Element psiXref = doXrefCollection(cvTissue.getXrefs(), "psi-mi");
                psiTissue.appendChild(psiXref);
            } catch (ElementNotParseableException e) {
                logger.warn("xref failed (required):" + e.getMessage());
            }
            //returning result DOMObject
            if (!psiTissue.hasChildNodes()) {
                logger.warn("cvTissue failed, no child elements.");
                throw new ElementNotParseableException("Tissue has no Child Elements");
            }
            return psiTissue;
        } else {
            return null;
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // helper methods
    // They differs from proc*-mothods in that way, that they get a basic object and processing only a
    // part of this object - they are only for reduce redundancy !


    /**
     * Create a names element from an IntAct AnnotatedObject
     * @param anAnnotatedObject
     * @return DOM-Object, representing a names element
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException
     */
    private Element getPsiNamesOfAnnotatedObject(AnnotatedObject anAnnotatedObject)
            throws ElementNotParseableException {

        if(anAnnotatedObject != null)
            return getNames(anAnnotatedObject.getShortLabel(), anAnnotatedObject.getFullName());

        return null;
    }

    /**
     * Create a names element from shortLabel and fullName strings.
     * @param shortLabel
     * @param fullName
     * @return DOM-Object, representing a <names>
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException
     */
    private Element getNames(String shortLabel, String fullName) throws ElementNotParseableException {
        //generate DOM-Element
        Element psiNames = doc.createElement("names");
        //local elements processing...
        try {
            //getshortLabel
            if (shortLabel != null) {
                Element psiShortLabel = doc.createElement("shortLabel");
                psiShortLabel.appendChild(doc.createTextNode(shortLabel));
                psiNames.appendChild(psiShortLabel);
                //getFullName is names/fullName
            } else {
                logger.warn("names failed (required): no shortLabel");

            }
        } catch (NullPointerException e) {
            logger.warn("names failed (required): no shortLabel");

        }
        try {
            if (fullName != null) {
                Element psiFullName = doc.createElement("fullName");
                psiFullName.appendChild(doc.createTextNode(fullName));
                psiNames.appendChild(psiFullName);
            }   //else: dont need a FullName
        } catch (NullPointerException e) {
        } // donty worry - dont need a FullName

        //returning result DOMObject
        if (!psiNames.hasChildNodes()) {
            logger.warn("names failed, no child elements.");
            throw new ElementNotParseableException("Names has no Child Elements");
        }

        return psiNames;
    }


    /**
     * WorkAround Method getRelInteractionTypes retrieves a Collection of all related InteractionTypes
     * of an Collection of Experiments
     * @param relExperiments where related InteractionTypes are returned for
     * @return Collection of Features
     * @see uk.ac.ebi.intact.model.Feature
     */

    private Collection getRelInteractionTypes(Collection relExperiments) {
        // TODO: an HashSet would me more appropriate ? this is a distinct set of CvInteraction !
        Collection relInteractionTypes = new ArrayList(relExperiments.size());
        Iterator relExperimentsIterator = relExperiments.iterator();
        while (relExperimentsIterator.hasNext()) {
            Experiment experiment = (Experiment) relExperimentsIterator.next();
            relInteractionTypes.add(experiment.getCvInteraction());
        }
        return relInteractionTypes;
    }

    /**
     * Builds a release date String. Makes the code a little tidier like this.
     * @return String the release date (based on current date) as a String
     */
    private String getReleaseDate() {

        //releaseDate will be current date...
        String monthString = null;
        String dayString = null;
        Calendar cal = null;
        int month = 0;

        cal = Calendar.getInstance();
        //appending "0" for 1-9th day of a month
        if (cal.get(Calendar.DAY_OF_MONTH) <= 9) {
            dayString = "0" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        } else {
            dayString = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        }
        //appending "0" for 1-9th month of a year
        month = cal.get(Calendar.MONTH) + 1;
        if (month <= 9) {
            monthString = "0" + month;
        } else {
            monthString = Integer.toString(month);
        }
        return (cal.get(Calendar.YEAR) + "-" + monthString + "-" + dayString);

    }

}
