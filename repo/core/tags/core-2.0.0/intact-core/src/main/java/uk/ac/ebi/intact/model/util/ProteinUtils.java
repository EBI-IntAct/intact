/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.filter.CvObjectFilterGroup;
import uk.ac.ebi.intact.model.util.filter.XrefCvFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility methods for Proteins.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinUtils {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(ProteinUtils.class);

    /**
     * Checks if the protein has been annotated with the no-uniprot-update CvTopic, if so, return false, otherwise true.
     * That flag is added to a protein when created via the editor. As some protein may have a UniProt ID as identity we
     * don't want those to be overwitten.
     *
     * @param protein the protein to check
     *
     * @return true if uniprot
     */
    public static boolean isFromUniprot( Protein protein ) {
        boolean isFromUniprot = true;
        
        for (Annotation annotation : protein.getAnnotations()) {
            String topicLabel = annotation.getCvTopic().getShortLabel();

            if (topicLabel.equals(CvTopic.NON_UNIPROT)) {
                isFromUniprot = false;
            }
        }

        return isFromUniprot;
    }



    public static InteractorXref getUniprotXref(Protein protein){
        return getUniprotXref((Interactor)protein);
    }
    
    /**
     * Return the xref of the protein having as cvQualifier, the CvQualifier with psi-mi equal to
     * CvXrefQualifier.IDENTITY_MI_REF and as cvDatabase, the CvDatabase with psi-mi equal to CvDatabase.UNIPROT_MI_REF
     * and returns it. Return null otherwise.
     * @param protein a non null Protein object.
     * @return the uniprotkb identity xref if the protein has one, null otherwise.
     */
    public static InteractorXref getUniprotXref(Interactor protein){
        if(protein == null){
            throw new NullPointerException("Protein is null, shouldn't be null");
        }
        Collection<InteractorXref> xrefs = protein.getXrefs();
        for(InteractorXref xref : xrefs){
            CvXrefQualifier qualifier = xref.getCvXrefQualifier();
            if(qualifier != null){
                String qualifierIdentity = qualifier.getIdentifier();
                if(qualifierIdentity!= null && CvXrefQualifier.IDENTITY_MI_REF.equals(qualifierIdentity)){
                    CvDatabase database = xref.getCvDatabase();
                    String databaseIdentity = database.getIdentifier();
                    if(databaseIdentity != null && CvDatabase.UNIPROT_MI_REF.equals(databaseIdentity)){
                        return xref;
                    }
                }
            }
        }
        return null;
    }



    /**
     * Get the gene name of a protein
     *
     * @since 1.6
     */
    public static String getGeneName( final Interactor protein ) {

        if ( (protein instanceof Protein) && !(isFromUniprot( (Protein)protein )) ) {
            // if the protein is NOT a UniProt one, then use the shortlabel as we won't get a gene name.
            return protein.getShortLabel();
        }

        // the gene name we want to extract from the protein.
        String geneName = null;

        for ( Alias alias : protein.getAliases()) {
            CvAliasType aliasType = alias.getCvAliasType();
            String aliasTypeIdentity = aliasType.getIdentifier();

            if (aliasTypeIdentity != null && CvAliasType.GENE_NAME_MI_REF.equals(aliasTypeIdentity)) {
                geneName = alias.getName();
                break;
            }
        }

        if ( geneName == null ) {

            geneName = protein.getShortLabel();

            // remove any _organism in case it exists
            int index = geneName.indexOf( '_' );
            if ( index != -1 ) {
                geneName = geneName.substring( 0, index );
            }
        }

        return geneName;
    }

    /**
     * Get the Xref identities for an interactor
     * @param interactor
     * @return
     */
    public static List<InteractorXref> getIdentityXrefs(Interactor interactor) {
        return getIdentityXrefs(interactor, false);
    }

    /**
     * Get the Xref identities for an interactor, allowing the exclude the identities that come from the IMEx partners (intact, mint and dip)
     * @param interactor
     * @param excludeIdentitiesFromImexPartners
     * @return
     */
    public static List<InteractorXref> getIdentityXrefs(Interactor interactor, boolean excludeIdentitiesFromImexPartners) {

        CvObjectFilterGroup qualifierFilterGroup = new CvObjectFilterGroup();
        qualifierFilterGroup.addIncludedIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        CvObjectFilterGroup databaseFilterGroup = new CvObjectFilterGroup();

        if (excludeIdentitiesFromImexPartners) {            
            // TODO this has to be maintained in case we get new IMEx partners
            // TODO a work around could be to load the list of MI identities of all institutions present in the repository
            databaseFilterGroup.addExcludedIdentifier(CvDatabase.INTACT_MI_REF);
            databaseFilterGroup.addExcludedIdentifier(CvDatabase.MINT_MI_REF);
            databaseFilterGroup.addExcludedIdentifier(CvDatabase.DIP_MI_REF);
        }

        return AnnotatedObjectUtils.searchXrefs(interactor, new XrefCvFilter(databaseFilterGroup, qualifierFilterGroup));
    }

    /**
     * Check if two interactors contain the same identity xrefs, excluding the IMEx partner identities
     * @param interactor1
     * @param interactor2
     * @return
     */
    public static boolean containTheSameIdentities(Interactor interactor1, Interactor interactor2) {
        List<InteractorXref> identities1 = getIdentityXrefs(interactor1, true);
        List<InteractorXref> identities2 = getIdentityXrefs(interactor2, true);

        if (identities1.size() != identities2.size()) {
            return false;
        }

        Comparator<InteractorXref> identityXrefComparator = new Comparator<InteractorXref>() {
            public int compare(InteractorXref o1, InteractorXref o2) {
                return o1.getPrimaryId().compareTo(o2.getPrimaryId());
            }
        };

        Collections.sort(identities1, identityXrefComparator);
        Collections.sort(identities2, identityXrefComparator);

        for (int i=0; i<identities1.size(); i++) {
            if (!(identities1.get(i).equals(identities2.get(i)))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the current protein is a splice variant
     * @param protein the protein to check
     * @return true if the protein is a splice variant
     */
    public static boolean isSpliceVariant(Protein protein) {
        Collection<InteractorXref> xrefs = protein.getXrefs();
        for (InteractorXref xref : xrefs) {
            if (xref.getCvXrefQualifier() != null) {
                String qualifierIdentity = xref.getCvXrefQualifier().getIdentifier();
                if (CvXrefQualifier.ISOFORM_PARENT_MI_REF.equals(qualifierIdentity)) {
                    return true;
                }
            }
        }
        return false;
    }
}
