/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.image;

import org.w3c.dom.Document;


/**
 * Abstract class allowing convert a SVG document to an other format (JPEG, TIFF, PNG)
 *
 * @author Emilie FROT
 */

public abstract class ConvertSVG {


    /**
     * Provides a implementation of ConvertSVG by its name.
     * for example you have an implementation of this abstract class called : <b>JPEGConvertSVG</b>.
     *      so, you could call the following method to get an instance of this class :
     *      <br>
     *      <b>ConvertSVG.getConvertSVG ("mypackage.JPEGConvertSVG");</b>
     *      <br>
     *      then you're able to use methods provided by this abstract class without to know
     *      what implementation you are using.
     *
     * @param aClassName the name of the implementation class you want to get
     * @return an ConvertSVG object, or null if an error occurs.
     */
    public static ConvertSVG getConvertSVG (String aClassName) {

        Object object = null;

        try {
            // create a class by its name
            Class cls = Class.forName(aClassName);

            // Create an instance of the class invoked
            object = cls.newInstance();

        } catch (Exception e) {
            // nothing to do, object is already setted to null
        }

        return (ConvertSVG) object;

    } // getConvertSVG

    /**
     * Convert an object Document to a byte []
     *
     */
    abstract public byte[] convert(Document doc) throws Exception ;

    /**
     * Give the MIME type
     *
     * @return String
     */
    abstract public String getMimeType();

} // ConvertSVG

