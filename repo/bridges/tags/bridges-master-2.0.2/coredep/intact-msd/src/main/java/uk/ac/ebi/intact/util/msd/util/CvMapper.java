/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.msd.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.util.PropertyLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class is done to load the mapping of msd term to psi-mi vocabulary. This mapping is given in the file intactCore
 * /data/msdMapping.dat.
 * This file should have the following structure :
 * <p/>
 *      mapping.1=electron microscopy,MI:0040
 *      mapping.2=electron tomography,MI:0410
 *      mapping.3=NMR,MI:0077
 *      mapping.4=Single crystal X-ray diffraction,MI:0114
 *      ...etc
 * <p/>
 * This is use to mapp the intact_msd_data.experiment_type field (corresponding to the bean PdbBean) to the correspon-
 * ding CvObject in IntAct. This is done loading a file associated and msd term to it's psi-mi in the /data/msdMapping.dat
 * file. This cvObject will be used as the Experiment detectMethod.
 *
 * We do not need the same kind of mapper for the participant detection as it will always be:
 * predetermined: id: MI:0396 name: predetermined participant
 * As well, just to mention that the Biosource is of an msd experiment will always be :
 * in vitro : full host organisms description is recommended using tax id == -1 as convention to refer to 'in vitro' interaction
 *
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public final class CvMapper {

    /**
     * Map associating a psi-mi Id String to a CvObject.
     */
    private static Map<String,CvObject> msdTerm2cvObject = new HashMap<String,CvObject>();
    /**
     * String beeing the first part of the property names contained in the file MAPPING_FILE.
     */
    private static final String MAPPING = "mapping";
    /**
     * String containing the file name where the mapping msdTerm/psi-mi id is done.
     */
    private static final String MAPPING_FILE = "/data/msdMapping.dat";

    /**
     * boolean telling whether or not the file MAPPING_FILE has already been loaded or not.
     * If it's equal to false, it hasn't otherwise, it has been loaded.
     */
    private static boolean fileLoaded = false;
    /**
     * Number of duplicated lines in the MAPPING_FILE
     */
    private static int duplicatedLines = 0;

    /**
     * The constructor is done private as this class is not done to be instanciated.
     * Only the static method getCvObjectFromMsdTerm(String msdTerm) should be used.
     */
    private CvMapper() {
    }

    /**
     * Load the msdMapping.dat file into the msdTerm2cvObject Map.
     *
     * @throws IntactException
     */
    private static void loadMappingFile() throws IntactException {

            //load the file.
            Properties props = PropertyLoader.load( MAPPING_FILE );
            int i = 1;
            //For each mapping done in the file (mapping.1, mapping.2...etc)
            String propertyValue = props.getProperty( MAPPING + "." + i++ );
            while ( propertyValue != null ) {
                // process the property value. ex : electron microscopy,MI:0040
                processPropertyValue( propertyValue );
                propertyValue = props.getProperty( MAPPING + "." + i++ );
            }
            // If the number of properties contained in the msdMapping.dat is not equal to the number of mapping loaded
            // in the msdTerm2cvobject map we throw an IntactException. If some lines are duplicated :
            //  mapping.1=NMR,MI:0077
            //  mapping.2=NMR,MI:0077
            //  mapping.3=electron microscopy,MI:0040
            // it won't thow an exception.
            if ( msdTerm2cvObject.size() < ( props.size() - duplicatedLines ) ) {
                throw new IntactException( MAPPING_FILE + " contains " + props.size() + " properties but we could map" +
                                           "only " + msdTerm2cvObject.size() + " of them. Check " + MAPPING_FILE + "." );
            }

    }

    /**
     * This method take the value of a property.
     * ex : NMR,MI:0077
     * split it using the "," search the CvObject corresponding to the psi-mi number and put it in the msdTerm2cvObject
     * map as a value associated to the msd term key (NMR in the exemple).
     *
     * @param value  ( a String composed as follow [msdTerm],[psi-mi id])
     *
     * @throws IntactException
     */
    private static void processPropertyValue( String value ) throws IntactException {
        //Split the value of the property. mapping[0] should correspond to the msd term and mapping[1] to the psi-mi id.
        String[] mapping = value.split( "," );
        //If it was more then on "," throw an IntactException.
        if ( mapping.length > 2 ) {
            throw new IntactException( "The format of your data/msdMapping.dat file is incorect, one of the line " +
                                       "contains more then one \",\" Here is the format of each line : \n" +
                                       "mapping.number=msd name,corresponding psi-mi id. " +
                                       "\n Exemple : " +
                                       "\n mapping.1=electron microscopy,MI:0040" );
        } else {
            String msdTerm = mapping[ 0 ].trim();
            String psiMiId = mapping[ 1 ].trim();

            CvObject cvObject = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao().getByXref(psiMiId);
            // If the corresponding IntAct CvObject could not be found, throw an IntactException.
            if ( cvObject == null ) {
                throw new IntactException( "Could not find CvVocabulary for psi-mi : " + psiMiId );
            } else {
                //If this msd term is associated twice to different psi-mi id throw an IntactException.
                if ( msdTerm2cvObject.containsKey( msdTerm ) && !psiMiId.equals( msdTerm2cvObject.get( msdTerm ) ) ) {
                    throw new IntactException( MAPPING_FILE + " contains several times the msd term " + msdTerm +
                                               " associated to at least 2 different psi-mi id (" + msdTerm2cvObject.get( msdTerm ) + " and "
                                               + psiMiId + ")." );
                    //If one of the mapping is duplicated (the same msd term associated twice to the same psi-mi id, we
                    //increment the duplicatedLines int.
                } else
                if ( msdTerm2cvObject.containsKey( msdTerm ) && psiMiId.equals( msdTerm2cvObject.get( msdTerm ) ) ) {
                    duplicatedLines++;
                    //We map the msdTerm to its corresponding cvObject.
                } else {
                    msdTerm2cvObject.put( msdTerm, cvObject );
                }
            }
        }
    }


    /**
     * Given an msd term it try to find the corresponding CvObject, if found it returns it, if not found it returns null.
     *
     * @param msdTerm
     *
     * @return The msd term if found, null otherwise.
     *
     * @throws IntactException
     * @throws IOException
     */
    public static CvObject getCvObjectFromMsdTerm( String msdTerm ) throws IntactException, IOException {
        if ( fileLoaded == false ) {
            loadMappingFile();
            fileLoaded = true;
        }
        if ( msdTerm2cvObject.containsKey( msdTerm ) ) {
            return msdTerm2cvObject.get( msdTerm );
        } else {
            return null;
        }

    }

    public static void main( String[] args ) {

        try {
            CvObject cvObject = CvMapper.getCvObjectFromMsdTerm( "NMR" );
            System.out.println( "cvObject.getShortLabel() = " + cvObject.getShortLabel() );
            cvObject = CvMapper.getCvObjectFromMsdTerm( "Single crystal X-ray diffraction" );
            System.out.println( "cvObject.getShortLabel() = " + cvObject.getShortLabel() );
            cvObject = CvMapper.getCvObjectFromMsdTerm( "electron tomography" );
            System.out.println( "cvObject.getShortLabel() = " + cvObject.getShortLabel() );
            cvObject = CvMapper.getCvObjectFromMsdTerm( "electron microscopy" );
            System.out.println( "cvObject.getShortLabel() = " + cvObject.getShortLabel() );


        } catch ( IntactException e ) {
            System.out.println( "Could not find in Intact any cvObject corresponding to the msdTerm NMR" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }


}

