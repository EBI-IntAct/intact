
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


drop table ia_cvobject2annot ;
--  drop table ia_cvobject2annot_audit ;

drop table ia_int2annot ;
--  drop table ia_int2annot_audit ;

drop table ia_exp2annot ;
--  drop table ia_exp2annot_audit ;

drop table ia_biosource2annot ;
--  drop table ia_biosource2annot_audit  ;

drop table ia_annotation ;
--  drop table ia_annotation_audit ;

drop table ia_component ;
--  drop table ia_component_audit ;

drop table ia_cv2cv ;
--  drop table ia_cv2cv_audit ;

drop table ia_int2exp ;
--  drop table ia_int2exp_audit ;

drop table ia_experiment ;
--  drop table ia_experiment_audit ;

drop table ia_intactnode ;
--  drop table ia_intactnode_audit ;

drop table ia_xref ;
--  drop table ia_xref_audit ;

drop table ia_alias;
--  drop table ia_alias_audit ;

drop table ia_sequence_chunk ;

drop table ia_interactor ;
--  drop table ia_interactor_audit ;

drop table ia_biosource ;
--  drop table ia_biosource_audit ;

drop table ia_controlledvocab ;
--  drop table ia_controlledvocab_audit ;

drop table ia_institution ;
--  drop table ia_institution_audit ;

drop sequence intact_ac ;



drop table ia_statistics ;
--  drop table ia_statistics_audit ;

drop sequence statistics_seq ;

