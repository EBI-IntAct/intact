/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    Overview of pairwise interactions

  Usage:      sqlplus username/password @progname.sql

  $Date$
  $Author$
  $Locker$

  *************************************************************/

SET DOC OFF

--- Setup layout
SET PAGESIZE 50
SET LINESIZE 2000
SET NEWPAGE 0
SET LINE 0

--- Main query
SELECT e.shortlabel Experiment, i.shortLabel Interaction, 
       m.shortLabel Molecule, r.shortLabel Role
  FROM ia_interactor i, ia_interactor m, ia_component c, ia_ControlledVocab r, 
       ia_experiment e, ia_int2exp i2e 
 WHERE i.ac=c.interaction_ac 
   AND m.ac=interactor_ac 
   AND c.role=r.ac 
   AND e.ac=i2e.experiment_ac 
   AND i.ac=i2e.interaction_ac;

--- Exit
exit;

--- End