/*************************************************************

  Package:    IntAct core

  Purpose:    Clean test data out following a Unit Test Problem

  Usage:      sqlplus username/password@INSTANCE @cleanup_test_data.sql

  $Date$
  $Locker$

  *************************************************************/

-- same SQL, different tables
DELETE FROM Interactor WHERE ac LIKE 'EBITEST%';

DELETE FROM Experiment WHERE ac LIKE 'EBITEST%';

DELETE FROM BioSource WHERE ac LIKE 'EBITEST%';

DELETE FROM Xref WHERE ac LIKE 'EBITEST%';

DELETE FROM ControlledVocab WHERE ac LIKE 'EBITEST%';

DELETE FROM Institution WHERE ac LIKE 'EBITEST%';






