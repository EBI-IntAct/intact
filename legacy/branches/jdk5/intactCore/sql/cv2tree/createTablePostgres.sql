/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    table for tree information of CvDagObject

  $Date$
  $Author$
  $Locker$

  *************************************************************/




CREATE TABLE IA_TreeHierarchie
(       cvObjectAc                      VARCHAR (30)    NOT NULL
     ,  type                    VARCHAR (255)
     ,  leftBound               DECIMAL (5)
     ,  rightBound              DECIMAL (5)
     ,  created                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL

)
;
