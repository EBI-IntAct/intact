/*
 * Created by IntelliJ IDEA.
 * User: aceol
 * Date: Oct 17, 2002
 * Time: 4:09:09 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
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


public class CheckMapping {

    private List classes;

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
            System.out.println("[CheckMappings] [ERROR] Was not possible to get the poperties");
            throw e;
        }

        System.out.println("[CheckMappings] got properties");

        // initialise OJB broker
        try{
            broker=PersistenceBrokerFactory.createPersistenceBroker(RepositoryFile);
        } catch(MalformedURLException e) {
            System.out.println("[CheckMappings] [ERROR] Was not possible to get the repository file");
            throw e;
        }

        //initialize FileWriter and PrintWriter for Castor output messages

        System.out.println("[CheckMappings] Created appropriate Filename: " + fileName);

        try{
            outputFile = new CheckedOutputStream(new FileOutputStream(properties.getProperty("CheckMappings.directory","") + fileName + ".xml"), new CRC32());
        } catch(FileNotFoundException e) {
            System.out.println("[CheckMappings] [ERROR] Was not possible to open the output file");
            throw e;
        }
        writer = new Logger(outputFile).setPrefix(properties.getProperty("loggerPrefix",""));

        // Load the mapping file and builds the mapping
        System.out.println("[CheckMappings] createMapping");
        mapping = new Mapping( getClass().getClassLoader() );

        try{
            mapping.loadMapping( getClass().getResource( MappingFile ) );
        } catch(Exception e) {
            System.out.println("[CheckMappings] [ERROR] Was not possible to load the mappingfile");
            throw e;
        }
        mapping.setLogWriter( writer );

        // Initialise Castor XML marshaller

        try{
            marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
        } catch (Exception e){
            System.out.println("[CheckMappings] [ERROR] Was not possible to get the marshaller");
            throw e;
        }
    }


    private void getClasses() {
//        Package package = java.lang.Package.getPackage(uk.ac.ebi.intact.model);
        File dir = new File("../model");

        // get name of the files in the directory model
        String[] files = dir.list();

        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".java")) {
                try {
                    Class c = Class.forName("uk.ak.ebi.intact.model." + files[i].substring(1, files[i].lastIndexOf(".")));
                    System.out.println("[CheckMappings] add class " + "uk.ak.ebi.intact.model." +
                            files[i].substring(1, files[i].lastIndexOf(".")));
                    classes.add(c);
                } catch (Exception e) {}
            }
        }
    }

    private void checkClass(Class c) {


        // get the getters
        java.lang.reflect.Method getters1[] = c.getDeclaredMethods();
        java.lang.reflect.Method getters2[] = c.getMethods();

        ArrayList getters = new ArrayList();

        for (int k = 0; k < java.lang.reflect.Array.getLength(getters1); k++) {
            if (getters1[k].getName().startsWith("get")) {
                getters.add( getters1[k] );
            }
        }
        for (int k = 0; k < java.lang.reflect.Array.getLength(getters2); k++) {
            if (getters1[k].getName().startsWith("get")) {
                getters.add( getters2[k] );
            }
        }

        Iterator gettersIt = getters.iterator();
        while (gettersIt.hasNext()) {
            String fieldName = ((java.lang.reflect.Method)(gettersIt.next())).getName().substring(3);
            checkOJB(c, fieldName);
            checkCastor(c, fieldName);
        }
    }


   private void checkOJB(Class c, String fieldName) {
       ClassDescriptor cdescriptor = broker.getClassDescriptor(c);

       FieldDescriptor fdescriptor = cdescriptor.getFieldDescriptorByName(fieldName);

       try {



           if (c.getMethod("get" + fieldName, null).getReturnType() == Collection.class) {
           }
       }  catch(Exception e) {}
   }

   private void checkCastor(Class f, String fieldName)
   {

   }


}
