
/*
  Copyright (c) 2003 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct core

  Purpose:    Drop all Postgres components for IntAct

  Usage:      psql - f drop_tables.sql


  $Date$
  $Locker$

  *************************************************************/


drop table ia_cvobject2annot cascade ;
--  drop table ia_cvobject2annot_audit ;

drop table ia_int2annot cascade ;
--  drop table ia_int2annot_audit ;

drop table ia_exp2annot cascade ;
--  drop table ia_exp2annot_audit ;

drop table ia_biosource2annot cascade ;
--  drop table ia_biosource2annot_audit  ;

drop table ia_annotation cascade ;
--  drop table ia_annotation_audit ;

drop table ia_component cascade ;
--  drop table ia_component_audit ;

drop table ia_cv2cv cascade ;
--  drop table ia_cv2cv_audit ;

drop table ia_int2exp cascade ;
--  drop table ia_int2exp_audit ;

drop table ia_experiment cascade ;
--  drop table ia_experiment_audit ;

drop table ia_intactnode cascade ;
--  drop table ia_intactnode_audit ;

drop table ia_xref cascade ;
--  drop table ia_xref_audit ;

drop table ia_alias cascade ;
--  drop table ia_alias_audit ;

drop table ia_sequence_chunk cascade ;

drop table ia_interactor cascade ;
--  drop table ia_interactor_audit ;

drop table ia_biosource cascade ;
--  drop table ia_biosource_audit ;

drop table ia_controlledvocab cascade ;
--  drop table ia_controlledvocab_audit ;

drop table ia_institution cascade ;
--  drop table ia_institution_audit ;

drop sequence intact_ac ;



drop table ia_statistics ;
--  drop table ia_statistics_audit ;

drop sequence statistics_seq ;

drop table ia_payg;
drop table current_edge;
drop table temp_node; 

