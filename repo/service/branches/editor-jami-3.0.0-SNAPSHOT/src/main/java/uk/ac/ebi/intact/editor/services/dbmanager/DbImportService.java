package uk.ac.ebi.intact.editor.services.dbmanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;

/**
 * Db import service
 */
@Service
public class DbImportService extends AbstractEditorService {

    private static final Log log = LogFactory.getLog( DbImportService.class );

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void acceptImport(String importId){
        if (importId != null && importId.length() > 0){
            // first delete features imported
            int updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_annotation where ac in ( " +
                    "select distinct a.ac from ia_annotation a, ia_controlledvocab cv where a.topic_ac = cv.ac and cv.shortlabel = :remark and a.description = :jobId " +
                    ")")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted import annotations "+updated);
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void deleteImport(String importId){
        if (importId != null && importId.length() > 0){
            // first delete features imported
            int updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_feature where ac in ( " +
                    "select distinct f.ac from ia_feature f, ia_feature2annot fa, ia_annotation a, ia_controlledvocab cv " +
                    "where cv.ac = a.topic_ac and a.ac = fa.annotation_ac and f.ac = fa.feature_ac and " +
                    "cv.shortlabel = :remark and a.description = :jobId" +
                    " )")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted features "+updated);
            // then delete participants imported
            updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_component where ac in ( " +
                    "select distinct f.ac from ia_component f, ia_component2annot fa, ia_annotation a, ia_controlledvocab cv " +
                    "where cv.ac = a.topic_ac and a.ac = fa.annotation_ac and f.ac = fa.component_ac and " +
                    "cv.shortlabel = :remark and a.description = :jobId" +
                    " )")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted participants "+updated);

            // then delete interactions imported
            updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_interactor where ac in ( " +
                    "select distinct f.ac from ia_interactor f, ia_int2annot fa, ia_annotation a, ia_controlledvocab cv " +
                    "where cv.ac = a.topic_ac and a.ac = fa.annotation_ac and f.ac = fa.interactor_ac and " +
                    "cv.shortlabel = :remark and a.description = :jobId" +
                    " )")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted interactions/complexes/interactors"+updated);

            // then delete experiments imported
            updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_experiment where ac in ( " +
                    "select distinct f.ac from ia_experiment f, ia_exp2annot fa, ia_annotation a, ia_controlledvocab cv " +
                    "where cv.ac = a.topic_ac and a.ac = fa.annotation_ac and f.ac = fa.experiment_ac and " +
                    "cv.shortlabel = :remark and a.description = :jobId" +
                    " )")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted experiments "+updated);

            // then delete publications imported
            updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_publication where ac in ( " +
                    "select distinct f.ac from ia_publication f, ia_pub2annot fa, ia_annotation a, ia_controlledvocab cv " +
                    "where cv.ac = a.topic_ac and a.ac = fa.annotation_ac and f.ac = fa.publication_ac and " +
                    "cv.shortlabel = :remark and a.description = :jobId" +
                    " )")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted publications "+updated);

            // then delete organisms imported
            updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_biosource where ac in ( " +
                    "select distinct f.ac from ia_biosource f, ia_biosource_alias a, ia_controlledvocab cv " +
                    "where cv.ac = a.aliastype_ac and a.biosource_ac = f.ac and " +
                    "cv.shortlabel = :synonym and a.name = :jobId" +
                    " )")
                    .setParameter("synonym", Alias.SYNONYM)
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted Organisms "+updated);

            // then delete source imported
            updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_institution where ac in ( " +
                    "select distinct f.ac from ia_institution f, ia_institution2annot fa, ia_annotation a, ia_controlledvocab cv " +
                    "where cv.ac = a.topic_ac and a.ac = fa.annotation_ac and f.ac = fa.institution_ac and " +
                    "cv.shortlabel = :remark and a.description = :jobId" +
                    " )")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted sources "+updated);

            // then delete cv imported
            updated = getIntactDao().getEntityManager().createNativeQuery("delete from ia_controlledvocab where ac in ( " +
                    "select distinct f.ac from ia_controlledvocab f, ia_cvobject2annot fa, ia_annotation a, ia_controlledvocab cv " +
                    "where cv.ac = a.topic_ac and a.ac = fa.annotation_ac and f.ac = fa.cvobject_ac and " +
                    "cv.shortlabel = :remark and a.description = :jobId" +
                    " )")
                    .setParameter("remark", "remark-internal")
                    .setParameter("jobId", importId)
                    .executeUpdate();

            log.info("Deleted cvs "+updated);
        }
    }
}
