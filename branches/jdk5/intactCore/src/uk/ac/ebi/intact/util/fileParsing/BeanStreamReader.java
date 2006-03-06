/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.fileParsing;

import uk.ac.ebi.intact.util.fileParsing.beans.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Allow to read beans from a input
 *
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
/**
 * Allow to iterate through the line or the resource describe by a given URL.
 */
public class BeanStreamReader {

    //////////////////////////
    // Static variables
    //////////////////////////

    /**
     * The supported bean -> parameter to give them in constructor
     */
    private static final HashMap allowedBean = new HashMap( );
    static {
        allowedBean.put( GiotLineBean.class,       String.class );
//        allowedBean.put( MappingLineBean.class,    String.class );
//        allowedBean.put( DecypherOutputBean.class, String.class );
    }


    ///////////////////////////
    // Instance variables
    ///////////////////////////

    private URL mySourceURL;

    private BufferedReader bufferedReader;

    /**
     * Constructor of the bean type we want to generate
     * out of the StreamReader.
     */
    private Constructor constructor;
    private Class       constructorParam;

    private int itemCount = 0;


    /////////////////////////////
    // Constructors
    /////////////////////////////

    public BeanStreamReader( String sourceUrl, Class clazz, int lineToSkip )
            throws MalformedURLException,
            IOException,
            NoSuchMethodException {

        if ( ! allowedBean.containsKey( clazz ) )
            throw new IllegalArgumentException( "Can not handle bean of type: " + clazz.getName() );

        constructorParam = (Class) allowedBean.get( clazz );

        try {
            constructor = clazz.getConstructor( new Class[] { constructorParam } );
        } catch ( NoSuchMethodException e ) {
            throw e;
        } catch ( SecurityException e ) {
            throw e;
        }

        try {
            mySourceURL = new URL( sourceUrl );

            InputStream in = mySourceURL.openStream();
            InputStreamReader isr = new InputStreamReader( in );
            bufferedReader = new BufferedReader( isr );

            // Pass the n first lines
            for (int i = 0; i < lineToSkip; i++)
                bufferedReader.readLine();

        } catch ( MalformedURLException e ) {
            throw e;
        } catch ( IOException e ) {
            throw e;
        }
    }

    public BeanStreamReader( String sourceUrl, Class clazz )
            throws MalformedURLException,
            IOException,
            NoSuchMethodException {

        this( sourceUrl, clazz, 0 );
    }


    //////////////////////////////
    // Instance methods
    //////////////////////////////

    public Object readBean()
            throws IOException, IllegalAccessException,
                   InvocationTargetException, InstantiationException {

        Object item = null;

        if ( constructorParam == String.class ) {
            String line = bufferedReader.readLine(); // read the first line.
            if ( line == null ) return null; // end of the stream;

            /**
             * Create an instance of the bean and give in parameter the line to parse to populate.
             */
            item = constructor.newInstance( new Object[]{ line } );
            itemCount++;
        } else {
            item = constructor.newInstance( new Object[]{ bufferedReader } );
            itemCount++;
        }

        return item;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }

    public int getItemCount () {
        return itemCount;
    }
} // BeanStreamReader
