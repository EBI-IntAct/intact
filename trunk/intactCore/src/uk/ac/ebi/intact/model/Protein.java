/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.*;

/**
 * Represents a protein or peptide. The object should only contain
 * the minimum information relevant for IntAct, most information
 * should only be retrieved by the xref.
 *
 * @author hhe
 */
public class Protein extends Interactor implements Editable {

    ///////////////////////////////////////
    //Constants

    /**
     * As the maximum size of database objects is limited, the sequence is represented as
     * an array of strings of maximum length MAX_SEQUENCE_LENGTH.
     */
    private static final int MAX_SEQUENCE_LENGTH = 1000;



    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    public String  cvProteinFormAc;

    protected String formOfAc;


    /**
     * Represents the CRC64 checksum. This checksum is used to
     * detect potential inconsistencies between the sequence the object
     * refers to and the external sequence object, for example when the external
     * object has been updated.
     */
    protected String crc64;

    ///////////////////////////////////////
    // associations

    /**

     */
    public Protein formOf;
    /**
     *
     */
    public CvProteinForm cvProteinForm;
    /**
     *
     */
    public Collection feature = new Vector();
    /**
     *
     */
    public Collection modification = new Vector();

    /**
     * The protein sequence. If the protein is present in a public database,
     * the sequence should not be repeated.
     */
    private Collection sequenceChunks = new Vector();




    ///////////////////////////////////////
    //access methods for attributes

    public String getSequence() {

        if ((null == sequenceChunks) || 0 == (sequenceChunks.size() )) {
            return null;
        }

        // Re-join the sequence chunks.
        // The correct ordering is done during retrieval from the database.
        // It relies on the OJB setting (mapping)
        StringBuffer sequence = new StringBuffer();
        for (Iterator iterator = sequenceChunks.iterator(); iterator.hasNext();) {
            SequenceChunk sequenceChunk = (SequenceChunk) iterator.next();
            sequence.append(sequenceChunk.getSequenceChunk());
        }

        return sequence.toString();
    }


    /**
     * If there is existing sequence (and chunks), reuse existing chunk
     * in order to save AC.<br>
     * The update is canceled if the sequence is null or the same.
     *
     * @param helper
     * @param aSequence the sequence to update in the protein
     * @throws uk.ac.ebi.intact.business.IntactException
     */
    public void setSequence(IntactHelper helper, String aSequence) throws IntactException {

        if (null == aSequence) {
            return;
        }

        if (null == this.getAc()) {
            throw new IntactException ("The object AC must be set before setting the sequence.");
        }

        // Save work if the new sequence is identical to the old one.
        if (aSequence.equals(this.getSequence())) {
            return;
        }

        ArrayList chunkPool = null;
        SequenceChunk s = null;
        String chunk = null;

        // All old data are kept, we try to recycle as much chunk as possible
        if (sequenceChunks == null) {
            sequenceChunks = new Vector();
        } else if (false == sequenceChunks.isEmpty()) {
            // There is existing chunk ... prepare them for recycling.
            chunkPool = new ArrayList (sequenceChunks.size());
            chunkPool.addAll (sequenceChunks);
            int count = chunkPool.size();

            // clean chunk to recycle
            for (int i = 0; i<count; i++) {
                s = (SequenceChunk) chunkPool.get(i);
                removeSequenceChunk(s);
            }
        }

        // Note the use of integer operations
        int chunkCount = aSequence.length() / MAX_SEQUENCE_LENGTH;
        if (aSequence.length() % MAX_SEQUENCE_LENGTH > 0) {
            chunkCount++;
        }

        for (int i = 0; i < chunkCount; i++) {
            chunk = aSequence.substring( i * MAX_SEQUENCE_LENGTH,
                                         Math.min( (i+1) * MAX_SEQUENCE_LENGTH,
                                                    aSequence.length()
                                                 )
                                       );

            if (chunkPool != null && chunkPool.size() > 0) {
                // recycle chunk
                s = (SequenceChunk) chunkPool.remove (0);
                s.setSequenceChunk(chunk);
                s.setSequenceIndex(i);
                addSequenceChunk(s);

                helper.update(s);
            } else {
                // create new chunk
                s = new SequenceChunk(i, chunk);
                addSequenceChunk(s);

                helper.create(s);
            }
        }

        // Delete non recyclable chunk
        while (chunkPool != null && chunkPool.size() > 0) {
            s = (SequenceChunk) chunkPool.remove (0);
            helper.delete(s);
        }
    }


    public String getCrc64() {
        return crc64;
    }
    public void setCrc64(String crc64) {
        this.crc64 = crc64;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Protein getFormOf() {
        return formOf;
    }

    public void setFormOf(Protein protein) {
        this.formOf = protein;
    }
    public CvProteinForm getCvProteinForm() {
        return cvProteinForm;
    }

    public void setCvProteinForm(CvProteinForm cvProteinForm) {
        this.cvProteinForm = cvProteinForm;
    }
    public void setFeature(Collection someFeature) {
        this.feature = someFeature;
    }
    public Collection getFeature() {
        return feature;
    }
    public void addFeature(Feature feature) {
        if (! this.feature.contains(feature)) {
            this.feature.add(feature);
            feature.setProtein(this);
        }
    }
    public void removeFeature(Feature feature) {
        boolean removed = this.feature.remove(feature);
        if (removed) feature.setProtein(null);
    }
    public void setModification(Collection someModification) {
        this.modification = someModification;
    }
    public Collection getModification() {
        return modification;
    }
    public void addModification(Modification modification) {
        if (! this.modification.contains(modification)) this.modification.add(modification);
    }
    public void removeModification(Modification modification) {
        this.modification.remove(modification);
    }

    private Collection getSequenceChunks() {
        return sequenceChunks;
    }

    private void addSequenceChunk(SequenceChunk sequenceChunk) {
        if (! this.sequenceChunks.contains(sequenceChunk)) {
            this.sequenceChunks.add(sequenceChunk);
            sequenceChunk.setParentAc(this.getAc());
        }
    }
    private void removeSequenceChunk (SequenceChunk sequenceChunk) {
        boolean removed = this.sequenceChunks.remove(sequenceChunk);
        if (removed) sequenceChunk.setParentAc(null);
    }


    //attributes used for mapping BasicObjects - project synchron
    public String  getCvProteinFormAc() {
        return cvProteinFormAc;
    }
    public void setCvProteinFormAc(String cvProteinFormAc) {
        this.cvProteinFormAc = cvProteinFormAc;
    }
    public String getFormOfAc() {
        return this.formOfAc;
    }
    public void setFormOfAc(String ac) {
        this.formOfAc = ac;
    }


    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Protein)) return false;
        if (!super.equals(o)) return false;

        Protein protein = (Protein) o;

        if (xref.equals(protein.getXref())) return false;

        return protein.getBioSource().equals (bioSource);
    }

    /**
     * Remember that hashCode and equals methods has to be develop in parallel
     * since : if a.equals(b) then a.hoshCode() == b.hashCode()
     * The other way round is NOT true.
     * Unless it could break consistancy when storing object in a hash-based
     * collection such as HashMap...
     *
     */
    public int hashCode () {
        int result = super.hashCode();
        final int prime = 29;

        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref xref = (Xref) iterator.next();
            result = prime * result + xref.hashCode();
        }

        BioSource bioSource = getBioSource();
        if (bioSource != null) {
            String taxid = bioSource.getTaxId();
            if (taxid != null) result = prime * result + taxid.hashCode();
        }

        return result;
    }

} // end Protein




