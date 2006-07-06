/** 
 */
package uk.ac.ebi.intact.util;

import java.util.*;

/**
 * Represents a set of IntAct objects
 * 
 * @author Henning Hermjakob
 */
public class ObjectSet {

/**
 * 
 */
    public Vector objects = null;

    // * Constructor
    public ObjectSet(){
	this.objects = new Vector();
    }

   ///////////////////////////////////////
   // access methods for associations

    public Vector getObjects() {
        return objects;
    }

    public void setObjects(Vector someObjects) {
        this.objects = someObjects;
    }

    public void addObject(Object anObject) {
        if (! this.objects.contains(anObject)) {     
            this.objects.addElement(anObject);  
        }
    }
    public void removeObject(Object anObject) {
        boolean removed = this.objects.removeElement(anObject);
    }

} // end ObjectSet




