/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:   Create all tables which are needed for the statisticsView


  $Date$
  $Author$
  $Locker$

*************************************************************/


CREATE TABLE IA_BioSourceStatistics
(     ac                      NUMBER(9) NOT NULL
                              CONSTRAINT pk_BioSourceStatistics
                              PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     , timestamp              DATE            DEFAULT  SYSDATE NOT NULL
     , taxid                  VARCHAR2(30)
     , shortLabel             VARCHAR2(20)
     , protein_number         NUMBER(9)       DEFAULT  0       NOT NULL
     , binary_interactions    NUMBER(9)       DEFAULT  0       NOT NULL
);

CREATE TABLE IA_DetectionMethodsStatistics
(     ac                      NUMBER(9) NOT NULL
                              CONSTRAINT pk_DetectionMethodsStatistics
                              PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     , timestamp              DATE            DEFAULT  SYSDATE NOT NULL
     , fullName               VARCHAR(250)
     , number_interactions    NUMBER(9)       DEFAULT  0       NOT NULL
);
