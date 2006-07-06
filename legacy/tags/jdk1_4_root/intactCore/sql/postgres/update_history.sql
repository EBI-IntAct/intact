/***********************************************************************************************************************

  Package:    IntAct

  Purpose:    Update scripts to go from database version to database version.

  Usage:      psql -f update_history.sql


  $Date$

  $Locker$


 **********************************************************************************************************************/


 CREATE  TABLE IA_SEARCH AS (
     SELECT ac,
            shortlabel as value,
            objclass,
            'shortlabel' as type
        FROM ia_interactor
 UNION
     SELECT ac,
        LOWER(fullname) as value,
        objclass,
        'fullname' as type
        FROM ia_interactor
 UNION
     SELECT I.ac,
        X.primaryid as value,
        objclass,
        'xref' as type
        FROM ia_interactor I, ia_xref X
        WHERE I.ac = X.parent_ac
 UNION
     SELECT I.ac,
        A.name as value,
        I.objclass,
        CV.shortlabel as type
        FROM ia_interactor I, ia_alias A, ia_controlledvocab CV
        WHERE I.ac = A.parent_ac and
              A.aliastype_ac = CV.ac
 UNION
     SELECT E.ac,
        X.primaryid as value,
        'uk.ac.ebi.intact.model.Experiment',
        'xref' as type
        FROM ia_experiment E, ia_xref X
        WHERE E.ac = X.parent_ac
 UNION
     SELECT ac,
        shortlabel as value,
        'uk.ac.ebi.intact.model.Experiment',
        'shortlabel' as type
        FROM ia_experiment
 UNION
     SELECT ac,
        LOWER(fullname) as value,
        'uk.ac.ebi.intact.model.Experiment',
        'fullname' as type
        FROM ia_experiment
 UNION
     SELECT ac,
        LOWER(fullname) as value,
        objclass,
        'fullname' as type
        FROM ia_controlledvocab
 UNION
     SELECT ac,
        shortlabel as value,
        objclass,
        'shortlabel' as type
        FROM ia_controlledvocab
 UNION
     SELECT CV.ac,
        X.primaryid as value,
        objclass,
        'xref' as type
        FROM ia_controlledvocab CV, ia_xref X
        WHERE CV.ac = X.parent_ac
 UNION
     SELECT CV.ac,
        A.name as value,
        objclass,
        'alias' as type
        FROM ia_controlledvocab CV, ia_alias A
        WHERE CV.ac = A.parent_ac);

 CREATE INDEX i_search_value on ia_search(value);


