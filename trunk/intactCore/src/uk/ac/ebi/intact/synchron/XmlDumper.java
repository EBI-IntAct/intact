package uk.ac.ebi.intact.synchron;

// JDK
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.zip.*;
import java.net.MalformedURLException;
import java.lang.reflect.Field;


// Castor
import org.xml.sax.ContentHandler;
import org.exolab.castor.util.Logger;
import org.exolab.castor.xml.*;
import org.exolab.castor.mapping.Mapping;
import org.apache.xml.serialize.*;


// Intact
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.Utilities;
import uk.ac.ebi.intact.util.ObjectSet;

//OJB
import org.apache.ojb.broker.*;
import org.apache.ojb.broker.util.logging.*;
import org.apache.ojb.broker.accesslayer.*;
import org.apache.ojb.broker.query.*;
import org.apache.ojb.broker.util.*;
import org.apache.ojb.broker.metadata.*;

/**
 * Dump all data to XML in a normalised XML format.
 */
public class XmlDumper {


    private Properties properties = null;

    //information about the mapping between Java objects and XML; needed by Castor
    public static final String
            MappingFile = "dbIoMapping.xml";

    //information about the mapping between persistent objects and Java objects; needed by the OJB broker
    public static final String
            RepositoryFile = "repository.xml";

    private static Mapping  mapping;

    //OJB PersistenceBroker for the access to persistent objects
    public PersistenceBroker broker;

    // Writer for castor messages
    private String fileName = "";
    private static PrintWriter   writer;

    private CheckedOutputStream outputFile;
    // Marshaller for XML output
    private static Marshaller marshaller;


    //information about which data has to be selected
    private boolean release = false;
    private Timestamp dateOfLastFile;


    // * method implementation

    /**
     * Initialize FileWriter, PrintWriter, Castor Mapping, Castor Marshaller and OJB PersistenceBroker
     */
    private void init() throws Exception {


        //get properties from the property file
        try{
            properties=Utilities.getProperties("config");
        } catch(Exception e) {
            System.out.println("[ERROR] Was not possible to get the poperties");
            throw e;
        }

        System.out.println("got properties\n");
        System.out.println("a property: " + properties.getProperty("XmlDumper.directory",""));

        // initialise OJB broker
        try{
            broker=PersistenceBrokerFactory.createPersistenceBroker(RepositoryFile);
        } catch(MalformedURLException e) {
            System.out.println("[ERROR] Was not possible to get the repository file");
            throw e;
        }


        System.out.println("\nOJB Broker\n");


        //initialize FileWriter and PrintWriter for Castor output messages
        try{
            fileName =getFileName();
        } catch(Exception e){
            throw e;
        }

        System.out.println("Created appropriate Filename: " + fileName+"\n");

        try{
            outputFile = new CheckedOutputStream(new FileOutputStream(properties.getProperty("XmlDumper.directory","") + fileName + ".xml"), new CRC32());
        } catch(FileNotFoundException e) {
            System.out.println("[ERROR] Was not possible to open the output file");
            throw e;
        }

        writer = new Logger(outputFile).setPrefix(properties.getProperty("loggerPrefix",""));

        // Load the mapping file and builds the mapping
        System.out.println("createMapping \n");
        mapping = new Mapping( getClass().getClassLoader() );

        try{
            mapping.loadMapping( getClass().getResource( MappingFile ) );
        } catch(Exception e) {
            System.out.println("[ERROR] Was not possible to load the mappingfile");
            throw e;
        }
        mapping.setLogWriter( writer );

        // Initialise Castor XML marshaller

        try{
            marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
        } catch (Exception e){
            System.out.println("[ERROR] Was not possible to get the marshaller");
            throw e;
        }
    }

    /**
     * accesses to the db table IntactNode builds the correct file name and updates the db entry
     */
    private String  getFileName() throws Exception{

        IntactNode intactNode = new IntactNode();
        Query query;
        Criteria criteria = new Criteria();
        String fileName = "";
        GregorianCalendar currentDate = new GregorianCalendar();



        //build query for the access to the IntactNode table
        criteria.addLike("ownerPrefix",(properties.getProperty("ownerPrefix")));
        query =	QueryFactory.newQuery(IntactNode.class,criteria);

        System.out.println("getFileName \n");

        broker.beginTransaction();
        System.out.println("beginTransaction \n");
        System.out.println("[[query: " + query.toString() + "]]");

        //select db entry about itselfs
        intactNode = (IntactNode) broker.getObjectByQuery(query);
        if (intactNode == null) {
            System.out.println("[ERROR][No node found for the prefix specified in property file");
            throw new Exception();
        }

        System.out.println("getIntactNode " + broker.getExtentClass(intactNode.getClass()) + "\n");

        broker.commitTransaction();



        //build file name
        System.out.println("\nGot db entry about my own intactnode to build the filename ac: "+ intactNode.getAc()+"\n");
        intactNode.setLastProvidedId(intactNode.getLastProvidedId() + 1);

        fileName += String.valueOf(intactNode.getLastProvidedId());  //add serial number to the name by increasing the last on; concurrently, the field of the object is updated
        fileName += "-";
        if(release)
            fileName += "R";
        else
            fileName += "U";
        fileName += "-";
        currentDate.setTime(new java.util.Date(System.currentTimeMillis()));

        //year
        fileName += currentDate.get(Calendar.YEAR);
        // month
        if(currentDate.get(Calendar.MONTH) < 10)
            fileName += "0";
        fileName += currentDate.get(Calendar.MONTH) + 1;
        // day
        if(currentDate.get(Calendar.DAY_OF_MONTH) < 10)
            fileName += "0";
        fileName += currentDate.get(Calendar.DAY_OF_MONTH);
        fileName += "-";

        fileName += properties.getProperty("ownerPrefix","UNKNOWN");

        //fill dateOfLastFile field with the date of the last file, which was provided
        dateOfLastFile = new Timestamp(intactNode.getLastProvidedDate().getTime());

        //update dateOfLastFile field of the object
        intactNode.getLastProvidedDate().setTime(System.currentTimeMillis());

        //update db entry
        broker.store(intactNode);

        System.out.println("updated my intactnode entry...");
        System.out.println("lastProvidedId: "+ intactNode.getLastProvidedId() + "\n");
        System.out.println("lastProvidedDate: " + intactNode.getLastProvidedDate() + "\n");
        System.out.println("dateOflastFile: " + dateOfLastFile.toString() +"\n");

        return fileName;

    }


    /**
     * Dump all requiered data enties from the database in
     * XML format
     */
    private void fillFile()
            throws Exception {

        System.out.println("Try to fill the file\n");

        Collection result;
        Query query;
        Criteria criteria = new Criteria();
        ObjectSet container = new ObjectSet();
        Iterator iterator;
        //Criteria criteriaUpdated = new Criteria();

        //build "where" part of the query
        criteria.addLike("ac", "%" + properties.getProperty("ownerPrefix")+"%");
        if( !release ) {
            criteria.addGreaterThan("updated",dateOfLastFile);
        }

        //build query
//        query =	QueryFactory.newQuery(BasicObject.class,criteria,true);

        System.out.println("created appropriate query to select the required data\n");



        //Collect objects
//        result = broker.getCollectionByQuery(query);
        int cpt = 0;
        query =	QueryFactory.newQuery(BasicObject.class,criteria,true);

        broker.beginTransaction();

        result = broker.getCollectionByQuery(query);
        System.out.println("selected data\n");

        broker.commitTransaction();

        //put collected objects in a container
        iterator =result.iterator() ;
        while (iterator.hasNext() ) {
            Object o = iterator.next();

            simplifyCollections(o);

            container.addObject(o);
            cpt++;
        }



//        ClassDescriptor cd = broker.getClassDescriptor(Interaction.class);
//        Vector subclasses = cd.getExtentClasses();
//        Iterator it = subclasses.iterator();
//
//        while(it.hasNext())
//        {
//            Class c = (Class)(it.next());
//            System.out.println("[Dump class: " + c.getName() + "]");
//            query =	QueryFactory.newQuery(c,criteria,true);
//
//            broker.beginTransaction();
//
//            result = broker.getCollectionByQuery(query);
////          System.out.println("selected data\n");
//
//            broker.commitTransaction();
//
//            //put collected objects in a container
//            iterator =result.iterator() ;
//
////            int cpt = 0;
//
//            while (iterator.hasNext() ) {
//                Object o = iterator.next();
//                container.addObject(o);
//                cpt++;
//            }
//        }
        broker.close();
        System.out.println("put all recieved objects into a container: " + cpt +"\n");

        // XML dump container
        try{
            marshaller.marshal(container);
        } catch(MarshalException e){
            System.out.println("[ERROR] Was not possible to marshal the container");
            throw e;
        }

        System.out.println("marshall container\n");
    }


   private void simplifyCollections(Object o) throws IllegalAccessException, InstantiationException{

        Field fields[] = (o.getClass()).getFields();

        for (int i=0; i < java.lang.reflect.Array.getLength(fields); i++)
        {

            Field f = (Field)fields[i] ;
            if (Collection.class == f.getType())
            {
                Collection c = (Collection)f.get(o);
//                System.out.println("Object: "+((BasicObject)o).getAc());
                Collection newC = new Vector();

                Iterator it = c.iterator();
                while(it.hasNext())
                {
                    BasicObject e= (BasicObject)it.next();
//                    System.out.println("Simplify Object: "+e.getAc()+ " of type: "+e.getClass().getName());
                    Object emptyObject = e.getClass().newInstance() ;
                    ((BasicObject)emptyObject).setUpdated(null);
                    ((BasicObject)emptyObject).setCreated(null);
                    ((BasicObject)emptyObject).setAc(e.getAc());
                    newC.add(emptyObject);

                }
                f.set(o, newC);
            }
        }

   }

    /**
     * Computes the checksum for the output file an writes it to the checksum file
     */

    private void buildCheckSum() throws FileNotFoundException, IOException{

        System.out.println("build checksum for the "+ fileName + ".xml and writes this to a " + fileName + ".crc32 file\n");
        try {
            FileWriter checksumFile = new FileWriter(properties.getProperty("XmlDumper.directory","") + fileName + ".crc32");

            checksumFile.write(String.valueOf(outputFile.getChecksum().getValue()));
            checksumFile.close();
        } catch (Exception e) {
            System.out.println("[ERROR] Was not possible to create the checksum file");
        }

    }


    public static void main(String[] args) throws Exception
    {
        if(args.length == 0){
            System.out.println("\n\n*******************************************************************************************\n\n");
            System.out.println("MISSING ARGUMENT!!!\n\n");
            System.out.println("mandatory input argument: \"update\" or \"release\"\n\n");
            System.out.println("*******************************************************************************************\n\n");
        }
        else{
            if(!(args[0].equalsIgnoreCase("release") || args[0].equalsIgnoreCase("update"))){

                System.out.println("\n\n*******************************************************************************************\n\n");
                System.out.println("FALSE ARGUMENT!!!\n\n");
                System.out.println("mandatory input argument: \"update\" or \"release\"\n\n");
                System.out.println("*******************************************************************************************\n\n");
                System.out.println(args[0]+ "\n");

            }
            else{
                XmlDumper xmlDumper = new XmlDumper();

                if(args[0].equalsIgnoreCase("release"))
                    xmlDumper.release = true;

                try {
                    System.out.println("[XmlDumper: initialisation]" );
                    xmlDumper.init();
                    xmlDumper.fillFile();
                    xmlDumper.buildCheckSum();

                }   catch (Exception e) {
                    System.out.println("\n\n*********************************************************\n\n");
                    System.out.println("           DUMPING FAILED!\n\n");
                    System.out.println("           check \"[ERROR]\" messages");
                    System.out.println("\n\n*********************************************************\n\n");
                    e.printStackTrace(System.out);
                    System.exit(0);
                }
            }
        }
    }
}

