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
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Protein;


/**
 * Quick and dirty application to demo very simple qerying via the intact API,
 * OJB and an oracle DB
 *
 * @author Chris Lewington
 */
public class WriteTest {

    IntactHelper helper;
    DAOSource dataSource;
    DAO dao;

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper
     */
    public WriteTest() throws Exception {

        dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

        //set the config details, ie repository file for OJB in this case
        Map config = new HashMap();
        config.put("mappingfile", "config/repository.xml");
        dataSource.setConfig(config);

        try {
            dao = dataSource.getDAO();
        } catch (Exception e) {
            e.printStackTrace();
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
        WriteTest app = new WriteTest();

//I/O...
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

        try {
            System.out.println("Trying to create objects: ");
            Institution newInstitution = new Institution();
            newInstitution.setShortLabel("EB2");
            app.dao.create(newInstitution);
            System.out.println("Created: " + newInstitution + "\n");
            Protein newProtein = new Protein();
            newProtein.setShortLabel(" " + System.currentTimeMillis() % 1000000);
            newProtein.setOwner(newInstitution);
            app.dao.create(newProtein);
            System.out.println("Created: " + newProtein + "\n");

            System.out.println("Classname: " + newProtein.getClass().getName() + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
