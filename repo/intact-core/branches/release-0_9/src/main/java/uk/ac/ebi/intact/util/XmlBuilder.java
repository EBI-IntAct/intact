/*
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 03-Feb-2003
 * Time: 10:36:17
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * This utility class provides operations for generating XML, typically
 * in the form of Document or Element objects. The main purpose of having
 * XML genration as a seperate facility is to allow generation of "partial"
 * XML documents which are only completed when necessary. Tools such as
 * Castor only provide the means to generate a complete XML document, which can
 * become very memory-intensive for large data sets.
 *
 * @author Chris Lewington
 */
public class XmlBuilder implements Serializable {

    //used for caching XML details, keyed on AC
    private HashMap expandedCache = new HashMap();

    //cache of compact elements, keyed on object
    private HashMap compactCache = new HashMap();

    /**
     * Identifies an expand request
     */
    public static final int EXPAND_NODES = 1;

    /**
     * identifies a contract request
     */
    public static final int CONTRACT_NODES = 2;


    public XmlBuilder()  {

    }

    /**
     * Convenience method to build a Document for one object only.
     * @param obj The object for which we want a Document built
     * @return Document The object as an Element within a Document
     * @exception ParserConfigurationException thrown if t6here was a problem parsing
     */
    public Document buildXml(Object obj) throws ParserConfigurationException {

        Collection params = new ArrayList();
        params.add(obj);
        return this.buildXml(params);
    }

    /**
     * This method builds an XML Document for a Collection of
     * objects. The Document produced is at the "first level" of the
     * object tree, ie only the String and primitive attributes are generated. A deeper
     * level of document generation (expanding object references and
     * Collections) can be obtained from the <code>modifyDoc</code>
     * method. It is assumed that all objects passed as parameters will have a non-null
     * AC attribute set to allow for cacheing.
     *
     * @param items The group of objects for which an XML format is required
     *
     * @return Document The generated XML in Document format (empty if nothing generated)
     * @exception ParserConfigurationException thrown if it was not possible to create a document builder
     */
    public  Document buildXml(Collection items) throws ParserConfigurationException {

        Element elem = null;

        //NB Need to get the item's ACs and cache via them - makes it specific...
        String ac = null;



        //create a document to hold the elements as they get built
        /*
        * IMPORTANT NOTE ON THE DOM:
        * the Document object is a type of factory for creating eg elements etc.
        * Consequently it is NOT possible to simply append Elements that have been
        * built by other Document objects (they exist in a "different space". If you
        * try to do this you get a "DOM005 Wrong document" error. To overcome the problem,
        * you must first *import* the new Node into the Document you wish to add it into.
        * This fact is very badly discussed (if at all!) anywhere.
        */
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("intact-root");

        //sanity check...
        if((items == null) || (items.isEmpty())) return doc;

        Iterator it = items.iterator();
        while(it.hasNext()) {

            Object item = it.next();
            //Need the try/catch because the Field.get throws the exceptions but
            //the getReferences method changes the security anyway so the expcetions will
            //never be thrown
            try {

                //to avoid using intact classes (keeps it a little more flexible)
                //get the references and find the AC from that...
                Collection referenceList = this.getReferences(item, new HashSet());
                Iterator iter = referenceList.iterator();
                while(iter.hasNext()) {
                    Field field = (Field)iter.next();
                    if(field.getName().equals("ac")) {
                        ac = (String)field.get(item);
                        break;
                    }
                }
            }
            catch(IllegalArgumentException iae) {
            }
            catch(IllegalAccessException ie) {
            }

            //try the cache first..
            elem = (Element)compactCache.get(ac);

            if(elem == null) {
                //get the compact Element then cache it
                elem = this.buildCompactElem(item);
                System.out.println("cacheing compact Element for item AC= " + ac);
                if(ac != null) compactCache.put(ac, elem);
            }

            //import the new Element first (it was created by a different Document)
            Node newChild = doc.importNode(elem, true);
            root.appendChild(newChild);
        }

        //finally put the root into the Document
        doc.appendChild(root);

        return doc;
    }

    /**
     * This method allows for modification of a given Document. Specifically, the
     * Document tree has specific nodes changed as determined by the set
     * of ACs provided as a parameter and also the operation mode. Note that if a particular
     * AC occurs more than once in the DOM tree then *all* DOM Elements found with that
     * AC will be amodified with the same specified mode.
     *
     * @param dc The Document to be expanded
     * @param ids A Collection of the items (identified by AC) to be expanded
     * @param mode The mode of operation to be carried out (eg expand, contract etc)
     *
     * @return Document the expanded XML Document or the original Document if no changes
     * were performed. If no Document object was supplied as an argument, null is returned
     *
     * @exception ParserConfigurationException thrown if a document builder could not be created
     *
     */
    public Document modifyDoc(Document dc, Collection ids, int mode) throws ParserConfigurationException {

        //basic idea:
        //1. Get all the objects from the DB using their AC (probably in the OJB
        //cache in any case, so this won't always result in a DB query)
        //2. Modify each in turn, and cache the changes in a map (keyed on AC);
        //3. go across the document and search for each AC in turn;
        //if found, get that object's modification from the appropriate map and replace it's old
        //version in the document with the new one.
        if(dc == null) return null;
        if((ids == null) || (ids.isEmpty())) return dc;

        Iterator it = ids.iterator();

        //This is actually inefficient - the tree is traversed for each AC in the List.
        //A better solution (maybe later) would be to traverse the tree only once and check
        //for any AC match at each node. Just needs a little reorganising of the code...
        try {
            while(it.hasNext()) {

                Object key = it.next();
                if (key.getClass() != String.class) {

                    //expecting ACs only as Strings - finish
                    break;
                }

                //check cache first...
                Element result = null;
                if(mode == XmlBuilder.EXPAND_NODES) {
                    result = (Element)expandedCache.get(key);
                    if(result != null) System.out.println("expand requested -found " + key + "in cache");
                }
                else {
                    //default to contracted
                    result = (Element)compactCache.get(key);
                    if(result != null) System.out.println("default to contract -found " + key + "in cache");
                }
                if(result == null) {

                    System.out.println("XML not in cache - building it...");
                    //get from DB/OJB cache, then save it locally
                    //NB *** this bit is intact specific *** -
                    //need to know what type to search on..
                    //Currently this is BasicObject or Institution...

                    Collection searchResults = new ArrayList();
                    System.out.println("searching by Standard Collection...");
                    searchResults = DaoFactory.getIntactObjectDao().getByAcLike((String)key);
//                    System.out.println("searching by Iterator....");
//                    Iterator iter = null;
//                    iter = helper.iterSearch("uk.ac.ebi.intact.model.BasicObject", "ac", (String)key);
//                    if((iter == null) || (!iter.hasNext())) {
//                         iter = helper.iterSearch("uk.ac.ebi.intact.model.Institution", "ac", (String)key);
//                    }
                    if(searchResults.isEmpty()) {

                        //try Institution instead
                        searchResults = DaoFactory.getInstitutionDao().getByAcLike((String)key);
                    }
                    if(searchResults.size() > 1) {

                        //something odd - AC is supposed to be unique! Do something...
                        return dc;
                    }
                    Object obj = searchResults.iterator().next();
//                    if((iter == null) || (!iter.hasNext())) {
//                        System.out.println("can't build any XML - no data found!!");
//                        return dc;
//                    }

                    //should only be one....
                    //Object obj = iter.next();

                    //debug...
                    System.out.println("first item found: " + ((uk.ac.ebi.intact.model.BasicObject)obj).getAc());
                    System.out.println("type: " + obj.getClass().getName());

                    if(mode == XmlBuilder.EXPAND_NODES) {
                        result = this.buildFullElem(obj);
                        expandedCache.put(key, result);
                    }
                    else {
                        //default to compact
                        result = this.buildCompactElem(obj);
                        compactCache.put(key, result);
                    }



                    //need to close the data if for an odd reason more
                    //than one item returned from search...
//                    if(iter.hasNext()) {
//                        System.out.println("something odd - search found more than one match for an ac - closing data..");
//                         //debug...
//                        System.out.println("next item found: " + ((uk.ac.ebi.intact.model.BasicObject)iter.next()).getAc());
//                        System.out.println("type: " + obj.getClass().getName());
//
//                        System.out.println("XmlBuilder: closing Result set to release resources...");
//                        helper.closeData(iter);
//
//                    }
                }

                //import the new Element into the current document so we can use it
                Node newNode = dc.importNode(result, true);

                System.out.println("about to walk the tree...");
                //now find the item for modification in the Document then replace
                //its element in the main Document with the new one
                String ac = (String)key;
                TreeWalker walker = ((DocumentTraversal)dc).createTreeWalker(dc, NodeFilter.SHOW_ALL,null,true);
                Node n = null;
                Node oldNode = null;
                boolean hasMatchedBefore = false;

                while((n = walker.nextNode()) != null) {

                    Element e = (Element)n;
                    if(ac.equals(e.getAttribute("ac"))) {

                        //found the node we want - replace it
                        oldNode = walker.getCurrentNode();
//                        System.out.println("Node to replace: AC =: " + ((Element)oldNode).getAttribute("ac"));
//                        System.out.println("Node to replace: Type =: " + ((Element)oldNode).getTagName());

                        Node parent = oldNode.getParentNode();
//                        System.out.println("Parent: AC =: " + ((Element)parent).getAttribute("ac"));
//                        System.out.println("Parent: Type =: " + ((Element)parent).getTagName());
//                        System.out.println("New child: AC =: " + ((Element)newNode).getAttribute("ac"));
//                        System.out.println("New child: Type =: " + ((Element)newNode).getTagName());

                        //have to use a copy to avoid the walker building references to only
                        //one
                        if(hasMatchedBefore) {
                            Node copy = newNode.cloneNode(true);
                            parent.replaceChild(copy, oldNode);
                           // System.out.println("setting walker back to replaced node.....");
                            walker.setCurrentNode(copy);
                        }
                        else {
                            parent.replaceChild(newNode, oldNode);
                           // System.out.println("setting walker back to replaced node.....");
                            walker.setCurrentNode(newNode);
                        }
//                        System.out.println("Where I am in the tree - Tag:" + ((Element)walker.getCurrentNode()).getTagName());
//                        System.out.println("AC: " + ((Element)walker.getCurrentNode()).getAttribute("ac"));

                        //flag that we already have a match
                        hasMatchedBefore = true;
                    }
                    //carry on - may be more than one element with this AC...
                }
            }
        }
        catch(IntactException ie) {

            //something failed during search - deal with it..
            System.out.println("got a searching exception");
            System.out.println(ie.getMessage() + ie.getNestedMessage());
            ie.printStackTrace();
        }

        return dc;

    }

    /**
     * Provides a 'compact' expansion of an object. That is, only the
     * String, primitive and any Timestamp attributes are generated. Although Timestamps
     * are strictly speaking reference types they are usually (like Strings) included
     * as attributes rather than Elements in their own right.
     * <p>
     * NB the 'Timestamp' considered here is the java.sql.Timestamp class - this may,
     * therefore, be intact-specific.
     * </p>
     *
     * @param item The object to expand
     * @return Element The DOM Element representing the expanded object (empty if nothing is done)
     *
     * @exception ParserConfigurationException thrown if a document builder could not be created
     * @exception NullPointerException if a null object parameter is supplied
     */
     public Element buildCompactElem(Object item) throws ParserConfigurationException {

        Element elem = null;
        //sanity check...
        if(item == null) throw new NullPointerException("cannot create XML for a null object!");

        //create a dummy document to build the Element
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();

        //get the fields defined in this class, and put them
        //in a Collection so we can add to them easily
        Collection allFields = Arrays.asList(this.getAllFields(item));

        //get the appropriate intact form of the class name
        //(needed to match the XSL templates correctly..)
        //NB tried String.split but it didn't work!!
        StringTokenizer tokenizer = new StringTokenizer(item.getClass().getName(), ".");
        String elemName = null;
        //we know the element name to use is the last token..
        while(tokenizer.hasMoreElements()) {elemName = tokenizer.nextToken();}

        //now build the element and populate as necessary....
        elem = doc.createElement(elemName);
        Iterator iter = allFields.iterator();
        while(iter.hasNext()) {

            Field currentField = (Field)iter.next();
            String name = currentField.getName();
            Class type = currentField.getType();

            //filter out the 'Ac' fields used by synchron, the ojb field and also
            //the non-String/primitive fields (except for Timestamps)..
            if((!(name.endsWith("Ac")) & (type.equals(String.class)) &
                    !(name.equals("ojbConcreteClass"))) |
                    (type.isPrimitive()) | (type.equals(java.sql.Timestamp.class))) {

                try {
                    Object obj = currentField.get(item);
                    if(obj != null) {
                        //populate the attributes of the new element
                        elem.setAttribute(name, obj.toString());
                    }
                }
                catch(IllegalArgumentException ie) {
                    //shouldn't happen - fields obtained from item in the first place!
                }
                catch(IllegalAccessException ia) {
                    //shouldn't happen - security overriden in getAllFields already
                }
            }
        }
        return elem;
    }


    /**
     * Provides a 'complete' expansion of an object. That is, all references (other than
     * Strings and java.sql.Timestamps) and Collections are expanded (but each only to a 'compact' level).
     *
     * @param obj The object to expand
     * @return Element The DOM Element representing the expanded object (at least compact)
     *
     * @exception ParserConfigurationException thrown if a document builder could not be created
     * @exception NullPointerException If the argument object is null
     */
    public Element buildFullElem(Object obj) throws ParserConfigurationException {

        Element elem = null;
        /*
        * Basic idea:
        * 1. do a compact expansion to fill an Element with the basic attributes;
        * 2. for each reference, expand it as compact and add as a sub-element;
        * 3. for each Collection, get the items and expand each in turn as compact, and
        *    add each as a sub-element.
        */

        //sanity check...
        if(obj == null) throw new NullPointerException("cannot build XML for a null object!");

        //do the attributes
        elem = this.buildCompactElem(obj);

        //need to get the owner Document of this Element as we need
        //to append things to it...
        Document doc = elem.getOwnerDocument();

        //now do the refs and Collections...
        expandReferences(doc, elem, obj, null);
        //ignore Strings, Collections and Timestamps
//        Set ignored = new HashSet();
//        ignored.add(String.class);
//        ignored.add(Collection.class);
//        ignored.add(java.sql.Timestamp.class);
//
//        Collection objRefs = this.getReferences(obj, ignored);
//        Collection collectionRefs = this.getCollections(obj);
//
//        Element refElement = null;
//        Iterator it = objRefs.iterator();
//
//        //references... (NB should they be cached, as they are displayed but not explicitly referenced?)
//        while(it.hasNext()) {
//
//            try {
//                Field refField = (Field)it.next();
//                Object value = refField.get(obj);
//
//                if(value != null) {
//                    refElement = this.buildCompactElem(refField.get(obj));
//
//                    //import the new node and then we can append it
//                    Node newNode = doc.importNode(refElement, true);
//                    elem.appendChild(newNode);
//                }
//            }
//            catch(IllegalArgumentException ie) {
//                //shouldn't happen - fields obtained from opbj in the first place!
//            }
//            catch(IllegalAccessException ia) {
//                //shouldn't happen - security overriden already
//            }
//        }

        //Collections (similar code, but not worth making a seperate method).....
        expandCollections(doc, elem, obj, null);
//        it = collectionRefs.iterator();
//        while(it.hasNext()) {
//
//            try {
//                Field collectionField = (Field)it.next();
//                String fieldName = collectionField.getName();
//                if(!fieldName.endsWith("s")) {
//                    //put an 's' on the end - helps readability in most cases
//                    //as Collections are plural but sometimes the reference names
//                    //are not! This may sometimes give some odd results, but will mostly be OK
//                    fieldName = fieldName + "s";
//                }
//
//                //create a collector Node in the same Document as elem
//                Element collectionElem = doc.createElement(fieldName);
//                Collection items = (Collection)collectionField.get(obj);
//                if(items != null) {
//                    Iterator iter = items.iterator();
//                    while(iter.hasNext()) {
//                        Object item = iter.next();
//
//                        //reuse the refElement to access each item of the Collection,
//                        //NB cache each one? it exists as compact but no AC references it...
//                        refElement = this.buildCompactElem(item);
//                        Node newItemNode = doc.importNode(refElement, true); //build returns Element from a different doc
//                        collectionElem.appendChild(newItemNode);
//                    }
//                }
//
//                elem.appendChild(collectionElem);
//            }
//            catch(IllegalAccessException ia) {
//                //shouldn't happen - security overriden already
//            }
//            catch(IllegalArgumentException ie) {
//                //shouldn't happen - fields obtained from obj in the first place!
//            }
//        }

        return elem;

    }

    /**
     * Provides a 'partial' expansion of an object. References and Collections whose
     * fields are not in the set of ones to ingore are expanded. Note that attributes of
     * the object (ie Strings, primitives etc) will always be defined - so a compact element
     * will be created.
     * @param obj The object to expand
     * @param fieldsToIgnore The object fields which should not be expanded. Expected to be Strings.
     * @return Element The DOM Element representing the expanded object (at least compact)
     *
     * @exception ParserConfigurationException thron if a document builder could not be created
     * @exception NullPointerException If the argument object is null
     */
    public Element buildPartialElem(Object obj, Collection fieldsToIgnore) throws ParserConfigurationException {

        Element elem = null;

        //sanity checks...
        if(obj == null) throw new NullPointerException("cannot build XML for a null object!");
        if(fieldsToIgnore != null) {
            Class type = fieldsToIgnore.iterator().next().getClass();
            if(!String.class.isAssignableFrom(type)) throw new IllegalArgumentException("fields to ignore must contain Strings!");
        }
        //do the attributes
        elem = this.buildCompactElem(obj);

        //need to get the owner Document of this Element as we need
        //to append things to it...
        Document doc = elem.getOwnerDocument();

        expandReferences(doc, elem, obj, fieldsToIgnore);
        expandCollections(doc, elem, obj, fieldsToIgnore);

        return elem;

    }

    //------------------------ private helper methods ------------------------------------

    /**
     * obtains all fields for a given object, including inherited ones, and makes them all accessible.
     *
     * @param obj The object to reflect upon
     *
     * @return Field[] An array of all fields (inherited, private, public, etc). The array is
     * of length 0 if no fields are found or a SecurityException is caught.
     *
     */
    private Field[] getAllFields(Object obj) {

        Class clazz = obj.getClass();
        Field[] result = new Field[0];

        try {

            //get the fields defined in this class, and put them
            //in a Collection so we can add to them easily
            Field[] fields = clazz.getDeclaredFields();

            //the Arrays.asList op gives you an AbstractList backed by the array, which means that
            //the add operation is not supported!! Hence the use of ArrayList...
            Collection allFields = new ArrayList(Arrays.asList(fields));

            //also need to get the declared fields of the superclasses...
            Class parent = clazz.getSuperclass();
            while(parent != java.lang.Object.class) {

                Field[] parentFields = parent.getDeclaredFields();
                for(int j=0; j < parentFields.length; j++) {
                    allFields.add(parentFields[j]);
                }
                parent = parent.getSuperclass();
            }

            //need to set the runtime type to be Field for this to work...
            result = (Field[])allFields.toArray(new Field[0]);

            //set all access permissions
            AccessibleObject.setAccessible(result, true);
        }
        catch(SecurityException se) {

            //this shouldn't happen for us as we don't have a SecurityManager installed
        }

        return result;
    }

    /**
     * obtain all the reference fields of an object apart from those specified
     * to be ignored in the parameter set.
     *
     * @param obj The object to reflect upon
     * @param onesToIgnore The set of Classes of fields to be left out (must be non-null, but may be empty)
     * @return Collection a Collection of object references, or an empty Collection if
     * the object contains only primitives, Collections or objects of type in the set to ignore.
     */
    private Collection getReferences(Object obj, Set onesToIgnore) {

        Collection result = new ArrayList();
        Field[] fields = this.getAllFields(obj);

        for (int i=0; i < fields.length; i++) {

            Class type = fields[i].getType();
            if((Collection.class.isAssignableFrom(type)) ||
                    (type.isPrimitive()) || (onesToIgnore.contains(type))) {

                //ignore - only want references
                continue;
            }
            //must be a reference type if we get to here
            result.add(fields[i]);

        }

        return result;
    }

    /**
     * obtain all the collection fields of an object.
     *
     * @param obj The object to reflect upon
     * @return Collection a Collection of Collection fields, or an empty Collection if
     * the object contains no Collections
     */
    private Collection getCollections(Object obj) {

        Collection result = new ArrayList();

        Field[] fields = this.getAllFields(obj);

        for (int i=0; i < fields.length; i++) {

            Class type = fields[i].getType();
            if(Collection.class.isAssignableFrom(type)) {

                result.add(fields[i]);
            }
        }
        return result;
    }

    private void expandReferences(Document doc, Element elem, Object obj, Collection noExpand)
                                                    throws ParserConfigurationException {

        Element refElement = null;

        //ignore Strings, Collections and Timestamps
        Set ignored = new HashSet();
        ignored.add(String.class);
        ignored.add(Collection.class);
        ignored.add(java.sql.Timestamp.class);
        Collection objRefs = this.getReferences(obj, ignored);

        //references... (NB should they be cached, as they are displayed but not explicitly referenced?)
        for (Iterator it = objRefs.iterator(); it.hasNext();) {
            try {
                Field refField = (Field) it.next();
                if(noExpand != null) {
                    //don't do this field if it is in the exclusion list
                    if(noExpand.contains(refField.getName())) continue;
                }
                    Object value = refField.get(obj);

                    if (value != null) {
                        refElement = this.buildCompactElem(refField.get(obj));

                        //import the new node and then we can append it
                        Node newNode = doc.importNode(refElement, true);
                        elem.appendChild(newNode);
                    }
            } catch (IllegalArgumentException ie) {
                //shouldn't happen - fields obtained from obj in the first place!
            } catch (IllegalAccessException ia) {
                //shouldn't happen - security overriden already
            }
        }

    }

    private void expandCollections(Document doc, Element elem, Object obj, Collection noExpand)
                                        throws ParserConfigurationException {

        Element refElement = null;

        Collection collectionRefs = getCollections(obj);
        for (Iterator it = collectionRefs.iterator(); it.hasNext();) {

            try {
                Field collectionField = (Field) it.next();
                String fieldName = collectionField.getName();
                if(noExpand != null) {
                    //don't do this field if it is in the exclusion list
                    if(noExpand.contains(fieldName)) continue;
                }
                if (!fieldName.endsWith("s")) {
                    //put an 's' on the end - helps readability in most cases
                    //as Collections are plural but sometimes the reference names
                    //are not! This may sometimes give some odd results, but will mostly be OK
                    fieldName = fieldName + "s";
                }

                //create a collector Node in the same Document as elem
                Element collectionElem = doc.createElement(fieldName);
                Collection items = (Collection) collectionField.get(obj);
                if (items != null) {
                    Iterator iter = items.iterator();
                    while (iter.hasNext()) {
                        Object item = iter.next();

                        //NB cache each one? it exists as compact but no AC references it...
                        refElement = this.buildCompactElem(item);
                        Node newItemNode = doc.importNode(refElement, true); //build returns Element from a different doc
                        collectionElem.appendChild(newItemNode);
                    }
                }

                elem.appendChild(collectionElem);
            } catch (IllegalAccessException ia) {
                //shouldn't happen - security overriden already
            } catch (IllegalArgumentException ie) {
                //shouldn't happen - fields obtained from obj in the first place!
            }
        }

    }

}
