SET DOC OFF
/*************************************************************

  Package:    IntAct core

  Purpose:    Drop all Oracle components for IntAct

  Usage:      sqlplus username/password@INSTANCE @drop_tables.sql

  $Date$
  $Locker$

  *************************************************************/

-- Tables
PROMPT Dropping tables ...

PROMPT ... Component
DROP TABLE Component;

PROMPT ... Int2Exp
DROP TABLE Int2Exp;

PROMPT ... Xref
DROP TABLE Xref;

PROMPT ... Obj2Annot
DROP TABLE Obj2Annot;

PROMPT ... Interactor
DROP TABLE Interactor;

PROMPT ... PolymerSeq
DROP TABLE PolymerSeq;

PROMPT ... Experiment
DROP TABLE Experiment;

PROMPT ... Annotation
DROP TABLE Annotation;

PROMPT ... BioSource
DROP TABLE BioSource;

PROMPT ... ControlledVocab
DROP TABLE ControlledVocab;

PROMPT ... IntactNode
DROP TABLE IntactNode;

PROMPT ... Institution
DROP TABLE Institution;



-- Sequences
PROMPT Dropping sequences ...
PROMPT ... Intact_ac
DROP SEQUENCE Intact_ac;


exit;


-- Grants

exit;






