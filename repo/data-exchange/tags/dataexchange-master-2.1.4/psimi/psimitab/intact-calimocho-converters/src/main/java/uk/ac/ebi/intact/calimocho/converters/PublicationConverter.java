package uk.ac.ebi.intact.calimocho.converters;

import org.hupo.psi.calimocho.key.CalimochoKeys;
import org.hupo.psi.calimocho.key.InteractionKeys;
import org.hupo.psi.calimocho.model.DefaultField;
import org.hupo.psi.calimocho.model.Field;
import org.hupo.psi.calimocho.model.Row;
import uk.ac.ebi.intact.model.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PublicationConverter : converts a publication
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/05/12</pre>
 */

public class PublicationConverter {

    private CrossReferenceConverter xrefConverter;
    private AnnotationConverter annotConverter;
    private DateFormat dateFormat;
    private DateFormat yearFormat;
    private DateFormat monthFormat;
    private DateFormat dayFormat;

    private List<String> tagsToExport;
    
    private final static String FULL_COVERAGE_MI = "MI:0957";
    private final static String PARTIAL_COVERAGE_MI = "MI:0958";
    private final static String CURATION_DEPTH_MI = "MI:0955";
    private final static String EXPERIMENTALLY_OBSERVED_MI = "MI:1054";
    private final static String IMPORTED_MI = "MI:1058";
    private final static String INTERNALLY_CURATED_MI = "MI:1055";
    private final static String PREDICTED_MI = "MI:1057";
    private final static String TEXT_MINING_MI = "MI:1056";

    public PublicationConverter(){
        xrefConverter = new CrossReferenceConverter();
        annotConverter = new AnnotationConverter();
        tagsToExport = new ArrayList<String>();
        initializeTagsToExport();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        yearFormat = new SimpleDateFormat("yyyy");
        monthFormat = new SimpleDateFormat("MM");
        dayFormat = new SimpleDateFormat("dd");
    }

    // tags at the publication level that will be exported as interaction annotations
    private void initializeTagsToExport(){
        // full coverage
        tagsToExport.add(FULL_COVERAGE_MI);
        // partial coverage
        tagsToExport.add(PARTIAL_COVERAGE_MI);
        // curation depth
        tagsToExport.add(CURATION_DEPTH_MI);
        // experimentally observed
        tagsToExport.add(EXPERIMENTALLY_OBSERVED_MI);
        // imported
        tagsToExport.add(IMPORTED_MI);
        // internally curated
        tagsToExport.add(INTERNALLY_CURATED_MI);
        // predicted
        tagsToExport.add(PREDICTED_MI);
        // textMining
        tagsToExport.add(TEXT_MINING_MI);
    }

    /**
     *
     * @param pub : intact publication to convert
     * @param row : the row to complete with publication details
     */
    public void intactToCalimocho(Publication pub, Row row){

        Collection<PublicationXref> pubRefs = pub.getXrefs();
        Collection<Annotation> pubAnnotations = pub.getAnnotations();

        Collection<Field> pubIds = new ArrayList<Field>(pubRefs.size());
        for (PublicationXref pubRef : pubRefs){
            if (pubRef.getCvXrefQualifier() != null && pubRef.getCvDatabase().getShortLabel() != null) {
                // publications
                if (CvXrefQualifier.PRIMARY_REFERENCE_MI_REF.equals(pubRef.getCvXrefQualifier().getIdentifier())) {
                    Field primaryRef = xrefConverter.intactToCalimocho(pubRef, false);
                    if (primaryRef != null){
                        pubIds.add(primaryRef);
                    }
                }
                // imexId
                else if (CvXrefQualifier.IMEX_PRIMARY_MI_REF.equals(pubRef.getCvXrefQualifier().getIdentifier())) {
                    Field imexRef = xrefConverter.intactToCalimocho(pubRef, false);
                    if (imexRef != null){
                        pubIds.add(imexRef);
                    }
                }
            }
        }

        if (!pubIds.isEmpty()){
            row.addFields(InteractionKeys.KEY_PUBID, pubIds);
        }
        
        String authorDateValue = null;
        String author = null;
        String date = null;
        
        for (Annotation annot : pubAnnotations){
            if (annot.getCvTopic() != null){
                // tag
                if (tagsToExport.contains(annot.getCvTopic().getIdentifier())){
                    Field tag = annotConverter.intactToCalimocho(annot);
                    if (tag != null){
                        row.addField(InteractionKeys.KEY_ANNOTATIONS_I, tag);
                    }
                }
                // author
                else if ( CvTopic.AUTHOR_LIST_MI_REF.equals(annot.getCvTopic().getIdentifier())){
                    author = annot.getAnnotationText();
                }
                // date
                else if ( CvTopic.PUBLICATION_YEAR_MI_REF.equals(annot.getCvTopic().getIdentifier())){
                    date = annot.getAnnotationText();
                }
            }
        }

        // create author/year value
        if (author != null && date != null){

            if (author.contains(" ")){
                authorDateValue = author.split(" ")[0] + " et al. ("+date+")";
            }
            else {
                authorDateValue = author + " et al. ("+date+")";
            }
        }
        else if (author != null){
            if (author.contains(" ")){
                authorDateValue = author.split(" ")[0] + " et al.";
            }
            else {
                authorDateValue = author + " et al.";
            }
        }
        else if (date != null){
            authorDateValue = "- ("+date+")";
        }
        if (authorDateValue != null){
            Field field = new DefaultField();
            field.set( CalimochoKeys.VALUE, authorDateValue);
            row.addField(InteractionKeys.KEY_PUBAUTH, field);
        }
        
        // create source database
        Institution institution = pub.getOwner();
        
        if (institution != null){
            Collection<InstitutionXref> ownerRefs = institution.getXrefs();

            Field identityRef = null;
            for (InstitutionXref ref : ownerRefs){
                if (CvXrefQualifier.IDENTITY_MI_REF.equals(ref.getCvXrefQualifier().getIdentifier())) {
                    identityRef = xrefConverter.intactToCalimocho(ref, false);
                    break;
                }
            }

            if (identityRef == null && institution.getShortLabel() != null){
                identityRef = new DefaultField();

                String db = CrossReferenceConverter.DATABASE_UNKNOWN;

                identityRef.set( CalimochoKeys.KEY, db);
                identityRef.set( CalimochoKeys.DB, db);
                identityRef.set( CalimochoKeys.VALUE, "-");
                identityRef.set( CalimochoKeys.TEXT, institution.getShortLabel());
            }
            else if (institution.getShortLabel() != null){
                identityRef.set( CalimochoKeys.TEXT, institution.getShortLabel());
            }

            if (identityRef != null){
                row.addField(InteractionKeys.KEY_SOURCE, identityRef);
            }
        }

        // creation date of publication
        if (pub.getCreated() != null){
            Field created = new DefaultField();
            created.set( CalimochoKeys.VALUE, dateFormat.format(pub.getCreated()) );
            created.set( CalimochoKeys.DAY, dayFormat.format(pub.getCreated()) );
            created.set( CalimochoKeys.MONTH, monthFormat.format(pub.getCreated()) );
            created.set( CalimochoKeys.YEAR, yearFormat.format(pub.getCreated()) );

            row.addField(InteractionKeys.KEY_CREATION_DATE, created);
        }
    }
    
    public Publication calimochoToIntact(Row row){

        if (row != null){

            Collection<Field> pubIds = row.getFields(InteractionKeys.KEY_PUBID);
            Collection<Field> authorDate = row.getFields(InteractionKeys.KEY_PUBAUTH);
        }

        return null;
    }
}
