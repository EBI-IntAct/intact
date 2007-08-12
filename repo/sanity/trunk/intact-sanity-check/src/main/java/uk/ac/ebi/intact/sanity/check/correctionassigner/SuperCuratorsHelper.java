/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.correctionassigner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * This method load the data contained in the correctionAssigner.properties file.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: SuperCuratorsGetter.java,v 1.1 2006/04/05 16:02:54 catherineleroy Exp $
 */
public class SuperCuratorsHelper
{

    private static final String NUMBER_CURATOR_PROPERTY = "super.curator.number";
    private static final String NAME_PROPERTIE_DESCRIPTION = "super.curator.name";
    private static final String PERCENTAGE_PROPERTIE_DESCRIPTION = "super.curator.percentage";

    private Collection superCurators;

    public SuperCuratorsHelper(Properties properties) throws Exception {
        this.superCurators = parseCuratorsFromProperties(properties);

        checkCurators();
    }

    public SuperCuratorsHelper(Collection<SuperCurator> superCurators) throws Exception {
        this.superCurators = superCurators;

        checkCurators();
    }

    public Collection<SuperCurator> getSuperCurators() {
        return superCurators;
    }

    public SuperCurator getSuperCurator(String name){
        for (SuperCurator curator : getSuperCurators()) {
            if (curator.getName().equalsIgnoreCase(name)) {
                return curator;
            }
        }

        throw new AssignerConfigurationException("Curator not found with name: "+name);
    }

    public void checkCurators() {
        int percentageTotal = 0;

        for (SuperCurator curator : getSuperCurators()) {
           percentageTotal += curator.getPercentage();
        }

        if (percentageTotal != 100) {
            throw new AssignerConfigurationException("Total percentage is different to 100%: "+percentageTotal+" ("+superCurators+")");
        }
    }

    public Collection<SuperCurator> parseCuratorsFromProperties(Properties props) throws Exception {
        /*
        Thow an exception if props is null.
        */
        if(props != null){

            if(props != null){


                /*
                Get the number of super curators from the property file.
                */
                int superCuratorsNumber;
                String number = props.getProperty(NUMBER_CURATOR_PROPERTY);
                if( number != null){
                    superCuratorsNumber = Integer.parseInt(number);
                }else{
                    throw new Exception("The number of curators hadn't been set properly in the properties" );
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


                }

            }
            else throw new Exception ( "Unable to open the properties");

        }

        return superCurators;
    }

}
