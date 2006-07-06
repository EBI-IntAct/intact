/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.synchron;

// JDK
import java.util.*;
import java.io.*;
import java.sql.*;
import java.lang.reflect.Field;

// Castor
import org.xml.sax.ContentHandler;
import org.exolab.castor.util.Logger;
import org.exolab.castor.xml.*;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.jdo.PersistenceException;
import org.apache.xml.serialize.*;

//OJB
import org.apache.ojb.broker.*;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.util.logging.*;
import org.apache.ojb.broker.accesslayer.*;
import org.apache.ojb.broker.query.*;
import org.apache.ojb.broker.util.*;
import org.apache.ojb.*;
// Intact
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.ObjectSet;


/**
 * Purpose : extracts data objects from an XML file, inserts
 * new data entries in the local database and update already
 * existing data entries.
 * For all references to non availables data entries,
 * a zombie object is created
 *
 * @author Antje Mueller, Arnaud Ceol
 */
public class XmlLoader
 {

    // OJB PersistenceBroker, which defines the interface between
    // Java Objects and persistent objects
    private PersistenceBroker broker;

    // Castor unmarshaller, which unmarshalls XML documents to java Objects
    private static Unmarshaller unmarshaller;

    // loads the mapping file and provides them to the _unmarshaller
    private static Mapping  mapping;

    // provides all necessary informations for the mapping between XML input and java objects
    // is load by the _mapping
    public static final String MappingFile = "dbIoMapping.xml";

    // Writer for castor messages
    private static PrintWriter writer;


    public XmlLoader(PersistenceBroker broker) throws Exception
    {
        writer = new Logger(System.out).setPrefix( "intactCore" );

        // Load the mapping file
        mapping = new Mapping( getClass().getClassLoader() );

        try {
            mapping.loadMapping( getClass().getResource( MappingFile ) );
        } catch(Exception e) {
            System.out.println("[XmlLoader] [ERROR] Was not possible to load the mappingfile");
            e.printStackTrace(System.out);
            throw e;
        }

        // Initialise XML marshaller
        try {
            unmarshaller = new Unmarshaller(ObjectSet.class);
            unmarshaller.setMapping(mapping);
        } catch (Exception e){
            System.out.println("[XmlLoader] [ERROR] Was not possible to get the unmarshaller");
            throw e;
        }

        this.broker = broker;
    }


    /**
     *
     */
    public XmlLoader() {}



    /**
     * unmarshall the XML file xmlFile @xmlFile
     */
    public void loadFile( String xmlFileName ) throws Exception
    {
        FileReader reader;
        // Create a Reader to the file to unmarshal from
        try {
            reader = new FileReader(xmlFileName);
        } catch (FileNotFoundException e) {
            System.out.println("[XmlLoader] [ERROR] Was not possible to open the file: " + xmlFileName + " for loading");
            throw e;
        }

        // Marshall the objects from file

        // receive all objects contain in the XML file
        ObjectSet objectSet;
        try {
            objectSet = (ObjectSet) unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            System.out.println("[XmlLoader] [ERROR] Was not possible to unmarshal the file: " + xmlFileName);
            throw e;
        }

        Iterator iterator = objectSet.getObjects().iterator();

        broker.beginTransaction();
        try {
            while (iterator.hasNext())
            {
                Object o = iterator.next();
                // check if all BasicObject (or an Institution)
                // pointed by the attributes of the object refers to
                // objects that are persistent in the DB, or create zombies
                checkForeignKey(o);
                // store the object in the DB
                updateDb(o);
            }
            broker.commitTransaction();
        } catch (Exception e) {
            broker.abortTransaction();
            System.out.println("[XmlLoader] [ERROR] errors during loading the file: " + xmlFileName + ": no entry has been added");
            throw e;
        }
    }


    /**
     * Retrieves an object o and has to check if its persistent representation
     * is already in the database.
     * In this case, it has to update this database entry.
     * Otherwise it has to insert a persistent representation of this object.
     */
    private void updateDb(Object o)
    {
        if(IntactNode.class.isInstance(o)){
            Criteria criteria = new Criteria();
            criteria.addLike("ac",((IntactNode)o).getAc());
            Query query =	QueryFactory.newQuery(IntactNode.class,criteria);
            //select db entry about itselfs
            IntactNode intactNode = (IntactNode) broker.getObjectByQuery(query);
            if (intactNode != null) {
                ((IntactNode)o).setLastCheckId(intactNode.getLastCheckId());
                ((IntactNode)o).setLastProvidedDate(intactNode.getLastProvidedDate());
                ((IntactNode)o).setLastProvidedId(intactNode.getLastProvidedId());
                ((IntactNode)o).setRejected(intactNode.getRejected());
            }
        }
        if (BasicObject.class.isAssignableFrom(o.getClass())) {
            System.out.println("[XmlLoader] store object with ac= " + ((BasicObject)o).getAc());
        } else if  (o.getClass() == Institution.class) {
            System.out.println("[XmlLoader] store Institution with ac= " + ((Institution)o).getAc());
        }

        broker.store(o);
    }


    /**
     * retrieve an Object o and has to check, if this Object
     * contains references to other Objects, which persistent
     * representation is not in the db yet.
     * For each such object reference, it inserts a "zombie object"
     * into the database
     */
    private void checkForeignKey(Object o) throws Exception
    {
        // get all methods from the class and the classes it inherits
        java.lang.reflect.Method getters1[] = o.getClass().getDeclaredMethods();
        java.lang.reflect.Method getters2[] = o.getClass().getMethods();

        // put the getters in a List
        ArrayList getters = new ArrayList();
        for (int k = 0; k < java.lang.reflect.Array.getLength(getters1); k++)
        {
            if  (getters1[k].getName().startsWith("get"))
            {
                getters.add( getters1[k] );
            }
        }
        for (int k = 0; k < java.lang.reflect.Array.getLength(getters2); k++)
        {
            if  (getters2[k].getName().startsWith("get"))
            {
                getters.add( getters2[k] );
            }
        }

        for (int i=0; i < getters.size(); i++)
        {
            // if the field's value is a BasicObject
            // or an Institution
            // check if it is persistent in db, else
            // create a zombie of type of the field
            // if it does not already exist
            java.lang.reflect.Method m = (java.lang.reflect.Method)getters.get(i) ;

            // fields we are interested in are of type BasicObject or Institution
            // for a field of type BasicObject, we look at the associated field of type String
            // for an Institution we look at the "ownerAc" field
            if ((BasicObject.class).isAssignableFrom(m.getReturnType())  || (Institution.class).isAssignableFrom(m.getReturnType()) )
            {
                // Does the Ac field exist?
                try {
                    // is the Ac null?
                    if ( (  (o.getClass().getMethod(m.getName() + "Ac", null)).invoke(o, null) != null) )
                    {
                        Class parameters[] = {m.getReturnType()};
                        checkPersistence( o, m, o.getClass().getMethod( "set" + m.getName().substring(3) , parameters) );
                    }
                } catch ( NoSuchMethodException nsme) {
                    System.out.println("[XmlLoader][WARNING] setter or getter not found for field: " +  m.getName().substring(3) + "Ac in class " + o.getClass().getName());
                }
            } // end for basic objects

            // if the field contains a Vector,
            // check all the objects it contains
            else if ( Collection.class == m.getReturnType() && m.invoke(o, null) != null)
            {
                try {
                    // collection associated to the new object
                    Collection newCollection = new Vector();

                    Iterator it =  ((AbstractCollection)(m.invoke(o, null))).iterator();

                    // for each object in the collection
                    while (it.hasNext()) {
                        Object value = it.next();
                        Criteria c = new Criteria();

                        c.addLike("ac", ((BasicObject)value).getAc());
                        Class classe=value.getClass();
                        Query q =  new QueryByCriteria(classe, c);

                        // object persistent in the DB
                        Collection persistentObject =  broker.getCollectionByQuery(q);

                        if (persistentObject.size() == 0)
                        {   // the data entry is not persistent
                            // add a zombie
//                            Object zombie = value.getClass().newInstance();
//                            ((BasicObject)zombie).setAc(((BasicObject)value).getAc());
//                            broker.store(zombie);
//                            col.add(zombie);

                            Object zombie;
                            String ac = ((BasicObject)value).getAc();
                            zombie = createZombie(value.getClass(), ac);
                            broker.store(zombie);
                            newCollection.add(zombie);
                        } else {
                            newCollection.add(persistentObject.toArray()[0]);
                        }
                    }

                    // associate the new collection to the object
                    Class parametersType[] = {Collection.class};
                    Object parameters[] = {newCollection};
                    o.getClass().getMethod( "set" + m.getName().substring(3) , parametersType).invoke(o, parameters);

                    // use the broker to know if the collection correspond to a m-to-n relationship
                    // if yes, keep all olds values
                    String fieldName = m.getName().substring(3,3).toLowerCase() + m.getName().substring(4);
                    CollectionDescriptor descriptor= broker.getClassDescriptor(o.getClass())
                            .getCollectionDescriptorByName(fieldName);
                    if(descriptor != null)  {
                        if (descriptor.isMtoNRelation()) {
                            synchronizeMtoNrelation(o, m, newCollection);
                        }
                    }

                } catch ( NoSuchMethodException nsme) {
                    System.out.println("[XmlLoader][WARNING] setter or getter not found for Collection: " +  m.getName().substring(3) + " in class " + o.getClass().getName());
                }
            } // end for collection
        }
    }



    /**
     * For m to n relationship, we need to keep all information already described in
     * te local database and that could get lost during the update of an object
     */
    private void synchronizeMtoNrelation(Object newObject, java.lang.reflect.Method getter, Collection newCollection) throws Exception
    {
        // get Object o in local database
        Criteria criteria = new Criteria();
        criteria.addLike("ac", ((BasicObject)newObject).getAc());
        Query q =  new QueryByCriteria(newObject.getClass(), criteria);

        Object persistentObject =broker.getObjectByQuery(q);

        if (persistentObject != null)
        {
            Collection persistentCollection = (Collection) getter.invoke(persistentObject, null);
            Iterator it = persistentCollection.iterator();
            while(it.hasNext())
            {
                Object o = it.next();
                if(!newCollection.contains(o))
                {
                    newCollection.add(o);
                }
            }
        }
    }


    /**
     * check if the data entry represented by the object is persistent in the database,
     * else add a zombie
     */
    private void checkPersistence(Object o, java.lang.reflect.Method getter, java.lang.reflect.Method setter) throws Exception
    {
        Object value = getter.invoke(o, null);

        String ac = (String)((o.getClass().getMethod(getter.getName() + "Ac", null)).invoke(o, null))  ;

        Criteria c = new Criteria();
        c.addLike("ac", ac);

        Collection res;
        // check if the entry is persistent in the database
        try {
            res=  broker.getCollectionByQuery(new QueryByCriteria(getter.getReturnType(), c));
        } catch  (Exception e) {
            res = new Vector();
        }

        if (res.size() == 0)
        {   // the data entry is not persistent
            // add a zombie

            Object zombie = null;
            Class type = getter.getReturnType();

            zombie = createZombie(type, ac);

            // store the zombie
//            Class parameters[] = {String.class};
//            Object parametersValues[] = {ac};
//            (o.getClass().getMethod(setter.getName() + "Ac", parameters)).invoke(o, parametersValues);
            broker.store(zombie);

            // assign the zombie to the field
            Object parameter[] = {zombie};
            setter.invoke(o, parameter);
        } else {
            Object parameter[] =  {res.toArray()[0]};
            setter.invoke(o,  parameter);
        }
    }

    private Object createZombie(Class type, String ac) throws Exception {
        Object zombie = null;
        // take care if the return type is not abstract

        // try to create a new instance of the class type,
        // if an error is reached, it's an abstract class
        // then try with one of the subtypes
        while (zombie == null) {
            try {
                zombie = type.newInstance();
            } catch (java.lang.InstantiationException ie) {
                type = getFirstConcreteSubclass(broker.getClassDescriptor(type).getExtentClasses());
                if (type == null) {
                    System.out.println("[XmlLoader] [ERROR] found an abstract type, but no concrete subclasses for " + type.getName());
                    throw new Exception();
                }
//                    System.out.println("[XmlLoader] found an abstract type, create a " + type.getName() + " instead of " + getter.getReturnType().getName());
            }
        }

        if ((BasicObject.class).isAssignableFrom(type))
        {
            ((BasicObject)zombie).setAc(ac);
        }
        else // an Institution
        {
            ((Institution)zombie).setAc(ac);
        }
        return zombie;
    }


    private Class getFirstConcreteSubclass(Vector subclasses) {
        if (subclasses.size() == 0) return null;
        Iterator it = subclasses.iterator();
        while (it.hasNext()) {
            Class type = (Class)it.next();
            if (!broker.getClassDescriptor(type).isInterface()) return type;
        }

        // no concrete class found in the first level of subclasses
        // try with next level
        it = subclasses.iterator();
        Vector subsubclasses = new Vector();
        while (it.hasNext()) {
            subsubclasses.addAll(broker.getClassDescriptor((Class)it.next()).getExtentClasses());
        }
        return getFirstConcreteSubclass(subsubclasses);
    }




    /**
     * initialisation method that should only be used
     * for loading files independantly from the Collector
     */
    private void init() throws Exception
    {
        writer = new Logger(System.out).setPrefix( "intactCore" );

        // Load the mapping file
        mapping = new Mapping( getClass().getClassLoader() );
        mapping.loadMapping( getClass().getResource( MappingFile ) );

        // Initialise XML marshaller
        unmarshaller = new Unmarshaller(ObjectSet.class);
        unmarshaller.setMapping(mapping);

        this.broker = broker;

        broker=PersistenceBrokerFactory.createPersistenceBroker("repository.xml");
    }


    /**
     * This main method is only
     * used for test, and has to be deleted later
     * with method init and constructor XmlLoader()
     */
    public static void main(String[] args) throws Exception
    {
        XmlLoader loader = new XmlLoader();
        loader.init();
        loader.loadFile(args[0]);
    }

}







