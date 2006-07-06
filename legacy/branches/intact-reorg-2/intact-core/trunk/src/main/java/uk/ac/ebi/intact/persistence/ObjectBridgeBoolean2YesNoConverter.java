/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;

/**
 * Converter to allow the OBJ converstion from boolean to char in the database
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>06-Jun-2006</pre>
 */
public class ObjectBridgeBoolean2YesNoConverter implements FieldConversion
{

    /**
     * Log for this class
     */
    public static final Log log = LogFactory.getLog(ObjectBridgeBoolean2YesNoConverter.class);

    private static String C_TRUE = "Y";
    private static String C_FALSE = "N";

    public Object javaToSql(Object source) throws ConversionException
    {

        if (source.equals(Boolean.TRUE))
        {
            return C_TRUE;
        }
        else
        {
            return C_FALSE;
        }
    }

    public Object sqlToJava(Object source) throws ConversionException
    {

        if (source.equals(C_TRUE))
        {
            return Boolean.TRUE;
        }
        else
        {
            return Boolean.FALSE;
        }
    }


    public static void main(String[] args)
    {
           ObjectBridgeBoolean2YesNoConverter c = new ObjectBridgeBoolean2YesNoConverter();


    }
}
