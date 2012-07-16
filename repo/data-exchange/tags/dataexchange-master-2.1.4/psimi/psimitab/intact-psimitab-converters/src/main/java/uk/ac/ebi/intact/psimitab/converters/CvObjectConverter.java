/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.psimitab.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.tab.model.CrossReferenceImpl;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvXrefQualifier;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;

/**
 * CvObject Converter.
 *
 * @author Nadin Neuhauser
 * @version $Id$
 * @since 2.0.0
 */
public class CvObjectConverter<T extends CrossReferenceImpl, O extends CvObject> {

    public static final Log logger = LogFactory.getLog( CvObjectConverter.class );

    public T toCrossReference( Class<T> clazz, O cvObject )  {
        if ( cvObject == null ) {
            throw new IllegalArgumentException( "CvObject must not be null. " );
        }

        if ( clazz == null ) {
            throw new IllegalArgumentException( "Class must not be null. " );
        }

        String text = cvObject.getShortLabel();
        String identity = cvObject.getIdentifier();

        if(identity == null ) {
            throw new NullPointerException( cvObject.getClass().getSimpleName() + "("+ text +") didn't have an identity" );
        }

        final CvObjectXref idXref = findMatchingIdentityXref(cvObject.getXrefs(), identity); //XrefUtils.getIdentityXref(cvObject, CvDatabase.PSI_MI_MI_REF);
        String db = (idXref != null)? idXref.getCvDatabase().getShortLabel() : "notspecified";

        try {
            Constructor<T> constructor = clazz.getConstructor( String.class, String.class, String.class );
            return constructor.newInstance( db, identity, text );
        } catch ( Exception e ) {
            throw new RuntimeException( "An exception occured while building a " + clazz.getSimpleName() + ": " + text, e );
        }
    } 
    
    private CvObjectXref findMatchingIdentityXref(Collection<CvObjectXref> xrefs, String identity){

        CvObjectXref identityRef = null;
        
        Collection<CvObjectXref> identities = new ArrayList<CvObjectXref>(xrefs.size());
        
        for (CvObjectXref xref : xrefs){
             if (xref.getCvXrefQualifier() != null && CvXrefQualifier.IDENTITY_MI_REF.equalsIgnoreCase(xref.getCvXrefQualifier().getIdentifier())){
                 if (identity.equalsIgnoreCase(xref.getPrimaryId())){
                     identityRef = xref;
                     break;
                 }
             }
            else {
                identities.add(xref);
            }
        }

        // in case the identity ref is null but we have xrefs matching the identifier, we take the first identity xref
        if (identityRef == null && !identities.isEmpty()){
            identityRef = identities.iterator().next();
        }
        
        return identityRef;
    }

    public T toCrossReference( O cvObject ) {
        return toCrossReference( ( Class<T> ) CrossReferenceImpl.class, cvObject );
    }

    public O fromCrossReference( Class<O> clazz, T reference ) {
        throw new UnsupportedOperationException();
    }
}
