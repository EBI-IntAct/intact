/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;
import java.io.*;

import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.persistence.*;


/**
 * Quick and dirty application to demo very simple qerying via the intact API,
 * OJB and an oracle DB
 *
 * @author Chris Lewington
 */
public class IntactCoreApp {

    IntactHelper helper;
    DAOSource dataSource;

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper
     */
    public IntactCoreApp() throws Exception {

        dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

        //set the config details, ie repository file for OJB in this case
        Map config = new HashMap();
        config.put("mappingfile", "config/repository.xml");
        dataSource.setConfig(config);

        try {

            helper = new IntactHelper(dataSource);

        } catch (IntactException ie) {

            //something failed with type map or datasource...
            String msg = "unable to create intact helper class - no datasource";
            System.out.println(msg);
            ie.printStackTrace();

        }
    }

    public Collection doSearch(String className, String searchParam, String searchValue) throws Exception {

        Collection resultList = null;
        if (helper != null) {

            //NB assumes full java className supplied...
            resultList = helper.search(className, searchParam, searchValue);
            return resultList;

        } else {

            System.out.println("something failed - couldn't create a helper class to access the data!!");
            throw new Exception();
        }


    }

    public static void main(String[] args) throws Exception {

        /*
        * basic idea: create an IntactHelper, then use it to submit command-line
        * search queries - NB searches only on AC or name, as it's a simple demo!!
        */

        Collection results = null;
        IntactCoreApp app = new IntactCoreApp();

        //I/O...
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

        while (true) {
            try {
                System.out.println("Please enter the type of intact object you wish to search for:");
                String itemType = "uk.ac.ebi.intact.model." + in.readLine();

                System.out.println("do you wish to search by AC or name (enter 'ac' or 'name')?:");


                String searchParam = in.readLine();
                System.out.println(searchParam + ":");

                System.out.println(" enter the search value:");
                String searchValue = in.readLine();

                System.out.println("search details entered:");
                System.out.println("intact type:" + itemType);
                System.out.println("search param: " + searchParam);
                System.out.println("search value: " + searchValue);
                results = app.doSearch(itemType, searchParam, searchValue);

                System.out.println("returned OK from helper search...");
                if (results.isEmpty()) {

                    //no matches found - forward to a suitable page
                    System.out.println("Sorry, no matches were found for your specified search criteria");

                } else {

                    //write results to console..
                    //NB need to do this for ALL references from the search results also!!..
                    Iterator it = results.iterator();
                    System.out.println("search results:");
                    while (it.hasNext()) {

                        System.out.println(it.next().toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*
        in.close();
        out.close();
           */
    }
}
