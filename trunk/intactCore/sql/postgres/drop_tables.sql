
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

  drop table annotation_audit cascade ;
  drop table biosource_audit cascade ;
  drop table biosource2annot_audit cascade ;
  drop table component_audit cascade ;
  drop table controlledvocab_audit cascade ;
  drop table cv2cv_audit cascade ;
  drop table cvobject2annot_audit cascade ;
  drop table exp2annot_audit cascade ;
  drop table experiment_audit cascade ;
  drop table institution_audit cascade ;
  drop table int2annot_audit cascade ;
  drop table int2exp_audit cascade ;
  drop table intactnode_audit cascade ;
  drop table interactor_audit cascade ;
  drop table xref_audit cascade ;
  drop table annotation cascade ;
  drop table biosource cascade ;
  drop table biosource2annot cascade ;
  drop table component cascade ;
  drop table controlledvocab cascade ;
  drop table cv2cv cascade ;
  drop table cvobject2annot cascade ;
  drop table exp2annot cascade ;
  drop table experiment cascade ;
  drop table institution cascade ;
  drop table int2annot cascade ;
  drop table int2exp cascade ;
  drop table intactnode cascade ;
  drop table interactor cascade ;
  drop table xref cascade ;

  drop sequence intact_ac;