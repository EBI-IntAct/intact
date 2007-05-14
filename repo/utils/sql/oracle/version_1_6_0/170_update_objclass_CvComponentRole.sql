update ia_controlledvocab
set objclass='uk.ac.ebi.intact.model.CvExperimentalRole'
where ac in (
select cv.ac
from ia_controlledvocab cv, ia_controlledvocab_xref x
where cv.ac = x.parent_ac
and x.database_ac = 'EBI-77142'
and x.qualifier_ac = 'EBI-28'
and x.primaryId in ('MI:0496', 'MI:0497', 'MI:0498', 'MI:0499', 'MI:0503', 'MI:0581', 'MI:0582', 'MI:0583', 'MI:0584', 'MI:0684','MI:0495' ));

update ia_controlledvocab
set objclass='uk.ac.ebi.intact.model.CvBiologicalRole'
where ac in (
select cv.ac
from ia_controlledvocab cv, ia_controlledvocab_xref x
where cv.ac = x.parent_ac
and x.database_ac = 'EBI-77142'
and x.qualifier_ac = 'EBI-28'
and x.primaryId in ('MI:0499', 'MI:0501', 'MI:0502', 'MI:0503', 'MI:0579', 'MI:0580', 'MI:0586', 'MI:0682', '
MI:0840', 'MI:0842', 'MI:0843','MI:0840','MI:0500' ));

commit;


