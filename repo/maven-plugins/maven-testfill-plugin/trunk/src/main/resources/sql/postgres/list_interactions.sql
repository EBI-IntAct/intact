/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    Overview of pairwise interactions

  Usage:      psql -f progname.sql

  $Date: 2004-11-04 11:54:38 +0000 (dj, 04 nov 2004) $
  $Author: smudali $
  $Locker$

  *************************************************************/


--- Main query
SELECT e.shortlabel, i.shortLabel, 
       m.shortLabel, r.shortLabel
  FROM ia_interactor i, ia_interactor m, ia_component c, ia_ControlledVocab r, 
       ia_experiment e, ia_int2exp i2e 
 WHERE i.ac=c.interaction_ac 
   AND m.ac=interactor_ac 
   AND c.role=r.ac 
   AND e.ac=i2e.experiment_ac 
   AND i.ac=i2e.interaction_ac;

--- End
