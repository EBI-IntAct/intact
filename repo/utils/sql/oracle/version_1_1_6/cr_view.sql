CREATE OR REPLACE VIEW IA_DELETED_ACS AS
select "SHORTLABEL","AC","TYPE","DELETED_DATE","DELETED_BY"
from
  (
    (SELECT shortlabel,ac, 'experiment' type , updated deleted_date, userstamp deleted_by
     FROM   ia_experiment_audit 
     WHERE  event = 'D'
     AND    ac in 
        (   SELECT ac 
            FROM   ia_experiment_audit 
            WHERE  event = 'D'
        MINUS
            SELECT primaryid ac
            FROM   ia_xref x
                   ,ia_controlledvocab c
                   ,ia_experiment e
            WHERE  x.qualifier_ac = c.ac 
            AND    objclass = 'uk.ac.ebi.intact.model.CvXrefQualifier'
            AND    c.shortlabel = 'intact-secondary'
            AND    x.parent_ac    = e.ac 
        )   
    ) 
  UNION
    (SELECT shortlabel,ac, 'interaction' type , updated deleted_date, userstamp deleted_by
     FROM   ia_interactor_audit 
     WHERE  objclass = 'uk.ac.ebi.intact.model.InteractionImpl'
     AND    event = 'D'
     AND    ac in 
        (   SELECT i.ac 
            FROM   ia_interactor_audit i
         /*         ,ia_int2exp_audit i2e   Can (in future) also be used as interactor, than there is no direct relationship
            WHERE  i.ac = i2e.interaction_ac */
            WHERE   i.event = 'D'
        MINUS
            SELECT primaryid ac
            FROM   ia_xref x                 
                   ,ia_controlledvocab c
                  ,ia_interactor i
            WHERE  x.qualifier_ac = c.ac 
            AND    c.objclass     = 'uk.ac.ebi.intact.model.CvXrefQualifier'
            AND    c.shortlabel   = 'intact-secondary'
            AND    x.parent_ac    = i.ac 
        )   
    )     
  UNION
    (SELECT shortlabel,ac, 'interactor' type , updated deleted_date, userstamp deleted_by
     FROM   ia_interactor_audit 
     WHERE  /* an interaction can also be used as interactor objclass <> 'uk.ac.ebi.intact.model.InteractionImpl' */
             event = 'D'
     AND    ac in 
        (   SELECT i.ac 
            FROM   ia_interactor_audit i
                   ,ia_component_audit c /*only in the list if ever used*/
            WHERE  i.ac = c.interactor_ac 
            AND    i.event = 'D'
        MINUS
            SELECT primaryid ac
            FROM   ia_xref x                 
                   ,ia_controlledvocab c
                  ,ia_interactor i
            WHERE  x.qualifier_ac = c.ac 
            AND    c.objclass     = 'uk.ac.ebi.intact.model.CvXrefQualifier'
            AND    c.shortlabel   = 'intact-secondary'
            AND    x.parent_ac    = i.ac 
        )   
    )  
  );

