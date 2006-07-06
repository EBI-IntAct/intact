/** 
 */
package uk.ac.ebi.intact.util;

import java.util.*;

/**
 * Represents a set of IntAct objects
 * 
 * @author Henning Hermjakob
 */
public class ObjectSet<T> {

/**
 * 
 */
    public Vector<T> objects = null;

    // * Constructor
    public ObjectSet(){
	this.objects = new Vector<T>();
    }

   ///////////////////////////////////////
   // access methods for associations

    public Vector getObjects() {
        return objects;
    }

    public void setObjects(Vector<T> someObjects) {
        this.objects = someObjects;
    }

    public void addObject(T anObject) {
        if (! this.objects.contains(anObject)) {     
            this.objects.addElement(anObject);  
        }
    }
    public void removeObject(T anObject) {
        boolean removed = this.objects.removeElement(anObject);
    }

} // end ObjectSet




