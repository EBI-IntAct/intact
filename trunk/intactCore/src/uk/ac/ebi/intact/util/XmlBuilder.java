/*
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 03-Feb-2003
 * Time: 10:36:17
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.util;

import java.util.*;
import java.lang.reflect.*;
import java.security.*;

//XML classes
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import javax.xml.parsers.*;

import java.io.*;

//DB accessor
import uk.ac.ebi.intact.business.*;

import uk.ac.ebi.intact.model.*;


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

    private IntactHelper helper;

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

    /**
     * Sets up an XML builder with suitable persistent store access. Currently this requires
     * a suitable IntactHelper object to ensure the caller's connection details can be reused (persistent
     * storage access is required here since sometimes details of objects referenced from elsewhere by
     * eg AC only may not yet be materialised). Note that if the builder is to be used in a
     * web application, this is the correct constructor to be used.
     *
     * @param helper The intact helper instance used by the caller - this allows the builder to
     * use the same data store access configurations.
     *
     * @exception IntactException thrown if no helper object was supplied
     */
    public XmlBuilder(IntactHelper helper) throws IntactException {

        if(helper == null) {
            throw new IntactException("Error - cannot use this XML Builder constuctor with a null helper parameter! Either pass a helper (preferred) or use the default constructor");
        }
        else this.helper = helper;
    }

    /**
     * Note that this default constructor will build a default IntactHelper object with
     * default configuration settings. This is fine for 'standalone' usage but can cause
     * problems in a web environment where connection profiles are more important. For use
     * by web applications it is recommended that the constructor taking a <code>IntactHlper</code>
     * instance is used.
     *
     * @exception IntactException thrown if even a default helper object could not be created.
     */
    public XmlBuilder() throws IntactException {

        helper = new IntactHelper();
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
     * Collections) can be obtained from the <code>expandDoc</code>
     * method.
     *
     * @param items The group of objects for which an XML format is required
     *
     * @return Document The generated XML in Document format (empty if nothing generated)
     * @exception ParserConfigurationException thrown if it was not possible to create a document builder
     */
    public  Document buildXml(Collection items) throws ParserConfigurationException {

        Element elem = null;
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

            //try the cache first..
            elem = (Element)compactCache.get(item);

            if(elem == null) {
                //get the compact Element then cache it
                elem = this.buildCompactElem(item);
                if(ac != null) compactCache.put(item, elem);
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
     * of ACs provided as a parameter and also the operation mode.
     *
     * @param dc The Document to be expanded
     * @param ids A list of the items (identified by AC) to be expanded
     * @param mode The mode of operation to be carried out (eg expand, contract etc)
     *
     * @return Document the expanded XML Document or the original Document if no changes
     * were performed. If no Document object was supplied as an argument, null is returned
     *
     * @exception ParserConfigurationException thrown if a document builder could not be created
     *
     */
    public Document modifyDoc(Document dc, List ids, int mode) throws ParserConfigurationException {

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
                }
                else {
                    //default to contracted
                    result = (Element)compactCache.get(key);
                }
                if(result == null) {

                    //get from DB/OJB cache, then save it locally
                    //NB *** this bit is intact specific *** -
                    //need to know what type to search on..
                    //Currently this is BasicObject or Institution...

                    //use a user object if possible as this means we can reuse
                    //the caller's DB connection...
                    Collection searchResults = new ArrayList();
                    searchResults = helper.search("uk.ac.ebi.intact.model.BasicObject", "ac", (String)key);
                    if(searchResults.isEmpty()) {

                        //try Institution instead
                        searchResults = helper.search("uk.ac.ebi.intact.model.Institution", "ac", (String)key);
                    }
                    if(searchResults.size() > 1) {

                        //something odd - AC is supposed to be unique! Report an error..
                    }
                    Object obj = searchResults.iterator().next();

                    if(mode == XmlBuilder.EXPAND_NODES) {
                        result = this.buildFullElem(obj);
                        expandedCache.put(key, result);
                    }
                    else {

                        //default to compact
                        result = this.buildCompactElem(obj);
                        compactCache.put(key, result);

                    }

                }

                //import the new Element into the current document so we can use it
                Node newNode = dc.importNode(result, true);

                //now find the item for modification in the Document then replace
                //its element in the main Document with the new one
                String ac = (String)key;
                TreeWalker walker = ((DocumentTraversal)dc).createTreeWalker(dc, NodeFilter.SHOW_ALL,null,true);
                Node n = null;
                Node oldNode = null;

                while((n = walker.nextNode()) != null) {

                    Element e = (Element)n;
                    if(ac.equals(e.getAttribute("ac"))) {

                        //found the node we want - replace it
                        oldNode = walker.getCurrentNode();
                        Node parent = oldNode.getParentNode();
                        parent.replaceChild(newNode, oldNode);
                    }
                    //carry on - may be more than one element with this AC...
                }
            }
        }
        catch(IntactException ie) {

            //something failed during search - deal with it..
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
     */
     public Element buildCompactElem(Object item) throws ParserConfigurationException {

        Element elem = null;
        //sanity check...
        if(item == null) return null;

        //create a dummy document to build the Element
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();

        //get the fields defined in this class, and put them
        //in a list so we can add to them easily
        List allFields = Arrays.asList(this.getAllFields(item));

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
                    //shouldn't happen - fields obtained form item in the first place!
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
     * @return Element The DOM Element representing the expanded object (empty if nothing is done)
     *
     * @exception ParserConfigurationException thron if a document builder could not be created
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
        if(obj == null) return null;

        //do the attributes
        elem = this.buildCompactElem(obj);

        //need to get the owner Document of this Element as we need
        //to append things to it...
        Document doc = elem.getOwnerDocument();

        //now do the refs and Collections...
        //ignore Strings, Collections and Timestamps
        Set ignored = new HashSet();
        ignored.add(String.class);
        ignored.add(Collection.class);
        ignored.add(java.sql.Timestamp.class);

        List objRefs = this.getReferences(obj, ignored);
        List collectionRefs = this.getCollections(obj);

        Element refElement = null;
        Iterator it = objRefs.iterator();

        //references...
        while(it.hasNext()) {

            try {
                Field refField = (Field)it.next();
                Object value = refField.get(obj);

                if(value != null) {
                    refElement = this.buildCompactElem(refField.get(obj));

                    //import the new node and then we can append it
                    Node newNode = doc.importNode(refElement, true);
                    elem.appendChild(newNode);
                }
            }
            catch(IllegalArgumentException ie) {
                //shouldn't happen - fields obtained from opbj in the first place!
            }
            catch(IllegalAccessException ia) {
                //shouldn't happen - security overriden already
            }
        }

        //Collections (similar code, but not worth making a seperate method).....
        it = collectionRefs.iterator();
        while(it.hasNext()) {

            try {
            Field collectionField = (Field)it.next();
            Collection items = (Collection)collectionField.get(obj);
            Iterator iter = items.iterator();
            while(iter.hasNext()) {

                //reuse the refElement to access each item of the Collection
                refElement = this.buildCompactElem(iter.next());

                //import the new Node and append it
                Node newItemNode = doc.importNode(refElement, true);
                elem.appendChild(newItemNode);
            }
            }
            catch(IllegalAccessException ia) {
                //shouldn't happen - security overriden already
            }
            catch(IllegalArgumentException ie) {
                //shouldn't happen - fields obtained from obj in the first place!
            }
        }

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
            //in a list so we can add to them easily
            Field[] fields = clazz.getDeclaredFields();

            //the Arrays.asList op gives you an AbstractList backed by the array, which means that
            //the add operation is not supported!! Hence the use of ArrayList...
            List allFields = new ArrayList(Arrays.asList(fields));

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
     * to be ingnored in the parameter set.
     *
     * @param obj The object to reflect upon
     * @param onesToIgnore The set of Classes of fields to be left out
     * @return List a list of object references, or an empty list if
     * the object contains only primitives, Collections or objects of type in the set to ignore.
     */
    private List getReferences(Object obj, Set onesToIgnore) {

        List result = new ArrayList();
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
     * @return List a list of Collection fields, or an empty list if
     * the object contains no Collections
     */
    private List getCollections(Object obj) {

        List result = new ArrayList();

        Field[] fields = this.getAllFields(obj);

        for (int i=0; i < fields.length; i++) {

            Class type = fields[i].getType();
            if(Collection.class.isAssignableFrom(type)) {

                result.add(fields[i]);
            }
        }
        return result;
    }

}
