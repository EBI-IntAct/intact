/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct core

  Purpose:    Clean test data out following a Unit Test Problem

  Usage:      sqlplus username/password@INSTANCE @cleanup_test_data.sql

  $Date: 2003-04-09 11:40:04 +0100 (dc, 09 abr 2003) $
  $Locker$

  *************************************************************/

-- same SQL, different tables
DELETE FROM  IA_Interactor WHERE ac LIKE 'EBITEST%';

DELETE FROM  IA_Experiment WHERE ac LIKE 'EBITEST%';

DELETE FROM  IA_BioSource WHERE ac LIKE 'EBITEST%';

DELETE FROM  IA_Xref WHERE ac LIKE 'EBITEST%';

DELETE FROM  IA_ControlledVocab WHERE ac LIKE 'EBITEST%';

DELETE FROM  IA_Institution WHERE ac LIKE 'EBITEST%';






