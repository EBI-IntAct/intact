/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.correctionAssigner;

import uk.ac.ebi.intact.util.PropertyLoader;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;

/**
 * This method load the data contained in the correctionAssigner.properties file.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SuperCuratorsGetter {

    private HashMap scName2scObject = new HashMap();

    private static final String CORRECTION_ASSIGNER_CONFIG_FILE = "/config/correctionAssigner.properties";

    private static final String NUMBER_CURATOR_PROPERTY = "super.curator.number";
    private static final String NAME_PROPERTIE_DESCRIPTION = "super.curator.name";
    private static final String PERCENTAGE_PROPERTIE_DESCRIPTION = "super.curator.percentage";

    private Collection superCurators = new ArrayList();

    public SuperCuratorsGetter() throws Exception {
        loadConfigFile();
    }

    public Collection getSuperCurators() {
        return superCurators;
    }

    public SuperCurator getSuperCurator(String name){
        return (SuperCurator)scName2scObject.get(name.toLowerCase());
    }

    public void  loadConfigFile() throws Exception {
        Properties props = PropertyLoader.load( CORRECTION_ASSIGNER_CONFIG_FILE );

        /*
        Thow an exception if props is null.
        */
        if(props != null){

            int percentageTotal = 0;
            if(props != null){


                /*
                Get the number of super curators from the property file.
                */
                int superCuratorsNumber;
                String number = props.getProperty(NUMBER_CURATOR_PROPERTY);
                if( number != null){
                    superCuratorsNumber = Integer.parseInt(number);
                }else{
                    throw new Exception("The number of curators hadn't been set properly in " + CORRECTION_ASSIGNER_CONFIG_FILE );
                }

                /*
                Instanciate superCurators using as initial capacity the number of superCurators.
                */
                superCurators = new ArrayList(superCuratorsNumber);


                /*
                Loading each curator from the config file
                */
                for(int i=1; i<=superCuratorsNumber; i++){

                    /*
                    Getting the name of curator i. If not found, throw an exception.
                    */
                    String name = props.getProperty(NAME_PROPERTIE_DESCRIPTION + i);
                    if( name == null ){
                        throw new Exception("Name property is not properly assign for super curator " + i);
                    }

                    /*
                    Getting the percentage of pubmed the super curator will have to review out of the total amount of
                    pubmed to correct. If not found, throw an exception.
                    */
                    int percentage;
                    try{
                        percentage = Integer.parseInt(props.getProperty(PERCENTAGE_PROPERTIE_DESCRIPTION + i));
                        percentageTotal = percentageTotal + percentage;
                    } catch (NumberFormatException nfe){
                        throw new Exception("Name property is not properly assign for super curator " + i);
                    }

                    /*
                    Creating the superCurator object.
                    */
                    SuperCurator superCurator = new SuperCurator(percentage, name);

                    /*
                    Adding the superCurator object to the superCurators collection.
                    */
                    superCurators.add(superCurator);

                    /*
                    Map the name of the curator to the SuperCurator object as it will help to retrieve the SuperCurator
                    object just having the name of the super curator.
                    */
                    scName2scObject.put(superCurator.getName(), superCurator);
                }

                /*
                The sum of all the percentage must be equal to 100%.
                */
                if( percentageTotal != 100 ){
                    throw new Exception( "The sum of each super curator ration should be equal to 100 and is equal to " + percentageTotal );
                }
            }
            else throw new Exception ( "Unable to open the properties file: " + CORRECTION_ASSIGNER_CONFIG_FILE);

        }
    }

}
