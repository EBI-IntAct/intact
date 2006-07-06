/*SET DOC OFF*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    Overview of pairwise interactions

  Usage:      sqlplus username/password @progname.sql

  $Date$
  $Author$
  $Locker$

  Note: not yet modified
	
  *************************************************************/

--- Setup layout
/*
SET PAGESIZE 50
SET LINESIZE 79
SET NEWPAGE 0
*/
--- Main query
SELECT e.shortlabel Experiment, i.shortLabel Interaction, 
       m.shortLabel Molecule, r.shortLabel Role
  FROM interactor i, interactor m, component c, ControlledVocab r, 
       experiment e, int2exp i2e 
 WHERE i.ac=c.interaction_ac 
   AND m.ac=interactor_ac 
   AND c.role=r.ac 
   AND e.ac=i2e.experiment_ac 
   AND i.ac=i2e.interaction_ac;

--- Exit
/*
exit;
*/
--- End
