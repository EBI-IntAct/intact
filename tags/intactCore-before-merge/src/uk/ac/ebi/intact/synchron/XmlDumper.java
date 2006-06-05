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
 *
 * @author Antje Mueller, Arnaud Ceol
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
            System.out.println("[XmlDumper] [ERROR] Was not possible to get the poperties");
            throw e;
        }

        System.out.println("[XmlDumper] got properties");

        // initialise OJB broker
        try{
            broker=PersistenceBrokerFactory.createPersistenceBroker(RepositoryFile);
        } catch(MalformedURLException e) {
            System.out.println("[XmlDumper] [ERROR] Was not possible to get the repository file");
            throw e;
        }

        //initialize FileWriter and PrintWriter for Castor output messages
        try{
            fileName =getFileName();
        } catch(Exception e){
            throw e;
        }

        System.out.println("[XmlDumper] Created appropriate Filename: " + fileName);

        try{
            outputFile = new CheckedOutputStream(new FileOutputStream(properties.getProperty("XmlDumper.directory","") + fileName + ".xml"), new CRC32());
        } catch(FileNotFoundException e) {
            System.out.println("[XmlDumper] [ERROR] Was not possible to open the output file");
            throw e;
        }
        writer = new Logger(outputFile).setPrefix(properties.getProperty("loggerPrefix",""));

        // Load the mapping file and builds the mapping
        System.out.println("[XmlDumper] createMapping");
        mapping = new Mapping( getClass().getClassLoader() );

        try{
            mapping.loadMapping( getClass().getResource( MappingFile ) );
        } catch(Exception e) {
            System.out.println("[XmlDumper] [ERROR] Was not possible to load the mappingfile");
            throw e;
        }
        mapping.setLogWriter( writer );

        // Initialise Castor XML marshaller

        try{
            marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
        } catch (Exception e){
            System.out.println("[XmlDumper] [ERROR] Was not possible to get the marshaller");
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

        System.out.println("[XmlDumper] getFileName");

        broker.beginTransaction();

        //select db entry about itselfs
        intactNode = (IntactNode) broker.getObjectByQuery(query);
        if (intactNode == null) {
            System.out.println("[XmlDumper] [ERROR] No node found for the prefix specified in property file");
            throw new Exception();
        }

        broker.commitTransaction();

        //build file name
        System.out.println("[XmlDumper] Got db entry about my own intactnode to build the filename ac: "+ intactNode.getAc());
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

        System.out.println("[XmlDumper] updated my intactnode entry...");
        System.out.println("[XmlDumper] lastProvidedId: "+ intactNode.getLastProvidedId());
        System.out.println("[XmlDumper] lastProvidedDate: " + intactNode.getLastProvidedDate());
        System.out.println("[XmlDumper] dateOflastFile: " + dateOfLastFile.toString());

        return fileName;

    }


    /**
     * Dump all requiered data enties from the database in
     * XML format
     */
    private void fillFile()
            throws Exception {

        System.out.println("[XmlDumper] Try to fill the file");

        Collection result;
        Query query;
        Criteria criteria = new Criteria();
        ObjectSet container = new ObjectSet();
        Iterator iterator;

        //build "where" part of the query
        criteria.addLike("ac", "%" + properties.getProperty("ownerPrefix")+"%");
        criteria.addOrderByAscending("ac");
        if( !release ) {
            criteria.addGreaterThan("updated",dateOfLastFile);
        }

        //build query
        System.out.println("[XmlDumper] created appropriate query to select the required data");

        //Collect objects
        int cpt = 0;
        query =	QueryFactory.newQuery(BasicObject.class,criteria,true);

        broker.beginTransaction();

        result = broker.getCollectionByQuery(query);
        System.out.println("[XmlDumper] selected data");

        broker.commitTransaction();

        // idem for Institutions
        criteria.addLike("ac", "%" + properties.getProperty("ownerPrefix")+"%");
        criteria.addOrderByAscending("ac");
        if( !release ) {
            criteria.addGreaterThan("updated",dateOfLastFile);
        }

        //Collect institutions
        query =	QueryFactory.newQuery(Institution.class,criteria,true);

        broker.beginTransaction();

        result.addAll(broker.getCollectionByQuery(query));

        broker.commitTransaction();



        //put collected objects in a container
        iterator =result.iterator() ;
        while (iterator.hasNext() ) {
            Object o = iterator.next();
            // remove from collections informations we don't need in the Xml file = everything but ac
            simplifyCollections(o);
            container.addObject(o);
            cpt++;
        }

        broker.close();
        System.out.println("[XmlDumper] put " + cpt + " objects into a container");

        // XML dump container
        try{
            marshaller.marshal(container);
        } catch(MarshalException e){
            System.out.println("[XmlDumper] [ERROR] Was not possible to marshal the container");
            throw e;
        }

        System.out.println("[XmlDumper] container marshalled");
    }


   private void simplifyCollections(Object o) throws IllegalAccessException, InstantiationException{

        Field fields[] = (o.getClass()).getFields();

        for (int i=0; i < java.lang.reflect.Array.getLength(fields); i++)
        {

            Field f = (Field)fields[i] ;
            if (Collection.class == f.getType())
            {
                Collection c = (Collection)f.get(o);
                Collection newC = new Vector();

                Iterator it = c.iterator();
                while(it.hasNext())
                {
                    BasicObject e = (BasicObject)it.next();
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

        System.out.println("[XmlDumper] build checksum for the "+ fileName + ".xml and writes this to a " + fileName + ".crc32 file");
        try {
            FileWriter checksumFile = new FileWriter(properties.getProperty("XmlDumper.directory","") + fileName + ".crc32");

            checksumFile.write(String.valueOf(outputFile.getChecksum().getValue()));
            checksumFile.close();
        } catch (Exception e) {
            System.out.println("[XmlDumper] [ERROR] Was not possible to create the checksum file");
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
                    System.out.println("[XmlDumper] initialisation]" );
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

