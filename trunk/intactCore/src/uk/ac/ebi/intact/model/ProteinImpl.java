/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a protein or peptide. The object should only contain
 * the minimum information relevant for IntAct, most information
 * should only be retrieved by the xref.
 *
 * @author hhe
 * @version $Id$
 */
public class ProteinImpl extends InteractorImpl implements Protein {

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
    // TODO: should be move out of the model.
    private String  cvProteinFormAc;

    /**
     * TODO comments
     */
    private String formOfAc;


    /**
     * Represents the CRC64 checksum. This checksum is used to
     * detect potential inconsistencies between the sequence the object
     * refers to and the external sequence object, for example when the external
     * object has been updated.
     */
    private String crc64;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Protein formOf;

    /**
     * TODO comments
     */
    private CvProteinForm cvProteinForm;

    /**
     * TODO comments
     */
    private Collection features = new ArrayList();

    /**
     * TODO comments
     */
    private Collection modifications = new ArrayList();

    /**
     * The protein sequence. If the protein is present in a public database,
     * the sequence should not be repeated.
     */
    private Collection sequenceChunks = new ArrayList();

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private ProteinImpl() {

        //super call sets creation time data
        super();
    }
    /**
     * Creates a valid Protein instance. A valid Protein must have at least an onwer, a
     * short label to refer to it and a biological source specified. A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     * @param owner The 'owner' of the Protein (what does this mean in real terms??)
     * @param source The biological source of the Protein observation
     * @param shortLabel A memorable label used to refer to the Protein instance
     * @exception NullPointerException thrown if any parameters are null.
     */
    public ProteinImpl(Institution owner, BioSource source, String shortLabel) {

        //TODO Q: what about crc64, fullName, formOf - they are all indexed...
        //ALSO..A Protein can have an interaction type IF it is an Interactor,
        //but if it isn't then it doesn't need an interaction type. This does not
        //match with the classes - Interaction has a type, not Interactor...

        //super call sets up a valid AnnotatedObject (should an Interactor be better defined?)
        super(shortLabel, owner);
        if(source == null) throw new NullPointerException("Can't create a valid Protein without a BioSource");
        setBioSource(source);
    }


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


    // TODO finish it
    /**
     *
     *
     * @param helper
     * @param aSequence
     * @param crc64
     * @throws IntactException
     */
    public void setSequence(IntactHelper helper, String aSequence, String crc64) throws IntactException {
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

        if (null == getAc()) {
            throw new IntactException ("The object AC must be set before setting the sequence.");
        }

        // Save work if the new sequence is identical to the old one.
        if (aSequence.equals(getSequence())) {
            return;
        }

        ArrayList chunkPool = null;
        SequenceChunk s = null;
        String chunk = null;

        // All old data are kept, we try to recycle as much chunk as possible
        if (sequenceChunks == null) {

            sequenceChunks = new ArrayList();
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
    public void setFeatures(Collection someFeature) {
        this.features = someFeature;
    }
    public Collection getFeatures() {
        return features;
    }
    public void addFeature(Feature feature) {
        if (! this.features.contains(feature)) {
            this.features.add(feature);
            feature.setProtein(this);
        }
    }
    public void removeFeature(Feature feature) {
        boolean removed = this.features.remove(feature);
        if (removed) feature.setProtein(null);
    }
    public void setModifications(Collection someModification) {
        this.modifications = someModification;
    }
    public Collection getModifications() {
        return modifications;
    }
    public void addModification(Modification modification) {
        if (! this.modifications.contains(modification)) this.modifications.add(modification);
    }
    public void removeModification(Modification modification) {
        this.modifications.remove(modification);
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
    // TODO: should be move out of the model.
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

    /**
     * Equality for Proteins is currently based on equality for
     * <code>Interactors</code>, the sequence and the crc64 checksum.
     * @see uk.ac.ebi.intact.model.InteractorImpl
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     */
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Protein)) return false;
        if (!super.equals(o)) return false;

        final Protein protein = (Protein) o;

        // TODO do we need to check sequence and CRC64
        if(getSequence() != null) {
            if (!getSequence().equals(protein.getSequence())) return false;
        }
        else {
            if (protein.getSequence() != null) return false;
        }

        if(crc64 != null) {
            if (!crc64.equals(protein.getCrc64())) return false;
        }
        else return protein.getCrc64() == null;

        return true;
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

        int code = super.hashCode();
        if (getSequence() != null) code = code * 29 + getSequence().hashCode();
        if (crc64 != null) code = code * 29 + crc64.hashCode();

        return code;
    }

} // end Protein




