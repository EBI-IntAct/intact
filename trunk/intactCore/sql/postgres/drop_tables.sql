
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

  drop table annotation_audit;
  drop table biosource_audit;
  drop table biosource2annot_audit;
  drop table component_audit;
  drop table controlledvocab_audit;
  drop table cv2cv_audit;
  drop table cvobject2annot_audit;
  drop table exp2annot_audit;
  drop table experiment_audit;
  drop table institution_audit;
  drop table int2annot_audit;
  drop table int2exp_audit;
  drop table intactnode_audit;
  drop table interactor_audit;
  drop table xref_audit;
  drop table annotation;
  drop table biosource;
  drop table biosource2annot;
  drop table component;
  drop table controlledvocab;
  drop table cv2cv;
  drop table cvobject2annot;
  drop table exp2annot;
  drop table experiment;
  drop table institution;
  drop table int2annot;
  drop table int2exp;
  drop table intactnode;
  drop table interactor;
  drop table xref;

  drop sequence intact_ac;