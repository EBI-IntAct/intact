/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import java.util.*;
import java.io.*;

import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.simpleGraph.*;


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

    /**
     *  Test search by object only.
     */
    public Collection doSearch(Object obj) throws Exception {

            Collection resultList = null;
            if (helper != null) {

                resultList = helper.search(obj);
                return resultList;

            }
            else {

                System.out.println("something failed - couldn't create a helper class to access the data!!");
                throw new Exception();
            }


    }

    /**
    *  Test Experiment search using an Institution
    */
    public Collection doExpSearch(Institution institution) throws Exception {

        Collection resultList = null;
        if (helper != null) {

            resultList = helper.getExperimentsByInstitution(institution);
            return resultList;

        }
        else {

            System.out.println("something failed - couldn't create a helper class to access the data!!");
            throw new Exception();
        }


    }

    /**
    *  Test name search, ie by fullName
    */
    public Object doNameSearch(Class clazz, String name) throws Exception {

        if (helper != null) {

            return helper.getObjectByName(clazz, name);

        }
        else {

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

        while (true) {
            try {
                System.out.println("Testing simple (type, parameter, value) searches.....");
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

                if(searchParam.equals("ac")) {

                    results = app.doSearch(itemType, searchParam, searchValue);
                    System.out.println("returned OK from helper search...");
                    if (results.isEmpty()) {

                        //no matches found
                        System.out.println("Sorry, no matches were found for your specified search criteria");

                    }
                    else {

                        //write results to console..
                        Iterator it = results.iterator();
                        System.out.println("search results:");
                        while (it.hasNext()) {

                            System.out.println(it.next().toString());
                        }
                    }

                }
                else if(searchParam.equals("name")) {

                    Object result = app.doNameSearch(Class.forName(itemType), searchValue);

                    if(result != null) {

                        System.out.println("search result - search by name (" + searchValue + ") for (" + itemType + "):");
                        System.out.println(result.toString());
                    }
                    else {

                        System.out.println("no matches found with query on " + itemType + " by name " + searchValue);
                    }
                }
                else {

                    System.out.println("sorry, at this point the search param should be 'ac' or 'name': not what you entered!");
                    System.out.println("skipping to next test.....");
                }

                /* Test the SimpleGraph generation */

                System.out.println();
                System.out.println("testing  SimpleGraph generation ...");
                System.out.println("enter an Interactor ac:");

                String proteinAc = in.readLine();
                System.out.println("Please enter graph depth: ");

                int depth = Integer.parseInt(in.readLine());


                //get a complete Institution, then add it to an Experiment..
                System.out.println("retrieving Interactor ...");
                results = app.doSearch("uk.ac.ebi.intact.model.Interactor", "ac", proteinAc);
                Iterator iter1 = results.iterator();

                //there is at most one - ac is unique
                if (iter1.hasNext()){

                    System.out.println("Starting graph generation ... ");
                    Interactor interactor = (Interactor)iter1.next();
		    Graph graph = new Graph();
                    System.out.println(app.helper.subGraph(interactor, depth, null,
                            Constants.EXPANSION_BAITPREY, graph));
                } else {
                    System.out.println("AC not found: " + proteinAc + "\n");
                    continue;
                }

                System.out.println();
                System.out.println("Now testing search by object with an AC set....enter intact object type (subclasses of BasicObject only):");
                itemType = in.readLine();
                System.out.println("enter AC of object:");
                String ac = in.readLine();

                System.out.println("searching....");

                String className = "uk.ac.ebi.intact.model." + itemType;
                //build an example...
                Class searchClass = Class.forName(className);
                BasicObject bo = null;
                if(BasicObject.class.isAssignableFrom(searchClass)) {

                    bo = (BasicObject)searchClass.newInstance();
                    bo.setAc(ac);

                    //NB created/updated auto-set in BasicObject - turn OFF in search example
                    bo.setCreated(null);
                    bo.setUpdated(null);

                    //do search - param assumes a no-arg constructor exists
                    results = app.doSearch(bo);
                    if (results.isEmpty()) {

                        //no matches found
                        System.out.println("Sorry, no matches were found for your specified search criteria");

                    }
                    else {

                        //write results to console..
                        System.out.println("search results for object of type " + itemType +":");
                        Iterator it1 = results.iterator();
                        while (it1.hasNext()) {

                            System.out.println(it1.next().toString());
                        }
                        System.out.println();
                    }
                }
                else {

                    System.out.println("sorry, this test only covers subclasses of BasicObject for now..");
                }

                System.out.println();
                System.out.println("testing helper method getExperimentsByInstitution....");
                System.out.println("enter an Institution AC:");

                ac = in.readLine();

                //get a complete Institution, then add it to an Experiment..
                System.out.println("retrieving Institution...");
                results = app.doSearch("uk.ac.ebi.intact.model.Institution", "ac", ac);
                Iterator iter = results.iterator();

                //there is only one - ac is unique
                Institution institution = (Institution)iter.next();
                System.out.println("sanity check - the Institution to be searched on is:");
                System.out.println(institution.toString());
                System.out.println();

                //now search for the matching experiments
                results = app.doExpSearch(institution);

                if (results.isEmpty()) {

                    //no matches found
                    System.out.println("Sorry, no matches were found for your specified search criteria");

                }
                else {

                    //write results to console..
                    System.out.println("search results for test of getExperimentsByInstitution...");
                    Iterator it2 = results.iterator();
                    while (it2.hasNext()) {

                        System.out.println(it2.next().toString());
                    }
                    System.out.println();
                }

                System.out.println("Now testing search by object with collection.....");
                System.out.println("currently hard-coded test: search for a Protein, given a single Xref...");
                System.out.println("building example Xref object, with AC = EBI-20...");
                Xref xref = new Xref();
                xref.setAc("EBI-20");

                //NB unset created/updated for searching...
                xref.setUpdated(null);
                xref.setCreated(null);

                System.out.println("building example (empty) Protein to search on.....");
                Protein prot = new Protein();

                //NB unset created/updated for searching...
                prot.setUpdated(null);
                prot.setCreated(null);

                System.out.println("adding Xref to Protein example....");
                prot.addXref(xref);
                System.out.println("done - example built, now performing search ..");

                results = app.doSearch(prot);
                if (results.isEmpty()) {

                    //no matches found
                    System.out.println("something went wrong - no match found!!");

                }
                else {

                    //write results to console..
                    System.out.println("search results: looking for a Protein with an Xref AC = EBI-20...");
                    System.out.println("(expecting EBI-19 details)....");
                    Iterator it2 = results.iterator();
                    while (it2.hasNext()) {

                        System.out.println(it2.next().toString());
                    }
                    System.out.println();
                }

                System.out.println("now testing search by object with collection, (m:n relation)....");
                System.out.println("currently hard-coded test: search for an Interaction, given a single Experiment...");
                System.out.println("building example Experiment object, with AC = EBI-17...");
                Experiment exp = new Experiment();

                //NB unset created/updated for searching...
                exp.setUpdated(null);
                exp.setCreated(null);

                exp.setAc("EBI-17");
                System.out.println("building example (empty) Interaction to search on.....");
                Interaction interaction = new Interaction();

                //NB unset created/updated for searching...
                interaction.setUpdated(null);
                interaction.setCreated(null);

                System.out.println("adding Experiment to Interaction example....");
                interaction.addExperiment(exp);
                System.out.println("done - example built, now performing search ..");

                results = app.doSearch(interaction);
                if (results.isEmpty()) {

                    //no matches found
                    System.out.println("something went wrong - no match found!!");

                }
                else {

                    //write results to console..
                    System.out.println("search results: looking for Interactions with an Experiment that has AC = EBI-17...");
                    System.out.println("(expecting details for EBI ACs 235, 240, 243, 246, 250, 255, 258)....");
                    Iterator it2 = results.iterator();
                    while (it2.hasNext()) {

                        System.out.println(it2.next().toString());
                    }
                    System.out.println();
                }

            }
            catch (Exception e) {

                e.printStackTrace();
            }
        }
        /*
        in.close();
        out.close();
           */
    }
}
