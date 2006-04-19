/*
  Copyright (c) 2003-2004 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/

/*************************************************************

  Package:    IntAct core

  Purpose:    Resets all Postgres tables

  Usage:      psql -f reset_tables.sql


  $Author: Sugath Mudali
  $Date$
  $Locker$

  *************************************************************/

delete from ia_range ;
delete from ia_feature2annot ;
delete from ia_feature ;
delete from ia_cvobject2annot ;
delete from ia_int2annot ;
delete from ia_exp2annot ;
delete from ia_biosource2annot ;
delete from ia_annotation ;
delete from ia_component ;
delete from ia_cv2cv ;
delete from ia_int2exp ;
delete from ia_experiment ;
delete from ia_intactnode ;
delete from ia_xref ;
delete from ia_alias ;
delete from ia_sequence_chunk ;
delete from ia_interactor ;
delete from ia_biosource ;
delete from ia_controlledvocab ;
delete from ia_institution ;
delete from ia_interactions ;

drop sequence intact_ac ;

-- Sequences

CREATE SEQUENCE Intact_ac start 10;

-- delete from table ia_statistics ;
-- delete from sequence statistics_seq ;

-- delete from table ia_payg;
-- delete from table current_edge;
-- delete from table temp_node;

