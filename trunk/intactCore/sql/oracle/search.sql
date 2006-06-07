/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    materialized view for search3 for the initial request
              after creating that view you have to grant right to user
              on it and to create a public view
              Note: having all the 'value' field lowercased allow us to
                    get ealily a non case sensitive search only by doing
                    a lowercase to the search query.

  Usage:      psql -f search.sql

  Update:     EXECUTE DBMS_MVIEW.REFRESH('IA_SEARCH', 'C');

  $Date$
  $Author$
  $Locker$

  *************************************************************/

DROP MATERIALIZED VIEW IA_SEARCH;

CREATE MATERIALIZED VIEW IA_SEARCH AS (
    SELECT ac, LOWER(shortlabel) as value, objclass, 'shortlabel' as type
    FROM ia_interactor
UNION
    SELECT ac, LOWER(fullname) as value, objclass, 'fullname' as type
    FROM ia_interactor
UNION
    SELECT ac, LOWER(ac) as value, objclass, 'ac' as type
    FROM ia_interactor
UNION
    SELECT I.ac, LOWER(X.primaryid) as value, objclass, 'xref' as type
    FROM ia_interactor I, ia_xref X
    WHERE I.ac = X.parent_ac
UNION
    SELECT I.ac, LOWER(A.name) as value, I.objclass, CV.shortlabel as type
    FROM ia_interactor I, ia_alias A, ia_controlledvocab CV
    WHERE I.ac = A.parent_ac and
          A.aliastype_ac = CV.ac
UNION
    SELECT E.ac, LOWER(X.primaryid) as value, 'uk.ac.ebi.intact.model.Experiment', 'xref' as type
    FROM ia_experiment E, ia_xref X
    WHERE E.ac = X.parent_ac
UNION
    SELECT ac, LOWER(shortlabel) as value, 'uk.ac.ebi.intact.model.Experiment', 'shortlabel' as type
    FROM ia_experiment
UNION
    SELECT ac, LOWER(fullname) as value, 'uk.ac.ebi.intact.model.Experiment', 'fullname' as type
    FROM ia_experiment
UNION
    SELECT ac,LOWER(ac) as value, 'uk.ac.ebi.intact.model.Experiment', 'ac' as type
    FROM ia_experiment
UNION
    SELECT ac, LOWER(fullname) as value, objclass, 'fullname' as type
    FROM ia_controlledvocab
UNION
    SELECT ac, LOWER(ac) as value, objclass, 'ac' as type
    FROM ia_controlledvocab
UNION
    SELECT ac, LOWER(shortlabel) as value, objclass, 'shortlabel' as type
    FROM ia_controlledvocab
UNION
    SELECT CV.ac, LOWER(X.primaryid) as value, objclass, 'xref' as type
    FROM ia_controlledvocab CV, ia_xref X
    WHERE CV.ac = X.parent_ac
UNION
    SELECT CV.ac, LOWER(A.name) as value, objclass, 'alias' as type
    FROM ia_controlledvocab CV, ia_alias A
    WHERE CV.ac = A.parent_ac
);

CREATE INDEX i_search_main ON ia_search(value,objclass);

-- Rights management.
GRANT select ON ia_search to INTACT_SELECT ;
GRANT select,insert,update,delete ON ia_search to INTACT_CURATOR ;