/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/

/*************************************************************

  Package:    IntAct reports

  Purpose:    Overview of items present in the IntAct Database

  Usage:      psql username/password @progname.sql

  $Date$
  $Author$
  $Locker$

  Note:

*************************************************************/

BEGIN WORK;
LOCK TABLE IA_Statistics IN EXCLUSIVE MODE;

INSERT INTO IA_Statistics (protein_number)
    SELECT count(*)
    FROM IA_Interactor
    WHERE objclass='uk.ac.ebi.intact.model.Protein';


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



-- END WORK;
COMMIT;
