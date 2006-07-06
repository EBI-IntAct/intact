package uk.ac.ebi.intact.util.fileParsing;

import java.io.*;
import java.util.*;

/**
 * That class .

 * @author Samuel
 * @version $Id$
 */
public class SerializationHelper {

//    Compressing Serialized Data
//    The serialized data can be compressed using the either the GZip or Zip formats by taking advantage of the new package java.util.zip. For example, suppose that you want to use the GZip format, then you would use the following code fragments:
//
//
//       /*
//        *  Open an output file with GZip compression.
//        */
//       try {
//          GZIPOutputStream zip =
//             new GZIPOutputStream(new FileOutputStream(filename));
//          ObjectOutputStream out = new ObjectOutputStream(zip);
//          ...
//       }
//
//
//
//       /*
//        *  Open an input file with GZip compression.
//        */
//       try {
//          GZIPInputStream zip =
//             new GZIPInputStream(new FileInputStream(filename));
//          ObjectInputStream in = new ObjectInputStream(zip);
//          ...
//
//       }
//


    // Returns the contents of the file in a byte array.
    public byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public void serialize( Object object, String filename ) throws IOException {
        // Serialize to a file
        ObjectOutput out = new ObjectOutputStream(new FileOutputStream( filename ));
        out.writeObject( object );
        out.close();
    }

    public Object deserialize( String filename ) throws IOException, ClassNotFoundException {
        Object deserializedObject = null;
        // Deserialize from a file
        File file = new File( filename );
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        // Deserialize the object
        deserializedObject = in.readObject();
        in.close();
        return deserializedObject;
    }


    /**
     * D E M O
     *
     * @param args
     */
    public static void main ( String[] args ) throws ClassNotFoundException, IOException {

        SerializationHelper helper = new SerializationHelper();
        HashMap hm = (HashMap) helper.deserialize("/homes/skerrien/HashMap.ser");
        for ( Iterator iterator = hm.keySet().iterator (); iterator.hasNext (); ) {
            String key = (String) iterator.next ();
            System.out.println ( key + " -> " + hm.get(key) );
        }

        System.out.println ( hm.size() + " associations red." );
    }
}

