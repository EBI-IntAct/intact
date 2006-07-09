set doc off

/**
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct reports

  Purpose:    table for tree information of CvDagObject

  $Date: 2005-04-28 19:38:17 +0100 (dj, 28 abr 2005) $
  $Author: afrie $
  $Locker$

  *************************************************************/


CREATE TABLE IA_TreeHierarchie
(       cvObjectAc                      VARCHAR2(30)    NOT NULL
     ,  type                    VARCHAR2(255)
     ,  leftBound               DECIMAL (5)
     ,  rightBound              DECIMAL (5)
     ,  created                 DATE       DEFAULT  SYSDATE   NOT NULL
     ,  updated                 DATE       DEFAULT  SYSDATE   NOT NULL
     ,  timestamp               DATE       DEFAULT  SYSDATE   NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
;
