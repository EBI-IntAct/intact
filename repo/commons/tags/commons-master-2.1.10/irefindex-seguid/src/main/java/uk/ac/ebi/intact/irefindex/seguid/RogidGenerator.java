/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.irefindex.seguid;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.irefindex.seguid.base64.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * A class that generates the RogIds for an interaction with a set of interactors
 * Reference for the methodology
 * iRefIndex: A consolidated protein interaction database with provenance
 * PMID: 18823568
 * http://www.biomedcentral.com/1471-2105/9/405
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class RogidGenerator {

    private static final Log log = LogFactory.getLog( RogidGenerator.class );

    public RogidGenerator() {
    }

    /**
     * calculates the Seguid for the given protein sequence
     *
     * @param sequence rogid sequence
     * @return Seguid
     * @throws SeguidException handled by  SeguidException class
     */
    public String calculateSeguid( String sequence ) throws SeguidException {

        if ( sequence == null ) {
            throw new NullPointerException( "You must give a non null sequence" );
        }

        return doMessageDigestAndBase64Encoding( sequence );
    }


    /**
     * @param sequence protein sequence
     * @return the MessageDigest based on SHA algorithm with Base64 encoding
     * @throws SeguidException handled by  SeguidException class
     */
    private String doMessageDigestAndBase64Encoding( String sequence ) throws SeguidException {
        if ( sequence == null ) {
            throw new NullPointerException( "You must give a non null sequence" );
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance( "SHA" );
            messageDigest.update( sequence.getBytes() );
            byte[] digest = messageDigest.digest();
            String seguid = Base64.encodeBytes( digest );
            seguid = seguid.replace( "=", "" );
            if ( log.isTraceEnabled() ) {
                log.trace("SEGUID " + seguid );
            }

            return seguid;
        } catch ( NoSuchAlgorithmException e ) {
            throw new SeguidException( "Exception thrown when calculating Seguid for " + sequence,e.getCause() );
        }

    }

    /**
     * calculate Rogid for the give protein sequence+taxid
     * Rogid = Sequid + taxid
     *
     * @param sequence protein sequence
     * @param taxid    taxonomy id
     * @return Rogid
     * @throws SeguidException handled by  SeguidException class
     */
    public String calculateRogid( String sequence, String taxid ) throws SeguidException {
        if ( sequence == null ) {
            throw new NullPointerException( "You must give a non null sequence" );
        }

        if ( taxid == null ) {
            throw new NullPointerException( "You must give a non null taxid" );
        }
        return doMessageDigestAndBase64Encoding( sequence.trim().toUpperCase()) + taxid;
    }
}
