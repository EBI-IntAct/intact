/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    Sql Script for generation the detection method statistic

  Usage:      sqlplus username/password @progname.sql

  $Date$
  $Author$
  $Locker$

*************************************************************/

INSERT INTO IA_DetectionMethodsStatistics(ac , fullname, number_interactions)
    SELECT  Intact_statistics_seq.nextval, t.*
    FROM (SELECT ia_controlledvocab.fullname, count(ia_interactor.ac)
          FROM dual,
               ia_interactor,
               ia_int2exp,
               ia_experiment,
               ia_controlledvocab
          WHERE ia_interactor.objclass like '%Interaction%'
                AND ia_interactor.ac = ia_int2exp.interaction_ac
                AND ia_experiment.ac = ia_int2exp.experiment_ac
                AND ia_controlledvocab.ac = ia_experiment.detectmethod_ac
          GROUP BY ia_controlledvocab.fullname ) t;

exit