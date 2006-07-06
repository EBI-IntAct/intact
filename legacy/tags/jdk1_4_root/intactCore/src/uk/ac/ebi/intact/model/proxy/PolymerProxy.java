/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Polymer;

import java.lang.reflect.InvocationHandler;
import java.util.List;

/**
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class PolymerProxy extends InteractorProxy implements Polymer {

    public PolymerProxy() {
    }

    /**
     * @param uniqueId org.apache.ojb.broker.Identity
     */
    public PolymerProxy( PBKey key, Identity uniqueId ) {
        super( key, uniqueId );
    }

    public PolymerProxy( InvocationHandler handler ) {
        super( handler );
    }

    private Polymer realSubject() {
        try {
            return (Polymer) getRealSubject();
        } catch ( Exception e ) {
            return null;
        }
    }

    public String getSequence() {
        return realSubject().getSequence();
    }

    public List setSequence( String aSequence ) {
        return realSubject().setSequence( aSequence );
    }

    public void setSequence( IntactHelper helper, String aSequence ) throws IntactException {
        realSubject().setSequence( helper, aSequence );
    }

    public String getCrc64() {
        return realSubject().getCrc64();
    }

    public void setCrc64( String crc64 ) {
        realSubject().setCrc64( crc64 );
    }

    public List getSequenceChunks() {
       return realSubject().getSequenceChunks();
    }

    public boolean equals( Object o ) {
        return realSubject().equals( o );
    }

    public int hashCode() {
        return realSubject().hashCode();
    }
}