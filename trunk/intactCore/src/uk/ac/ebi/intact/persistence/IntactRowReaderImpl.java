package uk.ac.ebi.intact.persistence;

import org.apache.ojb.broker.accesslayer.RowReaderDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.PersistenceBrokerException;

import java.util.Map;
import java.lang.reflect.Constructor;

/**
 * Row Reader class specific to Intact. This Row Reader is used by OJB
 * to materialize Intact objects via private no-arg constructors. This is
 * acheieved by reflection - OJB works best if it uses no-arg constructors
 * (although it supports multi-arg ones, the argumment must be primitive types only).
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class IntactRowReaderImpl extends RowReaderDefaultImpl {


    /**
     * Method used by the main OJB code in <code>RowReaderDefaultImpl</code> to
     * generate an object via reflections. This is the only difference for Intact -
     * we want private no-arg constructors, hence we deal with that here. This method overrides
     * the one provided in the default OJB class.
     * @param cld The ClassDescriptor of the class to build
     * @param row The DB row containing the object data
     * @return Object The object created
     */
    protected Object buildWithReflection(ClassDescriptor cld, Map row)
    {
        if(cld == null) throw new NullPointerException("No class Descriptor - can't build a class!");
        Object result = null;
        FieldDescriptor fmd = null;
        Constructor noArgConstructor = null;
        try
        {
            // 1. create an empty Object
            Class c = cld.getClassOfObject();

            //use this method to get all constructors (not just public ones)
            Constructor[] constructors = c.getDeclaredConstructors();
            for(int i=0; i < constructors.length; i++) {
                if(constructors[i].getParameterTypes().length == 0) {
                    //got the no-arg one - keep it
                    noArgConstructor = constructors[i];
                    break;
                }
            }

            //now override any security so we can run the constructor...
            if(noArgConstructor != null) {
                noArgConstructor.setAccessible(true);
                result = noArgConstructor.newInstance(null);
            }

            // 2. fill all scalar attributes of the new object
            FieldDescriptor[] fields = cld.getFieldDescriptions();
            for (int i = 0; i < fields.length; i++)
            {
                fmd = fields[i];
                fmd.getPersistentField().set(result, row.get(fmd.getColumnName()));
            }
            return result;
        }
        catch(Exception ex)
        {
            System.out.println("failed to create object " + cld.getClassNameOfObject() +
                    "via private constructor call");
            throw new PersistenceBrokerException("Unable to build object instance :"+cld.getClassOfObject(),ex);
        }
    }

}
