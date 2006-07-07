DEFINE intactIndexTablespace = INTACT_IDX

CLEAR SCREEN

SET   SERVEROUT ON
SET   FEEDBACK OFF
SET   VERIFY OFF
SET   LINES 150
SET   PAGES 20000
SET   DOC OFF

PROMPT *********************************************************************************
PROMPT
PROMPT   Copyright (c) 2005 The European Bioinformatics Institute, and others.
PROMPT   All rights reserved. Please see the file LICENSE
PROMPT   in the root directory of this distribution.
PROMPT
PROMPT **********************************************************************************
PROMPT   Package:    statistics
PROMPT   Author : Michael Kleen (mkleen@ebi.ac.uk)
PROMPT   Purpose: Create Tables and Datas needed for statisticView used with Oracle
PROMPT   Usage:   sqlplus username/password@INSTANCE @create_all_statistics.sql
PROMPT
PROMPT   $Date$
PROMPT   $Locker$
PROMPT **********************************
PROMPT Starting creation.
PROMPT
PROMPT Drop and recreating statistics tables
PROMPT **********************************
@create_statistics_tables.sql
PROMPT
PROMPT
PROMPT Creating ia_biosourcestatistics

PROMPT **********************************
@insert_ia_biosourcestatistics.sql
PROMPT
PROMPT
PROMPT Creating ia_detectionmethodsstatistics
PROMPT **********************************
@insert_ia_detectionmethodsstatistics.sql
PROMPT
PROMPT
PROMPT Update ia_statistics
PROMPT **********************************
@update_ia_statistics.sql
PROMPT
PROMPT
PROMPT Done !
PROMPT
PROMPT

exit;