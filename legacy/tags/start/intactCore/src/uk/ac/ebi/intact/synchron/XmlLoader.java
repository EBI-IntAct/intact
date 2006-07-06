package uk.ac.ebi.intact.synchron;

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
            System.out.println("[ERROR] Was not possible to load the mappingfile");
              e.printStackTrace(System.out);
            throw e;
        }

        // Initialise XML marshaller
        try {
            unmarshaller = new Unmarshaller(ObjectSet.class);
            unmarshaller.setMapping(mapping);
        } catch (Exception e){
            System.out.println("[ERROR] Was not possible to get the unmarshaller");
            throw e;
        }

        this.broker = broker;
    }


    /**
     * 1,2,1,2, it's just a test
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
            System.out.println("[ERROR] Was not possible to open the file: " + xmlFileName + " for loading");
            throw e;
        }

        // Marshall the objects from file

        ObjectSet objectSet;
        try {
            objectSet = (ObjectSet) unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            System.out.println("[ERROR] Was not possible to unmarshal the file: " + xmlFileName);
            throw e;
        }

        Iterator iterator = objectSet.getObjects().iterator();

        broker.beginTransaction();
        try {
            while (iterator.hasNext())
            {
                Object o = iterator.next();
                System.out.println("[Loader: try to store object of class: " + o.getClass().getName() + "]");
                checkForeignKey(o);
                updateDb(o);
            }
            System.out.println("commitTransaction");
            broker.commitTransaction();
            System.out.println("commit done" );
        } catch (Exception e) {
            broker.abortTransaction();
            System.out.println("[ERROR] errors during loading the file: " + xmlFileName + ": no entry has been added");
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
        System.out.println("[store object with ac= " + ((BasicObject)o).getAc() + "]");
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
        Field fields[] = (o.getClass()).getFields();

        for (int i=0; i < java.lang.reflect.Array.getLength(fields); i++)
        {
            // if the field's value is a BasicObject
            // check if it is persistent in db, else
            // create a zombie of type of the field
            // if it does not already exist
            Field f = (Field)fields[i] ;
            if ( BasicObject.class ==  (f.getType()).getSuperclass() && f.get(o) != null)
            {
                Field acField=null;
//                for (int j=0; j< java.lang.reflect.Array.getLength(fields); j++)
//                {
//                    if ( ((Field)fields[j]).getName() == f.getName() + "Ac")
//                    {
//                        acField = (Field)fields[j];
//                    }
//                }
//                System.out.println("checkPersistence for field: " +  f.getName() + "Ac");
//                System.out.println("checkPersistence for ac field: " +  o.getClass().getField(f.getName() + "Ac"));
                checkPersistence(o, f);
            }
            // if the field contains a Vector,
            // check all the objects it contains
//            else if ( AbstractCollection.class
//                    .isInstance(((Field)fields[i]).getType()))
            else if ( Collection.class == f.getType() && f.get(o) != null)
            {
                Collection col = new Vector();

                Iterator it =  ((AbstractCollection)((Field)fields[i]).get(o)).iterator();
                while (it.hasNext()) {
                    Object value = it.next();
                    Criteria c = new Criteria();

                    c.addLike("ac", ((BasicObject)value).getAc());

                    Class classe=value.getClass();

                    Query q =  new QueryByCriteria(classe, c);
                    Collection res;
                    res =  broker.getCollectionByQuery(q);

                    if (res.size() == 0)
                    {   // the data entry is not persistent
                        // add a zombie
                        Object zombie = value.getClass().newInstance();
                        ((BasicObject)zombie).setAc(((BasicObject)value).getAc());
                        broker.store(zombie);
                        col.add(zombie);
                    } else {
                        col.add(res.toArray()[0]);
                    }
                }
                f.set(o, col);
                CollectionDescriptor des= broker.getClassDescriptor(o.getClass()).getCollectionDescriptorByName(f.getName());
//                Iterator itCollect = des.iterator();
//                while (itCollect.hasNext()) {
//                    System.out.println(" a collection descriptor found: " + ((CollectionDescriptor)itCollect.next()).getAttributeName());
//                }
//                    System.out.println("No CollectionDescriptors");
              //  System.out.println("[Loader: looking for M to N relation for Collection: " + f.getName() +"]");
                if(des != null)
                {
                    if (broker.getClassDescriptor(o.getClass()).getCollectionDescriptorByName(f.getName()).isMtoNRelation())
                    {
                       // System.out.println("[Loader: found M to N relation for field " + f.getName() + "in object of type " + o.getClass() + "]");
                        synchronizeMtoNrelation(o, f, col);
                    }
                }
            }
        }
    }

    /**
     * For m to n relationship, we need to keep all information already described in
     * te local database and that could get lost during the update of an object
     */
    private void synchronizeMtoNrelation(Object newObject, Field f, Collection newCollection) throws Exception
    {
        // get Object o in local database
        Criteria criteria = new Criteria();
        criteria.addLike("ac", ((BasicObject)newObject).getAc());
        Query q =  new QueryByCriteria(newObject.getClass(), criteria);

        Object persistentObject =broker.getObjectByQuery(q);

        if (persistentObject != null)
        {
//            System.out.println("[Loader: For this object: synchronize M to N relationship described by th Collection: "+f.getName()+" ]");
            Collection persistentCollection = (Collection) f.get(persistentObject);
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
    private void checkPersistence(Object o, Field f) throws Exception
    {
        Object value = f.get(o);

        String ac = (String)((o.getClass().getMethod("get" + f.getName().toUpperCase().charAt(0) + f.getName().substring(1) + "Ac", null)).invoke(o, null))  ;

        if (ac == null) ac = "";

        Criteria c = new Criteria();
        c.addLike("ac", ac);

        Collection res =  broker.getCollectionByQuery(new QueryByCriteria(f.getType(), c));
        if (res.size() == 0)
        { // the data entry is not persistent
            // add a zombie
            Object zombie = f.getType().newInstance();
            if (ac != null && ac != "")
            {
                ((BasicObject)zombie).setAc(ac);
            }
            else
            {
                ((BasicObject)zombie).setAc("zombie");
            }

            // the name "Zombie" is given to the new object
            // it makes easy to identifie zombie objects in the database
            //((BasicObject)zombie).setName("Zombie");
            f.set(o, zombie);

            Class[] parameters = {String.class};
            Object[] parametersValues = {ac};
            (o.getClass().getMethod("set" + f.getName().toUpperCase().charAt(0) + f.getName().substring(1) + "Ac", parameters)).invoke(o, parametersValues);
            broker.store(zombie);
        }  else {
            f.set(o, res.toArray()[0]);
        }
    }



    /**
     * 1,2,1,2, it's just a test
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







