/*
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 29-Oct-2002
 * Time: 15:31:16
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.util;

import java.io.*;

/**
 * Simple utility class for checking serialization for objects.
 * Typically used in test cases to check particular objects
 * whose serialization behaviour may be unknown.
 *
 * @author Chris Lewington
 */
public class Serializer {

    private Serializer()
    {
        // no instantiation allowed
    }

    /**
     * performs serialization and then deserialization. If it fails,
     * a runtime exception is thrown - otherwise the original object is returned.
     *
     * @param obj - the object to serialize
     * @return Object the oroginal object, if serialization was successful
     */
    public static Object serializeDeserialize(Serializable obj) {

        try {

            byte[] bytes = Serializer.serialize(obj);
            return Serializer.deserialize(bytes);
        }
        catch(IOException ie) {

            throw new RuntimeException("cannot serialize " + obj.getClass().getName() + ie.getMessage(), ie);
        }
        catch(ClassNotFoundException ce) {

            throw new RuntimeException("cannot serialize " + obj.getClass().getName() + ce.getMessage(), ce);
        }
    }

    /**
     * simple method to serialize an object.
     * @param obj - an object to serialize
     * @return byte[] a byte array of the serialized object
     * @exception IOException thrown if the serialization failed
     */
    protected static byte[] serialize(Serializable obj) throws IOException {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutput serial = new ObjectOutputStream(byteStream);
        serial.writeObject(obj);

        return byteStream.toByteArray();
    }

    /**
     * simple method to deserialize an array of bytes.
     * @param bytes - the array which should hopefully be an object (!)
     * @return Object the deserialized object
     * @exception IOException if there were byte stream problems
     * @exception ClassNotFoundException thrown if the object to construct cannot be found
     */
    protected static Object deserialize(byte[] bytes) throws IOException,
                                                            ClassNotFoundException {
        InputStream in = new ByteArrayInputStream(bytes);
        ObjectInput serial = new ObjectInputStream(in);

        return serial.readObject();
    }
}
