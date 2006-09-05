package uk.ac.ebi.intact.persistence;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.accesslayer.RowReaderDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Row Reader class specific to Intact. This Row Reader is used by OJB to materialize Intact objects via private no-arg
 * constructors. This is acheieved by reflection - OJB works best if it uses no-arg constructors (although it supports
 * multi-arg ones, the argumment must be primitive types only).
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class IntactRowReaderImpl extends RowReaderDefaultImpl {

    /**
     * Constructor delegates to the parent.
     */
    public IntactRowReaderImpl( ClassDescriptor cld ) {
        super( cld );
    }

    /**
     * Method used by the main OJB code in <code>RowReaderDefaultImpl</code> to generate an object via reflections. This
     * is the only difference for Intact - we want private no-arg constructors, hence we deal with that here. This
     * method overrides the one provided in the default OJB class.
     *
     * @param cld The ClassDescriptor of the class to build
     * @param row The DB row containing the object data
     *
     * @return Object The object created
     */
    protected Object buildWithReflection( ClassDescriptor cld, Map row ) {
        if ( cld == null ) {
            throw new NullPointerException( "No class Descriptor - can't build a class!" );
        }
        Object result = null;
        FieldDescriptor fmd = null;
        Constructor noArgConstructor = null;
        Collection extentDescriptors = new ArrayList();
        ClassDescriptor extentDesc = null;
        try {
            // 1. create an empty Object
            Class c = cld.getClassOfObject();
            //System.out.println("class used in reader: " + c.getName());
            extentDescriptors = getExtentDescriptors( c );
            //NB sometimes the class descriptor is an interface -
            //then we should use the class descriptor of the extent class instead....
            //if(c.isInterface()) {
            //System.out.println("found interface of type " + c.getName());
            //Collection extents = cld.getExtentClasses();
            //need the class descriptors of them....
            //NB with our current descriptor all interfaces only have a single
            //extent (ie the implementation)
//                for(Iterator it = extents.iterator(); it.hasNext();) {
//                    Class extentClass = (Class)it.next();
//                    extentDesc = DescriptorRepository.getDefaultInstance().getDescriptorFor(extentClass);
//                    extentDescriptors.add(extentDesc);
//                }

            // }
            if ( !extentDescriptors.isEmpty() ) {
                //use it instead - BUT....
                //An issue here:
                //1) we only need this now because Experiments have a Collection
                //of interfaces and we need proxies for the contents, so we expect
                //only a single extent descriptor. So far this is the case, since the
                //only interfaces we have in the model are only declared to have a single
                //extent (ie their implementation, which could be a proxy - hence
                //this solution!). If we ever define interfaces with more than one
                //extent class then this will become an issue, since we can only
                //materialize a single object here!!
                //
                //IMPORTANT - this is ONLY an issue when interfaces with multiple
                //extents are accessed OUTSIDE THE SEARCH METHOD - in search it is already
                //handled, but for non-search ones we have no control.
                cld = (ClassDescriptor) extentDescriptors.iterator().next();
                c = cld.getClassOfObject();
                //System.out.println("extent used: " + cld.getClassNameOfObject());

            }

            //use this method to get all constructors (not just public ones)
            Constructor[] constructors = c.getDeclaredConstructors();
            for ( int i = 0; i < constructors.length; i++ ) {
                if ( constructors[ i ].getParameterTypes().length == 0 ) {
                    //got the no-arg one - keep it
                    noArgConstructor = constructors[ i ];
                    break;
                }
            }

            //now override any security so we can run the constructor...
            if ( noArgConstructor != null ) {
                noArgConstructor.setAccessible( true );
                result = noArgConstructor.newInstance();
            }

            // 2. fill all scalar attributes of the new object
            FieldDescriptor[] fields = cld.getFieldDescriptions();
            for ( int i = 0; i < fields.length; i++ ) {
                fmd = fields[ i ];
                fmd.getPersistentField().set( result, row.get( fmd.getColumnName() ) );
            }
            return result;
        }
        catch ( Exception ex ) {
            System.out.println( "failed to create object " + cld.getClassNameOfObject() +
                                " via private constructor call" );
            throw new PersistenceBrokerException( "Unable to build object instance :" + cld.getClassOfObject(), ex );
        }
    }

    /**
     * Retrieves the concrete class descriptors for classes which are originally interfaces (or abstract). This makes
     * sure that only cocnrete classes are searched for.
     *
     * @param clazz The class to drill down
     *
     * @return Collection the collection of concrete class descriptors - empty if none found.
     */
    private Collection getExtentDescriptors( Class clazz ) {

        Collection result = new ArrayList();
        ClassDescriptor extentDesc = null;
        if ( !clazz.isInterface() ) {
            extentDesc = MetadataManager.getInstance().getRepository().getDescriptorFor( clazz );
            result.add( extentDesc );
            //System.out.println("Adding concrete descriptor for " + clazz.getName());
            return result;
        }
        //System.out.println("found interface of type " + clazz.getName());
        extentDesc = MetadataManager.getInstance().getRepository().getDescriptorFor( clazz );
        Collection extents = extentDesc.getExtentClasses();

        //need the class descriptors of them....
        //NB with our current descriptor all interfaces only have a single
        //extent (ie the implementation)
        for ( Iterator it = extents.iterator(); it.hasNext(); ) {
            Class extentClass = (Class) it.next();
            result.addAll( getExtentDescriptors( extentClass ) );
        }
        return result;

    }

}
