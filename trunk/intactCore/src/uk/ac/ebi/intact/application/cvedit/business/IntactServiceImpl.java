/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.business;

import java.util.*;
import java.beans.*;

import uk.ac.ebi.intact.application.cvedit.exception.MissingIntactTypesException;
import uk.ac.ebi.intact.util.Key;

/**
 * Implments the IntactService interface.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactServiceImpl implements IntactServiceIF {

    /**
     * Intact Types.
     */
    private ResourceBundle myIntactTypes;

    /**
     * Intact types reflection map.
     */
    private Map myNameToClassInfo = new HashMap();


    /**
     * Constructs an instance with given resource file.
     *
     * @param types the name of the Intact types resource file.
     *
     * @exception MissingIntactTypesException for errors with IntactTypes
     * resource file. This may be for not able to find the resource file or
     * for an empty resource file.
     * @exception ClassNotFoundException unable to load the class for types
     * in the resource file.
     * @exception IntrospectionException error thrown when getting bean info for
     * types in the resource file.
     */
    public IntactServiceImpl(String types) throws MissingIntactTypesException,
        ClassNotFoundException, IntrospectionException {
        try {
            myIntactTypes = ResourceBundle.getBundle(types);
        }
        catch (MissingResourceException ex) {
            throw new MissingIntactTypesException(
                "Unable to find IntactTypes resource");
        }
        // We must have Intact Types to search for; in other words, resource
        // bundle can't be empty.
        if (!myIntactTypes.getKeys().hasMoreElements()) {
            throw new MissingIntactTypesException(
                "Can't build a map file from an empty IntactTypes resource file");
        }
//        HashMap map = new HashMap();
//
//        //don't want any other threads messing up the map....
//        synchronized(map) {
//            for (Enumeration enum = rb.getKeys(); enum.hasMoreElements();) {
//                String type = (String) enum.nextElement();
//                Key key = new Key(type);
//                map.put(key, rb.getString(type));
//            }
//        }
        // Loop through the resource bundle.
        for (
            Enumeration enum = myIntactTypes.getKeys(); enum.hasMoreElements();) {
            // Get the class type for the current key.
            String type = myIntactTypes.getString((String) enum.nextElement());
            // Create our own key to put into the map.
            Key key = new Key(type);
            BeanInfo info = Introspector.getBeanInfo(Class.forName(type));
            PropertyDescriptor[] fieldDescs = info.getPropertyDescriptors();
            myNameToClassInfo.put(key, fieldDescs);
        }
    }
    // Implements business methods

//    public IntactUser authenticate(String username, String password)
//        throws InvalidLoginException {
//
//        // The intact user to return.
//        IntactUser user = null;
//
//        // Just a dummy validation.
//        if (username.equals("abc") && password.equals("abc")) {
//            //user = new IntactUser(username, password);
//        }
//        else {
//            throw new InvalidLoginException();
//        }
//        return user;
//    }


    public String getClassName(String topic) {
        return myIntactTypes.getString(topic);
    }
}
