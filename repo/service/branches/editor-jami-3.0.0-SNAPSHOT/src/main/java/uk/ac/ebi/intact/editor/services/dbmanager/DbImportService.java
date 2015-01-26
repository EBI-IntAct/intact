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
    public void acceptImportEvidence(String importId){
        if (importId != null && importId.length() > 0){
            // first delete features imported
            int updated = getIntactDao().getEntityManager().createQuery("delete FeatureEvidenceAnnotation where ac in (select distinct a.ac from FeatureEvidenceAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted feature import annotations "+updated);

            // then delete participants imported
            updated = getIntactDao().getEntityManager().createQuery("delete ParticipantEvidenceAnnotation where ac in (select distinct a.ac from ParticipantEvidenceAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted participant evidences import annotations"+updated);

            // then delete interactions imported
            updated = getIntactDao().getEntityManager().createQuery("delete InteractionAnnotation where ac in (select distinct a.ac from InteractionAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted interaction evidences import annotations"+updated);

            // then delete experiments imported
            updated = getIntactDao().getEntityManager().createQuery("delete ExperimentAnnotation where ac in (select distinct a.ac from ExperimentAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted experiments import annotations"+updated);

            // then delete publications imported
            updated = getIntactDao().getEntityManager().createQuery("delete PublicationAnnotation where ac in (select distinct a.ac from PublicationAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted publications import annotations"+updated);

            // then delete interactors imported
            updated = getIntactDao().getEntityManager().createQuery("delete InteractorAnnotation where ac in (select distinct a.ac from InteractorAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted interactors import annotations"+updated);

            // then delete organisms imported
            updated = getIntactDao().getEntityManager().createQuery("delete OrganismAlias where ac in (select distinct a.ac from OrganismAlias a " +
                    "where a.type.shortName = :synonym and a.name = :jobId) ")
                    .setParameter("synonym", Alias.SYNONYM)
                    .executeUpdate();

            log.info("Deleted Organisms import annotations"+updated);

            // then delete source imported
            updated = getIntactDao().getEntityManager().createQuery("delete SourceAnnotation where ac in (select distinct a.ac from SourceAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted sources import annotations "+updated);

            // then delete cv imported
            updated = getIntactDao().getEntityManager().createQuery("delete CvTermAnnotation where ac in (select distinct a.ac from CvTermAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted cvs import annotations"+updated);
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void deleteImportEvidence(String importId){
        if (importId != null && importId.length() > 0){
            // first delete features imported
            int updated = getIntactDao().getEntityManager().createQuery("delete IntactFeatureEvidence where ac in (select distinct f.ac from IntactFeatureEvidence f " +
                    "join f.annotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted feature evidences "+updated);

            // then delete participants imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactParticipantEvidence where ac in (select distinct f.ac from IntactParticipantEvidence f " +
                    "join f.annotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted participant evidences "+updated);

            // then delete interactions imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactInteractionEvidence where ac in (select distinct f.ac from IntactInteractionEvidence f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted interaction evidences "+updated);

            // then delete experiments imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactExperiment where ac in (select distinct f.ac from IntactExperiment f " +
                    "join f.annotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted experiments "+updated);

            // then delete publications imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactPublication where ac in (select distinct f.ac from IntactPublication f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted publications "+updated);

            // then delete interactors imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactInteractor where ac in (select distinct f.ac from IntactInteractor f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted interactors "+updated);

            // then delete organisms imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactOrganism where ac in (select distinct f.ac from IntactOrganism f " +
                    "join f.aliases as a where a.type.shortName = :synonym and a.name = :jobId) ")
                    .setParameter("synonym", Alias.SYNONYM)
                    .executeUpdate();

            log.info("Deleted Organisms "+updated);

            // then delete source imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactSource where ac in (select distinct f.ac from IntactSource f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted sources "+updated);

            // then delete cv imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactCvTerm where ac in (select distinct f.ac from IntactCvTerm f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted cvs "+updated);
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void acceptImportComplexes(String importId){
        if (importId != null && importId.length() > 0){
            // first delete features imported
            int updated = getIntactDao().getEntityManager().createQuery("delete ModelledFeatureAnnotation where ac in (select distinct a.ac from ModelledFeatureAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted feature evidences import annotations"+updated);

            // then delete participants imported
            updated = getIntactDao().getEntityManager().createQuery("delete ModelledParticipantAnnotation where ac in (select distinct a.ac from ModelledParticipantAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted participant evidences import annotations "+updated);

            // then delete interactors imported
            updated = getIntactDao().getEntityManager().createQuery("delete InteractorAnnotation where ac in (select distinct a.ac from InteractorAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted interactors import annotations"+updated);

            // then delete organisms imported
            updated = getIntactDao().getEntityManager().createQuery("delete OrganismAlias where ac in (select distinct a.ac from OrganismAlias a " +
                    "where a.type.shortName = :synonym and a.name = :jobId) ")
                    .setParameter("synonym", Alias.SYNONYM)
                    .executeUpdate();

            log.info("Deleted Organisms import annotations"+updated);

            // then delete source imported
            updated = getIntactDao().getEntityManager().createQuery("delete SourceAnnotation where ac in (select distinct a.ac from SourceAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted sources import annotations"+updated);

            // then delete cv imported
            updated = getIntactDao().getEntityManager().createQuery("delete CvTermAnnotation where ac in (select distinct a.ac from CvTermAnnotation a " +
                    "where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted cvs import annotations"+updated);
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void deleteImportComplex(String importId){
        if (importId != null && importId.length() > 0){
            // first delete features imported
            int updated = getIntactDao().getEntityManager().createQuery("delete IntactModelledFeature where ac in (select distinct f.ac from IntactModelledFeature f " +
                    "join f.annotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted feature "+updated);

            // then delete participants imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactModelledParticipant where ac in (select distinct f.ac from IntactModelledParticipant f " +
                    "join f.annotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted participant "+updated);

            // then delete complexes imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactComplex where ac in (select distinct f.ac from IntactComplex f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted complexes "+updated);

            // then delete interactors imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactInteractor where ac in (select distinct f.ac from IntactInteractor f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted interactors "+updated);

            // then delete organisms imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactOrganism where ac in (select distinct f.ac from IntactOrganism f " +
                    "join f.aliases as a where a.type.shortName = :synonym and a.name = :jobId) ")
                    .setParameter("synonym", Alias.SYNONYM)
                    .executeUpdate();

            log.info("Deleted Organisms "+updated);

            // then delete source imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactSource where ac in (select distinct f.ac from IntactSource f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted sources "+updated);

            // then delete cv imported
            updated = getIntactDao().getEntityManager().createQuery("delete IntactCvTerm where ac in (select distinct f.ac from IntactCvTerm f " +
                    "join f.dbAnnotations as a where a.topic.shortName = :remark and a.value = :jobId) ")
                    .setParameter("remark", "remark-internal")
                    .executeUpdate();

            log.info("Deleted cvs "+updated);
        }
    }
}
