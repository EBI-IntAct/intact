/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    Overview of items present in the IntAct Database

  Usage:      sqlplus username/password @progname.sql

  $Date$
  $Author$
  $Locker$

  Note:

*************************************************************/

/* first, create a sequence that allows to increment the ac column, the primary key */
CREATE SEQUENCE statistics_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE
;


CREATE OR REPLACE TRIGGER TRG_STATISTICS
BEFORE INSERT
ON IA_Statistics
FOR EACH ROW
BEGIN
  select statistics_seq.nextval
  into : new.ac
  from dual
  ;
END;

/* fill the IA_Statistics table, counting each item */
LOCK TABLE IA_Statistics IN EXCLUSIVE MODE;

INSERT INTO IA_Statistics (protein_number)
    SELECT count(*)
    FROM IA_Interactor
    WHERE objclass='uk.ac.ebi.intact.model.Protein'
UPDATE IA_Statistics

SET interaction_number = (SELECT count(*)
                          FROM IA_Interactor
                          WHERE objclass='uk.ac.ebi.intact.model.Interaction'
WHERE timestamp=(SELECT max(timestamp)
                 FROM IA_Statistics);
UPDATE IA_Statistics

SET binary_interactions = (SELECT count(distinct interaction_ac)
                           FROM IA_Component c1
                           WHERE (SELECT count(*)
                                  FROM IA_Component c2
                                  WHERE c1.interaction_ac = c2.interaction_ac) = 2)
WHERE timestamp=(SELECT max(timestamp)
                 FROM IA_Statistics);
UPDATE IA_Statistics

SET complex_interactions = (SELECT count(distinct interaction_ac)
                           FROM IA_Component c1
                           WHERE (SELECT count(*)
                                  FROM IA_Component c2
                                  WHERE c1.interaction_ac = c2.interaction_ac) > 2)
WHERE timestamp=(SELECT max(timestamp)
                 FROM IA_Statistics);
UPDATE IA_Statistics

SET experiment_number = (SELECT count(*)
                         FROM IA_Experiment)
WHERE timestamp=(SELECT max(timestamp)
                 FROM IA_Statistics);
UPDATE IA_Statistics

SET term_number = (SELECT count(*)
                          FROM IA_ControlledVocab)
WHERE timestamp=(SELECT max(timestamp)
                 FROM IA_Statistics);
COMMIT;






/***********************************************************************
--- count the number of interactions with 2 interactors
    SELECT sum (count (distinct I.ac))
    FROM IA_COMPONENT C, IA_INTERACTOR I
    WHERE I.objclass = 'uk.ac.ebi.intact.model.Interaction' AND
        C.interaction_ac = I.ac
    GROUP BY I.ac
    HAVING count(C.interactor_ac) = 2;

--- count the number of interactions with more than 2 interactors
    SELECT sum (count (distinct I.ac))
    FROM IA_COMPONENT C, IA_INTERACTOR I
    WHERE I.objclass = 'uk.ac.ebi.intact.model.Interaction' AND
        C.interaction_ac = I.ac
    GROUP BY I.ac
    HAVING count(C.interactor_ac) > 2;
*************************************************************************/